package reggie.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author hwbstart
 * @create 2022-07-28 15:35
 */
@Slf4j
@RestControllerAdvice(annotations = {RestController.class, Controller.class})
public class GloblaExceptionHandle {
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> handelException(SQLIntegrityConstraintViolationException ex) {
        log.error(ex.getMessage());

        if (ex.getMessage().contains("Duplicate entry")) {
            String [] spilt = ex.getMessage().split(" ",5);
            return R.error(spilt[2]+"已存在");
        }

        return R.error("未知错误");
    }


    @ExceptionHandler(CustomerException.class)
    public R<String> handelException(CustomerException ex) {
        log.error(ex.getMessage());
        return R.error(ex.getMessage());
    }
}
