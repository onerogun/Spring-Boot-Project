package og.prj.adminservice.reactjs;

import og.prj.adminservice.order.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@CrossOrigin("*")
public class ReactOrderPlaceController {


    @Autowired
    private OrderService orderService;

    @RequestMapping(method = RequestMethod.POST, value = "/placeorderreact/{id}")
    public void reactOrderPlace(@RequestBody OrderArray[] orderArray, @PathVariable Long id)
    {

        Orders placedOrder = orderService.placeOrder(orderArray, id);


        System.out.println("Saved Order: " + placedOrder.toString() + "id: " + placedOrder.getOrderId());

    }

}
