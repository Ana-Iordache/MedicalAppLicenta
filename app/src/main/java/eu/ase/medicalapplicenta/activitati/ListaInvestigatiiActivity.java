package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.InvestigatieAdaptor;
import eu.ase.medicalapplicenta.entitati.Investigatie;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ListaInvestigatiiActivity extends AppCompatActivity {
    public static final String SPECIALITATI = "Specialitati";
    private final FirebaseService firebaseService = new FirebaseService(SPECIALITATI);
    //    ListView lv;
//    ArrayAdapter<Investigatie> arrayAdapter;
    private Spinner spnSpecialitati;
    private ProgressBar progressBar;

    private List<Investigatie> investigatii;
    private RecyclerView rwListaInvestigatii;
    private InvestigatieAdaptor investigatieAdaptor;
    private RecyclerView.LayoutManager layoutManager;

    private AdapterView.OnItemSelectedListener onItemSelectedListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_investigatii);

        progressBar = findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // setez un button de back ca sa ma pot intoarce in pagina principala
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        spnSpecialitati = findViewById(R.id.spnSpecialitati);

        firebaseService.preiaDateDinFirebase(preiaSpecialitati());

        rwListaInvestigatii = findViewById(R.id.rwListaInvestigatii);

        layoutManager = new LinearLayoutManager(this);
        rwListaInvestigatii.setLayoutManager(layoutManager);

        investigatii = new ArrayList<>();
        investigatieAdaptor = new InvestigatieAdaptor(investigatii, this);
        rwListaInvestigatii.setAdapter(investigatieAdaptor);
//        lv = findViewById(R.id.lv);

    }

    private ValueEventListener preiaSpecialitati() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Specialitate> specialitati = new ArrayList<>();
                List<String> denumiriSpecialitati = new ArrayList<>();
                denumiriSpecialitati.add("Selectati specialitatea");
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Specialitate s = dataSnapshot.getValue(Specialitate.class);
                    specialitati.add(s);
                    denumiriSpecialitati.add(s.getDenumire());
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.support_simple_spinner_dropdown_item, denumiriSpecialitati);
                spnSpecialitati.setAdapter(adapter);

                loading(false);

                spnSpecialitati.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        for (Specialitate s : specialitati)
                            if (spnSpecialitati.getItemAtPosition(i).toString().equals(s.getDenumire())) {
                                investigatii = s.getInvestigatii();
                                investigatieAdaptor = new InvestigatieAdaptor(investigatii, getApplicationContext());
                                rwListaInvestigatii.setAdapter(investigatieAdaptor);
                                //todo poate gasesc altceva
                                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rwListaInvestigatii.getContext(),
                                        DividerItemDecoration.VERTICAL);
                                rwListaInvestigatii.addItemDecoration(dividerItemDecoration);
//                                arrayAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, s.getInvestigatii());
//                                lv.setAdapter(arrayAdapter);
//                                arrayAdapter.notifyDataSetChanged(); nu trb
                                break;
                            }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
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

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

}