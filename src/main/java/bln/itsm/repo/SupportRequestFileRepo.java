package bln.itsm.repo;

import bln.itsm.entity.SupportRequestFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface SupportRequestFileRepo extends JpaRepository<SupportRequestFile, Long> {
}
