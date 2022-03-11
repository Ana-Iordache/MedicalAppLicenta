package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
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

public class ConectarePacientActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv;
    private TextView tvSuntMedic;

    private TextInputEditText tietLoginEmailPacient;
    private TextInputEditText tietLoginParolaPacient;
    private Button btnLoginPacient;

    private TextView tvCreareCont;
    private TextView tvResetareParola;

    private ProgressBar progressBar;

    private CheckBox cbRamaiAutentificat;
    private SharedPreferences preferinteConectare;
    private SharedPreferences.Editor preferinteConectareEditor;
    private Boolean salveazaDateConectare;

    private ImageView ivMedic;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectare_pacient);

        initializeazaAtribute();

        //todo
        tv = findViewById(R.id.tv);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AdminActivity.class));
            }
        });


        btnLoginPacient.setOnClickListener(this);
        tvCreareCont.setOnClickListener(this);
        tvResetareParola.setOnClickListener(this);
        ivMedic.setOnClickListener(this);

        preiaPreferinte();

    }

    private void preiaPreferinte() {
        preferinteConectare = getSharedPreferences("salveazaDateConectare", MODE_PRIVATE);
        preferinteConectareEditor = preferinteConectare.edit();

        salveazaDateConectare = preferinteConectare.getBoolean("salveazaDateConectare", false);
        if (salveazaDateConectare) {
            tietLoginEmailPacient.setText(preferinteConectare.getString("email", ""));
            tietLoginParolaPacient.setText(preferinteConectare.getString("parola", ""));
        }
    }

    private void initializeazaAtribute() {
        tietLoginEmailPacient = findViewById(R.id.tietLoginEmailPacient);
        tietLoginParolaPacient = findViewById(R.id.tietLoginParolaPacient);

        btnLoginPacient = findViewById(R.id.btnLoginPacient);

        tvCreareCont = findViewById(R.id.tvCreareCont);
        tvResetareParola = findViewById(R.id.tvResetareParola);

        progressBar = findViewById(R.id.progressBar);

        cbRamaiAutentificat = findViewById(R.id.cbRamaiAutentificat);

        ivMedic = findViewById(R.id.ivMedic);

        tvSuntMedic = findViewById(R.id.tvSuntMedic);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCreareCont:
                startActivity(new Intent(getApplicationContext(), InregistrarePacientActivity.class));
                break;
            case R.id.btnLoginPacient:
                conecteazaPacient();
                break;
            case R.id.tvResetareParola:
                startActivity(new Intent(getApplicationContext(), ResetareParolaActivity.class));
                break;
            case R.id.ivMedic:
                startActivity(new Intent(getApplicationContext(), ConectareMedicActivity.class));
                finish();
                break;
        }
    }

    private void conecteazaPacient() {
        String email = tietLoginEmailPacient.getText().toString().trim(); //trim in cazul in care pune space
        String parola = tietLoginParolaPacient.getText().toString().trim();

        if (email.isEmpty()) {
            tietLoginEmailPacient.setError("Introduceti emailul!");
            tietLoginEmailPacient.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tietLoginEmailPacient.setError("Introduceti un email valid!");
            tietLoginEmailPacient.requestFocus();
            return;
        }

        //TODO poate am cum sa trimit emailul si parola deja introduse aici atunci cand apasa pe sunt medic
        Pattern pattern = Pattern.compile(getString(R.string.pattern_email_medic));
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            tvSuntMedic.setError("Va rugam sa va conectati din pagina medicului!");
            tvSuntMedic.requestFocus();
            return;
        }

        if (parola.isEmpty()) {
            tietLoginParolaPacient.setError("Introduceti parola!");
            tietLoginParolaPacient.requestFocus();
            return;
        }

        if (parola.length() < 6) {
            tietLoginParolaPacient.setError("Parola trebuie sa contina cel putin 6 caractere!");
            tietLoginParolaPacient.requestFocus();
            return;
        }

        loading(true);

        seteazaPreferinte(email, parola);

        mAuth.signInWithEmailAndPassword(email, parola).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // daca emailul a fost verificat prin link conectez utilizatorul in cont
//                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//                    if(firebaseUser.isEmailVerified())
//                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
//                    else{
//                        firebaseUser.sendEmailVerification();
//                        Toast.makeText(getApplicationContext(), "Accesati link-ul primit pe email pentru verificare!", Toast.LENGTH_SHORT).show();
//                    }
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
//                    finish(); TODO daca dau back din main sa nu ma intoarca la login
                    // dar daca pun asa cand dau back ma scoate din ap, e ok
                    // dar daca vreau sa revin in ap imi deschide pagina de log in in loc de main
                } else {
                    loading(false);
                    Toast.makeText(getApplicationContext(), "Credentiale invalide!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // verific daca user-ul doreste sa ramana autentificat
    // prin urmare daca i se pastreaza datele de autentificare atunci cand se deconecteaza
    private void seteazaPreferinte(String email, String parola) {
        if (cbRamaiAutentificat.isChecked()) {
            preferinteConectareEditor.putBoolean("salveazaDateConectare", true);
            preferinteConectareEditor.putString("email", email);
            preferinteConectareEditor.putString("parola", parola);
        } else {
            preferinteConectareEditor.clear();
        }
        preferinteConectareEditor.commit();
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

}