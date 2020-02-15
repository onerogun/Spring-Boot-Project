package og.prj.adminservice;

import og.prj.adminservice.customer.Customer;
import og.prj.adminservice.customer.CustomerContact;
import og.prj.adminservice.customer.CustomerContactRepository;
import og.prj.adminservice.customer.CustomerRepository;
import og.prj.adminservice.jpafiles.UserRepository;
import og.prj.adminservice.jpafiles.Users;
import og.prj.adminservice.util.PasswordConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.validation.Valid;
import java.security.InvalidParameterException;
import java.util.List;

@Controller
public class AdminResources implements WebMvcConfigurer {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CustomerContactRepository customerContactRepository;

    @Autowired
    private PasswordConfig passwordConfig;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/addsuccessful").setViewName("addsuccess");
        registry.addViewController("/home").setViewName("home");
        registry.addViewController("/deletesuccessful").setViewName("deletesuccess");
        registry.addViewController("/orderplaced").setViewName("ordersuccess");
        registry.addViewController("/error").setViewName("error");
        registry.addViewController("/emptyorder").setViewName("emptyorder");
        registry.addViewController("/accessdenied").setViewName("accessdenied");
        registry.addViewController("/logoutsuccessful").setViewName("logoutsuccess");
        registry.addViewController("/editsuccessful").setViewName("editsuccess");
        registry.addViewController("/uploadStatus").setViewName("uploadStatus");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }

    @GetMapping("/")
    public String home() {return "redirect:/home";}

    @GetMapping("/login")
    public String login(){return "login";}

    @PostMapping("/logout")
    public String logout(){return "redirect:/logout";}


    @GetMapping("/admin")
    public String showForm(Users users) {
        return "adduser";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "action=save")
    public String checkPersonInfo(@Valid Users users, BindingResult bindingResult) throws InvalidParameterException{
        if (bindingResult.hasErrors()) {
            return "form";
        } else if(userRepository.findByUserName(users.getUserName()).isPresent()) {


        } else {
            users.setPassword(passwordConfig.passwordEncoder().encode(users.getPassword()));
            userRepository.save(users);
            Long id = userRepository.findByUserName(users.getUserName()).get().getId();

            if(users.getRoles().equals("ROLE_CUSTOMER")) {
                Customer c = new Customer();
                c.setEmail(users.getUserName() + "@ads.com");
                c.setId(id);
                CustomerContact c1 = new CustomerContact();
                c1.setAddress(users.getUserName() + " 3243 w223");
                c1.setPhoneNumber("94568 848646");
                c1.setId(id);
                customerRepository.save(c);
                customerContactRepository.save(c1);
            }
        }

        return "redirect:/addsuccessful";
    }
/*
    @RequestMapping(value = "/admin", method = RequestMethod.POST, params = "action=delete")
    public String deleteUser(@RequestParam Long id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return  "redirect:/deletesuccessful";
        }
        else{
            throw new InvalidParameterException("User doesn't exist!!");
        }
    }
*/
    @GetMapping(value = "/deleteuser/{id}")
    public String deleteUser(@PathVariable Long id) {
        if(userRepository.findById(id).isPresent()) {
            userRepository.deleteById(id);
            return  "redirect:/deletesuccessful";
        }
        else{
            throw new InvalidParameterException("User doesn't exist!!");
        }
    }

    @GetMapping("/admin/show")
    public String showUsers(Model model) {
        List<Users> userList =  userRepository.findAll();
        model.addAttribute("userList",userList);
        return "userrecords";
    }

    @GetMapping(value = "/edituser/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        model.addAttribute("users", userRepository.findById(id).get());
        return "edituser";
    }

    @RequestMapping(value = "/edituser", method = RequestMethod.POST, params = "action=edit")
    public String editUser(@Valid Users users, BindingResult bindingResult) throws InvalidParameterException {
        if (bindingResult.hasErrors()) {
            return "edituser";
        } else if(userRepository.findById(users.getId()).isPresent()) {
            userRepository.save(users);
            return "redirect:/editsuccessful";
        } else {
            return "redirect:/error";
        }
    }


}
