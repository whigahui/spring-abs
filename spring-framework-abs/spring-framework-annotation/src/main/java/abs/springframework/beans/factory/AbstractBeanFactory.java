package abs.springframework.beans.factory;

import abs.springframework.beans.definition.BeanDefinition;
import abs.springframework.beans.registry.DefaultSingletonBeanRegistry;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whig
 * @date 2021/4/26 21:50
 * @desc
 */
public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {

    protected ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();  //bean最重要的信息 在这里哈呢

    /**
     * 依赖当中有所作用 当前这个bean 依赖于其他bean  例如UserService类当中 依赖于 UserDao userDao     User user
     * 我们要把当前Bean依赖的属性存放到这里边
     */
    private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);


    public Object getBean(String beanName) throws Exception {
        return doGetBean(beanName, null);
    }

    public Object getBean(String beanName, Class clazz) throws Exception {
        return doGetBean(beanName, clazz);
    }


    /**
     * protected表示在子类 继承者可以使用呗
     *
     * @param beanName     bean名字
     * @param requiredType 类名.class
     * @param <T>          返回类型class
     *                     -------------------------------------
     *                     1.主要就是这个GetBean流程呢 在这里哈呢 要注意呗哈
     */
    protected <T> T doGetBean(String beanName, Class<T> requiredType) throws Exception {
        Object sharedInstance;

        sharedInstance = getSingleton(beanName);

        if (sharedInstance != null) {
            //在源码当中 这里边就是检查是否是BeanFactory的实例  这里边就是不进行判断呗 在这里哈呢
            return (T) sharedInstance;
        }

        // Fail if we're already creating this bean instance:
        // We're assumably within a circular reference.     获取失败 但是我们假设当前bean存在循环依赖呗
        else {
            //源码当中一些属性检查太难 这里边就是简单的实现一下源码的逻辑
            //1.这里边不用判断 beanName是否存在 因为我们是重前边开始遍历过来的 讨论clazz 类型就是可以了呢
            BeanDefinition bd = beanDefinitionMap.get(beanName);

            String[] dependsOn = bd.getDependsOn();     //获取循环依赖的bean className  field.getClass().getName(); 在AnnotationApplicationContext扫描获取

            if (dependsOn != null) {                    //循环依赖的重点还有就是 利用Set集合来保存beanName
                //源码当中循环依赖依赖的是名字 例如user
                //这里边我们就是按名字来查询呗 按类型来查询太难了呢
                for (String depName : dependsOn) {
                    if (!beanDefinitionMap.containsKey(depName))
                        throw new RuntimeException("依赖的属性 找不到注册信息 请让属性名与注册bean名字一致");

                    //注册完当前bean信息之后
                    registerDependentBean(beanName, depName);       //单例和原型在这里需要进行后续判断呗  后续进行属性注入中在进行详细判断

                    getBean(depName);                               //递归先创建 Bean-->depName

                }
            }

            //正真开始创建Bean的时刻呗 在这里哈呢
            if (bd.isSingleton()) {
                sharedInstance = getSingleton(beanName, () -> {         //这一步的lambda表达式 始终就是没看明白 在这里哈呢
                    try {
                        //都找不到 在这里就是createBean呗
                        return createBean(beanName, bd);
                    } catch (Exception ex) {
                        throw ex;
                    }
                });
            }

            //如果是原型Bean 我们就创建不同的对象利用 DefaultListableBeanFactory种方法 CreateBean
            if (bd.isPrototype()) {
                return (T) createBean(beanName, bd);    //直接就是反射创建 我们不要通过单例池呗
            }
        }
        return (T) sharedInstance;
    }


    /**
     * @param beanName       当前Bean名字
     * @param dependBeanName 当前Bean 依赖于其他  BeanName
     *                       我们注册到          dependentBeanMap
     */
    private void registerDependentBean(String beanName, String dependBeanName) {

        synchronized (this.dependentBeanMap) {
            if (dependentBeanMap.containsKey(beanName)) {
                Set<String> set = dependentBeanMap.get(beanName);
                set.add(dependBeanName);
            }
            //第一次创建 不包含
            else {
                Set<String> set = new LinkedHashSet<>();
                set.add(dependBeanName);
                dependentBeanMap.put(beanName, set);
            }
        }
    }

    /**
     * AbstractBeanFactory 在这里只管实例化
     * 属性填充 和 初始化 我们在DefaultListableBeanFactory 中来实现呗
     */
    protected abstract Object createBean(String beanName, BeanDefinition bd) throws Exception;


    public boolean containsBean(String beanName) {
        return false;
    }

    public boolean isSingleton(String beanName) throws Exception {
        return false;
    }

    public boolean isPrototype(String beanName) throws Exception {
        return false;
    }
}
