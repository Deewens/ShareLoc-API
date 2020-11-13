package model.dao;

import model.ejb.User;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.util.Optional;

public class UserDAO extends DAO<User> {
    public UserDAO() {
        super(User.class);
    }

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
}
