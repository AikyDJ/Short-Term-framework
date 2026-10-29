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
        StringBuilder pathBuilder = new StringBuilder(prefix);
        pathBuilder.append(view);
        pathBuilder.append(suffix);
        return pathBuilder.toString();
    }

    private void prepareRequest(ModelView modelView, HttpServletRequest request) {
        modelView.getData().forEach(request::setAttribute);
    }
}
