package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    CircleImageView ciwPozaProfilPacient;

    TextInputEditText tietDataNasterii;
//    ImageView imgCalendar;
//    Icon icCalendar;

    TextInputEditText tietNumePacient;
    TextInputEditText tietPrenumePacient;
    TextInputEditText tietCnp;
    TextInputEditText tietNrTelefonPacient;
    TextInputEditText tietAdresa;
    TextInputEditText tietGreutate;
    TextInputEditText tietInaltime;

//    RadioGroup rgSex;
//    RadioButton radioButton;

    Spinner spGrupaSange;

    TextInputEditText tietEmailPacient;
    TextInputEditText tietParolaPacient;
    TextInputEditText tietConfirmareParolaPacient;

    Button btnInregistrarePacient;

    ProgressBar progressBar;
//    ProgressDialog progressDialog;

    Uri uri;
    //    String urlPoza = "";
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private FirebaseService firebaseService = new FirebaseService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare_pacient);

        ciwPozaProfilPacient = findViewById(R.id.ciwPozaProfilPacient);
        ciwPozaProfilPacient.setOnClickListener(this);

        tietDataNasterii = findViewById(R.id.tietDataNasterii);
//        tietDataNasterii.setEnabled(false); TODO daca il setez enabled nu ma lasa sa dau click
//        imgCalendar = findViewById(R.id.imgCalendar);
//        imgCalendar.setOnClickListener(this);
        tietDataNasterii.setOnClickListener(this);

        tietNumePacient = findViewById(R.id.tietNumePacient);
        tietPrenumePacient = findViewById(R.id.tietPrenumePacient);
        tietCnp = findViewById(R.id.tietCnp);
        tietNrTelefonPacient = findViewById(R.id.tietNrTelefonPacient);
        tietAdresa = findViewById(R.id.tietAdresa);
        tietGreutate = findViewById(R.id.tietGreutate);
        tietInaltime = findViewById(R.id.tietInaltime);

//        rgSex = findViewById(R.id.rgSex);

        spGrupaSange = findViewById(R.id.spGrupaSange);

        tietEmailPacient = findViewById(R.id.tietEmailPacient);
        tietParolaPacient = findViewById(R.id.tietParolaPacient);
        tietConfirmareParolaPacient = findViewById(R.id.tietConfirmareParolaPacient);

        btnInregistrarePacient = findViewById(R.id.btnInregistrarePacient);
        btnInregistrarePacient.setOnClickListener(this);


        progressBar = findViewById(R.id.progressBar);
//        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tietDataNasterii:
                afiseazaCalendar();
                break;
            case R.id.btnInregistrarePacient:
                inregistrarePacient();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void inregistrarePacient() {
        if (validareInput()) {

            String nume = tietNumePacient.getText().toString();
            String prenume = tietPrenumePacient.getText().toString();
            Long cnp = Long.parseLong(tietCnp.getText().toString());
            Long nrTelefon = Long.parseLong(tietNrTelefonPacient.getText().toString());

//        radioButton = findViewById(rgSex.getCheckedRadioButtonId());
//        String sex = String.valueOf(radioButton.getText().toString().charAt(0));

            String sex = "";
            int primaCifraCnp = Integer.parseInt(String.valueOf(String.valueOf(cnp).charAt(0)));
            if (primaCifraCnp == 1 || primaCifraCnp == 5) {
                sex = "Masculin";
            } else if (primaCifraCnp == 6 || primaCifraCnp == 2) {
                sex = "Feminin";
            }

//        String adresa = tietAdresa.getText().toString();

            String dataNasterii = tietDataNasterii.getText().toString();
            int varsta = 0;
            try {
                DateFormat formatInitial = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                DateFormat formatSchimbat = new SimpleDateFormat("yyyy/MM/dd");
                Date data = formatInitial.parse(dataNasterii);
                String dataFormatata = formatSchimbat.format(data);

                varsta = calculeazaVarsta(dataFormatata);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String grupaSange = spGrupaSange.getSelectedItem().toString();

//        Double greutate = Double.valueOf(tietGreutate.getText().toString());
//        Double inaltime = Double.valueOf(tietInaltime.getText().toString());


            String adresaEmail = tietEmailPacient.getText().toString();
            String parola = tietParolaPacient.getText().toString();
            String confirmareParola = tietConfirmareParolaPacient.getText().toString();

            String adresa;
            if (tietAdresa.getText().toString().isEmpty()) {
                adresa = "Necunoscuta";
            } else {
                adresa = tietAdresa.getText().toString();
            }

            double greutate;
            if (tietGreutate.getText().toString().isEmpty()) {
                greutate = 0.0;
            } else {
                greutate = Double.parseDouble(tietGreutate.getText().toString());
            }

            double inaltime;
            if (tietInaltime.getText().toString().isEmpty()) {
                inaltime = 0.0;
            } else {
                inaltime = Double.parseDouble(tietInaltime.getText().toString());
            }


//        Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();

//        progressBar.setVisibility(View.VISIBLE);
//        progressDialog.setMessage("Se creeaza contul...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        progressDialog.show();

//        validareInput(nume, prenume, cnp, nrTelefon, dataNasterii, adresaEmail, parola, confirmareParola);
//        if (nume.isEmpty()) {
//            tietNumePacient.setError("Introduceti numele!");
//            tietNumePacient.requestFocus();
//            return;
//        }
//
//        if (prenume.isEmpty()) {
//            tietPrenumePacient.setError("Introduceti prenumele!");
//            tietPrenumePacient.requestFocus();
//            return;
//        }
//
//        if (String.valueOf(nrTelefon).isEmpty()) {
//            tietNrTelefonPacient.setError("Introduceti numarul de telefon!");
//            tietNrTelefonPacient.requestFocus();
//            return;
//        }
//
//        if (String.valueOf(cnp).isEmpty()) {
//            tietCnp.setError("Introduceti CNP-ul!");
//            tietCnp.requestFocus();
//            return;
//        }
//
//
//        if (dataNasterii.equals("dd/MM/yyyy")) {
//            tietDataNasterii.setError("Alegeti data nasterii!");
//            tietDataNasterii.requestFocus();
//            return;
//        }
//
//        if (adresaEmail.isEmpty()) {
//            tietEmailPacient.setError("Introduceti emailul!");
//            tietEmailPacient.requestFocus();
//            return;
//        }
//
//        if (!Patterns.EMAIL_ADDRESS.matcher(adresaEmail).matches()) {
//            tietEmailPacient.setError("Introduceti un email valid!");
//            tietEmailPacient.requestFocus();
//            return;
//        }
//
//        if (parola.isEmpty()) {
//            tietParolaPacient.setError("Introduceti parola!");
//            tietParolaPacient.requestFocus();
//            return;
//        }
//
//        if (parola.length() < 6) {
//            tietParolaPacient.setError("Parola trebuie sa contina cel putin 6 caractere!");
//            tietParolaPacient.requestFocus();
//            return;
//        }
//
//        if (!parola.equals(confirmareParola)) {
//            tietConfirmareParolaPacient.setError("Parola nu corespunde!");
//            tietConfirmareParolaPacient.requestFocus();
//            return;
//        }
            int finalVarsta = varsta;
            String finalSex = sex;
//        if () {

            mAuth.createUserWithEmailAndPassword(adresaEmail, parola)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // daca userul a fost inregistrat
                            if (task.isSuccessful()) {
//                            Pacient pacient = new Pacient(nume, prenume, cnp, nrTelefon, "", finalAdresa, dataNasterii, adresaEmail, urlPozaProfil);
                                Pacient pacient = new Pacient(nume, prenume, cnp, nrTelefon, finalSex, adresa, dataNasterii, adresaEmail, grupaSange, greutate, inaltime, finalVarsta, "");

                                firebaseService.databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .setValue(pacient).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
                                            finish();
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                        progressBar.setVisibility(View.GONE);
                                        }

                                    }
                                });

                                // compresia pozei de profil
                                if (uri != null)
                                    incarcaPoza();

                            } else {
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                            progressBar.setVisibility(View.GONE);
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
                            firebaseService.databaseReference.child(mAuth.getCurrentUser().getUid()).updateChildren(mapPoza).addOnCompleteListener(new OnCompleteListener() {
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
//                                progressDialog.dismiss();

    }

    // TODO
    // data nasterii sa corespunda cu cnp-ul
    // greutatea si inaltimea sa fie double
    private boolean validareInput() {
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

        Pattern patternCnp = Pattern.compile("^[1-9]\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])(0[1-9]|[1-4]\\d|5[0-2]|99)(00[1-9]|0[1-9]\\d|[1-9]\\d\\d)\\d$");
        Matcher matcherCnp = patternCnp.matcher(tietCnp.getText().toString());

        if (tietCnp.getText().toString().isEmpty()) {
            tietCnp.setError("Introduceti CNP-ul!");
            tietCnp.requestFocus();
            return false;
        }

        if (!matcherCnp.matches()) {
            tietCnp.setError("CNP-ul este invalid!");
            tietCnp.requestFocus();
            return false;
        }

        Pattern patternTel = Pattern.compile("^407[2-8][0-9]{7}$");
        Matcher matcherTel = patternTel.matcher(tietNrTelefonPacient.getText().toString());

        if (tietNrTelefonPacient.getText().toString().isEmpty()) {
            tietNrTelefonPacient.setError("Introduceti numarul de telefon!");
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        if(!matcherTel.matches()){
            tietNrTelefonPacient.setError("Formatul acceptat este: 407xxxxxxxx!");
            tietNrTelefonPacient.requestFocus();
            return false;
        }

        if (tietDataNasterii.getText().toString().equals("dd/MM/yyyy")) {
            tietDataNasterii.setError("Alegeti data nasterii!");
            tietDataNasterii.requestFocus();
            return false;
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

        if (tietParolaPacient.getText().toString().isEmpty()) {
            tietParolaPacient.setError("Introduceti parola!");
            tietParolaPacient.requestFocus();
            return false;
        }

        if (tietParolaPacient.getText().toString().length() < 6) {
            tietParolaPacient.setError("Parola trebuie sa contina cel putin 6 caractere!");
            tietParolaPacient.requestFocus();
            return false;
        }

        if (!tietParolaPacient.getText().toString().equals(tietConfirmareParolaPacient.getText().toString())) {
            tietConfirmareParolaPacient.setError("Parola nu corespunde!");
            tietConfirmareParolaPacient.requestFocus();
            return false;
        }

        return true;
    }


    //TODO
    //sa pun o data maxima
    private void afiseazaCalendar() {
        tietDataNasterii.setError(null);
        final Calendar calendar = Calendar.getInstance();
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
//                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.BLUE));
        datePickerDialog.show();
    }
}