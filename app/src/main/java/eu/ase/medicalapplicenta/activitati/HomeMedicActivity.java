package eu.ase.medicalapplicenta.activitati;

import static eu.ase.medicalapplicenta.activitati.MainActivity.EMAIL;
import static eu.ase.medicalapplicenta.activitati.MainActivity.SUBIECT;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Mesaj;
import eu.ase.medicalapplicenta.entitati.Notificare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class HomeMedicActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String MEDIC = "medic";
    public static final String VIZUALIZARE_FEEDBACK = "vizualizareFeedback";
    public static final String VIZUALIZARE_PACIENTI = "vizualizarePacienti";

    private Toolbar toolbar; // ca sa atasez toolbarul in pagina
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle; // ca sa atasez meniul de tip "burger"
    private NavigationView navigationView; // ca sa gestionez optiunile din meniu

    private RelativeLayout rlLogout;

    private TextView tvNumeUserConectat;
    private TextView tvEmailUserConectat;

    private FirebaseUser medicConectat;
    private FirebaseService firebaseService = new FirebaseService("Medici");
    private String idMedic;

    private CircleImageView ciwPozaProfilUser;

    private CardView cwProgramari;
    private CardView cwPacienti;
    private CardView cwFeedback;
    private CardView cwIncasari;

    private ImageView ivNotificari;
    private FirebaseService firebaseServiceNotificari = new FirebaseService("Notificari");
    private List<Notificare> notificari = new ArrayList<>();

    private FloatingActionButton fabChat;
    private FirebaseService firebaseServiceConversatii = new FirebaseService("Conversatii");
    private List<Mesaj> mesaje = new ArrayList<>();

    private String numeComplet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_medic);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaToggle();

        verificaNotificariNecitite();
        verificaMesajeNecitite();

        navigationView.setNavigationItemSelectedListener(this);
        rlLogout.setOnClickListener(this);
        cwProgramari.setOnClickListener(this);
        cwPacienti.setOnClickListener(this);
        cwFeedback.setOnClickListener(this);
        cwIncasari.setOnClickListener(this);
        ivNotificari.setOnClickListener(this);
        fabChat.setOnClickListener(this);

        incarcaInfoNavMenu();

        ascundeItemDocumentePersonale();
    }

    private void ascundeItemDocumentePersonale() {
        Menu navigationMenu = navigationView.getMenu();
        navigationMenu.findItem(R.id.item_documente_personale).setVisible(false);
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

        medicConectat = FirebaseAuth.getInstance().getCurrentUser();
        idMedic = medicConectat.getUid();

        cwProgramari = findViewById(R.id.cwProgramari);
        cwPacienti = findViewById(R.id.cwPacienti);
        cwFeedback = findViewById(R.id.cwFeedback);
        cwIncasari = findViewById(R.id.cwIncasari);

        ivNotificari = findViewById(R.id.ivNotificari);

        fabChat = findViewById(R.id.fabChat);
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
                    if (ultimulMesaj.getIdReceptor().equals(idMedic) && !ultimulMesaj.isMesajCitit()) {
                        fabChat.setImageResource(R.drawable.ic_chat_mesaje_necitite);
                        break;
                    } else {
                        fabChat.setImageResource(R.drawable.ic_chat);
                    }
                }

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
                    if (notificare.getIdReceptor().equals(idMedic))
                        notificari.add(notificare);
                }

                if (!notificari.isEmpty()) {
                    if (notificari.stream().anyMatch(notificare -> !notificare.isNotificareCitita())) {
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

    @Override
    protected void onResume() {
        super.onResume();
        incarcaInfoNavMenu();
        preiaNotificari();
    }

    private void incarcaInfoNavMenu() {
        firebaseService.databaseReference.child(idMedic).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Medic medic = snapshot.getValue(Medic.class);

                if (medic != null) {
                    String nume = medic.getNume();
                    String prenume = medic.getPrenume();
                    numeComplet = nume + " " + prenume;
                    String email = medic.getAdresaEmail();
                    String urlPozaProfil = medic.getUrlPozaProfil();

                    tvNumeUserConectat.setText(numeComplet);

                    tvEmailUserConectat.setText(email);

                    if (!urlPozaProfil.equals("")) {
                        Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedic", error.getMessage());
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cwProgramari:
                startActivity(new Intent(getApplicationContext(), ProgramariActivity.class).putExtra(MEDIC, "medic"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cwPacienti:
                startActivity(new Intent(getApplicationContext(), ListaPacientiActivity.class).putExtra(VIZUALIZARE_PACIENTI, ""));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cwFeedback:
                startActivity(new Intent(getApplicationContext(), ListaPacientiActivity.class).putExtra(VIZUALIZARE_FEEDBACK, ""));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cwIncasari:
                startActivity(new Intent(getApplicationContext(), IncasariActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.ivNotificari:
                ivNotificari.setImageResource(R.drawable.ic_notificari);
                startActivity(new Intent(getApplicationContext(), NotificariActivity.class).putExtra(MEDIC, "medic"));
                break;
            case R.id.fabChat:
                startActivity(new Intent(getApplicationContext(), ConversatiiActivity.class).putExtra(MEDIC, "medic"));
                break;
            case R.id.rlLogout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ConectareMedicActivity.class));
                finish();
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profil:
                startActivity(new Intent(getApplicationContext(), ProfilMedicActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.item_bmi:
                startActivity(new Intent(getApplicationContext(), CalculatorBmiActivity.class).putExtra(MEDIC, "medic"));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
//            case R.id.item_log_out:
//                FirebaseAuth.getInstance().signOut();
//                startActivity(new Intent(getApplicationContext(), ConectareMedicActivity.class));
//                finish();
//                break;
            case R.id.item_feedback_aplicatie:
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{EMAIL});
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBIECT);
                intent.putExtra(Intent.EXTRA_TEXT, "Nume medic: " + numeComplet + "\nFeedback: ");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "Nu aveti niciun serviciu de email disponibil!", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.item_despre_noi:
                startActivity(new Intent(getApplicationContext(), DespreNoiActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}