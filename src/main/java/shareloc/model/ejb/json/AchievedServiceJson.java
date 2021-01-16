package shareloc.model.ejb.json;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.XmlRootElement;
import shareloc.model.ejb.AchievedService;
import shareloc.model.validation.customconstraints.Date;
import shareloc.model.validation.groups.AchievedServiceConstraints;

@XmlRootElement
public class AchievedServiceJson {
    @NotNull(groups = { AchievedServiceConstraints.CreateAchievedServiceConstraint.class })
    private Integer serviceId;

    @NotNull(groups = { AchievedServiceConstraints.CreateAchievedServiceConstraint.class })
    private Integer beneficiaryId;

    @Date(groups = AchievedServiceConstraints.CreateAchievedServiceConstraint.class )
    private String date;

    private String picture;

    public AchievedServiceJson() {}

    public AchievedServiceJson(Integer serviceId, Integer beneficiaryId, String date, String picture) {
        this.serviceId = serviceId;
        this.beneficiaryId = beneficiaryId;
        this.date = date;
        this.picture = picture;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Integer getBeneficiaryId() {
        return beneficiaryId;
    }

    public void setBeneficiaryId(Integer beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }
}
