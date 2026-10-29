package mg.sprintlab.controller;

import mg.sprintlab.annotation.Controller;
import mg.sprintlab.utils.ClassPathScanner;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Url extends HttpServlet {

        private List<Class<?>> classList;


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
        System.out.println("Url servlet initialized.");
        String blockPackage = getServletContext().getInitParameter("package_list");
        String separator = getServletContext().getInitParameter("list_separator");
        
        String[] packageList = blockPackage.split(separator);
        Set<Class<?>> withAnnotation = new HashSet<>();
        ClassPathScanner clp = new ClassPathScanner();

        for (String pkg : packageList) {
            classList = clp.getClasses(pkg);
            for (Class<?> cls : classList) {
                if (cls.isAnnotationPresent(Controller.class)) {
                    withAnnotation.add(cls);
                }
            }
        }

        classList = new ArrayList<>(withAnnotation);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        // out.println("The called url : " + request.getRequestURL());
                response.setContentType("text/html");
        out.println("<p> The method : " + request.getMethod() + "</p>");
        out.println("<p> The called url : " + request.getRequestURL() + "</p>");

        out.println("<h1> Class list : </h1>");
        out.println("<ul>");
        for (Class<?> cls : classList) {
            out.println("<li>" + cls.getName() + "</li>");
        }
        out.println("</ul>");
    }

}
