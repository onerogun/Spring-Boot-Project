package og.prj.adminservice.reactjs;

import og.prj.adminservice.order.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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

    @RequestMapping(method = RequestMethod.DELETE, value = "/cancelorderreact/{id}/{orderid}")
    public void reactOrderCancel(HttpServletResponse response, @PathVariable Long id, @PathVariable Long orderid) throws IOException {
        System.out.println("user ID " + id + " Order id: " + orderid);
        if (orderService.cancelOrder(id, orderid) == false )
        {   response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);}
    }

}
