## Implementation de SPRINT 0
### Cote dev
Implemetaion du controlleur qui bloque toutes les requetes http. Pour implementes cela configure le `web.xml` en ajoutant la class `mg.springlab.controller.url
```xml
    <!-- request handler -->
    <servlet>
        <servlet-name>httpwall</servlet-name>
        <servlet-class>mg.sprintlab.controller.Url</servlet-class>
    </servlet>
    <servlet-mapping>
        <servlet-name>httpwall</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>
```