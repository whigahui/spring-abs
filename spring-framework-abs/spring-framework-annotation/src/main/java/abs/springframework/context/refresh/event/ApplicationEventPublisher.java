package abs.springframework.context.refresh.event;

/**
 * @author whig
 * @date 2021/4/26 21:58
 * @desc
 */
public interface ApplicationEventPublisher {

    //进行事件发布被 在这里哈呢
    void publishEvent(Object event);
}
