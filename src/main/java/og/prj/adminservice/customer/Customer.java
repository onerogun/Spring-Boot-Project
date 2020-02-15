package og.prj.adminservice.customer;

import og.prj.adminservice.jpafiles.Users;
import og.prj.adminservice.order.Orders;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Customer")
public class Customer {

    @Id
    @Column(name = "id")
    private Long id;

    private String email;

    @OneToMany
    @JoinColumn(name = "customer_fk")
    private List<Orders> ordersList;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id")
    private Users users;


    public Customer() {
    }

    public List<Orders> getOrdersList() {
        return ordersList;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOrdersList(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }
}
