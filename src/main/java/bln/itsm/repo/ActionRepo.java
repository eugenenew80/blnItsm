package bln.itsm.repo;

import bln.itsm.entity.ActionRequest;
import bln.itsm.entity.enums.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface ActionRepo extends JpaRepository<ActionRequest, Long> {
    ActionRequest findByRequestNumberAndStatus(String requestNumber, BatchStatusEnum status);

    @Procedure(name = "Request.updateStatuses")
    void updateStatuses();
}
