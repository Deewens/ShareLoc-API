package shareloc.model.ejb;

import jakarta.persistence.*;

@Entity
public class VoteService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int voteServiceId;

    @ManyToOne(targetEntity = User.class)
    private User voter;

    @ManyToOne(targetEntity = Service.class)
    private Service service;

    private Integer voteType; // 1 = suppresion, 0 = ajout

    private boolean vote; // true = oui, false = non

    public VoteService() {}

    public VoteService(User voter, Service service, Integer voteType, boolean vote) {
        this.voter = voter;
        this.service = service;
        this.voteType = voteType;
        this.vote = vote;
    }

    public int getVoteServiceId() { return voteServiceId; }

    public User getVoter() { return this.voter; }

    public void setVoter(User voter) { this.voter = voter; }

    public Service getService() { return this.service; }

    public void setService(Service service) { this.service = service; }

    public Integer getVoteType() { return this.voteType; }

    public void setVoteType(Integer voteType) { this.voteType = voteType; }

    public boolean getVote() { return this.vote; }

    public void setVote(boolean vote) { this.vote = vote; }

}
