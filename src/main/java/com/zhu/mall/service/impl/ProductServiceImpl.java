package com.zhu.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zhu.mall.common.Constant;
import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.model.dao.ProductMapper;
import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.query.ProductListQuery;
import com.zhu.mall.model.request.AddProductReq;
import com.zhu.mall.model.request.ProductListReq;
import com.zhu.mall.model.vo.CategoryVO;
import com.zhu.mall.service.CategoryService;
import com.zhu.mall.service.ProductService;
import com.zhu.mall.utils.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 描述：       商品服务实现类
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    ProductMapper productMapper;

    @Autowired
    CategoryService categoryService;

    @Override
    public void add(AddProductReq addProductReq) {

        Product productOld = productMapper.selectByName(addProductReq.getName());
        if (productOld != null) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }

        Product product = new Product();
        //赋值bean的内容，前面为来源，后面参数为目标
        BeanUtils.copyProperties(addProductReq, product);
        int count = productMapper.insertSelective(product);
        if (count != 1) {
            throw new MallException(MallExceptionEnum.CREATE_FAILED);
        }
    }

    @Override
    public void update(Product updateProduct) {
        Product productOld = productMapper.selectByName(updateProduct.getName());

        //数据库中有同名（用更改后的name能查到结果）
        if (productOld != null && productOld.getId().equals(updateProduct.getId())) {
            throw new MallException(MallExceptionEnum.NAME_EXISTED);
        }
        int count = productMapper.updateByPrimaryKeySelective(updateProduct);
        if (count != 1) {
            throw new MallException(MallExceptionEnum.UPDATE_FAILED);
        }
    }

    @Override
    public void delete(Integer id) {
        Product productOld = productMapper.selectByPrimaryKey(id);
        //查不到记录
        if (productOld == null) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
        int count = productMapper.deleteByPrimaryKey(id);
        if (count != 1) {
            throw new MallException(MallExceptionEnum.DELETE_FAILED);
        }
    }

    @Override
    public void batchUpdateSellStatus(Integer[] ids, Integer sellStatus) {
        int count = productMapper.batchUpdateSellStatus(ids, sellStatus);
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        //开启分页，设置分页参数
        PageHelper.startPage(pageNum, pageSize);
        List<Product> products = productMapper.selectListForAdmin();

        //获取分页后的对象
        PageInfo pageInfo = new PageInfo(products);
        return pageInfo;
    }

    @Override
    public Product detail(Integer id) {
        Product product = productMapper.selectByPrimaryKey(id);
        return product;
    }

    @Override
    public PageInfo list(ProductListReq productListReq) {
        //构建Query对象
        ProductListQuery productListQuery = new ProductListQuery();

        //搜索处理
        if (!StringUtils.isEmpty(productListReq.getKeyword())) {
            String keyword = new StringBuilder().append("%").
                    append(productListReq.getKeyword()).
                    append("%").toString();
            productListQuery.setKeyword(keyword);
        }

        //目录处理：如果要查询一个目录下的商品，还要查询目录下的子目录下的商品，所以要拿到一个id的list
        if (productListReq.getCategoryId() != null) {
            List<CategoryVO> categoryVOList = categoryService.
                    listCategoryForCustomer(productListReq.getCategoryId());
            ArrayList<Integer> categoryIds = new ArrayList<>();
            categoryIds.add(productListReq.getCategoryId());
            getCategoryIds(categoryVOList, categoryIds);
            productListQuery.setCategoryIds(categoryIds);
        }

        //排序处理
        String orderBy = productListReq.getOrderBy();
        if (Constant.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize(), orderBy);
        } else {
            PageHelper.startPage(productListReq.getPageNum(), productListReq.getPageSize());
        }
        List<Product> productList = productMapper.selectList(productListQuery);
        PageInfo pageInfo = new PageInfo(productList);
        return pageInfo;
    }

    private void getCategoryIds(List<CategoryVO> categoryVOList, ArrayList<Integer> categoryIds) {
        for (int i = 0; i < categoryVOList.size(); i++) {
            CategoryVO categoryVO = categoryVOList.get(i);
            if (categoryVO != null) {
                categoryIds.add(categoryVO.getId());
                getCategoryIds(categoryVO.getChildCategory(), categoryIds);
            }
        }
    }

    @Override
    public void addProductByExcel(File destFile) throws IOException {
        List<Product> productList = readProductFromExcel(destFile);
        for (int i = 0; i < productList.size(); i++) {
            Product productNew = productList.get(i);
            Product productOld = productMapper.selectByName(productNew.getName());
            if (productOld != null) {
                throw new MallException(MallExceptionEnum.NAME_EXISTED);
            }
            int count = productMapper.insertSelective(productNew);
            if (count == 0) {
                throw new MallException(MallExceptionEnum.CREATE_FAILED);
            }
        }


    }

    /**
     * 输入一个Excel文件，返回一个列表
     */
    private List<Product> readProductFromExcel(File excelFile) throws IOException {
        List<Product> productList = new ArrayList<>();
        //将文件变成输入流
        FileInputStream fileInputStream = new FileInputStream(excelFile);
        XSSFWorkbook workbook = new XSSFWorkbook(fileInputStream);
        XSSFSheet firstSheet = workbook.getSheetAt(0);
        //或者XSSFSheet firstSheet = workbook.getSheet("Sheet1");
        Iterator<Row> rowIterator = firstSheet.iterator();
        while (rowIterator.hasNext()) {
            Row nextRow = rowIterator.next();
            Iterator<Cell> iteratorCell = nextRow.iterator();
            Product aProduct = new Product();
            //遍历每行的列
            while (iteratorCell.hasNext()) {
                Cell nextCell = iteratorCell.next();
                //得到当前列的序号
                int columnIndex = nextCell.getColumnIndex();
                switch (columnIndex) {
                    case 0:
                        aProduct.setName((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 1:
                        aProduct.setImage((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 2:
                        aProduct.setDetail((String) ExcelUtil.getCellValue(nextCell));
                        break;
                    case 3:
                        //从Excel中取出的默认为Double
                        Double cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setCategoryId(cellValue.intValue());
                        break;
                    case 4:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setPrice(cellValue.intValue());
                        break;
                    case 5:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setStock(cellValue.intValue());
                        break;
                    case 6:
                        cellValue = (Double) ExcelUtil.getCellValue(nextCell);
                        aProduct.setStatus(cellValue.intValue());
                        break;
                    default:
                        break;
                }
            }
            //添加这一行（每一行是一个product对象）
            productList.add(aProduct);
        }
        workbook.close();
        fileInputStream.close();
        return productList;
    }
}
