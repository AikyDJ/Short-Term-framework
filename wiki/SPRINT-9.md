# implemetation  Sprint 9 
## Framework 
#### Pourquoi ? 
On veut garde en memoire les sessions on a crees un class fille de http session qui dispose des meme atribut de http session
#### MAJ
- Ajout de la classe `session` qui enregistre les derrniere conection es les attribur
- Ameliaration de context listner pour maetre optionnel quelque atribut dans le web.xml
## Developpeur
#### Session
creation de la session
```java
@MapUrl(path = "/login", methode = HTTPMethode.POST)
public String login(Session session, @RequestParam("name") String name) {
    session.setAttribute("user", name);
    return "home";
}

@MapUrl(path = "/profile")
public ModelView profile(Session session) {
    ModelView mv = new ModelView("profile");
    mv.addItem("user", session.getAttribute("user", String.class));
    return mv;
}

@MapUrl(path = "/logout")
public String logout(Session session) {
    session.invalidate();
    return "login";
}
```

