package shareloc.model.dao;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public abstract class DAO<T> {
    @PersistenceContext(unitName = "MariaDB")
    protected EntityManager em;
    private final Class<T> entityClass;

    public DAO(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Transactional
    public T create(T entity) {
        em.persist(entity);

        return entity;
    }

    @Transactional
    public T update(T entity) {
        em.merge(entity);

        return entity;
    }

    public void delete(T entity) {
        em.remove(em.merge(entity));
    }

    public Optional<T> findById(Object id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public List<T> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);

        cq.select(root);
        return em.createQuery(cq).getResultList();
    }
}