package eu.ase.medicalapplicenta.entitati;

import java.util.Date;

public class Pacient {
//    private String idPacient;
    private String nume;
    private String prenume;
    private Long cnp;
    private Long nrTelefon;
    private String sex; // setata automat pe baza primei cifre din cnp
    private String adresa; // optional
    private String dataNasterii; // ar trb sa corespunda cu cnp TODO maybe
    private String adresaEmail;
    private String urlPozaProfil; // optional
    private String grupaSange;
    private Double greutate; // optional
    private Double inaltime; // optional
    private int varsta; // calculata automat pe baza dataNasterii

    public Pacient() {
    }


//    public Pacient(String nume, String prenume, Long cnp, Long nrTelefon, String sex, String adresa, String dataNasterii, String adresaEmail) {
//        this.nume = nume;
//        this.prenume = prenume;
//        this.cnp = cnp;
//        this.nrTelefon = nrTelefon;
//        this.sex = sex;
//        this.adresa = adresa;
//        this.dataNasterii = dataNasterii;
//        this.adresaEmail = adresaEmail;
//    }
//
//    public Pacient(String nume, String prenume, Long cnp, Long nrTelefon, String sex, String adresa, String dataNasterii, String adresaEmail, String urlPozaProfil) {
//        this.nume = nume;
//        this.prenume = prenume;
//        this.cnp = cnp;
//        this.nrTelefon = nrTelefon;
//        this.sex = sex;
//        this.adresa = adresa;
//        this.dataNasterii = dataNasterii;
//        this.adresaEmail = adresaEmail;
//        this.urlPozaProfil = urlPozaProfil;
//    }

//    public Pacient(String nume, String prenume, Long cnp, Long nrTelefon, String sex, String adresa, String dataNasterii, String adresaEmail, String urlPozaProfil, String grupaSange, Double greutate, Double inaltime, int varsta) {
//        this.nume = nume;
//        this.prenume = prenume;
//        this.cnp = cnp;
//        this.nrTelefon = nrTelefon;
//        this.sex = sex;
//        this.adresa = adresa;
//        this.dataNasterii = dataNasterii;
//        this.adresaEmail = adresaEmail;
//        this.urlPozaProfil = urlPozaProfil;
//        this.grupaSange = grupaSange;
//        this.greutate = greutate;
//        this.inaltime = inaltime;
//        this.varsta = varsta;
//    }
public Pacient(String nume, String prenume, Long cnp, Long nrTelefon, String sex, String adresa, String dataNasterii, String adresaEmail, String grupaSange, Double greutate, Double inaltime, int varsta, String urlPozaProfil) {
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
}

    //    public String getIdPacient() {
//        return idPacient;
//    }
//
//    public void setIdPacient(String idPacient) {
//        this.idPacient = idPacient;
//    }


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

    public Long getCnp() {
        return cnp;
    }

    public void setCnp(Long cnp) {
        this.cnp = cnp;
    }

    public Long getNrTelefon() {
        return nrTelefon;
    }

    public void setNrTelefon(Long nrTelefon) {
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

    public Double getGreutate() {
        return greutate;
    }

    public void setGreutate(Double greutate) {
        this.greutate = greutate;
    }

    public Double getInaltime() {
        return inaltime;
    }

    public void setInaltime(Double inaltime) {
        this.inaltime = inaltime;
    }

    public int getVarsta() {
        return varsta;
    }

    public void setVarsta(int varsta) {
        this.varsta = varsta;
    }

    @Override
    public String toString() {
        return "Pacient{" +
                ", nume='" + nume + '\'' +
                ", prenume='" + prenume + '\'' +
                ", cnp=" + cnp +
                ", nrTelefon=" + nrTelefon +
                ", sex='" + sex + '\'' +
                ", adresa='" + adresa + '\'' +
                ", dataNasterii='" + dataNasterii + '\'' +
                ", adresaEmail='" + adresaEmail + '\'' +
                '}';
    }
}
