# Implemetation de Sprint 3
## Framework

### Pourquoi ?
Grace au srpint 2 on peut map les url a une methode a une route url on veut maintenant que les methode soi gere par les requet HTTP standard GET et POST
### MAJ
- Ajout de l'enum `HTTPMethode` qui contient les variables GET et POST

## Developper
- `MapUrl` a maintenant un novel argument HTTPMethode
#### MapUrl
```java
@MapUrl(path = "/", methode = HTTPMethode.GET)
public String home() {
        return "Home page";
}
```