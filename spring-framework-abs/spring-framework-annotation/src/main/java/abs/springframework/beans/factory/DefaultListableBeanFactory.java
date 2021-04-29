package abs.springframework.beans.factory;

import abs.springframework.beans.annotation.Autowired;
import abs.springframework.beans.definition.BeanDefinition;
import abs.springframework.beans.registry.BeanDefinitionRegistry;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author whig
 * @date 2021/4/29 10:53
 * @desc
 */
public class DefaultListableBeanFactory extends AbstractBeanFactory implements BeanDefinitionRegistry {

    //bean信息名字 根据注册顺序来保存到List集合当中
    private volatile List<String> beanDefinitionNames = new ArrayList<>(256);


    //在refresh()方法中倒数第二步骤呢 在这里哈呢
    public void preInstantiateSingletons() throws Exception {
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            String beanName = entry.getKey();
            BeanDefinition bd = entry.getValue();
            //if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {  源码 当中这样写 这里边就是简化
            if (bd == null) {
                throw new RuntimeException("bean信息为null , 请检查bean名字是否正确");
            }
            if (bd.isSingleton()) {

                //在这里并判断是不是FactoryBean 的对象 这里步进行考虑
                //getBean方法 具体在BeanFactory中实现呗
                getBean(beanName);
            }
        }
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition bd) throws Exception {
        beanDefinitionMap.put(beanName, bd);
    }


    /**
     * 这个在源码当中 是在AbstractAutowireCapableBeanFactory          为了简化开发 DefaultListableBeanFactory在这里进行完成呗
     *
     * @param beanName bean名字
     * @param bd       bean信息
     */
    protected Object createBean(String beanName, BeanDefinition bd) throws Exception {
        //源码当中进行一些逻辑判断 在这里就是不整了哈呢
        return doCreateBean(beanName, bd);
    }

    private Object doCreateBean(String beanName, BeanDefinition bd) throws Exception {

        //源码当中 这里边有wrapper接口  在这里就是先不用了呢 先实现逻辑功能呗 在这里哈呢
        // 在源码当中createBeanInstance 推选构造器    简化实现 我们就是使用无参来构造呗
        Object bean = bd.getBeanClass().getDeclaredConstructor().newInstance();


        //----------------------------1.这一步算实例化完成 --------------------------------------------
        //----------------------------2.开始属性赋值   beanPostProcessor先不用处理

        Object exposedObject = bean;
        try {
            //实例化Bean实例之后 就是开始属性填充了呢 在这一步呗
            populateBean(beanName, bd, bean);

            //实例化之后就是开始调用Aware接口
            exposedObject = initializeBean(beanName, exposedObject, bd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Bean结束之后我们要注入到单例池当中呗
        return exposedObject;
    }

    /**
     * bean生命周期 最重要得一步 在这里就是开始属性赋值呗
     * 实例化Bean 之后 在这里就是进行属性赋值的呢 主要就是 @Autowired注解  后续的Value注解什么的再说
     * 源码当中属性赋值太过于麻烦 这里就是简单实现一个就得了
     *
     * @param bd
     */
    protected void populateBean(String beanName, BeanDefinition bd, Object bean) {
        Class clazz = bd.getBeanClass();
        try {
            Field[] fields = clazz.getDeclaredFields();         //这里边一定要注意要是用 getDeclaredFields()这个函数 不要使用getFields()这个函数呢否则私有字段拿不出来

            for (Field field : fields) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    //例如就是Private User user  在这里我们就是要必须获得user实例呗
                    //因为 这里就是依赖于dependOn  在先头的递归里边 我们应该是创建了这个Bean依赖
                    String dependBeanName = field.getName();
                    Object dependBean = getSingleton(dependBeanName);

                    field.setAccessible(true);              //这里边就是必须要设置这个权限 否则最后会报错呢 在这里哈呢

                    field.set(bean, dependBean);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行一些初始化
     * 初始化这个接口 后续在进行扩展
     */
    protected Object initializeBean(String beanName, Object bean, BeanDefinition bd) {
        //主要就是BeanPostProcessor 以及Wrapper接口
        return bean;
    }
}
