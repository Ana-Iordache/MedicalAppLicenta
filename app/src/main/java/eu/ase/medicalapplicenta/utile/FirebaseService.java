package eu.ase.medicalapplicenta.utile;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseService {
    public DatabaseReference databaseReference;

    public FirebaseService(String enitate) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(enitate);
    }

    public void preiaDateDinFirebase(ValueEventListener eventListener) {
        if (eventListener != null) {
            databaseReference.addValueEventListener(eventListener);
        }
    }

    public void preiaObiectDinFirebase(ValueEventListener eventListener, String uid) {
        if (eventListener != null && uid != null) {
            databaseReference.child(uid).addListenerForSingleValueEvent(eventListener);
        }
    }
}
