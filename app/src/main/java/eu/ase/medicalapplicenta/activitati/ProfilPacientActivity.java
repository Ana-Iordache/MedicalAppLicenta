package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ProfilPacientActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 200;
    private CircleImageView ciwPozaProfilPacient; //todo sa modific si in xml inapoi

    private TextInputEditText tietNumePacient;
    private TextInputEditText tietPrenumePacient;
    private TextInputEditText tietCnp;
    private TextInputEditText tietDataNasterii;
    private TextInputEditText tietNrTelefonPacient;
    private TextInputEditText tietAdresa;
    private TextInputEditText tietEmailPacient;
    private TextInputEditText tietGreutate;
    private TextInputEditText tietInaltime;
    private TextInputEditText tietVarsta;
    private TextInputEditText tietSex;

    private Spinner spGrupaSange;

    private Button btnModificaDate;
    private Button btnSalveaza;
    private Button btnRenunta;
    private Button btnSchimbaParola;
    private Button btnStergeCont;

    private Toolbar toolbar;

    private FirebaseUser pacientConectat;
    private FirebaseService firebaseService = new FirebaseService("Pacienti");
    private String idUserConectat;
    private DatabaseReference referintaUserConectat;
    private Pacient pacient;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_pacient);

        initializeazaAtribute();

        seteazaToolbar();

        spGrupaSange.setEnabled(false); // din xml nu merge

        btnModificaDate.setOnClickListener(this);
        btnSalveaza.setOnClickListener(this);
        btnRenunta.setOnClickListener(this);
        btnSchimbaParola.setOnClickListener(this);
        btnStergeCont.setOnClickListener(this);

        firebaseService.preiaObiectDinFirebase(preiaPacient(), idUserConectat);
    }

    private ValueEventListener preiaPacient() {
        return new ValueEventListener() {
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
                Log.e("preluarePacient", error.getMessage());
            }
        };
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        ciwPozaProfilPacient = findViewById(R.id.ciwPozaProfilUser);
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

        btnModificaDate = findViewById(R.id.btnModificaDate);
        btnSalveaza = findViewById(R.id.btnSalveaza);
        btnRenunta = findViewById(R.id.btnRenunta);
        btnSchimbaParola = findViewById(R.id.btnSchimbaParola);
        btnStergeCont = findViewById(R.id.btnStergeCont);

        pacientConectat = FirebaseAuth.getInstance().getCurrentUser();
        idUserConectat = pacientConectat.getUid();
        referintaUserConectat = firebaseService.databaseReference.child(idUserConectat);
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

    @SuppressLint("NonConstantResourceId")
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
            case R.id.btnSalveaza:
                actualizeazaDate();
                break;
            case R.id.btnRenunta:
                seteazaAccesibilitatea(false);
                seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
                break;
            case R.id.ciwPozaProfilUser:
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
        //todo sa poata modifica si grupa de sange
        if (inputValid()) {

            String nume = tietNumePacient.getText().toString().trim();
            String prenume = tietPrenumePacient.getText().toString().trim();

            String adresa;
            if (tietAdresa.getText().toString().isEmpty()) {
                adresa = "Necunoscuta";
                tietAdresa.setText(adresa);
            } else {
                adresa = tietAdresa.getText().toString().trim();
            }

            long nrTelefon = Long.parseLong(tietNrTelefonPacient.getText().toString());

            double greutate;
            if (tietGreutate.getText().toString().isEmpty()) {
                greutate = 0.0;
                tietGreutate.setText(String.valueOf(greutate));
            } else {
                greutate = Double.parseDouble(tietGreutate.getText().toString().trim());
            }

            double inaltime;
            if (tietInaltime.getText().toString().isEmpty()) {
                inaltime = 0.0;
                tietInaltime.setText(String.valueOf(inaltime));
            } else {
                inaltime = Double.parseDouble(tietInaltime.getText().toString().trim());
            }

            String email = tietEmailPacient.getText().toString().trim();

            if (nume.equals(pacient.getNume()) && prenume.equals(pacient.getPrenume())
                    && adresa.equals(pacient.getAdresa()) && nrTelefon == pacient.getNrTelefon()
                    && greutate == pacient.getGreutate() && inaltime == pacient.getInaltime() && uri == null) {
                Toast.makeText(getApplicationContext(), "Informatiile nu au fost modificate!", Toast.LENGTH_SHORT).show();
                return;
            }

            referintaUserConectat.child("nume").setValue(nume);
            referintaUserConectat.child("prenume").setValue(prenume);
            referintaUserConectat.child("adresa").setValue(adresa);
            referintaUserConectat.child("nrTelefon").setValue(nrTelefon);
            referintaUserConectat.child("greutate").setValue(greutate);
            referintaUserConectat.child("inaltime").setValue(inaltime);

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

                // TODO sa fac si schimbare parola
            }

            Toast.makeText(getApplicationContext(), "Datele au fost actualizate!", Toast.LENGTH_SHORT).show();

            seteazaAccesibilitatea(false);
            seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
        }
    }

    private boolean inputValid() {
        Pattern pattern;
        Matcher matcher;

        if (tietNumePacient.getText().toString().isEmpty()) {
            tietNumePacient.setError("Introduceti numele!");
            tietNumePacient.requestFocus();
            return false;
        }

        if (tietPrenumePacient.getText().toString().isEmpty()) {
            tietPrenumePacient.setError("Introduceti prenumele!");
            tietPrenumePacient.requestFocus();
            return false;
        }


        if (tietNrTelefonPacient.getText().toString().isEmpty()) {
            tietNrTelefonPacient.setError("Introduceti numarul de telefon!");
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
        matcher = pattern.matcher(tietNrTelefonPacient.getText().toString());
        if (!matcher.matches()) {
            tietNrTelefonPacient.setError("Formatul acceptat este: 407xxxxxxxx!");
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        if (!tietGreutate.getText().toString().isEmpty() && !tietGreutate.getText().toString().equals("0.0")) {
            pattern = Pattern.compile(getString(R.string.pattern_greutate));
            matcher = pattern.matcher(tietGreutate.getText().toString());
            if (!matcher.matches()) {
                tietGreutate.setError("Introduceti o greutate valida (de ex: 45.7)!");
                tietGreutate.requestFocus();
                return false;
            }

        }

        if (!tietInaltime.getText().toString().isEmpty() && !tietInaltime.getText().toString().equals("0.0")) {

            pattern = Pattern.compile(getString(R.string.pattern_inaltime));
            matcher = pattern.matcher(tietInaltime.getText().toString());
            if (!matcher.matches()) {
                tietInaltime.setError("Introduceti un o inaltime valida (de ex: 1.65)!");
                tietInaltime.requestFocus();
                return false;
            }
        }

        if (tietEmailPacient.getText().toString().isEmpty()) {
            tietEmailPacient.setError("Introduceti emailul!");
            tietEmailPacient.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tietEmailPacient.getText().toString()).matches()) {
            tietEmailPacient.setError("Introduceti un email valid!");
            tietEmailPacient.requestFocus();
            return false;
        }

        return true;
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