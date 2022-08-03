package reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggie.entity.Dish;

/**
 * @author hwbstart
 * @create 2022-07-29 11:17
 */
@Mapper
//菜品mapper
public interface DishMapper  extends BaseMapper<Dish>{
}
