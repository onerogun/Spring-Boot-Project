package og.prj.adminservice.orderitem;

import og.prj.adminservice.order.Orders;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "OrderItem")
@DynamicUpdate
public class OrderItem {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "orderitem_id")
        private Long id;

        private Long productId;
        private String productName;
        private double price;
        private int quantity;

        private String orderTime;

        @Column(name = "cust_fk")
        private Long custFK;

        @ManyToOne
        @JoinColumn(name = "order_id")
        private Orders orders;

    public Long getCustFK() {
        return custFK;
    }

    public void setCustFK(Long custFK) {
        this.custFK = custFK;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public OrderItem() {
    }

    public OrderItem(Long productId,String productName, double price,String orderTime,Long custFK) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.orderTime =orderTime;
        this.custFK =custFK;
    }

    public OrderItem(Long id, Long productId,String productName, double price,String orderTime) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.orderTime =orderTime;
    }

    public OrderItem(Long id, int quantity, Long custFK) {
        this.id = id;
        this.quantity = quantity;
        this.custFK = custFK;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
