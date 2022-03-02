package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.ase.medicalapplicenta.R;

public class ConectareMedicActivity extends AppCompatActivity implements View.OnClickListener{
    TextInputEditText tietLoginEmailMedic;
    TextInputEditText tietLoginParolaMedic;

    TextView tvResetareParola;
    TextView tvCreareCont;

    CheckBox cbRamaiAutentificat;
    SharedPreferences preferinteConectare;
    SharedPreferences.Editor preferinteConectareEditor;
    Boolean salveazaDateConectare;

    Button btnLoginMedic;

    ImageView ivPacient;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectare_medic);

        tietLoginEmailMedic = findViewById(R.id.tietLoginEmailMedic);
        tietLoginParolaMedic = findViewById(R.id.tietLoginParolaMedic);

        tvResetareParola = findViewById(R.id.tvResetareParola);
        tvResetareParola.setOnClickListener(this);

        tvCreareCont = findViewById(R.id.tvCreareCont);
        tvCreareCont.setOnClickListener(this);

        cbRamaiAutentificat = findViewById(R.id.cbRamaiAutentificat);

        preferinteConectare = getSharedPreferences("salveazaDateConectare",MODE_PRIVATE);
        preferinteConectareEditor = preferinteConectare.edit();

        salveazaDateConectare = preferinteConectare.getBoolean("salveazaDateConectare", false);
        if(salveazaDateConectare){
            tietLoginEmailMedic.setText(preferinteConectare.getString("email", ""));
            tietLoginParolaMedic.setText(preferinteConectare.getString("parola",""));
        }

        btnLoginMedic = findViewById(R.id.btnLoginMedic);
        btnLoginMedic.setOnClickListener(this);

        ivPacient = findViewById(R.id.ivPacient);
        ivPacient.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCreareCont:
                startActivity(new Intent(getApplicationContext(), InregistrareMedicActivity.class));
                break;
            case R.id.btnLoginMedic:
                conectareMedic();
                break;
            case R.id.tvResetareParola:
                //TODO poate incerc cu un fragment
//                startActivity(new Intent(getApplicationContext(), ResetareParolaActivity.class));
                Toast.makeText(getApplicationContext(), "tvResetareParola", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ivPacient:
                startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                finish();
                break;
        }
    }

    private void conectareMedic() {
        String email = tietLoginEmailMedic.getText().toString().trim(); //trim in cazul in care pune space
        String parola = tietLoginParolaMedic.getText().toString().trim();

        if (email.isEmpty()) {
            tietLoginEmailMedic.setError("Introduceti emailul!");
            tietLoginEmailMedic.requestFocus();
            return;
        }

        Pattern pattern = Pattern.compile("^([A-Za-z0-9._]+)(@clinica-medicala\\.ro)$");
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            tietLoginEmailMedic.setError("Introduceti emailul oficial (de forma adresa@clinica-medicala.ro)!");
            tietLoginEmailMedic.requestFocus();
            return;
        }

        if (parola.isEmpty()) {
            tietLoginParolaMedic.setError("Introduceti parola!");
            tietLoginParolaMedic.requestFocus();
            return;
        }

        if (parola.length() < 6) {
            tietLoginParolaMedic.setError("Parola trebuie sa contina cel putin 6 caractere!");
            tietLoginParolaMedic.requestFocus();
            return;
        }

        if (cbRamaiAutentificat.isChecked()) {
            preferinteConectareEditor.putBoolean("salveazaDateConectare", true);
            preferinteConectareEditor.putString("email", email);
            preferinteConectareEditor.putString("parola", parola);
        } else {
            preferinteConectareEditor.clear();
        }
        preferinteConectareEditor.commit();

        mAuth.signInWithEmailAndPassword(email, parola).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // daca emailul a fost verificat prin link conectez utilizatorul in cont
//                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                    if(firebaseUser.isEmailVerified())
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                    else{
//                        firebaseUser.sendEmailVerification();
//                        Toast.makeText(getApplicationContext(), "Accesati link-ul primit pe email pentru verificare!", Toast.LENGTH_SHORT).show();
//                    }
                    startActivity(new Intent(getApplicationContext(), HomeMedicActivity.class));

//                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Credentiale invalide!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
//todo
//daca ultima data a fost conectat un medic, ar trb sa ramana pagina de pornire ConectareMedicActivity