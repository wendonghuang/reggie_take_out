package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.entity.User;
import reggie.service.UserService;
import reggie.utils.SMSUtils;
import reggie.utils.ValidateCodeUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author hwbstart
 * @create 2022-08-02 8:48
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //添加redis对象
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 获取验证码
     *
     * @param user

     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user) {
        //获取手机号
        String phone = user.getPhone();
        //判断手机号是否为空
        if (StringUtils.isNotEmpty(phone)) {
            String s = ValidateCodeUtils.generateValidateCode(4).toString();
            //输出验证码
            log.info("验证码：{}", s);
            //调用API发送信息
            //SMSUtils.sendMessage("阿里云", "", phone, s);
            //将验证码存入session中
            //session.setAttribute(phone, s);
            //将生成的验证码存入redis中
            redisTemplate.opsForValue().set(phone,s,1, TimeUnit.MINUTES);
            return R.success("验证码发送成功！");
        }
        return R.error("发送失败!手机号为空！");
    }

    /**
     * 移动端用户登录
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        //Object codeInSession = session.getAttribute(phone);
        //从redis中获取验证码
        String o = (String) redisTemplate.opsForValue().get(phone);
        //进行验证码的比对（页面提交的验证码和redis中保存的验证码比对）
        if (o != null && o.equals(code)) {
            //如果能够比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);
            if (user == null) {
                //判断当前手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            return R.success(user);
        }


        return R.error("验证码错误！");
    }

}
