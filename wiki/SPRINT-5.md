# Implemtation Sprint 5
## Framework
#### Pourquoi ?
le but et de gere les page avec une classe apart en utilisent la methode du _dispacher_ natif au servelet
#### MAJ
- Changemt du fontionnement de Appinitilaizer
- Ajout de `ModelView`
- Ajout du mode debug
## Develeopeur
- Ajout des nouveau atri but relatif a l;implemetation de modelView 
  - `view_prefix` : le root repertoir ou se touve les vue
  - `view_sufix` : l'extention du ficher pour notre ficher de view dans notre cas `.jsp`
#### Web.xml
```xml
    <!-- debug mode -->
    <context-param>
        <param-name>debug_mode</param-name>
        <param-value>true</param-value>
    </context-param>

        <!-- prefix and suffix for view resolution -->
    <context-param>
        <param-name>view_prefix</param-name>
        <param-value>WEB-INF/pages/</param-value>
    </context-param>
    <context-param>
        <param-name>view_suffix</param-name>
        <param-value>.jsp</param-value>
    </context-param>

```

#### Controller
```java
    @MapUrl(path = "/", methode = HTTPMethode.GET)
    public ModelView home() {
        ModelView modelView = new ModelView("index");
        modelView.addItem("title", "Home Page");
        modelView.addItem("message", "Welcome to the Home Page");
        return modelView;
    }
```
#### View
```html
<h1>Welcome to the ${title}</h1>
<h2>${message}</h2>
```

