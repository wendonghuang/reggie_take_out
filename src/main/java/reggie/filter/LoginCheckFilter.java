package reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.util.AntPathMatcher;
import reggie.common.BaseContext;
import reggie.common.R;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author hwbstart
 * @create 2022-07-28 13:50
 */
@Slf4j
@WebFilter(urlPatterns = "/*")
public class LoginCheckFilter implements Filter{
    //路径匹配器
    public static AntPathMatcher pathMatcher = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1、获取url
        String requestURI = request.getRequestURI();

        //2、放行静态资源
        String [] urls = new String[]{
                "/employee/login",
                "/employee/loginout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        boolean check = check(urls, requestURI);
        if (check == true){
            log.info("请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //3、判断是否已登录
        if ( request.getSession().getAttribute("employee") != null){
            log.info("用户已登录,用户ID为{}",request.getSession().getAttribute("employee"));
            BaseContext.setThreadLocal( (Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        //判断手机端用户是否已登录
        if (request.getSession().getAttribute("user") != null) {
                log.info("用户已登录，id为：{}",request.getSession().getAttribute("user"));
            BaseContext.setThreadLocal( (Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }
        //4 未登录返回信息,通过输出流向客户端相应信息
        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

    }

    /**
     *
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {

            boolean match = pathMatcher.match(url, requestURI);
            if(match == true){
                return true;
            }

        }
        return false;
    }

}
