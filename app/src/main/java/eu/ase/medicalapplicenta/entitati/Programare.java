package eu.ase.medicalapplicenta.entitati;

public class Programare {
    private String idMedic;
    private String idPacient;
    private String data;
    private String ora;
    private Factura factura;

    public Programare(){}

    public Programare(String idMedic, String idPacient, String data, String ora, Factura factura) {
        this.idMedic = idMedic;
        this.idPacient = idPacient;
        this.data = data;
        this.ora = ora;
        this.factura = factura;
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
