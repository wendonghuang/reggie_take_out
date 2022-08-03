package reggie.controller;

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
import reggie.dto.SetmealDto;
import reggie.entity.Category;
import reggie.entity.Dish;
import reggie.entity.Setmeal;
import reggie.service.CategoryService;
import reggie.service.SetmealDishService;
import reggie.service.SetmealService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author hwbstart
 * @create 2022-08-01 13:38
 */
@Slf4j
@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SetmealService setmealService;

    /**
     * 实现套餐管理中的新增套餐功能
     * @param setmealDto
     * @return
     */
    @PostMapping
    @Transactional
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.add(setmealDto);
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
        Page<Setmeal> pageInfo = new Page(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Setmeal::getUpdateTime).like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        setmealService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, setmealDtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        final List<SetmealDto> collect = records.stream().map(item -> {
            final SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);
            if (categoryService.getById(item.getCategoryId()) != null) {
                final Category byId = categoryService.getById(item.getCategoryId());

                setmealDto.setCategoryName(byId.getName());
            }
            return setmealDto;
        }).collect(Collectors.toList());
        setmealDtoPage.setRecords(collect);
        return R.success(setmealDtoPage);
    }

    /**
     * 根据套餐ID查询所有信息
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<SetmealDto> getById(@PathVariable Long id) {
       SetmealDto setmealDto = setmealService.get(id);

        return R.success(setmealDto);
    }

    /**
     * 实现套餐管理的修改功能
     *
     * @param setmealDto
     * @return
     * */

    @PutMapping
    @Transactional
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateFlavor(setmealDto);
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
        List<Setmeal> setmeales = Arrays.stream(ids).map(item -> {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(item);
            setmeal.setStatus(status);
            return setmeal;

        }).collect(Collectors.toList());
        if (setmealService.updateBatchById(setmeales)) {

            return R.success("修改状态成功！");
        } else {
            return R.error("修改失败");
        }

    }

    /**
     * 根据id删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @Transactional
    public R<String> delete(@RequestParam  List<Long> ids) {

       setmealService.deleteWithDish(ids);
        return R.success("删除成功！");
    }
    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        List<Setmeal> list = setmealService.list(queryWrapper);

        return R.success(list);
    }
}
