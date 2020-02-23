package og.prj.adminservice;

import og.prj.adminservice.customer.CustomerRepository;
import og.prj.adminservice.jpafiles.UserRepository;
import og.prj.adminservice.order.OrderRepository;
import og.prj.adminservice.order.Orders;
import og.prj.adminservice.orderitem.OrderItem;
import og.prj.adminservice.orderitem.OrderItemRepository;
import og.prj.adminservice.orderitem.ProductConverter;
import og.prj.adminservice.product.AmountWrapper;
import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Controller
public class CustomerResources {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static Principal prncipal;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;




    @GetMapping("/orders")
    @ResponseBody
    public List<Orders> showOrders(Principal principal) {
        orderItemRepository.deleteByQuantity(0);
        return customerRepository.getOne(userRepository.findByUserName(principal.getName()).get().getId()).getOrdersList();
    }

    @GetMapping("/orderlist")
    public String showUsers(Model model, Principal principal) {
        orderItemRepository.deleteByQuantity(0);
        List<Orders> ordersList =  customerRepository.getOne(userRepository.findByUserName(principal.getName()).get().getId()).getOrdersList();
        model.addAttribute("ordersList",ordersList);

        return "orderlist";
    }

/*
    @RequestMapping(value = "/username", method = RequestMethod.GET)
    @ResponseBody
    public String currentUserName(Principal principal) {
        return principal.getName() + " id: " +userRepository.findByUserName(principal.getName()).get().getId();
    }
*/
    @GetMapping("/order")
    public String showAvailableProducts(Model model, Principal principal) {
        prncipal = principal;
        Orders order = new Orders();
        order.setAmountWrapper(new AmountWrapper());
        AmountWrapper amountWrapper = order.getAmountWrapper();
        Iterable<Product> productList = productRepository.findAll();
        productList.forEach(product -> amountWrapper.addProduct(product));
        order.setAmountWrapper(amountWrapper);
        model.addAttribute("order", order);
        return "order";
    }

    @PostMapping("/order")
    public String placeOrder(@ModelAttribute Orders order,Principal principal) {
        prncipal = principal;
        if (ProductConverter.time == null) {
            ProductConverter.time = LocalDateTime.now();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatDateTime = ProductConverter.time.format(formatter);

        order.setTimeOfOrder(formatDateTime);
        order.setCustomerFK(userRepository.findByUserName(principal.getName()).get().getId());

        AtomicBoolean isOrderEmpty = new AtomicBoolean(true);
        order.getAmountWrapper().getWrapper().forEach(product ->
                {
                    if(product.getAmount() != 0) {
                        isOrderEmpty.set(false);
                    }
                }
                );
        if(isOrderEmpty.get()) {
            return "redirect:/emptyorder";
        }
        orderRepository.save(order);
        AtomicReference<Double> orderTotal = new AtomicReference<>((double) 0);

        order.getAmountWrapper().getWrapper().forEach(product ->
                {
                        Long productId = product.getProductId();
                        double productPrice = productRepository.findById(productId).get().getPrice();
                        String orderTime = ProductConverter.time.toString();
                        OrderItem orderItem =orderItemRepository.findByProductIdAndCustFKAndOrderTime(productId,userRepository.findByUserName(principal.getName()).get().getId(),orderTime);
                        if(orderItem != null) {
                            if (product.getAmount() != 0) {
                                orderTotal.updateAndGet(v -> (double) (v + productPrice * product.getAmount()));
                                orderItem.setQuantity(product.getAmount());
                                orderItemRepository.save(orderItem);
                            }
                        }
                }
        );
        order.setOrderTotal(round(orderTotal.get().doubleValue(),3));
        orderRepository.save(order);

        order.deleteAmountWrapper();
        orderItemRepository.deleteByQuantity(0);
        return "redirect:/orderplaced";
    }
}
