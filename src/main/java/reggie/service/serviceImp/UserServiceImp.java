package reggie.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggie.entity.User;
import reggie.mapper.UserMapper;
import reggie.service.UserService;

/**
 * @author hwbstart
 * @create 2022-08-02 8:46
 */
@Service
public class UserServiceImp extends ServiceImpl<UserMapper,User> implements UserService{
}
