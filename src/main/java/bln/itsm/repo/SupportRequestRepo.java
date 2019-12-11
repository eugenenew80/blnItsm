package bln.itsm.repo;

import bln.itsm.entity.SupportRequest;
import bln.itsm.entity.enums.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SupportRequestRepo extends JpaRepository<SupportRequest, Long> {
    List<SupportRequest> findByStatus(BatchStatusEnum status);
}
