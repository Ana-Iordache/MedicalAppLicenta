package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.ChatAdaptor;
import eu.ase.medicalapplicenta.entitati.Conversatie;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Mesaj;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatActivity extends AppCompatActivity {
    private final FirebaseService firebaseService = new FirebaseService("Conversatii");
    private final String idUserConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private Toolbar toolbar;
    private CircleImageView ciwPozaProfilUser;
    private TextView tvNumeUser;
    private FloatingActionButton fabTrimiteMesaj;
    private EditText etMesaj;

    private Intent intent;
    private Medic medic;
    private Pacient pacient;
    private String idMedic;
    private String idPacient;

    private RecyclerView rwMesaje;
    private ChatAdaptor adaptor;
    private RecyclerView.LayoutManager layoutManager;
    private List<Mesaj> mesaje = new ArrayList<>();
    private String idConversatie = "";

    private String tipUtilizator;

    private ValueEventListener mesajeCititeListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaRecycleView();

        String numeComplet;
        if (tipUtilizator.equals(MainActivity.PACIENT)) {
            numeComplet = "Dr. " + medic.getNume() + " " + medic.getPrenume();
            if (!medic.getUrlPozaProfil().equals("")) {
                Glide.with(getApplicationContext()).load(medic.getUrlPozaProfil()).into(ciwPozaProfilUser);
            }
        } else {
            numeComplet = pacient.getNume() + " " + pacient.getPrenume();
            if (!pacient.getUrlPozaProfil().equals("")) {
                Glide.with(getApplicationContext()).load(pacient.getUrlPozaProfil()).into(ciwPozaProfilUser);
            }
        }

        tvNumeUser.setText(numeComplet);

        firebaseService.preiaDateDinFirebase(preiaMesaje());

        fabTrimiteMesaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mesaj = etMesaj.getText().toString().trim();
                if (tipUtilizator.equals(MainActivity.PACIENT)) {
                    trimiteMesaj(idUserConectat, medic.getIdMedic(), mesaj);
                } else {
                    trimiteMesaj(idUserConectat, pacient.getIdPacient(), mesaj);
                }

                etMesaj.setText("");
            }
        });

        if (tipUtilizator.equals(MainActivity.PACIENT)) {
            marcheazaMesajeCititeDePacient();
        } else if (tipUtilizator.equals(HomeMedicActivity.MEDIC)) {
            marcheazaMesajeCititeDeMedic();
        }
    }


    private void seteazaRecycleView() {
        rwMesaje.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwMesaje.setLayoutManager(layoutManager);
        seteazaAdaptor();
    }

    private void seteazaAdaptor() {
        adaptor = new ChatAdaptor(mesaje, this);
        if (!mesaje.isEmpty()) {
            rwMesaje.smoothScrollToPosition(mesaje.size() - 1);
        }
        rwMesaje.setAdapter(adaptor);

    }

    private void marcheazaMesajeCititeDeMedic() {
        mesajeCititeListener = firebaseService.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Conversatie conversatie = dataSnapshot.getValue(Conversatie.class);
                    List<Mesaj> mesaje = conversatie.getMesaje();
                    if ((mesaje.get(0).getIdEmitator().equals(idUserConectat) && mesaje.get(0).getIdReceptor().equals(idPacient))
                            || (mesaje.get(0).getIdReceptor().equals(idUserConectat) && mesaje.get(0).getIdEmitator().equals(idPacient))) {
                        for (int i = 0; i < conversatie.getMesaje().size(); i++) {
                            if (mesaje.get(i).getIdEmitator().equals(idPacient) && !mesaje.get(i).isMesajCitit()) {
                                firebaseService.databaseReference
                                        .child(conversatie.getIdConversatie())
                                        .child("mesaje")
                                        .child(String.valueOf(i))
                                        .child("mesajCitit")
                                        .setValue(true);
                                mesaje.get(i).setMesajCitit(true);
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void marcheazaMesajeCititeDePacient() {
        mesajeCititeListener = firebaseService.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Conversatie conversatie = dataSnapshot.getValue(Conversatie.class);
                    List<Mesaj> mesaje = conversatie.getMesaje();
                    if ((mesaje.get(0).getIdEmitator().equals(idUserConectat) && mesaje.get(0).getIdReceptor().equals(idMedic))
                            || (mesaje.get(0).getIdReceptor().equals(idUserConectat) && mesaje.get(0).getIdEmitator().equals(idMedic))) {
                        for (int i = 0; i < conversatie.getMesaje().size(); i++) {
                            if (mesaje.get(i).getIdEmitator().equals(idMedic) && !mesaje.get(i).isMesajCitit()) {
                                firebaseService.databaseReference
                                        .child(conversatie.getIdConversatie())
                                        .child("mesaje")
                                        .child(String.valueOf(i))
                                        .child("mesajCitit")
                                        .setValue(true);
                                mesaje.get(i).setMesajCitit(true);
                            }
                        }

//                        break;
                    }

                }

//                    adaptor.notifyDataSetChanged();
                // daca sunt conectat ca pacient marchez ca citite mesajele care au ca trimitator medicul
//                    for (int i = 0; i < conversatie.getMesaje().size(); i++) {
//                        if (tipUtilizator.equals(HomeMedicActivity.MEDIC) && mesaje.get(i).getIdEmitator().equals(idPacient)
//                                && !mesaje.get(i).isMesajCitit()) {
//                            firebaseService.databaseReference
//                                    .child(idConversatie)
//                                    .child("mesaje")
//                                    .child(String.valueOf(i))
//                                    .child("mesajCitit")
//                                    .setValue(true);
//                        }
//
//                        if (tipUtilizator.equals(MainActivity.PACIENT) && mesaje.get(i).getIdEmitator().equals(idMedic)
//                                && !mesaje.get(i).isMesajCitit()) {
//                            firebaseService.databaseReference
//                                    .child(idConversatie)
//                                    .child("mesaje")
//                                    .child(String.valueOf(i))
//                                    .child("mesajCitit")
//                                    .setValue(true);
//                        }
//                    }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private ValueEventListener preiaMesaje() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Conversatie conversatie = dataSnapshot.getValue(Conversatie.class);
                    mesaje = conversatie.getMesaje();
                    if ((tipUtilizator.equals(MainActivity.PACIENT) &&
                            ((mesaje.get(0).getIdEmitator().equals(idUserConectat) && mesaje.get(0).getIdReceptor().equals(idMedic))
                                    || (mesaje.get(0).getIdReceptor().equals(idUserConectat) && mesaje.get(0).getIdEmitator().equals(idMedic))))
                            || (tipUtilizator.equals(HomeMedicActivity.MEDIC) &&
                            ((mesaje.get(0).getIdEmitator().equals(idUserConectat) && mesaje.get(0).getIdReceptor().equals(idPacient))
                                    || (mesaje.get(0).getIdReceptor().equals(idUserConectat) && mesaje.get(0).getIdEmitator().equals(idPacient))))) {
                        idConversatie = conversatie.getIdConversatie();
//                        seteazaRecycleView();
                        seteazaAdaptor();
                        break;
                    } else {
                        mesaje = new ArrayList<>();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void trimiteMesaj(String idEmitator, String idReceptor, String text) {
        Mesaj mesaj = new Mesaj(idEmitator, idReceptor, text, false);
        if (!idConversatie.equals("")) {
            mesaje.add(mesaj);
//            adaptor.notifyDataSetChanged();
            firebaseService.databaseReference.child(idConversatie)
                    .child("mesaje").setValue(mesaje);
        } else {
            mesaje.add(mesaj);
//            adaptor.notifyDataSetChanged();
            Conversatie conversatie = new Conversatie(null, mesaje);
            idConversatie = firebaseService.databaseReference.push().getKey();
            conversatie.setIdConversatie(idConversatie);
            firebaseService.databaseReference.child(idConversatie).setValue(conversatie);
        }
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        ciwPozaProfilUser = findViewById(R.id.ciwPozaProfilUser);
        tvNumeUser = findViewById(R.id.tvNumeUser);
        rwMesaje = findViewById(R.id.rwMesaje);
        fabTrimiteMesaj = findViewById(R.id.fabTrimiteMesaj);
        etMesaj = findViewById(R.id.etMesaj);

        etMesaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mesaje.isEmpty()) {
                    rwMesaje.smoothScrollToPosition(mesaje.size() - 1);
                }
            }
        });

        etMesaj.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String mesaj = charSequence.toString();
                fabTrimiteMesaj.setEnabled(!mesaj.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        intent = getIntent();
        if (intent.hasExtra(ListaMediciActivity.MEDIC)) {
            medic = (Medic) intent.getSerializableExtra(ListaMediciActivity.MEDIC);
            idMedic = medic.getIdMedic();
            tipUtilizator = MainActivity.PACIENT;
        } else if (intent.hasExtra(ListaMediciActivity.INFO_MEDIC)) {
            medic = (Medic) intent.getSerializableExtra(ListaMediciActivity.INFO_MEDIC);
            idMedic = medic.getIdMedic();
            tipUtilizator = MainActivity.PACIENT;
        } else {
            pacient = (Pacient) intent.getSerializableExtra(ListaPacientiActivity.PACIENT);
            idPacient = pacient.getIdPacient();
            tipUtilizator = HomeMedicActivity.MEDIC;
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
        firebaseService.databaseReference.removeEventListener(mesajeCititeListener);
        if (intent.hasExtra(ListaMediciActivity.MEDIC) || intent.hasExtra(ListaPacientiActivity.PACIENT)) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}