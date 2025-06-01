package vn.fs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.fs.entities.Order;
import vn.fs.entities.Product;
import vn.fs.repository.ProductRepository;

import java.util.List;

@Service
public class ProductDetailService {
    @Autowired
    ProductRepository repo;
    public List<Product> listAll() {
        return (List<Product>) repo.findAll();
    }

}
