package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class InregistrarePacientActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 200;
    public static final String PACIENTI = "Pacienti";
    public static final String MASCULIN = "Masculin";
    public static final String FEMININ = "Feminin";
    private CircleImageView ciwPozaProfilPacient;

    private TextInputEditText tietDataNasterii;

    private TextInputEditText tietNumePacient;
    private TextInputEditText tietPrenumePacient;
    private TextInputEditText tietCnp;
    private TextInputEditText tietNrTelefonPacient;
    private TextInputEditText tietAdresa;
    private TextInputEditText tietGreutate;
    private TextInputEditText tietInaltime;

    private AutoCompleteTextView actvGrupeSange;

    private TextInputEditText tietEmailPacient;
    private TextInputEditText tietParolaPacient;
    private TextInputEditText tietConfirmareParolaPacient;

    private AppCompatButton btnInregistrarePacient;

    private ProgressDialog progressDialog;

    private Uri uri;

    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseService firebaseService = new FirebaseService(PACIENTI);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare_pacient);

        initializeazaAtribute();

        seteazaAdaptorGrupeSange();

        ciwPozaProfilPacient.setOnClickListener(this);
        tietDataNasterii.setOnClickListener(this);
        btnInregistrarePacient.setOnClickListener(this);
    }

    private void seteazaAdaptorGrupeSange() {
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.grupe_sange));
        actvGrupeSange.setAdapter(adapter);
    }

    private void initializeazaAtribute() {
        ciwPozaProfilPacient = findViewById(R.id.ciwPozaProfilUser);
        tietDataNasterii = findViewById(R.id.tietDataNasterii);
        tietNumePacient = findViewById(R.id.tietNumePacient);
        tietPrenumePacient = findViewById(R.id.tietPrenumePacient);
        tietCnp = findViewById(R.id.tietCnp);
        tietNrTelefonPacient = findViewById(R.id.tietNrTelefonPacient);
        tietAdresa = findViewById(R.id.tietAdresa);
        tietGreutate = findViewById(R.id.tietGreutate);
        tietInaltime = findViewById(R.id.tietInaltime);

        actvGrupeSange = findViewById(R.id.actvGrupeSange);

        tietEmailPacient = findViewById(R.id.tietEmailPacient);
        tietParolaPacient = findViewById(R.id.tietParolaPacient);
        tietConfirmareParolaPacient = findViewById(R.id.tietConfirmareParolaPacient);

        btnInregistrarePacient = findViewById(R.id.btnInregistrarePacient);

        mAuth = FirebaseAuth.getInstance();
    }

    @SuppressLint("NonConstantResourceId")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tietDataNasterii:
                afiseazaCalendar();
                break;
            case R.id.btnInregistrarePacient:
                inregistreazaPacient();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void inregistreazaPacient() {
        if (inputValid()) {

            String nume = tietNumePacient.getText().toString().trim();
            String prenume = tietPrenumePacient.getText().toString().trim();
            Long cnp = Long.parseLong(tietCnp.getText().toString().trim());
            Long nrTelefon = Long.parseLong("4" + tietNrTelefonPacient.getText().toString().trim());

            String sex = "";
            int primaCifraCnp = Integer.parseInt(String.valueOf(String.valueOf(cnp).charAt(0)));
            if (primaCifraCnp == 1 || primaCifraCnp == 5) {
                sex = MASCULIN;
            } else if (primaCifraCnp == 6 || primaCifraCnp == 2) {
                sex = FEMININ;
            }

            String dataNasterii = tietDataNasterii.getText().toString();
            int varsta = 0;
            try {
                DateFormat formatInitial = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                DateFormat formatSchimbat = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
                Date data = formatInitial.parse(dataNasterii);
                String dataFormatata = formatSchimbat.format(data);

                varsta = calculeazaVarsta(dataFormatata);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String grupaSange = actvGrupeSange.getText().toString();

            String adresaEmail = tietEmailPacient.getText().toString().trim();
            String parola = tietParolaPacient.getText().toString().trim();

            String adresa;
            if (tietAdresa.getText().toString().isEmpty()) {
                adresa = getString(R.string.necunoscut);
            } else {
                adresa = tietAdresa.getText().toString().trim();
            }

            double greutate;
            if (tietGreutate.getText().toString().isEmpty()) {
                greutate = 0.0;
            } else {
                greutate = Double.parseDouble(tietGreutate.getText().toString().trim());
            }

            double inaltime;
            if (tietInaltime.getText().toString().isEmpty()) {
                inaltime = 0.0;
            } else {
                inaltime = Double.parseDouble(tietInaltime.getText().toString().trim());
            }


            int finalVarsta = varsta;
            String finalSex = sex;

            progressDialog = new ProgressDialog(InregistrarePacientActivity.this, R.style.ProgressDialogStyle);
            progressDialog.setMessage("Se creează contul...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(adresaEmail, parola)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // daca userul a fost inregistrat
                            if (task.isSuccessful()) {
                                String idPacient = mAuth.getCurrentUser().getUid();

                                Pacient pacient = new Pacient(idPacient, nume,
                                        prenume,
                                        cnp,
                                        nrTelefon,
                                        finalSex,
                                        adresa,
                                        dataNasterii,
                                        adresaEmail,
                                        grupaSange,
                                        greutate,
                                        inaltime,
                                        finalVarsta,
                                        "");

                                firebaseService.databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(pacient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(),
                                                    "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
                                            finish();
                                            mAuth.signOut();
                                        } else {
                                            Log.e("adaugarePacient", task.getException().getMessage());
                                        }

                                        progressDialog.dismiss();
                                    }
                                });

                                // compresia pozei de profil
                                if (uri != null)
                                    incarcaPoza();

                            } else {
                                Log.e("inregistrarePacient", task.getException().getMessage());
                            }
                        }
                    });
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private int calculeazaVarsta(String dataFormatata) {
        LocalDate dataNasterii = LocalDate.of(Integer.parseInt(dataFormatata.substring(0, 4)),
                Integer.parseInt(dataFormatata.substring(5, 7)),
                Integer.parseInt(dataFormatata.substring(8)));
        LocalDate dataCurenta = LocalDate.now();
        if (dataNasterii != null && dataCurenta != null) {
            return Period.between(dataNasterii, dataCurenta).getYears();
        }
        return 0;
    }

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
                Toast.makeText(getApplicationContext(), "Nu s-a putut încărca poza de profil!", Toast.LENGTH_SHORT).show();
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
                            firebaseService.databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(mapPoza).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Log.i("incarcarePoza", "Poza a fost incarcata cu succes!");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Poza de profil nu a putut fi incarcata!", Toast.LENGTH_SHORT).show();
                                        Log.e("incarcarePoza", task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });

    }

    private boolean inputValid() {
        Pattern pattern;
        Matcher matcher;

        if (tietNumePacient.getText().toString().isEmpty()) {
            tietNumePacient.setError(getString(R.string.err_empty_nume));
            tietNumePacient.requestFocus();
            return false;
        }

        if (tietPrenumePacient.getText().toString().isEmpty()) {
            tietPrenumePacient.setError(getString(R.string.err_empty_prenume));
            tietPrenumePacient.requestFocus();
            return false;
        }

        pattern = Pattern.compile(getString(R.string.pattern_cnp));
        matcher = pattern.matcher(tietCnp.getText().toString());

        if (tietCnp.getText().toString().isEmpty()) {
            tietCnp.setError(getString(R.string.err_empty_cnp));
            tietCnp.requestFocus();
            return false;
        }

        if (!matcher.matches()) {
            tietCnp.setError(getString(R.string.err_not_valid_cnp));
            tietCnp.requestFocus();
            return false;
        }

        pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
        matcher = pattern.matcher(tietNrTelefonPacient.getText().toString());

        if (tietNrTelefonPacient.getText().toString().isEmpty()) {
            tietNrTelefonPacient.setError(getString(R.string.err_empty_telefon));
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        if (!matcher.matches()) {
            tietNrTelefonPacient.setError(getString(R.string.err_not_valid_telefon));
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        if (tietDataNasterii.getText().toString().isEmpty()) {
            tietDataNasterii.setError(getString(R.string.err_empty_data_nasterii));
            tietDataNasterii.requestFocus();
            return false;
        }

        String anCnp = tietCnp.getText().toString().substring(1, 3);
        String lunaCnp = tietCnp.getText().toString().substring(3, 5);
        String ziCnp = tietCnp.getText().toString().substring(5, 7);
        try {
            Date dataNasteriiCnp = new SimpleDateFormat("dd/MM/yy", Locale.US).parse(ziCnp + "/" + lunaCnp + "/" + anCnp);
            Date dataNasterii = new SimpleDateFormat("dd/MM/yy", Locale.US).parse(tietDataNasterii.getText().toString());
            if (!dataNasterii.equals(dataNasteriiCnp)) {
                tietDataNasterii.setError(getString(R.string.err_not_valid_data_nasterii));
                tietDataNasterii.requestFocus();
                return false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (!tietGreutate.getText().toString().isEmpty() && !tietGreutate.getText().toString().equals(getString(R.string.default_empty_double))) {
            pattern = Pattern.compile(getString(R.string.pattern_greutate));
            matcher = pattern.matcher(tietGreutate.getText().toString());
            if (!matcher.matches()) {
                tietGreutate.setError(getString(R.string.err_not_valid_greutate));
                tietGreutate.requestFocus();
                return false;
            }

        }

        if (!tietInaltime.getText().toString().isEmpty() && !tietInaltime.getText().toString().equals(getString(R.string.default_empty_double))) {

            pattern = Pattern.compile(getString(R.string.pattern_inaltime));
            matcher = pattern.matcher(tietInaltime.getText().toString());
            if (!matcher.matches()) {
                tietInaltime.setError(getString(R.string.err_not_valid_inaltime));
                tietInaltime.requestFocus();
                return false;
            }
        }

        if (tietEmailPacient.getText().toString().isEmpty()) {
            tietEmailPacient.setError(getString(R.string.err_empty_email));
            tietEmailPacient.requestFocus();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(tietEmailPacient.getText().toString()).matches()) {
            tietEmailPacient.setError(getString(R.string.err_not_valid_email));
            tietEmailPacient.requestFocus();
            return false;
        }

        if (tietParolaPacient.getText().toString().isEmpty()) {
            tietParolaPacient.setError(getString(R.string.err_empty_parola));
            tietParolaPacient.requestFocus();
            return false;
        }

        if (tietParolaPacient.getText().toString().length() < 6) {
            tietParolaPacient.setError(getString(R.string.err_not_valid_parola));
            tietParolaPacient.requestFocus();
            return false;
        }

        if (tietConfirmareParolaPacient.getText().toString().isEmpty()) {
            tietConfirmareParolaPacient.setError(getString(R.string.err_empty_confirmare_parola));
            tietConfirmareParolaPacient.requestFocus();
            return false;
        }

        if (!tietParolaPacient.getText().toString().equals(tietConfirmareParolaPacient.getText().toString())) {
            tietConfirmareParolaPacient.setError(getString(R.string.err_not_valid_confirmare_parola));
            tietConfirmareParolaPacient.requestFocus();
            return false;
        }

        return true;
    }


    private void afiseazaCalendar() {
        tietDataNasterii.setError(null);
        final Calendar calendar = Calendar.getInstance();

        if (!tietDataNasterii.getText().toString().equals(getString(R.string.selectati_data))) {
            try {
                Date dataSelectata = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(tietDataNasterii.getText().toString());
                calendar.setTime(dataSelectata);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int zi = calendar.get(Calendar.DATE);
        int luna = calendar.get(Calendar.MONTH);
        int an = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(InregistrarePacientActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int an, int luna, int zi) {
                        luna += 1;
                        String data = zi + "/" + luna + "/" + an;
                        tietDataNasterii.setText(data);
                    }
                }, an, luna, zi);

        //setare data maxima
        Calendar dataMaxima = Calendar.getInstance();
        dataMaxima.add(Calendar.YEAR, -14);
        datePickerDialog.getDatePicker().setMaxDate(dataMaxima.getTimeInMillis());

        datePickerDialog.show();
    }

}