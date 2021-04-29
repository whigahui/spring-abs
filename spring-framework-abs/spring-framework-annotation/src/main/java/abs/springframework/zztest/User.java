package abs.springframework.zztest;

import abs.springframework.beans.annotation.Autowired;
import abs.springframework.beans.annotation.Component;
import abs.springframework.beans.annotation.Scope;

/**
 * @author whig
 * @date 2021/4/28 10:45
 * @desc
 */
@Component("user")
@Scope("singleton")
public class User {
    private int age;
    private String name;


}
