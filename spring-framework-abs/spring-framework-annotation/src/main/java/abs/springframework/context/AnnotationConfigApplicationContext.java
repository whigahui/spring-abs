package abs.springframework.context;

import abs.springframework.beans.annotation.Autowired;
import abs.springframework.beans.annotation.Component;
import abs.springframework.beans.annotation.ComponentScan;
import abs.springframework.beans.annotation.Scope;
import abs.springframework.beans.definition.BeanDefinition;
import abs.springframework.beans.factory.DefaultListableBeanFactory;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author whig
 * @date 2021/4/26 22:05
 * @desc
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {


    private Class clazz;

    //在源码当中 DefaultListableBeanFactory 是在GenericApplicationContext中创建的 为了简化在这里创建
    private DefaultListableBeanFactory beanFactory;

    //我们就是需要进行注解扫描 所以不需要其他的内容 传进来一个配置类 并判断@ComponetScan
    public AnnotationConfigApplicationContext(Class clazz) throws Exception {
        this.clazz = clazz;
        this.beanFactory = new DefaultListableBeanFactory();
        scan(clazz);                     //进行包扫描  同时注册bean信息 到mapDefinitionMap当中

        //为什么ApplicationContext是高级容器 而BeanFactory是低级容器  就是ApplicationContext 有refresh步骤
        refresh();
    }

    //在这里就是进行包扫描 扫描@ComponetScan注解呗
    private void scan(Class clazz) throws Exception {
        if (clazz.isAnnotationPresent(ComponentScan.class)) {
            //1. 这里边注解就是必须要强转一下  拿不到@ComponentScan中的value值呗
            ComponentScan componentScanAnnotation = (ComponentScan) clazz.getAnnotation(ComponentScan.class);
            String path = componentScanAnnotation.value();

            String classPath = path.replace(".", "/");     //abs/springframework/zztest

            //2. 知道类路径之后 我们需要扫描这个包下的所有类呗
            //   BootStrap Ext App  我们就是使用App类加载器  获取资源之后来扫描这个包下的所有类呗
            ClassLoader classLoader = AnnotationConfigApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(classPath);               //通过类路径获取url  在url-->File

            if (resource != null) {                                         //需要对获取的资源进行一下判断被
                File file = new File(resource.getFile());
                if (file.isDirectory()) {                                   //并判断这个文件是不是文件夹
                    File[] files = file.listFiles();
                    for (File f : files) {
                        String fileName = f.getAbsolutePath();

                        if (fileName.endsWith(".class")) {                //对文件移除不是Class类型的
                            //E:\offer\spring-framework-abs\spring-framework-annotation\target\classes\abs\springframework\zztest\AppConfig.class
                            //classes\ 占位8个字符，我们对其进行截取。找到我们开始包开始的地方
                            String className = fileName.substring(fileName.indexOf("classes\\") + 8, fileName.indexOf(".class"));

                            //知道了ClassName 我们接下来就是进行判断 判断当前class是否包含@Component注解  解析Bean信息 之后进行注册呗
                            className = className.replace("\\", ".");
                            register(className, classLoader);
                        }
                    }
                } else {
                    throw new RuntimeException("classPath error : @Component中value值 类路径不对");
                }


            } else {
                //如果不包含这个 @ComponentScan这个注解  我们就是扔出异常呗
                throw new RuntimeException("没有找到@ComponentScan 包扫描注解");
            }

        }

    }

    /**
     * 主要作用 判断是否包含@Component 如果包含解析Bean信息  解析并放到 beanDefinitionMap
     *
     * @param className   包种的className
     * @param classLoader 类加载器
     * @return boolean类型
     */
    private void register(String className, ClassLoader classLoader) throws Exception {
        Class<?> clazz = null;
        try {
            clazz = classLoader.loadClass(className);
            if (clazz.isAnnotationPresent(Component.class)) {
                //说明这个Bean需要交给我们spring容器来管理呗  在这里我们就是来整合Bean信息
                String beanName = clazz.getAnnotation(Component.class).value();
                BeanDefinition bd = new BeanDefinition();
                bd.setBeanClassName(className);
                bd.setBeanClass(clazz);

                //在这里判断bean的作用域 判断单例还是原型  单例懒加载形式先不整
                //循环依赖只有在单例的基础上才可以进行循环依赖  原型循环依赖是报错的
                if (clazz.isAnnotationPresent(Scope.class)) {
                    String value = clazz.getAnnotation(Scope.class).value();
                    if (value.equals("singleton")) {
                        bd.setSingleton(true);
                        /**
                         * 只有单例的bean会通过三级缓存提前暴露来解决循环依赖的问题，而非单例的bean，每次从容器中获取都是一个新的对象，单例依赖原型Bean 会有点麻烦 所以在这里就是不行考虑
                         *      1. 必须是getDeclaredFields()这个方法 如果是getField()将获取不到私有属性字段
                         */
                        Field[] fields = clazz.getDeclaredFields();
                        ArrayList<String> list = new ArrayList<>();
                        for (Field field : fields) {
                            //如果当前字段属性包含 @Autowired 注解  我们把属性名字来放到List当中
                            if (field.isAnnotationPresent(Autowired.class)) {
                                field.setAccessible(true);
                                String dependName = field.getName();
                                list.add(dependName);                       //但是这个依赖 private User user               User类上必须是 @Component("user")
                            }
                        }
                        if (list.size() == 0) bd.setDependsOn(null);       //这里就是必须要这么写呗 否则就会出事情

                        else bd.setDependsOn(list.toArray(new String[0]));
                    }
                    //下边是原型Bean的开始
                    else if (value.equals("prototype")) {
                        bd.setPrototype(true);
                        //这里边如果多例依赖于单例 或者多例依赖多例会报错
                        Field[] fields = clazz.getDeclaredFields();
                        for (Field field : fields) {
                            if (field.isAnnotationPresent(Autowired.class)) {
                                throw new RuntimeException("scope原型 不能存在循环依赖");
                            }
                        }
                    } else {
                        throw new RuntimeException("请判断@Scope 作用域是否正确 singleton or prototype");
                    }
                }
                //就是不包含 @Scope注解
                else {
                    bd.setSingleton(true);
                }

                //向 DefaultListableBeanFactory 当中注册Bean信息 并且map是定义在AbstractBeanFactory当中
                beanFactory.registerBeanDefinition(beanName, bd);
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public DefaultListableBeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }

    @Override
    public Object getBean(String beanName, Class clazz) throws Exception {
        return null;
    }

    @Override
    public boolean containsBean(String beanName) {
        return false;
    }

    @Override
    public boolean isSingleton(String beanName) throws Exception {
        return false;
    }

    @Override
    public boolean isPrototype(String beanName) throws Exception {
        return false;
    }
}
