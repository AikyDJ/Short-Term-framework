# Implemetation Srpint 7 bis
## Framework
#### Pourquoi ?
On veut que notre les requets ayant l'annotaion RequestParam peut supporter les type objet complex
#### MAJ 
- ajout de la class utils ObjectHAadeler qui recupre les valeur pour le transformet en string puis en objet en utilisant de la recurtion de ClassparmScanner
- pour cela on construit une scne le string de la maniere suivante
```
?car.model=nm&car.matricule=mal
```
## Developpeur
il peu maintenant faire
#### controller
```java
@Controller
public class DataController {
    @MapUrl(path = "/data/object", methode = HTTPMethode.GET)
    public ModelView getCar(@RequestParam("car") Car car) {
        ModelView modelView = new ModelView("data/object");
        modelView.addItem("car", car);
        return modelView;
    }
}
```
#### model view
```html
<h2>Bound object</h2>
<p>Model: ${car.model}</p>
<p>Matricule: ${car.matricule}</p>
```
