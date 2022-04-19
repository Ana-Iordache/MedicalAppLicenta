package eu.ase.medicalapplicenta.entitati;

import java.util.List;

public class Feedback {
    private String idFeedback;
    private String idMedic;
    private String idPacient;
    private int nota;
    private List<String> recenzii;

    public Feedback() {
    }

    public Feedback(String idFeedback, String idMedic, String idPacient, int nota, List<String> recenzii) {
        this.idFeedback = idFeedback;
        this.idMedic = idMedic;
        this.idPacient = idPacient;
        this.nota = nota;
        this.recenzii = recenzii;
    }

    public String getIdFeedback() {
        return idFeedback;
    }

    public void setIdFeedback(String idFeedback) {
        this.idFeedback = idFeedback;
    }

    public String getIdMedic() {
        return idMedic;
    }

    public void setIdMedic(String idMedic) {
        this.idMedic = idMedic;
    }

    public String getIdPacient() {
        return idPacient;
    }

    public void setIdPacient(String idPacient) {
        this.idPacient = idPacient;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public List<String> getRecenzii() {
        return recenzii;
    }

    public void setRecenzii(List<String> recenzii) {
        this.recenzii = recenzii;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "idFeedback='" + idFeedback + '\'' +
                ", idMedic='" + idMedic + '\'' +
                ", idPacient='" + idPacient + '\'' +
                ", nota=" + nota +
                ", recenzii=" + recenzii +
                '}';
    }
}
