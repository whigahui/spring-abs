package abs.springframework.beans.registry;

import abs.springframework.beans.definition.BeanDefinition;

/**
 * @author whig
 * @date 2021/4/29 15:05
 * @desc 注册Bean信息 在这里哈呢
 */
public interface BeanDefinitionRegistry {



    void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws Exception;

}
