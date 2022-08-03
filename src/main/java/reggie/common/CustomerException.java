package reggie.common;

/**
 * @author hwbstart
 * @create 2022-07-29 11:48
 */

/**
 * 自定义异常
 */
public class CustomerException extends RuntimeException{
    public CustomerException(String msg){
        super(msg);
    }
}
