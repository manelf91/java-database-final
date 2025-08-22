package com.project.code.Repo;


import com.project.code.Model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    List<Product> findByName(String name);

    List<Product> findBySku(String sku);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId")
    List<Product> findProductsByStoreId(Long storeId);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findProductsByNameLike(Long storeId, String name);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category")
    List<Product> findProductsByCategory(Long storeId, String category);

    @Query("SELECT i.product FROM Inventory i WHERE i.store.id = :storeId AND i.product.category = :category AND LOWER(i.product.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findProductsByNameAndCategory(Long storeId, String name, String category);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Product> findProductBySubName(String name);

    @Query("SELECT i FROM Product i WHERE LOWER(i.name) LIKE LOWER(CONCAT('%', :name, '%')) AND i.category = :category")
    List<Product> findProductBySubNameAndCategory(String name, String category);
}
