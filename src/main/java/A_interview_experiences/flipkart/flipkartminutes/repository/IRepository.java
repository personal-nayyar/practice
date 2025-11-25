package A_interview_experiences.flipkart.flipkartminutes.repository;

import java.util.List;
import java.util.Optional;

// To make use of repository design pattern
public interface IRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    List<T> findAll();
    void deleteById(ID id);
    boolean existsById(ID id);
}
