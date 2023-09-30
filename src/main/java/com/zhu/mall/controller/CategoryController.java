package com.zhu.mall.controller;

import com.github.pagehelper.PageInfo;
import com.zhu.mall.common.ApiRestResponse;
import com.zhu.mall.common.Constant;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.model.pojo.User;
import com.zhu.mall.model.request.AddCategoryReq;
import com.zhu.mall.model.vo.CategoryVO;
import com.zhu.mall.service.CategoryService;
import com.zhu.mall.service.UserService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;


@Controller
public class CategoryController {
    @Autowired
    UserService userService;

    @Autowired
    CategoryService categoryService;

    @ApiOperation("后台添加目录")
    @PostMapping("/admin/category/add")
    @ResponseBody
    public ApiRestResponse addCategory(HttpSession session,@Valid @RequestBody AddCategoryReq addCategoryReq){

        User currentUser = (User) session.getAttribute(Constant.MALL_USER);
        if (currentUser == null) {
            return ApiRestResponse.error(MallExceptionEnum.NEED_LOGIN);
        }
        if (userService.checkAdminRole(currentUser)) {
            categoryService.add(addCategoryReq);
            return ApiRestResponse.success();

        } else {
            return ApiRestResponse.error(MallExceptionEnum.NEED_ADMIN);
        }

    }

    @ApiOperation("后台删除目录")
    @PostMapping("/admin/category/delete")
    @ResponseBody
    public ApiRestResponse deleteCategory(@RequestParam Integer id){
        categoryService.delete(id);
        return ApiRestResponse.success();
    }

    @ApiOperation("后台目录列表")
    @PostMapping("/admin/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForAdmin(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        PageInfo pageInfo = categoryService.listForAdmin(pageNum, pageSize);
        return ApiRestResponse.success(pageInfo);
    }

    @ApiOperation("用户目录列表")
    @PostMapping("/category/list")
    @ResponseBody
    public ApiRestResponse listCategoryForCustomer(){
        List<CategoryVO> categoryVOS = categoryService.listCategoryForCustomer(0);
        return ApiRestResponse.success(categoryVOS);
    }
}
