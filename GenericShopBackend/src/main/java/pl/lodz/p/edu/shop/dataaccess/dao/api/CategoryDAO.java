package pl.lodz.p.edu.shop.dataaccess.dao.api;

import pl.lodz.p.edu.shop.dataaccess.model.other.Constraint;

import java.util.List;
import java.util.Map;

public interface CategoryDAO {

    Map<String, List<Constraint>> createTable(String tableName, Map<String, List<Constraint>> schema);

    List<Map<String, Object>> findTableSchema(String tableName);
}
