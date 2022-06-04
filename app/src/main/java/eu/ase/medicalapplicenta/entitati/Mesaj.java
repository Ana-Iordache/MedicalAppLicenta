package eu.ase.medicalapplicenta.entitati;

public class Mesaj {
    private String idEmitator;
    private String idReceptor;
    private String text;

    public Mesaj() {
    }

    public Mesaj(String idEmitator, String idReceptor, String text) {
        this.idEmitator = idEmitator;
        this.idReceptor = idReceptor;
        this.text = text;
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

    @Override
    public String toString() {
        return "Mesaj{" +
                "idEmitator='" + idEmitator + '\'' +
                ", idReceptor='" + idReceptor + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
