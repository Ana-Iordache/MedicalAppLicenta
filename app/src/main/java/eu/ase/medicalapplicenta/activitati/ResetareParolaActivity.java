package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import eu.ase.medicalapplicenta.R;

//TODO poate ar fi mai bine sa fac un fragment...
public class ResetareParolaActivity extends AppCompatActivity {
    TextInputEditText tietEmailPacient;
    Button btnRestareParola;
    ProgressBar progressBar;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetare_parola);

        mAuth = FirebaseAuth.getInstance();

        tietEmailPacient = findViewById(R.id.tietEmailPacient);
        progressBar = findViewById(R.id.progressBar);

        btnRestareParola =  findViewById(R.id.btnRestareParola);
        btnRestareParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reseteazaParola();
            }
        });
    }

    private void reseteazaParola() {
        String email = tietEmailPacient.getText().toString();
        if(email.isEmpty()){
            tietEmailPacient.setError("Introduceti emailul!");
            tietEmailPacient.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            tietEmailPacient.setError("Introduceti un email valid!");
            tietEmailPacient.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Link-ul de resetare a prolei a fost trimis pe email!", Toast.LENGTH_SHORT).show();
                } else{
                    Toast.makeText(getApplicationContext(), "Nu s-a putut trimite link-ul de resetare a parolei!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}