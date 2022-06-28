package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.FeedbackAdaptor;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class FeedbackPacientActivity extends AppCompatActivity {
    public static final String PROGRAMARI = "Programari";
    private final FirebaseService firebaseService = new FirebaseService(PROGRAMARI);
    private final String idMedicConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String idPacient;
    private Toolbar toolbar;
    private Intent intent;
    private TextView tvTitlu;
    private Pacient pacient;
    private RecyclerView rwListaFeedback;
    private FeedbackAdaptor adaptor;
    private GridLayoutManager gridLayoutManager;
    private List<Programare> programariCuFeedback;
    private RelativeLayout rlNiciunFeedback;
    private ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_pacient);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaRecyclerView();

        firebaseService.preiaDateDinFirebase(preiaProgramariCuFeedback());
    }

    private ValueEventListener preiaProgramariCuFeedback() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                programariCuFeedback.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare programare = dataSnapshot.getValue(Programare.class);
                    if (programare.getIdMedic().equals(idMedicConectat) && programare.getIdPacient().equals(idPacient)
                            && programare.getFeedback() != null) {
                        programariCuFeedback.add(programare);
                    }
                }

                if (programariCuFeedback.isEmpty()) {
                    rlNiciunFeedback.setVisibility(View.VISIBLE);
                    rwListaFeedback.setVisibility(View.GONE);
                } else {
                    rlNiciunFeedback.setVisibility(View.GONE);
                    seteazaAdaptor();
                    rwListaFeedback.setVisibility(View.VISIBLE);
                }

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private void seteazaRecyclerView() {
        rwListaFeedback.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rwListaFeedback.setLayoutManager(gridLayoutManager);
        seteazaAdaptor();
    }

    private void seteazaAdaptor() {
        adaptor = new FeedbackAdaptor(programariCuFeedback);
        rwListaFeedback.setAdapter(adaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String titlu = "Feedback " + pacient.getNume() + " " + pacient.getPrenume();
        tvTitlu.setText(titlu);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        tvTitlu = findViewById(R.id.tvTitlu);
        rwListaFeedback = findViewById(R.id.rwListaFeedback);
        rlNiciunFeedback = findViewById(R.id.rlNiciunFeedback);
        progressBar = findViewById(R.id.progressBar);
        intent = getIntent();
        pacient = (Pacient) intent.getSerializableExtra(ListaPacientiActivity.PACIENT);
        idPacient = pacient.getIdPacient();
        programariCuFeedback = new ArrayList<>();
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
}