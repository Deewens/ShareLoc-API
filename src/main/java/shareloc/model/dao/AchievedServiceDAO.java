package shareloc.model.dao;

import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import shareloc.model.ejb.AchievedService;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;

import java.util.List;

public class AchievedServiceDAO extends DAO<AchievedService> {
    public AchievedServiceDAO() {
        super(AchievedService.class);
    }

    @Transactional
    public List<AchievedService> findByHouseshare(Houseshare houseshare) {
        TypedQuery<AchievedService> query = em.createQuery("SELECT a FROM AchievedService a WHERE a.houseshare = :houseshare", AchievedService.class);
        query.setParameter("houseshare", houseshare);
        return query.getResultList();
    }
}
