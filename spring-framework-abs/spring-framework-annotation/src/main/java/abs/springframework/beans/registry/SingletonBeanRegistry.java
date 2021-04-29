package abs.springframework.beans.registry;

/**
 * @author whig
 * @date 2021/4/29 15:09
 * @desc
 */
public interface SingletonBeanRegistry {


    public Object getSingleton(String beanName) throws Exception;
}
