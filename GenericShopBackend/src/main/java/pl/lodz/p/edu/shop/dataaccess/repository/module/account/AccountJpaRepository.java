package pl.lodz.p.edu.shop.dataaccess.repository.module.account;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.repository.api.AccountRepository;

@Repository
interface AccountJpaRepository extends JpaRepository<Account, Long>, AccountRepository {

    @Query("SELECT a FROM Account a JOIN a.contact c ORDER BY c.firstName ASC")
    Page<Account> findAllSortedByContactFirstName(Pageable pageable);
}
