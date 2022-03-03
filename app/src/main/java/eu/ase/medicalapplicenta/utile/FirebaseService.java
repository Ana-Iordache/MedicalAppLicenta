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
    private static final String PACIENTI = "Pacienti";
    private static final String FIREBASE_TAG = "evenimentFirebase";
    private Context context;

    public FirebaseService(String enitate){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child(enitate);
    }

    public void preiaDateDinFirebase(ValueEventListener eventListener){
        if(eventListener != null){
            databaseReference.addValueEventListener(eventListener);
        }
    }



//    public FirebaseService(Context context){
//        this.context = context;
//    }


//    public void adaugaPacient(Pacient pacient) {
//        if(pacient!=null){
////            databaseReference.addValueEventListener(new ValueEventListener() {
////                @Override
////                public void onDataChange(@NonNull DataSnapshot snapshot) {
//////                    pacient.setIdPacient(databaseReference.push().getKey());
//////                    databaseReference.child(pacient.getIdPacient()).setValue(pacient);
////                    databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
////                            .setValue(pacient);
////                    Log.i(FIREBASE_TAG, "Pacientul a fost adaugat in baza de date!");
////                }
////
////                @Override
////                public void onCancelled(@NonNull DatabaseError error) {
////                    Log.w(FIREBASE_TAG, "Pacientul NU a fost adaugat in baza de date!");
////                }
////            });
//            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                    .setValue(pacient).addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful()){
//                        Toast.makeText(context, "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//        }
//    }
}
