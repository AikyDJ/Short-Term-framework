# Implemetaion Sprint 8
## Framework
#### pouquoi ?
on veut uploder des ficher dans le server
#### MAJ
- ajourt Methode `FileMapping` qui mape lse ficher recu en paht et en en liste de byte
## Developper
- peut chager le repertoire des upload
#### web.xml
```xml
<context-param>
    <param-name>upload_directory</param-name>
    <param-value>/var/www/my-app/uploads</param-value>
</context-param>
```