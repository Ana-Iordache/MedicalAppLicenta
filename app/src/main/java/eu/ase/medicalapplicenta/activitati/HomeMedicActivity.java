package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class HomeMedicActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String MEDIC = "medic";
    public static final String VIZUALIZARE_FEEDBACK = "vizualizareFeedback";
    public static final String VIZUALIZARE_PACIENTI = "vizualizarePacienti";

    private Toolbar toolbar; // ca sa atasez toolbarul in pagina
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle; // ca sa atasez meniul de tip "burger"
    private NavigationView navigationView; // ca sa gestionez optiunile din meniu

    private TextView tvNumeUserConectat;
    private TextView tvEmailUserConectat;

    private FirebaseUser medicConectat;
    private FirebaseService firebaseService = new FirebaseService("Medici");
    private String idMedic;

    private CircleImageView ciwPozaProfilUser;

    private CardView cwProgramari;
    private CardView cwPacienti;
    private CardView cwFeedback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_medic);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaToggle();

        navigationView.setNavigationItemSelectedListener(this);
        cwProgramari.setOnClickListener(this);
        cwPacienti.setOnClickListener(this);
        cwFeedback.setOnClickListener(this);

        incarcaInfoNavMenu();
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

        medicConectat = FirebaseAuth.getInstance().getCurrentUser();
        idMedic = medicConectat.getUid();

        cwProgramari = findViewById(R.id.cwProgramari);
        cwPacienti = findViewById(R.id.cwPacienti);
        cwFeedback = findViewById(R.id.cwFeedback);
    }

    @Override
    protected void onResume() {
        super.onResume();
        incarcaInfoNavMenu();
    }

    private void incarcaInfoNavMenu() {
        firebaseService.databaseReference.child(idMedic).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Medic medic = snapshot.getValue(Medic.class);

                if (medic != null) {
                    String nume = medic.getNume();
                    String prenume = medic.getPrenume();
                    String numeComplet = nume + " " + prenume;
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
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profil:
                startActivity(new Intent(getApplicationContext(), ProfilMedicActivity.class));
                break;
            case R.id.item_bmi:
                startActivity(new Intent(getApplicationContext(), CalculatorBmiActivity.class).putExtra(MEDIC, "medic"));
                break;
            case R.id.item_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ConectareMedicActivity.class));
                finish();
                break;
            //TODO
            case R.id.item_feedback_aplicatie:
                Toast.makeText(getApplicationContext(), "Feedback", Toast.LENGTH_SHORT).show();
                break;
            //TODO
            case R.id.item_despre_noi:
                Toast.makeText(getApplicationContext(), "Despre noi", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}