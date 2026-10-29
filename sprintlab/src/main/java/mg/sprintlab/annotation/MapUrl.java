package mg.sprintlab.annotation;

import mg.sprintlab.annotation.enums.HTTPMethode;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention (RetentionPolicy.RUNTIME)
public @interface MapUrl {
    String path() default "/";
    HTTPMethode methode() default HTTPMethode.GET;
}
