package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.MedicAdaptor;
import eu.ase.medicalapplicenta.adaptori.ProgramAdaptor;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ListaMediciActivity extends AppCompatActivity implements MedicAdaptor.OnDoctorClickListener, AdapterView.OnItemClickListener, TextWatcher {
    public static final String SPECIALITATI = "Specialitati";
    public static final String MEDICI = "Medici";
    public static final String ORE_DISPONIBILE = "oreDisponibile";
    public static final String MEDIC = "medic";
    public static final String INFO_MEDIC = "infoMedic";
    private final FirebaseService firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
    private final FirebaseService firebaseServiceMedici = new FirebaseService(MEDICI);

    private Intent intent;
    private AppCompatButton btnProgramare;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private List<Medic> medici;
    private RecyclerView rwListaMedici;
    private MedicAdaptor adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AutoCompleteTextView actvSpecialitati;
    private TextView tvTitlu;
    private EditText etCautaMedic;

    private List<Specialitate> specialitati;
    private int specialitateSelectata = 0;
    private List<Medic> mediciFiltered = new ArrayList<>();

    private RelativeLayout rlNiciunMedic;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_medici);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaRecyclerView();

        firebaseServiceSpecialitati.preiaDateDinFirebase(preiaSpecialitati());
        firebaseServiceMedici.preiaDateDinFirebase(preiaMedici());
    }

    private void seteazaRecyclerView() {
        rwListaMedici.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwListaMedici.setLayoutManager(layoutManager);
        adapter = new MedicAdaptor(medici, this, this);
        rwListaMedici.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (intent.hasExtra(ConversatiiActivity.CONVERSATIE_NOUA)) {
            tvTitlu.setText(R.string.title_conversatie_noua);
        } else if (intent.hasExtra(ProgramariActivity.ADAUGA_PROGRAMARE)) {
            tvTitlu.setText(R.string.title_selectati_medicul);
        }
    }

    private void initializeazaAtribute() {
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        medici = new ArrayList<>();
        rwListaMedici = findViewById(R.id.rwListaMedici);
        actvSpecialitati = findViewById(R.id.actvSpecialitati);
        tvTitlu = findViewById(R.id.tvTitlu);
        intent = getIntent();
        etCautaMedic = findViewById(R.id.etCautaMedic);
        rlNiciunMedic = findViewById(R.id.rlNiciunMedic);
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private ValueEventListener preiaSpecialitati() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specialitati = new ArrayList<>();
                List<String> denumiriSpecialitati = new ArrayList<>();

                denumiriSpecialitati.add(getString(R.string.toate_specialitatile));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Specialitate s = dataSnapshot.getValue(Specialitate.class);
                    specialitati.add(s);
                    denumiriSpecialitati.add(s.getDenumire());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item, denumiriSpecialitati);
                actvSpecialitati.setAdapter(adapter);

                actvSpecialitati.setOnItemClickListener(ListaMediciActivity.this);

                etCautaMedic.addTextChangedListener(ListaMediciActivity.this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
            }
        };
    }

    private ValueEventListener preiaMedici() {
        loading(true);
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medici.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Medic m = dataSnapshot.getValue(Medic.class);
                    medici.add(m);
                }

                seteazaAdaptorMedici(medici);

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedici", error.getMessage());
            }
        };
    }

    private void seteazaAdaptorMedici(List<Medic> medici) {
        // elimin intai toti medicii al caror cont este sters
        medici.removeAll(medici.stream().filter(Medic::isContSters).collect(Collectors.toList()));
        adapter = new MedicAdaptor(medici, getApplicationContext(), ListaMediciActivity.this);
        rwListaMedici.setAdapter(adapter);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDoctorClick(int position) {
        Medic medic;
        if (mediciFiltered.isEmpty()) {
            medic = medici.get(position);
        } else {
            medic = mediciFiltered.get(position);
        }
        if (intent.hasExtra(ProgramariActivity.ADAUGA_PROGRAMARE)) {
            startActivity(new Intent(getApplicationContext(), OreDisponibileActivity.class).putExtra(ORE_DISPONIBILE, medic));
        } else if (intent.hasExtra(MainActivity.VIZUALIZARE_MEDICI)) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    ListaMediciActivity.this, R.style.BottomSheetDialogTheme
            );
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(
                            R.layout.bottom_sheet_medic,
                            findViewById(R.id.rlBottomSheet)
                    );
            CircleImageView ciwPozaProfilMedic = view.findViewById(R.id.ciwPozaProfilMedic);
            TextView tvNumeMedic = view.findViewById(R.id.tvNumeMedic);
            TextView tvGradProfesional = view.findViewById(R.id.tvGradProfesional);
            TextView tvSpecialitate = view.findViewById(R.id.tvSpecialitate);
            TextView tvEmail = view.findViewById(R.id.tvEmail);
            TextView tvNota = view.findViewById(R.id.tvNota);
            ListView lvProgram = view.findViewById(R.id.lvProgram);
            btnProgramare = view.findViewById(R.id.btnProgramare);
            btnProgramare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), OreDisponibileActivity.class).putExtra(ORE_DISPONIBILE, medic));
                }
            });

            AppCompatButton btnContact = view.findViewById(R.id.btnContact);
            btnContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(INFO_MEDIC, medic));
                }
            });

            String numeComplet = "Dr. " + medic.getNume() + " " + medic.getPrenume();
            tvNumeMedic.setText(numeComplet);

            if (!medic.getGradProfesional().equals("Nespecificat")) {
                tvGradProfesional.setText(medic.getGradProfesional());
            } else
                tvGradProfesional.setVisibility(View.GONE);

            String urlPozaProfil = medic.getUrlPozaProfil();
            if (!urlPozaProfil.equals("")) {
                Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilMedic);
            }

            firebaseServiceSpecialitati.preiaObiectDinFirebase(preiaSpecialitate(tvSpecialitate), medic.getIdSpecialitate());

            tvEmail.setText(medic.getAdresaEmail());

            String nota = "-";
            if (medic.getNotaFeedback() != 0) {
                nota = MedicAdaptor.NUMBER_FORMAT.format(medic.getNotaFeedback()) + " din 10";
            }
            tvNota.setText(nota);

            ProgramAdaptor adapter = new ProgramAdaptor(getApplicationContext(), R.layout.element_program_medic,
                    medic.getProgram(), getLayoutInflater());
            lvProgram.setAdapter(adapter);

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();

        } else if (intent.hasExtra(ConversatiiActivity.CONVERSATIE_NOUA)) {
            startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(MEDIC, medic));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private ValueEventListener preiaSpecialitate(TextView tvSpecialitate) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Specialitate s = snapshot.getValue(Specialitate.class);
                tvSpecialitate.setText(s.getDenumire());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitate", error.getMessage());
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String filtruCautare = etCautaMedic.getText().toString();
        specialitateSelectata = i;
        if (i != 0) {
            if (!filtruCautare.isEmpty()) { //dar daca am deja ceva in cautare
                filtreazaMediciDupaSpecialitateSiNume(specialitati.get(i - 1).getIdSpecialitate(), filtruCautare);
            } else {
                filtreazaMediciDupaSpecialitate(specialitati.get(i - 1).getIdSpecialitate());
            }

            afiseazaMedici();

        } else {
            if (filtruCautare.isEmpty()) {
                rlNiciunMedic.setVisibility(View.GONE);
                rwListaMedici.setVisibility(View.VISIBLE);
                seteazaAdaptorMedici(medici);
            } else {
                filtreazaMediciDupaNume(filtruCautare);
                afiseazaMedici();
            }
        }

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String stringCurent = charSequence.toString().toLowerCase();
        if (!stringCurent.equals("")) {
//            List<Medic> mediciFiltered;

            if (specialitateSelectata == 0) { //daca selectia din actvSpecialitati e pe primul elem (nu am nicio specialiatte selectata)
                filtreazaMediciDupaNume(stringCurent);
            } else { //caut medicii doar de la specialitatea selecatata
                filtreazaMediciDupaSpecialitateSiNume(specialitati.get(specialitateSelectata - 1).getIdSpecialitate(), stringCurent);
            }
            afiseazaMedici();

        } else {
            mediciFiltered.clear();
            if (specialitateSelectata == 0) {
                rlNiciunMedic.setVisibility(View.GONE);
                rwListaMedici.setVisibility(View.VISIBLE);
                seteazaAdaptorMedici(medici);
            } else {
                filtreazaMediciDupaSpecialitate(specialitati.get(specialitateSelectata - 1).getIdSpecialitate());
                afiseazaMedici();
            }
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void afiseazaMedici() {
        if (mediciFiltered.isEmpty()) {
            rlNiciunMedic.setVisibility(View.VISIBLE);
            rwListaMedici.setVisibility(View.GONE);
        } else {
            rlNiciunMedic.setVisibility(View.GONE);
            rwListaMedici.setVisibility(View.VISIBLE);
            seteazaAdaptorMedici(mediciFiltered);
        }
    }

    private void filtreazaMediciDupaSpecialitate(String idSpecialitate) {
        mediciFiltered = medici.stream().
                filter(medic -> medic.getIdSpecialitate().equals(idSpecialitate))
                .collect(Collectors.toList());
    }

    private void filtreazaMediciDupaNume(String stringCautare) {
        mediciFiltered = medici.stream()
                .filter(medic -> medic.getNume().toLowerCase().contains(stringCautare.toLowerCase())
                        || medic.getPrenume().toLowerCase().contains(stringCautare.toLowerCase()))
                .collect(Collectors.toList());
    }

    private void filtreazaMediciDupaSpecialitateSiNume(String idSpecialitate, String stringCautare) {
        mediciFiltered = medici.stream().
                filter(medic -> medic.getIdSpecialitate().equals(idSpecialitate)
                        && (medic.getNume().toLowerCase().contains(stringCautare.toLowerCase())
                        || medic.getPrenume().toLowerCase().contains(stringCautare.toLowerCase())))
                .collect(Collectors.toList());
    }

}