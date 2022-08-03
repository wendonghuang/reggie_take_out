package reggie.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.mapper.DishFlavorMapper;
import reggie.mapper.DishMapper;
import reggie.service.DishFlavorService;
import reggie.service.DishService;

/**
 * @author hwbstart
 * @create 2022-07-29 16:02
 *
 */
@Service
public class DishFlavorServiceImp extends ServiceImpl<DishFlavorMapper,DishFlavor> implements DishFlavorService {
}
