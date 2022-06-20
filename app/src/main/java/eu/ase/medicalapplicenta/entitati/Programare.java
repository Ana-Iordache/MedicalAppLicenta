package eu.ase.medicalapplicenta.entitati;

public class Programare {
    private String idProgramare;
    private String idMedic;
    private String idPacient;
    private String data;
    private String ora;
    private String status;
    private Factura factura;
    private Feedback feedback;
    private String urlReteta;

    public Programare() {
    }

    public Programare(String idProgramare, String idMedic, String idPacient, String data, String ora, String status,
                      Factura factura, Feedback feedback, String urlReteta) {
        this.idProgramare = idProgramare;
        this.idMedic = idMedic;
        this.idPacient = idPacient;
        this.data = data;
        this.ora = ora;
        this.status = status;
        this.factura = factura;
        this.feedback = feedback;
        this.urlReteta = urlReteta;
    }

    public String getIdProgramare() {
        return idProgramare;
    }

    public void setIdProgramare(String idProgramare) {
        this.idProgramare = idProgramare;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getOra() {
        return ora;
    }

    public void setOra(String ora) {
        this.ora = ora;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Factura getFactura() {
        return factura;
    }

    public void setFactura(Factura factura) {
        this.factura = factura;
    }

    public Feedback getFeedback() {
        return feedback;
    }

    public void setFeedback(Feedback feedback) {
        this.feedback = feedback;
    }

    public String getUrlReteta() {
        return urlReteta;
    }

    public void setUrlReteta(String urlReteta) {
        this.urlReteta = urlReteta;
    }

    @Override
    public String toString() {
        return "Programare{" +
                "idProgramare='" + idProgramare + '\'' +
                ", idMedic='" + idMedic + '\'' +
                ", idPacient='" + idPacient + '\'' +
                ", data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                ", status='" + status + '\'' +
                ", factura=" + factura +
                ", feedback=" + feedback +
                ", urlReteta='" + urlReteta + '\'' +
                '}';
    }
}
