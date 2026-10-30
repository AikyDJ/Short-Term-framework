package mg.sprintlab.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Builds a Java object from the request parameters (query string or form body).
 *
 * The parameter "product" of a controller method is filled from the request parameters
 * named "product.<field>" :
 *
 *   product.name=Phone  product.price=99.5  product.supplier.city=Paris
 *
 *   -> new Product(), then for each field of the class :
 *        simple type  (String, int, LocalDate, enum...) -> one value
 *        array / List / Set of simple types             -> several values with the same name
 *        any other class                                -> built recursively (prefix "product.supplier.")
 *
 * ClassParmScanner decides WHICH parameter needs an object; ObjectHandler knows HOW to build it.
 */
public final class ObjectHandler {

    /** "the request sent nothing for this" (not the same thing as null). */
    static final Object NOT_SET = new Object();

    private ObjectHandler() {
        // utility class
    }

    /**
     * Returns the value for this request-parameter name and type, or NOT_SET if the request
     * has nothing for it. Used for method arguments AND for the fields of an object.
     */
    static Object read(String key, Class<?> type, Type genericType, HttpServletRequest request) {
        // 1) simple type, or array of simple type
        if (ClassParmScanner.isSimpleType(type)) {
            String[] values = request.getParameterValues(key);
            if (values == null || values.length == 0
                    || (values.length == 1 && values[0].trim().isEmpty() && type != String.class)) {
                return NOT_SET;                         // an empty number/date field = not sent
            }
            try {
                return ClassParmScanner.parseParameter(values, type);
            } catch (RuntimeException e) {
                throw new IllegalArgumentException("Invalid value for '" + key + "' (expected "
                        + type.getSimpleName() + ")", e);
            }
        }

        // 2) List / Set / Collection of simple types
        if (type == List.class || type == Set.class || type == Collection.class) {
            String[] values = request.getParameterValues(key);
            if (values == null || values.length == 0) {
                return NOT_SET;
            }
            Class<?> element = elementType(genericType);
            if (!ClassParmScanner.isSimpleType(element)) {
                throw new IllegalArgumentException("Unsupported element type " + element.getName()
                        + " for '" + key + "'");
            }
            List<Object> list = new ArrayList<>();
            for (String value : values) {
                try {
                    list.add(ClassParmScanner.parseParameter(value, element));
                } catch (RuntimeException e) {
                    throw new IllegalArgumentException("Invalid value '" + value + "' for '" + key
                            + "' (expected " + element.getSimpleName() + ")", e);
                }
            }
            return type == Set.class ? new LinkedHashSet<>(list) : list;
        }

        // 3) any other class: build an object
        Object nested = bind(key, type, request);
        return nested == null ? NOT_SET : nested;
    }

    /**
     * Creates an instance of 'type' and fills its fields from the parameters "name.field".
     * Returns null if the request has no parameter starting with "name.".
     */
    public static Object bind(String name, Class<?> type, HttpServletRequest request) {
        if (type.isInterface() || type.isArray() || Modifier.isAbstract(type.getModifiers())) {
            throw new IllegalArgumentException("Cannot build an object of type " + type.getName());
        }

        String prefix = name + ".";
        boolean present = request.getParameterMap().keySet().stream().anyMatch(k -> k.startsWith(prefix));
        if (!present) {
            return null;                                // nothing sent: do not create an empty object
        }

        try {
            Constructor<?> constructor = type.getDeclaredConstructor();   // needs a no-arg constructor
            constructor.setAccessible(true);
            Object instance = constructor.newInstance();

            // the class and its parents
            for (Class<?> c = type; c != null && c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    int modifiers = field.getModifiers();
                    if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers) || field.isSynthetic()) {
                        continue;
                    }
                    // same rules as for a method argument -> nested objects work by recursion
                    Object value = read(prefix + field.getName(), field.getType(), field.getGenericType(), request);
                    if (value != NOT_SET) {
                        setValue(instance, field, value);
                    }
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot build " + type.getName()
                    + " (does it have a no-arg constructor?) : " + e.getMessage(), e);
        }
    }

    /** Uses the setter (setName) if there is one, else writes the field directly. */
    private static void setValue(Object instance, Field field, Object value) throws ReflectiveOperationException {
        String name = field.getName();
        String setter = "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
        try {
            Method method = instance.getClass().getMethod(setter, field.getType());
            method.invoke(instance, value);
        } catch (NoSuchMethodException e) {
            field.setAccessible(true);
            field.set(instance, value);
        }
    }

    /** List<Integer> -> Integer ; a raw List -> String */
    private static Class<?> elementType(Type genericType) {
        if (genericType instanceof ParameterizedType parameterized
                && parameterized.getActualTypeArguments()[0] instanceof Class<?> element) {
            return element;
        }
        return String.class;
    }
}