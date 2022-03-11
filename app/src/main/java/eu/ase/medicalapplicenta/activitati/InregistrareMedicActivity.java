package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
import eu.ase.medicalapplicenta.entitati.ZiDeLucru;
import eu.ase.medicalapplicenta.utile.FirebaseService;

//todo sa testez iar ca merge
public class InregistrareMedicActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String SPECIALITATI = "Specialitati";
    public static final String MEDICI = "Medici";
    public static final int REQUEST_CODE = 200;

    private CircleImageView ciwPozaProfilMedic;

    private TextInputEditText tietNumeMedic;
    private TextInputEditText tietPrenumeMedic;
    private TextInputEditText tietNrTelefonMedic;
    private TextInputEditText tietEmailMedic;
    private TextInputEditText tietParolaMedic;
    private TextInputEditText tietConfirmareParolaMedic;

    private TextView tvSpecialitate;

    private Spinner spnGradProfesional;
    private Spinner spnSpecialitate;

    private Button btnInregistrareMedic;

    private List<Specialitate> specialitati;

    private FirebaseService firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
    private FirebaseService firebaseServiceMedici = new FirebaseService(MEDICI);
    private FirebaseAuth mAuth;

    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare_medic);

        initializeazaAtribute();

        ciwPozaProfilMedic.setOnClickListener(this);

        //todo sa fac dropdown menu in loc de spinner si un progress bar pana se incarca specialitatile maybe
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.grade_profesionale,
                R.layout.support_simple_spinner_dropdown_item);
        spnGradProfesional.setAdapter(adapter);

        firebaseServiceSpecialitati.preiaDateDinFirebase(preiaSpecialitati());

        btnInregistrareMedic.setOnClickListener(this);
    }

    private void initializeazaAtribute() {
        ciwPozaProfilMedic = findViewById(R.id.ciwPozaProfilMedic);
        tietNumeMedic = findViewById(R.id.tietNumeMedic);
        tietPrenumeMedic = findViewById(R.id.tietPrenumeMedic);
        tietNrTelefonMedic = findViewById(R.id.tietNrTelefonMedic);
        tietEmailMedic = findViewById(R.id.tietEmailMedic);
        tietParolaMedic = findViewById(R.id.tietParolaMedic);
        tietConfirmareParolaMedic = findViewById(R.id.tietConfirmareParolaMedic);

        tvSpecialitate = findViewById(R.id.tvSpecialitate);

        spnGradProfesional = findViewById(R.id.spnGradProfesional);

        specialitati = new ArrayList<>();

        spnSpecialitate = findViewById(R.id.spnSpecialitate);

        btnInregistrareMedic = findViewById(R.id.btnInregistrareMedic);

        mAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ciwPozaProfilMedic:
                alegePozaProfil();
                break;
            case R.id.btnInregistrareMedic:
                inregistreazaMedic();
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

    private void inregistreazaMedic() {
        tvSpecialitate.setError(null);
        if (inputValid()) {
            String nume = tietNumeMedic.getText().toString();
            String prenume = tietPrenumeMedic.getText().toString();

            long nrTelefon;
            if (!tietNrTelefonMedic.getText().toString().isEmpty())
                nrTelefon = Long.parseLong(tietNrTelefonMedic.getText().toString());
            else nrTelefon = 0;

            String gradProfesional = spnGradProfesional.getSelectedItem().toString();
//            if (spnGradProfesional.getSelectedItemPosition() == 0) {
//                gradProfesional = "Nespecificat";
//            } else gradProfesional = spnGradProfesional.getSelectedItem().toString();

            String specialitate = spnSpecialitate.getSelectedItem().toString();
            String idSpecialitate = "";
            for (Specialitate s : specialitati) {
                if (s.getDenumire().equals(specialitate)) {
                    idSpecialitate = s.getIdSpecialitate();
                    break;
                }
            }

            String adresaEmail = tietEmailMedic.getText().toString();
            String parola = tietParolaMedic.getText().toString();

            String finalIdSpecialitate = idSpecialitate;
            mAuth.createUserWithEmailAndPassword(adresaEmail, parola)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                //todo sa iti aleaga el programul
                                String idMedic = mAuth.getCurrentUser().getUid();
                                List<ZiDeLucru> program = new ArrayList<>();
                                program.add(new ZiDeLucru("luni", "12:00", "15:20"));
                                program.add(new ZiDeLucru("miercuri", "14:00", "18:20"));

                                Medic medic = new Medic(idMedic, nume, prenume, nrTelefon, adresaEmail, finalIdSpecialitate, 0.0, gradProfesional, "", program);

                                firebaseServiceMedici.databaseReference.child(idMedic)
                                        .setValue(medic).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
//                                            medic.setIdMedic(FirebaseAuth.getInstance().getCurrentUser().getUid());//aici
                                            Toast.makeText(getApplicationContext(), "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Log.e("adaugareMedic", task.getException().getMessage());
                                        }
                                    }
                                });

                                if (uri != null)
                                    incarcaPoza();
                            } else {
                                Log.e("creareContMedic", task.getException().getMessage());
                            }
                        }
                    });

        }
    }

    //todo poate fac o clasa ca sa apelez funtia asta cand am nevoie
    private void incarcaPoza() {
        StorageReference caleFisier = FirebaseStorage.getInstance().getReference()
                .child("poze de profil").child(mAuth.getCurrentUser().getUid());
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
                            firebaseServiceMedici.databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(mapPoza).addOnCompleteListener(new OnCompleteListener() {
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

        Pattern pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
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

        pattern = Pattern.compile(getString(R.string.pattern_email_medic));
        matcher = pattern.matcher(tietEmailMedic.getText().toString());
        if (!matcher.matches()) {
            tietEmailMedic.setError("Introduceti emailul oficial (de forma adresa@clinica-medicala.ro)!");
            tietEmailMedic.requestFocus();
            return false;
        }

        if (tietParolaMedic.getText().toString().isEmpty()) {
            tietParolaMedic.setError("Introduceti parola!");
            tietParolaMedic.requestFocus();
            return false;
        }

        if (tietParolaMedic.getText().toString().length() < 6) {
            tietParolaMedic.setError("Parola trebuie sa contina cel putin 6 caractere!");
            tietParolaMedic.requestFocus();
            return false;
        }

        if (tietConfirmareParolaMedic.getText().toString().isEmpty()) {
            tietConfirmareParolaMedic.setError("Reintroduceti parola!");
            tietConfirmareParolaMedic.requestFocus();
            return false;
        }

        if (!tietParolaMedic.getText().toString().equals(tietConfirmareParolaMedic.getText().toString())) {
            tietConfirmareParolaMedic.setError("Parola nu corespunde!");
            tietConfirmareParolaMedic.requestFocus();
            return false;
        }

        return true;
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
                denumiriSpecialitati.add("Selectati specialitatea");
                for (Specialitate s : specialitati) {
                    denumiriSpecialitati.add(s.getDenumire());
                }
                ArrayAdapter<String> adapterSpec = new ArrayAdapter<>(getApplicationContext(),
                        android.R.layout.simple_spinner_dropdown_item, denumiriSpecialitati);
                spnSpecialitate.setAdapter(adapterSpec);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
            }
        };
    }
}