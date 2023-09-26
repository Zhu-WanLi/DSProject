package com.zhu.mall.service.impl;

import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.model.dao.ProductMapper;
import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.request.AddProductReq;
import com.zhu.mall.service.ProductService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
* 描述：       商品服务实现类
* */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;
    @Override
    public void add(AddProductReq addProductReq){

        Product productOld = productMapper.selectByName(addProductReq.getName());
        if(productOld != null){
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }

        Product product = new Product();
        //赋值bean的内容，前面为来源，后面参数为目标
        BeanUtils.copyProperties(addProductReq,product);
        int count = productMapper.insertSelective(product);
        if(count != 1){
            throw new MallException(MallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Product updateProduct){
        Product productOld = productMapper.selectByName(updateProduct.getName());

        //数据库中有同名（用更改后的name能查到结果）
        if(productOld != null && productOld.getId().equals(updateProduct.getId())){
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count != 1){
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id){
        Product productOld = productMapper.selectByPrimaryKey(id);
        //查不到记录
        if(productOld == null) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if(count != 1) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
    }
}
