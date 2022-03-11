package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.FacturaAdaptor;
import eu.ase.medicalapplicenta.entitati.Factura;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class FacturiActivity extends AppCompatActivity {
    public static final String PROGRAMARI = "Programari";
    private final String idPacient = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Toolbar toolbar;

    private RecyclerView rwFacturi;
    private FacturaAdaptor adaptor;
    private List<Factura> facturi;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseService firebaseService = new FirebaseService(PROGRAMARI);

    private RelativeLayout ryNicioFactura;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturi);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaRecyclerView();

        firebaseService.preiaDateDinFirebase(preiaFacturi());
    }

    private void seteazaRecyclerView() {
        rwFacturi.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwFacturi.setLayoutManager(layoutManager);
        adaptor = new FacturaAdaptor(facturi);
        rwFacturi.setAdapter(adaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        ryNicioFactura = findViewById(R.id.ryNicioFactura);
        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);

        facturi = new ArrayList<>();
        rwFacturi = findViewById(R.id.rwFacturi);
    }

    private ValueEventListener preiaFacturi() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facturi = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdPacient().equals(idPacient) && p.getFactura() != null) {
                        facturi.add(p.getFactura());
                    }
                }

                if (!facturi.isEmpty()) {
                    ryNicioFactura.setVisibility(View.GONE);
                    adaptor = new FacturaAdaptor(facturi);
                    rwFacturi.setAdapter(adaptor);
                    rwFacturi.setVisibility(View.VISIBLE);
                } else {
                    rwFacturi.setVisibility(View.GONE);
                    ryNicioFactura.setVisibility(View.VISIBLE);
                }

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareFacturi", error.getMessage());
            }
        };
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }
}