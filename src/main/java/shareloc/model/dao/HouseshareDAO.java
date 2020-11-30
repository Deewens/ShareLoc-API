package shareloc.model.dao;

import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.User;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Optional;

public class HouseshareDAO extends DAO<Houseshare> {
    public HouseshareDAO() {
        super(Houseshare.class);
    }

    @Transactional
    public Optional<Houseshare> findByName(String name) {
        Houseshare houseshare;

        Query query = em.createQuery("SELECT h FROM Houseshare h WHERE h.name = :name");
        query.setParameter("name", name);

        try {
            houseshare = (Houseshare) query.getSingleResult();
            return Optional.of(houseshare);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
