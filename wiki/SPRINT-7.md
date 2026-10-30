# Implemetaiton sprint 7
## Framework
#### Pourquoi ?
pour cette partie on veut que les methode dans le controller dispose de parametre standard que l'on puisse utitilieser a notre guise lors des request http GET ou POST
exemple :
```bash
curl 'http://localhost:8080/sprinttest/api/data/?id=1'
```
- `api/data/` est le route de la requet
- `?id=1` la partie ou transite les donne via les methode HTTP GET
En utilisant la methode standart serverlet on doit attribure le parametre de la requete http pour obtenir les requet en string
```java
int id = (int) request.getParameterValues("id")
```
Or que a se stade on ne peut pas directement acceder au `HttpServletRequest` dans le cote developper du framrwork
Donc on veut directement attribuer cette demande directement dans le controller grace a une annotation `@RequestParam`
#### MAJ
- Ajout annotaion `@ResquestParam` Qui se place sur l'argumment sible
- Ajout d'un Utils `ClassParmScanner` qui execute directemet l'objet en atribuant les parametres des fonction du controller contient comme parametre @ResquestParam
- Delocalistaion de la convertion en json avec une class utils `JsonConvertor`
## Developper
#### controller
```java
    @RequestBody 
    @MapUrl(path = "/api/data", methode = HTTPMethode.GET)
    public Car getDataREST(@RequestParam int id) {
        return id == 1 ? new Car("Audi", "2525TCE") : null;
    }
```
> on peut aussi utiliser @RequestParam( value = "id_car")
> `@RequestParam` a pour valeur par defaut le nom de l'argume


  
