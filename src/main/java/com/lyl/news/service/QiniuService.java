package com.lyl.news.service;

import com.alibaba.fastjson.JSONObject;
import com.lyl.news.util.NewsUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 *  基本是照着七牛云的官方文档来看的
 *  之所以要单独拧出来一个Service，主要是也方便以后换别的云服务
 */
@Service
public class QiniuService {

    //这个就有点非对称加密的意思
    String ACCESS_KEY = "MRM_TMavgUigQiULf22g6TBq2CEDCVQZGqKE8ygM";
    String SECRET_KEY = "CyPYWJN0qIxmqOMp822mR9EIPsOvnF8j_wjeVcUI";
    //要上传的空间名
    String bucketname = "lyl_img";

    //密钥配置
    Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
    //创建上传对象
    UploadManager uploadManager = new UploadManager();

    private static String QINIU_IMAGE_DOMAIN ="http://pry9gt85w.bkt.clouddn.com/";

    //简单上传，采用默认策略，只需要设置上传的空间名
    public String getUpToken(){
        return auth.uploadToken(bucketname);
    }

    /**
     *
     * @param file 本地希望上传到服务器的文件名
     * @return  上传成功返回的是上传后文件的url，上传失败返回null
     */
    public String saveImage(MultipartFile file) throws IOException {
        //检查一下图片的后缀名是否有问题
        try {
            int dotPos = file.getOriginalFilename().lastIndexOf(".");
            if (dotPos < 0) {
                return null;
            }
            //获得后缀名，判断后缀名是否符合图片的标准
            String fileExt = file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
            if (!NewsUtil.isFileAllowed(fileExt)) {
                return null;
            }

            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + "." + fileExt;
            //调用put方法上传
            //可以打印这个信息来看看，这是一个hash+key组成的，hash应该是解密的，key是保存的文件名
            //这也能下面的返回值的含义了
            Response res = uploadManager.put(file.getBytes(), fileName, getUpToken());
            System.out.println(res);
            System.out.println(res);

            //打印返回的信息
            if (res.isOK() && res.isJson()) {
                return QINIU_IMAGE_DOMAIN + JSONObject.parseObject(res.bodyString()).get("key");
            } else {
                //   logger.error("七牛异常:" + res.bodyString());
                return null;
            }
        } catch (QiniuException e) {
            // 请求失败时打印的异常的信息
            return null;
        }
    }
}
