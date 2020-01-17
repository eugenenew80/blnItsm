package bln.itsm.repo;

import bln.itsm.entity.ActionFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;

@Repository
@Transactional
public interface ActionFileRepo extends JpaRepository<ActionFile, Long> {
}
