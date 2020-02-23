package og.prj.adminservice.androidservices;

import og.prj.adminservice.customer.CustomerRepository;
import og.prj.adminservice.filestore.FileStorageService;
import og.prj.adminservice.order.Orders;
import og.prj.adminservice.orderitem.OrderItemRepository;
import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AndroidResources {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/getjson")
    public Iterable<Product> showJsonOrders() {
        return productRepository.findAll();
    }

    @GetMapping(path = "/getimageforjson/{productId}")
    public byte[] downloadProductImage(@PathVariable("productId") Long id) {
        return fileStorageService.downloadFromBucket(id);
    }
}
