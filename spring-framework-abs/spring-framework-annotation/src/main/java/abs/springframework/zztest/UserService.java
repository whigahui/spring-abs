package abs.springframework.zztest;

import abs.springframework.beans.annotation.Autowired;
import abs.springframework.beans.annotation.Component;
import abs.springframework.beans.annotation.Scope;

/**
 * @author whig
 * @date 2021/4/29 22:03
 * @desc
 */

@Component("userService")
@Scope("singleton")
public class UserService {

    @Autowired
    private User user;          //注意属性名 一定要本单例 bean注入的一致

    public void dependTest() {
        System.out.println(user);
    }
}
