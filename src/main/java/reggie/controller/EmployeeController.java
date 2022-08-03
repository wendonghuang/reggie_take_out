package reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import reggie.common.BaseContext;
import reggie.common.R;
import reggie.entity.Employee;
import reggie.service.EmployeeService;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @author hwbstart
 * @create 2022-07-28 10:13
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /*
    *
    *登录功能

     *  */

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        // 1、将页面提交的密码passmord进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

       // 2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<Employee>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp  = employeeService.getOne(lambdaQueryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("用户名不存在");

        }
      //4、密码比对，如果不一致则返回登录失败结果
        if (!password.equals(emp.getPassword())) {
            return R.error("密码错误");
        }

       // 5、查看员工状态，如果为已禁用状态，则返回员工己禁用结果
        if (emp.getStatus() == 0){
            return R.error("该账号已禁用");
        }
        //6.登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

   /*
   * 退出功能实现
   * */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    /**
     * 用户添加功能实现
     * @param request
     * @param emp
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee emp){
        emp.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
       // emp.setCreateTime(LocalDateTime.now());
        //emp.setUpdateTime(LocalDateTime.now());
        //将id存入BaseContext工具类中
        //Long empId = (Long) request.getSession().getAttribute("employee");
       // BaseContext.setThreadLocal(empId);
        //emp.setCreateUser(empId);
        //emp.setUpdateUser(empId);
        employeeService.save(emp);
        log.info("用户添加成功");
        return R.success("用户添加成功");

    }


    /**
     * 分页查询及搜索
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){

        //构造分页对象
        Page<Employee> pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        employeeService.page(pageInfo,queryWrapper);
        return R.success(pageInfo);
    }


    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee emp){
        log.info(emp.toString());
        //将id存入BaseContext工具类中
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //BaseContext.setThreadLocal(empId);
        //emp.setUpdateUser(empId);
        //emp.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(emp);
        return R.success("用户修改成功！");
    }

    /**
     * 根据用户ID查询所有信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee byId = employeeService.getById(id);
        return R.success(byId);
    }
}
