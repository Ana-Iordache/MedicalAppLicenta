package eu.ase.medicalapplicenta.entitati;

import java.io.Serializable;

public class Pacient implements Serializable {
    private String idPacient;
    private String nume;
    private String prenume;
    private long cnp;
    private long nrTelefon;
    private String sex;
    private String adresa;
    private String dataNasterii;
    private String adresaEmail;
    private String urlPozaProfil;
    private String grupaSange;
    private double greutate;
    private double inaltime;
    private int varsta;
    private boolean contSters;

    public Pacient() {
    }

    public Pacient(String idPacient, String nume, String prenume, long cnp, long nrTelefon, String sex,
                   String adresa, String dataNasterii, String adresaEmail, String grupaSange, double greutate,
                   double inaltime, int varsta, String urlPozaProfil) {
        this.idPacient = idPacient;
        this.nume = nume;
        this.prenume = prenume;
        this.cnp = cnp;
        this.nrTelefon = nrTelefon;
        this.sex = sex;
        this.adresa = adresa;
        this.dataNasterii = dataNasterii;
        this.adresaEmail = adresaEmail;
        this.grupaSange = grupaSange;
        this.greutate = greutate;
        this.inaltime = inaltime;
        this.varsta = varsta;
        this.urlPozaProfil = urlPozaProfil;
        this.contSters = false;
    }

    public String getIdPacient() {
        return idPacient;
    }

    public void setIdPacient(String idPacient) {
        this.idPacient = idPacient;
    }


    public String getUrlPozaProfil() {
        return urlPozaProfil;
    }

    public void setUrlPozaProfil(String urlPozaProfil) {
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

    public long getCnp() {
        return cnp;
    }

    public void setCnp(long cnp) {
        this.cnp = cnp;
    }

    public long getNrTelefon() {
        return nrTelefon;
    }

    public void setNrTelefon(long nrTelefon) {
        this.nrTelefon = nrTelefon;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getDataNasterii() {
        return dataNasterii;
    }

    public void setDataNasterii(String dataNasterii) {
        this.dataNasterii = dataNasterii;
    }

    public String getAdresaEmail() {
        return adresaEmail;
    }

    public void setAdresaEmail(String adresaEmail) {
        this.adresaEmail = adresaEmail;
    }

    public String getGrupaSange() {
        return grupaSange;
    }

    public void setGrupaSange(String grupaSange) {
        this.grupaSange = grupaSange;
    }

    public double getGreutate() {
        return greutate;
    }

    public void setGreutate(double greutate) {
        this.greutate = greutate;
    }

    public double getInaltime() {
        return inaltime;
    }

    public void setInaltime(double inaltime) {
        this.inaltime = inaltime;
    }

    public int getVarsta() {
        return varsta;
    }

    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }

    public boolean isContSters() {
        return contSters;
    }

    public void setContSters(boolean contSters) {
        this.contSters = contSters;
    }

    @Override
    public String toString() {
        return "Pacient{" +
                "idPacient='" + idPacient + '\'' +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", cnp=" + cnp +
                ", nrTelefon=" + nrTelefon +
                ", sex='" + sex + '\'' +
                ", adresa='" + adresa + '\'' +
                ", dataNasterii='" + dataNasterii + '\'' +
                ", adresaEmail='" + adresaEmail + '\'' +
                ", urlPozaProfil='" + urlPozaProfil + '\'' +
                ", grupaSange='" + grupaSange + '\'' +
                ", greutate=" + greutate +
                ", inaltime=" + inaltime +
                ", varsta=" + varsta +
                ", contSters=" + contSters +
                '}';
    }
}
