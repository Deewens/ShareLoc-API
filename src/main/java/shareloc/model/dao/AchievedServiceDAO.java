package shareloc.model.dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import shareloc.model.ejb.AchievedService;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;
import shareloc.model.ejb.User;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class AchievedServiceDAO extends DAO<AchievedService> {
    public AchievedServiceDAO() {
        super(AchievedService.class);
    }

    @Transactional
    public List<AchievedService> findByHouseshare(Houseshare houseshare) {
        TypedQuery<AchievedService> query = em.createQuery("SELECT a FROM AchievedService a WHERE a.houseshare = :houseshare ORDER BY a.date, a.service.serviceId ASC", AchievedService.class);
        query.setParameter("houseshare", houseshare);
        return query.getResultList();
    }

    @Transactional
    public List<AchievedService> findByHouseshareAndTo(Houseshare houseshare, User user) {
        TypedQuery<AchievedService> query = em.createQuery("SELECT a FROM AchievedService a WHERE a.houseshare = :houseshare AND a.to = :user ORDER BY a.date, a.service.serviceId ASC", AchievedService.class);
        query.setParameter("houseshare", houseshare);
        query.setParameter("user", user);
        return query.getResultList();
    }

    @Transactional
    public List<AchievedService> findByHouseshareAndFrom(Houseshare houseshare, User user) {
        TypedQuery<AchievedService> query = em.createQuery("SELECT a FROM AchievedService a WHERE a.houseshare = :houseshare AND a.from = :user ORDER BY a.date, a.service.serviceId ASC", AchievedService.class);
        query.setParameter("houseshare", houseshare);
        query.setParameter("user", user);
        return query.getResultList();
    }


    /**
     * Calcul le nombre de point d'un utilisateur en fonction de la co-location
     *
     * @param houseshare co-location
     * @param user membre de la co-location
     * @param isValid true = service validé, false = service non validé
     * @return points calculés
     */
    @Transactional
    public int getPointsByUser(Houseshare houseshare, User user, boolean isValid) {
        Query queryPositive = em.createQuery("SELECT COALESCE(SUM(a.service.cost), 0) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.from = :from AND a.valid = :valid"); // positive
        queryPositive.setParameter("houseshare", houseshare);
        queryPositive.setParameter("from", user);
        queryPositive.setParameter("valid", isValid);

        Query queryNegative = em.createQuery("SELECT COALESCE(SUM(a.service.cost), 0) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.to = :to AND a.valid = :valid"); // negative
        queryNegative.setParameter("houseshare", houseshare);
        queryNegative.setParameter("to", user);
        queryNegative.setParameter("valid", isValid);

        int positivePoints = ((BigDecimal) queryPositive.getSingleResult()).intValue();
        int negativePoints = ((BigDecimal) queryNegative.getSingleResult()).intValue();

        System.out.println("Query result: " + (positivePoints-negativePoints));
        return positivePoints - negativePoints;
    }
}
