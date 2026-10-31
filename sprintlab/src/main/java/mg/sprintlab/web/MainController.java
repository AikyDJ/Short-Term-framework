package mg.sprintlab.web;

import mg.sprintlab.utils.UrlUtils;
import mg.sprintlab.utils.JsonConvertor;
import mg.sprintlab.core.AppInitializer;
import mg.sprintlab.core.mapping.MethodMapping;
import mg.sprintlab.core.mapping.UrlInfo;

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
    private String sessionStringAttr = AppInitializer.SESSION_ATTRIBUTE;
    
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
    @SuppressWarnings ("unchecked")
    public void init() throws ServletException {
        routesMapping = (Map<UrlInfo, MethodMapping>) getServletContext().getAttribute(AppInitializer.ROUTE_MAPPER);
        viewDispatcher = (ViewDispach) getServletContext().getAttribute(AppInitializer.VIEW_DISPATCHER);
        Object debugModeObj = getServletContext().getAttribute(AppInitializer.DEBUG_MODE_PARAM);
        debugMode = Boolean.TRUE.equals(debugModeObj);
        jsonConvertor = (JsonConvertor) getServletContext().getAttribute(AppInitializer.JSON_CONVERTOR);
        sessionStringAttr = (String) getServletContext().getAttribute(AppInitializer.SESSION_ATTRIBUTE);
        System.out.println("[FRAMEWORK] MainController initialized with debugMode = " + debugMode);
    }

        private void setSession(HttpSession session) {
        try {
            Session source = (Session) session.getAttribute(sessionStringAttr);
            if(source == null) return;

            List<String> attributeNames = Collections.list(session.getAttributeNames());

            for(String attribute : attributeNames){
                if(!source.getAttributes().containsKey(attribute)){
                    session.removeAttribute(attribute);
                }
            }

            source.getAttributes().forEach((key , value) -> {
                System.out.println(key);
                session.setAttribute(key,value);
            });
        } catch (Exception e){
            System.err.println("Error occurred while synchronizing session: " + e.getMessage());
        }
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String path = UrlUtils.normalize(request.getRequestURI().substring(request.getContextPath().length()));
        if (debugMode) {
            response.setContentType("text/plain;charset=UTF-8");
            debug(path, request, response);
        }
        //
        setSession(request.getSession());
        UrlInfo urlInfo = new UrlInfo(path, null);
        if (routesMapping.containsKey(urlInfo)) {
            MethodMapping methodMapping = routesMapping.get(urlInfo);
            Object viewresp = methodMapping.executeMethod(request, response);
            if (methodMapping.isJson()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().println(jsonConvertor.toJson(viewresp));
            } else if (viewresp instanceof ModelView m) {
                viewDispatcher.render(m, request, response);
            } else if (viewresp instanceof String s) {
                viewDispatcher.render(new ModelView(s), request, response);
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
                    out.println(methodMapping.executeMethod(request, response));
                }
            }
        }
    }
}
