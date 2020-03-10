package og.prj.adminservice;

import og.prj.adminservice.customer.*;
import og.prj.adminservice.jpafiles.UserRepository;
import og.prj.adminservice.jpafiles.Users;
import og.prj.adminservice.order.OrderRepository;
import og.prj.adminservice.order.Orders;
import og.prj.adminservice.orderitem.OrderItem;
import og.prj.adminservice.orderitem.OrderItemRepository;
import og.prj.adminservice.orderitem.ProductConverter;
import og.prj.adminservice.product.AmountWrapper;
import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import og.prj.adminservice.util.PasswordConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
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
    private PasswordConfig passwordConfig;


    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerContactRepository customerContactRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @Autowired
    private OrderItemRepository orderItemRepository;


    @GetMapping(path = "/orders")
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {

        Long userId = userRepository.findByUserName(principal.getName()).get().getId();

        response.sendRedirect("http://edit-pics.s3-website-us-east-1.amazonaws.com/getcustomertoken/" + userId);
    }



    @GetMapping("/getorders/{id}")
    @ResponseBody
    public List<Orders> showOrders(@PathVariable Long id) {
        orderItemRepository.deleteByQuantity(0);
       // return customerRepository.getOne(Long.valueOf(336)).getOrdersList();
        return customerRepository.getOne(id).getOrdersList();
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



    @GetMapping("/signup")
    public String showFormSignUpCustomer(Model model) {
        model.addAttribute("customer", new CustomerSignUp());
        return "signup";
    }

    @RequestMapping(value = "/signup" , method = RequestMethod.POST)
    public String signUpNewUser(@Valid CustomerSignUp customer, BindingResult bindingResult) {
        if(!bindingResult.hasErrors()) {
            if(userRepository.findByUserName(customer.getUserName()).isPresent()) {
                return "redirect:/error";
            }


            Users newCustomer = new Users();
            newCustomer.setPassword(passwordConfig.passwordEncoder().encode(customer.getPassword()));
            newCustomer.setUserName(customer.getUserName());
            newCustomer.setActive(true);
            newCustomer.setRoles("ROLE_CUSTOMER");

            Users savedUser = userRepository.save(newCustomer);

            Customer c = new Customer();
            c.setEmail(newCustomer.getUserName() + "@ads.com");
            c.setId(savedUser.getId());
            CustomerContact c1 = new CustomerContact();
            c1.setAddress(savedUser.getUserName() + " 3243 w223");
            c1.setPhoneNumber("94568 848646");
            c1.setId(savedUser.getId());
            customerRepository.save(c);
            customerContactRepository.save(c1);

            return "redirect:/signupsuccess";
        } else {
            return "redirect:/error";
        }
    }
}
