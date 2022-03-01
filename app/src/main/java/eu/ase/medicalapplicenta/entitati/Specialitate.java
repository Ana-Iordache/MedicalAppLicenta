package eu.ase.medicalapplicenta.entitati;

import java.util.Arrays;

public class Specialitate {
    private String idSpecialitate;
    private String denumire;
    private Investigatie[] investigatii;

    public Specialitate(){}

    public Specialitate(String idSpecialitate, String denumire, Investigatie[] investigatii) {
        this.idSpecialitate = idSpecialitate;
        this.denumire = denumire;
        this.investigatii = investigatii;
    }

    public String getDenumire() {
        return denumire;
    }

    public void setDenumire(String denumire) {
        this.denumire = denumire;
    }

    public Investigatie[] getInvestigatii() {
        return investigatii;
    }

    public void setInvestigatii(Investigatie[] investigatii) {
        this.investigatii = investigatii;
    }

    public String getIdSpecialitate() {
        return idSpecialitate;
    }

    public void setIdSpecialitate(String idSpecialitate) {
        this.idSpecialitate = idSpecialitate;
    }

    @Override
    public String toString() {
        return "Specialitate{" +
                "idSpecialitate='" + idSpecialitate + '\'' +
                ", denumire='" + denumire + '\'' +
                ", investigatii=" + Arrays.toString(investigatii) +
                '}';
    }
}
