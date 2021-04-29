package abs.springframework.context;

import abs.springframework.beans.definition.BeanDefinition;
import abs.springframework.beans.factory.DefaultListableBeanFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author whig
 * @date 2021/4/26 22:04
 * @desc
 */
public abstract class AbstractApplicationContext implements ApplicationContext {

    protected ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();  //bean最重要的信息 在这里哈呢


    private final Object startupShutdownMonitor = new Object();  //其中refresh()步骤会用到它
    private final AtomicBoolean active = new AtomicBoolean();
    private final AtomicBoolean closed = new AtomicBoolean();


    public void refresh() throws Exception, IllegalStateException {
        synchronized (this.startupShutdownMonitor) {

            // Prepare this context for refreshing.
            // 1. 刷新前的预处理
            prepareRefresh();

            //这个就是非常重要 我们让子类GenericApplicationContext 重写这个方法 并返回DefaultListableBeanFactory
            //创建BeanFactory实例，即前面创建的【DefaultListableBeanFactory】，并且注册xml文件中相关的bean信息
            DefaultListableBeanFactory beanFactory = getBeanFactory();

            //3. 预处理 beanFactory，向容器中添加一些组件
            prepareBeanFactory(beanFactory);

            try {
                // 4. 子类通过重写这个方法可以在 BeanFactory 创建并与准备完成以后做进一步的设置
                postProcessBeanFactory(beanFactory);

//                StartupStep beanPostProcess = this.applicationStartup.start("spring.context.beans.post-process");

                // 5. 执行 BeanFactoryPostProcessor 方法，beanFactory 后置处理器
                invokeBeanFactoryPostProcessors(beanFactory);

                //6. 注册 BeanPostProcessors，bean 后置处理器 这个就是非常重要
                registerBeanPostProcessors(beanFactory);

                // Initialize message source for this context.
                // 7. 初始化 MessageSource 组件（做国际化功能；消息绑定，消息解析）
                initMessageSource();

                // Initialize event multicaster for this context.
                // 8. 初始化事件派发器，在注册监听器时会用到
                initApplicationEventMulticaster();

                // Initialize other special beans in specific context subclasses.
                // 9. 留给子容器（子类），子类重写这个方法，在容器刷新的时候可以自定义逻辑，web 场景下会使用
                onRefresh();

                // Check for listener beans and register them.
                // 10. 注册监听器，派发之前步骤产生的一些事件（可能没有）
                registerListeners();

                // Instantiate all remaining (non-lazy-init) singletons.
                //11. 对已经注册的非延迟单例 bean的实例化
                //这个步骤是最终要的  是创建Bean的入口机制呢
                finishBeanFactoryInitialization(beanFactory);

                // Last step: publish corresponding event.
                //12.清除缓存的资源信息，初始化一些声明周期相关的bean，并且发布Context已被初始化的事件
                finishRefresh();
            } catch (Exception ex) {

                //声明周期在这里销毁 我们就不做了呗
                throw ex;
            }
        }
    }

    protected void prepareRefresh() {
        // Switch to active. 让容器保持存活
        this.closed.set(false);
        this.active.set(true);
        // Initialize any placeholder property sources in the context environment.
        //后续会有web xml配置来参加  让自类去继承 重写
        //initPropertySources();
        //事件监听机制在这里我们就是不需要了
    }

    protected void prepareBeanFactory(DefaultListableBeanFactory beanFactory) {
    }

    protected void postProcessBeanFactory(DefaultListableBeanFactory beanFactory) {
    }

    protected void invokeBeanFactoryPostProcessors(DefaultListableBeanFactory beanFactory) throws Exception {
    }

    //这步骤也很关键 注册BeanPostProcessor
    protected void registerBeanPostProcessors(DefaultListableBeanFactory beanFactory) {
    }

    protected void initMessageSource() {
    }

    protected void initApplicationEventMulticaster() {
    }

    protected void onRefresh() throws Exception {
    }

    protected void registerListeners() {
    }

    //这个就是最终要 完成所有非懒加载机制的单例Bean的初始化呢  这个步骤就是非常关键
    protected void finishBeanFactoryInitialization(DefaultListableBeanFactory beanFactory) throws Exception {
        // Instantiate all remaining (non-lazy-init) singletons.
        //源码当中 前边都是一些属性信息  最后一步
        //这个方法在 DefaultListableBeanFactory中有所实现
        beanFactory.preInstantiateSingletons();
    }


    protected void finishRefresh() {
    }


    public ApplicationContext getParent() {
        return null;
    }

    protected abstract DefaultListableBeanFactory getBeanFactory();
}
