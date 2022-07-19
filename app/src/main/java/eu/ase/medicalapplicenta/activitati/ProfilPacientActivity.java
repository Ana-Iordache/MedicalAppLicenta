package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ProfilPacientActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 200;
    private CircleImageView ciwPozaProfilPacient;

    private TextInputEditText tietNumePacient;
    private TextInputEditText tietPrenumePacient;
    private TextInputEditText tietCnp;
    private TextInputEditText tietDataNasterii;
    private TextInputEditText tietNrTelefonPacient;
    private TextInputEditText tietAdresa;
    private TextInputEditText tietEmailPacient;
    private TextInputEditText tietGreutate;
    private TextInputEditText tietInaltime;
    private TextInputEditText tietVarsta;
    private TextInputEditText tietSex;

    //    private Spinner spGrupaSange;
    private AutoCompleteTextView actvGrupeSange;

    private Button btnModificaDate;
    private Button btnSalveaza;
    private Button btnRenunta;
    private LinearLayout llButoane;

    private Button btnSchimbaParola;
    private Button btnSchimbaEmail;
    private Button btnStergeCont;

    private Toolbar toolbar;

    private FirebaseUser pacientConectat;
    private FirebaseService firebaseService = new FirebaseService("Pacienti");
    private String idUserConectat;
    private DatabaseReference referintaUserConectat;
    private Pacient pacient;

    private Uri uri;

    private AlertDialog dialogParola;
    private AlertDialog dialogEmail;
    private AlertDialog dialogStergereCont;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_pacient);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaAdaptorGrupeSange();

        seteazaDialogSchimbaParola();
        seteazaDialogSchimbaEmail();
        seteazaDialogStergeCont();

//        spGrupaSange.setEnabled(false); // din xml nu merge
//        actvGrupeSange.setDropDownHeight(0);

        btnModificaDate.setOnClickListener(this);
        btnSalveaza.setOnClickListener(this);
        btnRenunta.setOnClickListener(this);
        btnSchimbaParola.setOnClickListener(this);
        btnSchimbaEmail.setOnClickListener(this);
        btnStergeCont.setOnClickListener(this);

        firebaseService.preiaObiectDinFirebase(preiaPacient(), idUserConectat);
    }


    private ValueEventListener preiaPacient() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pacient = snapshot.getValue(Pacient.class);

                if (pacient != null) {
                    String urlPozaProfil = pacient.getUrlPozaProfil();
                    String nume = pacient.getNume();
                    String prenume = pacient.getPrenume();
                    Long cnp = pacient.getCnp();
                    int varsta = pacient.getVarsta();
                    String sex = pacient.getSex();
                    String dataNasterii = pacient.getDataNasterii();
                    Long telefon = pacient.getNrTelefon();
                    String adresa = pacient.getAdresa();
                    String grupaSange = pacient.getGrupaSange();
                    double greutate = pacient.getGreutate();
                    double inaltime = pacient.getInaltime();
                    String email = pacient.getAdresaEmail();

                    tietNumePacient.setText(nume);
                    tietPrenumePacient.setText(prenume);
                    tietCnp.setText(String.valueOf(cnp));
                    tietDataNasterii.setText(dataNasterii);
                    tietNrTelefonPacient.setText(String.valueOf(telefon).substring(1));
                    tietAdresa.setText(adresa);
                    tietEmailPacient.setText(email);
                    tietVarsta.setText(String.valueOf(varsta));
                    tietSex.setText(sex);
                    if (sex.equals("Feminin")) {
                        tietSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_feminin, 0, 0, 0);
                    } else {
                        tietSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_masculin, 0, 0, 0);
                    }
                    tietGreutate.setText(String.valueOf(greutate));
                    tietInaltime.setText(String.valueOf(inaltime));

                    if (!urlPozaProfil.equals("")) {
                        Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilPacient);
                    }

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) actvGrupeSange.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).equals(grupaSange)) {
                            actvGrupeSange.setText(adapter.getItem(i));
                            break;
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluarePacient", error.getMessage());
            }
        };
    }

    private void seteazaAdaptorGrupeSange() {
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.grupe_sange));
        actvGrupeSange.setAdapter(adapter);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        ciwPozaProfilPacient = findViewById(R.id.ciwPozaProfilUser);
        tietNumePacient = findViewById(R.id.tietNumePacient);
        tietPrenumePacient = findViewById(R.id.tietPrenumePacient);
        tietCnp = findViewById(R.id.tietCnp);
        tietDataNasterii = findViewById(R.id.tietDataNasterii);
        tietNrTelefonPacient = findViewById(R.id.tietNrTelefonPacient);
        tietAdresa = findViewById(R.id.tietAdresa);
        tietEmailPacient = findViewById(R.id.tietEmailPacient);
        tietGreutate = findViewById(R.id.tietGreutate);
        tietInaltime = findViewById(R.id.tietInaltime);
        tietVarsta = findViewById(R.id.tietVarsta);
        tietSex = findViewById(R.id.tietSex);

        actvGrupeSange = findViewById(R.id.actvGrupeSange);

        btnModificaDate = findViewById(R.id.btnModificaDate);
        btnSalveaza = findViewById(R.id.btnSalveaza);
        btnRenunta = findViewById(R.id.btnRenunta);
        llButoane = findViewById(R.id.llButoane);

        btnSchimbaParola = findViewById(R.id.btnSchimbaParola);
        btnSchimbaEmail = findViewById(R.id.btnSchimbaEmail);
        btnStergeCont = findViewById(R.id.btnStergeCont);

        pacientConectat = FirebaseAuth.getInstance().getCurrentUser();
        idUserConectat = pacientConectat.getUid();
        referintaUserConectat = firebaseService.databaseReference.child(idUserConectat);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnModificaDate:
                ciwPozaProfilPacient.setOnClickListener(this);
                seteazaAdaptorGrupeSange();
                seteazaAccesibilitatea(true);
                seteazaVizibilitateButoane(View.GONE, View.VISIBLE);
                break;
            case R.id.btnSchimbaParola:
                dialogParola.show();
                break;
            case R.id.btnSchimbaEmail:
                dialogEmail.show();
                break;
            case R.id.btnStergeCont:
                dialogStergereCont.show();
                break;
            case R.id.btnSalveaza:
                actualizeazaDate();
                break;
            case R.id.btnRenunta:
                seteazaAccesibilitatea(false);
                seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
                ciwPozaProfilPacient.setOnClickListener(null);
                break;
            case R.id.ciwPozaProfilUser:
                alegePozaProfil();
                break;
        }
    }

    private void seteazaDialogSchimbaParola() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.schimba_parola));

        View view = getLayoutInflater().inflate(R.layout.dialog_schimba_parola, null);
        TextInputEditText tietParolaActuala = view.findViewById(R.id.tietParolaActuala);
        TextInputEditText tietParolaNoua = view.findViewById(R.id.tietParolaNoua);
        TextInputEditText tietConfirmareParolaNoua = view.findViewById(R.id.tietConfirmareParolaNoua);
        AppCompatButton btnSalveaza = view.findViewById(R.id.btnSalveaza);
        AppCompatButton btnRenunta = view.findViewById(R.id.btnRenunta);

        progressBar = view.findViewById(R.id.progressBar);

        btnRenunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogParola.dismiss();
            }
        });

        btnSalveaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parolaActuala = tietParolaActuala.getText().toString().trim();
                String parolaNoua = tietParolaNoua.getText().toString().trim();
                String confirmareParolaNoua = tietConfirmareParolaNoua.getText().toString().trim();

                if (parolaActuala.isEmpty()) {
                    tietParolaActuala.setError(getString(R.string.err_empty_parola));
                    tietParolaActuala.requestFocus();
                    return;
                }
                if (parolaActuala.length() < 6) {
                    tietParolaActuala.setError(getString(R.string.err_not_valid_parola));
                    tietParolaActuala.requestFocus();
                    return;
                }

                if (parolaNoua.isEmpty()) {
                    tietParolaNoua.setError(getString(R.string.err_empty_parola));
                    tietParolaNoua.requestFocus();
                    return;
                }
                if (parolaNoua.length() < 6) {
                    tietParolaNoua.setError(getString(R.string.err_not_valid_parola));
                    tietParolaNoua.requestFocus();
                    return;
                }

                if (confirmareParolaNoua.isEmpty()) {
                    tietConfirmareParolaNoua.setError(getString(R.string.err_empty_confirmare_parola));
                    tietConfirmareParolaNoua.requestFocus();
                    return;
                }
                if (!confirmareParolaNoua.equals(parolaNoua)) {
                    tietConfirmareParolaNoua.setError(getString(R.string.err_not_valid_confirmare_parola));
                    tietConfirmareParolaNoua.requestFocus();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(pacientConectat.getEmail(), parolaActuala);
                loading(true);
                pacientConectat.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    pacientConectat.updatePassword(parolaNoua).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loading(false);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Parola a fost actualizată!", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Log.e("schimbareParola", task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(),
                                                        "A intervenit o eroare. Parola nu a fost schimbată!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });


                                    tietParolaActuala.setText("");
                                    tietParolaNoua.setText("");
                                    tietConfirmareParolaNoua.setText("");

                                    dialogParola.dismiss();
                                } else {
                                    loading(false);
                                    Log.e("reautentificareUser", task.getException().getMessage());
                                    tietParolaActuala.setError("Parola nu este corectă!");
                                    tietParolaActuala.requestFocus();
                                }
                            }
                        });
            }
        });

        builder.setView(view);
        dialogParola = builder.create();
        dialogParola.setCanceledOnTouchOutside(false);
    }

    private void seteazaDialogSchimbaEmail() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.schimba_email));

        View view = getLayoutInflater().inflate(R.layout.dialog_schimba_email, null);
        TextInputEditText tietParola = view.findViewById(R.id.tietParola);
        TextInputEditText tietEmail = view.findViewById(R.id.tietEmail);

        AppCompatButton btnSalveaza = view.findViewById(R.id.btnSalveaza);
        AppCompatButton btnRenunta = view.findViewById(R.id.btnRenunta);

        progressBar = view.findViewById(R.id.progressBar);

        btnRenunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogEmail.dismiss();
            }
        });

        btnSalveaza.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = tietEmail.getText().toString().trim();
                String parola = tietParola.getText().toString().trim();
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

                if (parola.isEmpty()) {
                    tietParola.setError(getString(R.string.err_empty_parola));
                    tietParola.requestFocus();
                    return;
                }
                if (parola.length() < 6) {
                    tietParola.setError(getString(R.string.err_not_valid_parola));
                    tietParola.requestFocus();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(pacientConectat.getEmail(), parola);
                loading(true);
                pacientConectat.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    pacientConectat.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loading(false);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(), "Emailul a fost actualizat!", Toast.LENGTH_SHORT).show();
                                                tietEmailPacient.setText(email);
                                                referintaUserConectat.child("adresaEmail").setValue(email);
                                            } else {
                                                Log.e("schimbareEmail", task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(), "A intervenit o eroare. Emailul nu a fost schimbat!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    tietEmail.setText("");
                                    tietParola.setText("");

                                    dialogEmail.dismiss();
                                } else {
                                    loading(false);
                                    Log.e("reautentificareUser", task.getException().getMessage());
                                    tietParola.setError("Parola nu este corectă!");
                                    tietParola.requestFocus();
                                }
                            }
                        });
            }
        });

        builder.setView(view);
        dialogEmail = builder.create();
        dialogEmail.setCanceledOnTouchOutside(false);
    }

    private void seteazaDialogStergeCont() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.sterge_contul));

        View view = getLayoutInflater().inflate(R.layout.dialog_sterge_cont, null);
        TextInputEditText tietParola = view.findViewById(R.id.tietParola);

        AppCompatButton btnDa = view.findViewById(R.id.btnDa);
        AppCompatButton btnNu = view.findViewById(R.id.btnNu);

        progressBar = view.findViewById(R.id.progressBar);

        btnNu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogStergereCont.dismiss();
            }
        });

        btnDa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parola = tietParola.getText().toString().trim();
                if (parola.isEmpty()) {
                    tietParola.setError(getString(R.string.err_empty_parola));
                    tietParola.requestFocus();
                    return;
                }
                if (parola.length() < 6) {
                    tietParola.setError(getString(R.string.err_not_valid_parola));
                    tietParola.requestFocus();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(pacientConectat.getEmail(), parola);
                loading(true);
                pacientConectat.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    pacientConectat.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loading(false);
                                            if (task.isSuccessful()) {
                                                /*/todo nush cum sa fac cand dau start sa nu mai apara credentialele si cand ies din ap si intru iar sa nu ma mai duca la profil...
//                                                SharedPreferences preferinteConectare = getSharedPreferences("salveazaDateConectare", MODE_PRIVATE);
//                                                SharedPreferences.Editor preferinteConectareEditor = preferinteConectare.edit();
//                                                preferinteConectareEditor.clear();
//                                                preferinteConectareEditor.commit();*/

                                                Toast.makeText(getApplicationContext(), "Contul a fost șters cu succes!",
                                                        Toast.LENGTH_SHORT).show();
                                                Log.d("stergereCont", "Contul a fost sters.");
                                                referintaUserConectat.child("contSters").setValue(true);
                                                startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                                                finish();
                                            } else {
                                                Log.e("stergereCont", task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(),
                                                        "A intervenit o eroare. Contul nu a putut fi sters!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    tietParola.setText("");
                                    dialogStergereCont.dismiss();
                                    startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                                    finish();
                                } else {
                                    loading(false);
                                    Log.e("reautentificareUser", task.getException().getMessage());
                                    tietParola.setError("Parola nu este corectă!");
                                    tietParola.requestFocus();
                                }
                            }
                        });
            }
        });

        builder.setView(view);
        dialogStergereCont = builder.create();
        dialogStergereCont.setCanceledOnTouchOutside(false);
    }

    private void loading(@NonNull Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private void alegePozaProfil() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // in data am poza aleasa
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            ciwPozaProfilPacient.setImageURI(uri); // setez poza pe care a ales-o
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void actualizeazaDate() {
        if (inputValid()) {
            String nume = tietNumePacient.getText().toString().trim();
            String prenume = tietPrenumePacient.getText().toString().trim();
            String grupaSange = actvGrupeSange.getText().toString();
            long nrTelefon = Long.parseLong("4" + tietNrTelefonPacient.getText().toString().trim());
            String adresa = tietAdresa.getText().toString().trim();

            double greutate;
            if (tietGreutate.getText().toString().isEmpty()) {
                greutate = 0.0;
                tietGreutate.setText(String.valueOf(greutate));
            } else {
                greutate = Double.parseDouble(tietGreutate.getText().toString().trim());
            }

            double inaltime;
            if (tietInaltime.getText().toString().isEmpty()) {
                inaltime = 0.0;
                tietInaltime.setText(String.valueOf(inaltime));
            } else {
                inaltime = Double.parseDouble(tietInaltime.getText().toString().trim());
            }

            if (nume.equals(pacient.getNume()) && prenume.equals(pacient.getPrenume()) &&
                    grupaSange.equals(pacient.getGrupaSange()) && nrTelefon == pacient.getNrTelefon()
                    && adresa.equals(pacient.getAdresa()) && greutate == pacient.getGreutate()
                    && inaltime == pacient.getInaltime() && uri == null) {

                seteazaAccesibilitatea(false);
                seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
                ciwPozaProfilPacient.setOnClickListener(null);
                Toast.makeText(getApplicationContext(), "Informatiile nu au fost modificate!", Toast.LENGTH_SHORT).show();
                return;
            }

            referintaUserConectat.child("nume").setValue(nume);
            referintaUserConectat.child("prenume").setValue(prenume);
            referintaUserConectat.child("grupaSange").setValue(grupaSange);
            referintaUserConectat.child("nrTelefon").setValue(nrTelefon);
            referintaUserConectat.child("greutate").setValue(greutate);
            referintaUserConectat.child("inaltime").setValue(inaltime);
            if (adresa.equals("")) {
                referintaUserConectat.child("adresa").setValue(getString(R.string.nespecificat));
                tietAdresa.setText(getString(R.string.nespecificat));
            } else {
                referintaUserConectat.child("adresa").setValue(adresa);
            }

            if (uri != null) {
                incarcaPoza();
            }

            seteazaAccesibilitatea(false);
            seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
            ciwPozaProfilPacient.setOnClickListener(null);
            Toast.makeText(getApplicationContext(), "Datele au fost actualizate!", Toast.LENGTH_SHORT).show();

        }
    }

    private boolean inputValid() {
        Pattern pattern;
        Matcher matcher;

        if (tietNumePacient.getText().toString().trim().isEmpty()) {
            tietNumePacient.setError(getString(R.string.err_empty_nume));
            tietNumePacient.requestFocus();
            return false;
        }

        if (tietPrenumePacient.getText().toString().trim().isEmpty()) {
            tietPrenumePacient.setError(getString(R.string.err_empty_prenume));
            tietPrenumePacient.requestFocus();
            return false;
        }


        if (tietNrTelefonPacient.getText().toString().trim().isEmpty()) {
            tietNrTelefonPacient.setError(getString(R.string.err_empty_telefon));
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
        matcher = pattern.matcher(tietNrTelefonPacient.getText().toString().trim());
        if (!matcher.matches()) {
            tietNrTelefonPacient.setError(getString(R.string.err_not_valid_telefon));
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        if (!tietGreutate.getText().toString().isEmpty() && !tietGreutate.getText().toString().equals(getString(R.string.default_empty_double))) {
            pattern = Pattern.compile(getString(R.string.pattern_greutate));
            matcher = pattern.matcher(tietGreutate.getText().toString());
            if (!matcher.matches()) {
                tietGreutate.setError(getString(R.string.err_not_valid_greutate));
                tietGreutate.requestFocus();
                return false;
            }

        }

        if (!tietInaltime.getText().toString().isEmpty() && !tietInaltime.getText().toString().equals(getString(R.string.default_empty_double))) {

            pattern = Pattern.compile(getString(R.string.pattern_inaltime));
            matcher = pattern.matcher(tietInaltime.getText().toString());
            if (!matcher.matches()) {
                tietInaltime.setError(getString(R.string.err_not_valid_inaltime));
                tietInaltime.requestFocus();
                return false;
            }
        }


        return true;
    }

    private void incarcaPoza() {
        StorageReference caleFisier = FirebaseStorage.getInstance().getReference()
                .child("poze de profil").child(idUserConectat);
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, out);
        byte[] bytes = out.toByteArray();
        UploadTask upload = caleFisier.putBytes(bytes);

        upload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Nu s-a putut încărca poza de profil!", Toast.LENGTH_SHORT).show();
            }
        });

        upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> rezultat = taskSnapshot.getStorage().getDownloadUrl();
                    rezultat.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String urlPoza = uri.toString();
                            Map mapPoza = new HashMap();
                            mapPoza.put("urlPozaProfil", urlPoza);

                            //si abia aici salvez url pozei in firebase....
                            referintaUserConectat.updateChildren(mapPoza).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Log.i("incarcarePoza", "Poza a fost incarcata cu succes!");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Poza de profil nu a putut fi incarcata!", Toast.LENGTH_SHORT).show();
                                        Log.e("incarcarePoza", task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void seteazaVizibilitateButoane(int v1, int v2) {
        btnModificaDate.setVisibility(v1);
        llButoane.setVisibility(v2);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void seteazaAccesibilitatea(boolean b) {
        tietNumePacient.setEnabled(b);
        tietPrenumePacient.setEnabled(b);
        tietAdresa.setEnabled(b);
        actvGrupeSange.setEnabled(b);
//        actvGrupeSange.setDropDownHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        tietNrTelefonPacient.setEnabled(b);
        tietGreutate.setEnabled(b);
        tietInaltime.setEnabled(b);
//        tietEmailPacient.setEnabled(b);

        btnSchimbaParola.setEnabled(!b);
        btnSchimbaEmail.setEnabled(!b);
        btnStergeCont.setEnabled(!b);
    }
}