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

    /**
     * Récupère les servies achevés que l'utilisateur a fait pour les autres de tout les utilisateurs de la colocation
     *
     * @param houseshare
     * @param isValid
     * @return
     */
    @Transactional
    public int countFrom(Houseshare houseshare, User user, boolean isValid) {
        Query query = em.createQuery("SELECT COUNT(a) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.from = :from AND a.valid = :valid AND a.service = :service GROUP BY a.date");
        query.setParameter("houseshare", houseshare);
        query.setParameter("from", user);
        query.setParameter("valid", isValid);

        Query query = em.createQuery("SELECT COUNT(a) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.to = :to AND a.valid = :valid GROUP BY a.date, a.service");
        query.setParameter("houseshare", houseshare);
        query.setParameter("to", user);
        query.setParameter("valid", isValid);

        System.out.println("Query result: " + query.getSingleResult());
        return ((BigDecimal) query.getSingleResult()).intValue();
    }


    /**
     * Récupère les servies achevés que l'utilisateur a fait pour les autres de tout les utilisateurs de la colocation
     *
     * @param houseshare
     * @param isValid
     * @return
     */
    @Transactional
    public List<AchievedService> countPositivePoints(Houseshare houseshare, boolean isValid) {
        TypedQuery<AchievedService> query = em.createQuery("SELECT a.from, COALESCE(SUM(a.service.cost), 0) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.valid = :valid GROUP BY a.from, a.date", AchievedService.class);
        query.setParameter("houseshare", houseshare);
        query.setParameter("valid", isValid);
        System.out.println(query.getResultList());
        return query.getResultList();
    }

    /**
     * Récupère les servies achevés dont à profité l'utilisateur de tout les utilisateurs de la colocation
     *
     * @param houseshare
     * @param isValid
     * @return
     */
    @Transactional
    public List<AchievedService> countNegativePoints(Houseshare houseshare, boolean isValid) {
        TypedQuery<AchievedService> query = em.createQuery("SELECT a.to, COALESCE(SUM(a.service.cost), 0) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.valid = :valid GROUP BY a.to, a.date", AchievedService.class);
        query.setParameter("houseshare", houseshare);
        query.setParameter("valid", isValid);
        System.out.println(query.getResultList());
        return query.getResultList();
    }

    /**
     * Récupère les servies achevés que l'utilisateur a fait pour les autres
     *
     * @param houseshare
     * @param user
     * @param isValid
     * @return
     */
    @Transactional
    public int countPositivePointsByUser(Houseshare houseshare, User user, boolean isValid) {
        Query query = em.createQuery("SELECT COALESCE(SUM(a.service.cost), 0) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.from = :from AND a.valid = :valid");
        query.setParameter("houseshare", houseshare);
        query.setParameter("from", user);
        query.setParameter("valid", isValid);

        System.out.println("Query result: " + query.getSingleResult());
        return ((BigDecimal) query.getSingleResult()).intValue();
    }

    /**
     * Récupère les servies achevés dont à profité l'utilisateur
     *
     * @param houseshare
     * @param user
     * @param isValid
     * @return
     */
    @Transactional
    public int countNegativePointsByUser(Houseshare houseshare, User user, boolean isValid) {
        Query query = em.createQuery("SELECT COALESCE(SUM(a.service.cost), 0) FROM AchievedService a WHERE a.houseshare = :houseshare AND a.to = :to AND a.valid = :valid");
        query.setParameter("houseshare", houseshare);
        query.setParameter("to", user);
        query.setParameter("valid", isValid);

        System.out.println("Query result: " + query.getSingleResult());
        return ((BigDecimal) query.getSingleResult()).intValue();
    }
}
