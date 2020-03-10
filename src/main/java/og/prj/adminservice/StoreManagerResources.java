package og.prj.adminservice;

import og.prj.adminservice.jpafiles.UserRepository;
import og.prj.adminservice.product.Product;
import og.prj.adminservice.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.security.Principal;

@Controller
@CrossOrigin("*")
public class StoreManagerResources {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/manager")
    public String showProductForm(Product product) {
        return "addproduct";
    }

    @RequestMapping(value = "/manager", method = RequestMethod.POST, params = "action=save")
    public String addProduct(@Valid Product product, BindingResult bindingResult) throws InvalidParameterException {
        if (bindingResult.hasErrors()) {
            return "addproduct";
        } else if(productRepository.findByProductName(product.getProductName()).isPresent()) {
            return "redirect:/error";

        } else {
            productRepository.save(product);
            return "redirect:/addsuccessful";
        }
    }
/*
    @RequestMapping(value = "/manager", method = RequestMethod.POST, params = "action=delete")
    public String deleteProduct(@RequestParam Long productId) {
        if(productRepository.findById(productId).isPresent()) {
            productRepository.deleteById(productId);
            return  "redirect:/deletesuccessful";
        }
        else{
            throw new InvalidParameterException("Product doesn't exist!!");
        }
    }
*/
    @GetMapping("/products")
    public String showProducts(Model model) {
        Iterable<Product> productList = productRepository.findAll();
        model.addAttribute("productList",productList);
        return "products";
    }

    @GetMapping(value = "/deleteproduct/{id}")
    public String deleteProduct(@PathVariable Long id) {
        if(productRepository.findById(id).isPresent()) {
            productRepository.deleteById(id);
            return  "redirect:/deletesuccessful";
        }
        else{
            throw new InvalidParameterException("Product doesn't exist!!");
        }
    }

    @GetMapping(value = "/editproduct/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        model.addAttribute("product",productRepository.findById(id).get());
        return "editproduct";
    }

    @RequestMapping(value = "/editproduct", method = RequestMethod.POST, params = "action=edit")
    public String editProduct(@Valid Product product, BindingResult bindingResult) throws InvalidParameterException {
        if (bindingResult.hasErrors()) {
            return "editproduct";
        } else if(productRepository.findById(product.getProductId()).isPresent()) {
            productRepository.save(product);
            return "redirect:/editsuccessful";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping(path = "/editproductsreact")
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, Principal principal) throws IOException {

        Long userId = userRepository.findByUserName(principal.getName()).get().getId();

        response.sendRedirect("http://edit-pics.s3-website-us-east-1.amazonaws.com/gettoken/" + userId);
    }


}
