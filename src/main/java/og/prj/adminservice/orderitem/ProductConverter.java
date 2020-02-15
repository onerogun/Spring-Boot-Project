package og.prj.adminservice.orderitem;

import og.prj.adminservice.CustomerResources;
import og.prj.adminservice.jpafiles.UserRepository;
import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ProductConverter implements Converter<String, OrderItem> {
    @Autowired
    ProductRepository productRepository;

    @Autowired
    UserRepository userRepository;

    public static LocalDateTime time;

    //get user Id to create and save in orderItem object
    private Long getUserId() {
        return  userRepository.findByUserName(CustomerResources.prncipal.getName()).get().getId();
    }


    @Override
    public OrderItem convert(String idS) {
        Long id = Long.parseLong(idS);
        if(time == null || LocalDateTime.now().minusSeconds(5).isAfter(time)) {
            time = LocalDateTime.now();
        }
       Product p = productRepository.findById(id).get();
       OrderItem o = new OrderItem(p.getProductId(),p.getProductName(),p.getPrice(),time.toString(), getUserId() );
       return o;
    }
}
