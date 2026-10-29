package mg.sprintlab.web;

import mg.sprintlab.utils.UrlUtils;
import mg.sprintlab.core.AppInitializer;
import mg.sprintlab.core.mapping.MethodMapping;
import mg.sprintlab.core.mapping.UrlInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class MainController extends HttpServlet {

    private Map<UrlInfo, MethodMapping> routesMapping;
    private ViewDispach viewDispatcher;
    private boolean debugMode;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // GET
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            System.err.println("Error occurred while processing request: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // POST
        try {
            processRequest(req, resp);
        } catch (Exception e) {
            System.err.println("Error occurred while processing request: " + e.getMessage());
        }
    }

    @Override
    public void init() throws ServletException {
        routesMapping = (Map<UrlInfo, MethodMapping>) getServletContext().getAttribute(AppInitializer.ROUTE_MAPPER);
        viewDispatcher = (ViewDispach) getServletContext().getAttribute(AppInitializer.VIEW_DISPATCHER);
        Object debugModeObj = getInitParameter(AppInitializer.DEBUG_MODE_PARAM) ;
        debugMode = debugModeObj != null ;
        System.out.println("[FRAMEWORK] MainController initialized with debugMode = " + debugMode);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = UrlUtils.normalize(request.getRequestURI().substring(request.getContextPath().length()));
        if (debugMode) {
            response.setContentType("text/plain;charset=UTF-8");
            debug(path, request, response);
        }
        //
        UrlInfo urlInfo = new UrlInfo(path, null);
        if (routesMapping.containsKey(urlInfo)) {
            MethodMapping methodMapping = routesMapping.get(urlInfo);
            //
            Object modelView = methodMapping.executeMethod();
            if (modelView instanceof ModelView m) {
                viewDispatcher.render(m, request, response);
            }
        }

    }

    private void debug(String path, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // This method can be used to print debug information if needed
        Map<UrlInfo, MethodMapping> mappings = routesMapping;
        // get all mapped methods and print them for debugging
        try (PrintWriter out = response.getWriter()) {
            out.println("\nRequested Path: " + path);
            out.println("Mapped Methods:");
            for (Map.Entry<UrlInfo, MethodMapping> entry : mappings.entrySet()) {
                UrlInfo urlInfo = entry.getKey();
                MethodMapping methodMapping = entry.getValue();
                if (urlInfo.getRoute().equals(path)
                        && request.getMethod().equalsIgnoreCase(urlInfo.getHttpMethode().name())) {
                    out.println(methodMapping.toString());
                    out.println("---------------[EXECUTE]---------------");
                    out.println(methodMapping.executeMethod());
                }
            }
        }
    }
}
