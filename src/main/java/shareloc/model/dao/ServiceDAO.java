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
        return query.getResultList();
    }
}
