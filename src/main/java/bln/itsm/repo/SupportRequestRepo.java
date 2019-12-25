package bln.itsm.repo;

import bln.itsm.entity.SupportRequest;
import bln.itsm.entity.enums.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SupportRequestRepo extends JpaRepository<SupportRequest, Long> {

    List<SupportRequest> findByStatus(BatchStatusEnum status);


    @Procedure(name = "apps.sap_interface.itsm_out_update")
    void itsmOutUpdate(@Param("p_auto_commit") Boolean autoCommit);

    @Procedure(name = "apps.sap_interface.itsm_in_update")
    void itsmInUpdate(@Param("p_auto_commit") Boolean autoCommit);
}
