package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

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
import eu.ase.medicalapplicenta.adaptori.ProgramareAdaptor;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ProgramariActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PROGRAMARI = "Programari";
    private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final String ADAUGA_PROGRAMARE = "adaugaProgramare";
    private final FirebaseService firebaseService = new FirebaseService(PROGRAMARI);
    //    private final DatabaseReference referintaDb = firebaseService.databaseReference;
    private final String idPacient = FirebaseAuth.getInstance().getCurrentUser().getUid();
    //    ListView lv;
    private FloatingActionButton fabAdaugaProgramare;
    private AppCompatRadioButton rbIstoric;
    private AppCompatRadioButton rbViitoare;
    private List<Programare> programari;
    private Date dataCurenta;

    private RecyclerView rwProgramari;
    private ProgramareAdaptor adaptor;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar progressBar;
    private RelativeLayout ryNicioProgramare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programari);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        // setez un button de back ca sa ma pot intoarce in pagina principala
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        fabAdaugaProgramare = findViewById(R.id.fabAdaugaProgramare);
        fabAdaugaProgramare.setOnClickListener(this);

        rbIstoric = findViewById(R.id.rbIstoric);
        rbIstoric.setOnClickListener(this);

        rbViitoare = findViewById(R.id.rbViitoare);
        rbViitoare.setOnClickListener(this);

        try {
            dataCurenta = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(FORMAT_DATA.format(LocalDateTime.now()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        progressBar = findViewById(R.id.progressBar);
        ryNicioProgramare = findViewById(R.id.ryNicioProgramare);

//        lv = findViewById(R.id.lv);
        rwProgramari = findViewById(R.id.rwProgramari);
        rwProgramari.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        rwProgramari.setLayoutManager(layoutManager);
        adaptor = new ProgramareAdaptor(programari);
        rwProgramari.setAdapter(adaptor);

        firebaseService.preiaDateDinFirebase(preiaProgramari());

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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdaugaProgramare:
                startActivity(new Intent(getApplicationContext(), ListaMediciActivity.class).putExtra(ADAUGA_PROGRAMARE, ""));
                break;
            case R.id.rbViitoare:
                if (((AppCompatRadioButton) view).isChecked()) {
                    rbViitoare.setTextColor(Color.WHITE);
                    rbIstoric.setTextColor(ContextCompat.getColor(this, R.color.custom_blue));
                }
                firebaseService.preiaDateDinFirebase(preiaProgramari());
                break;
            case R.id.rbIstoric:
                if (((AppCompatRadioButton) view).isChecked()) {
                    rbIstoric.setTextColor(Color.WHITE);
                    rbViitoare.setTextColor(ContextCompat.getColor(this, R.color.custom_blue));
                }
                firebaseService.preiaDateDinFirebase(preiaProgramari());
                break;
        }
    }

    private ValueEventListener preiaProgramari() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                programari = new ArrayList<>();
//                programari.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdPacient().equals(idPacient)) {
                        try {
                            Date dataProgramarii = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(p.getData());
                            //todo poate mai fac frumos..
                            if (rbViitoare.isChecked()) {
                                if (dataProgramarii.after(dataCurenta)) {//todo si ora poate..
                                    programari.add(p);
                                }
                            } else if (rbIstoric.isChecked())
                                if (dataProgramarii.before(dataCurenta)) {
                                    programari.add(p);
                                }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }

                if (!programari.isEmpty()) {
                    ryNicioProgramare.setVisibility(View.GONE);
//                ArrayAdapter<Programare> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, programari);
//                lv.setAdapter(adapter);
                    adaptor = new ProgramareAdaptor(programari);
                    rwProgramari.setAdapter(adaptor);
                    rwProgramari.setVisibility(View.VISIBLE);
                } else {
                    rwProgramari.setVisibility(View.GONE);
                    ryNicioProgramare.setVisibility(View.VISIBLE);
                }

                loading(false);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareProgramari", error.getMessage());
            }
        };
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }
}