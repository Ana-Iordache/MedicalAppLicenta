package eu.ase.medicalapplicenta.entitati;


public class Medic {
    private String nume;
    private String prenume;
    private long nrTelefon;
    private String adresaEmail;
    private String idSpecialitate; // preluat din bd
    private double notaFeedback; // initial o sa fie 0
    private String gradProfesional;
    private String urlPozaProfil;

    public Medic(){}

    public Medic(String nume, String prenume, long nrTelefon, String adresaEmail, String idSpecialitate, double notaFeedback, String gradProfesional, String urlPozaProfil) {
        this.nume = nume;
        this.prenume = prenume;
        this.nrTelefon = nrTelefon;
        this.adresaEmail = adresaEmail;
        this.idSpecialitate = idSpecialitate;
        this.notaFeedback = notaFeedback;
        this.gradProfesional = gradProfesional;
        this.urlPozaProfil = urlPozaProfil;
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

    @Override
    public String toString() {
        return "Medic{" +
                "nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", nrTelefon=" + nrTelefon +
                ", adresaEmail='" + adresaEmail + '\'' +
                ", idSpecialitate='" + idSpecialitate + '\'' +
                ", notaFeedback=" + notaFeedback +
                ", gradProfesional='" + gradProfesional + '\'' +
                ", urlPozaProfil='" + urlPozaProfil + '\'' +
                '}';
    }
}
