package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
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

public class HomeMedicActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener{
    Toolbar toolbar; // ca sa atasez toolbarul in pagina
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle; // ca sa atasez meniul de tip "burger"
    NavigationView navigationView; // ca sa gestionez optiunile din meniu

    TextView tvNumeUserConectat;
    TextView tvEmailUserConectat;

    FirebaseUser medicConectat;
    FirebaseService firebaseService = new FirebaseService("Medici");
    String idMedic;

    CircleImageView ciwPozaProfilUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_medic);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toggle, R.string.close_toggle);
        drawerLayout.addDrawerListener(toggle); // atasam toggle-ul la drawerLayout
        toggle.syncState(); // sa se roteasca atunci cand inchid/deschid meniul

        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        tvNumeUserConectat = navigationView.getHeaderView(0).findViewById(R.id.tvNumeUserConectat);
        tvEmailUserConectat = navigationView.getHeaderView(0).findViewById(R.id.tvEmailUserConectat);
        ciwPozaProfilUser = navigationView.getHeaderView(0).findViewById(R.id.ciwPozaProfilUser);

        medicConectat = FirebaseAuth.getInstance().getCurrentUser();
        idMedic = medicConectat.getUid();

        incarcaInfoNavMenu();
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

                if(medic!=null){
                    String nume = medic.getNume();
                    String prenume = medic.getPrenume();
                    String numeComplet = nume + " " + prenume;
                    String email = medic.getAdresaEmail();
                    String urlPozaProfil = medic.getUrlPozaProfil();

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

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profil:
                startActivity(new Intent(getApplicationContext(), ProfilMedicActivity.class));
                break;
            case R.id.item_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), ConectareMedicActivity.class));
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