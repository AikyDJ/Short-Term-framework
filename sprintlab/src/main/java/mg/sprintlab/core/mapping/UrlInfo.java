package mg.sprintlab.core.mapping;

import mg.sprintlab.annotation.enums.*;

public class UrlInfo {
    HTTPMethode httpMethode;
    String route;

    public UrlInfo(String path, HTTPMethode urlMethod) {
        this.route = path;
        this.httpMethode = urlMethod == null ? HTTPMethode.GET : urlMethod;
    }

    public HTTPMethode getHttpMethode() {
        return httpMethode;
    }

    public void setHttpMethode(HTTPMethode httpMethode) {
        this.httpMethode = httpMethode == null ? HTTPMethode.GET : httpMethode;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UrlInfo url = (UrlInfo) obj;
        return url.getRoute().equals(getRoute()) && getHttpMethode() == url.getHttpMethode();
    }

    @Override
    public int hashCode() {
        return 31 * route.hashCode() + httpMethode.hashCode();
    }

}