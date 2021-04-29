package abs.springframework.beans;

import java.lang.reflect.Field;

/**
 * @author whig
 * @date 2021/4/29 15:58
 * @desc
 */
public class UserService {

    private User user;

    public static void main(String[] args) {
        UserService us = new UserService();

        Field[] fields = us.getClass().getDeclaredFields();

        for(Field field:fields){
            field.setAccessible(true);
            System.out.println(field.getClass().getName());     //这种方法只能获取公共字段 私有字段获取不了呢
            System.out.println(field.getName());
        }
    }
}
