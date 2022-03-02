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
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ProfilMedicActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String MEDICI = "Medici";
    public static final String SPECIALITATI = "Specialitati";
    public static final int REQUEST_CODE = 200;
    private CircleImageView ciwPozaProfilMedic;

    private TextInputEditText tietNota;
    private TextInputEditText tietNumeMedic;
    private TextInputEditText tietPrenumeMedic;
    private TextInputEditText tietNrTelefonMedic;
    private TextInputEditText tietEmailMedic;

    private TextView tvSpecialitate;

    private Spinner spnGradProfesional;
    private Spinner spnSpecialitate;

    private Button btnModificaDate;
    private Button btnSalveaza;
    private Button btnRenunta;
    private Button btnSchimbaParola;
    private Button btnStergeCont;

    private Toolbar toolbar;
    private FirebaseUser medicConectat;
    private FirebaseService firebaseServiceMedic = new FirebaseService(MEDICI);
    private FirebaseService firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
    private String idUserConectat;
    private DatabaseReference referintaUserConectat;

    private List<Specialitate> specialitati;

    private Medic medic;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_medic);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profil");
        // setez un button de back ca sa ma pot intoarce in pagina principala
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //todo poate grupez in functii initializarea si setarea even de click

        ciwPozaProfilMedic = findViewById(R.id.ciwPozaProfilMedic);

        tietNota = findViewById(R.id.tietNota);
        tietNumeMedic = findViewById(R.id.tietNumeMedic);
        tietPrenumeMedic = findViewById(R.id.tietPrenumeMedic);
        tietNrTelefonMedic = findViewById(R.id.tietNrTelefonMedic);
        tietEmailMedic = findViewById(R.id.tietEmailMedic);

        tvSpecialitate = findViewById(R.id.tvSpecialitate);

        spnGradProfesional = findViewById(R.id.spnGradProfesional);
        spnGradProfesional.setEnabled(false);
        ArrayAdapter<CharSequence> adapterGrad = ArrayAdapter.createFromResource(this, R.array.grade_profesionale,
                R.layout.support_simple_spinner_dropdown_item);
        spnGradProfesional.setAdapter(adapterGrad);

        spnSpecialitate = findViewById(R.id.spnSpecialitate);
        spnSpecialitate.setEnabled(false);
        specialitati = new ArrayList<>();
//        firebaseServiceSpecialitati.preiaSpecialitatiDinFirebase(preiaSpecialitati());

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

        medicConectat = FirebaseAuth.getInstance().getCurrentUser();
        idUserConectat = medicConectat.getUid();
        referintaUserConectat = firebaseServiceMedic.databaseReference.child(idUserConectat);

        referintaUserConectat.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                medic = snapshot.getValue(Medic.class);
                if (medic != null) {
                    String nume = medic.getNume();
                    String prenume = medic.getPrenume();
                    long nrTelefon = medic.getNrTelefon();
                    String adresaEmail = medic.getAdresaEmail();
//                    String idSpecialitate = medic.getIdSpecialitate();
                    double notaFeedback = medic.getNotaFeedback();
                    String gradProfesional = medic.getGradProfesional();
                    String urlPozaProfil = medic.getUrlPozaProfil();

                    tietNumeMedic.setText(nume);
                    tietPrenumeMedic.setText(prenume);

                    if (nrTelefon != 0)
                        tietNrTelefonMedic.setText(String.valueOf(nrTelefon));
                    else tietNrTelefonMedic.setText("");

                    tietEmailMedic.setText(adresaEmail);
                    tietNota.setText(String.valueOf(notaFeedback));

                    ArrayAdapter<String> adapter = (ArrayAdapter<String>) spnGradProfesional.getAdapter();
                    for (int i = 0; i < adapter.getCount(); i++) {
                        if (adapter.getItem(i).equals(gradProfesional)) {
                            spnGradProfesional.setSelection(i);
                            break;
                        }
                    }

                    firebaseServiceSpecialitati.preiaSpecialitatiDinFirebase(preiaSpecialitati());

                    if (!urlPozaProfil.equals("")) {
                        Glide.with(getApplicationContext()).load(urlPozaProfil).into(ciwPozaProfilMedic);
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

    private ValueEventListener preiaSpecialitati() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specialitati.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Specialitate s = dataSnapshot.getValue(Specialitate.class);
                    specialitati.add(s);
                }
                List<String> denumiriSpecialitati = new ArrayList<>();
//                denumiriSpecialitati.add("Selectati specialitatea");
                int poz = 0;
//                Toast.makeText(getApplicationContext(), String.valueOf(idSpecialitate),Toast.LENGTH_SHORT).show(); e null..
                for (int i = 0; i < specialitati.size(); i++) {
                    if (specialitati.get(i).getIdSpecialitate().equals(medic.getIdSpecialitate())) {
                        poz = i;
                    }
                    denumiriSpecialitati.add(specialitati.get(i).getDenumire());
                }
                ArrayAdapter<String> adapterSpec = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, denumiriSpecialitati);
                spnSpecialitate.setAdapter(adapterSpec);
                spnSpecialitate.setSelection(poz);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getApplicationContext(), "Nu s-a putut prelua specialitatea!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnModificaDate:
                ciwPozaProfilMedic.setOnClickListener(this);
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
            case R.id.ciwPozaProfilMedic:
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
            ciwPozaProfilMedic.setImageURI(uri); // setez poza pe care a ales-o
        }
    }

    private void actualizeazaDate() {
        if (inputValid()) {
            String nume = tietNumeMedic.getText().toString();
            String prenume = tietPrenumeMedic.getText().toString();

            long nrTelefon;
            if (!tietNrTelefonMedic.getText().toString().isEmpty())
                nrTelefon = Long.parseLong(tietNrTelefonMedic.getText().toString());
            else nrTelefon = 0;

            String gradProfesional = spnGradProfesional.getSelectedItem().toString();

            String specialitate = spnSpecialitate.getSelectedItem().toString();
            //todo something is fishy....
            String idSpecialitate = "";
            for (Specialitate s : specialitati) {
                if (s.getDenumire().equals(specialitate)) {
                    idSpecialitate = s.getIdSpecialitate();
                    break;
                }
            }

            String adresaEmail = tietEmailMedic.getText().toString();

            //todo un progress bar pana cand se actualizeaza si in bd datele
            if (nume.equals(medic.getNume()) && prenume.equals(medic.getPrenume())
                    && nrTelefon == medic.getNrTelefon() && gradProfesional.equals(medic.getGradProfesional())
                    && idSpecialitate.equals(medic.getIdSpecialitate()) && adresaEmail.equals(medicConectat.getEmail()) && uri == null) {
                Toast.makeText(getApplicationContext(), "Informatiile nu au fost modificate!", Toast.LENGTH_SHORT).show();

                seteazaAccesibilitatea(false);
                seteazaVizibilitateButoane(View.VISIBLE, View.GONE);
                return;
            }


            //todo poate fac direct update cu setValue ca la seminar
            referintaUserConectat.child("nume").setValue(nume);
            referintaUserConectat.child("prenume").setValue(prenume);
            referintaUserConectat.child("nrTelefon").setValue(nrTelefon);
            referintaUserConectat.child("gradProfesional").setValue(gradProfesional);
            referintaUserConectat.child("idSpecialitate").setValue(idSpecialitate);

            if (uri != null) {
                incarcaPoza();
            }

            if (!adresaEmail.equals(medicConectat.getEmail())) {
                // TODO !!!!!
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
                medicConectat.updateEmail(adresaEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        referintaUserConectat.child("adresaEmail").setValue(adresaEmail);
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

    // todo poate mai bn fac o clasa care sa contina functia asta si s o apelez mereu cand am nevoie
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

    private boolean inputValid() {
        if (tietNumeMedic.getText().toString().isEmpty()) {
            tietNumeMedic.setError("Introduceti numele!");
            tietNumeMedic.requestFocus();
            return false;
        }

        if (tietPrenumeMedic.getText().toString().isEmpty()) {
            tietPrenumeMedic.setError("Introduceti prenumele!");
            tietPrenumeMedic.requestFocus();
            return false;
        }

        Pattern pattern = Pattern.compile("^407[2-8][0-9]{7}$");
        Matcher matcher = pattern.matcher(tietNrTelefonMedic.getText().toString());
        if (!tietNrTelefonMedic.getText().toString().isEmpty() && !matcher.matches()) {
            tietNrTelefonMedic.setError("Formatul acceptat este: 407xxxxxxxx!");
            tietNrTelefonMedic.requestFocus();
            return false;
        }

        if (spnSpecialitate.getSelectedItem().toString().equals("Selectati specialitatea")) {
            //android:focusable="true"
            //android:focusableInTouchMode="true"
            //in xml ca sa apara si textul
            tvSpecialitate.setError("Alegeti specialitatea!");
            spnSpecialitate.requestFocus();
            return false;
        }

        if (tietEmailMedic.getText().toString().isEmpty()) {
            tietEmailMedic.setError("Introduceti emailul!");
            tietEmailMedic.requestFocus();
            return false;
        }

        pattern = Pattern.compile("^([A-Za-z0-9._]+)(@clinica-medicala\\.ro)$");
        matcher = pattern.matcher(tietEmailMedic.getText().toString());
        if (!matcher.matches()) {
            tietEmailMedic.setError("Introduceti emailul oficial (de forma adresa@clinica-medicala.ro)!");
            tietEmailMedic.requestFocus();
            return false;
        }

        return true;
    }

    private void seteazaVizibilitateButoane(int v1, int v2) {
        btnModificaDate.setVisibility(v1);
        btnSalveaza.setVisibility(v2);
        btnRenunta.setVisibility(v2);
    }

    private void seteazaAccesibilitatea(boolean b) {
        tietNumeMedic.setEnabled(b);
        tietPrenumeMedic.setEnabled(b);
        tietNrTelefonMedic.setEnabled(b);
        spnGradProfesional.setEnabled(b);
        spnSpecialitate.setEnabled(b);
        tietEmailMedic.setEnabled(b);
    }
}