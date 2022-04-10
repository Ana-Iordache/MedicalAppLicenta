package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {
    public static final String VIZUALIZARE_MEDICI = "vizualizareMedici";
    public static final String PACIENT = "pacient";
    private static final String NUMAR_CALL_CENTER = "0219268";
    public static final String PACIENTI = "Pacienti";

    //todo sa pun un progress bar sau ceva pana se incarca datele de la profil
    private Toolbar toolbar; // ca sa atasez toolbarul in pagina
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle; // ca sa atasez meniul de tip "burger"
    private NavigationView navigationView; // ca sa gestionez optiunile din meniu

    private TextView tvNumeUserConectat;
    private TextView tvEmailUserConectat;

    private FirebaseUser pacientConectat;
    private FirebaseService firebaseService = new FirebaseService(PACIENTI);
    private String idPacient;

    private CircleImageView ciwPozaProfilUser;

    private CardView cwMedici;
    private CardView cwInvestigatii;
    private CardView cwProgramari;
    private CardView cwFacturi;

    private FloatingActionButton fabCallCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaToggle();

        navigationView.setNavigationItemSelectedListener(this);
        cwMedici.setOnClickListener(this);
        cwInvestigatii.setOnClickListener(this);
        cwProgramari.setOnClickListener(this);
        cwFacturi.setOnClickListener(this);
        fabCallCenter.setOnClickListener(this);

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

        pacientConectat = FirebaseAuth.getInstance().getCurrentUser();
        idPacient = pacientConectat.getUid();

        cwMedici = findViewById(R.id.cwMedici);
        cwInvestigatii = findViewById(R.id.cwInvestigatii);
        cwProgramari = findViewById(R.id.cwProgramari);
        cwFacturi = findViewById(R.id.cwFacturi);
        fabCallCenter = findViewById(R.id.fabCallCenter);
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
                    String numeComplet = nume + " " + prenume;
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
            case R.id.item_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ConectarePacientActivity.class));
                finish();
                break;
            //TODO
            case R.id.item_feedback:
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