package eu.ase.medicalapplicenta.utile;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import eu.ase.medicalapplicenta.activitati.HomeMedicActivity;
import eu.ase.medicalapplicenta.activitati.MainActivity;

public class VerificaUserConectat extends Application {
    SharedPreferences preferinteConectare;
    Boolean salveazaDateConectare;

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();

        preferinteConectare = getSharedPreferences("salveazaDateConectare", MODE_PRIVATE);
        salveazaDateConectare = preferinteConectare.getBoolean("salveazaDateConectare", false);
        if (!salveazaDateConectare) {
            if (user != null)
                mAuth.signOut();
        }

        if (mAuth.getCurrentUser() != null) {
            if (!user.getEmail().contains("clinica-medicala.ro"))
                startActivity(new Intent(getApplicationContext(), MainActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            else
                startActivity(new Intent(getApplicationContext(), HomeMedicActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }
}
