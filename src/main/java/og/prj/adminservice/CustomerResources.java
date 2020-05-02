package og.prj.adminservice;

import og.prj.adminservice.customer.*;
import og.prj.adminservice.jpafiles.UserRepository;
import og.prj.adminservice.jpafiles.Users;
import og.prj.adminservice.order.OrderRepository;
import og.prj.adminservice.order.Orders;
import og.prj.adminservice.orderitem.OrderItem;
import og.prj.adminservice.orderitem.ProductConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Controller
public class CustomerResources {

    public static Principal prncipal;

    public static final String REACTJS_URI = "http://edit-pics.s3-website-us-east-1.amazonaws.com/";
  //  public static final String REACTJS_URI = "http://localhost:3000/";

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerServices customerServices;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping(path = "/orders")
    public void sendRedirect(HttpServletResponse response, Principal principal) throws IOException {

        Long userId = userRepository.findByUserName(principal.getName()).get().getId();

        response.sendRedirect(REACTJS_URI + "getcustomertoken/" + userId);
    }

    @GetMapping(path = "/neworderreact")
    public void sendReactNewOrder(HttpServletResponse response, Principal principal) throws IOException {

        Long userId = userRepository.findByUserName(principal.getName()).get().getId();

        response.sendRedirect(REACTJS_URI + "getcustomerordertoken/" + userId);
    }


    @GetMapping("/getorders/{id}")
    @ResponseBody
    public List<Orders> showOrders(@PathVariable Long id) {
        List<Orders> orders = customerRepository.getOne(id).getOrdersList();
        if(!orders.isEmpty()) {
            Collections.sort(orders, Comparator.comparingLong(Orders::getOrderId).reversed());
        }
        return orders;
    }

    @GetMapping("/getcustomerinfo/{id}")
    @ResponseBody
    public ResponseEntity<CustomerVO> getCustomerInfo(@PathVariable Long id) {
        return   ResponseEntity.ok(customerServices.getCustomerInformation(id));
    }

    @GetMapping("/orderlist")
    public String showUsers(Model model, Principal principal) {
        List<Orders> ordersList =  customerRepository.getOne(userRepository.findByUserName(principal.getName()).get().getId()).getOrdersList();
        model.addAttribute("ordersList",ordersList);

        return "orderlist";
    }

    @GetMapping("/order")
    public String showAvailableProducts(Model model,Principal principal) {
        prncipal = principal;
        model.addAttribute("order", customerServices.getAvailableProductsFromDB());
        return "order";
    }

    @PostMapping("/order")
    public String placeOrder(@ModelAttribute Orders order,Principal principal) {
        prncipal = principal;

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

        LocalDateTime time = ProductConverter.time;
        if(time == null) {
            ProductConverter.time = LocalDateTime.now();
            time = ProductConverter.time;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = time.format(formatter);

        order.setTimeOfOrder(formattedDateTime);

        Long customerFK = userRepository.findByUserName(principal.getName()).get().getId();
        order.setCustomerFK(customerFK);

        List<OrderItem> orderItems = order.getOrderItems();

        //   orderRepository.save(order);
        AtomicReference<Double> orderTotal = new AtomicReference<>((double) 0);

        List<OrderItem> finalOrderItems = orderItems;

        order.getAmountWrapper().getWrapper().forEach(product ->
                {
                    if(product.getAmount() != 0) {
                        finalOrderItems.forEach(orderItem -> {
                            if (orderItem.getProductId() == product.getProductId()) {
                                orderItem.setQuantity(product.getAmount());
                            }
                        });


           /*             double productPrice = productRepository.findById(productId).get().getPrice();
                        String orderTime = ProductConverter.time.toString();
                        OrderItem orderItem =orderItemRepository.findByProductIdAndCustFKAndOrderTime(productId,userRepository.findByUserName(principal.getName()).get().getId(),orderTime);
                        if(orderItem != null) {
                            if (product.getAmount() != 0) {
                                orderTotal.updateAndGet(v -> (double) (v + productPrice * product.getAmount()));
                                orderItem.setQuantity(product.getAmount());
                                orderItemRepository.save(orderItem);
                            }
                        }   */
                    }
                }
        );

        orderItems = orderItems.stream().filter(orderItem -> orderItem.getQuantity() != 0).collect(Collectors.toList());
        orderItems.forEach(orderItem -> orderTotal.updateAndGet(v -> (double) (v + orderItem.getQuantity() * orderItem.getPrice())));

        order.setOrderItems(orderItems);
        order.setOrderTotal(CustomerServices.round(orderTotal.get().doubleValue(),3));

        order.deleteAmountWrapper();
        orderRepository.save(order);

        return "redirect:/orderplaced";
    }


    @GetMapping("/signup")
    public String showFormSignUpCustomer(Model model) {
        model.addAttribute("customer", new CustomerSignUp());
        return "signup";
    }

    @RequestMapping(value = "/signup" , method = RequestMethod.POST)
    public String signUpNewUser(@Valid CustomerSignUp customer, BindingResult bindingResult) {
        if(!bindingResult.hasErrors()) {
            if(userRepository.findByUserName(customer.getUserName()).isPresent()) {

            }

           Users savedCustomer = customerServices.saveNewCustomer(customer);

            if(savedCustomer == null) {
                return "redirect:/error";
            }

            return "redirect:/signupsuccess";
        } else {
            return "redirect:/error";
        }
    }
}
