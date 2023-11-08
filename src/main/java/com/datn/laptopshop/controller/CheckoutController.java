package com.datn.laptopshop.controller;

import com.datn.laptopshop.config.ConfigVNPAY;
import com.datn.laptopshop.config.ResponseHandler;
import com.datn.laptopshop.dto.CheckOutDto;
import com.datn.laptopshop.dto.OrderDto;
import com.datn.laptopshop.enums.StateCheckout;
import com.datn.laptopshop.service.ICheckoutService;
import com.datn.laptopshop.service.IOrderDetailService;
import com.datn.laptopshop.service.IOrderService;
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@GetMapping("/vnpay")
	public ResponseEntity<Object> PaymentVNPAY(
			@RequestParam("codeOrder") String codeOrder,
			@RequestParam("bankCode") String bankCode) throws IOException {

        System.out.println("da vao PaymentVNPAY ******************");
        System.out.println("bankCode trong PaymentVNPAY: "+bankCode);

		OrderDto orderEntity = orderService.findByCodeOrder(codeOrder);
		String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        long amount = (long) (orderEntity.getTotal_money()*100);
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
        vnp_Params.put("vnp_BankCode", bankCode);
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

		System.out.println("vnp_params: "+ vnp_Params);

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
			@RequestParam("vnp_TxnRef") String vnp_TxnRef ) throws UnsupportedEncodingException {

        Map m = new HashMap<>();

		OrderDto orderEntity = orderService.findByCodeOrder(vnp_TxnRef);
		System.out.println("da vao infoPayment ****@@@@@@@@@******");
		if("00".equals(vnp_ResponseCode)) {
			CheckOutDto checkoutDto = new CheckOutDto();
			//Accounts and Orders Paid
			checkoutDto.setOrder(orderEntity.getId());
			checkoutDto.setUser(orderEntity.getUser());
			//Save information from vnpay returned
			checkoutDto.setAmount(Integer.parseInt(vnp_Amount));
			checkoutDto.setBankCode(vnp_BankCode);
			checkoutDto.setCardType(vnp_CardType);
			checkoutDto.setOrderInfo(vnp_OrderInfo);
			checkoutDto.setPayDate(vnp_PayDate);
			//Save Data
			checkoutService.insert(checkoutDto);
			//Change order status is successful payment
			orderService.updateStateCheckout(orderEntity.getId(), StateCheckout.PAID);

			m.put("success", "Giao dịch thàng công");
		}else {
			m.put("error", "Giao dịch thất bại");
		}

        m.put("order", orderEntity);
        m.put("orderdetail", orderDetailService.findByOrder(orderEntity.getId()));

        return ResponseHandler.responseBuilder("Message","Check Out Success",
                HttpStatus.OK,m,0);
	}

    @GetMapping("/getBill")
    public ResponseEntity<Object> getBill(@RequestParam("id") long id){
        Map m = new HashMap<>();
        m.put("order",orderService.findById(id));
        m.put("orderdetail",orderDetailService.findByOrder(id));

        return ResponseHandler.responseBuilder("Message","Get bill in checkout Success",
                HttpStatus.OK,m,0);
    }

}


