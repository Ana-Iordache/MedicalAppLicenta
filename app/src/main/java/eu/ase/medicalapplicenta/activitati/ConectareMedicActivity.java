package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
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

    private AlertDialog resetareParolaDialog;

    private ProgressDialog progressDialog;

    private Intent intent;

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

        if (intent.hasExtra(ConectarePacientActivity.CREDETIALE)) {
            String[] credentiale = intent.getStringArrayExtra(ConectarePacientActivity.CREDETIALE);
            if (!credentiale[0].equals("")) {
                tietLoginEmailMedic.setText(credentiale[0]);
                tietLoginParolaMedic.setText(credentiale[1]);
            }
        }

        seteazaDialogResetareParola();
    }

    private void seteazaDialogResetareParola() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resetare parolă");

        View view = getLayoutInflater().inflate(R.layout.dialog_resetare_parola, null);
        TextInputEditText tietEmail = view.findViewById(R.id.tietEmail);
        AppCompatButton btnReseteazaParola = view.findViewById(R.id.btnReseteazaParola);
        AppCompatButton btnRenunta = view.findViewById(R.id.btnRenunta);

        btnReseteazaParola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tietEmail.getText().toString();
                if (email.isEmpty()) {
                    tietEmail.setError(getString(R.string.err_empty_email));
                    tietEmail.requestFocus();
                    return;
                }

                Pattern pattern = Pattern.compile(getString(R.string.pattern_email_medic));
                Matcher matcher = pattern.matcher(email);
                if (!matcher.matches()) {
                    tietEmail.setError(getString(R.string.err_not_valid_email_doctor));
                    tietEmail.requestFocus();
                    return;
                }

                progressDialog = new ProgressDialog(ConectareMedicActivity.this, R.style.ProgressDialogStyle);
                progressDialog.setMessage("Se trimite emailul...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(),
                                    "Link-ul de resetare a parolei a fost trimis pe email!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "Nu s-a putut trimite link-ul de resetare a parolei!", Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                        resetareParolaDialog.dismiss();
                    }
                });
            }
        });

        btnRenunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetareParolaDialog.dismiss();
                tietEmail.clearFocus();
                tietEmail.setText("");
            }
        });

        builder.setView(view);
        resetareParolaDialog = builder.create();
        resetareParolaDialog.setCanceledOnTouchOutside(false);
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

        mAuth = FirebaseAuth.getInstance();

        intent = getIntent();
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
                resetareParolaDialog.show();
                break;
            case R.id.ivPacient:
                startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                finish();
                break;
        }
    }

    private void conecteazaMedic() {
        String email = tietLoginEmailMedic.getText().toString().trim();
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

        progressDialog = new ProgressDialog(ConectareMedicActivity.this, R.style.ProgressDialogStyle);
        progressDialog.setMessage("Se verifică credețialele...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

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
                    Toast.makeText(getApplicationContext(), getString(R.string.err_credentiale), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
            }
        });
    }

}