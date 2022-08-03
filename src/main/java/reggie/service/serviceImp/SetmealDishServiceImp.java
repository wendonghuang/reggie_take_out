package reggie.service.serviceImp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggie.entity.SetmealDish;
import reggie.mapper.SetmealDishMapper;
import reggie.service.SetmealDishService;

/**
 * @author hwbstart
 * @create 2022-07-29 11:21
 */
@Service
public class SetmealDishServiceImp extends ServiceImpl<SetmealDishMapper,SetmealDish> implements SetmealDishService {
}
