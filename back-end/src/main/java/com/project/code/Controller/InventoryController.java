package com.project.code.Controller;

import com.project.code.Model.CombinedRequest;
import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequestMapping("/inventory")
@Controller
public class InventoryController {

    @Autowired
    private InventoryRepository inventoryRepo;

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ServiceClass serviceClass;

    @PutMapping
    public ResponseEntity<?> updateInventory(@RequestBody CombinedRequest request) {
        Product product = request.getProduct();
        Inventory inventory = request.getInventory();

        if (product == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Product is mandatory");
        }

        if (!serviceClass.validateProductId(product.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product does not exist");
        }

        if (inventory == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Inventory is mandatory");
        }

        if (!serviceClass.validateInventoryId(product.getId())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory does not exist");
        }

        try {
            Inventory result = serviceClass.getInventoryId(inventory);
            inventory.setId(result.getId()); //todo: test without this
            productRepo.save(product);
            inventoryRepo.save(inventory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }

        return ResponseEntity.status(HttpStatus.OK).body(inventory);
    }

    @PostMapping
    public ResponseEntity<?> saveInventory(@RequestBody Inventory inventory) {
        if (inventoryRepo.findById(inventory.getId()).isPresent()) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Inventory already exists");
        }
        Inventory saved = inventoryRepo.save(inventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<?> getAllProducts(@PathVariable Long storeId) {
        List<Product> products = productRepo.findProductsByStoreId(storeId);
        Map<String, List<Product>> map = Map.of("products", products);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @GetMapping("filter/{category}/{name}/{storeId}")
    public ResponseEntity<?> getProductName(@PathVariable String category, @PathVariable String name, @PathVariable Long storeId) {
        List<Product> products;
        if (category.equals("null")) {
            products = productRepo.findProductsByNameLike(storeId, name);
        } else if (name.equals("null")) {
            products = productRepo.findProductsByCategory(storeId, category);
        } else {
            products = productRepo.findProductsByNameAndCategory(storeId, name, category);
        }

        Map<String, List<Product>> map = Map.of("products", products);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @GetMapping("search/{name}/{storeId}")
    public ResponseEntity<?> searchProduct(@PathVariable String name, @PathVariable Long storeId) {
        List<Product> products = productRepo.findProductsByNameLike(storeId, name);
        Map<String, List<Product>> map = Map.of("products", products);
        return ResponseEntity.status(HttpStatus.OK).body(map);
    }

    @DeleteMapping("delete/{productId}")
    public ResponseEntity<?> removeProduct(@PathVariable Long productId) {
        if (!serviceClass.validateProductId(productId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product does not exist");
        }
        productRepo.deleteById(productId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("validate/{quantity}/{storeId}/{productId}")
    public ResponseEntity<?> validateQuantity(@PathVariable Integer quantity, @PathVariable Long storeId, @PathVariable Long productId) {
        Inventory inventory = inventoryRepo.findByProductIdAndStoreId(productId, storeId);
        return ResponseEntity.status(HttpStatus.OK).body(inventory.getStockLevel() >= quantity);
    }
}
