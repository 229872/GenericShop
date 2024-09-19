package pl.lodz.p.edu.shop.presentation.adapter.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pl.lodz.p.edu.shop.presentation.dto.preference.UserPreferencesDto;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.UpdateProductDto;

import java.util.List;

public interface ProductServiceOperations {

    List<ProductOutputDto> findAll();

    Page<ProductOutputDto> findAll(Pageable pageable);

    Page<ProductOutputDto> findByCategory(Pageable pageable, String category);

    List<ProductOutputDto> getRecommendations(String login, UserPreferencesDto userPreferencesDto);

    ProductOutputDto findById(Long id);

    ProductOutputDto findByIdShort(Long id);

    ProductOutputDto create(InputProductDto product);

    ProductOutputDto update(Long id, UpdateProductDto productWithNewData);

    ProductOutputDto archive(Long id);
}
