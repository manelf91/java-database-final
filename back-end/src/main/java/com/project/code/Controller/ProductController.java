package com.project.code.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.OrderItemRepository;
import com.project.code.Repo.ProductRepository;
import com.project.code.Service.ServiceClass;

@RequestMapping("/product")
@RestController
public class ProductController {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    @Autowired
    private ServiceClass serviceClass;

    @Autowired
    private InventoryRepository inventoryRepo;

    @PostMapping
    public Map<String, String> addProduct(@RequestBody Product product) {
        Map<String, String> map = new HashMap<>();
        if (!serviceClass.validateProduct(product)) {
            map.put("message", "Product already present in database");
            return map;
        }
        try {
            productRepo.save(product);
            map.put("message", "Product added successfully");
        } catch (DataIntegrityViolationException e) {
            map.put("message", "SKU should be unique");
        }
        return map;
    }

    @GetMapping("/product/{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        System.out.println("result: ");
        System.out.println("result: ");
        System.out.println("result: ");
        Map<String, Object> map = new HashMap<>();
        Product result = productRepo.getReferenceById(id);

        System.out.println("result: " + result);
        map.put("products", result);
        return map;
    }

    @PutMapping
    public Map<String, String> updateProduct(@RequestBody Product product) {
        Map<String, String> map = new HashMap<>();
        try {
            productRepo.save(product);
            map.put("message", "Data upated sucessfully");
        } catch (Error e) {
            map.put("message", "Error occured");
        }
        return map;
    }

    @GetMapping("/category/{name}/{category}")
    public Map<String, Object> filterByCategoryProduct(@PathVariable String name, @PathVariable String category) {
        Map<String, Object> map = new HashMap<>();

        if (name.equals("null")) {
            map.put("products", productRepo.findByCategory(category));
            return map;
        } else if (category.equals("null")) {
            map.put("products", productRepo.findProductBySubName(name));
            return map;

        }
        map.put("products", productRepo.findProductBySubNameAndCategory(name, category));
        return map;
    }

    @GetMapping
    public Map<String, Object> listProduct() {

        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepo.findAll());
        return map;
    }

    @GetMapping("filter/{category}/{storeid}")
    public Map<String, Object> getProductByCategoryAndStoreId(@PathVariable String category, @PathVariable Long storeId) {
        Map<String, Object> map = new HashMap<>();
        List<Product> result = productRepo.findProductsByCategory(storeId, category);
        map.put("product", result);
        return map;
    }

    @DeleteMapping("/{id}")
    public Map<String, String> deleteProduct(@PathVariable Long id) {
        Map<String, String> map = new HashMap<>();

        if (!serviceClass.validateProductId(id)) {
            map.put("message", "Id " + id + " not present in database");
            return map;
        }
        inventoryRepo.deleteByProductId(id);
        orderItemRepo.deleteByProductId(id);
        productRepo.deleteById(id);

        map.put("message", "Deleted product successfully with id: " + id);
        return map;
    }

    @GetMapping("/searchProduct/{name}")
    public Map<String, Object> searchProduct(@PathVariable String name) {
        Map<String, Object> map = new HashMap<>();
        map.put("products", productRepo.findProductBySubName(name));
        return map;
    }
}