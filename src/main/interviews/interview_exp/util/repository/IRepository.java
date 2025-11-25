package util.repository;

import java.util.List;

public interface IRepository<T> {
    void save(T entity);
    void update(T entity);
    void delete(T entity);
    T findById(String id);
    List<T> findAll();
}
