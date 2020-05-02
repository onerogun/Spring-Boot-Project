package og.prj.adminservice.orderitem;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
  //  public OrderItem findByProductIdAndCustFKAndOrderTime(Long productId,Long custFK,String orderTime);
  //  public void deleteByQuantity(int quantiy);
}
