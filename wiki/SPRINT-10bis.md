# Implementation Sprint 10 bis
## Framework
#### Pourquoi ?
Precedement on a vue que @Auth controler blocque la methode sur la quel elle es attribuer mai on veut aussi quel ger les role complex comme ADMIN RH , ...
#### MAJ
- ajout des Attribut role dans session comme valeur par defaut USER
- surperetion du role par defaut ADMIN dans Auth
## developer
#### Controller
- on peut set le role en appelant role ne prend que 1 seul param e a come valeur defaut `USER`
```java
session.setRole("RH");
```
- attribution des role sur la methode
```java
    @Auth(roles = {"ADMIN","RH"})
    @MapUrl (path = "/admin", methode = HTTPMethode.GET)
    public String adminPage() {
        // .  .  . 
        return "admin";
    }
```
