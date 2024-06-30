package pl.lodz.p.edu.shop.presentation.controller;

import jakarta.annotation.security.DenyAll;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.edu.shop.presentation.adapter.api.ProductServiceOperations;
import pl.lodz.p.edu.shop.presentation.dto.product.InputProductDto;
import pl.lodz.p.edu.shop.presentation.dto.product.ProductOutputDto;
import pl.lodz.p.edu.shop.presentation.dto.product.UpdateProductDto;

import java.net.URI;

import static pl.lodz.p.edu.shop.config.security.role.RoleName.*;

@RequiredArgsConstructor

@RestController
@RequestMapping(ApiRoot.API_ROOT + "/products")
@DenyAll
public class ProductController {

    private final ProductServiceOperations productService;

    @GetMapping
    @RolesAllowed({EMPLOYEE, GUEST, CLIENT})
    public ResponseEntity<Page<ProductOutputDto>> findAll(Pageable pageable) {
        Page<ProductOutputDto> responseBody = productService.findAll(pageable);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/id/{id}")
    @RolesAllowed({EMPLOYEE, GUEST, CLIENT})
    public ResponseEntity<ProductOutputDto> findById(@PathVariable("id") Long id) {
        ProductOutputDto responseBody = productService.findById(id);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/id/{id}/short")
    @RolesAllowed({EMPLOYEE, GUEST, CLIENT})
    public ResponseEntity<ProductOutputDto> findByIdShort(@PathVariable("id") Long id) {
        ProductOutputDto responseBody = productService.findByIdShort(id);

        return ResponseEntity.ok(responseBody);
    }


    @PostMapping
    @RolesAllowed({EMPLOYEE})
    public ResponseEntity<ProductOutputDto> create(@RequestBody @Valid InputProductDto productDto) {
        ProductOutputDto responseBody = productService.create(productDto);

        return ResponseEntity.created(URI.create("/id/%d".formatted(responseBody.id()))).body(responseBody);
    }

    @PutMapping("/id/{id}/archive")
    @RolesAllowed({EMPLOYEE})
    public ResponseEntity<ProductOutputDto> archive(@PathVariable("id") Long id) {
        ProductOutputDto responseBody = productService.archive(id);

        return ResponseEntity.ok(responseBody);
    }

    @PutMapping("/id/{id}")
    @RolesAllowed({EMPLOYEE})
    public ResponseEntity<ProductOutputDto> update(@PathVariable("id") Long id,
                                                   @RequestBody @Valid UpdateProductDto productDto) {
        ProductOutputDto responseBody = productService.update(id, productDto);

        return ResponseEntity.ok(responseBody);
    }
}
