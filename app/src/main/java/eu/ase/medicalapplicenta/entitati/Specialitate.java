package eu.ase.medicalapplicenta.entitati;

import java.util.Arrays;
import java.util.List;

public class Specialitate {
    private String idSpecialitate;
    private String denumire;
    private List<Investigatie> investigatii;

    public Specialitate(){}

    public Specialitate(String idSpecialitate, String denumire, List<Investigatie> investigatii) {
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

    public List<Investigatie> getInvestigatii() {
        return investigatii;
    }

    public void setInvestigatii(List<Investigatie> investigatii) {
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
                ", investigatii=" + investigatii +
                '}';
    }
}
