package abs.springframework.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author whig
 * @date 2021/4/26 10:00
 * @desc
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)             //再类上生效呗
public @interface Scope {
    String value();      //prototype singleton 一个就是表示原型  一个就是表示单例bean
}
