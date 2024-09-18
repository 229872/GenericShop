package pl.lodz.p.edu.shop.dataaccess.repository.module.order;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;
import pl.lodz.p.edu.shop.dataaccess.repository.api.ReadOnlyAccountRepository;

interface ROAccountJpaRepository extends JpaRepository<Account, Long>, ReadOnlyAccountRepository {
}
