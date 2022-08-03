package reggie.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reggie.common.R;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.UUID;

/**
 * @author hwbstart
 * @create 2022-07-29 13:57
 */
@RestController
@RequestMapping("/common")
public class CommonController {

    @Value("${reggie.path}")
    private String basePath;
    /**
     * 上传文件
     * @param files
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(@RequestParam( value = "file") MultipartFile files) throws IOException {


        //截取文件类型
        String originalFilename = files.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));


        //使用UUID重新生成文件名，防止重名覆盖
         String fileName = UUID.randomUUID().toString() + suffix;

         //创建一个目录对象

        File dir = new File(basePath);
        if(!dir.exists()){
            dir.mkdirs();
        }


        //file是一个临时文件，需要转存文件,否则请求完成后临时文件会被删除
        files.transferTo(new File(basePath+fileName));

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws Exception {
        //通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath+name));

        //通过输出流传到response
        response.setContentType("image/jpeg");
        ServletOutputStream outputStream = response.getOutputStream();
        int len = 0;
        byte [] bytes = new byte[1024];
        while((len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes,0,len);
            //刷新
            outputStream.flush();

        }
        outputStream.close();;
        fileInputStream.close();


    }
}
