package mg.sprintlab.web;

import mg.sprintlab.utils.UrlUtils;
import mg.sprintlab.utils.JsonConvertor;
import mg.sprintlab.annotation.enums.HTTPMethode;
import mg.sprintlab.core.AppInitializer;
import mg.sprintlab.core.mapping.MethodMapping;
import mg.sprintlab.core.mapping.UrlInfo;
import mg.sprintlab.exception.AuthException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class MainController extends HttpServlet {

    private Map<UrlInfo, MethodMapping> routesMapping;
    private ViewDispach viewDispatcher;
    private boolean debugMode;
    private JsonConvertor jsonConvertor;

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
    @SuppressWarnings("unchecked")
    public void init() throws ServletException {
        routesMapping = (Map<UrlInfo, MethodMapping>) getServletContext().getAttribute(AppInitializer.ROUTE_MAPPER);
        viewDispatcher = (ViewDispach) getServletContext().getAttribute(AppInitializer.VIEW_DISPATCHER);
        Object debugModeObj = getServletContext().getAttribute(AppInitializer.DEBUG_MODE_PARAM);
        debugMode = Boolean.TRUE.equals(debugModeObj);
        jsonConvertor = (JsonConvertor) getServletContext().getAttribute(AppInitializer.JSON_CONVERTOR);
        System.out.println("[FRAMEWORK] MainController initialized with debugMode = " + debugMode);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        String path = UrlUtils.normalize(request.getRequestURI().substring(request.getContextPath().length()));

        if (debugMode) {
            response.setContentType("text/plain;charset=UTF-8");
            debug(path, request, response);
        }

        HTTPMethode httpMethode;
        try {
            httpMethode = HTTPMethode.valueOf(request.getMethod().toUpperCase());
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, request.getMethod());
            return;
        }

        MethodMapping methodMapping = routesMapping.get(new UrlInfo(path, httpMethode));
        if (methodMapping == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND,
                    "No route for " + request.getMethod() + " " + path);
            return;
        }

        try {

            Object viewresp = methodMapping.executeMethod(request, response);

            if (methodMapping.isJson()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(jsonConvertor.toJson(viewresp));
            } else if (viewresp instanceof ModelView m) {
                viewDispatcher.render(m, request, response);
            } else if (viewresp instanceof String s) {
                viewDispatcher.render(new ModelView(s), request, response);
            }
        } catch (Exception e) {
            log("Error while processing request", e);
            if (e.getClass().equals(AuthException.class)) {
                response.sendError(AuthException.getErrorCode(), e.getMessage());
            }
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
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
            out.println("-------------------------------");
            for (Map.Entry<UrlInfo, MethodMapping> entry : mappings.entrySet()) {
                UrlInfo urlInfo = entry.getKey();
                MethodMapping methodMapping = entry.getValue();
                if (urlInfo.getRoute().equals(path)
                        && request.getMethod().equalsIgnoreCase(urlInfo.getHttpMethode().name())) {
                    out.println(methodMapping.toString());
                    out.println("---------------[EXECUTE]---------------");
                    out.println(methodMapping.executeMethod(request, response));
                }
            }
        }
    }
}
