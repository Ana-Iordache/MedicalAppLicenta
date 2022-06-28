package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.MedicAdaptor;
import eu.ase.medicalapplicenta.adaptori.MedicConversatieAdaptor;
import eu.ase.medicalapplicenta.adaptori.PacientAdaptor;
import eu.ase.medicalapplicenta.adaptori.PacientConversatieAdaptor;
import eu.ase.medicalapplicenta.entitati.Conversatie;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ConversatiiActivity extends AppCompatActivity implements View.OnClickListener, MedicAdaptor.OnDoctorClickListener, PacientAdaptor.OnPacientClickListener {
    public static final String CONVERSATIE_NOUA = "conversatieNoua";

    private final FirebaseService firebaseServiceConversatii = new FirebaseService("Conversatii");
    private final FirebaseService firebaseServiceMedici = new FirebaseService("Medici");
    private final FirebaseService firebaseServicePacienti = new FirebaseService("Pacienti");
    private String idUserConectat;

    private Toolbar toolbar;
    private FloatingActionButton fabConversatieNoua;

    private RelativeLayout rlNicioConversatie;
    private ProgressBar progressBar;

    private List<String> iduriMedici = new ArrayList<>();
    private List<String> iduriPacienti = new ArrayList<>();

    private RecyclerView rwConversatii;
    private List<Medic> medici = new ArrayList<>();
    private List<Pacient> pacienti = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    //    private MedicAdaptor medicAdaptor;
    private MedicConversatieAdaptor medicConversatieAdaptor;
    //    private PacientAdaptor pacientAdaptor;
    private PacientConversatieAdaptor pacientConversatieAdaptor;

    private String tipUtilizator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversatii);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaTipUtilizator();

        seteazaRecyclerView();

        if (tipUtilizator.equals(MainActivity.PACIENT)) {
            firebaseServiceConversatii.preiaDateDinFirebase(preiaIduriMedici());
        } else {
            firebaseServiceConversatii.preiaDateDinFirebase(preiaIduriPacienti());
        }

        fabConversatieNoua.setOnClickListener(this);
    }


    private void seteazaTipUtilizator() {
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.PACIENT)) {
            tipUtilizator = intent.getStringExtra(MainActivity.PACIENT);
        } else {
            tipUtilizator = intent.getStringExtra(HomeMedicActivity.MEDIC);
        }
    }

    private ValueEventListener preiaIduriPacienti() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Conversatie conversatie = dataSnapshot.getValue(Conversatie.class);
                    if (conversatie.getMesaje().get(0).getIdEmitator().equals(idUserConectat)) {
                        iduriPacienti.add(conversatie.getMesaje().get(0).getIdReceptor());
                    } else if (conversatie.getMesaje().get(0).getIdReceptor().equals(idUserConectat)) {
                        iduriPacienti.add(conversatie.getMesaje().get(0).getIdEmitator());
                    }
                }

                if (iduriPacienti.isEmpty()) {
                    loading(false);
                    rlNicioConversatie.setVisibility(View.VISIBLE);
                } else {
                    firebaseServicePacienti.preiaDateDinFirebase(preiaPacienti());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener preiaIduriMedici() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Conversatie conversatie = dataSnapshot.getValue(Conversatie.class);
                    if (conversatie.getMesaje().get(0).getIdEmitator().equals(idUserConectat)) {
                        iduriMedici.add(conversatie.getMesaje().get(0).getIdReceptor());
                    } else if (conversatie.getMesaje().get(0).getIdReceptor().equals(idUserConectat)) {
                        iduriMedici.add(conversatie.getMesaje().get(0).getIdEmitator());
                    }
                }

                if (iduriMedici.isEmpty()) {
                    loading(false);
                    rlNicioConversatie.setVisibility(View.VISIBLE);
                } else {
                    firebaseServiceMedici.preiaDateDinFirebase(preiaMedici());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener preiaPacienti() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pacienti.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pacient pacient = dataSnapshot.getValue(Pacient.class);
                    if (iduriPacienti.contains(pacient.getIdPacient())) {
                        pacienti.add(pacient);
                    }
                }

//                pacientAdaptor.notifyDataSetChanged();
//                seteazaAdaptorPacienti();
                rwConversatii.setVisibility(View.VISIBLE);

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener preiaMedici() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medici.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Medic medic = dataSnapshot.getValue(Medic.class);
                    if (iduriMedici.contains(medic.getIdMedic())) {
                        medici.add(medic);
                    }
                }

//                seteazaAdaptorMedici();
                rwConversatii.setVisibility(View.VISIBLE);

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void seteazaRecyclerView() {
        rwConversatii.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwConversatii.setLayoutManager(layoutManager);

        if (tipUtilizator.equals(MainActivity.PACIENT)) {
            seteazaAdaptorMedici();
        } else {
            seteazaAdaptorPacienti();
        }
    }

    private void seteazaAdaptorPacienti() {
        pacientConversatieAdaptor = new PacientConversatieAdaptor(pacienti, this, this);
        rwConversatii.setAdapter(pacientConversatieAdaptor);
    }

    private void seteazaAdaptorMedici() {
//        medicAdaptor = new MedicAdaptor(medici, this, this);
//        rwConversatii.setAdapter(medicAdaptor);

        medicConversatieAdaptor = new MedicConversatieAdaptor(medici, this, this);
        rwConversatii.setAdapter(medicConversatieAdaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        fabConversatieNoua = findViewById(R.id.fabConversatieNoua);
        rlNicioConversatie = findViewById(R.id.rlNicioConversatie);
        rwConversatii = findViewById(R.id.rwConversatii);
        idUserConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
        progressBar = findViewById(R.id.progressBar);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabConversatieNoua:
                if (tipUtilizator.equals(MainActivity.PACIENT)) {
                    startActivity(new Intent(getApplicationContext(), ListaMediciActivity.class).putExtra(CONVERSATIE_NOUA, ""));
                } else {
                    startActivity(new Intent(getApplicationContext(), ListaPacientiActivity.class).putExtra(CONVERSATIE_NOUA, ""));
                }
                break;
        }
    }

    @Override
    public void onPacientClick(int position) {
        Pacient pacient = pacienti.get(position);
        startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(ListaPacientiActivity.PACIENT, pacient));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onDoctorClick(int position) {
        Medic medic = medici.get(position);
        startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(ListaMediciActivity.MEDIC, medic));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }
}