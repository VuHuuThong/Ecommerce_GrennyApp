package vn.fs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fs.entities.Faq;

@Repository
public interface FaqRepository extends JpaRepository<Faq, Long> {
}
