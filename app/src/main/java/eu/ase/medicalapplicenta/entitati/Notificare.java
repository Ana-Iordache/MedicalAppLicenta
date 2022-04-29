package eu.ase.medicalapplicenta.entitati;

public class Notificare {
    private String idNotificare;
    private String titlu;
    private String idEmitator;
    private String idReceptor;
    private String dataProgramarii;
    private String oraProgramarii;
    private String data;
    private boolean notificareCitita;

    public Notificare() {
    }

    public Notificare(String idNotificare, String titlu, String idEmitator, String idReceptor, String dataProgramarii, String oraProgramarii, String data, boolean notificareCitita) {
        this.idNotificare = idNotificare;
        this.titlu = titlu;
        this.idEmitator = idEmitator;
        this.idReceptor = idReceptor;
        this.dataProgramarii = dataProgramarii;
        this.oraProgramarii = oraProgramarii;
        this.data = data;
        this.notificareCitita = notificareCitita;
    }

    public String getIdNotificare() {
        return idNotificare;
    }

    public void setIdNotificare(String idNotificare) {
        this.idNotificare = idNotificare;
    }

    public String getTitlu() {
        return titlu;
    }

    public void setTitlu(String titlu) {
        this.titlu = titlu;
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

    public String getDataProgramarii() {
        return dataProgramarii;
    }

    public void setDataProgramarii(String dataProgramarii) {
        this.dataProgramarii = dataProgramarii;
    }

    public String getOraProgramarii() {
        return oraProgramarii;
    }

    public void setOraProgramarii(String oraProgramarii) {
        this.oraProgramarii = oraProgramarii;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isNotificareCitita() {
        return notificareCitita;
    }

    public void setNotificareCitita(boolean notificareCitita) {
        this.notificareCitita = notificareCitita;
    }

    @Override
    public String toString() {
        return "Notificare{" +
                "idNotificare='" + idNotificare + '\'' +
                ", titlu='" + titlu + '\'' +
                ", idEmitator='" + idEmitator + '\'' +
                ", idReceptor='" + idReceptor + '\'' +
                ", dataProgramarii='" + dataProgramarii + '\'' +
                ", oraProgramarii='" + oraProgramarii + '\'' +
                ", data='" + data + '\'' +
                ", notificareCitita=" + notificareCitita +
                '}';
    }
}
