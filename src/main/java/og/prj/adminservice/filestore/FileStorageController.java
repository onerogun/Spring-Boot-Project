package og.prj.adminservice.filestore;

import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/getproducts")
@CrossOrigin("*")
public class FileStorageController {

    @Autowired
    private ProductRepository productRepository;


    private FileStorageService fileStorageService;

    @Autowired
    public FileStorageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }





    @GetMapping
    public List<Product> getProductFromDB() {
        List<Product> productList = new ArrayList<>();
        productRepository.findAll().forEach(m -> productList.add(m));
        return productList;

    }

    @PostMapping(path = "saveimage/{productId}",
                consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadProductImage(@PathVariable("productId") Long id,
                                   @RequestParam("file")MultipartFile file) throws IOException {

        fileStorageService.saveToBucket(id, file);
    }

    @GetMapping(path = "getimage/{productId}")
    public byte[] downloadProductImage(@PathVariable("productId") Long id) {
        return fileStorageService.downloadFromBucket(id);
    }
}
