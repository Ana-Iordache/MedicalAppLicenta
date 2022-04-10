package eu.ase.medicalapplicenta.entitati;

import java.io.Serializable;

public class ZiDeLucru implements Serializable {
    private String zi;
    private String oraInceput;
    private String oraSfarsit;

    public ZiDeLucru() {
    }

    public ZiDeLucru(String zi, String oraInceput, String oraSfarsit) {
        this.zi = zi;
        this.oraInceput = oraInceput;
        this.oraSfarsit = oraSfarsit;
    }

    public String getZi() {
        return zi;
    }

    public void setZi(String zi) {
        this.zi = zi;
    }

    public String getOraInceput() {
        return oraInceput;
    }

    public void setOraInceput(String oraInceput) {
        this.oraInceput = oraInceput;
    }

    public String getOraSfarsit() {
        return oraSfarsit;
    }

    public void setOraSfarsit(String oraSfarsit) {
        this.oraSfarsit = oraSfarsit;
    }

    @Override
    public String toString() {
        return zi + ": " + oraInceput + " - " + oraSfarsit;
    }
}
