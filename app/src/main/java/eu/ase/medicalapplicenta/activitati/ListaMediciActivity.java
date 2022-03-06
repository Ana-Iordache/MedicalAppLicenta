package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.MedicAdaptor;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ListaMediciActivity extends AppCompatActivity  implements MedicAdaptor.OnDoctorClickListener {
    public static final String MEDICI = "Medici";
    public static final String ORE_DISPONIBILE = "oreDisponibile";
    public static final String INFORMATII_MEDIC = "informatiiMedic";
    private final FirebaseService firebaseService = new FirebaseService(MEDICI);
    //    private ListView lwListaMedici;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private List<Medic> medici;
    private RecyclerView rwListaMedici;
    private MedicAdaptor adapter;
    private RecyclerView.LayoutManager layoutManager;

//    MedicAdaptor.OnDoctorClickListener onDoctorClickListener;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_medici);

        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //!!!cand imi da eroare aici de nullNullPointerException inseamna ca nu am pus <Toolbar/> in xml
        // setez un button de back ca sa ma pot intoarce in pagina principala
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        final Intent intent = getIntent();
//        if(intent.hasExtra(ProgramariActivity.ADAUGA_PROGRAMARE)){
//            onDoctorClickListener = new MedicAdaptor.OnDoctorClickListener() {
//                @Override
//                public void onDoctorClick(int position) {
//                    startActivity(new Intent(getApplicationContext(), OreDisponibileActivity.class).putExtra(ORE_DISPONIBILE, medici.get(position)));
//                }
//            };
//        }

        medici = new ArrayList<>();

//        lwListaMedici = findViewById(R.id.lwListaMedici);
        rwListaMedici = findViewById(R.id.rwListaMedici);
        rwListaMedici.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rwListaMedici.setLayoutManager(layoutManager);
        adapter = new MedicAdaptor(medici, this, this);
        rwListaMedici.setAdapter(adapter);

        firebaseService.preiaDateDinFirebase(preiaMedici());


    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private ValueEventListener preiaMedici() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medici.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Medic m = dataSnapshot.getValue(Medic.class);
                    medici.add(m);
                }

                adapter = new MedicAdaptor(medici, getApplicationContext(), ListaMediciActivity.this);
                rwListaMedici.setAdapter(adapter);
//                adapter.notifyDataSetChanged(); nu cred ca trb

                //todo poate gasesc altceva
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rwListaMedici.getContext(), DividerItemDecoration.VERTICAL);
                rwListaMedici.addItemDecoration(dividerItemDecoration);

                loading(false);
//                MedicAdaptor adapter = new MedicAdaptor(getApplicationContext(), R.layout.element_lista_medici, medici, getLayoutInflater());
//                lwListaMedici.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedici", error.getMessage());
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDoctorClick(int position) {
        final Intent intent = getIntent();
        if(intent.hasExtra(ProgramariActivity.ADAUGA_PROGRAMARE)){
            startActivity(new Intent(getApplicationContext(), OreDisponibileActivity.class).putExtra(ORE_DISPONIBILE, medici.get(position)));
        } else if(intent.hasExtra(MainActivity.VIZUALIZARE_MEDICI)){
            startActivity(new Intent(getApplicationContext(), InformatiiMedicActivity.class).putExtra(INFORMATII_MEDIC, medici.get(position)));
        }
    }
}