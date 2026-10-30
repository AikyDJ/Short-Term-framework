package mg.sprintlab.core.mapping;

import java.lang.reflect.Method;

import mg.sprintlab.annotation.enums.HTTPMethode;

/*
 * Mapping class that holds information about a controller method, including the controller class, the path to the file, the method itself, and the HTTP method (GET or POST).
 */
public class MethodMapping {
    private Class<?> controllerClass;
    private String pathToFile;
    private Method method;
    private HTTPMethode httpMethode;
    private boolean isJson;

    public boolean isJson() {
        return isJson;
    }
    public void setJson(boolean isJson) {
        this.isJson = isJson;
    }
    public Class<?> getControllerClass() {
        return controllerClass;
    }
    public void setControllerClass(Class<?> controllerClass) {
        this.controllerClass = controllerClass;
    }
    public String getPathToFile() {
        return pathToFile;
    }
    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }
    public Method getMethod() {
        return method;
    }
    public void setMethod(Method method) {
        this.method = method;
    }
    public HTTPMethode getHttpMethode() {
        return httpMethode;
    }
    public void setHttpMethode(HTTPMethode httpMethode) {
        this.httpMethode = httpMethode;
    }
    public Object executeMethod(){
        try {
            return method.invoke(controllerClass.getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute method: " + method.getName() + " in controller: " + controllerClass.getName(), e);
        }
    }

    private String debugLog() {
        StringBuilder sb = new StringBuilder();
        sb.append("Controller Class: ").append(controllerClass.getName()).append("\n");
        sb.append("Path to File: ").append(pathToFile).append("\n");
        sb.append("Method: ").append(method.getName()).append("\n");
        sb.append("HTTP Method: ").append(httpMethode).append("\n");
        String jsonstate = isJson ? " Active ": "";
        sb.append("REST Status:").append(jsonstate).append("\n");
        // list all methods of the controller class
        sb.append("Methods in Controller Class: \n");
        for (Method m : controllerClass.getDeclaredMethods()) {
            sb.append(" - ").append(m.getName()).append("\n");
        }

        return sb.toString();
    }
    @Override
    public String toString() {
        return debugLog();
    }

}
