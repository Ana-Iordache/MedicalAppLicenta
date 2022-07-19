package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.MedicAdaptor;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.entitati.ZiDeLucru;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ProfilMedicActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MEDICI = "Medici";
    public static final String SPECIALITATI = "Specialitati";
    public static final int REQUEST_CODE = 200;
    private CircleImageView ciwPozaProfilMedic;

    private TextInputEditText tietNota;
    private TextInputEditText tietNumeMedic;
    private TextInputEditText tietPrenumeMedic;
    private TextInputEditText tietNrTelefonMedic;
    private TextInputEditText tietEmailMedic;
    private TextInputEditText tietProgramLucru;

    private AutoCompleteTextView actvGradeProfesionale;
    private AutoCompleteTextView actvSpecialitati;

    private Button btnModificaDate;
    private Button btnSalveaza;
    private Button btnRenunta;
    private LinearLayout llButoane;

    private Button btnSchimbaParola;
    private Button btnSchimbaEmail;
    private Button btnStergeCont;

    private Toolbar toolbar;
    private FirebaseUser medicConectat;
    private FirebaseService firebaseServiceMedic = new FirebaseService(MEDICI);
    private FirebaseService firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
    private String idUserConectat;
    private DatabaseReference referintaUserConectat;

    private List<Specialitate> specialitati;
    private List<String> denumiriSpecialitati;
    private List<ZiDeLucru> program;

    private Medic medic;

    private Uri uri;

    private AlertDialog dialogParola;
    private TextInputEditText tietOraInceput;
    private TextInputEditText tietOraSfarsit;
    private AppCompatButton btnAdauga;
    private AutoCompleteTextView actvZileDeLucru;
    private AppCompatButton btnElimina;

    private AlertDialog dialogEmail;
    private AlertDialog dialogStergereCont;
    private AlertDialog dialogProgram;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_medic);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaAdaptorGradeProfesionale();

        seteazaDialogSchimbaParola();
        seteazaDialogSchimbaEmail();
        seteazaDialogStergeCont();
        seteazaDialogProgramLucru();

        btnModificaDate.setOnClickListener(this);
        btnSalveaza.setOnClickListener(this);
        btnRenunta.setOnClickListener(this);
        btnSchimbaParola.setOnClickListener(this);
        btnSchimbaEmail.setOnClickListener(this);
        btnStergeCont.setOnClickListener(this);
        tietProgramLucru.setOnClickListener(this);
        actvZileDeLucru.setOnClickListener(this);

        actvZileDeLucru.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                for (ZiDeLucru zi : program) {
                    if (zi.getZi().equals(actvZileDeLucru.getText().toString())) {
                        btnAdauga.setVisibility(View.GONE);
                        btnElimina.setVisibility(View.VISIBLE);
                        break;
                    }
                    btnElimina.setVisibility(View.GONE);
                    btnAdauga.setVisibility(View.VISIBLE);
                    btnAdauga.setEnabled(true);
                }
            }
        });

        firebaseServiceMedic.preiaObiectDinFirebase(preiaMedic(), idUserConectat);
    }

    private void seteazaDialogProgramLucru() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifică program");

        View view = getLayoutInflater().inflate(R.layout.dialog_program_lucru, null);
        actvZileDeLucru = view.findViewById(R.id.actvZileDeLucru);
        tietOraInceput = view.findViewById(R.id.tietOraInceput);
        tietOraSfarsit = view.findViewById(R.id.tietOraSfarsit);
        btnAdauga = view.findViewById(R.id.btnAdauga);
        btnElimina = view.findViewById(R.id.btnElimina);
        AppCompatButton btnRenunta = view.findViewById(R.id.btnRenunta);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.zile_saptamana));
        actvZileDeLucru.setAdapter(adapter);

        tietOraInceput.setOnClickListener(this);
        tietOraSfarsit.setOnClickListener(this);
        btnAdauga.setOnClickListener(this);
        btnRenunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogProgram.dismiss();
                golesteInputuri();
            }
        });
        btnElimina.setOnClickListener(this);

        builder.setView(view);
        dialogProgram = builder.create();
        dialogProgram.setCanceledOnTouchOutside(false);
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

                tietParolaActuala.clearFocus();
                tietParolaActuala.setText("");
                tietParolaActuala.setError(null);

                tietParolaNoua.clearFocus();
                tietParolaNoua.setText("");
                tietParolaNoua.setError(null);

                tietConfirmareParolaNoua.clearFocus();
                tietConfirmareParolaNoua.setText("");
                tietConfirmareParolaNoua.setError(null);
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

                AuthCredential credential = EmailAuthProvider.getCredential(medicConectat.getEmail(), parolaActuala);
                loading(true);
                medicConectat.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    medicConectat.updatePassword(parolaNoua).addOnCompleteListener(new OnCompleteListener<Void>() {
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

                                    dialogParola.dismiss();

                                    tietParolaActuala.clearFocus();
                                    tietParolaActuala.setText("");
                                    tietParolaActuala.setError(null);

                                    tietParolaNoua.clearFocus();
                                    tietParolaNoua.setText("");
                                    tietParolaNoua.setError(null);

                                    tietConfirmareParolaNoua.clearFocus();
                                    tietConfirmareParolaNoua.setText("");
                                    tietConfirmareParolaNoua.setError(null);
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

                tietEmail.clearFocus();
                tietEmail.setText("");
                tietEmail.setError(null);

                tietParola.clearFocus();
                tietParola.setText("");
                tietParola.setError(null);
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

                Pattern pattern = Pattern.compile(getString(R.string.pattern_email_medic));
                Matcher matcher = pattern.matcher(tietEmail.getText().toString());
                if (!matcher.matches()) {
                    tietEmail.setError(getString(R.string.err_not_valid_email_doctor));
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

                AuthCredential credential = EmailAuthProvider.getCredential(medicConectat.getEmail(), parola);
                loading(true);
                medicConectat.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    medicConectat.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loading(false);
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getApplicationContext(),
                                                        "Emailul a fost actualizat!", Toast.LENGTH_SHORT).show();
                                                tietEmailMedic.setText(email);
                                                referintaUserConectat.child("adresaEmail").setValue(email);
                                            } else {
                                                Log.e("schimbareEmail", task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(),
                                                        "A intervenit o eroare. Emailul nu a fost schimbat!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    dialogEmail.dismiss();

                                    tietEmail.clearFocus();
                                    tietEmail.setText("");
                                    tietEmail.setError(null);

                                    tietParola.clearFocus();
                                    tietParola.setText("");
                                    tietParola.setError(null);
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

                AuthCredential credential = EmailAuthProvider.getCredential(medicConectat.getEmail(), parola);
                loading(true);
                medicConectat.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    medicConectat.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loading(false);
                                            if (task.isSuccessful()) {
                                                //todo nush cum sa fac cand dau start sa nu mai apara credentialele si cand ies din ap si intru iar sa nu ma mai duca la profil...
                                                SharedPreferences preferinteConectare = getSharedPreferences("salveazaDateConectare",
                                                        MODE_PRIVATE);
                                                SharedPreferences.Editor preferinteConectareEditor = preferinteConectare.edit();
                                                preferinteConectareEditor.clear();
                                                preferinteConectareEditor.commit();

                                                Toast.makeText(getApplicationContext(), "Contul a fost șters cu succes!",
                                                        Toast.LENGTH_SHORT).show();
                                                Log.d("stergereCont", "Contul a fost sters.");
                                                referintaUserConectat.child("contSters").setValue(true);
                                                startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                                                finish();
                                            } else {
                                                Log.e("stergereCont", task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(),
                                                        "A intervenit o eroare. Contul nu a putut fi șters!", Toast.LENGTH_SHORT).show();
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

    private void seteazaAdaptorSpecialitati() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                denumiriSpecialitati);
        actvSpecialitati.setAdapter(adapter);
    }

    private void seteazaAdaptorGradeProfesionale() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.grade_profesionale));
        actvGradeProfesionale.setAdapter(adapter);
    }

    private ValueEventListener preiaMedic() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medic = snapshot.getValue(Medic.class);
                if (medic != null) {
                    String nume = medic.getNume();
                    String prenume = medic.getPrenume();
                    long nrTelefon = medic.getNrTelefon();
                    String adresaEmail = medic.getAdresaEmail();
                    double notaFeedback = medic.getNotaFeedback();
                    String gradProfesional = medic.getGradProfesional();
                    String urlPozaProfil = medic.getUrlPozaProfil();

                    program = new ArrayList<>();
                    program.addAll(medic.getProgram());

                    StringBuilder programString = new StringBuilder();
                    for (int i = 0; i < program.size(); i++) {
                        programString.append(program.get(i).toString());
                        if (i != program.size() - 1) {
                            programString.append("\n");
                        }
                    }
                    tietProgramLucru.setText(programString.toString());

                    tietNumeMedic.setText(nume);
                    tietPrenumeMedic.setText(prenume);

                    if (nrTelefon != 0)
                        tietNrTelefonMedic.setText(String.valueOf(nrTelefon).substring(1));
                    else tietNrTelefonMedic.setText("");

                    tietEmailMedic.setText(adresaEmail);
                    if (notaFeedback != 0) {
                        tietNota.setText(MedicAdaptor.NUMBER_FORMAT.format(notaFeedback));
                    } else {
                        tietNota.setText("-");
                    }

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) actvGradeProfesionale.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).equals(gradProfesional)) {
                            actvGradeProfesionale.setText(adapter.getItem(i));
                            break;
                        }
                    }

                    firebaseServiceSpecialitati.preiaDateDinFirebase(preiaSpecialitati());

                    if (!urlPozaProfil.equals("")) {
                        Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilMedic);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedic", error.getMessage());
            }
        };
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        ciwPozaProfilMedic = findViewById(R.id.ciwPozaProfilMedic);

        tietNota = findViewById(R.id.tietNota);
        tietNumeMedic = findViewById(R.id.tietNumeMedic);
        tietPrenumeMedic = findViewById(R.id.tietPrenumeMedic);
        tietNrTelefonMedic = findViewById(R.id.tietNrTelefonMedic);
        tietEmailMedic = findViewById(R.id.tietEmailMedic);
        tietProgramLucru = findViewById(R.id.tietProgramLucru);

        actvGradeProfesionale = findViewById(R.id.actvGradeProfesionale);
        actvSpecialitati = findViewById(R.id.actvSpecialitati);
        specialitati = new ArrayList<>();

        btnModificaDate = findViewById(R.id.btnModificaDate);
        btnSalveaza = findViewById(R.id.btnSalveaza);
        btnRenunta = findViewById(R.id.btnRenunta);
        llButoane = findViewById(R.id.llButoane);

        btnSchimbaParola = findViewById(R.id.btnSchimbaParola);
        btnSchimbaEmail = findViewById(R.id.btnSchimbaEmail);
        btnStergeCont = findViewById(R.id.btnStergeCont);

        medicConectat = FirebaseAuth.getInstance().getCurrentUser();
        idUserConectat = medicConectat.getUid();
        referintaUserConectat = firebaseServiceMedic.databaseReference.child(idUserConectat);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private ValueEventListener preiaSpecialitati() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specialitati.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Specialitate s = dataSnapshot.getValue(Specialitate.class);
                    specialitati.add(s);
                }
                denumiriSpecialitati = new ArrayList<>();
                int poz = 0;
                for (int i = 0; i < specialitati.size(); i++) {
                    if (specialitati.get(i).getIdSpecialitate().equals(medic.getIdSpecialitate())) {
                        poz = i;
                    }
                    denumiriSpecialitati.add(specialitati.get(i).getDenumire());
                }
                seteazaAdaptorSpecialitati();
                actvSpecialitati.setText(denumiriSpecialitati.get(poz));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnModificaDate:
                ciwPozaProfilMedic.setOnClickListener(this);
                seteazaAdaptorGradeProfesionale();
                seteazaAdaptorSpecialitati();
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
                firebaseServiceMedic.preiaObiectDinFirebase(preiaMedic(), idUserConectat);
                seteazaAccesibilitatea(false);
                seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
                ciwPozaProfilMedic.setOnClickListener(null);
                break;
            case R.id.ciwPozaProfilMedic:
                alegePozaProfil();
                break;
            case R.id.tietProgramLucru:
                tietProgramLucru.setError(null);
                dialogProgram.show();
                break;
            case R.id.actvZileDeLucru:
                actvZileDeLucru.setError(null);
                break;
            case R.id.btnElimina:
                AlertDialog dialog = new AlertDialog.Builder(ProfilMedicActivity.this)
                        .setTitle("Confirmare ștergere zi din program")
                        .setMessage("Ștergeți ziua de " + actvZileDeLucru.getText().toString() + " din program?")
                        .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                program.removeIf(zi -> zi.getZi().equals(actvZileDeLucru.getText().toString()));
                                StringBuilder programString = new StringBuilder();
                                for (int j = 0; j < program.size(); j++) {
                                    programString.append(program.get(j).toString());
                                    if (j != program.size() - 1) {
                                        programString.append("\n");
                                    }
                                }
                                tietProgramLucru.setText(programString.toString());
                                dialogInterface.cancel();
                                dialogProgram.dismiss();
                                golesteInputuri();
                            }
                        }).create();
                dialog.show();
                break;
            case R.id.tietOraInceput:
                tietOraInceput.setError(null);
                afiseazaTimePicker(tietOraInceput, getString(R.string.ora_de_inceput));
                break;
            case R.id.tietOraSfarsit:
                tietOraSfarsit.setError(null);
                afiseazaTimePicker(tietOraSfarsit, getString(R.string.ora_de_sfarsit));
                break;
            case R.id.btnAdauga:
//                if (program.stream().anyMatch(zi -> zi.getZi().equals(actvZileDeLucru.getText().toString()))) {
//                    actvZileDeLucru.setError("Ziua de " + actvZileDeLucru.getText().toString() + " există deja în program!");
//                    actvZileDeLucru.requestFocus();
//                    return;
//                }

                if (tietOraInceput.getText().toString().isEmpty()) {
                    //ca sa apara si textul trb sa-i pun din xml focusable pe true
                    //dar asa imi apare tastatura la primuk click pe tiet uof
                    tietOraInceput.setError("Selectați ora de început!");
                    tietOraInceput.requestFocus();
                    return;
                }

                if (tietOraSfarsit.getText().toString().isEmpty()) {
                    tietOraSfarsit.setError("Selectați ora de sfârșit!");
                    tietOraSfarsit.requestFocus();
                    return;
                }

                ZiDeLucru ziDeLucru = new ZiDeLucru(actvZileDeLucru.getText().toString(), tietOraInceput.getText().toString(),
                        tietOraSfarsit.getText().toString());

                program.add(ziDeLucru); // todo sa sortez lista dupa zilele saptamanii

                StringBuilder programString = new StringBuilder();
                for (int i = 0; i < program.size(); i++) {
                    programString.append(program.get(i).toString());
                    if (i != program.size() - 1) {
                        programString.append("\n");
                    }
                }
                tietProgramLucru.setText(programString.toString());
                dialogProgram.dismiss();
                golesteInputuri();
                break;
        }
    }

    private void golesteInputuri() {
        actvZileDeLucru.setText("");
        actvZileDeLucru.clearFocus();
        actvZileDeLucru.setError(null);

        tietOraInceput.setText("");
        tietOraInceput.setError(null);

        tietOraSfarsit.setText("");
        tietOraSfarsit.setError(null);

        btnAdauga.setVisibility(View.VISIBLE);
        btnAdauga.setEnabled(false);
        btnElimina.setVisibility(View.GONE);
    }


    private void afiseazaTimePicker(TextInputEditText tiet, String titlu) {
        Calendar calendar = Calendar.getInstance();
        Date dataDefault = null;
        try {
            dataDefault = new SimpleDateFormat("HH:mm", Locale.US).parse("08:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dataDefault);
        int ora = calendar.get(Calendar.HOUR);
        int minut = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(ProfilMedicActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int ora, int minut) {
                String timp = convertInt(ora) + ":" + convertInt(minut);
                tiet.setText(timp);
            }
        }, ora, minut, true);
        timePickerDialog.setTitle(titlu);
        timePickerDialog.show();
    }

    private String convertInt(int i) {
        if (i >= 10) {
            return String.valueOf(i);
        }
        return "0" + i;
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
            ciwPozaProfilMedic.setImageURI(uri); // setez poza pe care a ales-o
        }
    }

    private void actualizeazaDate() {
        if (inputValid()) {
            String nume = tietNumeMedic.getText().toString().trim();
            String prenume = tietPrenumeMedic.getText().toString().trim();

            long nrTelefon;
            if (!tietNrTelefonMedic.getText().toString().isEmpty())
                nrTelefon = Long.parseLong("4" + tietNrTelefonMedic.getText().toString().trim());
            else nrTelefon = 0;

            String gradProfesional = actvGradeProfesionale.getText().toString();

            String specialitate = actvSpecialitati.getText().toString();
            String idSpecialitate = "";
            for (Specialitate s : specialitati) {
                if (s.getDenumire().equals(specialitate)) {
                    idSpecialitate = s.getIdSpecialitate();
                    break;
                }
            }

            if (nume.equals(medic.getNume()) && prenume.equals(medic.getPrenume())
                    && nrTelefon == medic.getNrTelefon() && gradProfesional.equals(medic.getGradProfesional())
                    && idSpecialitate.equals(medic.getIdSpecialitate()) && uri == null
                    && program == medic.getProgram()) {

                btnRenunta.callOnClick();
                Toast.makeText(getApplicationContext(), "Informațiile nu au fost modificate!", Toast.LENGTH_SHORT).show();
                return;
            }

            referintaUserConectat.child("nume").setValue(nume);
            referintaUserConectat.child("prenume").setValue(prenume);
            referintaUserConectat.child("nrTelefon").setValue(nrTelefon);
            referintaUserConectat.child("gradProfesional").setValue(gradProfesional);
            referintaUserConectat.child("idSpecialitate").setValue(idSpecialitate);
            referintaUserConectat.child("program").setValue(program);

            if (uri != null) {
                incarcaPoza();
            }

            seteazaAccesibilitatea(false);
            seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
            ciwPozaProfilMedic.setOnClickListener(null);

            Toast.makeText(getApplicationContext(), "Datele au fost actualizate!", Toast.LENGTH_SHORT).show();

        }

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

    private boolean inputValid() {
        if (tietNumeMedic.getText().toString().isEmpty()) {
            tietNumeMedic.setError(getString(R.string.err_empty_nume));
            tietNumeMedic.requestFocus();
            return false;
        }

        if (tietPrenumeMedic.getText().toString().isEmpty()) {
            tietPrenumeMedic.setError(getString(R.string.err_empty_prenume));
            tietPrenumeMedic.requestFocus();
            return false;
        }

        Pattern pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
        Matcher matcher = pattern.matcher(tietNrTelefonMedic.getText().toString());
        if (!tietNrTelefonMedic.getText().toString().isEmpty() && !matcher.matches()) {
            tietNrTelefonMedic.setError(getString(R.string.err_not_valid_telefon));
            tietNrTelefonMedic.requestFocus();
            return false;
        }

        return true;
    }

    private void seteazaVizibilitateButoane(int v1, int v2) {
        btnModificaDate.setVisibility(v1);
        llButoane.setVisibility(v2);
    }

    private void seteazaAccesibilitatea(boolean b) {
        tietNumeMedic.setEnabled(b);
        tietPrenumeMedic.setEnabled(b);
        tietNrTelefonMedic.setEnabled(b);
        tietProgramLucru.setEnabled(b);
        actvGradeProfesionale.setEnabled(b);
        actvSpecialitati.setEnabled(b);

        btnSchimbaParola.setEnabled(!b);
        btnSchimbaEmail.setEnabled(!b);
        btnStergeCont.setEnabled(!b);
    }
}