package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.PacientAdaptor;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ListaPacientiActivity extends AppCompatActivity implements PacientAdaptor.OnPacientClickListener {
    public static final String PROGRAMARI = "Programari";
    public static final String PACIENTI = "Pacienti";
    public static final String PACIENT = "pacient";
    private final FirebaseService firebaseServiceProgramari = new FirebaseService(PROGRAMARI);
    private final FirebaseService firebaseServicePacienti = new FirebaseService(PACIENTI);
    private final String idUtilizator = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Toolbar toolbar;

    private HashSet<String> iduriPacienti;
    private List<Pacient> pacienti;

    //    private ListView lv;
    private RecyclerView rwListaPacienti;
    private PacientAdaptor adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar progressBar;

    private RelativeLayout rlNiciunPacient;

    private TextView tvTitlu;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pacienti);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaRecyclerView();

        firebaseServiceProgramari.preiaDateDinFirebase(preiaIduriPacienti());
    }

    private void seteazaRecyclerView() {
        rwListaPacienti.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwListaPacienti.setLayoutManager(layoutManager);
        seteazaAdaptorPacienti();
    }

    private void seteazaAdaptorPacienti() {
        adapter = new PacientAdaptor(pacienti, this, this);
        rwListaPacienti.setAdapter(adapter);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
//        lv = findViewById(R.id.lv);
        rwListaPacienti = findViewById(R.id.rwListaPacienti);
        progressBar = findViewById(R.id.progressBar);
        rlNiciunPacient = findViewById(R.id.rlNiciunPacient);
        pacienti = new ArrayList<>();
        tvTitlu = findViewById(R.id.tvTitlu);

        intent = getIntent();
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private ValueEventListener preiaIduriPacienti() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                iduriPacienti = new HashSet<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdMedic().equals(idUtilizator)) {
                        iduriPacienti.add(p.getIdPacient());
                    }
                }

                if (iduriPacienti.isEmpty()) {
                    loading(false);
                    rlNiciunPacient.setVisibility(View.VISIBLE);
                } else {
                    firebaseServicePacienti.preiaDateDinFirebase(preiaPacienti());
                }
//                Toast.makeText(getApplicationContext(), iduriPacienti.toString(), Toast.LENGTH_SHORT).show();
//                List<String> list = new ArrayList<>(iduriPacienti);
//                pacienti.clear();
//                for (String id : list) {
//                    firebaseServicePacienti.preiaObiectDinFirebase(preiaPacient(), id);
//                }

//                try {
//                    Thread.sleep(2000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }

//                seteazaAdaptorPacienti();
//                loading(false);

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
                pacienti = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pacient p = dataSnapshot.getValue(Pacient.class);
                    if (iduriPacienti.contains(p.getIdPacient()))
                        pacienti.add(p);
                }

                seteazaAdaptorPacienti();
                rwListaPacienti.setVisibility(View.VISIBLE);
                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

//    private ValueEventListener preiaPacient() {
//        return new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Pacient p = snapshot.getValue(Pacient.class);
////                Toast.makeText(getApplicationContext(), p.toString(), Toast.LENGTH_SHORT).show();
//                pacienti.add(p);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
//    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (intent.hasExtra(HomeMedicActivity.VIZUALIZARE_FEEDBACK)) {
            tvTitlu.setText(getString(R.string.feedback_pacienti));
        }
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

    @Override
    public void onPacientClick(int position) {
        Pacient pacient = pacienti.get(position);
        if (intent.hasExtra(HomeMedicActivity.VIZUALIZARE_FEEDBACK)) {
            startActivity(new Intent(getApplicationContext(), FeedbackPacientActivity.class).putExtra(PACIENT, pacient));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        } else if (intent.hasExtra(HomeMedicActivity.VIZUALIZARE_PACIENTI)) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(ListaPacientiActivity.this,
                    R.style.BottomSheetDialogTheme);
            View view = LayoutInflater.from(getApplicationContext())
                    .inflate(
                            R.layout.bottom_sheet_pacient,
                            findViewById(R.id.rlBottomSheet)
                    );
            CircleImageView ciwPozaProfilPacient = view.findViewById(R.id.ciwPozaProfilPacient);
            TextView tvNumePacient = view.findViewById(R.id.tvNumePacient);
            TextView tvSex = view.findViewById(R.id.tvSex);
            TextView tvCnp = view.findViewById(R.id.tvCnp);
            TextView tvVarsta = view.findViewById(R.id.tvVarsta);
            TextView tvNrTel = view.findViewById(R.id.tvNrTel);
            TextView tvAdresa = view.findViewById(R.id.tvAdresa);
            TextView tvDataNasterii = view.findViewById(R.id.tvDataNasterii);
            TextView tvGrupaSange = view.findViewById(R.id.tvGrupaSange);
            TextView tvGreutate = view.findViewById(R.id.tvGreutate);
            TextView tvInaltime = view.findViewById(R.id.tvInaltime);
            TextView tvEmail = view.findViewById(R.id.tvEmail);

            String urlPozaProfil = pacient.getUrlPozaProfil();
            if (!urlPozaProfil.equals("")) {
                Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilPacient);
            }

            String numeComplet = pacient.getNume() + " " + pacient.getPrenume();
            tvNumePacient.setText(numeComplet);

            tvSex.setText(pacient.getSex());
            tvCnp.setText(String.valueOf(pacient.getCnp()));
            tvVarsta.setText(String.valueOf(pacient.getVarsta()));
            tvNrTel.setText(String.valueOf(pacient.getNrTelefon()).substring(1));
            tvAdresa.setText(pacient.getAdresa());
            tvDataNasterii.setText(pacient.getDataNasterii());
            tvGrupaSange.setText(pacient.getGrupaSange());
            tvGreutate.setText(String.valueOf(pacient.getGreutate()));
            tvInaltime.setText(String.valueOf(pacient.getInaltime()));
            tvEmail.setText(pacient.getAdresaEmail());

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        } else if (intent.hasExtra(ConversatiiActivity.CONVERSATIE_NOUA)) {
            startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(PACIENT, pacient));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }
}