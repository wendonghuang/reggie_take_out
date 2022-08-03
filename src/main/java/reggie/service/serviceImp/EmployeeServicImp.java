package reggie.service.serviceImp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import reggie.entity.Employee;
import reggie.mapper.EmployeeMapper;
import reggie.service.EmployeeService;

/**
 * @author hwbstart
 * @create 2022-07-28 10:06
 */
@Service
public class EmployeeServicImp  extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
}
