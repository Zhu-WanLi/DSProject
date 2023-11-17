package com.zhu.mall.controller;

import com.github.pagehelper.PageInfo;
import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.common.Constant;
import com.zhu.mall.common.ValidList;
import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.request.AddProductReq;
import com.zhu.mall.model.request.UpdateProductReq;
import com.zhu.mall.service.ProductService;
import io.swagger.annotations.ApiOperation;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 描述：       后台商品管理Controller
 */
@RestController
@Validated
public class ProductAdminController {
    @Autowired
    ProductService productService;
    @Value("${file.upload.uri}")
    String uri;

    @PostMapping("admin/product/add")
    public ApiRestResponse addProduct(@Valid @RequestBody AddProductReq addProductReq) {
        productService.add(addProductReq);
        return ApiRestResponse.success();
    }

    @PostMapping("admin/upload/file")
    public ApiRestResponse upload(HttpServletRequest httpServletRequest, @RequestParam("file") MultipartFile file) {
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
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = uri;
        return ApiRestResponse.success("http://" + address + "/images/" + newFileName);

    }

    private URI getHost(URI uri) {
        URI effectiveURI;
        try {
            effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(),
                    null, null, null);
        } catch (URISyntaxException e) {
            effectiveURI = null;
        }
        return effectiveURI;
    }

    @ApiOperation("后台更新商品")
    @PostMapping("admin/product/update")
    public ApiRestResponse updateProduct(@Valid @RequestBody UpdateProductReq updateProductReq) {
        Product product = new Product();
        BeanUtils.copyProperties(updateProductReq, product);
        productService.update(product);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台删除商品")
    @PostMapping("admin/product/delete")
    public ApiRestResponse deleteProduct(@RequestParam Integer id) {
        productService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量上下架商品")
    @PostMapping("admin/product/batchUpdateSellStatus")
    public ApiRestResponse batchUpdateSellStatus(@RequestParam Integer[] ids,
                                                 @RequestParam Integer sellStatus) {
        productService.batchUpdateSellStatus(ids, sellStatus);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台商品列表接口")
    @PostMapping("admin/product/list")
    public ApiRestResponse list(@RequestParam Integer pageNum,
                                @RequestParam Integer pageSize) {
        PageInfo pageInfo = productService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("后台批量上传架商品")
    @PostMapping("admin/upload/product")
    public ApiRestResponse uploadProduct(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        //拿到文件名
        String fileName = multipartFile.getOriginalFilename();
        //拿到后缀
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        UUID uuid = UUID.randomUUID();
        String newFileName = uuid + suffixName;
        //创建文件夹
        File fileDirectory = new File(Constant.FILE_UPLOAD_DIR);
        //创建文件
        File destFile = new File(Constant.FILE_UPLOAD_DIR + newFileName);

        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {  //fileDirectory.mkdir() 创建这个文件夹，成功返回true
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            multipartFile.transferTo(destFile); //把multipartFile 传到 destFile
        } catch (IOException e) {
            e.printStackTrace();
        }
        productService.addProductByExcel(destFile);

        return ApiRestResponse.success();
    }

    @PostMapping("admin/upload/image")
    public ApiRestResponse uploadImage(HttpServletRequest httpServletRequest,
                                       @RequestParam("file") MultipartFile file)
            throws IOException {
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
        creatFile(file, fileDirectory, destFile);
        Thumbnails.of(destFile).size(Constant.IMAGE_SIZE, Constant.IMAGE_SIZE).watermark(Positions.BOTTOM_RIGHT,
                ImageIO.read(new File(Constant.FILE_UPLOAD_DIR + Constant.WATER_MARK_JPG)),
                Constant.IMAGE_OPACITY).toFile(Constant.FILE_UPLOAD_DIR + newFileName);
        String address = uri;
        return ApiRestResponse.success("http://" + address +
                "/images/" + newFileName);

    }

    private static void creatFile(MultipartFile file, File fileDirectory, File destFile) {
        if (!fileDirectory.exists()) {
            if (!fileDirectory.mkdir()) {
                throw new MallException(MallExceptionEnum.MKDIR_FAILED);
            }
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation("后台批量更新商品")
    @PostMapping("admin/product/batchUpdate")
    //Valid只适用于Bean,ValidList视为自己定义的bean,List在官方不算bean
    public ApiRestResponse batchUpdateProduct
            (@Valid @RequestBody ValidList<UpdateProductReq> updateProductReqList) {
        for (int i = 0; i < updateProductReqList.size(); i++) {
            UpdateProductReq updateProductReq = updateProductReqList.get(i);
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
    }

    @ApiOperation("后台批量更新商品,使用@Validated")
    @PostMapping("admin/product/batchUpdate2")   //在类上加@Validated
    public ApiRestResponse batchUpdateProduct(@Valid @RequestBody List<UpdateProductReq> updateProductReqList) {
        for (int i = 0; i < updateProductReqList.size(); i++) {
            UpdateProductReq updateProductReq = updateProductReqList.get(i);
            Product product = new Product();
            BeanUtils.copyProperties(updateProductReq, product);
            productService.update(product);
        }
        return ApiRestResponse.success();
    }

}
