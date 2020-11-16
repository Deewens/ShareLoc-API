package shareloc.model.dao;

import shareloc.model.ejb.User;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.Optional;

public class UserDAO extends DAO<User> {
    public UserDAO() {
        super(User.class);
    }

    @Transactional
    public Optional<User> findByEmail(String email) {
        User user;

        Query query = em.createQuery("SELECT u FROM User u WHERE u.email = :email");
        query.setParameter("email", email);

        try {
            user = (User) query.getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<User> findByPseudo(String pseudo) {
        User user;

        Query query = em.createQuery("SELECT u FROM User u WHERE u.pseudo = :pseudo");
        query.setParameter("pseudo", pseudo);

        try {
            user = (User) query.getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
