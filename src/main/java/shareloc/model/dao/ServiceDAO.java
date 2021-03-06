package shareloc.model.dao;

import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;

import java.util.List;

public class ServiceDAO extends DAO<Service> {
    public ServiceDAO() {
        super(Service.class);
    }

    @Transactional
    public List<Service> findByHouseshare(Houseshare houseshare) {
        TypedQuery<Service> query = em.createQuery("SELECT s FROM Service s WHERE s.houseshare = :houseshare", Service.class);
        query.setParameter("houseshare", houseshare);
        System.out.println(query.getResultList().toString());
        return query.getResultList();
    }

    @Transactional
    public List<Service> findByStatus(Houseshare houseshare, int status) {
        TypedQuery<Service> query = em.createQuery("SELECT s FROM Service s WHERE s.houseshare = :houseshare AND s.status = :status", Service.class);
        query.setParameter("houseshare", houseshare);
        query.setParameter("status", status);
        return query.getResultList();
    }
}
