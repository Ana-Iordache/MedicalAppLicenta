package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.InvestigatieAdaptor;
import eu.ase.medicalapplicenta.entitati.Investigatie;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ListaInvestigatiiActivity extends AppCompatActivity {
    public static final String SPECIALITATI = "Specialitati";
    private final FirebaseService firebaseService = new FirebaseService(SPECIALITATI);

    private AutoCompleteTextView actvSpecialitati;
    private ProgressBar progressBar;

    private Toolbar toolbar;

    private LinearLayout llSelectatiSpecialitatea;

    private List<Investigatie> investigatii;
    private RecyclerView rwListaInvestigatii;
    private InvestigatieAdaptor investigatieAdaptor;
    private RecyclerView.LayoutManager layoutManager;

    private AdapterView.OnItemSelectedListener onItemSelectedListener;

    private EditText etCautaInvestigatie;
    private RelativeLayout rlNicioInvestigatie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_investigatii);

        initializeazaAtribute();

        seteazaToolbar();

        firebaseService.preiaDateDinFirebase(preiaSpecialitati());

        seteazaRecyclerView();

    }

    private void seteazaRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        rwListaInvestigatii.setLayoutManager(layoutManager);
        investigatieAdaptor = new InvestigatieAdaptor(investigatii, this);
        rwListaInvestigatii.setAdapter(investigatieAdaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        actvSpecialitati = findViewById(R.id.actvSpecialitati);
        llSelectatiSpecialitatea = findViewById(R.id.llSelectatiSpecialitatea);
        rwListaInvestigatii = findViewById(R.id.rwListaInvestigatii);
        investigatii = new ArrayList<>();
        etCautaInvestigatie = findViewById(R.id.etCautaInvestigatie);
        rlNicioInvestigatie = findViewById(R.id.rlNicioInvestigatie);
    }

    private ValueEventListener preiaSpecialitati() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Specialitate> specialitati = new ArrayList<>();
                List<String> denumiriSpecialitati = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Specialitate s = dataSnapshot.getValue(Specialitate.class);
                    specialitati.add(s);
                    denumiriSpecialitati.add(s.getDenumire());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.dropdown_item, denumiriSpecialitati);
                actvSpecialitati.setAdapter(adapter);
                loading(false);

                actvSpecialitati.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        etCautaInvestigatie.setFocusable(true);
                        etCautaInvestigatie.setFocusableInTouchMode(true);
                        etCautaInvestigatie.setText("");
                        rlNicioInvestigatie.setVisibility(View.GONE);
                        for (Specialitate s : specialitati)
                            if (actvSpecialitati.getText().toString().equals(s.getDenumire())) {
                                investigatii = s.getInvestigatii();
                                seteazaAdaptor(investigatii);

                                rwListaInvestigatii.setVisibility(View.VISIBLE);
                                llSelectatiSpecialitatea.setVisibility(View.GONE);
                                break;
                            }
                    }
                });

                etCautaInvestigatie.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String textCautare = charSequence.toString().toLowerCase();
                        List<Investigatie> investigatiiiFiltered = investigatii.stream()
                                .filter(inv -> inv.getDenumire().toLowerCase().contains(textCautare))
                                .collect(Collectors.toList());

                        if (investigatiiiFiltered.isEmpty()) {
                            rlNicioInvestigatie.setVisibility(View.VISIBLE);
                            rwListaInvestigatii.setVisibility(View.GONE);
                        } else {
                            rlNicioInvestigatie.setVisibility(View.GONE);
                            rwListaInvestigatii.setVisibility(View.VISIBLE);
                            seteazaAdaptor(investigatiiiFiltered);
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
            }
        };
    }

    private void seteazaAdaptor(List<Investigatie> investigatii) {
        investigatieAdaptor = new InvestigatieAdaptor(investigatii, getApplicationContext());
        rwListaInvestigatii.setAdapter(investigatieAdaptor);
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

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

}