package com.zhu.mall.controller;

import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.common.Constant;
import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.request.AddProductReq;
import com.zhu.mall.model.request.UpdateProductReq;
import com.zhu.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

/*
* 描述：       后台商品管理Controller
* */
@RestController
public class ProductAdminController {
    @Autowired
    ProductService productService;
    @PostMapping("admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq){
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @PostMapping("admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest,@RequestParam("file") MultipartFile file){
        //获取原始名
        String fileName = file.getOriginalFilename();

        //截取后缀，最后一个点的位置到最后
        String suffixName = fileName.substring(fileName.lastIndexOf("."));

        //生成文件名称UUID
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid.toString() + suffixName;

        //创建文件
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR); //生成文件夹
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);
        if(!fileDirectory.exists()){
            if(!fileDirectory.mkdir()){
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            return ApiRestResponse.success(getHost(new URI(httpServletRequest.getRequestURL() + "")) +
                    "/images/"+newFileName);
        } catch (URISyntaxException e) {
            return ApiRestResponse.error(MallExceptionEnum.UPLOAD_FAILED);
        }
    }

    private URI getHost(URI uri){
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(),uri.getUserInfo(),uri.getHost(),uri.getPort(),
                    null,null,null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @ApiOperation("后台更新商品")
    @PostMapping("admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq){
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq,product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id){
        productService.delete(id);
        return ApiRestResponse.success();
    }


}
