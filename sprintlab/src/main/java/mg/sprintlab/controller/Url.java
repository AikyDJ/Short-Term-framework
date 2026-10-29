package mg.sprintlab.controller;

import mg.sprintlab.utils.UrlUtils;
import mg.sprintlab.core.AppInitializer;
import mg.sprintlab.core.mapping.MethodMapping;
import mg.sprintlab.core.mapping.RouteMapping;
import mg.sprintlab.core.mapping.UrlInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Url extends HttpServlet {

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

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        String path = UrlUtils.normalize(request.getRequestURI().substring(request.getContextPath().length()));
        List<Class<?>> listClass = (List<Class<?>>) getServletContext().getAttribute(AppInitializer.LIST_CLASS);

        if (listClass == null) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try (PrintWriter out = response.getWriter()) {
                out.println("Framework not initialized: InitializerContextListener missing in web.xml");
            }
            return;
        }

        Map<UrlInfo, MethodMapping> mappings = RouteMapping.scanAndBuildMapping(listClass);
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
