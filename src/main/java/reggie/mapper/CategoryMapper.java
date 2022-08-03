package reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import reggie.entity.Category;

/**
 * @author hwbstart
 * @create 2022-07-29 9:33
 */

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
