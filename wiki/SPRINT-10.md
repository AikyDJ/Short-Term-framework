# Impemetation Sprint 10
## Framework
#### Pourquoi ?
on veut que les route dans les controler ayant l'annotation @Auth soit bloque et intercepter directememt par le servelet controlle
#### MAJ
- ajout de la class `@Auth` qui defini que la classe soit bloquer par l'utilisateur
- ajout `ExeptionAuth` qui gere les Autorisation
- update de la methode mappping pour supporter la  nouvelle annotaion `@Auth`
- Auth dispose de role standard en String[] par defaut `ADMIN`et `USER`
## Developeur
#### Controller
```java
    @Auth
    @MapUrl (path = "/admin", methode = HTTPMethode.GET)
    public String adminPage() {
        // ( . . . )
        return "admin";
    }
```