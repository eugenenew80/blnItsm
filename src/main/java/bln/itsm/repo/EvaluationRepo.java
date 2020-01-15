package bln.itsm.repo;

import bln.itsm.entity.Evaluation;
import bln.itsm.entity.enums.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EvaluationRepo extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByTransferStatus(BatchStatusEnum status);
}
