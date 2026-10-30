package mg.sprintlab.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import mg.sprintlab.annotation.RequestParam;

public class ClassParmScanner {

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
            return request.getSession();
        }

        RequestParam definedName = parameter.getAnnotation(RequestParam.class);
        String name = (definedName != null && !definedName.value().trim().isEmpty()) ? definedName.value()
                : parameter.getName();

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
        return parseParameter(name, parameter.getType());
    }

    private static Object parseParameter(String value, Class<?> goal) {
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
