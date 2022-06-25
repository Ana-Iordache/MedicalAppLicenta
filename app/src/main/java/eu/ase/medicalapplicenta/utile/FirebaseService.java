package eu.ase.medicalapplicenta.utile;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import eu.ase.medicalapplicenta.activitati.InregistrarePacientActivity;
import eu.ase.medicalapplicenta.entitati.Pacient;

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
