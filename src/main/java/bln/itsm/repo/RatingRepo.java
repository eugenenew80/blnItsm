package bln.itsm.repo;

import bln.itsm.entity.Rating;
import bln.itsm.entity.enums.BatchStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface RatingRepo extends JpaRepository<Rating, Long> {
    List<Rating> findByTransferStatus(BatchStatusEnum status);
}
