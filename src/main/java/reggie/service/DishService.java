package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.dto.DishDto;
import reggie.entity.Dish;

import java.util.List;

/**
 * @author hwbstart
 * @create 2022-07-29 11:20
 */
public interface DishService extends IService<Dish> {
    void add(DishDto dishDto);

    DishDto getDishDto(Long id);

    void updateFlavor(DishDto dishDto);

    void deleteWithDish(List<Long> ids);
}
