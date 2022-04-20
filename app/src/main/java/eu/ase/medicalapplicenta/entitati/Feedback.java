package eu.ase.medicalapplicenta.entitati;

public class Feedback {
    private int nota;
    private String recenzie;

    public Feedback() {
    }

    public Feedback(int nota, String recenzie) {
        this.nota = nota;
        this.recenzie = recenzie;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getRecenzie() {
        return recenzie;
    }

    public void setRecenzii(String recenzie) {
        this.recenzie = recenzie;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "nota=" + nota +
                ", recenzie='" + recenzie + '\'' +
                '}';
    }
}
