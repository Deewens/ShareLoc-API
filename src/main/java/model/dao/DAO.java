package model.dao;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.Optional;

public abstract class DAO<T> {

    @PersistenceUnit(unitName = "MariaDB")
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("MariaDB");
    @PersistenceContext(unitName = "MariaDB")
    protected EntityManager em;
    private Class<T> entityClass;

    public DAO(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.em = getEntityManager();
    }

    private EntityManager getEntityManager() {
        if (em == null)
            em = emf.createEntityManager();

        return em;
    }

    public T create(T entity) {
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();

        return entity;
    }

    public void update(T entity) {
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
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