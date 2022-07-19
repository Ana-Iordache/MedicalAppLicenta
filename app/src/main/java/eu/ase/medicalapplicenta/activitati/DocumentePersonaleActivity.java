package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import eu.ase.medicalapplicenta.R;

public class DocumentePersonaleActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;
    public static final int REQUEST_CODE_INTENT = 200;
    public static final String DOCUMENTE_PACIENTI = "documente pacienti";
    public static final String CARD_DE_SANATATE = "card_de_sanatate";
    public static final String BULETIN = "buletin";
    public static final String EXTENSIE_PDF = ".pdf";
    private final String idPacientConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private final StorageReference referintaBuletin = FirebaseStorage.getInstance().getReference().child(DOCUMENTE_PACIENTI)
            .child(idPacientConectat).child(BULETIN);
    private final StorageReference referintaCardSanatate = FirebaseStorage.getInstance().getReference().child(DOCUMENTE_PACIENTI)
            .child(idPacientConectat).child(CARD_DE_SANATATE);
    private final String denumirePdfCardSanatate = CARD_DE_SANATATE + EXTENSIE_PDF;
    private final String denumirePdfBuletin = BULETIN + EXTENSIE_PDF;
    private Toolbar toolbar;
    private TextView tvPdfCardSanatate;
    private ImageView ivAtaseazaCardSanatate;
    private ImageView ivIncarcaCardSanatate;
    private ImageView ivDescarcaCardSanatate;
    private TextView tvPdfBuletin;
    private ImageView ivAtaseazaBuletin;
    private ImageView ivIncarcaBuletin;
    private ImageView ivDescarcaBuletin;
    private Uri uriPDF;
    private String tipDocument;
    private ProgressDialog progressDialog;
    private Uri uriCardSanatate;
    private Uri uriBuletin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documente_personale);

        initializeazaAtribute();

        seteazaToolbar();

        preiaUrlDocumente();

        ivAtaseazaCardSanatate.setOnClickListener(this);
        ivIncarcaCardSanatate.setOnClickListener(this);
        ivDescarcaCardSanatate.setOnClickListener(this);
        ivAtaseazaBuletin.setOnClickListener(this);
        ivIncarcaBuletin.setOnClickListener(this);
        ivDescarcaBuletin.setOnClickListener(this);
    }

    private void preiaUrlDocumente() {
        referintaBuletin.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                tvPdfBuletin.setText(denumirePdfBuletin);
                uriBuletin = uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tvPdfBuletin.setText(getString(R.string.niciun_document_atasat));
            }
        });

        referintaCardSanatate.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                tvPdfCardSanatate.setText(denumirePdfCardSanatate);
                uriCardSanatate = uri;
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                tvPdfCardSanatate.setText(getString(R.string.niciun_document_atasat));
            }
        });
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        tvPdfCardSanatate = findViewById(R.id.tvPdfCardSanatate);
        ivAtaseazaCardSanatate = findViewById(R.id.ivAtaseazaCardSanatate);
        ivIncarcaCardSanatate = findViewById(R.id.ivIncarcaCardSanatate);
        ivDescarcaCardSanatate = findViewById(R.id.ivDescarcaCardSanatate);
        tvPdfBuletin = findViewById(R.id.tvPdfBuletin);
        ivAtaseazaBuletin = findViewById(R.id.ivAtaseazaBuletin);
        ivIncarcaBuletin = findViewById(R.id.ivIncarcaBuletin);
        ivDescarcaBuletin = findViewById(R.id.ivDescarcaBuletin);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivAtaseazaCardSanatate:
                tipDocument = CARD_DE_SANATATE;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ataseazaPdf();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                }
                break;
            case R.id.ivIncarcaCardSanatate:
            case R.id.ivIncarcaBuletin:
                if (uriPDF != null) {
                    incarcaPdf(uriPDF);
                }
                break;
            case R.id.ivDescarcaCardSanatate:
                if (tvPdfCardSanatate.getText().toString().equals(getString(R.string.niciun_document_atasat))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.niciun_document_atasat), Toast.LENGTH_SHORT).show();
                } else {
                    descarcaDocument(uriCardSanatate, Environment.DIRECTORY_DOWNLOADS, denumirePdfCardSanatate);
                }
                break;
            case R.id.ivAtaseazaBuletin:
                tipDocument = BULETIN;
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    ataseazaPdf();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                }
                break;
            case R.id.ivDescarcaBuletin:
                if (tvPdfBuletin.getText().toString().equals(getString(R.string.niciun_document_atasat))) {
                    Toast.makeText(getApplicationContext(), getString(R.string.niciun_document_atasat), Toast.LENGTH_SHORT).show();
                } else {
                    descarcaDocument(uriBuletin, Environment.DIRECTORY_DOWNLOADS, denumirePdfBuletin);
                }
                break;
        }
    }

    private void descarcaDocument(Uri uri, String directorDestinatie, String denumireFisier) {
        DownloadManager downloadManager = (DownloadManager) getApplicationContext()
                .getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        Toast.makeText(getApplicationContext(), R.string.descarcare_document,
                Toast.LENGTH_SHORT).show();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(getApplicationContext(),
                directorDestinatie, denumireFisier);
        downloadManager.enqueue(request);
    }

    private void incarcaPdf(Uri uriPDF) {
        progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
        progressDialog.setMessage(getString(R.string.pd_incarcare_document));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child(DOCUMENTE_PACIENTI).child(idPacientConectat).child(tipDocument).putFile(uriPDF)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getApplicationContext(), R.string.document_incarcat, Toast.LENGTH_SHORT).show();
                        if (tipDocument.equals(CARD_DE_SANATATE)) {
                            tvPdfCardSanatate.setText(denumirePdfCardSanatate);
                            ivDescarcaCardSanatate.setVisibility(View.VISIBLE);
                            ivIncarcaCardSanatate.setVisibility(View.GONE);

                        } else {
                            tvPdfBuletin.setText(denumirePdfBuletin);
                            ivDescarcaBuletin.setVisibility(View.VISIBLE);
                            ivIncarcaBuletin.setVisibility(View.GONE);
                        }
                        preiaUrlDocumente();
                        progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Documentul nu a putut fi incarcat!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
    }

    private void ataseazaPdf() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTENT && resultCode == RESULT_OK && data != null) {
            uriPDF = data.getData();
            if (tipDocument.equals(CARD_DE_SANATATE)) {
                tvPdfCardSanatate.setText(uriPDF.getLastPathSegment());
                ivDescarcaCardSanatate.setVisibility(View.GONE);
                ivIncarcaCardSanatate.setVisibility(View.VISIBLE);
            } else {
                tvPdfBuletin.setText(uriPDF.getLastPathSegment());
                ivDescarcaBuletin.setVisibility(View.GONE);
                ivIncarcaBuletin.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ataseazaPdf();
        }
    }
}