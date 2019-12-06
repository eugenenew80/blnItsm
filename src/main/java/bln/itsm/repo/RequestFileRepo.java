package bln.itsm.repo;

import bln.itsm.entity.RequestFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface RequestFileRepo extends JpaRepository<RequestFile, Long> {
}
