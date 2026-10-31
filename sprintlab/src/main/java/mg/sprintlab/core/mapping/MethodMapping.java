package mg.sprintlab.core.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mg.sprintlab.annotation.Auth;
import mg.sprintlab.annotation.enums.HTTPMethode;
import mg.sprintlab.exception.AuthException;
import mg.sprintlab.web.Session;

/*
 * Mapping class that holds information about a controller method, including the controller class, the path to the file, the method itself, and the HTTP method (GET or POST).
 */
public class MethodMapping {
    private Class<?> controllerClass;
    private String pathToFile;
    private Method method;
    private HTTPMethode httpMethode;
    private boolean isJson;
    private boolean isAuthRequired;
    private String[] authorizeRoles;
    
    public String[] getAuthorizeRoles() {
        return authorizeRoles;
    }
    public void setAuthorizeRoles(String[] authorizeRoles) {
        this.authorizeRoles = authorizeRoles;
    }
    public boolean isAuthRequired() {
        return isAuthRequired;
    }
    public void setAuthRequired(boolean isAuthRequired) {
        this.isAuthRequired = isAuthRequired;
    }
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
    public Object executeMethod(HttpServletRequest request, HttpServletResponse response) {
        try {
            if (method.isAnnotationPresent(Auth.class)) {
                Auth authAn = method.getAnnotation(Auth.class);
                String[] roles = authAn.roles();
                // get roles from session
                Session session = new Session(request);
                String userRole = session.getRole();
                boolean authorized = false;
                
                for (String role : roles) {
                    if (role.contains(userRole)) {
                        authorized = true;
                        break;
                    }
                }
                if (!authorized) {
                    throw new AuthException("User is not authorized to access this resource",403);
                }
            }
            Object[] args = mg.sprintlab.utils.ClassParmScanner.getMethodArgs(method, request, response);
            return method.invoke(controllerClass.getDeclaredConstructor().newInstance(), args);
        } catch (AuthException e) {
            throw e;
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof AuthException authException) {
                throw authException;
            }
            Throwable cause = e.getCause() == null ? e : e.getCause();
            throw new RuntimeException("Failed to execute method: " + method.getName()
                    + " in controller: " + controllerClass.getName() + " - " + cause.getMessage(), cause);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute method: " + method.getName() + " in controller: " + controllerClass.getName() + " - " + e.getMessage(), e);
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
