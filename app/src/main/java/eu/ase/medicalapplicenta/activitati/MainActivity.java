package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
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
//    Button btnDeconectare;

//todo sa pun un progress bat sau ceva pana se incarca datele de la profil
    Toolbar toolbar; // ca sa atasez toolbarul in pagina
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle; // ca sa atasez meniul de tip "burger"
    NavigationView navigationView; // ca sa gestionez optiunile din meniu

    TextView tvNumeUserConectat;
    TextView tvEmailUserConectat;

    FirebaseUser pacientConectat;
    FirebaseService firebaseService = new FirebaseService("Pacienti");
    String idPacient;

    CircleImageView ciwPozaProfilUser;

    CardView cwMedici;
    CardView cwInvestigatii;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toggle, R.string.close_toggle);
        drawerLayout.addDrawerListener(toggle); // atasam toggle-ul la drawerLayout
        toggle.syncState(); // sa se roteasca atunci cand inchid/deschid meniul

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        tvNumeUserConectat = navigationView.getHeaderView(0).findViewById(R.id.tvNumeUserConectat);
        tvEmailUserConectat = navigationView.getHeaderView(0).findViewById(R.id.tvEmailUserConectat);
        ciwPozaProfilUser = navigationView.getHeaderView(0).findViewById(R.id.ciwPozaProfilUser);

        pacientConectat = FirebaseAuth.getInstance().getCurrentUser();
        idPacient = pacientConectat.getUid();

        cwMedici = findViewById(R.id.cwMedici);
        cwMedici.setOnClickListener(this);

        cwInvestigatii = findViewById(R.id.cwInvestigatii);
        cwInvestigatii.setOnClickListener(this);

        incarcaInfoNavMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        incarcaInfoNavMenu();
    }

    public void incarcaInfoNavMenu(){
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

                    if(!urlPozaProfil.equals("")){
                        Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilUser);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Nu s-au putut prelua datele!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.cwMedici:
                startActivity(new Intent(getApplicationContext(), ListaMediciActivity.class));
                break;
            case R.id.cwInvestigatii:
                startActivity(new Intent(getApplicationContext(), ListaInvestigatiiActivity.class));
                break;
        }
    }

    // gestionez ce se intampla atunci cand dau click pe fiecare optiune din meniu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profil:
                startActivity(new Intent(getApplicationContext(), ProfilPacientActivity.class));
//                Toast.makeText(getApplicationContext(), "Profil", Toast.LENGTH_SHORT).show();
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