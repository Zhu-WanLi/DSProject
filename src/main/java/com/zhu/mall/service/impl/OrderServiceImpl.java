package com.zhu.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.zxing.WriterException;
import com.zhu.mall.common.Constant;
import com.zhu.mall.exception.MallException;
import com.zhu.mall.exception.MallExceptionEnum;
import com.zhu.mall.filter.UserFilter;
import com.zhu.mall.model.dao.CartMapper;
import com.zhu.mall.model.dao.OrderItemMapper;
import com.zhu.mall.model.dao.OrderMapper;
import com.zhu.mall.model.dao.ProductMapper;
import com.zhu.mall.model.pojo.Order;
import com.zhu.mall.model.pojo.OrderItem;
import com.zhu.mall.model.pojo.Product;
import com.zhu.mall.model.query.OrderStatisticsQuery;
import com.zhu.mall.model.request.CreateOrderReq;
import com.zhu.mall.model.vo.CartVO;
import com.zhu.mall.model.vo.OrderItemVO;
import com.zhu.mall.model.vo.OrderStatisticsVO;
import com.zhu.mall.model.vo.OrderVO;
import com.zhu.mall.service.CartService;
import com.zhu.mall.service.OrderService;
import com.zhu.mall.service.UserService;
import com.zhu.mall.utils.OrderCodeFactory;
import com.zhu.mall.utils.QRCodeGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述：      订单Service实现类
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    CartMapper cartMapper;
    @Autowired
    CartService cartService;
    @Autowired
    ProductMapper productMapper;
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    OrderItemMapper orderItemMapper;
    @Autowired
    UserService userService;

    @Value("${file.upload.uri}")
    String uri;

    @Override
    //数据库事务，遇到任何异常（Exception.class）回滚
    @Transactional(rollbackFor = Exception.class)
    //返回的是订单号
    public String create(CreateOrderReq createOrderReq) {
        //拿到用户ID
        Integer userId = UserFilter.currentUser.getId();
        //从购物车中查找已勾选的商品
        List<CartVO> cartVOList = cartService.list(userId);
        //如果购物车勾选为空，报错
        List<CartVO> cartVOListTemp = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            if (cartVO.getSelected().equals(Constant.Cart.CHECKED)) {
                cartVOListTemp.add(cartVO);
            }
        }
        cartVOList = cartVOListTemp;
        //判断商品是否存在、上下架状态、库存
        if (CollectionUtils.isEmpty(cartVOList)) {
            throw new MallException(MallExceptionEnum.CART_EMPTY);
        }
        validSaleStatusAndStock(cartVOList);
        //把购物车对象转为订单item对象
        List<OrderItem> orderItemList = cartVOListToOrderItemList(cartVOList);
        //扣库存
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            Product product = productMapper.selectByPrimaryKey(orderItem.getProductId());
            int stock = product.getStock() - orderItem.getQuantity();
            if (stock < 0) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }
            product.setStock(stock);
            productMapper.updateByPrimaryKeySelective(product);
        }
        //把购物车中的已勾选的商品删除
        cleanCart(cartVOList);
        //生成订单
        Order order = new Order();
        //生成订单号，有独立的规则
        String orderNo = OrderCodeFactory.getOrderCode(Long.valueOf(userId));
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalPrice(totalPrice(orderItemList));
        order.setReceiverAddress(createOrderReq.getReceiverAddress());
        order.setReceiverName(createOrderReq.getReceiverName());
        order.setReceiverMobile(createOrderReq.getReceiverMobile());
        //利用常量枚举类设置status的值
        order.setOrderStatus(Constant.OrderStatusEnum.NOT_PAID.getCode());
        order.setPostage(0);
        order.setPaymentType(1);
        //插入到order表
        orderMapper.insertSelective(order);
        //循环保存每个商品到order_item表
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            orderItem.setOrderNo(orderNo);
            orderItemMapper.insertSelective(orderItem);
        }
        //返回结果
        return orderNo;
    }

    private Integer totalPrice(List<OrderItem> orderItemList) {
        Integer totalPrice = 0;
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            totalPrice += orderItem.getTotalPrice();
        }
        return totalPrice;
    }

    private void cleanCart(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            cartMapper.deleteByPrimaryKey(cartVO.getId());
        }
    }

    private List<OrderItem> cartVOListToOrderItemList(List<CartVO> cartVOList) {
        List<OrderItem> orderItemList = new ArrayList<>();
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(cartVO.getProductId());
            //记录商品快照信息
            orderItem.setProductName(cartVO.getProductName());
            orderItem.setProductImg(cartVO.getProductImage());
            orderItem.setQuantity(cartVO.getQuantity());
            orderItem.setUnitPrice(cartVO.getPrice());
            orderItem.setTotalPrice(cartVO.getTotalPrice());
            orderItemList.add(orderItem);
        }
        return orderItemList;
    }

    private void validSaleStatusAndStock(List<CartVO> cartVOList) {
        for (int i = 0; i < cartVOList.size(); i++) {
            CartVO cartVO = cartVOList.get(i);
            Product product = productMapper.selectByPrimaryKey(cartVO.getProductId());
            //判断商品是否存在、上架
            if (product == null || product.getStatus().equals(Constant.SaleStatus.NOT_SALE)) {
                throw new MallException(MallExceptionEnum.NOT_SALE);
            }
            //判断商品库存
            if (cartVO.getQuantity() > product.getStock()) {
                throw new MallException(MallExceptionEnum.NOT_ENOUGH);
            }
        }
    }

    @Override
    public OrderVO detail(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        //校验订单是否存在
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        //校验订单所属
        Integer userId = UserFilter.currentUser.getId();
        if (!order.getUserId().equals(userId)) {
            throw new MallException(MallExceptionEnum.NO_YOUR_ORDER);
        }
        //调用方法将Order类型变成OrderVO类型
        OrderVO orderVO = getOrderVO(order);
        return orderVO;

    }

    private OrderVO getOrderVO(Order order) {
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        //获取订单对应的orderItemVOList
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNo(order.getOrderNo());
        List<OrderItemVO> orderItemVOList = new ArrayList<>();
        for (int i = 0; i < orderItemList.size(); i++) {
            OrderItem orderItem = orderItemList.get(i);
            OrderItemVO orderItemVO = new OrderItemVO();
            BeanUtils.copyProperties(orderItem, orderItemVO);
            orderItemVOList.add(orderItemVO);
        }
        orderVO.setOrderItemVOList(orderItemVOList);
        //通过code拿到枚举，再通过getValue()拿到信息
        orderVO.setOrderStatusName(Constant.OrderStatusEnum.codeOf(orderVO.getOrderStatus()).getValue());
        return orderVO;
    }

    @Override
    public PageInfo listForCustomer(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Integer userId = UserFilter.currentUser.getId();
        List<Order> orderList = orderMapper.selectForCustomer(userId);
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderVOList);
        return pageInfo;
    }

    private List<OrderVO> orderListToOrderVOList(List<Order> orderList) {
        List<OrderVO> orderVOList = new ArrayList<>();
        for (int i = 0; i < orderList.size(); i++) {
            Order order = orderList.get(i);
            OrderVO orderVO = getOrderVO(order);
            orderVOList.add(orderVO);
        }
        return orderVOList;
    }

    @Override
    public void cancel(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) {
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        //验证用户身份
        if (order.getUserId() != UserFilter.currentUser.getId()) {
            throw new MallException(MallExceptionEnum.NO_YOUR_ORDER);
        }
        //未付款时允许取消
        if (order.getOrderStatus().equals(Constant.OrderStatusEnum.NOT_PAID.getCode())) {
            order.setOrderStatus(Constant.OrderStatusEnum.CANCELED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        } else {
            throw new MallException(MallExceptionEnum.CANCEL_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public String qrcode(String orderNo) {
        //获得IP
        ServletRequestAttributes attributes
                = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

//        try {
//            //获取简单局域网中的本机ip
//            ip = InetAddress.getLocalHost().getHostAddress();
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }

        //                     拿到端口号
        String address = this.uri;
        //支付url
        String payUrl = "http://" + address + "/order/pay?orderNo=" + orderNo;
        try {
            QRCodeGenerator.
                    generateQRCode(payUrl, 350, 350, Constant.FILE_UPLOAD_DIR + orderNo + ".png");
        } catch (WriterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String pngAddress = "http://" + address + "/images/" + orderNo + ".png";
        return pngAddress;
    }

    @Override
    public PageInfo listForAdmin(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectListForAdmin();
        List<OrderVO> orderVOList = orderListToOrderVOList(orderList);
        PageInfo pageInfo = new PageInfo(orderVOList);
        return pageInfo;
    }

    @Override
    public void pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        if(order.getOrderStatus() == Constant.OrderStatusEnum.NOT_PAID.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.PAID.getCode());
            order.setPayTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new MallException(MallExceptionEnum.PAY_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void delivered(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        if(order.getOrderStatus() == Constant.OrderStatusEnum.PAID.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.DELIVERED.getCode());
            order.setDeliveryTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new MallException(MallExceptionEnum.DELIVER_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public void finish(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order == null){
            throw new MallException(MallExceptionEnum.NO_ORDER);
        }
        if(!userService.checkAdminRole(UserFilter.currentUser) && UserFilter.currentUser.getId() != order.getUserId()){
            throw new MallException(MallExceptionEnum.NO_YOUR_ORDER);
        }
        //发货后修改状态
        if(order.getOrderStatus() == Constant.OrderStatusEnum.DELIVERED.getCode()){
            order.setOrderStatus(Constant.OrderStatusEnum.FINISHED.getCode());
            order.setEndTime(new Date());
            orderMapper.updateByPrimaryKeySelective(order);
        }else {
            throw new MallException(MallExceptionEnum.FINISH_WRONG_ORDER_STATUS);
        }
    }

    @Override
    public List<OrderStatisticsVO> statistic(Date startDate, Date endDate){
        OrderStatisticsQuery orderStatisticsQuery = new OrderStatisticsQuery();
        orderStatisticsQuery.setStartDate(startDate);
        orderStatisticsQuery.setEndDate(endDate);
        List<OrderStatisticsVO> orderStatisticsVOList = orderMapper.selectOrderStatistics(orderStatisticsQuery);
        return orderStatisticsVOList;
    }


}


