package mg.sprintlab.core.mapping;

import mg.sprintlab.annotation.Auth;
import mg.sprintlab.annotation.MapUrl;
import mg.sprintlab.annotation.RequestBody;
import mg.sprintlab.annotation.enums.HTTPMethode;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class RouteMapping {
    private RouteMapping() {
    }

    public static void scanAndBuildMapping(Map<UrlInfo, MethodMapping> routeMapper, List<Class<?>> controllerList) {
        for (Class<?> controller : controllerList) {
            Method[] methods = controller.getDeclaredMethods();
            for (Method toAnalyze : methods) {
                if (toAnalyze.isAnnotationPresent(MapUrl.class)) {
                    MapUrl urlAn = toAnalyze.getAnnotation(MapUrl.class);
                    String path = urlAn.path();
                    HTTPMethode urlMethod = urlAn.methode();

                    boolean isJson = toAnalyze.isAnnotationPresent(RequestBody.class);
                    boolean isAuthRequired = toAnalyze.isAnnotationPresent(Auth.class);
                    String[] authorizeRoles = new String[0];
                    
                    if (isAuthRequired) {
                        Auth authAn = toAnalyze.getAnnotation(Auth.class);
                        authorizeRoles = authAn.roles();
                    }
                    routeMapper.put(new UrlInfo(path, urlMethod), buildMapping(toAnalyze, controller, path, urlMethod
                        ,isJson
                        ,isAuthRequired
                        ,authorizeRoles
                    ));

                }
            }
        }
    }

    public static MethodMapping buildMapping(Method method, Class<?> controller, String path, HTTPMethode urlMethod,boolean isJson, boolean isAuthRequired, String[] authorizeRoles) {
        MethodMapping methodMap = new MethodMapping();
        methodMap.setControllerClass(controller);
        methodMap.setPathToFile(path);
        methodMap.setMethod(method);
        methodMap.setHttpMethode(urlMethod);
        methodMap.setJson(isJson);
        methodMap.setAuthRequired(isAuthRequired);
        return methodMap;
    }
}
