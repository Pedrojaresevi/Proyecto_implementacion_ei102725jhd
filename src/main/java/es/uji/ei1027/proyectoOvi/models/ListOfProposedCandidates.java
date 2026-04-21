package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class ListOfProposedCandidates {
    private String request_Id;
    private String pap_patiID;
    private float sutainabilityScore;
    private Date proposalDate;

    public String getRequest_Id() {
        return request_Id;
    }

    public void setRequest_Id(String request_Id) {
        this.request_Id = request_Id;
    }

    public String getPap_patiID() {
        return pap_patiID;
    }

    public void setPap_patiID(String pap_patiID) {
        this.pap_patiID = pap_patiID;
    }

    public float getSutainabilityScore() {
        return sutainabilityScore;
    }

    public void setSutainabilityScore(float sutainabilityScore) {
        this.sutainabilityScore = sutainabilityScore;
    }

    public Date getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(Date proposalDate) {
        this.proposalDate = proposalDate;
    }

    @Override
    public String toString() {
        return "ListOfProposedCandidates{" +
                "request_Id='" + request_Id + '\'' +
                ", pap_patiID='" + pap_patiID + '\'' +
                ", sutainabilityScore=" + sutainabilityScore +
                ", proposalDate=" + proposalDate +
                '}';
    }
}
