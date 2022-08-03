package reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggie.entity.User;

/**
 * @author hwbstart
 * @create 2022-08-02 8:45
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
