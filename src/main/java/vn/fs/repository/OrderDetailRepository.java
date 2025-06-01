package vn.fs.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.fs.entities.OrderDetail;

/**
 * @author DongTHD
 *
 */
@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

	@Query(value = "select * from order_details where order_id = ?;", nativeQuery = true)
	List<OrderDetail> findByOrderId(Long id);
	@Query(value = "select * from order_details where product_id = ?;", nativeQuery = true)
	List<OrderDetail> findbyProduct(Long id);
	// Statistics by product sold
    @Query(value = "SELECT p.product_name , \r\n"
    		+ "SUM(o.quantitydetail) as quantitydetail ,\r\n"
    		+ "SUM(o.quantitydetail * o.pricedetail) as sum,\r\n"
    		+ "AVG(o.pricedetail) as avg,\r\n"
    		+ "Min(o.pricedetail) as min, \r\n"
    		+ "max(o.pricedetail) as max\r\n"
    		+ "FROM order_details o\r\n"
    		+ "INNER JOIN products p ON o.product_id = p.product_id\r\n"
    		+ "GROUP BY p.product_name;", nativeQuery = true)
    public List<Object[]> repo();
    
    // Statistics by category sold
    @Query(value = "SELECT c.category_name , \r\n"
    		+ "SUM(o.quantitydetail) as quantitydetail ,\r\n"
    		+ "SUM(o.quantitydetail * o.pricedetail) as sum,\r\n"
    		+ "AVG(o.pricedetail) as avg,\r\n"
    		+ "Min(o.pricedetail) as min,\r\n"
    		+ "max(o.pricedetail) as max \r\n"
    		+ "FROM order_details o\r\n"
    		+ "INNER JOIN products p ON o.product_id = p.product_id\r\n"
    		+ "INNER JOIN categories c ON p.category_id = c.category_id\r\n"
    		+ "GROUP BY c.category_name;", nativeQuery = true)
    public List<Object[]> repoWhereCategory();
    
    // Statistics of products sold by year
    @Query(value = "Select YEAR(od.order_date) ,\r\n"
    		+ "SUM(o.quantitydetail) as quantitydetail ,\r\n"
    		+ "SUM(o.quantitydetail * o.pricedetail) as sum,\r\n"
    		+ "AVG(o.pricedetail) as avg,\r\n"
    		+ "Min(o.pricedetail) as min,\r\n"
    		+ "max(o.pricedetail) as max \r\n"
    		+ "FROM order_details o\r\n"
    		+ "INNER JOIN orders od ON o.order_id = od.order_id\r\n"
    		+ "GROUP BY YEAR(od.order_date);", nativeQuery = true)
    public List<Object[]> repoWhereYear();
    
    // Statistics of products sold by month
    @Query(value = "Select month(od.order_date) ,\r\n"
    		+ "SUM(o.quantitydetail) as quantitydetail ,\r\n"
    		+ "SUM(o.quantitydetail * o.pricedetail) as sum,\r\n"
    		+ "AVG(o.pricedetail) as avg,\r\n"
    		+ "Min(o.pricedetail) as min,\r\n"
    		+ "max(o.pricedetail) as max\r\n"
    		+ "FROM order_details o\r\n"
    		+ "INNER JOIN orders od ON o.order_id = od.order_id\r\n"
    		+ "GROUP BY month(od.order_date);", nativeQuery = true)
    public List<Object[]> repoWhereMonth();
    
    // Statistics of products sold by quarter
    @Query(value = "Select QUARTER(od.order_date),\r\n"
    		+ "SUM(o.quantitydetail) as quantitydetail ,\r\n"
    		+ "SUM(o.quantitydetail * o.pricedetail) as sum,\r\n"
    		+ "AVG(o.pricedetail) as avg,\r\n"
    		+ "Min(o.pricedetail) as min,\r\n"
    		+ "max(o.pricedetail) as max\r\n"
    		+ "FROM order_details o\r\n"
    		+ "INNER JOIN orders od ON o.order_id = od.order_id\r\n"
    		+ "GROUP By QUARTER(od.order_date);", nativeQuery = true)
    public List<Object[]> repoWhereQUARTER();
    
    // Statistics by user
    @Query(value = "SELECT c.name,\r\n"
    		+ "SUM(o.quantitydetail) as quantitydetail,\r\n"
    		+ "SUM(o.quantitydetail * o.pricedetail) as sum,\r\n"
    		+ "AVG(o.pricedetail) as avg,\r\n"
    		+ "Min(o.pricedetail) as min,\r\n"
    		+ "max(o.pricedetail) as max\r\n"
    		+ "FROM order_details o\r\n"
    		+ "INNER JOIN orders p ON o.order_id = p.order_id\r\n"
    		+ "INNER JOIN user c ON p.user_id = c.user_id\r\n"
    		+ "GROUP BY c.name;", nativeQuery = true)
    public List<Object[]> reportCustommer();

}
