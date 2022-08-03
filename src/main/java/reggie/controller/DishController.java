package reggie.controller;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reggie.common.R;
import reggie.dto.DishDto;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.DishFlavor;
import reggie.entity.Employee;
import reggie.service.CategoryService;
import reggie.service.DishFlavorService;
import reggie.service.DishService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hwbstart
 * @create 2022-07-29 15:59
 */
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    DishFlavorService dishFlavorService;
    @Autowired
    CategoryService categoryService;

    /**
     * 实现菜品管理的添加功能
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> add(@RequestBody DishDto dishDto) {
        dishService.add(dishDto);
        return R.success("添加成功！");
    }


    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page:{},pageSize", page, pageSize);
        //构造分页对象
        Page<Dish> pageInfo = new Page(page, pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Dish::getSort).like(StringUtils.isNotEmpty(name), Dish::getName, name);
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dishDtoPage, "records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map(item -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item, dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dishDto.setCategoryName(category.getName());
            }

            return dishDto;
        }).collect(Collectors.toList());
        dishDtoPage.setRecords(list);

        return R.success(dishDtoPage);
    }

    /**
     * 根据菜品ID查询所有口味信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getById(@PathVariable Long id) {
        DishDto dishDto = dishService.getDishDto(id);

        return R.success(dishDto);
    }

    /**
     * 实现菜品管理的修改功能
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto) {
        dishService.updateFlavor(dishDto);
        return R.success("修改成功！");
    }


    /**
     * 根据id修改起售和停售
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> upStatus(@PathVariable Integer status, Long[] ids) {
        List<Dish> dishes = Arrays.stream(ids).map(item -> {
            Dish dish = new Dish();
            dish.setId(item);
            dish.setStatus(status);
            return dish;

        }).collect(Collectors.toList());
        if (dishService.updateBatchById(dishes)) {

            return R.success("修改状态成功！");
        } else {
            return R.error("修改失败");
        }

    }

    /**
     * 根据id删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    @Transactional
    public R<String> delete(@RequestParam  List<Long> ids) {

        dishService.deleteWithDish(ids);
        return R.success("删除成功！");
    }


    /**
     * 根据菜品分类id查询相应菜品
     * @param dish
     * @return
     */
   /* @GetMapping("/list")
    public R<List<Dish>> list(Dish dish) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(dish != null,Dish::getCategoryId,dish.getCategoryId());

        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        return R.success(list);
    }
*/
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加条件，查询状态为1（起售状态）的菜品
        queryWrapper.eq(Dish::getStatus,1);

        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        List<DishDto> dishDtoList = list.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);

            if(category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品的id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            //SQL:select * from dish_flavor where dish_id = ?
            List<DishFlavor> dishFlavorList = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);
    }
}
