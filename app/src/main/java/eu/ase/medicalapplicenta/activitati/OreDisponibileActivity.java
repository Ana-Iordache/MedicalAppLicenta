package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.icu.util.LocaleData;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.OraDisponibilaAdaptor;
import eu.ase.medicalapplicenta.adaptori.ProgramAdaptor;
import eu.ase.medicalapplicenta.entitati.Factura;
import eu.ase.medicalapplicenta.entitati.Investigatie;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Notificare;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.entitati.ZiDeLucru;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.O)
public class OreDisponibileActivity extends AppCompatActivity implements View.OnClickListener, OraDisponibilaAdaptor.OnOraClickListener {
    public static final String PROGRAMARI = "Programari";
    public static final String SPECIALITATI = "Specialitati";
    public static final String NOTIFICARI = "Notificari";
    private final FirebaseService firebaseServiceProgramari = new FirebaseService(PROGRAMARI);
    private final FirebaseService firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
    private final FirebaseService firebaseServiceNotificari = new FirebaseService(NOTIFICARI);
    private final String idPacient = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final DateFormat format = new SimpleDateFormat("HH:mm", Locale.US); //parsare ora din string
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final String dataCurenta = formatter.format(LocalDate.now());

    private List<Programare> programari = new ArrayList<>();
    private TextView tvMedic;
    private TextInputEditText tietDataProgramarii;
    private List<String> oreDisponibile;
    private List<String> oreIndisponibile;

    private Medic medic;

    private OraDisponibilaAdaptor adaptor;
    private RecyclerView rwOreDisponibile;

    private LinearLayout llSelectareData;

    private AutoCompleteTextView actvInvestigatii;
    private List<Investigatie> investigatii;
    private List<String> denumiriInvestigatii = new ArrayList<>();

    private Toolbar toolbar;

    private ProgressBar progressBar;

    private RelativeLayout rlNicioOra;
    private TextView tvNicioOra;

    private ListView lvProgram;
    private ProgramAdaptor programAdaptor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ore_disponibile);

        initializeazaAtribute();

        seteazaToolbar();

        Intent intent = getIntent();
        medic = (Medic) intent.getSerializableExtra(ListaMediciActivity.ORE_DISPONIBILE);

        actvInvestigatii.setOnClickListener(this);

        seteazaInformatiiMedic();

        tietDataProgramarii.setOnClickListener(this);

        firebaseServiceSpecialitati.preiaObiectDinFirebase(preiaInvestigatii(), medic.getIdSpecialitate());

        seteazaRecyclerView();
    }

    private void seteazaInformatiiMedic() {
        String nume = "Dr. " + medic.getNume() + " " + medic.getPrenume();
        tvMedic.setText(nume);

        programAdaptor = new ProgramAdaptor(getApplicationContext(), R.layout.element_program_medic,
                medic.getProgram(), getLayoutInflater());
        lvProgram.setAdapter(programAdaptor);
    }

    private void seteazaRecyclerView() {
        rwOreDisponibile.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
        rwOreDisponibile.setLayoutManager(gridLayoutManager);
        seteazaAdaptor();
    }

    private void seteazaAdaptor() {
        adaptor = new OraDisponibilaAdaptor(oreDisponibile, OreDisponibileActivity.this);
        rwOreDisponibile.setAdapter(adaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        llSelectareData = findViewById(R.id.llSelectareData);
        tvMedic = findViewById(R.id.tvMedic);
        tietDataProgramarii = findViewById(R.id.tietDataProgramarii);

        actvInvestigatii = findViewById(R.id.actvInvestigatii);

        oreDisponibile = new ArrayList<>();
        rwOreDisponibile = findViewById(R.id.rwOreDisponibile);

        progressBar = findViewById(R.id.progressBar);

        rlNicioOra = findViewById(R.id.rlNicioOra);
        tvNicioOra = findViewById(R.id.tvNicioOra);

        lvProgram = findViewById(R.id.lvProgram);
    }

    private ValueEventListener preiaInvestigatii() {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                investigatii = new ArrayList<>();
                Specialitate specialitate = snapshot.getValue(Specialitate.class);
                investigatii.addAll(specialitate.getInvestigatii());
                denumiriInvestigatii = investigatii.stream()
                        .map(Investigatie::getDenumire)
                        .collect(Collectors.toList());

                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item, denumiriInvestigatii);
                actvInvestigatii.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareInvestigatii", error.getMessage());
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tietDataProgramarii:
                afiseazaCalendar();
                break;
            case R.id.actvInvestigatii:
                actvInvestigatii.setError(null);
                break;
        }

    }

    private void afiseazaCalendar() {
        final Calendar calendar = Calendar.getInstance();

        if (!tietDataProgramarii.getText().toString().equals(getString(R.string.selectati_data))) {
            try {
                Date dataSelectata = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(tietDataProgramarii.getText().toString());
                calendar.setTime(dataSelectata);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int zi = calendar.get(Calendar.DATE);
        int luna = calendar.get(Calendar.MONTH);
        int an = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(OreDisponibileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int an, int luna, int zi) {
                        luna += 1;
                        String dataString = zi + "/" + luna + "/" + an;
                        tietDataProgramarii.setText(dataString);

                        afiseazaOreDisponibile(dataString);
                    }
                }, an, luna, zi);

        //setare data minima
        Calendar dataMinima = Calendar.getInstance();
        dataMinima.add(Calendar.DAY_OF_MONTH, 1); //adaug o zi la data curenta
        datePickerDialog.getDatePicker().setMinDate(dataMinima.getTimeInMillis()); //si o setez ca valoare minima

        //setare data maxima
        Calendar dataMaxima = Calendar.getInstance();
        dataMaxima.add(Calendar.DAY_OF_MONTH, 90);
        datePickerDialog.getDatePicker().setMaxDate(dataMaxima.getTimeInMillis());

        datePickerDialog.show();
        datePickerDialog.setCanceledOnTouchOutside(false);
    }

    private void afiseazaOreDisponibile(String dataString) {
        llSelectareData.setVisibility(View.GONE);
        Date data = null;
        try {
            data = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String ziProgramare = new SimpleDateFormat("EEEE", new Locale("ro", "RO")).format(data);

        for (ZiDeLucru z : medic.getProgram()) {
            if (z.getZi().equals(ziProgramare)) {
                oreDisponibile = new ArrayList<>();
                try {
                    Date dOraInceput = format.parse(z.getOraInceput());
                    Calendar cOraInceput = Calendar.getInstance();
                    cOraInceput.setTime(dOraInceput);

                    Date dOraSfarsit = format.parse(z.getOraSfarsit());
                    Calendar cOraSfarsit = Calendar.getInstance();
                    cOraSfarsit.setTime(dOraSfarsit);
                    cOraSfarsit.add(Calendar.MINUTE, -20);

                    /*cat timp ora de inceput e mai mica decat ora de sfarsit - 20 min
                    pt ca ultima programare poate fi de la orasf - 20 min
                    ca ora de sfasit inseamna ca atunci se termina programul medicului*/
                    while (cOraInceput.getTime().compareTo(cOraSfarsit.getTime()) <= 0) {
                        oreDisponibile.add(format.format(cOraInceput.getTime()));
                        cOraInceput.add(Calendar.MINUTE, 20);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            } else oreDisponibile = null;
        }

        if (oreDisponibile == null) {
            rwOreDisponibile.setVisibility(View.GONE);
            String mesaj = "Medicul nu are program in zilele de " + ziProgramare + "!";
            tvNicioOra.setText(mesaj);
            rlNicioOra.setVisibility(View.VISIBLE);
        } else if (!oreDisponibile.isEmpty()) {
            firebaseServiceProgramari.preiaDateDinFirebase(preiaOreDisponibile());
        }
        /*else {
            rwOreDisponibile.setVisibility(View.GONE);
            tvNicioOra.setText(getString(R.string.nicio_ora));
            rlNicioOra.setVisibility(View.VISIBLE);
        }*/
    }

    private ValueEventListener preiaOreDisponibile() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                oreIndisponibile = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdMedic().equals(medic.getIdMedic()) &&
                            p.getData().equals(tietDataProgramarii.getText().toString())
                            && p.getStatus().equals(getString(R.string.status_noua))) {
                        // daca programarea a fost anulata (statusul nu e "noua")
                        // atunci trec ora ca fiind disponibila
                        oreIndisponibile.add(p.getOra());
                    }
                }

                oreDisponibile.removeAll(oreIndisponibile);

                // cand toate orele din program sunt indisponibile
                if (oreDisponibile.isEmpty()) {
                    rwOreDisponibile.setVisibility(View.GONE);
                    tvNicioOra.setText(getString(R.string.nicio_ora));
                    rlNicioOra.setVisibility(View.VISIBLE);
                } else {
                    rlNicioOra.setVisibility(View.GONE);
                    rwOreDisponibile.setVisibility(View.VISIBLE);
                    seteazaAdaptor();
                }

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareProgramari", error.getMessage());
            }
        };
    }

    @Override
    public void onOraClick(int position) {
        if (!actvInvestigatii.getText().toString().equals(getString(R.string.selectati_investigatia))) {
            AlertDialog dialog = new AlertDialog.Builder(OreDisponibileActivity.this)
                    .setTitle("Confirmare programare")
                    .setMessage("Trimiteți programarea pentru data " + tietDataProgramarii.getText().toString() + " la ora " + oreDisponibile.get(position) + "?")
                    .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Investigatie investigatie = investigatii.stream()
                                    .filter(inv -> inv.getDenumire().equals(actvInvestigatii.getText().toString()))
                                    .findFirst()
                                    .orElseThrow(Resources.NotFoundException::new);
                            double valoare = investigatie.getPret();

//                            LocalDate dataProgramarii = LocalDate.parse(tietDataProgramarii.getText().toString(), formatter);

                            Factura factura = new Factura(null,
                                    valoare,
                                    dataCurenta,
                                    formatter.format(LocalDate.now().plusDays(30)),
                                    getString(R.string.neachitata));

                            Programare programare = new Programare(null, medic.getIdMedic(),
                                    idPacient,
                                    tietDataProgramarii.getText().toString(),
                                    oreDisponibile.get(position),
                                    "nouă",
                                    factura,
                                    null,
                                    "");
                            String idProgramare = firebaseServiceProgramari.databaseReference.push().getKey();
                            programare.setIdProgramare(idProgramare);
                            factura.setIdFactura(idProgramare);
                            firebaseServiceProgramari.databaseReference.child(idProgramare).setValue(programare);

                            Notificare notificare = new Notificare(null,
                                    getString(R.string.programare_noua),
                                    idPacient,
                                    programare.getIdMedic(),
                                    programare.getData(),
                                    programare.getOra(),
                                    dataCurenta,
                                    false);
                            String idNotificare = firebaseServiceNotificari.databaseReference.push().getKey();
                            notificare.setIdNotificare(idNotificare);
                            firebaseServiceNotificari.databaseReference.child(idNotificare).setValue(notificare);

                            Toast.makeText(getApplicationContext(), "Programarea a fost trimisă!", Toast.LENGTH_SHORT).show();
                            dialogInterface.cancel();

                            finish();

                        }
                    }).create();
            dialog.show();
            dialog.setCanceledOnTouchOutside(false);
        } else {
            actvInvestigatii.setError(getString(R.string.err_empty_investigatie));
            actvInvestigatii.requestFocus();
        }
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }
}