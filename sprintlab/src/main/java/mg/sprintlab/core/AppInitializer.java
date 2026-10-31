package mg.sprintlab.core;

import mg.sprintlab.annotation.Controller;
import mg.sprintlab.core.mapping.FileMapping;
import mg.sprintlab.core.mapping.MethodMapping;
import mg.sprintlab.core.mapping.RouteMapping;
import mg.sprintlab.core.mapping.UrlInfo;
import mg.sprintlab.utils.ClassPathScanner;
import mg.sprintlab.utils.JsonConvertor;
import mg.sprintlab.web.ViewDispach;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class AppInitializer implements ServletContextListener {
    public static final String PACKAGE_LIST_PARAM = "package_list";
    public static final String LIST_SEPARATOR_PARAM = "list_separator";
    public static final String LIST_CLASS = "list_class";
    public static final String DEBUG_MODE_PARAM = "debug_mode";
    public static final String ROUTE_MAPPER = "route_mapper";
    public static final String VIEW_DISPATCHER = "view_dispatcher";
    public static final String JSON_CONVERTOR = "json_convertor";
    public static final String UPLOAD_DIRECTORY_PARAM = "upload_directory";
    public static final String SESSION_ATTRIBUTE = "attribute.session.name";
    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        FileMapping.configureUploadDirectory(context.getInitParameter(UPLOAD_DIRECTORY_PARAM));
        context.setAttribute(LIST_CLASS, getControllerClassList(context));
        String configuredDebugMode = context.getInitParameter(DEBUG_MODE_PARAM);
        context.setAttribute(
                DEBUG_MODE_PARAM,
                configuredDebugMode != null && Boolean.parseBoolean(configuredDebugMode.trim()));

        Map<UrlInfo, MethodMapping> routesMapping = new HashMap<>();

        List<Class<?>> uniqueClassList = getControllerClassList(context);
        RouteMapping.scanAndBuildMapping(routesMapping, uniqueClassList);
        context.setAttribute(ROUTE_MAPPER, routesMapping);

        context.setAttribute(JSON_CONVERTOR, new JsonConvertor());

        // Initialize the view dispatcher
        String suffix = context.getInitParameter("view_suffix");
        String prefix = context.getInitParameter("view_prefix");
        ViewDispach viewDispatcher = new ViewDispach();
        viewDispatcher.setSuffix(suffix == null ? ".jsp" : suffix);
        viewDispatcher.setPrefix(prefix == null ? "WEB-INF/pages/" : prefix);
        context.setAttribute(VIEW_DISPATCHER, viewDispatcher);
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
