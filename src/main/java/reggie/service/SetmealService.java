package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggie.dto.SetmealDto;
import reggie.entity.Setmeal;

import java.util.List;

/**
 * @author hwbstart
 * @create 2022-07-29 11:33
 */
public interface SetmealService extends IService<Setmeal> {
    void add(SetmealDto setmealDto);

    SetmealDto get(Long id);

    void updateFlavor(SetmealDto setmealDto);

    void deleteWithDish(List<Long> ids);
}
