package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.shop.presentation.adapter.api.ProductServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductSchemaDTO;

import java.util.List;
import java.util.Map;

import static pl.lodz.p.edu.shop.config.security.role.RoleName.EMPLOYEE;

@RequiredArgsConstructor

@RestController
@RequestMapping(ApiRoot.API_ROOT + "/categories")
@DenyAll
public class CategoryController {

    private final ProductServiceOperations productService;

    @GetMapping
    @RolesAllowed({EMPLOYEE})
    public ResponseEntity<List<String>> findAll() {
        List<String> responseBody = productService.findAllCategories();

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/name/{name}")
    @RolesAllowed({EMPLOYEE})
    public ResponseEntity<List<Map<String, Object>>> getCategorySchema(@PathVariable String name) {
        List<Map<String, Object>> responseBody = productService.findSchemaByCategoryName(name);

        return ResponseEntity.ok(responseBody);
    }

    @PostMapping
    @RolesAllowed({EMPLOYEE})
    public ResponseEntity<Void> createNewCategory(@RequestBody @Valid @NotNull ProductSchemaDTO productSchemaDTO) {
        productService.createCategory(productSchemaDTO);

        return ResponseEntity.ok().build();
    }
}
