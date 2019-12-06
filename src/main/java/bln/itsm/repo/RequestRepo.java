package bln.itsm.repo;

import bln.itsm.entity.Request;
import bln.itsm.entity.enums.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface RequestRepo extends JpaRepository<Request, Long> {
    Request findByRequestNumberAndStatus(String requestNumber, BatchStatusEnum status);
}
