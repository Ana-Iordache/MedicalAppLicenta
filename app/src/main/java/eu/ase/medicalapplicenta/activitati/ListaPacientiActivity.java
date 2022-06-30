package eu.ase.medicalapplicenta.activitati;

import static eu.ase.medicalapplicenta.activitati.DocumentePersonaleActivity.BULETIN;
import static eu.ase.medicalapplicenta.activitati.DocumentePersonaleActivity.CARD_DE_SANATATE;
import static eu.ase.medicalapplicenta.activitati.DocumentePersonaleActivity.DOCUMENTE_PACIENTI;
import static eu.ase.medicalapplicenta.activitati.DocumentePersonaleActivity.EXTENSIE_PDF;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.PacientAdaptor;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ListaPacientiActivity extends AppCompatActivity implements PacientAdaptor.OnPacientClickListener, View.OnClickListener {
    public static final String PROGRAMARI = "Programari";
    public static final String PACIENTI = "Pacienti";
    public static final String PACIENT = "pacient";
    private final FirebaseService firebaseServiceProgramari = new FirebaseService(PROGRAMARI);
    private final FirebaseService firebaseServicePacienti = new FirebaseService(PACIENTI);
    private final String idUtilizator = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private StorageReference referintaBuletin;
    private StorageReference referintaCardSanatate;
    private Toolbar toolbar;

    private HashSet<String> iduriPacienti;
    private List<Pacient> pacienti;

    private RecyclerView rwListaPacienti;
    private PacientAdaptor adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar progressBar;

    private RelativeLayout rlNiciunPacient;

    private TextView tvTitlu;

    private Intent intent;

    private Uri uriCardSanatate;
    private Uri uriBuletin;

    private String denumirePdfCardSanatate;
    private String denumirePdfBuletin;

    private EditText etCautaPacient;

    private List<Pacient> pacientiFiltered = new ArrayList<>();

    private AppCompatButton btnMediaNotelor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_pacienti);

        initializeazaAtribute();

        seteazaToolbar();

        if (intent.hasExtra(HomeMedicActivity.VIZUALIZARE_FEEDBACK)) {
            btnMediaNotelor.setVisibility(View.VISIBLE);
            tvTitlu.setText(getString(R.string.feedback_pacienti));
        } else if (intent.hasExtra(ConversatiiActivity.CONVERSATIE_NOUA)) {
            tvTitlu.setText(getString(R.string.title_conversatie_noua));
        }

        btnMediaNotelor.setOnClickListener(this);

        seteazaRecyclerView();

        firebaseServiceProgramari.preiaDateDinFirebase(preiaIduriPacienti());
    }

    private void seteazaRecyclerView() {
        rwListaPacienti.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwListaPacienti.setLayoutManager(layoutManager);
        seteazaAdaptorPacienti(pacienti);
    }

    private void seteazaAdaptorPacienti(List<Pacient> pacienti) {
        adapter = new PacientAdaptor(pacienti, this, this);
        rwListaPacienti.setAdapter(adapter);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        rwListaPacienti = findViewById(R.id.rwListaPacienti);
        progressBar = findViewById(R.id.progressBar);
        rlNiciunPacient = findViewById(R.id.rlNiciunPacient);
        pacienti = new ArrayList<>();
        tvTitlu = findViewById(R.id.tvTitlu);

        intent = getIntent();
        etCautaPacient = findViewById(R.id.etCautaPacient);

        btnMediaNotelor = findViewById(R.id.btnMediaNotelor);
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
                    btnMediaNotelor.setEnabled(false);
                    btnMediaNotelor.setTextColor(getResources().getColor(R.color.custom_light_blue));
                    btnMediaNotelor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pie_chart_light_blue, 0);
                } else {
                    firebaseServicePacienti.preiaDateDinFirebase(preiaPacienti());
                    btnMediaNotelor.setEnabled(true);
                    btnMediaNotelor.setTextColor(getResources().getColor(R.color.custom_blue));
                    btnMediaNotelor.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_pie_chart_blue, 0);
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
                pacienti = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pacient p = dataSnapshot.getValue(Pacient.class);
                    if (iduriPacienti.contains(p.getIdPacient()))
                        pacienti.add(p);
                }

                seteazaAdaptorPacienti(pacienti);
                rwListaPacienti.setVisibility(View.VISIBLE);
                loading(false);

                etCautaPacient.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String stringCurent = charSequence.toString().toLowerCase();
                        if (!stringCurent.isEmpty()) {
                            pacientiFiltered = pacienti.stream().filter(pacient ->
                                    pacient.getNume().toLowerCase().contains(stringCurent) ||
                                            pacient.getPrenume().toLowerCase().contains(stringCurent) ||
                                            String.valueOf(pacient.getCnp()).contains(stringCurent))
                                    .collect(Collectors.toList());

                            afiseazaPacienti(pacientiFiltered);
                        } else {
                            pacientiFiltered.clear();
                            seteazaAdaptorPacienti(pacienti);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void afiseazaPacienti(List<Pacient> pacientiFiltered) {
        if (pacientiFiltered.isEmpty()) {
            rlNiciunPacient.setVisibility(View.VISIBLE);
            rwListaPacienti.setVisibility(View.GONE);
        } else {
            rlNiciunPacient.setVisibility(View.GONE);
            rwListaPacienti.setVisibility(View.VISIBLE);
            seteazaAdaptorPacienti(pacientiFiltered);
        }
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        Pacient pacient;
        if (pacientiFiltered.isEmpty()) {
            pacient = pacienti.get(position);
        } else {
            pacient = pacientiFiltered.get(position);
        }

        referintaBuletin = FirebaseStorage.getInstance().getReference().child(DOCUMENTE_PACIENTI)
                .child(pacient.getIdPacient()).child(BULETIN);

        referintaCardSanatate = FirebaseStorage.getInstance().getReference().child(DOCUMENTE_PACIENTI)
                .child(pacient.getIdPacient()).child(CARD_DE_SANATATE);

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
            AppCompatButton btnCardSanatate = view.findViewById(R.id.btnCardSanatate);
            AppCompatButton btnBuletin = view.findViewById(R.id.btnBuletin);

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
            if (pacient.getGreutate() != 0.0) {
                tvGreutate.setText(String.valueOf(pacient.getGreutate()));
            } else {
                tvGreutate.setText("-");
            }
            if (pacient.getInaltime() != 0.0) {
                tvInaltime.setText(String.valueOf(pacient.getInaltime()));
            } else {
                tvInaltime.setText("-");
            }
            tvEmail.setText(pacient.getAdresaEmail());

            referintaCardSanatate.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    denumirePdfCardSanatate = CARD_DE_SANATATE + " - " + pacient.getNume() +
                            " " + pacient.getPrenume() + EXTENSIE_PDF;
                    btnCardSanatate.setEnabled(true);
                    btnCardSanatate.setTextColor(getResources().getColor(R.color.custom_blue));
                    btnCardSanatate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_download_blue, 0);
                    uriCardSanatate = uri;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    btnCardSanatate.setEnabled(false);
                    btnCardSanatate.setTextColor(getResources().getColor(R.color.custom_light_blue));
                    btnCardSanatate.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_download_light_blue, 0);
                }
            });

            referintaBuletin.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    denumirePdfBuletin = BULETIN + " - " + pacient.getNume() + " " + pacient.getPrenume()
                            + EXTENSIE_PDF;
                    btnBuletin.setEnabled(true);
                    btnBuletin.setTextColor(getResources().getColor(R.color.custom_blue));
                    btnBuletin.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_download_blue, 0);
                    uriBuletin = uri;
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    btnBuletin.setEnabled(false);
                    btnBuletin.setTextColor(getResources().getColor(R.color.custom_light_blue));
                    btnBuletin.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.ic_download_light_blue, 0);
                }
            });

            btnCardSanatate.setOnClickListener(this);
            btnBuletin.setOnClickListener(this);

            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        } else if (intent.hasExtra(ConversatiiActivity.CONVERSATIE_NOUA)) {
            startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(PACIENT, pacient));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCardSanatate:
                descarcaDocument(uriCardSanatate, Environment.DIRECTORY_DOWNLOADS, denumirePdfCardSanatate);
                break;
            case R.id.btnBuletin:
                descarcaDocument(uriBuletin, Environment.DIRECTORY_DOWNLOADS, denumirePdfBuletin);
                break;
            case R.id.btnMediaNotelor:
                startActivity(new Intent(getApplicationContext(), PieChartNoteActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }
    }

    private void descarcaDocument(Uri uri, String directorDestinatie, String denumireFisier) {
        DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        Toast.makeText(getApplicationContext(), R.string.descarcare_document,
                Toast.LENGTH_SHORT).show();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getApplicationContext(), directorDestinatie, denumireFisier);
        downloadManager.enqueue(request);
    }
}