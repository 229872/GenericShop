package pl.lodz.p.edu.dataaccess.repository.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.edu.dataaccess.model.Account;
import pl.lodz.p.edu.dataaccess.repository.api.AccountRepository;

@Repository
interface JpaAccountRepository extends AccountRepository, JpaRepository<Account, Long> {

}
