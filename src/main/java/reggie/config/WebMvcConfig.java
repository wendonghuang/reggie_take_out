package reggie.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.ClassPath;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import reggie.common.JacksonObjectMapper;

import java.util.List;

/**
 * @author hwbstart
 * @create 2022-07-28 9:33
 */
@Slf4j
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport{
    /*
    * 设置静态资源映射
    * */

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        log.info("静态资源映射");
        registry.addResourceHandler("/backend/**").addResourceLocations("ClassPath:/backend/");
        registry.addResourceHandler("/front/**").addResourceLocations("ClassPath:/front/");

    }

    /**
     * 扩展mvc框架消息转换器
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器");
        //创建消息转换器对象
        MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter();
        //设置对象转换器，底层使用Jackson来转换JSON对象
        mappingJackson2HttpMessageConverter.setObjectMapper(new JacksonObjectMapper());
        //将对象转换器添加到转换器集合中，并放在第一位
        converters.add(0,mappingJackson2HttpMessageConverter);
    }
}
