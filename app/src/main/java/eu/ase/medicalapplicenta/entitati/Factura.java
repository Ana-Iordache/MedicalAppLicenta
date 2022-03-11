package eu.ase.medicalapplicenta.entitati;

public class Factura {
    private double valoare;
    private String dataEmiterii; //data curenta
    private String dataScadenta;
    private String status;

    public Factura(){}

    public Factura(double valoare, String dataEmiterii, String dataScadenta, String status) {
        this.valoare = valoare;
        this.dataEmiterii = dataEmiterii;
        this.dataScadenta = dataScadenta;
        this.status = status;
    }

    public double getValoare() {
        return valoare;
    }

    public void setValoare(double valoare) {
        this.valoare = valoare;
    }

    public String getDataEmiterii() {
        return dataEmiterii;
    }

    public void setDataEmiterii(String dataEmiterii) {
        this.dataEmiterii = dataEmiterii;
    }

    public String getDataScadenta() {
        return dataScadenta;
    }

    public void setDataScadenta(String dataScadenta) {
        this.dataScadenta = dataScadenta;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Factura{" +
                "valoare=" + valoare +
                ", dataEmiterii='" + dataEmiterii + '\'' +
                ", dataScadenta='" + dataScadenta + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
