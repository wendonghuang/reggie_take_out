package reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import reggie.entity.Category;

/**
 * @author hwbstart
 * @create 2022-07-29 9:34
 */

public interface CategoryService extends IService<Category> {

    void delete(Long id);

}
