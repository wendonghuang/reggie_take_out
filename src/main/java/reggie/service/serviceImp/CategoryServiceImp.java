package reggie.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reggie.common.BaseContext;
import reggie.common.CustomerException;
import reggie.common.R;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.Setmeal;
import reggie.entity.SetmealDish;
import reggie.mapper.CategoryMapper;
import reggie.service.CategoryService;
import reggie.service.DishService;
import reggie.service.SetmealDishService;
import reggie.service.SetmealService;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hwbstart
 * @create 2022-07-29 9:34
 */
@Service
public class CategoryServiceImp  extends ServiceImpl<CategoryMapper,Category> implements CategoryService {


    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 先查询分类是否关联菜品套餐
     * @param id
     */
    @Override
    public void delete(Long id) {
        //查询是否关联菜品，已关联则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count > 0){
            throw  new CustomerException("该分类关联菜品，不能删除！");
        }
        //查询是否关联套餐，已关联则抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<Setmeal>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1 > 0){
            throw  new CustomerException("该分类关联套餐，不能删除！");

        }
        //没有关联，可以删除
        super.removeById(id);
    }
}
