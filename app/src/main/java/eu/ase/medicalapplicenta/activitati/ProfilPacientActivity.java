package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ProfilPacientActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 200;
    CircleImageView ciwPozaProfilPacient;

    TextInputEditText tietNumePacient;
    TextInputEditText tietPrenumePacient;
    TextInputEditText tietCnp;
    TextInputEditText tietDataNasterii;
    TextInputEditText tietNrTelefonPacient;
    TextInputEditText tietAdresa;
    TextInputEditText tietEmailPacient;
    TextInputEditText tietGreutate;
    TextInputEditText tietInaltime;
    TextInputEditText tietVarsta;
    TextInputEditText tietSex;

    Spinner spGrupaSange;

    Button btnModificaDate;
    Button btnSalveaza;
    Button btnRenunta;
    Button btnSchimbaParola;
    Button btnStergeCont;

    Toolbar toolbar;

    FirebaseUser pacientConectat;
    FirebaseService firebaseService = new FirebaseService();
    String idUserConectat;
    DatabaseReference referintaUserConectat;
    Pacient pacient;

    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_pacient);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profil");
        // setez un button de back ca sa ma pot intoarce in pagina principala
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ciwPozaProfilPacient = findViewById(R.id.ciwPozaProfilPacient);
//        ciwPozaProfilPacient.setOnClickListener(this);

        tietNumePacient = findViewById(R.id.tietNumePacient);
        tietPrenumePacient = findViewById(R.id.tietPrenumePacient);
        tietCnp = findViewById(R.id.tietCnp);
        tietDataNasterii = findViewById(R.id.tietDataNasterii);
        tietNrTelefonPacient = findViewById(R.id.tietNrTelefonPacient);
        tietAdresa = findViewById(R.id.tietAdresa);
        tietEmailPacient = findViewById(R.id.tietEmailPacient);
        tietGreutate = findViewById(R.id.tietGreutate);
        tietInaltime = findViewById(R.id.tietInaltime);
        tietVarsta = findViewById(R.id.tietVarsta);
        tietSex = findViewById(R.id.tietSex);

        spGrupaSange = findViewById(R.id.spGrupaSange);
        spGrupaSange.setEnabled(false); // din xml nu merge

        btnModificaDate = findViewById(R.id.btnModificaDate);
        btnModificaDate.setOnClickListener(this);

        btnSalveaza = findViewById(R.id.btnSalveaza);
        btnSalveaza.setOnClickListener(this);

        btnRenunta = findViewById(R.id.btnRenunta);
        btnRenunta.setOnClickListener(this);

        btnSchimbaParola = findViewById(R.id.btnSchimbaParola);
        btnSchimbaParola.setOnClickListener(this);

        btnStergeCont = findViewById(R.id.btnStergeCont);
        btnStergeCont.setOnClickListener(this);

        pacientConectat = FirebaseAuth.getInstance().getCurrentUser();
        idUserConectat = pacientConectat.getUid();
        referintaUserConectat = firebaseService.databaseReference.child(idUserConectat);

        referintaUserConectat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pacient = snapshot.getValue(Pacient.class);

                if (pacient != null) {
                    String urlPozaProfil = pacient.getUrlPozaProfil();
                    String nume = pacient.getNume();
                    String prenume = pacient.getPrenume();
                    Long cnp = pacient.getCnp();
                    int varsta = pacient.getVarsta();
                    String sex = pacient.getSex();
                    String dataNasterii = pacient.getDataNasterii();
                    Long telefon = pacient.getNrTelefon();
                    String adresa = pacient.getAdresa();
                    String grupaSange = pacient.getGrupaSange();
                    double greutate = pacient.getGreutate();
                    double inaltime = pacient.getInaltime();
                    String email = pacient.getAdresaEmail();

                    tietNumePacient.setText(nume);
                    tietPrenumePacient.setText(prenume);
                    tietCnp.setText(String.valueOf(cnp));
                    tietDataNasterii.setText(dataNasterii);
                    tietNrTelefonPacient.setText(String.valueOf(telefon));
                    tietAdresa.setText(adresa);
                    tietEmailPacient.setText(email);
                    tietVarsta.setText(String.valueOf(varsta));
                    tietSex.setText(sex);
                    if (sex.equals("Feminin")) {
                        tietSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_feminin, 0, 0, 0);
                    } else {
                        tietSex.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_masculin, 0, 0, 0);
                    }
                    tietGreutate.setText(String.valueOf(greutate));
                    tietInaltime.setText(String.valueOf(inaltime));

                    if (!urlPozaProfil.equals("")) {
                        Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilPacient);
                    }

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) spGrupaSange.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).equals(grupaSange)) {
                            spGrupaSange.setSelection(i);
                            break;
                        }
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnModificaDate:
                ciwPozaProfilPacient.setOnClickListener(this);
                seteazaAccesibilitatea(true);
                seteazaVizibilitateButoane(View.GONE, View.VISIBLE);
                break;
            case R.id.btnSchimbaParola: //TODO
                Toast.makeText(getApplicationContext(), "schimba parola", Toast.LENGTH_SHORT).show();
            case R.id.btnStergeCont: //TODO
                Toast.makeText(getApplicationContext(), "sterge cont", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnSalveaza: //TODO
                actualizeazaDate();
                seteazaAccesibilitatea(false);
                seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
                break;
            case R.id.btnRenunta:
                seteazaAccesibilitatea(false);
                seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
                break;
            case R.id.ciwPozaProfilPacient:
                alegePozaProfil();
                break;
        }
    }

    private void alegePozaProfil() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // in data am poza aleasa
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            uri = data.getData();
            ciwPozaProfilPacient.setImageURI(uri); // setez poza pe care a ales-o
        }
    }

    private void actualizeazaDate() {
        String nume = tietNumePacient.getText().toString();
        String prenume = tietPrenumePacient.getText().toString();
        String adresa = tietAdresa.getText().toString();
        long nrTelefon = Long.parseLong(tietNrTelefonPacient.getText().toString());
        double greutate = Double.parseDouble(tietGreutate.getText().toString());
        double inaltime = Double.parseDouble(tietInaltime.getText().toString());
        String email = tietEmailPacient.getText().toString();

//        if (!nume.equals(pacient.getNume()) || !prenume.equals(pacient.getPrenume())
//                || !adresa.equals(pacient.getAdresa()) || nrTelefon != pacient.getNrTelefon()
//                || greutate != pacient.getGreutate() || inaltime != pacient.getInaltime()) {
//            btnSalveaza.setEnabled(true);

        referintaUserConectat.child("nume").setValue(nume);
        referintaUserConectat.child("prenume").setValue(prenume);
        referintaUserConectat.child("adresa").setValue(adresa);
        referintaUserConectat.child("nrTelefon").setValue(nrTelefon);
        referintaUserConectat.child("greutate").setValue(greutate);
        referintaUserConectat.child("inaltime").setValue(inaltime);

        // TODO nu se incarca si in nav_header din Main datele modificate
        if (uri != null) {
            incarcaPoza();
        }

        if (!email.equals(pacientConectat.getEmail())) {
            // TODO
            // trb sa reautentific utilizatorul daca nu a fost recent logat
            // deci cred ca ar trb sa l pun sa introduca parola ca sa si poata schimba emailul,
            // ca sa o iau si sa-l reautentific eu gen
            // sau as putea sa pun in loc de btnSchimbaParola sa pun un buton de Schimba date de autentificare
            // si sa se deschida un fragment sau o activitate
//            AuthCredential credential = EmailAuthProvider.getCredential(pacientConectat.getEmail(), "123456");
//            pacientConectat.reauthenticate(credential)
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            Log.d("reautentificareUser", "User re-authenticated.");
//                        }
//                    });
            pacientConectat.updateEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    referintaUserConectat.child("adresaEmail").setValue(email);
                    Log.i("actualizareEmail", "Emailul a fost actualizat!");
                }
            });
        }

        Toast.makeText(getApplicationContext(), "Datele au fost actualizate!", Toast.LENGTH_SHORT).show();
//        }
    }

    private void incarcaPoza() {
        StorageReference caleFisier = FirebaseStorage.getInstance().getReference()
                .child("poze de profil").child(idUserConectat);
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, out);
        byte[] bytes = out.toByteArray();
        UploadTask upload = caleFisier.putBytes(bytes);

        upload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Nu s-a putut incarca poza!", Toast.LENGTH_SHORT).show();
            }
        });

        upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> rezultat = taskSnapshot.getStorage().getDownloadUrl();
                    rezultat.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String urlPoza = uri.toString();
                            Map mapPoza = new HashMap();
                            mapPoza.put("urlPozaProfil", urlPoza);

                            //si abia aici salvez url pozei in firebase....
                            referintaUserConectat.updateChildren(mapPoza).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Poza a fost incarcata cu succes!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
//                            finish();
                        }
                    });
                }
            }
        });
    }

    private void seteazaVizibilitateButoane(int v1, int v2) {
        btnModificaDate.setVisibility(v1);
        btnSalveaza.setVisibility(v2);
        btnRenunta.setVisibility(v2);
    }

    private void seteazaAccesibilitatea(boolean b) {
        tietNumePacient.setEnabled(b);
        tietPrenumePacient.setEnabled(b);
        tietAdresa.setEnabled(b);
        tietNrTelefonPacient.setEnabled(b);
        tietGreutate.setEnabled(b);
        tietInaltime.setEnabled(b);
        tietEmailPacient.setEnabled(b);
    }
}