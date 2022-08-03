package reggie.service.serviceImp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reggie.common.CustomerException;
import reggie.dto.SetmealDto;
import reggie.entity.DishFlavor;
import reggie.entity.Setmeal;
import reggie.entity.SetmealDish;
import reggie.mapper.SetmealMapper;
import reggie.service.SetmealDishService;
import reggie.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hwbstart
 * @create 2022-07-29 11:33
 */
@Service
public class SetmealServiceImp extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private SetmealService setmealService;

    @Override
    @Transactional
    public void add(SetmealDto setmealDto) {
        //保存套餐基本信息到套餐表
        this.save(setmealDto);
        //获取菜品id
        Long id = setmealDto.getId();
        //获取菜品口味集合
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //利用stream流将菜品id赋给每一个口味并保存到setmeal_dish表
        List<SetmealDish> collect = setmealDishes.stream().map(item -> {
            item.setSetmealId(id);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(collect);



    }


    @Override
    public SetmealDto get(Long id) {
        //查询套餐基本信息
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        //将查询的基本信息拷贝到dto中
        BeanUtils.copyProperties(setmeal, setmealDto);
        //创建条件构造器查询套餐菜品信息
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(id != null, SetmealDish::getSetmealId, id);
        List<SetmealDish> list = setmealDishService.list(setmealDishLambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);


        return setmealDto;
    }

    @Transactional
    @Override
    public void updateFlavor(SetmealDto setmealDto) {
        //保存菜品基本信息
        this.updateById(setmealDto);
        //修改setmeal_dish表中的信息
        //先清理再添加
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(setmealDishLambdaQueryWrapper);
        List<SetmealDish> collect = setmealDishes.stream().map(item -> {
            SetmealDish setmealDish = new SetmealDish();
            BeanUtils.copyProperties(item, setmealDish);
            setmealDish.setSetmealId(setmealDto.getId());
            return setmealDish;

        }).collect(Collectors.toList());
        setmealDishService.saveBatch(collect);

    }

    @Override
    @Transactional
    public void deleteWithDish(List<Long> ids) {
        //筛选可以删除的套餐id集合，必须停售才能删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId,ids).eq(Setmeal::getStatus,0);
        if (setmealService.count(setmealLambdaQueryWrapper) < ids.size() ) {
            throw new CustomerException("要删除的套餐含有正在出售的套餐，不能删除");
        }
        setmealService.removeByIds(ids);
        //删除套餐菜品关联表的记录
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(ids != null,SetmealDish::getSetmealId,ids);
        setmealDishService.remove(setmealDishLambdaQueryWrapper);

    }
}
