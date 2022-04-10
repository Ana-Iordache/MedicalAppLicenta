package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

public class ConectareMedicActivity extends AppCompatActivity implements View.OnClickListener {
    private TextInputEditText tietLoginEmailMedic;
    private TextInputEditText tietLoginParolaMedic;

    private TextView tvResetareParola;
    private TextView tvCreareCont;

    private CheckBox cbRamaiAutentificat;
    private SharedPreferences preferinteConectare;
    private SharedPreferences.Editor preferinteConectareEditor;
    private Boolean salveazaDateConectare;

    private AppCompatButton btnLoginMedic;

    private ImageView ivPacient;

    private FirebaseAuth mAuth;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectare_medic);

        initializeazaAtribute();

        tvResetareParola.setOnClickListener(this);
        tvCreareCont.setOnClickListener(this);
        btnLoginMedic.setOnClickListener(this);
        ivPacient.setOnClickListener(this);

        preiaPreferinte();
    }

    private void preiaPreferinte() {
        preferinteConectare = getSharedPreferences("salveazaDateConectare", MODE_PRIVATE);
        preferinteConectareEditor = preferinteConectare.edit();

        salveazaDateConectare = preferinteConectare.getBoolean("salveazaDateConectare", false);
        if (salveazaDateConectare) {
            tietLoginEmailMedic.setText(preferinteConectare.getString("email", ""));
            tietLoginParolaMedic.setText(preferinteConectare.getString("parola", ""));
        }
    }

    private void initializeazaAtribute() {
        tietLoginEmailMedic = findViewById(R.id.tietLoginEmailMedic);
        tietLoginParolaMedic = findViewById(R.id.tietLoginParolaMedic);

        tvResetareParola = findViewById(R.id.tvResetareParola);
        tvCreareCont = findViewById(R.id.tvCreareCont);

        cbRamaiAutentificat = findViewById(R.id.cbRamaiAutentificat);

        btnLoginMedic = findViewById(R.id.btnLoginMedic);

        ivPacient = findViewById(R.id.ivPacient);

        progressBar = findViewById(R.id.progressBar);

        mAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCreareCont:
                startActivity(new Intent(getApplicationContext(), InregistrareMedicActivity.class));
                break;
            case R.id.btnLoginMedic:
                conecteazaMedic();
                break;
            case R.id.tvResetareParola:
                //TODO poate incerc cu un fragment sau alert dialog mai bn
//                startActivity(new Intent(getApplicationContext(), ResetareParolaActivity.class));
                Toast.makeText(getApplicationContext(), "tvResetareParola", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ivPacient:
                startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                finish();
                break;
        }
    }

    private void conecteazaMedic() {
        String email = tietLoginEmailMedic.getText().toString().trim(); //trim in cazul in care pune space
        String parola = tietLoginParolaMedic.getText().toString().trim();

        if (email.isEmpty()) {
            tietLoginEmailMedic.setError(getString(R.string.err_empty_email));
            tietLoginEmailMedic.requestFocus();
            return;
        }

        Pattern pattern = Pattern.compile(getString(R.string.pattern_email_medic));
        Matcher matcher = pattern.matcher(email);
        if (!matcher.matches()) {
            tietLoginEmailMedic.setError(getString(R.string.err_not_valid_email_doctor));
            tietLoginEmailMedic.requestFocus();
            return;
        }

        if (parola.isEmpty()) {
            tietLoginParolaMedic.setError(getString(R.string.err_empty_parola));
            tietLoginParolaMedic.requestFocus();
            return;
        }

        if (parola.length() < 6) {
            tietLoginParolaMedic.setError(getString(R.string.err_not_valid_parola));
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

        loading(true);

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
                    startActivity(new Intent(getApplicationContext(), HomeMedicActivity.class));

//                    finish();
                } else {
                    loading(false);
                    Toast.makeText(getApplicationContext(), getString(R.string.err_credentiale), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }
}
//todo daca ultima data a fost conectat un medic, ar trb sa ramana pagina de pornire ConectareMedicActivity