package og.prj.adminservice.customer;

import og.prj.adminservice.jpafiles.UserRepository;
import og.prj.adminservice.jpafiles.Users;
import og.prj.adminservice.order.Orders;
import og.prj.adminservice.product.AmountWrapper;
import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import og.prj.adminservice.util.PasswordConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CustomerServices {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomerContactRepository customerContactRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PasswordConfig passwordConfig;

    @Autowired
    private CustomerRepository customerRepository;


 /*   public Orders placeOrderThymeleaf(Orders order, Principal principal) {


    }
*/
    public CustomerVO getCustomerInformation(Long id) {
        CustomerContact customer = customerContactRepository.getOne(id);

        CustomerVO customerVO = new CustomerVO();
        customerVO.setUserName(customer.getCustomer().getUsers().getUserName());
        customerVO.setId(customer.getId().intValue());
        customerVO.setAddress(customer.getAddress());
        customerVO.setEmail(customer.getCustomer().getEmail());
        customerVO.setPhoneNumber(customer.getPhoneNumber());

        return customerVO;
    }

    public Orders getAvailableProductsFromDB() {
        Orders order = new Orders();
        order.setAmountWrapper(new AmountWrapper());
        AmountWrapper amountWrapper = order.getAmountWrapper();
        Iterable<Product> productList = productRepository.findAll();
        productList.forEach(product -> amountWrapper.addProduct(product));
        order.setAmountWrapper(amountWrapper);
        return order;
    }

    public Users saveNewCustomer(CustomerSignUp customer) {

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

        return savedUser;
    }

    public void setCustomerInfo(CustomerVO customerInfo, Long id) {

        CustomerContact customerContact = customerContactRepository.getOne(id);
        customerContact.setPhoneNumber(customerInfo.getPhoneNumber());
        customerContact.setAddress(customerInfo.getAddress());

        Customer customer = customerContact.getCustomer();
        customer.setEmail(customerInfo.getEmail());
        customerContact.setCustomer(customer);

        customerContactRepository.save(customerContact);
    }
}
