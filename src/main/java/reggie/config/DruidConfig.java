package reggie.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hwbstart
 * @create 2022-08-05 11:59
 */
@Configuration
public class DruidConfig {
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druid(){
        return new DruidDataSource();
    }
    //配置Druid监控
//    配置管理后台的servlet
    @Bean
    public ServletRegistrationBean servletRegistration(){
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String,String> initParams = new HashMap<>();
        initParams.put("resetEnable", "true");
        initParams.put("loginUsername","admin");
        initParams.put("loginPassword","admin");
        initParams.put("deny", "192.168.200.125");
        bean.setInitParameters(initParams);
        return bean;

    }
    //    配置filter
    @Bean
    public FilterRegistrationBean webStatFillter(){
        FilterRegistrationBean bean = new FilterRegistrationBean();
        Map<String,String> init = new HashMap<>();
        bean.setFilter( new WebStatFilter());
        init.put("exclusions", "*.js,*.css,/druid/*");
        //bean.setUrlPatterns(Arrays.asList("/*"));
        bean.setInitParameters(init);
        return bean;
    }
}
