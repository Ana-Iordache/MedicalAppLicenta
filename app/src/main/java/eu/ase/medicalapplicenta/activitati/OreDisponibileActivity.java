package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.OraDisponibilaAdaptor;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.entitati.ZiDeLucru;
import eu.ase.medicalapplicenta.utile.FirebaseService;

//todo
public class OreDisponibileActivity extends AppCompatActivity implements View.OnClickListener, OraDisponibilaAdaptor.OnButtonClickListener {
    public static final String PROGRAMARI = "Programari";
    private final FirebaseService firebaseService = new FirebaseService(PROGRAMARI);
    private final String idPacient = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final DateFormat format = new SimpleDateFormat("HH:mm", Locale.US); //parsare ora din string
    private List<Programare> programari = new ArrayList<>();
    private TextView tvMedic;
    private TextView tvDataProgramarii;
    //    private ListView lvOre;
    private List<String> oreDisponibile;
    private List<String> oreIndisponibile;
    private Medic m;

    private OraDisponibilaAdaptor adaptor;
    private RecyclerView rwOreDisponibile;
    private RecyclerView.LayoutManager layoutManager;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ore_disponibile);

        Intent intent = getIntent();
        m = (Medic) intent.getSerializableExtra(ListaMediciActivity.ORE_DISPONIBILE);

        tvMedic = findViewById(R.id.tvMedic);
        tvDataProgramarii = findViewById(R.id.tvDataProgramarii);
//        lvOre = findViewById(R.id.lvOre);

        String nume = "Dr. " + m.getNume() + " " + m.getPrenume();
        tvMedic.setText(nume);
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        dataDefault = LocalDateTime.now().plusDays(1); // pun automat data de ziua urm, pt ca programarile se fac cu cel putin o zi inainte
//
//        tvDataProgramarii.setText(formatter.format(dataDefault));
        tvDataProgramarii.setOnClickListener(this);


//        lvOre.setOnItemClickListener(this);
        oreDisponibile = new ArrayList<>();
        rwOreDisponibile = findViewById(R.id.rwOreDisponibile);
        rwOreDisponibile.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rwOreDisponibile.setLayoutManager(layoutManager);
        adaptor = new OraDisponibilaAdaptor(oreDisponibile, this);
        rwOreDisponibile.setAdapter(adaptor);

//        Date ora = null;
//        try {
//             ora = format.parse("10:40");
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

//        Calendar calendar = Calendar.getInstance();
//        calendar.setTime(ora);
//        Toast.makeText(getApplicationContext(), format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
//        calendar.add(Calendar.MINUTE, 20); //adaugare minute
//        Toast.makeText(getApplicationContext(), format.format(calendar.getTime()), Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onClick(View view) {
        final Calendar calendar = Calendar.getInstance();
        int zi = calendar.get(Calendar.DATE);
        int luna = calendar.get(Calendar.MONTH);
        int an = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(OreDisponibileActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int an, int luna, int zi) {
                        luna += 1;
                        String dataString = zi + "/" + luna + "/" + an;
                        tvDataProgramarii.setText(dataString);

                        afiseazaOreDisponibile(dataString);
                    }
                }, an, luna, zi);
//                datePickerDialog.getDatePicker().setMinDate(calendar.setTime();
        datePickerDialog.show();
    }

    private void afiseazaOreDisponibile(String dataString) {
        Date data = null;
        try {
            data = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String ziProgramare = new SimpleDateFormat("EEEE", new Locale("ro", "RO")).format(data);

        for (ZiDeLucru z : m.getProgram()) {
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

                    //cat timp ora de inceput e mai mica decat ora de sfarsit - 20 min
                    // pt ca ultima programare poate fi de la orasf - 20 min
                    // ca ora de sfasit inseamna ca atunci se termina programul medicului
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
//            lvOre.setVisibility(View.GONE);
            rwOreDisponibile.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Medicul nu are program in zilele de " + ziProgramare + "!", Toast.LENGTH_SHORT).show();
        } else {
            //todo sa iau toate programarile si sa verific la care corespunde idMedic, data si ora
            //pun ora intr-o lista si apoi scot din oreDisponibile orele gasite adineauri
            firebaseService.preiaDateDinFirebase(preiaProgramari());

        }
    }

    private ValueEventListener preiaProgramari() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                programari.clear();
                oreIndisponibile = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdMedic().equals(m.getIdMedic()) && p.getData().equals(tvDataProgramarii.getText().toString())) {
                        oreIndisponibile.add(p.getOra());
                    }
                }

//                List<String> oreDisp = new ArrayList<>();

//                for (int i = 0; i < oreIndisponibile.size(); i++) {
                oreDisponibile.removeAll(oreIndisponibile);
//                    for (int j = 0; j < oreDisponibile.size(); j++) {
//                        if (oreIndisponibile.get(i).equals(oreDisponibile.get(j))) {
//                            oreDisponibile.remove(i);
//                        }
//                    }
//                }

                rwOreDisponibile.setVisibility(View.VISIBLE);
                adaptor = new OraDisponibilaAdaptor(oreDisponibile, OreDisponibileActivity.this);
                rwOreDisponibile.setAdapter(adaptor);

////                lvOre.setVisibility(View.VISIBLE);
//                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, oreDisponibile);
//                lvOre.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareProgramari", error.getMessage());
            }
        };
    }

    @Override
    public void onButtonClick(int position) {
        AlertDialog dialog = new AlertDialog.Builder(OreDisponibileActivity.this)
                .setTitle("Confirmare programare")
                .setMessage("Trimiteti programarea pentru data " + tvDataProgramarii.getText().toString() + " la ora " + oreDisponibile.get(position) + "?")
                .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Programarea nu a fost trimisa!", Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();
                    }
                })
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Programare programare = new Programare(m.getIdMedic(), idPacient, tvDataProgramarii.getText().toString(), oreDisponibile.get(position));
                        String idProgramare = firebaseService.databaseReference.push().getKey();
                        firebaseService.databaseReference.child(idProgramare).setValue(programare);
                        Toast.makeText(getApplicationContext(), "Programarea a fost trimisa!", Toast.LENGTH_SHORT).show();
                        dialogInterface.cancel();

                        finish();
                    }
                }).create();
        dialog.show();
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//        AlertDialog dialog = new AlertDialog.Builder(OreDisponibileActivity.this)
//                .setTitle("Confirmare programare")
//                .setMessage("Trimiteti programarea pentru data " + tvDataProgramarii.getText().toString() + " la ora " + oreDisponibile.get(position) + "?")
//                .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Toast.makeText(getApplicationContext(), "Programarea nu a fost trimisa!", Toast.LENGTH_SHORT).show();
//                        dialogInterface.cancel();
//                    }
//                })
//                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        Programare programare = new Programare(m.getIdMedic(), idPacient, tvDataProgramarii.getText().toString(), oreDisponibile.get(position));
//                        String idProgramare = firebaseService.databaseReference.push().getKey();
//                        firebaseService.databaseReference.child(idProgramare).setValue(programare);
//                        Toast.makeText(getApplicationContext(), "Programarea a fost trimisa!", Toast.LENGTH_SHORT).show();
//                        dialogInterface.cancel();
//
//                        finish();
//                    }
//                }).create();
//        dialog.show();
//    }
}