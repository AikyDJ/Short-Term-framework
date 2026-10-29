# Impletation Sprint 4
## Framework
#### pouquoi ?
initialistion du servelet doit etre delocaliser hors de la methode `init()` . d'ou on fait appel au liter en invocan un context lister
#### MAJ
- chagement de nom de la class `Url` en `MainController` dans le package web
## Developer
- Modification du mount point du HTTPservelet server  de `mg.sprintlab.controller.Url` en `mg.sprintlab.web.MainController`
```xml
```xml
    <!-- request handler -->
    <servlet>
        <servlet-name>httpwall</servlet-name>
        <servlet-class>mg.sprintlab.web.MainController</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>httpwall</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
```
```