package abs.springframework.beans.registry;

/**
 * @author whig
 * @date 2021/4/29 15:05
 * @desc
 */
@FunctionalInterface
public interface ObjectFactory<T> {

    /**
     * Return an instance (possibly shared or independent)of the object managed by this factory.
     * @return the resulting instance
     */
    T getObject() throws Exception;
}
