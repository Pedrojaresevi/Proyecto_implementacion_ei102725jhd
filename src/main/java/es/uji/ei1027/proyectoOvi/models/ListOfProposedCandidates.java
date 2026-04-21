package es.uji.ei1027.proyectoOvi.models;

import java.util.Date;

public class ListOfProposedCandidates {
    private String list_id;
    private float suitabilityScore;
    private Date proposalDate;
    private String pappati_id;
    private String request_id;

    public ListOfProposedCandidates() {
    }

    public String getList_id() {
        return list_id;
    }

    public void setList_id(String list_id) {
        this.list_id = list_id;
    }

    public float getSuitabilityScore() {
        return suitabilityScore;
    }

    public void setSuitabilityScore(float suitabilityScore) {
        this.suitabilityScore = suitabilityScore;
    }

    public Date getProposalDate() {
        return proposalDate;
    }

    public void setProposalDate(Date proposalDate) {
        this.proposalDate = proposalDate;
    }

    public String getPappati_id() {
        return pappati_id;
    }

    public void setPappati_id(String pappati_id) {
        this.pappati_id = pappati_id;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    @Override
    public String toString() {
        return "ListOfProposedCandidates{" +
                "list_id='" + list_id + '\'' +
                ", suitabilityScore=" + suitabilityScore +
                ", proposalDate=" + proposalDate +
                ", pappati_id='" + pappati_id + '\'' +
                ", request_id='" + request_id + '\'' +
                '}';
    }
}
