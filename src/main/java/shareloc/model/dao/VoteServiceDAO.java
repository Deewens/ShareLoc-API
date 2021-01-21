package shareloc.model.dao;

import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import shareloc.model.ejb.Houseshare;
import shareloc.model.ejb.Service;
import shareloc.model.ejb.User;
import shareloc.model.ejb.VoteService;

import java.util.List;
import java.util.Optional;

public class VoteServiceDAO extends DAO<VoteService> {
    public VoteServiceDAO() { super(VoteService.class); }

    @Transactional
    public Optional<VoteService> findByVoter(User voter) {
        VoteService voteService;

        TypedQuery<VoteService> query = em.createQuery("SELECT vs FROM VoteService vs WHERE vs.voter = :voter", VoteService.class);
        query.setParameter("voter", voter);

        try {
            voteService = query.getSingleResult();
            return Optional.of(voteService);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public Optional<VoteService> findByVoterAndService(User voter, Service service) {
        VoteService voteService;

        TypedQuery<VoteService> query = em.createQuery("SELECT vs FROM VoteService vs WHERE vs.voter = :voter AND vs.service = :service", VoteService.class);
        query.setParameter("voter", voter);
        query.setParameter("service", service);

        try {
            voteService = query.getSingleResult();
            return Optional.of(voteService);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Transactional
    public List<VoteService> findByService(Service service) {
        TypedQuery<VoteService> query = em.createQuery("SELECT vs FROM VoteService vs WHERE vs.service = :service", VoteService.class);
        query.setParameter("service", service);
        return query.getResultList();
    }

    /*@Transactional
    public void deleteEndedVotes() {
        Query query = em.createQuery("DELETE FROM VoteService vs WHERE ")
    }*/
}
