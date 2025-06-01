package vn.fs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.fs.entities.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query(value = "SELECT * FROM Comments where product_id = ?;", nativeQuery = true)
    public List<Comment> selectAllSaves(int productId);
    @Query(value = "SELECT * FROM Comments where user_id = ?1 and product_id =?2", nativeQuery = true)
    public Optional<Comment> SearchUser(int user_id, int productId);
}
