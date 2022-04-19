package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.ProgramareAdaptor;
import eu.ase.medicalapplicenta.entitati.Feedback;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProgramariActivity extends AppCompatActivity implements View.OnClickListener,
        ProgramareAdaptor.OnProgramareClickListener,
        ProgramareAdaptor.OnProgramareLongClickListener,
        ProgramareAdaptor.OnBtnFeedbackClickListener {
    public static final String PROGRAMARI = "Programari";
    public static final String ADAUGA_PROGRAMARE = "adaugaProgramare";
    public static final String FEEDBACKURI = "Feedbackuri";
    private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateFormat FORMAT_ORA = new SimpleDateFormat("HH:mm", Locale.US);
    private final FirebaseService firebaseServiceProgramari = new FirebaseService(PROGRAMARI);
    //    private final DatabaseReference referintaDb = firebaseService.databaseReference;
    private final String idUtilizator = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public String tipUtilizator;
    private FirebaseService firebaseServiceFeedbackuri = new FirebaseService(FEEDBACKURI);
    //    ListView lv;
    private FloatingActionButton fabAdaugaProgramare;
    private AppCompatRadioButton rbIstoric;
    private AppCompatRadioButton rbViitoare;
    private List<Programare> programari;

    private Date dataCurenta;
//    private Calendar oraCurenta = Calendar.getInstance();

    private RecyclerView rwProgramari;
    private ProgramareAdaptor adaptor;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar progressBar;
    private RelativeLayout ryNicioProgramare;

    private Toolbar toolbar;

    private String statusSelectat;

    private AlertDialog dialogFeedback;
    private TextView tvNota;
    private Programare programare;
    private RadioGroup rgNote;
    private EditText etRecenzie;
    private String idFeedback;

    private List<Feedback> feedbackuri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programari);

        initializeazaAtribute();

        seteazaToolbar();

        fabAdaugaProgramare.setOnClickListener(this);
        rbIstoric.setOnClickListener(this);
        rbViitoare.setOnClickListener(this);

        seteazaTipUtilizator();

        seteazaRecyclerView();

        seteazaDialogFeedback();

        firebaseServiceProgramari.preiaDateDinFirebase(preiaProgramari());
    }

    private void seteazaDialogFeedback() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Acordă feedback medicului");

        View view = getLayoutInflater().inflate(R.layout.dialog_feedback_medic, null);
        rgNote = view.findViewById(R.id.rgNote);

        etRecenzie = view.findViewById(R.id.etRecenzie);
        AppCompatButton btnTrimite = view.findViewById(R.id.btnTrimite);
        AppCompatButton btnRenunta = view.findViewById(R.id.btnRenunta);
        tvNota = view.findViewById(R.id.tvNota);

        rgNote.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                tvNota.setError(null);
            }
        });

        btnRenunta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogFeedback.dismiss();
            }
        });


        btnTrimite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // o sa caut daca exista feedback acordata de pacient pentru medic
                // si daca exista preiau feedback-ul si actualizez nota si adaug recenzia in lista daca e cazul
                // adica daca a completat campul cu experienta avuta

                firebaseServiceFeedbackuri.preiaObiectDinFirebase(preiaFeedback(), idFeedback);

//                firebaseServiceFeedbackuri = new FirebaseService(FEEDBACKURI);

//                List<String> recenzii = new ArrayList<>();
//                int notaAcordata = rgNote.getCheckedRadioButtonId();
//                if (notaAcordata == -1) {
//                    tvNota.setError("Alegeți o notă!");
//                    tvNota.requestFocus();
//                    return;
//                }
//
//                if (etRecenzie.getText().toString().isEmpty()) {
//                    recenzii = null;
//                } else {
//                    recenzii.add(etRecenzie.getText().toString());
//                }
//
//
//                Feedback feedback = new Feedback(idFeedback,
//                        programare.getIdMedic(),
//                        programare.getIdPacient(),
//                        notaAcordata,
//                        recenzii);
//                firebaseServiceFeedbackuri.databaseReference.
//                feedback.setIdFeedback(idFeedback);


//                firebaseServiceFeedbackuri.databaseReference.child(idFeedback).setValue(feedback);
//                firebaseServiceProgramari.databaseReference
//                        .child(programare.getIdProgramare())
//                        .child("feedbackAcordat")
//                        .setValue(true);
//                Toast.makeText(getApplicationContext(), "Feedback-ul a fost trimis!", Toast.LENGTH_SHORT).show();
//                rgNote.clearCheck();
//                dialogFeedback.dismiss();
            }
        });

        builder.setView(view);
        dialogFeedback = builder.create();
    }

    private ValueEventListener preiaNotaFeedback() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Feedback feedback = snapshot.getValue(Feedback.class);
                if (feedback != null) {
                    rgNote.check(feedback.getNota());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener preiaFeedback() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Feedback feedback = snapshot.getValue(Feedback.class);

                int notaAcordata;
                List<String> recenzii;
                if (feedback != null) {
                    notaAcordata = rgNote.getCheckedRadioButtonId();
                    if (feedback.getRecenzii() != null)
                        recenzii = feedback.getRecenzii();
                    else {
                        recenzii = new ArrayList<>();
                        feedback.setRecenzii(recenzii);
                    }

                    if (!etRecenzie.getText().toString().isEmpty()) {
                        recenzii.add(etRecenzie.getText().toString());
                    }

                    feedback.setNota(notaAcordata);
                } else {
                    recenzii = new ArrayList<>();

                    notaAcordata = rgNote.getCheckedRadioButtonId();
                    if (notaAcordata == -1) {
                        tvNota.setError("Alegeți o notă!");
                        tvNota.requestFocus();
                        return;
                    }

                    if (etRecenzie.getText().toString().isEmpty()) {
                        recenzii = null;
                    } else {
                        recenzii.add(etRecenzie.getText().toString());
                    }

                    feedback = new Feedback(idFeedback,
                            programare.getIdMedic(),
                            programare.getIdPacient(),
                            notaAcordata,
                            recenzii);
                }

                firebaseServiceFeedbackuri = new FirebaseService(FEEDBACKURI);
                firebaseServiceFeedbackuri.databaseReference.child(idFeedback).setValue(feedback);
                firebaseServiceProgramari.databaseReference
                        .child(programare.getIdProgramare())
                        .child("feedbackAcordat")
                        .setValue(true);
                Toast.makeText(getApplicationContext(), "Feedback-ul a fost trimis!", Toast.LENGTH_SHORT).show();
                rgNote.clearCheck();
                dialogFeedback.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void seteazaTipUtilizator() {
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.PACIENT)) {
            tipUtilizator = intent.getStringExtra(MainActivity.PACIENT);
        } else {
            fabAdaugaProgramare.setVisibility(View.GONE);
            tipUtilizator = intent.getStringExtra(HomeMedicActivity.MEDIC);
        }
    }

    private void seteazaRecyclerView() {
        rwProgramari.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwProgramari.setLayoutManager(layoutManager);
        seteazaAdaptor();
    }

    private void seteazaAdaptor() {
        adaptor = new ProgramareAdaptor(programari, tipUtilizator,
                ProgramariActivity.this, ProgramariActivity.this,
                ProgramariActivity.this, getApplicationContext());
        rwProgramari.setAdapter(adaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        fabAdaugaProgramare = findViewById(R.id.fabAdaugaProgramare);
        rbIstoric = findViewById(R.id.rbIstoric);
        rbViitoare = findViewById(R.id.rbViitoare);

        try {
            dataCurenta = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(FORMAT_DATA.format(LocalDateTime.now()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        progressBar = findViewById(R.id.progressBar);

        ryNicioProgramare = findViewById(R.id.ryNicioProgramare);
        rwProgramari = findViewById(R.id.rwProgramari);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdaugaProgramare:
                startActivity(new Intent(getApplicationContext(), ListaMediciActivity.class).putExtra(ADAUGA_PROGRAMARE, ""));
                break;
            case R.id.rbViitoare:
                if (((AppCompatRadioButton) view).isChecked()) {
                    rbViitoare.setTextColor(Color.WHITE);
                    rbIstoric.setTextColor(ContextCompat.getColor(this, R.color.custom_blue));
                }
                firebaseServiceProgramari.preiaDateDinFirebase(preiaProgramari());
                break;
            case R.id.rbIstoric:
                if (((AppCompatRadioButton) view).isChecked()) {
                    rbIstoric.setTextColor(Color.WHITE);
                    rbViitoare.setTextColor(ContextCompat.getColor(this, R.color.custom_blue));
                }
                firebaseServiceProgramari.preiaDateDinFirebase(preiaProgramari());
                break;
        }
    }

    private ValueEventListener preiaProgramari() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                programari = new ArrayList<>();
//                programari.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdPacient().equals(idUtilizator) || p.getIdMedic().equals(idUtilizator)) {
                        try {
                            String dataOra = p.getData() + " " + p.getOra();
                            Date dataProgramarii = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(dataOra);
                            //todo poate mai fac frumos..
                            if (rbViitoare.isChecked()) {
                                if (dataProgramarii.after(dataCurenta) && p.getStatus().equals(getString(R.string.status_noua))) {
                                    programari.add(p);
                                }
                            } else if (rbIstoric.isChecked()) {
                                if (dataProgramarii.before(dataCurenta) && p.getStatus().equals(getString(R.string.status_noua))) {
                                    firebaseServiceProgramari.databaseReference
                                            .child(p.getIdProgramare())
                                            .child("status")
                                            .setValue(getString(R.string.status_necompletat));
                                }
                                if (dataProgramarii.before(dataCurenta) || p.getStatus().equals(getString(R.string.status_anulata))) {
                                    programari.add(p);
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }

                if (!programari.isEmpty()) {
                    ryNicioProgramare.setVisibility(View.GONE);
                    seteazaAdaptor();
                    rwProgramari.setVisibility(View.VISIBLE);
                } else {
                    rwProgramari.setVisibility(View.GONE);
                    ryNicioProgramare.setVisibility(View.VISIBLE);
                }

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareProgramari", error.getMessage());
            }
        };
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onProgramareClick(int position) {
        Programare programare = programari.get(position);
        if (programare.getStatus().equals(getString(R.string.status_noua))) {
            Toast.makeText(getApplicationContext(), "Apăsați lung pentru a anula programarea.", Toast.LENGTH_SHORT).show();
        } else if (tipUtilizator.equals(HomeMedicActivity.MEDIC) && programare.getStatus().equals(getString(R.string.status_necompletat))) {
            final String[] statusuri = new String[]{getString(R.string.status_onorata), getString(R.string.status_neonorata)};
            statusSelectat = statusuri[0];
            AlertDialog.Builder builder = new AlertDialog.Builder(ProgramariActivity.this)
                    .setTitle("Setează status programare")
                    .setSingleChoiceItems(statusuri, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            statusSelectat = statusuri[i];
                        }
                    })
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            firebaseServiceProgramari.databaseReference
                                    .child(programare.getIdProgramare())
                                    .child("status")
                                    .setValue(statusSelectat);
                            dialogInterface.dismiss();
                        }
                    });
            builder.show();
        }
    }

    @Override
    public void onProgramareLongClick(int position) {
        Programare programare = programari.get(position);
        if (programare.getStatus().equals(getString(R.string.status_noua))) {
            AlertDialog dialog = new AlertDialog.Builder(ProgramariActivity.this)
                    .setTitle("Confirmare anulare")
                    .setMessage("Anulați programarea din " + programare.getData() + " de la ora " + programare.getOra() + "?")
                    .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            programare.setStatus(getString(R.string.status_anulata));
                            firebaseServiceProgramari.databaseReference
                                    .child(programare.getIdProgramare())
                                    .child("status")
                                    .setValue(getString(R.string.status_anulata));
                            dialogInterface.cancel();
                            Toast.makeText(getApplicationContext(), "Programarea a fost anulată!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create();
            dialog.show();
        }

    }

    @Override
    public void onBtnFeedbackClick(int position) {
        programare = programari.get(position);
        idFeedback = programare.getIdMedic() + programare.getIdPacient();
        firebaseServiceFeedbackuri.preiaObiectDinFirebase(preiaNotaFeedback(), idFeedback);
        tvNota.setError(null);
        dialogFeedback.show();
    }
}