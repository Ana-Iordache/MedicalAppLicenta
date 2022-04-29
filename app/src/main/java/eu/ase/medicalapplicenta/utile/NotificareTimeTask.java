package eu.ase.medicalapplicenta.utile;

import java.util.TimerTask;

import eu.ase.medicalapplicenta.entitati.Notificare;

public class NotificareTimeTask extends TimerTask {
    private final FirebaseService firebaseService;
    private Notificare notificare;

    public NotificareTimeTask(Notificare notificare) {
        this.notificare = notificare;
        firebaseService = new FirebaseService("Notificari");
    }

    @Override
    public void run() {
        String idNotificare = firebaseService.databaseReference.push().getKey();
        notificare.setIdNotificare(idNotificare);
        firebaseService.databaseReference.child(idNotificare).setValue(notificare);
    }
}
