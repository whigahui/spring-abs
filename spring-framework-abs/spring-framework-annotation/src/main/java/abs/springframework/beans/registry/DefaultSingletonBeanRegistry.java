package abs.springframework.beans.registry;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author whig
 * @date 2021/4/29 15:10
 * @desc BeanFactory只是获取Bean 而真正创建Bean的是DefaultSingletonBeanRegistry 这个相关类呗
 * 原来，DefaultSingletonBeanRegistry搞了一个Set<String> singletonsCurrentlyInCreation，专门来存放正在创建的单例bean的名字
 * （注意，只是名字而不是bean，因为bean还在创建中）。
 * DefaultSingletonBeanRegistry没有getBean()方法，因为它压根就没实现BeanFactory！！
 * 它实现的是SingletonBeanRegistry，专门管理单例bean的。
 */
public class DefaultSingletonBeanRegistry implements SingletonBeanRegistry {

    /**
     * Cache of singleton objects: bean name to bean instance.          一级缓存 存放真正的单例Bean
     */
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

    /**
     * Cache of early singleton objects: bean name to bean instance.    二级缓存 发生循环依赖时， 作为早期引用
     */
    private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);

    /**
     * Cache of singleton factories: bean name to ObjectFactory.        三级缓存用于解决循环依赖呗 AOP中很重要 在这里哈呢
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);


    /**
     * Set of registered singletons, containing the bean names in registration order.
     * 存放单例的名字 顺序来存储呗
     */
    private final Set<String> registeredSingletons = new LinkedHashSet<>(256);



    @Override
    public Object getSingleton(String beanName) throws Exception {
        return getSingleton(beanName, true);
    }

    /**
     * 这个实现其实是在DefaultisableBeanFactory
     *
     * @param beanName            the name of the bean to look for
     * @param allowEarlyReference whether early references should be created or not
     * @return the registered singleton object, or {@code null} if none found
     */
    public Object getSingleton(String beanName, boolean allowEarlyReference) throws Exception {
        // Quick check for existing instance without full singleton lock
        Object singletonObject = this.singletonObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = this.earlySingletonObjects.get(beanName);
            if (singletonObject == null && allowEarlyReference) {
                synchronized (this.singletonObjects) {

                    // 单例模式 双重检查机制 非常棒
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        singletonObject = this.earlySingletonObjects.get(beanName);
                        if (singletonObject == null) {
                            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
                            if (singletonFactory != null) {
                                singletonObject = singletonFactory.getObject();
                                this.earlySingletonObjects.put(beanName, singletonObject);
                                this.singletonFactories.remove(beanName);
                            }
                        }
                    }
                }
            }
        }
        return singletonObject;
    }


    /**
     * Return the (raw) singleton object registered under the given name,creating and registering a new one if none registered yet.
     *
     * @param beanName         the name of the bean
     * @param singletonFactory the ObjectFactory to lazily create the singleton with, if necessary
     * @return the registered singleton object
     */
    public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) throws Exception {
        synchronized (this.singletonObjects) {
            Object singletonObject = this.singletonObjects.get(beanName);         //1.先重单例池中检查 看存在不以创建好的
            // 在单例对象创建前先做一个标记
            // 将beanName放入到singletonsCurrentlyInCreation这个集合中
            // 标志着这个单例Bean正在创建
            // 如果同一个单例Bean多次被创建，这里会抛出异常
            if (singletonObject == null) {

                boolean newSingleton = false;

                try {
                    //上游传入的lambda在这里会被执行，调用createBean方法创建一个Bean后返回
                    singletonObject = singletonFactory.getObject();     //ObjectFactory中的方法
                    newSingleton = true;
                } catch (IllegalStateException ex) {
                    // Has the singleton object implicitly appeared in the meantime ->
                    // if yes, proceed with it since the exception indicates that state.
                    singletonObject = this.singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        throw ex;
                    }
                }

                if (newSingleton) {
                    addSingleton(beanName, singletonObject);
                }
            }
            return singletonObject;
        }
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        synchronized (this.singletonObjects) {
            this.singletonObjects.put(beanName, singletonObject);       //存放在一级缓存单例池当中
            this.singletonFactories.remove(beanName);                   //三级缓存移除这个单例 不知道为啥？？？
            this.earlySingletonObjects.remove(beanName);                //二级缓存 作为早期循环依赖作用 这里边也移除？？？
            this.registeredSingletons.add(beanName);                    //根据beanName注册进去 防止被重复创建呗
        }
    }

}
