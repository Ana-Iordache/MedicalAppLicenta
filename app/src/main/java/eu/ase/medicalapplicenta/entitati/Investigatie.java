package eu.ase.medicalapplicenta.entitati;

public class Investigatie {
    private String codInvestigatie;
    private String denumire;
    private double pret; //oare merge si asa sau trb Double neaparat?

    public Investigatie(){}

    public Investigatie(String codInvestigatie, String denumire, double pret) {
        this.codInvestigatie = codInvestigatie;
        this.denumire = denumire;
        this.pret = pret;
    }

    public String getCodInvestigatie() {
        return codInvestigatie;
    }

    public void setCodInvestigatie(String codInvestigatie) {
        this.codInvestigatie = codInvestigatie;
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
                "codInvestigatie='" + codInvestigatie + '\'' +
                ", denumire='" + denumire + '\'' +
                ", pret=" + pret +
                '}';
    }
}
