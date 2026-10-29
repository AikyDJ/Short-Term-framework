package mg.sprintlab.core.mapping;

import mg.sprintlab.annotation.MapUrl;
import mg.sprintlab.annotation.enums.HTTPMethode;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteMapping {
    private RouteMapping() {
    }

    public static Map<UrlInfo, MethodMapping> scanAndBuildMapping(List<Class<?>> controllerList) {
        Map<UrlInfo, MethodMapping> methodMap = new HashMap<>();
        for (Class<?> controller : controllerList) {
            Method[] methods = controller.getDeclaredMethods();
            for (Method toAnalyze : methods) {
                if (toAnalyze.isAnnotationPresent(MapUrl.class)) {
                    MapUrl urlAn = toAnalyze.getAnnotation(MapUrl.class);
                    String path = urlAn.path();
                    HTTPMethode urlMethod = urlAn.methode();
                    methodMap.put(new UrlInfo(path, urlMethod), buildMapping(toAnalyze, controller, path, urlMethod));
                }
            }
        }
        return methodMap;
    }

    public static MethodMapping buildMapping(Method method, Class<?> controller, String path, HTTPMethode urlMethod) {
        MethodMapping methodMap = new MethodMapping();
        methodMap.setControllerClass(controller);
        methodMap.setPathToFile(path);
        methodMap.setMethod(method);
        methodMap.setHttpMethode(urlMethod);
        return methodMap;
    }
}
