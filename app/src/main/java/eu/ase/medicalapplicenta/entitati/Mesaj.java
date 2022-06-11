package eu.ase.medicalapplicenta.entitati;

public class Mesaj {
    private String idEmitator;
    private String idReceptor;
    private String text;
    private boolean mesajCitit;

    public Mesaj() {
    }

    public Mesaj(String idEmitator, String idReceptor, String text, boolean mesajCitit) {
        this.idEmitator = idEmitator;
        this.idReceptor = idReceptor;
        this.text = text;
        this.mesajCitit = mesajCitit;
    }

    public String getIdEmitator() {
        return idEmitator;
    }

    public void setIdEmitator(String idEmitator) {
        this.idEmitator = idEmitator;
    }

    public String getIdReceptor() {
        return idReceptor;
    }

    public void setIdReceptor(String idReceptor) {
        this.idReceptor = idReceptor;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isMesajCitit() {
        return mesajCitit;
    }

    public void setMesajCitit(boolean mesajCitit) {
        this.mesajCitit = mesajCitit;
    }

    @Override
    public String toString() {
        return "Mesaj{" +
                "idEmitator='" + idEmitator + '\'' +
                ", idReceptor='" + idReceptor + '\'' +
                ", text='" + text + '\'' +
                ", mesajCitit=" + mesajCitit +
                '}';
    }
}
