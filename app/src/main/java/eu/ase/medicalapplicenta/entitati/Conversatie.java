package eu.ase.medicalapplicenta.entitati;

import java.util.List;

public class Conversatie {
    private String idConversatie;
    private List<Mesaj> mesaje;

    public Conversatie() {
    }

    public Conversatie(String idConversatie, List<Mesaj> mesaje) {
        this.idConversatie = idConversatie;
        this.mesaje = mesaje;
    }

    public String getIdConversatie() {
        return idConversatie;
    }

    public void setIdConversatie(String idConversatie) {
        this.idConversatie = idConversatie;
    }


    public List<Mesaj> getMesaje() {
        return mesaje;
    }

    public void setMesaje(List<Mesaj> mesaje) {
        this.mesaje = mesaje;
    }

    @Override
    public String toString() {
        return "Conversatie{" +
                "idConversatie='" + idConversatie + '\'' +
                ", mesaje=" + mesaje +
                '}';
    }
}
