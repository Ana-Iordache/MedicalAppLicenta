package eu.ase.medicalapplicenta.entitati;

public class Programare {
    private String idProgramare;
    private String idMedic;
    private String idPacient;
    private String data;
    private String ora;
    private String status;
    private Factura factura;

    public Programare(){}

    public Programare(String idProgramare, String idMedic, String idPacient, String data, String ora, String status, Factura factura) {
        this.idProgramare = idProgramare;
        this.idMedic = idMedic;
        this.idPacient = idPacient;
        this.data = data;
        this.ora = ora;
        this.status = status;
        this.factura = factura;
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

    @Override
    public String toString() {
        return "Programare{" +
                "idMedic='" + idMedic + '\'' +
                ", idPacient='" + idPacient + '\'' +
                ", data='" + data + '\'' +
                ", ora='" + ora + '\'' +
                ", factura=" + factura +
                '}';
    }
}
