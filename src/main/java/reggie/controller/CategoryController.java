package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.entity.Category;
import reggie.entity.Employee;
import reggie.service.CategoryService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

/**
 * @author hwbstart
 * @create 2022-07-29 9:38
 */
@Slf4j
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 分类管理添加实现
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody Category category) {

        categoryService.save(category);
        return R.success("添加成功！");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize) {
        log.info("page:{},pageSize", page, pageSize);
        //构造分页对象
        Page<Category> pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }


    /**
     * 根据id删除分类
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam(value = "ids") Long id) {
        categoryService.delete(id);
        return R.success("删除成功！");
    }

    /**
     * 根据id修改菜品分类
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Category category) {
        categoryService.updateById(category);
        return R.success("修改成功！");
    }

    /**
     * 根据条件查询菜品分类，实现菜品管理中的添加下拉框
     *
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> list(Category category) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(category.getType() != null,Category::getType,category.getType()).orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime);
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);
        return R.success(list);


    }

}
