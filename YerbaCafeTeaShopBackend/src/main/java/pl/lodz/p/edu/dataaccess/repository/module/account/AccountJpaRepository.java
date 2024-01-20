package pl.lodz.p.edu.dataaccess.repository.module.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.edu.dataaccess.model.entity.Account;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;

@Repository
interface AccountJpaRepository extends JpaRepository<Account, Long>, AccountRepository {
}
