package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Conversatie;
import eu.ase.medicalapplicenta.entitati.Mesaj;
import eu.ase.medicalapplicenta.entitati.Notificare;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String VIZUALIZARE_MEDICI = "vizualizareMedici";
    public static final String PACIENT = "pacient";
    public static final String PACIENTI = "Pacienti";
    public static final String NOTIFICARI = "Notificari";
    public static final String EMAIL = "iordacheana19@stud.ase.ro";
    public static final String SUBIECT = "Feedback aplicatie medicala";
    private static final String NUMAR_CALL_CENTER = "0219268";
    //todo sa pun un progress bar sau ceva pana se incarca datele de la profil
    private Toolbar toolbar; // ca sa atasez toolbarul in pagina
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle; // ca sa atasez meniul de tip "burger"
    private NavigationView navigationView; // ca sa gestionez optiunile din meniu

    private RelativeLayout rlLogout;

    private TextView tvNumeUserConectat;
    private TextView tvEmailUserConectat;

    private FirebaseUser pacientConectat;
    private FirebaseService firebaseService = new FirebaseService(PACIENTI);
    private String idPacient;

    private FirebaseService firebaseServiceNotificari = new FirebaseService(NOTIFICARI);
    private List<Notificare> notificari = new ArrayList<>();

    private CircleImageView ciwPozaProfilUser;

    private CardView cwMedici;
    private CardView cwInvestigatii;
    private CardView cwProgramari;
    private CardView cwFacturi;

    private FloatingActionButton fabCallCenter;
    //    private FloatingActionButton fabNotificari;
    private ImageView ivNotificari;
    private FloatingActionButton fabChat;

    private FirebaseService firebaseServiceConversatii = new FirebaseService("Conversatii");
    private List<Mesaj> mesaje = new ArrayList<>();

    private String numeComplet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaToggle();

//        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
//        try {
//            Date date = dateFormat.parse("28/04/2022 20:01");
//            Timer timer = new Timer();
//            timer.schedule(new NotificareTimeTask(new Notificare(null, "titlu", "idEmitator", "idReceptor", "dataProgramarii", "oraProgramarii", "data", false)), date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        verificaNotificariNecitite();
        verificaMesajeNecitite();

        navigationView.setNavigationItemSelectedListener(this);
        rlLogout.setOnClickListener(this);
        cwMedici.setOnClickListener(this);
        cwInvestigatii.setOnClickListener(this);
        cwProgramari.setOnClickListener(this);
        cwFacturi.setOnClickListener(this);
        fabCallCenter.setOnClickListener(this);
//        fabNotificari.setOnClickListener(this);
        ivNotificari.setOnClickListener(this);
        fabChat.setOnClickListener(this);

        incarcaInfoNavMenu();
    }

    private void verificaMesajeNecitite() {
        firebaseServiceConversatii.preiaDateDinFirebase(preiaConversatii());
    }

    private ValueEventListener preiaConversatii() {
        mesaje.clear();
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Conversatie conversatie = dataSnapshot.getValue(Conversatie.class);
                    List<Mesaj> mesaje = conversatie.getMesaje();
                    Mesaj ultimulMesaj = mesaje.get(mesaje.size() - 1);
                    if (ultimulMesaj.getIdReceptor().equals(idPacient) && !ultimulMesaj.isMesajCitit()) {
                        fabChat.setImageResource(R.drawable.ic_chat_mesaje_necitite);
                        break;
                    } else {
                        fabChat.setImageResource(R.drawable.ic_chat);
                    }
//                    if (conversatie.getMesaje().stream().anyMatch(m -> m.getIdReceptor().equals(idPacient))) {
//                        mesaje.addAll(conversatie.getMesaje());
//                    }
//                    for (Mesaj mesaj : conversatie.getMesaje()) {
//                        if (mesaj.getIdReceptor().equals(idPacient)) {
//                            if (!mesaj.isMesajCitit()) {
//                                fabChat.setImageResource(R.drawable.ic_chat_mesaje_necitite);
//                            } else {
//                                fabChat.setImageResource(R.drawable.ic_chat);
//                            }
//                        }
//                    }

                }

//                if (!mesaje.isEmpty()) {
//                    if ((!mesaje.stream().anyMatch(Mesaj::isMesajCitit))) {
//                        fabChat.setImageResource(R.drawable.ic_chat_mesaje_necitite);
//                    } else {
//                        fabChat.setImageResource(R.drawable.ic_chat);
//                    }
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void verificaNotificariNecitite() {
        firebaseServiceNotificari.preiaDateDinFirebase(preiaNotificari());
    }

    private ValueEventListener preiaNotificari() {
        notificari.clear();
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notificare notificare = dataSnapshot.getValue(Notificare.class);
                    if (notificare.getIdReceptor().equals(idPacient))
                        notificari.add(notificare);
                }

                if (!notificari.isEmpty()) {
                    if (!notificari.stream().anyMatch(Notificare::isNotificareCitita)) {
                        ivNotificari.setImageResource(R.drawable.ic_notificari_necitite);
                    } else {
                        ivNotificari.setImageResource(R.drawable.ic_notificari);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void seteazaToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toggle, R.string.close_toggle);
        drawerLayout.addDrawerListener(toggle); // atasam toggle-ul la drawerLayout
        toggle.syncState(); // sa se roteasca atunci cand inchid/deschid meniul
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navigationView);
        tvNumeUserConectat = navigationView.getHeaderView(0).findViewById(R.id.tvNumeUserConectat);
        tvEmailUserConectat = navigationView.getHeaderView(0).findViewById(R.id.tvEmailUserConectat);
        ciwPozaProfilUser = navigationView.getHeaderView(0).findViewById(R.id.ciwPozaProfilUser);

        rlLogout = findViewById(R.id.rlLogout);

        pacientConectat = FirebaseAuth.getInstance().getCurrentUser();
        idPacient = pacientConectat.getUid();

        cwMedici = findViewById(R.id.cwMedici);
        cwInvestigatii = findViewById(R.id.cwInvestigatii);
        cwProgramari = findViewById(R.id.cwProgramari);
        cwFacturi = findViewById(R.id.cwFacturi);
        fabCallCenter = findViewById(R.id.fabCallCenter);
        ivNotificari = findViewById(R.id.ivNotificari);
        fabChat = findViewById(R.id.fabChat);
    }

    @Override
    protected void onResume() {
        super.onResume();
        incarcaInfoNavMenu();
    }

    public void incarcaInfoNavMenu() {
        firebaseService.databaseReference.child(idPacient).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pacient pacient = snapshot.getValue(Pacient.class);

                if (pacient != null) {
                    String nume = pacient.getNume();
                    String prenume = pacient.getPrenume();
                    numeComplet = nume + " " + prenume;
                    String email = pacient.getAdresaEmail();
                    String urlPozaProfil = pacient.getUrlPozaProfil();

                    tvNumeUserConectat.setText(numeComplet);

                    tvEmailUserConectat.setText(email);

                    if (!urlPozaProfil.equals("")) {
                        Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluarePacient", error.getMessage());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cwMedici:
                startActivity(new Intent(getApplicationContext(), ListaMediciActivity.class).putExtra(VIZUALIZARE_MEDICI, ""));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cwInvestigatii:
                startActivity(new Intent(getApplicationContext(), ListaInvestigatiiActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cwProgramari:
                startActivity(new Intent(getApplicationContext(), ProgramariActivity.class).putExtra(PACIENT, "pacient"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cwFacturi:
                startActivity(new Intent(getApplicationContext(), FacturiActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.fabCallCenter:
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", NUMAR_CALL_CENTER, null)));
                break;
            case R.id.ivNotificari:
//                notificari.stream().filter(n -> !n.isNotificareCitita()).forEach(n -> n.setNotificareCitita(true));
                startActivity(new Intent(getApplicationContext(), NotificariActivity.class).putExtra(PACIENT, "pacient"));
                break;
            case R.id.fabChat:
                startActivity(new Intent(getApplicationContext(), ConversatiiActivity.class).putExtra(PACIENT, "pacient"));
                break;
            case R.id.rlLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                finish();
                break;
        }
    }

    // gestionez ce se intampla atunci cand dau click pe fiecare optiune din meniu
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profil:
                startActivity(new Intent(getApplicationContext(), ProfilPacientActivity.class));
//                Toast.makeText(getApplicationContext(), "Profil", Toast.LENGTH_SHORT).show();
                break;
            case R.id.item_bmi:
                startActivity(new Intent(getApplicationContext(), CalculatorBmiActivity.class).putExtra(PACIENT, "pacient"));
                break;
            case R.id.item_documente_personale:
                startActivity(new Intent(getApplicationContext(), DocumentePersonaleActivity.class));
                break;
            case R.id.item_feedback_aplicatie:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBIECT);
                intent.putExtra(Intent.EXTRA_TEXT, "Nume pacient: " + numeComplet + "\nFeedback: ");
                startActivity(intent);
                break;
            case R.id.item_despre_noi:
                startActivity(new Intent(getApplicationContext(), DespreNoiActivity.class));
                break;
        }
        return true;
    }
}