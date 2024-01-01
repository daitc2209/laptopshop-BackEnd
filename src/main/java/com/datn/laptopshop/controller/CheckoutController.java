package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ConfigVNPAY;
import com.datn.laptopshop.config.JavaMail;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.CheckOutDto;
import com.datn.laptopshop.dto.OrderDetailDto;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.service.ICheckoutService;
import com.datn.laptopshop.service.IOrderDetailService;
import com.datn.laptopshop.service.IOrderService;
import com.datn.laptopshop.service.IProductService;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {
    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger(CheckoutController.class);

    @Autowired
    private ICheckoutService checkoutService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private IOrderDetailService orderDetailService;

    @Autowired
    private IProductService productService;

    @Autowired
    private JavaMail javaMail;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @GetMapping("/vnpay")
    public ResponseEntity<Object> PaymentVNPAY(
            @RequestParam("codeOrder") String codeOrder,
            @RequestParam("bankCode") String bankCode) throws IOException {

        OrderDto orderEntity = orderService.findByCodeOrder(codeOrder);
        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        long amount = (long) (orderEntity.getTotal_money())*100;

        String orderType = "other";

        String vnp_TxnRef = codeOrder;
        String vnp_TmnCode = ConfigVNPAY.vnp_TmnCode;
        String vnp_IpAddr = "127.0.0.1";
        
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
//        vnp_Params.put("vnp_BankCode", bankCode);
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef );
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", ConfigVNPAY.vnp_Returnurl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(orderEntity.getCreated_at());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);
        
        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = ConfigVNPAY.hmacSHA512(ConfigVNPAY.vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = ConfigVNPAY.vnp_PayUrl + "?" + queryUrl;

        Map m = new HashMap<>();
        m.put("redirectUrl", paymentUrl);
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", paymentUrl)
                .body(m);
    }

    @GetMapping("/vnpay/info")
    public ResponseEntity<Object> infoPayment(
            @RequestParam("vnp_Amount") String vnp_Amount,
            @RequestParam("vnp_BankCode") String vnp_BankCode,
            @RequestParam("vnp_CardType") String vnp_CardType,
            @RequestParam("vnp_OrderInfo") String vnp_OrderInfo,
            @RequestParam("vnp_PayDate") String vnp_PayDate,
            @RequestParam("vnp_ResponseCode") String vnp_ResponseCode,
            @RequestParam("vnp_TxnRef") String vnp_TxnRef ) throws UnsupportedEncodingException, MessagingException {

        Map m = new HashMap<>();

        OrderDto order = orderService.findByCodeOrder(vnp_TxnRef);

        if("00".equals(vnp_ResponseCode)) {
            CheckOutDto checkoutDto = new CheckOutDto();
            //Accounts and Orders Paid
            checkoutDto.setOrder(order.getId());
            checkoutDto.setUser(order.getUser());
            //Save information from vnpay returned
            checkoutDto.setAmount(Long.parseLong(vnp_Amount)/100);
            checkoutDto.setBankCode(vnp_BankCode);
            checkoutDto.setCardType(vnp_CardType);
            checkoutDto.setOrderInfo(vnp_OrderInfo);
            checkoutDto.setPayDate(vnp_PayDate);
            //Save Data
            checkoutService.insert(checkoutDto);
            //Change order status is successful payment
            orderService.updateStateCheckout(order.getId(), StateCheckout.PAID);

            m.put("success", "Giao dịch thàng công");
            boolean b = productService.updateQuantityProduct(orderDetailService.findByOrder(order.getId()));
            if (!b)
                return ResponseHandler.responseBuilder("Message","Check Out Failed!!!",
                        HttpStatus.BAD_REQUEST,"",99);
            m.put("order", order);
            m.put("orderdetail", orderDetailService.findByOrder(order.getId()));

            return ResponseHandler.responseBuilder("Message","Check Out Success",
                    HttpStatus.OK,m,0);
        }else {
            orderService.deleteOrder(order.getId());
            m.put("error", "Giao dịch thất bại");
            return ResponseHandler.responseBuilder("Message","Check Out Success",
                    HttpStatus.OK,m,0);
        }
    }

    @GetMapping("/getBill")
    public ResponseEntity<Object> getBill(@RequestParam("id") int id){
        boolean b = productService.updateQuantityProduct(orderDetailService.findByOrder(id));
        if (!b)
            return ResponseHandler.responseBuilder("Message","Get bill in checkout Failed!!!",
                    HttpStatus.BAD_REQUEST,"",99);
        Map m = new HashMap<>();
        m.put("order",orderService.findById(id));
        m.put("orderdetail",orderDetailService.findByOrder(id));

        return ResponseHandler.responseBuilder("Message","Get bill in checkout Success",
                HttpStatus.OK,m,0);
    }

    @PostMapping("/orderConfirm")
    public ResponseEntity<?> orderConfirm(@RequestParam("codeOrder") String codeOrder){
        int attemptCount=0;
        int maxAttempt=0;
        do {
            try {
                OrderDto orderEntity = orderService.findByCodeOrder(codeOrder);
                String subject = "Order confirmation";
                var od = orderDetailService.findByOrder(orderEntity.getId());
                StringBuilder productsHtml = new StringBuilder();
                for (OrderDetailDto product : od) {
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
                String content = "<div style=\"font-family:&quot;Arial&quot;,Helvetica Neue,Helvetica,sans-serif;line-height:14pt;padding:20px 0px;font-size:14px;max-width:580px;margin:0 auto\"><div>\n" +
                        "  </div><div style=\"padding:0 10px;margin-bottom:25px\"><div>\n" +
                        "    \n" +
                        "    </div><p>Xin chào " + orderEntity.getName() + "</p>\n" +
                        "    <p>Cảm ơn Anh/chị đã đặt hàng tại <strong>TCD Laptop</strong>!</p>\n" +
                        "    <p>Đơn hàng của Anh/chị đã được tiếp nhận, chúng tôi sẽ nhanh chóng liên hệ với Anh/chị.</p>\n" +
                        "  </div>\n" +
                        "  <hr>\n" +
                        "  <div style=\"padding:0 10px\">\n" +
                        "    \n" +
                        "    <table style=\"width:100%;border-collapse:collapse;margin-top:20px\">\n" +
                        "      <thead>\n" +
                        "        <tr>\n" +
                        "          <th style=\"text-align:left;width:50%;font-size:medium;padding:5px 0\">Thông tin mua hàng</th>\n" +
                        "          <th style=\"text-align:left;width:50%;font-size:medium;padding:5px 0\">Địa chỉ nhận hàng</th>\n" +
                        "        </tr>\n" +
                        "      </thead>\n" +
                        "      <tbody>\n" +
                        "        <tr>\n" +
                        "          <td style=\"padding-right:15px\">\n" +
                        "            <table style=\"width:100%\">\n" +
                        "              <tbody>\n" +
                        "                \n" +
                        "                <tr>\n" +
                        "                  <td>" + orderEntity.getName() + "</td>\n" +
                        "                </tr>\n" +
                        "                \n" +
                        "                \n" +
                        "                <tr>\n" +
                        "                  <td style=\"word-break:break-word;word-wrap:break-word\"><a href=\"mailto:" + orderEntity.getEmail() + "\" target=\"_blank\">" + orderEntity.getEmail() + "</a></td>\n" +
                        "                </tr>\n" +
                        "                \n" +
                        "                \n" +
                        "                <tr>\n" +
                        "                  <td>" + orderEntity.getPhone() + "</td>\n" +
                        "                </tr>\n" +
                        "                \n" +
                        "              </tbody>\n" +
                        "            </table>\n" +
                        "          </td>\n" +
                        "          <td>\n" +
                        "            <table style=\"width:100%\">\n" +
                        "              <tbody>\n" +
                        "                \n" +
                        "                <tr>\n" +
                        "                  <td>" + orderEntity.getName() + "</td>\n" +
                        "                </tr>\n" +
                        "                \n" +
                        "                \n" +
                        "                <tr>\n" +
                        "                  <td style=\"word-break:break-word;word-wrap:break-word\">\n" +
                        "                    \n" +
                        "                  </td>\n" +
                        "                </tr>\n" +
                        "                \n" +
                        "                <tr>\n" +
                        "                  <td style=\"word-break:break-word;word-wrap:break-word\">\n" +
                        "                    \n" +
                        "                    " + orderEntity.getAddress_delivery() + "\n" +
                        "                  </td>\n" +
                        "                </tr>\n" +
                        "                \n" +
                        "                <tr>\n" +
                        "                  <td>" + orderEntity.getPhone() + "</td>\n" +
                        "                </tr>\n" +
                        "                \n" +
                        "              </tbody>\n" +
                        "            </table>\n" +
                        "          </td>\n" +
                        "        </tr>\n" +
                        "      </tbody>\n" +
                        "    </table>\n" +
                        "    <table style=\"width:100%;border-collapse:collapse;margin-top:20px\">\n" +
                        "      <thead>\n" +
                        "        <tr>\n" +
                        "          <th style=\"text-align:left;width:50%;font-size:medium;padding:5px 0\">Phương thức thanh toán</th>\n" +
                        "          <th style=\"text-align:left;width:50%;font-size:medium;padding:5px 0\">Phương thức vận chuyển</th>\n" +
                        "        </tr>\n" +
                        "      </thead>\n" +
                        "      <tbody>\n" +
                        "        <tr>\n" +
                        "          <td style=\"padding-right:15px\">" + orderEntity.getPayment() + "</td>\n" +
                        "          <td>\n" +
                        "            \n" +
                        "            Giao hàng tận nơi<br>\n" +
                        "            \n" +
                        "          </td>\n" +
                        "        </tr>\n" +
                        "      </tbody>\n" +
                        "    </table>\n" +
                        "    \n" +
                        "  </div>\n" +
                        "  <div style=\"margin-top:20px;padding:0 10px\">\n" +
                        "    <div style=\"padding-top:10px;font-size:medium\"><strong>Thông tin đơn hàng</strong></div>\n" +
                        "    <table style=\"width:100%;margin:10px 0\">\n" +
                        "      <tbody><tr>\n" +
                        "        <td style=\"width:50%;padding-right:15px\">Mã đơn hàng: " + orderEntity.getCodeOrder() + "</td>\n" +
                        "        <td style=\"width:50%\">Ngày đặt hàng: " + orderEntity.getCreated_at() + "</td>\n" +
                        "      </tr>\n" +
                        "    </tbody></table>\n" +
                        "    <ul style=\"padding-left:0;list-style-type:none;margin-bottom:0\">\n" +
                        productsHtml +
                        "    </ul>\n" +
                        "    <table style=\"width:100%;border-collapse:collapse;margin-bottom:50px;margin-top:10px\">\n" +
                        "      <tbody><tr>\n" +
                        "        <td style=\"width:20%\"></td>\n" +
                        "        <td style=\"width:80%\">\n" +
                        "          <table style=\"width:100%;float:right\">\n" +
                        "            <tbody><tr>\n" +
                        "            <tr>\n" +
                        "              <td style=\"padding-bottom:10px\">Tổng tiền:</td>\n" +
                        "              <td style=\"font-weight:bold;text-align:right;padding-bottom:10px\">\n" +
                        "                " + orderEntity.getTotal_money() + " VND</td>\n" +
                        "            </tr>\n" +
                        "            <tr>\n" +
                        "              <td style=\"padding-bottom:10px\">Phí vận chuyển:</td>\n" +
                        "              <td style=\"font-weight:bold;text-align:right;padding-bottom:10px\">\n" +
                        "                40,000 VND</td>\n" +
                        "            </tr>\n" +
                        "            <tr style=\"border-top:1px solid #e5e9ec\">\n" +
                        "              <td style=\"padding-top:10px\">Thành tiền</td>\n" +
                        "              <td style=\"font-weight:bold;text-align:right;font-size:16px;padding-top:10px\">\n" +
                        "                " + (orderEntity.getTotal_money()) + " VND</td>\n" +
                        "            </tr>\n" +
                        "          </tbody></table>\n" +
                        "        </td>\n" +
                        "      </tr>\n" +
                        "    </tbody></table>\n" +
                        "  </div>\n" +
                        "  <div style=\"clear:both\"></div>\n" +
                        "  \n" +
                        "<div style=\"padding:0 10px\">" +
                        "<p style=\"margin:30px 0\"><span style=\"font-weight:bold\">Ghi chú:</span> " + orderEntity.getNote() + "</p>" +
                        "</div>\n" +
                        "  <div style=\"clear:both\"></div>\n" +
                        "  <div style=\"padding:0 10px\">\n" +
                        "    <div style=\"clear:both\"></div>\n" +
                        "    <p style=\"margin:30px 0\">Nếu Anh/chị có bất kỳ câu hỏi nào, xin liên hệ với chúng tôi tại <a href=\"mailto:trandai1116@gmail.com\" style=\"color:#357ebd\" target=\"_blank\">trandai1116@gmail.com</a></p>\n" +
                        "    <p style=\"text-align:right\"><i>Trân trọng,</i></p>\n" +
                        "    <p style=\"text-align:right\"><strong>Ban quản trị cửa hàng TCD Laptop </strong></p>\n" +
                        "  </div>\n" +
                        "</div>";

                javaMail.sendEmail(orderEntity.getEmail(), subject, content);
                return ResponseHandler.responseBuilder("Message", " Successfully !!!",
                        HttpStatus.OK, "", 0);
            } catch (Exception e) {
                attemptCount++;
                e.printStackTrace();
            }
        }while(attemptCount < maxAttempt);

        return ResponseHandler.responseBuilder("Message", "Failed to send email after multiple attempts",
                HttpStatus.INTERNAL_SERVER_ERROR, "", 99);
    }

}


