package A_interview_experiences.flipkart.flipkartminutes.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

// Repository design pattern
public abstract class InMemoryRepository<T, ID> implements IRepository<T, ID> {
    protected final Map<ID, T> store = new ConcurrentHashMap<>();

    @Override
    public T save(T entity) {
        store.put(getId(entity), entity);
        return entity;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(ID id) {
        store.remove(id);
    }

    @Override
    public boolean existsById(ID id) {
        return store.containsKey(id);
    }

    // Each subclass tells how to extract the ID
    protected abstract ID getId(T entity);
}