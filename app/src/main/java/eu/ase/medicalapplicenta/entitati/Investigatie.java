package eu.ase.medicalapplicenta.entitati;

public class Investigatie {
    private String denumire;
    private double pret;

    public Investigatie(){}

    public Investigatie(String denumire, double pret) {
        this.denumire = denumire;
        this.pret = pret;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public double getPret() {
        return pret;
    }

    public void setPret(double pret) {
        this.pret = pret;
    }

    @Override
    public String toString() {
        return "Investigatie{" +
                "denumire='" + denumire + '\'' +
                ", pret=" + pret +
                '}';
    }
}
