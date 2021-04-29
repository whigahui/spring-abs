package abs.springframework.beans.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author whig
 * @date 2021/4/26 11:32
 * @desc
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD})
public @interface Autowired {
    //作用在 字段上 或者就是方法上呗 在这里哈呢
}
