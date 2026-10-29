package mg.sprintlab.core;

import mg.sprintlab.annotation.Controller;
import mg.sprintlab.utils.ClassPathScanner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppInitializer implements ServletContextListener {
    public static final String PACKAGE_LIST_PARAM = "package_list";
    public static final String LIST_SEPARATOR_PARAM = "list_separator";
    public static final String LIST_CLASS = "list_class";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        context.setAttribute(LIST_CLASS, getControllerClassList(context));
    }

    public List<Class<?>> getControllerClassList() {
        return getControllerClassList(null);
    }

    public List<Class<?>> getControllerClassList(ServletContext context) {
        String configuredPackages = context != null
                ? context.getInitParameter(PACKAGE_LIST_PARAM)
                : System.getProperty(PACKAGE_LIST_PARAM);
        String configuredSeparator = context != null
                ? context.getInitParameter(LIST_SEPARATOR_PARAM)
                : System.getProperty(LIST_SEPARATOR_PARAM, ",");

        if (configuredPackages == null || configuredPackages.isBlank()) {
            return new ArrayList<>();
        }

        String separator = configuredSeparator == null || configuredSeparator.isBlank() ? "," : configuredSeparator;
        return findClassAnnoted(separator, configuredPackages);
    }

    private List<Class<?>> findClassAnnoted(String separator, String configuredPackages) {
        String[] packageList = configuredPackages.split(separator);
        Set<Class<?>> withAnnotation = new HashSet<>();
        ClassPathScanner clp = new ClassPathScanner();

        for (String pkg : packageList) {
            String packageName = pkg == null ? "" : pkg.trim();
            if (packageName.isEmpty()) {
                continue;
            }
            for (Class<?> cls : clp.getClasses(packageName)) {
                if (cls != null && cls.isAnnotationPresent(Controller.class)) {
                    withAnnotation.add(cls);
                }
            }
        }
        return new ArrayList<>(withAnnotation);

    }
}
