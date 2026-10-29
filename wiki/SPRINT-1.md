# Implementation de Sprint 1

## Framework
#### Pourquoi ?
pour savoir quel class est un controller, on doit cree une **anotaion** qui se place sur la classe existante puis compare toute les class qui contient se anatotion pour les attribuer des fonction specifique au controller (__ou d'autre anotation__) dans le futur.
#### Ajout
- `anotation.Controller` permet de dire au frame work que cette clase et un controller
- `utils.classPathScanner` permet de scan tout le repertoire du projet utilisant le framework 
#### Modif
- `url` utilistaion de la fonction `init()` servelet pour initialiser les contexr parm
 
## Developper
#### Comment ?
pour le developeur pour beneficier de la fontionaliter du framwork le web.xml doit contenir les contexts parameters `pakage_list` et `list_separator` qui permet au framework de siture toute le class du projet
```xml
    <context-param>
        <param-name>package_list</param-name>
        <param-value>${package.src}</param-value> 
    </context-param>
    <context-param>
        <param-name>list_separator</param-name>
        <param-value>,</param-value>
    </context-param>
```