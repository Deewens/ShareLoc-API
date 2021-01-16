package shareloc.model.dao;

import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Message;

import java.util.List;

public class MessageDAO extends DAO<Message>{
    public MessageDAO () { super(Message.class); }

    @Transactional
    public List<Message> findByHouseshare(Houseshare houseshare) {
        TypedQuery<Message> query = em.createQuery("SELECT m FROM Message m WHERE m.houseshare = :houseshare", Message.class);
        query.setParameter("houseshare", houseshare);
        return query.getResultList();
    }
}
