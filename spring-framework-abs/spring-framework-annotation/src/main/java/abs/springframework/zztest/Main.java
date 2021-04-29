package abs.springframework.zztest;

import abs.springframework.context.AnnotationConfigApplicationContext;

/**
 * @author whig
 * @date 2021/4/28 10:48
 * @desc
 */
public class Main {

    public static void main(String[] args) throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

        Object user = context.getBean("user");

        UserService userService =(UserService) context.getBean("userService");

        System.out.println(user);

        System.out.println(userService);

    }
}
