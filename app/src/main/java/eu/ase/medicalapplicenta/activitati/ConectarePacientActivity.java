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
import android.util.Patterns;
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

public class ConectarePacientActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String CREDETIALE = "credetiale";
    private TextView tv;
    private TextView tvSuntMedic;
    private ImageView ivMedic;

    private TextInputEditText tietLoginEmailPacient;
    private TextInputEditText tietLoginParolaPacient;
    private AppCompatButton btnLoginPacient;

    private TextView tvCreareCont;
    private TextView tvResetareParola;

    private CheckBox cbRamaiAutentificat;
    private SharedPreferences preferinteConectare;
    private SharedPreferences.Editor preferinteConectareEditor;
    private Boolean salveazaDateConectare;

    private FirebaseAuth mAuth;

    private AlertDialog resetareParolaDialog;

    private ProgressDialog progressDialog;

    private String[] credentiale = new String[]{"", ""};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_MedicalAppLicenta);
        setContentView(R.layout.activity_conectare_pacient);

        initializeazaAtribute();

        //todo de sters
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

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    tietEmail.setError(getString(R.string.err_not_valid_email));
                    tietEmail.requestFocus();
                    return;
                }

                progressDialog = new ProgressDialog(ConectarePacientActivity.this, R.style.ProgressDialogStyle);
                progressDialog.setMessage("Se trimite emailul...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Link-ul de resetare a parolei a fost trimis pe email!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Nu s-a putut trimite link-ul de resetare a parolei!", Toast.LENGTH_LONG).show();
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

        cbRamaiAutentificat = findViewById(R.id.cbRamaiAutentificat);

        tvSuntMedic = findViewById(R.id.tvSuntMedic);
        ivMedic = findViewById(R.id.ivMedic);

        mAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("NonConstantResourceId")
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
                resetareParolaDialog.show();
                break;
            case R.id.ivMedic:
                startActivity(new Intent(getApplicationContext(), ConectareMedicActivity.class).putExtra(CREDETIALE, credentiale));
                finish();
                break;
        }
    }

    private void conecteazaPacient() {
        String email = tietLoginEmailPacient.getText().toString().trim(); //trim in cazul in care pune space
        String parola = tietLoginParolaPacient.getText().toString().trim();

        if (email.isEmpty()) {
            tietLoginEmailPacient.setError(getString(R.string.err_empty_email));
            tietLoginEmailPacient.requestFocus();
            return;
        }

        credentiale[0] = email;

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tietLoginEmailPacient.setError(getString(R.string.err_not_valid_email));
            tietLoginEmailPacient.requestFocus();
            return;
        }

        Pattern pattern = Pattern.compile(getString(R.string.pattern_email_medic));
        Matcher matcher = pattern.matcher(email);
        if (matcher.matches()) {
            tietLoginEmailPacient.setError(getString(R.string.err_conectare_medic));
            tietLoginEmailPacient.requestFocus();

            tvSuntMedic.setError("");
            tvSuntMedic.requestFocus();
            return;
        }

        if (parola.isEmpty()) {
            tietLoginParolaPacient.setError(getString(R.string.err_empty_parola));
            tietLoginParolaPacient.requestFocus();
            return;
        }

        credentiale[1] = parola;

        if (parola.length() < 6) {
            tietLoginParolaPacient.setError(getString(R.string.err_not_valid_parola));
            tietLoginParolaPacient.requestFocus();
            return;
        }

        progressDialog = new ProgressDialog(ConectarePacientActivity.this, R.style.ProgressDialogStyle);
        progressDialog.setMessage("Se verifică credețialele...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

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
                    finish(); // sa nu ma intoarca la login
                    // dar daca pun asa cand dau back ma scoate din ap, e ok
                    // dar daca vreau sa revin in ap imi deschide pagina de log in in loc de main
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.err_credentiale), Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();
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

//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            moveTaskToBack(true);
//        }
//    }

    // ca daca ies din ap si intru iar sa mi deschida pagina principala
    // dar daca dau back prima data imi deschide tot pag principala
    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (FirebaseAuth.getInstance().getCurrentUser().getEmail().contains("clinica-medicala.ro")) {
                startActivity(new Intent(getApplicationContext(), HomeMedicActivity.class));
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            finish();
        }
    }
}