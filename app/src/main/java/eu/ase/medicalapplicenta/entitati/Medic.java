package eu.ase.medicalapplicenta.entitati;


import java.io.Serializable;
import java.util.List;

public class Medic implements Serializable {
    private String idMedic;
    private String nume;
    private String prenume;
    private long nrTelefon;
    private String adresaEmail;
    private String idSpecialitate;
    private List<Integer> noteFeedback;
    private double notaFeedback;
    private String gradProfesional;
    private String urlPozaProfil;
    private List<ZiDeLucru> program;
    private boolean contSters;

    public Medic() {
    }

    public Medic(String idMedic, String nume, String prenume, long nrTelefon, String adresaEmail, String idSpecialitate, List<Integer> noteFeedback, double notaFeedback, String gradProfesional, String urlPozaProfil, List<ZiDeLucru> program) {
        this.idMedic = idMedic;
        this.nume = nume;
        this.prenume = prenume;
        this.nrTelefon = nrTelefon;
        this.adresaEmail = adresaEmail;
        this.idSpecialitate = idSpecialitate;
        this.notaFeedback = notaFeedback;
        this.noteFeedback = noteFeedback;
        this.gradProfesional = gradProfesional;
        this.urlPozaProfil = urlPozaProfil;
        this.program = program;
        this.contSters = false;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPrenume() {
        return prenume;
    }

    public void setPrenume(String prenume) {
        this.prenume = prenume;
    }

    public long getNrTelefon() {
        return nrTelefon;
    }

    public void setNrTelefon(long nrTelefon) {
        this.nrTelefon = nrTelefon;
    }

    public String getAdresaEmail() {
        return adresaEmail;
    }

    public void setAdresaEmail(String adresaEmail) {
        this.adresaEmail = adresaEmail;
    }

    public String getIdSpecialitate() {
        return idSpecialitate;
    }

    public void setIdSpecialitate(String idSpecialitate) {
        this.idSpecialitate = idSpecialitate;
    }

    public double getNotaFeedback() {
        return notaFeedback;
    }

    public void setNotaFeedback(double notaFeedback) {
        this.notaFeedback = notaFeedback;
    }

    public List<Integer> getNoteFeedback() {
        return noteFeedback;
    }

    public void setNoteFeedback(List<Integer> noteFeedback) {
        this.noteFeedback = noteFeedback;
    }

    public String getGradProfesional() {
        return gradProfesional;
    }

    public void setGradProfesional(String gradProfesional) {
        this.gradProfesional = gradProfesional;
    }

    public String getUrlPozaProfil() {
        return urlPozaProfil;
    }

    public void setUrlPozaProfil(String urlPozaProfil) {
        this.urlPozaProfil = urlPozaProfil;
    }

    //aici am adaugat
    public String getIdMedic() {
        return idMedic;
    }

    public void setIdMedic(String idMedic) {
        this.idMedic = idMedic;
    }

    public List<ZiDeLucru> getProgram() {
        return program;
    }

    public void setProgram(List<ZiDeLucru> program) {
        this.program = program;
    }

    public boolean isContSters() {
        return contSters;
    }

    public void setContSters(boolean contSters) {
        this.contSters = contSters;
    }

    @Override
    public String toString() {
        return "Medic{" +
                "idMedic='" + idMedic + '\'' +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", nrTelefon=" + nrTelefon +
                ", adresaEmail='" + adresaEmail + '\'' +
                ", idSpecialitate='" + idSpecialitate + '\'' +
                ", noteFeedback=" + noteFeedback +
                ", notaFeedback=" + notaFeedback +
                ", gradProfesional='" + gradProfesional + '\'' +
                ", urlPozaProfil='" + urlPozaProfil + '\'' +
                ", program=" + program +
                ", contSters=" + contSters +
                '}';
    }
}
