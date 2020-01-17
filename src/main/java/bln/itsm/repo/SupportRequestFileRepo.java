package bln.itsm.repo;

import bln.itsm.entity.SupportRequestFile;
import bln.itsm.entity.enums.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface SupportRequestFileRepo extends JpaRepository<SupportRequestFile, Long> {
    List<SupportRequestFile> findByStatus(BatchStatusEnum status);
}
