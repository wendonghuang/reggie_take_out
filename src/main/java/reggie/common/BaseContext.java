package reggie.common;

/**
 * @author hwbstart
 * @create 2022-07-29 8:58
 */
public class BaseContext {
    private  static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    public static void setThreadLocal(Long id) {
        threadLocal.set(id);
    }

    public static Long getThreadLocal() {
        return threadLocal.get();
    }
}
