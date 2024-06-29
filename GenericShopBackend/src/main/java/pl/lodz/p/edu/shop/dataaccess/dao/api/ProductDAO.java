package pl.lodz.p.edu.shop.dataaccess.dao.api;

import java.util.Map;

public interface ProductDAO {

    Map<String, Object> insert(String tableName, Map<String, Object> record);

    Map<String, Object> findByIdInTable(Long id, String tableName);
}
