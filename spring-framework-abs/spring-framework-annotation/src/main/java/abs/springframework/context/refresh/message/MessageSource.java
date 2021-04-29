package abs.springframework.context.refresh.message;

import java.util.Locale;

/**
 * @author whig
 * @date 2021/4/26 21:57
 * @desc
 */
public interface MessageSource {

    String getMessage(String code,  Object[] args, String defaultMessage, Locale locale);
}
