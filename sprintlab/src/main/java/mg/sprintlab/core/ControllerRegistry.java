package mg.sprintlab.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds ONE instance of each controller class (singleton per class).
 *
 * The instances are created once, at startup, by AppInitializer. After that the map is
 * read-only, so any number of request threads can call get() without locking.
 *
 * Because the instance is shared by all the users, a controller must be STATELESS:
 * no per-request or per-user data in fields (use method parameters and local variables;
 * the user's data goes in the Session parameter).
 *
 * It is a normal object kept in the ServletContext (not a static field), so a redeploy of the
 * webapp creates a fresh registry and the old controllers can be garbage collected.
 */
public final class ControllerRegistry {

    private final Map<Class<?>, Object> instances;

    /** Creates every controller now. A controller that cannot be built stops the startup (fail fast). */
    public ControllerRegistry(Collection<Class<?>> controllerClasses) {
        Map<Class<?>, Object> created = new HashMap<>();
        for (Class<?> controller : controllerClasses) {
            created.put(controller, instantiate(controller));
        }
        this.instances = Map.copyOf(created);          // read-only from now on
    }

    /** The one instance of this controller class. */
    public Object get(Class<?> controllerClass) {
        Object instance = instances.get(controllerClass);
        if (instance == null) {
            throw new IllegalStateException(controllerClass.getName() + " is not a registered controller");
        }
        return instance;
    }

    public int size() {
        return instances.size();
    }

    private static Object instantiate(Class<?> controller) {
        try {
            Constructor<?> constructor = controller.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Controller " + controller.getName()
                    + " needs a no-arg constructor", e);
        } catch (InvocationTargetException e) {
            throw new IllegalStateException("The constructor of " + controller.getName()
                    + " threw " + e.getCause(), e.getCause());
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Cannot create controller " + controller.getName()
                    + " : " + e, e);
        }
    }
}