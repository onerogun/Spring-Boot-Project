package og.prj.adminservice.order;

import og.prj.adminservice.orderitem.OrderItem;
import og.prj.adminservice.product.AmountWrapper;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Orders")
@DynamicUpdate
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "order_id")
    private Long orderId;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

    @Column(name = "customer_fk")
    private Long customerFK;

    private double orderTotal;

    private String timeOfOrder;

    @Transient
    private AmountWrapper amountWrapper;

    public Orders() {
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public void deleteAmountWrapper() {this.amountWrapper.setWrapper(null);}

    public AmountWrapper getAmountWrapper() {
        return amountWrapper;
    }

    public void setAmountWrapper(AmountWrapper amountWrapper) {
        this.amountWrapper = amountWrapper;
    }

    public String getTimeOfOrder() {
        return timeOfOrder;
    }

    public void setTimeOfOrder(String timeOfOrder) {
        this.timeOfOrder = timeOfOrder;
    }

    public void addOrderItems(OrderItem orderItem) {
        orderItems.add(orderItem);
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getCustomerFK() {
        return customerFK;
    }

    public void setCustomerFK(Long customerFK) {
        this.customerFK = customerFK;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
