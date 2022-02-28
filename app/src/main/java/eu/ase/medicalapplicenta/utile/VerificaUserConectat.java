package eu.ase.medicalapplicenta.utile;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eu.ase.medicalapplicenta.activitati.MainActivity;

// clasa prin care verific daca user-ul este conectat
public class VerificaUserConectat extends Application {
    SharedPreferences preferinteConectare;
    Boolean salveazaDateConectare;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        preferinteConectare = getSharedPreferences("salveazaDateConectare", MODE_PRIVATE);
        salveazaDateConectare = preferinteConectare.getBoolean("salveazaDateConectare", false);
        if (!salveazaDateConectare) {
            mAuth.signOut();
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            // FLAG_ACTIVITY_NEW_TASK imi trebuie asta pt accesez o acivitate dintr-o clasa care nu e activitate
        }
    }
}
