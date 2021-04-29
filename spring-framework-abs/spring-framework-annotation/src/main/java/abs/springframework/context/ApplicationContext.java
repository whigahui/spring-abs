package abs.springframework.context;

import abs.springframework.beans.factory.BeanFactory;

/**
 * @author whig
 * @date 2021/4/26 21:51
 * @desc 因为ApplicationContext是高级容器接口 其中refresh()方法中就是具体体现了
 */
public interface ApplicationContext extends BeanFactory {

    //子类实现这个方法 获取父类Context
    public ApplicationContext getParent();

}
