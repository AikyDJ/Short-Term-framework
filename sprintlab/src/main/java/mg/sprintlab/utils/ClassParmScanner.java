package mg.sprintlab.utils;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import mg.sprintlab.annotation.RequestParam;
import mg.sprintlab.core.mapping.FileMapping;
import mg.sprintlab.web.Session;

public class ClassParmScanner {

    private static final Set<Class<?>> WRAPPERS = Set.of(
            Integer.class, Double.class, Float.class, Long.class,
            Short.class, Byte.class, Boolean.class, Character.class);

    private ClassParmScanner() {
        // private constructor to prevent instantiation
    }

    public static Object[] getMethodArgs(Method method, HttpServletRequest request, HttpServletResponse response) {
        Parameter[] parameters = method.getParameters();
        Object[] arguments = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            arguments[i] = resolveParameter(parameters[i], request, response);
        }

        return arguments;
    }

    private static Object resolveParameter(Parameter parameter, HttpServletRequest request,
            HttpServletResponse response) {
        // verificaion si le parameter est de type HttpServletRequest,
        // HttpServletResponse ou HttpSession
        if (HttpServletRequest.class.isAssignableFrom(parameter.getType())) {
            return request;
        } else if (HttpServletResponse.class.isAssignableFrom(parameter.getType())) {
            return response;
        } else if (HttpSession.class.isAssignableFrom(parameter.getType())) {
            return new Session(request);
        } else if (Session.class.isAssignableFrom(parameter.getType())) {
            return new Session(request);
        }

        RequestParam definedName = parameter.getAnnotation(RequestParam.class);
        String name = (definedName != null && !definedName.value().trim().isEmpty()) ? definedName.value()
                : parameter.getName();

        if (parameter.getType() == FileMapping.class) {
            try {
                return manageFile(request.getPart(name));
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        // not a simple type -> an object (or a List/Set) built by ObjectHandler
        // from the parameters "name.field"
        if (!isSimpleType(parameter.getType())) {
            Object value = ObjectHandler.read(name, parameter.getType(), parameter.getParameterizedType(), request);
            return value == ObjectHandler.NOT_SET ? null : value;
        }

        String[] values = request.getParameterValues(name);

        if (values == null || values.length == 0
                || (values.length == 1 && values[0].trim().isEmpty() && parameter.getType() != String.class)) {
            if (parameter.getType().isArray()) {
                return Array.newInstance(parameter.getType().componentType(), 0);
            }
            if (parameter.getType().isPrimitive()) {
                return getDefaultValue(parameter.getType());
            }
            return null;
        }
        return parseParameter(values, parameter.getType());
    }

    private static FileMapping manageFile(Part filepart) {
        FileMapping fileMapping = new FileMapping();

        fileMapping.setFilename(filepart.getSubmittedFileName());
        fileMapping.setMimeType(filepart.getContentType());

        try (InputStream data = filepart.getInputStream()) {
            fileMapping.setData(data.readAllBytes());
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return fileMapping;
    }

    /**
     * true for the types that can be built from one text: String, primitives and
     * their wrappers,
     * BigDecimal, LocalDate, LocalDateTime, Date, enums, and arrays of those.
     * (package visible: ObjectHandler uses it too)
     */
    static boolean isSimpleType(Class<?> type) {
        if (type.isArray()) {
            return isSimpleType(type.componentType());
        }
        return type.isPrimitive()
                || type == String.class
                || type == BigDecimal.class
                || type == LocalDate.class
                || type == LocalDateTime.class
                || type == java.util.Date.class
                || type.isEnum()
                || WRAPPERS.contains(type);
    }

    static Object parseParameter(String[] values, Class<?> goal) {
        if (goal.isArray()) {
            Class<?> componentType = goal.componentType();

            String[] effectiveValues = (values.length == 1 && values[0].contains(","))
                    ? values[0].split("\\s*,\\s*")
                    : values;

            Object toReturn = Array.newInstance(componentType, effectiveValues.length);
            for (int i = 0; i < effectiveValues.length; i++) {
                Array.set(toReturn, i, parseParameter(effectiveValues[i], componentType));
            }
            return toReturn;
        }
        return parseParameter(values[0], goal);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    static Object parseParameter(String value, Class<?> goal) {
        String trimmed = value.trim();

        if (goal == String.class) {
            return value;
        } else if (goal == LocalDate.class) {
            return LocalDate.parse(trimmed);
        } else if (goal == LocalDateTime.class) {
            return LocalDateTime.parse(trimmed);
        } else if (goal == java.util.Date.class) {
            try {
                return new java.text.SimpleDateFormat("yyyy-MM-dd").parse(trimmed);
            } catch (java.text.ParseException e) {
                throw new IllegalArgumentException("Date format invalid (attendu yyyy-MM-dd) : " + trimmed, e);
            }
        } else if (goal == Integer.class || int.class == goal) {
            return Integer.valueOf(trimmed);
        } else if (goal == Double.class || double.class == goal) {
            return Double.valueOf(trimmed);
        } else if (goal == Float.class || float.class == goal) {
            return Float.valueOf(trimmed);
        } else if (goal == Long.class || long.class == goal) {
            return Long.valueOf(trimmed);
        } else if (goal == Short.class || short.class == goal) {
            return Short.valueOf(trimmed);
        } else if (goal == Byte.class || byte.class == goal) {
            return Byte.valueOf(trimmed);
        } else if (goal == BigDecimal.class) {
            return new BigDecimal(trimmed);
        } else if (goal == Boolean.class || boolean.class == goal) {
            return "true".equalsIgnoreCase(trimmed) || "on".equalsIgnoreCase(trimmed) || "1".equals(trimmed);
        } else if (goal == char.class || goal == Character.class) {
            if (trimmed.length() != 1) {
                throw new IllegalArgumentException("Impossible to convert : '" + value + "' into a char");
            }
            return trimmed.charAt(0);
        } else if (goal.isEnum()) {
            return Enum.valueOf((Class) goal, trimmed);
        } else {
            throw new IllegalArgumentException("Unsupported type : " + goal.getName());
        }
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type == boolean.class)
            return false;
        if (type == int.class)
            return 0;
        if (type == long.class)
            return 0L;
        if (type == double.class)
            return 0.0d;
        if (type == float.class)
            return 0.0f;
        if (type == char.class)
            return '\0';
        if (type == short.class)
            return (short) 0;
        if (type == byte.class)
            return (byte) 0;
        return null;
    }
}
