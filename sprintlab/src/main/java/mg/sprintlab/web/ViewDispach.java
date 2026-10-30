package mg.sprintlab.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ViewDispach {

    
    private String prefix;
    private String suffix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void render(ModelView modelView, HttpServletRequest request, HttpServletResponse response) {
        String target = buildTargetView(modelView.getView());
        prepareRequest(modelView, request);
        System.out.println("FORWARD TO = " + target);

        RequestDispatcher dispatcher = request.getRequestDispatcher(target);
        try {
            System.out.println("BEFORE FORWARD");
            System.out.println("COMMITTED = " + response.isCommitted());
            System.out.println("DISPATCHER = " + dispatcher);
            dispatcher.forward(request, response);
            System.out.println("AFTER FORWARD");
        } catch (Exception e) {
            // e.printStackTrace();
            System.err.println("Error occurred while forwarding to view: " + e.getMessage());
        }
    }

    private String buildTargetView(String view) {
        String configuredPrefix = prefix == null ? "" : prefix;
        String configuredSuffix = suffix == null ? "" : suffix;
        String configuredView = view == null ? "" : view;

        StringBuilder pathBuilder = new StringBuilder(configuredPrefix);
        if (pathBuilder.isEmpty() || pathBuilder.charAt(0) != '/') {
            pathBuilder.insert(0, '/');
        }
        if (pathBuilder.length() > 1
                && pathBuilder.charAt(pathBuilder.length() - 1) == '/'
                && configuredView.startsWith("/")) {
            configuredView = configuredView.substring(1);
        }
        pathBuilder.append(configuredView);
        pathBuilder.append(configuredSuffix);
        return pathBuilder.toString();
    }

    private void prepareRequest(ModelView modelView, HttpServletRequest request) {
        modelView.getData().forEach(request::setAttribute);
    }
}
