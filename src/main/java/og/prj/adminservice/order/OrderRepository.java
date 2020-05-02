package og.prj.adminservice.order;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

@Transactional
public interface OrderRepository extends JpaRepository<Orders, Long> {
    public long deleteByOrderIdAndCustomerFK(Long orderId, Long customerFK);
}
