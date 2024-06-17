package pl.lodz.p.edu.shop.presentation.adapter.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;

import java.util.List;
import java.util.Map;

public interface ProductServiceOperations {

    List<ProductOutputDto> findAll();

    Page<ProductOutputDto> findAll(Pageable pageable);

    ProductOutputDto findById(Long id);

    List<Map<String, Object>> findSchemaByCategoryName(String name);

    ProductOutputDto create(InputProductDto product);

    ProductOutputDto update(Long id, InputProductDto newProduct);

    ProductOutputDto archive(Long id);

    List<String> findAllCategories();
}
