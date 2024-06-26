package pl.lodz.p.edu.shop.dataaccess.repository.module.init;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.lodz.p.edu.shop.dataaccess.model.entity.Account;

@Repository
interface InitSchemaRepository extends JpaRepository<Account, Long> {

}
