package abs.springframework.beans.factory;

/**
 * @author whig
 * @date 2021/4/8 15:19
 * @desc BeanFactory根据下边的方法就是典型的工厂模式
 */
public interface BeanFactory {

    //这里边就是为了区别BeanFactory和FactoryBean之间得东西被
    String FACTORY_BEAN_PREFIX = "&";   //这里边就是为了区别BeanFactory和FactoryBean创建得Bean对象 我们用&区别一下呗

    /**
     * 根据名称从容器中获取bean
     *
     * @param beanName bean的名字
     * @return bean实例对象
     */
    public Object getBean(String beanName) throws Exception;    //通过名字来获取Bean

    public Object getBean(String beanName, Class clazz) throws Exception;    //通过类型来获取Bean

    boolean containsBean(String beanName);

    boolean isSingleton(String beanName) throws Exception;  //否为单实例

    boolean isPrototype(String beanName) throws Exception;// 是否为原型（多实例）

}
