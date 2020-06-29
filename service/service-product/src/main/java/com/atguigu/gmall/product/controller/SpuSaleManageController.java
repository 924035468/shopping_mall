package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.product.service.SpuSaleManageService;
import io.swagger.annotations.Api;
import org.apache.commons.io.FilenameUtils;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


@Api(description = "spu商品属性管理")

@RestController
@RequestMapping("admin/product/")
@CrossOrigin
public class SpuSaleManageController {
    @Autowired
    private SpuSaleManageService spuSaleManageService;

    @RequestMapping("saveSpuInfo")
    public Result saveSpuSaleInfo(
            @RequestBody SpuInfo spuInfo){

        spuSaleManageService.saveSpuSaleInfo(spuInfo);


        return Result.ok();
    }






        @Value("${fileServer.url}")
        private String fileUrl;

        @RequestMapping("fileUpload")
        public Result<String> fileUpload(MultipartFile file) throws Exception{
            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            String path = null;

            if (configFile!=null){
// 初始化
                ClientGlobal.init(configFile);
// 创建trackerClient
                TrackerClient trackerClient = new TrackerClient();
// 获取trackerService
                TrackerServer trackerServer = trackerClient.getConnection();
// 创建storageClient1
                StorageClient1 storageClient1 = new StorageClient1(trackerServer, null);
                path = storageClient1.upload_appender_file1(file.getBytes(), FilenameUtils.getExtension(file.getOriginalFilename()), null);
                System.out.println(fileUrl + path);
            }
            return Result.ok(fileUrl+path);
        }





}
