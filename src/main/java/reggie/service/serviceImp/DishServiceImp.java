package reggie.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reggie.common.CustomerException;
import reggie.dto.DishDto;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.mapper.DishMapper;
import reggie.service.DishFlavorService;
import reggie.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hwbstart
 * @create 2022-07-29 11:22
 */
@Service
//设计两个表操作，需要开启事务控制
@Transactional
public class DishServiceImp extends ServiceImpl<DishMapper,Dish> implements DishService {
    @Autowired
    public DishFlavorService dishFlavorService;
    @Autowired
    public DishService dishService;
    @Override

    @Transactional
    public void add(DishDto dishDto) {
        //保存菜品基本信息到dish表
        this.save(dishDto);
        //获取菜品id
        Long id = dishDto.getCategoryId();
        //获取菜品口味集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        //利用stream流将菜品id赋给每一个口味并保存到dish_flavor表
        flavors = flavors.stream().map(item -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);




    }

    @Override
    public DishDto getDishDto(Long id) {
        //从dish表查询菜品基本信息
        Dish dish = this.getById(id);
        //从dish_flavor表查询口味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(list);




        return dishDto;
    }

    @Override
    @Transactional
    public void updateFlavor(DishDto dishDto) {
       // 修改dish表中的信息
        this.updateById(dishDto);
        //修改dish_flavor表中的信息
        //先清理再添加
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //获取菜品id
        Long id = dishDto.getId();
        //获取菜品口味集合
        List<DishFlavor> flavors = dishDto.getFlavors();
        //利用stream流将菜品id赋给每一个口味并保存到dish_flavor表
        flavors = flavors.stream().map(item -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);



    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        //筛选可以删除的套餐id集合，必须停售才能删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId,ids).eq(Dish::getStatus,0);
        if (dishService.count(dishLambdaQueryWrapper) < ids.size() ) {
            throw new CustomerException("要删除的菜品含有正在出售的菜品，不能删除");
        }
        dishService.removeByIds(ids);
        //删除套餐菜品关联表的记录
        LambdaQueryWrapper<DishFlavor> dishDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishDishLambdaQueryWrapper.in(ids != null,DishFlavor::getDishId,ids);
        dishFlavorService.remove(dishDishLambdaQueryWrapper);

    }
}
