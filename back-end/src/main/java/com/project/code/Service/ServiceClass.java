package com.project.code.Service;


import com.project.code.Model.Inventory;
import com.project.code.Model.Product;
import com.project.code.Repo.InventoryRepository;
import com.project.code.Repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ServiceClass {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private InventoryRepository inventoryRepo;

    public boolean validateProductId(long id) {
        return productRepo.findById(id).isPresent();
    }

    public boolean validateInventoryId(long id) {
        return inventoryRepo.findById(id).isPresent();
    }

    public Inventory getInventoryId(Inventory inventory) {
        return inventoryRepo.getReferenceById(inventory.getId());
    }

    public boolean validateProduct(Product product) {
        return !this.validateProductId(product.getId());
    }
}
