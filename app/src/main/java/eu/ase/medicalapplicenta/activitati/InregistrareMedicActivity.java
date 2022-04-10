package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private TextInputEditText tietProgramLucru;

    private AutoCompleteTextView actvGradeProfesionale;
    private AutoCompleteTextView actvSpecialitati;

    private AppCompatButton btnInregistrareMedic;

    private List<Specialitate> specialitati;

    private FirebaseService firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
    private FirebaseService firebaseServiceMedici = new FirebaseService(MEDICI);
    private FirebaseAuth mAuth;

    private Uri uri;

    private AlertDialog dialogProgram;
    private TextInputEditText tietOraInceput;
    private TextInputEditText tietOraSfarsit;
    private AppCompatButton btnAdauga;
    private AppCompatButton btnRenunta;
    private AutoCompleteTextView actvZileDeLucru;

    private List<ZiDeLucru> program;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inregistrare_medic);

        initializeazaAtribute();

        ciwPozaProfilMedic.setOnClickListener(this);

        seteazaAdaptorGradeProfesionale();
        seteazaDialogProgramLucru();

        firebaseServiceSpecialitati.preiaDateDinFirebase(preiaSpecialitati());

        btnInregistrareMedic.setOnClickListener(this);
        actvSpecialitati.setOnClickListener(this);
        tietProgramLucru.setOnClickListener(this);
        actvZileDeLucru.setOnClickListener(this);
    }

    private void seteazaDialogProgramLucru() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Adaugă zi de lucru");

        View view = getLayoutInflater().inflate(R.layout.dialog_program_lucru, null);
        actvZileDeLucru = view.findViewById(R.id.actvZileDeLucru);
        tietOraInceput = view.findViewById(R.id.tietOraInceput);
        tietOraSfarsit = view.findViewById(R.id.tietOraSfarsit);
        btnAdauga = view.findViewById(R.id.btnAdauga);
        btnRenunta = view.findViewById(R.id.btnRenunta);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.zile_saptamana));
        actvZileDeLucru.setAdapter(adapter);

        actvZileDeLucru.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btnAdauga.setEnabled(true);
            }
        });

        tietOraInceput.setOnClickListener(this);
        tietOraSfarsit.setOnClickListener(this);
        btnAdauga.setOnClickListener(this);
        btnRenunta.setOnClickListener(this);

        builder.setView(view);
        dialogProgram = builder.create();
    }


    private void initializeazaAtribute() {
        ciwPozaProfilMedic = findViewById(R.id.ciwPozaProfilMedic);
        tietNumeMedic = findViewById(R.id.tietNumeMedic);
        tietPrenumeMedic = findViewById(R.id.tietPrenumeMedic);
        tietNrTelefonMedic = findViewById(R.id.tietNrTelefonMedic);
        tietEmailMedic = findViewById(R.id.tietEmailMedic);
        tietParolaMedic = findViewById(R.id.tietParolaMedic);
        tietConfirmareParolaMedic = findViewById(R.id.tietConfirmareParolaMedic);
        tietProgramLucru = findViewById(R.id.tietProgramLucru);

        actvGradeProfesionale = findViewById(R.id.actvGradeProfesionale);
        actvSpecialitati = findViewById(R.id.actvSpecialitati);

        specialitati = new ArrayList<>();
        program = new ArrayList<>();

//        spnSpecialitate = findViewById(R.id.spnSpecialitate);

        btnInregistrareMedic = findViewById(R.id.btnInregistrareMedic);

        mAuth = FirebaseAuth.getInstance();
    }

    private void seteazaAdaptorGradeProfesionale() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.grade_profesionale));
        actvGradeProfesionale.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
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
            case R.id.actvSpecialitati:
                actvSpecialitati.setError(null);
                break;
            case R.id.actvZileDeLucru:
                actvZileDeLucru.setError(null);
                return;
            case R.id.tietProgramLucru:
                tietProgramLucru.setError(null);
                dialogProgram.show();
                break;
            case R.id.tietOraInceput:
                tietOraInceput.setError(null);
                afiseazaTimePicker(tietOraInceput, getString(R.string.ora_de_inceput));
                break;
            case R.id.tietOraSfarsit:
                tietOraSfarsit.setError(null);
                afiseazaTimePicker(tietOraSfarsit, getString(R.string.ora_de_sfarsit));
                break;
            case R.id.btnAdauga:
//                if (actvZileDeLucru.getText().toString().isEmpty()) {
//                    actvZileDeLucru.setError("Selectați ziua!");
//                    actvZileDeLucru.requestFocus();
//                    return;
//                }

                if(program.stream().anyMatch(zi->zi.getZi().equals(actvZileDeLucru.getText().toString()))){
                    actvZileDeLucru.setError("Ziua de " + actvZileDeLucru.getText().toString() + " există deja în program!");
                    actvZileDeLucru.requestFocus();
                    return;
                }
//                for (ZiDeLucru zi : program) {
//                    if (zi.getZi().equals(actvZileDeLucru.getText().toString())) {
//                        actvZileDeLucru.setError("Ziua de " + zi.getZi() + " există deja în program!");
//                        actvZileDeLucru.requestFocus();
//                        return;
//                    }
//                }

//                if (tietOraInceput.getText().toString().isEmpty()) {
//                    //todo
//                    //ca sa apara si textul trb sa-i pun din xml focusable pe true
//                    //dar asa imi apare tastatura la primuk click pe tiet uof
//                    tietOraInceput.setError("Selectați ora de început!");
//                    tietOraInceput.requestFocus();
//                    return;
//                }

                if (tietOraSfarsit.getText().toString().isEmpty()) {
                    tietOraSfarsit.setError("Selectați ora de sfârșit!");
                    tietOraSfarsit.requestFocus();
                    return;
                }

                ZiDeLucru ziDeLucru = new ZiDeLucru(actvZileDeLucru.getText().toString(), tietOraInceput.getText().toString(),
                        tietOraSfarsit.getText().toString());

                program.add(ziDeLucru);


                StringBuilder programString = new StringBuilder();
                for (int i = 0; i < program.size(); i++) {
                    programString.append(program.get(i).toString());
                    if (i != program.size() - 1) {
                        programString.append("\n");
                    }
                }
                tietProgramLucru.setText(programString.toString());
                dialogProgram.dismiss();
                golesteInputuri();
                break;
            case R.id.btnRenunta:
                dialogProgram.dismiss();
                golesteInputuri();
                break;
        }
    }

    private void golesteInputuri() {
        actvZileDeLucru.setText("");
        actvZileDeLucru.clearFocus();
        actvZileDeLucru.setError(null);
        tietOraInceput.setText("");
        tietOraSfarsit.setText("");
    }


    private void afiseazaTimePicker(TextInputEditText tiet, String titlu) {
        Calendar calendar = Calendar.getInstance();
        Date dataDefault = null;
        try {
            dataDefault = new SimpleDateFormat("HH:mm", Locale.US).parse("08:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dataDefault);
        int ora = calendar.get(Calendar.HOUR);
        int minut = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(InregistrareMedicActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int ora, int minut) {
                String timp = convertInt(ora) + ":" + convertInt(minut);
                tiet.setText(timp);
            }
        }, ora, minut, true);
        timePickerDialog.setTitle(titlu);
        timePickerDialog.show();
    }

    private String convertInt(int i) {
        if (i >= 10) {
            return String.valueOf(i);
        }
        return "0" + i;
    }

//    private void afiseazaDialog() {
//        String[] zile = getResources().getStringArray(R.array.zile_saptamana);
//        boolean[] zileBifate = new boolean[zile.length];
//        List<Integer> pozitii = new ArrayList<>();
//        AlertDialog.Builder dialog = new AlertDialog.Builder(InregistrareMedicActivity.this);
//        dialog.setTitle("Program de lucru")
//                .setMultiChoiceItems(zile, zileBifate, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
//                        if (b) {
//                            pozitii.add(i);
//                            Calendar calendar = Calendar.getInstance();
//                            int ora = calendar.get(Calendar.HOUR);
//                            int minut = calendar.get(Calendar.MINUTE);
//
//                            TimePickerDialog oraInceput = new TimePickerDialog(InregistrareMedicActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                                @Override
//                                public void onTimeSet(TimePicker timePicker, int ora, int minut) {
//                                    String timp = ora + ":" + minut;
//                                    Toast.makeText(getApplicationContext(), timp, Toast.LENGTH_SHORT).show();
//                                }
//                            }, ora, minut, true);
//                            oraInceput.setTitle("Ora inceput");
//                            oraInceput.show();
//
//                            TimePickerDialog oraSfarsit = new TimePickerDialog(InregistrareMedicActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                                @Override
//                                public void onTimeSet(TimePicker timePicker, int ora, int minut) {
//                                    String timp = ora + ":" + minut;
//                                    Toast.makeText(getApplicationContext(), timp, Toast.LENGTH_SHORT).show();
//                                }
//                            }, ora, minut, true);
//                            oraSfarsit.setTitle("Ora sfarsit");
//                            oraSfarsit.show();
//
//                        } else {
//                            pozitii.remove(i);
//                        }
//                    }
//                })
//                .setNegativeButton("Anulează", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                })
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        StringBuilder item = new StringBuilder();
//                        for (int j = 0; j < pozitii.size(); j++) {
//                            item.append(zile[pozitii.get(j)]);
//                            if (j != pozitii.size() - 1) {
//                                item.append("\n");
//                            }
//                        }
//                        tietProgramLucru.setText(item.toString());
//                    }
//                })
//                .create()
//                .show();
//    }

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
        if (inputValid()) {
            String nume = tietNumeMedic.getText().toString();
            String prenume = tietPrenumeMedic.getText().toString();

            long nrTelefon;
            if (!tietNrTelefonMedic.getText().toString().isEmpty())
                nrTelefon = Long.parseLong(tietNrTelefonMedic.getText().toString());
            else nrTelefon = 0;

            String gradProfesional = actvGradeProfesionale.getText().toString();
//            if (spnGradProfesional.getSelectedItemPosition() == 0) {
//                gradProfesional = "Nespecificat";
//            } else gradProfesional = spnGradProfesional.getSelectedItem().toString();

            String specialitate = actvSpecialitati.getText().toString();
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
                                String idMedic = mAuth.getCurrentUser().getUid();

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
            tietNumeMedic.setError(getString(R.string.err_empty_nume));
            tietNumeMedic.requestFocus();
            return false;
        }

        if (tietPrenumeMedic.getText().toString().isEmpty()) {
            tietPrenumeMedic.setError(getString(R.string.err_empty_prenume));
            tietPrenumeMedic.requestFocus();
            return false;
        }

        Pattern pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
        Matcher matcher = pattern.matcher(tietNrTelefonMedic.getText().toString());
        if (!tietNrTelefonMedic.getText().toString().isEmpty() && !matcher.matches()) {
            tietNrTelefonMedic.setError(getString(R.string.err_not_valid_telefon));
            tietNrTelefonMedic.requestFocus();
            return false;
        }

        if (actvSpecialitati.getText().toString().isEmpty()) {
            //android:focusable="true"
            //android:focusableInTouchMode="true"
            //in xml ca sa apara si textul (pt text view)
            actvSpecialitati.setError(getString(R.string.err_empty_specialitate));
            actvSpecialitati.requestFocus();
            return false;
        }

        if (tietProgramLucru.getText().toString().isEmpty()) {
            tietProgramLucru.setError(getString(R.string.err_empty_program_lucru));
            tietProgramLucru.requestFocus();
            return false;
        }

        if (tietEmailMedic.getText().toString().isEmpty()) {
            tietEmailMedic.setError(getString(R.string.err_empty_email));
            tietEmailMedic.requestFocus();
            return false;
        }

        pattern = Pattern.compile(getString(R.string.pattern_email_medic));
        matcher = pattern.matcher(tietEmailMedic.getText().toString());
        if (!matcher.matches()) {
            tietEmailMedic.setError(getString(R.string.err_not_valid_email_doctor));
            tietEmailMedic.requestFocus();
            return false;
        }

        if (tietParolaMedic.getText().toString().isEmpty()) {
            tietParolaMedic.setError(getString(R.string.err_empty_parola));
            tietParolaMedic.requestFocus();
            return false;
        }

        if (tietParolaMedic.getText().toString().length() < 6) {
            tietParolaMedic.setError(getString(R.string.err_not_valid_parola));
            tietParolaMedic.requestFocus();
            return false;
        }

        if (tietConfirmareParolaMedic.getText().toString().isEmpty()) {
            tietConfirmareParolaMedic.setError(getString(R.string.err_empty_confirmare_parola));
            tietConfirmareParolaMedic.requestFocus();
            return false;
        }

        if (!tietParolaMedic.getText().toString().equals(tietConfirmareParolaMedic.getText().toString())) {
            tietConfirmareParolaMedic.setError(getString(R.string.err_not_valid_confirmare_parola));
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
//                denumiriSpecialitati.add("Selectati specialitatea");
                for (Specialitate s : specialitati) {
                    denumiriSpecialitati.add(s.getDenumire());
                }
                ArrayAdapter<String> adapterSpec = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.dropdown_item, denumiriSpecialitati);
                actvSpecialitati.setAdapter(adapterSpec);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
            }
        };
    }
}