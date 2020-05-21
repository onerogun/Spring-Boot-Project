package og.prj.adminservice.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
public class CustomerContactController {

    @Autowired
    CustomerServices customerServices;

    @GetMapping("/getcustomerinfo/{id}")
    public ResponseEntity<CustomerVO> getCustomerInfo(@PathVariable Long id) {

        return ResponseEntity.ok(customerServices.getCustomerInformation(id));
    }

    @PostMapping("/getcustomerinfo/{id}")
    public void setCustomerInfo(@RequestBody CustomerVO customerInfo, @PathVariable Long id) {

        customerServices.setCustomerInfo(customerInfo, id);
    }
}
