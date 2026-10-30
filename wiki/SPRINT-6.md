# Implemetation Sprint 6

## Framework
#### Pourquoi
comme pour pour chaque frmamework on veut recupere des information via des les API donc on doit convertir les object en `json` pour faciliter la comunication REST
#### MAJ
- ajout de l'annotaion `RequestBody` a ajouter sur les methode coupler a MapUrl pour sortire les donne en JSON
- importaion de OjectWriter pour la convretion en JSON des objet
## Developper
- Nouvel annotaion `RequestBody` utilisable par dessus des methode
- le Model view maintenant peut directement etre afficher si le type de retour de la mehtode et de type String
#### RequestBody
```java
@Controller
public class DataController {
    @RequestBody 
    @MapUrl(path = "/api/data", methode = HTTPMethode.GET)
    public Car getDataREST() {
        return new Car("Audi", "2525TCE");
    }
}
```
```json
{
  "model" : "Audi",
  "matricule" : "2525TCE"
}
```
