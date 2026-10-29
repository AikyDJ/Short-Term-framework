# Implemetation de Sprint 2
## Framework

### Pourquoi ?
on veut que les methode des class contenue dans l'annnotation `@Controller` peuve etre mapper a une requete URL
### MAJ
- Refactoristion de la classe fille de HttpServelet en ajoutant `AppInitilizer` pour recuperre les information et context parm donner dans les ficher web.xml .
- Ajout de nouvel annotation `MapUrl` qui se place sur les methode , avec path attribue qui a besoin de string pour set les route utiliser pour apeller les methode dans la class annoter controller .
- ajout de `ClassScanner` et `UrlUtils` class de support qui aide a la normaistion de url et au scan reflective des methode d'une class .
## Developper

ce qui chage chez le developper 
- `list_separator` est optionnel avec unne valeur par defaut `,`
- `AppInitializer` doit etre appeler entant que listner dans le web.xml

#### web.xml
```xml
    <context-param>
        <param-name>package_list</param-name>
        <param-value>${package.src}</param-value>
    </context-param>
    <!-- optionel -->
    <context-param>
        <param-name>list_separator</param-name>
        <param-value>,</param-value>
    </context-param>

    <!-- Runs at startup and does the scan -->
    <listener>
        <listener-class>mg.sprintlab.core.AppInitializer</listener-class>
    </listener>
```
#### Controller & MapUrl
```java
@Controller
public class HomeController {

    @MapUrl(path = "/")
    public String home() {
        return "Home page";
    }
}
```