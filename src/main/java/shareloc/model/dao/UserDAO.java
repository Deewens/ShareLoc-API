package shareloc.model.dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import shareloc.model.ejb.User;

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
    public Optional<User> findByEmailAndPassword(String email, String password) {
        User user;

        Query query = em.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password");
        query.setParameter("email", email);
        query.setParameter("password", password);

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
