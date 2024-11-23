package ca.gbc.productservice.controller;

import ca.gbc.productservice.dto.ProductRequest;
import ca.gbc.productservice.dto.ProductResponse;
import ca.gbc.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // CREATE
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest) {

        // The @RequestBody annotation indicates that the request body contains a ProductRequest object.
        ProductResponse createdProduct = productService.createProduct(productRequest);

        // Set the headers (e.g., Location header if you want to indicate the URL of the created resource)
        HttpHeaders headers = new HttpHeaders ();
        headers.add("Location", "/api/product/" + createdProduct.id());

        // Return the ResponseEntity with the 201 Created status, response body, and headers.
        return ResponseEntity
                .status(HttpStatus.CREATED) // Set status to 201
                .headers(headers)
                .contentType(MediaType.APPLICATION_JSON) // Set content type to JSON
                .body(createdProduct); // Return the created product in the response body

    }



    // READ
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.getAllProducts();
    }

    // UPDATE
    @PutMapping("/{productId}")
    // @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> updateProduct(@PathVariable("productId") String productId,
                                           @RequestBody ProductRequest productRequest) {

        String updatedProductId = productService.updateProduct(productId, productRequest);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/api/product/" + updatedProductId); // Another way to add HTTP STATUS HEADER

        return new ResponseEntity<>(headers, HttpStatus.NO_CONTENT);
    }

    // DELETE
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable("productId") String productId) {
        productService.deleteProduct(productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}