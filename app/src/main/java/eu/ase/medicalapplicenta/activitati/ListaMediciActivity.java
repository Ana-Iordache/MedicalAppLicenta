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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.MedicAdaptor;
import eu.ase.medicalapplicenta.adaptori.ProgramAdaptor;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ListaMediciActivity extends AppCompatActivity implements MedicAdaptor.OnDoctorClickListener {
    public static final String SPECIALITATI = "Specialitati";
    public static final String MEDICI = "Medici";
    public static final String ORE_DISPONIBILE = "oreDisponibile";
    public static final String INFORMATII_MEDIC = "informatiiMedic";
    private final FirebaseService firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
    private final FirebaseService firebaseServiceMedici = new FirebaseService(MEDICI);
    AppCompatButton btnProgramare;
    //    private ListView lwListaMedici;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private List<Medic> medici;
    private RecyclerView rwListaMedici;
    private MedicAdaptor adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FrameLayout flInfoMedic;
    //    MedicAdaptor.OnDoctorClickListener onDoctorClickListener;
    private AutoCompleteTextView actvSpecialitati;

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

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        //!!!cand imi da eroare aici de nullNullPointerException inseamna ca nu am pus <Toolbar/> in xml
        // setez un button de back ca sa ma pot intoarce in pagina principala
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        medici = new ArrayList<>();
        rwListaMedici = findViewById(R.id.rwListaMedici);
        flInfoMedic = findViewById(R.id.flInfoMedic);
        actvSpecialitati = findViewById(R.id.actvSpecialitati);
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
                List<Specialitate> specialitati = new ArrayList<>();
                List<String> denumiriSpecialitati = new ArrayList<>();

                denumiriSpecialitati.add(getString(R.string.toate_specialitatile));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Specialitate s = dataSnapshot.getValue(Specialitate.class);
                    specialitati.add(s);
                    denumiriSpecialitati.add(s.getDenumire());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item, denumiriSpecialitati);
                actvSpecialitati.setAdapter(adapter);

                actvSpecialitati.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i != 0) {
                            List<Medic> mediciFiltered = medici.stream().
                                    filter(medic -> medic.getIdSpecialitate().equals(specialitati.get(i - 1).getIdSpecialitate()))
                                    .collect(Collectors.toList());

                            seteazaAdaptorMedici(mediciFiltered);
                        } else seteazaAdaptorMedici(medici);
                    }
                });

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
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medici.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Medic m = dataSnapshot.getValue(Medic.class);
                    medici.add(m);
                }

                seteazaAdaptorMedici(medici);
//                adapter.notifyDataSetChanged(); nu cred ca trb

//                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rwListaMedici.getContext(), DividerItemDecoration.VERTICAL);
//                rwListaMedici.addItemDecoration(dividerItemDecoration);

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

    private void seteazaAdaptorMedici(List<Medic> medici) {
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
        Medic medic = medici.get(position);
        final Intent intent = getIntent();
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
//                    btnProgramare.setBackgroundDrawable(getDrawable(R.drawable.background_butoane));
//                    btnProgramare.setTextColor(getColor(R.color.white));
                    startActivity(new Intent(getApplicationContext(), OreDisponibileActivity.class).putExtra(ORE_DISPONIBILE, medic));
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

            String nota = medic.getNotaFeedback() + " din 10";
            tvNota.setText(nota);

            ProgramAdaptor adapter = new ProgramAdaptor(getApplicationContext(), R.layout.element_program_medic, medic.getProgram(), getLayoutInflater());
            lvProgram.setAdapter(adapter);

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();

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

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onResume() {
//        super.onResume();
//        btnProgramare.setBackgroundDrawable(getDrawable(R.drawable.background_butoane_secundare));
//        btnProgramare.setTextColor(getColor(R.color.custom_blue));
//    }
}