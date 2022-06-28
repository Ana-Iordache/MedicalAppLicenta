package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.NotificareAdaptor;
import eu.ase.medicalapplicenta.entitati.Notificare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class NotificariActivity extends AppCompatActivity {
    public static final String NOTIFICARI = "Notificari";
    private final FirebaseService firebaseService = new FirebaseService(NOTIFICARI);
    private final String idUtilizator = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Toolbar toolbar;

    private ProgressBar progressBar;
    private RelativeLayout rlNicioNotificare;

    private RecyclerView rwNotificari;
    private NotificareAdaptor adaptor;
    private RecyclerView.LayoutManager layoutManager;
    private List<Notificare> notificari;

    private String tipUtilizator;

    private ValueEventListener notificariCititeListener;

//    private CardView cwNotificare;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificari);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaTipUtilizator();

        seteazaRecyclerView();

        firebaseService.preiaDateDinFirebase(preiaNotificari());
//        firebaseService.databaseReference.removeEventListener(notificariCititeListener);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        rlNicioNotificare = findViewById(R.id.rlNicioNotificare);
        rwNotificari = findViewById(R.id.rwNotificari);
        notificari = new ArrayList<>();
//        cwNotificare = findViewById(R.id.cwNotificare);
    }

    private void marcheazaNotificariCitite() {
        notificariCititeListener = firebaseService.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notificare notificare = dataSnapshot.getValue(Notificare.class);
                    if (notificare.getIdReceptor().equals(idUtilizator) && !notificare.isNotificareCitita()) {
//                        cwNotificare.setBackgroundColor(getResources().getColor(R.color.custom_light_blue));
                        firebaseService.databaseReference
                                .child(notificare.getIdNotificare())
                                .child("notificareCitita")
                                .setValue(true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void seteazaTipUtilizator() {
        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.PACIENT)) {
            tipUtilizator = intent.getStringExtra(MainActivity.PACIENT);
        } else {
            tipUtilizator = intent.getStringExtra(HomeMedicActivity.MEDIC);
        }
    }

    private ValueEventListener preiaNotificari() {
        loading(true);
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificari = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notificare notificare = dataSnapshot.getValue(Notificare.class);
                    if (notificare.getIdReceptor().equals(idUtilizator)) {
                        notificari.add(notificare);
                    }
                }

                if (!notificari.isEmpty()) {
                    Collections.reverse(notificari);
                    rlNicioNotificare.setVisibility(View.GONE);
                    seteazaAdaptor();
                    rwNotificari.setVisibility(View.VISIBLE);
                } else {
                    rwNotificari.setVisibility(View.GONE);
                    rlNicioNotificare.setVisibility(View.VISIBLE);
                }
                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void seteazaRecyclerView() {
        rwNotificari.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwNotificari.setLayoutManager(layoutManager);
        seteazaAdaptor();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void seteazaAdaptor() {
        adaptor = new NotificareAdaptor(notificari, tipUtilizator, this);
        rwNotificari.setAdapter(adaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
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
        marcheazaNotificariCitite();
//        firebaseService.databaseReference.removeEventListener(notificariCititeListener);
    }
}