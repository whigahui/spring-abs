package abs.springframework.beans.definition;

/**
 * @author whig
 * @date 2021/4/26 21:40
 * @desc 对Bean信息定义得一些包装
 */
public class BeanDefinition {

    private Object bean;                    //实例化后的对象

    private Class beanClass;                //beanClass类型 User.class

    private String beanClassName;           //bean类名  User.getClass.getName()==com.whig.User

    //这两个boolean变量不要包装类型 如果是Boolean类型 那么变量值默认为null  如果是boolean 变量值默认为false
    private volatile boolean singleton;              //是否是单例模式

    private volatile boolean prototype;             //判断是否是多例模式呗

    private String[] dependsOn;             //String[]数组 在这里就是检查循环依赖呗 但是在这里就是这个依赖需要怎么获取呢 如何判断依赖另一个对象呢


    //---------------------------------------------getter and setter 方法---------------------
    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }


    public Boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }

    public Boolean isPrototype() {
        return prototype;
    }

    public void setPrototype(Boolean prototype) {
        this.prototype = prototype;
    }

    public String[] getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(String[] dependsOn) {
        this.dependsOn = dependsOn;
    }
}
