package og.prj.adminservice.reactjs;

import og.prj.adminservice.customer.CustomerServices;
import og.prj.adminservice.order.OrderRepository;
import og.prj.adminservice.order.Orders;
import og.prj.adminservice.orderitem.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Orders placeOrder(OrderArray[] orderArray,Long id) {

        LocalDateTime timenow = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = timenow.format(formatter);

        AtomicReference<Double> orderTotal = new AtomicReference<>((double) 0);
        System.out.println("Placed Order: " + Arrays.asList(orderArray).toString());
        List<OrderItem> orderItems = Arrays.stream(orderArray).map(orderItem -> {
            orderTotal.updateAndGet(v -> (double) (v + orderItem.getPrice() * orderItem.getAmount()));
            return   new OrderItem(orderItem.getId(), orderItem.getName(), orderItem.getPrice()
                    , formattedDateTime, id, orderItem.getAmount());


        }).collect(Collectors.toList());

        Orders orders = new Orders();
        orders.setOrderItems(orderItems);
        orders.setTimeOfOrder(formattedDateTime);
        orders.setCustomerFK(id);
        Double orderTotalFixed = CustomerServices.round(orderTotal.get() * 1.1, 2);

        orders.setOrderTotal(orderTotalFixed);
        Orders savedOrder = orderRepository.save(orders);

        return savedOrder;
    }

    public boolean cancelOrder(Long userId, Long orderId) {
        return orderRepository.deleteByOrderIdAndCustomerFK(orderId, userId) == 0 ? false : true;
    }
}
