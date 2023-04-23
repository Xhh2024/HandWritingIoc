package xhh.club;

import org.junit.jupiter.api.Test;
import xhh.club.bean.AnnotationXhhApplicationContext;
import xhh.club.bean.XhhApplicationContext;
import xhh.club.service.UserService;

/**
 * 测试手写IOC容器
 * @author 谢环环
 * @date 2023/04/23
 */
public class TestHandWritingIoc {

    @Test
    void testHandWritingIoc(){
        XhhApplicationContext applicationContext = new AnnotationXhhApplicationContext("xhh.club");
        UserService userService = (UserService)applicationContext.getBean(UserService.class);
        userService.run();
    }
}
