package xhh.club.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})  //标记在属性上
@Retention(RetentionPolicy.RUNTIME)
public @interface XhhDI {
}
