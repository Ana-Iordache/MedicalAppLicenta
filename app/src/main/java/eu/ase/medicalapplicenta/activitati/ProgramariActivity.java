package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProgramariActivity extends AppCompatActivity implements View.OnClickListener, ProgramareAdaptor.OnProgramareClickListener {
    public static final String PROGRAMARI = "Programari";
    public static final String ADAUGA_PROGRAMARE = "adaugaProgramare";
    private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateFormat FORMAT_ORA = new SimpleDateFormat("HH:mm", Locale.US);
    private final FirebaseService firebaseService = new FirebaseService(PROGRAMARI);
    //    private final DatabaseReference referintaDb = firebaseService.databaseReference;
    private final String idUtilizator = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public String tipUtilizator;
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

    //todo cand apas pe o programare sa apara mesaj "apasati lung pentru a anula programarea"
    //si cand apasa lung sa o poata anula

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

        firebaseService.preiaDateDinFirebase(preiaProgramari());
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
        adaptor = new ProgramareAdaptor(programari, tipUtilizator, ProgramariActivity.this, getApplicationContext());
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
                firebaseService.preiaDateDinFirebase(preiaProgramari());
                break;
            case R.id.rbIstoric:
                if (((AppCompatRadioButton) view).isChecked()) {
                    rbIstoric.setTextColor(Color.WHITE);
                    rbViitoare.setTextColor(ContextCompat.getColor(this, R.color.custom_blue));
                }
                firebaseService.preiaDateDinFirebase(preiaProgramari());
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
                            } else if (rbIstoric.isChecked())
                                if (dataProgramarii.before(dataCurenta) || p.getStatus().equals(getString(R.string.status_anulata))) {
                                    programari.add(p);
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
            AlertDialog dialog = new AlertDialog.Builder(ProgramariActivity.this)
                    .setTitle("Confirmare anulare")
                    .setMessage("Anulați programarea din " + programari.get(position).getData() + "?")
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
                            firebaseService.databaseReference
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
//            Toast.makeText(getApplicationContext(), "Pentru a șterge o programare apăsați lung pe aceasta.", Toast.LENGTH_SHORT).show();
    }
}