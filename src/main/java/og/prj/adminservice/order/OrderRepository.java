package og.prj.adminservice.order;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface OrderRepository extends JpaRepository<Orders, Long> {
    //return value, of type long, indicates how many records the method deleted.
    long deleteByOrderIdAndCustomerFK(Long orderId, Long customerFK);
}
