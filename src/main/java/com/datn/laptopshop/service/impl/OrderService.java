package com.datn.laptopshop.service.impl;

import com.datn.laptopshop.config.JavaMail;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.*;
import com.datn.laptopshop.dto.request.InforOrder;
import com.datn.laptopshop.entity.Order;
import com.datn.laptopshop.entity.OrderDetail;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.enums.StateOrder;
import com.datn.laptopshop.repos.*;
import com.datn.laptopshop.service.IOrderService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class OrderService implements IOrderService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Autowired
    private JavaMail javaMail;

    @Override
    public OrderDto order(List<CartItem> carts, InforOrder inforOrder) {
        if (inforOrder == null || carts == null)
        {
            ResponseHandler.responseBuilder("Error","The input is null",
                    HttpStatus.BAD_REQUEST,"",99);
            return null;
        }

        var u = userRepository.findById(inforOrder.getUserId());
        if (u == null)
            return null;
        int delivery = 40000;
        Order o = new Order();
        o.setUser(u.get());
        o.setName(u.get().getFullname());
        o.setEmail(u.get().getEmail());
        o.setPhone(inforOrder.getPhone());
        o.setAddress_delivery(inforOrder.getAddress_delivery());
        o.setNum(inforOrder.getNum());
        o.setTotal_money(inforOrder.getTotalMoney() + delivery);
        o.setPayment(inforOrder.getPayment());
        o.setNote(inforOrder.getNote());
        o.setCreated_at(new Date());
        o.setStateCheckout(StateCheckout.UNPAID);
        o.setStateOrder(StateOrder.PENDING);
        Order order = orderRepository.save(o);

        System.out.println("order id trong orderService: "+order.getId());

        for (CartItem cartItem : carts){
            OrderDetail od =new OrderDetail();
            od.setOrder(order);
            od.setProduct(productRepository.findById(cartItem.getProductId()).get());
            od.setPrice(cartItem.getPrice());
            od.setDiscount(cartItem.getDiscount());
            od.setNum(cartItem.getNumProduct());
            od.setTotalPrice(cartItem.getTotalPrice());
            orderDetailRepository.save(od);
        }

        // Save order code. vd: (orderId + userId + orderDate) = codeOrder
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String date = formatter.format(order.getCreated_at());
        String codeOrder = "" + order.getId() + order.getUser().getId() + date;
        order.setCodeOrder(codeOrder);
        orderRepository.save(order);

        return new OrderDto().toOrderDto(order);
    }

    @Override
    public OrderDto findByCodeOrder(String codeOrder) {
        var o = orderRepository.findByCodeOrder(codeOrder);
        if (o.isPresent())
            return new OrderDto().toOrderDto(o.get());
        return null;
    }

    @Override
    public void updateStateCheckout(int id, StateCheckout paid) {
        Optional<Order> o = orderRepository.findById(id);
        if (o.isPresent()){
            o.get().setStateCheckout(paid);
            orderRepository.save(o.get());
        }
        else
            System.out.println("loi o phan update state check out");
    }

    @Override
    public OrderDto findById(int id) {
        var o = orderRepository.findById(id);
        if (o.isPresent())
            return new OrderDto().toOrderDto(o.get());
        return null;
    }

    @Override
    public List<OrderDto> findByOrderByStatus(String email, StateOrder status) {
        List<Order> listOrder = orderRepository.findByOrderByStatusUser(email, status);
        if (listOrder.isEmpty()){
                return new ArrayList<>();
        }

        List<OrderDto> listOrderDto = new ArrayList<>();
        for (Order o : listOrder){
            OrderDto orderDto = new OrderDto().toOrderDto(o);
            List<OrderDetail> listOrderDetail = o.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail oEntity : listOrderDetail){
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(oEntity);
                oDto.setProduct(new ProductDto().toProductDTO(oEntity.getProduct()));
                listOrderDetailDto.add(oDto);
            }

            orderDto.setOrderdetail(listOrderDetailDto);

            listOrderDto.add(orderDto);
        }

        return listOrderDto;
    }

    @Override
    public List<OrderDto> findByOrderByStatus(StateOrder status) {
        List<Order> listOrder = orderRepository.findByOrderByStatusAdmin(status);
        if (listOrder.isEmpty()){
            return new ArrayList<>();
        }

        List<OrderDto> listOrderDto = new ArrayList<>();
        for (Order o : listOrder){
            OrderDto orderDto = new OrderDto().toOrderDto(o);
            List<OrderDetail> listOrderDetail = o.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail oEntity : listOrderDetail){
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(oEntity);
                oDto.setProduct(new ProductDto().toProductDTO(oEntity.getProduct()));
                listOrderDetailDto.add(oDto);
            }

            orderDto.setOrderdetail(listOrderDetailDto);

            listOrderDto.add(orderDto);
        }

        return listOrderDto;
    }

    @Override
    public List<OrderDto> findOrderByRangeDay(String email, Date start, Date end, StateOrder status) {

        List<Order> listOrder = orderRepository.findOrderByRangeDay(email, start, end, status);

        if (listOrder.isEmpty()){
            return null;
        }

        List<OrderDto> listOrderDto = new ArrayList<>();
        for (Order o : listOrder){
            OrderDto orderDto = new OrderDto().toOrderDto(o);
            List<OrderDetail> listOrderDetail = o.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail oEntity : listOrderDetail){
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(oEntity);
                oDto.setProduct(new ProductDto().toProductDTO(oEntity.getProduct()));
                listOrderDetailDto.add(oDto);
            }

            orderDto.setOrderdetail(listOrderDetailDto);

            listOrderDto.add(orderDto);
        }

        return listOrderDto;
    }

    @Override
    public boolean deleteOrder(int id) {
        var o = orderRepository.findById(id);
        if (o.isEmpty())
            return false;
        orderRepository.delete(o.get());

        return true;
    }

    @Override
    public boolean cancelOrder(int id) {
        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty())
            return false;
        order.get().setStateOrder(StateOrder.CANCELLED);
        orderRepository.save(order.get());

        return true;
    }

    @Override
    public Page<OrderDto> findAll(int page, int limit, String search_text, StateOrder status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<Order> pageNews = orderRepository.findAll(search_text,status,p);

        if (pageNews.isEmpty())
            return null;

        Page<OrderDto> pageNewsDto =pageNews.map(n -> {
            OrderDto orderDto = new OrderDto().toOrderDto(n);
            List<OrderDetail> listOrderDetailEntity = n.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail o: listOrderDetailEntity
                 ) {
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(o);
                oDto.setProduct(new ProductDto().toProductDTO(o.getProduct()));
                listOrderDetailDto.add(oDto);
            }
            orderDto.setOrderdetail(listOrderDetailDto);
            return orderDto;
        });

        return pageNewsDto;
    }

    @Override
    public Page<OrderDto> findOrderByRangeDay(int page, int limit, String search_text, Date start, Date end, StateOrder status) {
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable p = PageRequest.of(page - 1,limit, sort);
        Page<Order> pageNews = orderRepository.findOrderByRangeDayAdmin(search_text, start, end,status,p);

        if (pageNews.isEmpty())
            return null;

        Page<OrderDto> pageNewsDto =pageNews.map(n -> {
            OrderDto orderDto = new OrderDto().toOrderDto(n);
            List<OrderDetail> listOrderDetailEntity = n.getOrderdetail();
            List<OrderDetailDto> listOrderDetailDto = new ArrayList<>();
            for (OrderDetail o: listOrderDetailEntity
            ) {
                OrderDetailDto oDto = new OrderDetailDto().toOrderDetailDto(o);
                oDto.setProduct(new ProductDto().toProductDTO(o.getProduct()));
                listOrderDetailDto.add(oDto);
            }
            orderDto.setOrderdetail(listOrderDetailDto);
            return orderDto;
        });

        return pageNewsDto;
    }

    @Override
    public boolean updateStateOrder(int id, StateOrder status) {

        Optional<Order> order = orderRepository.findById(id);
        if (order.isEmpty()) {
            return false;
        }

        // If saving modification fail, return false
        if (status == StateOrder.RECEIVED)
            order.get().setStateCheckout(StateCheckout.PAID);

//        if (status == StateOrder.CANCELLED)
//        {
//            // gui mail huy don hang ve
//            sendMailCancelledOrder(order.get().getCodeOrder());
//        }


        order.get().setStateOrder(status);
        if (orderRepository.save(order.get()) == null) {
            return false;
        }

        return true;
    }

    @Override
    public boolean sendMailCancelledOrder(String codeOrder) {
        int attemptCount=0;
        int maxAttempt=0;
        do {
            try {
                String subject = "Hủy đơn hàng ";
                var orderInfo = orderRepository.findByCodeOrder(codeOrder);
                var od = orderDetailRepository.findByOrder(orderInfo.get().getId());
                StringBuilder productsHtml = new StringBuilder();
                for (OrderDetail product : od) {
                    int newPrice = (product.getPrice() - (product.getDiscount() * product.getPrice()) / 100);
                    productsHtml.append("<li>");
                    productsHtml.append("<table style=\"width:100%;border-bottom:1px solid #e4e9eb\">");
                    productsHtml.append("<tbody><tr>");
                    productsHtml.append("<td style=\"width:100%;padding:25px 10px 0px 0\" colspan=\"2\">");
                    productsHtml.append("<div style=\"float:left;width:80px;height:80px;border:1px solid #ebeff2;overflow:hidden\">");
                    productsHtml.append("<img style=\"max-width:100%;max-height:100%\" src=\"" + product.getProduct().getImg() + "\"></div>");
                    productsHtml.append("<div style=\"margin-left:100px\">");
                    productsHtml.append("<a href=\"#\" style=\"color:#357ebd;text-decoration:none\">" + product.getProduct().getName() + "</a><p style=\"color:#678299;margin-bottom:0;margin-top:8px\">Giá: " + product.getPrice() + " VND</p><p style=\"color:#678299;margin-bottom:0;margin-top:8px\">Giảm giá: " + product.getDiscount() + "%</p></div></td></tr>");
                    productsHtml.append("<tr>");
                    productsHtml.append("<td style=\"width:70%;padding:5px 0px 25px\">");
                    productsHtml.append("<div style=\"margin-left:100px\">");
                    productsHtml.append("" + newPrice + " VND<span style=\"margin-left:20px\">x " + product.getNum() + "</span></div></td>");
                    productsHtml.append("<td style=\"text-align:right;width:30%;padding:5px 0px 25px\">");
                    productsHtml.append(" " + product.getTotalPrice() + " VND</td></tbody></table>");
                    productsHtml.append("</li>");
                }
                    String content ="<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f4f4f4;\">\n" +
                            "\n" +
                            "    <h2>Thông Báo Hủy Đơn Hàng và Hoàn Tiền</h2>\n" +
                            "\n" +
                            "    <p>Kính gửi Anh/Chị <strong>"+orderInfo.get().getName()+"</strong>, Email: "+orderInfo.get().getEmail()+"</p>\n" +
                            "\n" +
                            "    <p>Chúng tôi xin thông báo rằng đơn hàng của Anh/Chị đã được hủy thành công tại <strong>TCD Laptop</strong>. Việc hoàn tiền sẽ được thực hiện trong thời gian sớm nhất.</p>\n" +
                            "\n" +
                            "    <p>Thông tin chi tiết về đơn hàng đã hủy:</p>\n" +
                            "\n" +
                            "    \n" +
                            "        Mã đơn hàng: <span style=\"font-size:medium; margin-left:56%\"><strong>"+orderInfo.get().getCodeOrder()+"</strong></span> <br>\n" +
                            "        Ngày đặt hàng: <span style=\"font-size:medium; margin-left:55.5%\"><strong>"+orderInfo.get().getCreated_at()+"</strong></span> <br>\n" +
                            "        <span style=\"padding-top: 4px\">Sản phẩm:</span><br>\n" +
                            "        <ul style=\"padding-left:0;list-style-type:none;margin-bottom:0\">\n" +
                                      productsHtml  +
                            "              </ul> <br>\n" +
                            "        <span>Tổng tiền đơn hàng:</span> <span style=\"font-weight:600; font-size:medium; margin-left:60%\">"+orderInfo.get().getTotal_money()+" VND</span>\n" +
                            "    \n" +
                            "\n" +
                            "    <p style=\"font-size:medium\"><strong>Hướng dẫn hoàn tiền với đơn hàng đã thanh toán:</strong> Anh/Chị vui lòng liên hệ với chúng tôi qua <strong>email</strong> để được hỗ trợ hoàn tiền.</p>\n" +
                            "\n" +
                            "    <p>Nếu có bất kỳ câu hỏi hoặc cần hỗ trợ, vui lòng liên hệ chúng tôi qua email: <a href=\"mailto:trandai1116@gmail.com\">trandai1116@gmail.com</a>.</p>\n" +
                            "\n" +
                            "    <p style=\"font-size:medium\"><strong>Thông tin liên hệ:</strong></p>\n" +
                            "    <ul>\n" +
                            "        <li><strong>Email:</strong> <a href=\"mailto:trandai1116@gmail.com\">trandai1116@gmail.com</a></li>\n" +
                            "        <li><strong>Số điện thoại:</strong> (+84) 0906088493</li>\n" +
                            "    </ul>\n" +
                            "\n" +
                            "    <p>Xin cảm ơn sự quan tâm của Anh/Chị trong việc đặt hàng tại cửa hàng chúng tôi.</p>\n" +
                            "\n" +
                            "    <p style=\"text-align:right\"><i>Trân trọng,</i></p>\n" +
                            "    <p style=\"text-align:right\"><strong>Ban quản trị cửa hàng TCD Laptop </strong></p>\n" +
                            "\n" +
                            "</div>";
                javaMail.sendEmail(orderInfo.get().getEmail(), subject, content);
                return true;
            } catch (Exception e) {
                attemptCount++;
                e.printStackTrace();
            }
        }while(attemptCount < maxAttempt);
        return false;
    }

}
