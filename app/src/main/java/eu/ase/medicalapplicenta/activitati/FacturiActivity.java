package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.FacturaAdaptor;
import eu.ase.medicalapplicenta.entitati.Factura;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class FacturiActivity extends AppCompatActivity implements FacturaAdaptor.OnBtnPlataClickListener {
//    public static final int REQUEST_CODE_PAYPAL = 500;
//    public static final String CLIENT_ID_PAYPAL = "ARKg_wK_PVka1gDkXGMprZ35nLNJfz5ZalPsHz0i5tm2365NVOUqsUxFqbJzPlv5ZsTMVw3LYWdTKS9S";
//    private static PayPalConfiguration configuration = new PayPalConfiguration()
//            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
//            .clientId(CLIENT_ID_PAYPAL);

    public static final String PROGRAMARI = "Programari";

    private final String idPacient = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Toolbar toolbar;

    private RecyclerView rwFacturi;
    private FacturaAdaptor adaptor;
    private List<Factura> facturi;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseService firebaseService = new FirebaseService(PROGRAMARI);

    private RelativeLayout ryNicioFactura;

    private ProgressBar progressBar;

    private AlertDialog dialogPlata;
    private AppCompatButton btnAdauga;

    private Factura factura;
    private AutoCompleteTextView actvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturi);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaRecyclerView();

        seteazaDialogPlatesteFactura();

        firebaseService.preiaDateDinFirebase(preiaFacturi());

        seteazaAdaptorStatus();

        actvStatus.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    List<Factura> facturiFiltered = facturi.stream()
                            .filter(f -> f.getStatus().startsWith(actvStatus.getText().toString().toLowerCase().substring(0, 4)))
                            .collect(Collectors.toList());
                    if (!facturiFiltered.isEmpty()) {
                        ryNicioFactura.setVisibility(View.GONE);
                        seteazaAdaptorFacturi(facturiFiltered);
                        rwFacturi.setVisibility(View.VISIBLE);
                    } else {
                        ryNicioFactura.setVisibility(View.VISIBLE);
                        rwFacturi.setVisibility(View.GONE);
                    }
                } else {
                    if (!facturi.isEmpty()) {
                        ryNicioFactura.setVisibility(View.GONE);
                        seteazaAdaptorFacturi(facturi);
                        rwFacturi.setVisibility(View.VISIBLE);
                    } else {
                        ryNicioFactura.setVisibility(View.VISIBLE);
                        rwFacturi.setVisibility(View.GONE);
                    }
                }
            }
        });

//        Intent intent = new Intent(this, PayPalService.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
//        startService(intent);
    }

    private void seteazaAdaptorStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_item, getResources().getStringArray(R.array.statusuri_facturi));
        actvStatus.setAdapter(adapter);
    }

    private void seteazaDialogPlatesteFactura() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Plată factură");

        View view = getLayoutInflater().inflate(R.layout.dialog_plateste_factura, null);
        TextInputEditText tietNumeTitular = view.findViewById(R.id.tietNumeTitular);
        TextInputEditText tietNumarCard = view.findViewById(R.id.tietNumarCard);
        TextInputEditText tietDataExpirarii = view.findViewById(R.id.tietDataExpirarii);
        TextInputEditText tietCvv = view.findViewById(R.id.tietCvv);
        btnAdauga = view.findViewById(R.id.btnAdauga);

        tietNumarCard.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String stringCurent = charSequence.toString();
                if ((stringCurent.length() == 4 && start == 3) || (stringCurent.length() == 9 && start == 8) ||
                        (stringCurent.length() == 14 && start == 13)) {
                    tietNumarCard.setText(stringCurent + " ");
                    tietNumarCard.setSelection(stringCurent.length() + 1);
                }
                //todo atunci cand sterg inainte de " " sa-mi stearga automat si " " si numarul de dianinte de spatiu
                /*else if ((stringCurent.length() == 4 && before == 3)) {
                    stringCurent = stringCurent.substring(0, 3);
                    tietNumarCard.setText(stringCurent);
                    tietNumarCard.setSelection(stringCurent.length());
                }*/
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        tietDataExpirarii.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                String stringCurent = charSequence.toString();
                if (stringCurent.length() == 2 && start == 1) {
                    tietDataExpirarii.setText(stringCurent + "/");
                    tietDataExpirarii.setSelection(stringCurent.length() + 1);
                } else if (stringCurent.length() == 2 && before == 1) {
                    stringCurent = stringCurent.substring(0, 1);
                    tietDataExpirarii.setText(stringCurent);
                    tietDataExpirarii.setSelection(stringCurent.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnAdauga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numeTitular = tietNumeTitular.getText().toString().trim();
                if (numeTitular.isEmpty()) {
                    tietNumeTitular.setError(getString(R.string.err_empty_nume_titular));
                    tietNumeTitular.requestFocus();
                    return;
                }

                if (tietNumarCard.getText().toString().isEmpty()) {
                    tietNumarCard.setError(getString(R.string.err_empty_numar_card));
                    tietNumarCard.requestFocus();
                    return;
                }

                if (tietNumarCard.getText().toString().length() != 19) {
                    tietNumarCard.setError(getString(R.string.err_not_valid_numar_card));
                    tietNumarCard.requestFocus();
                    return;
                }

                String dataExpirariiString = tietDataExpirarii.getText().toString();
                if (dataExpirariiString.isEmpty()) {
                    tietDataExpirarii.setError(getString(R.string.err_empty_data_expirarii));
                    tietDataExpirarii.requestFocus();
                    return;
                }

                Pattern pattern = Pattern.compile(getString(R.string.pattern_data_expirarii));
                Matcher matcher = pattern.matcher(tietDataExpirarii.getText().toString());
                if (!matcher.matches()) {
                    tietDataExpirarii.setError(getString(R.string.err_not_valid_data_expirarii));
                    tietDataExpirarii.requestFocus();
                    return;
                }

                String cvv = tietCvv.getText().toString();
                if (cvv.isEmpty()) {
                    tietCvv.setError(getString(R.string.err_empty_cvv));
                    tietCvv.requestFocus();
                    return;
                }

                if (cvv.length() < 3) {
                    tietCvv.setError(getString(R.string.err_not_valid_cvv));
                    tietCvv.requestFocus();
                    return;
                }
//                try {
//                    Date dataExpirarii = new SimpleDateFormat("MM/yy", Locale.US).parse(dataExpirariiString);
//                    Toast.makeText(getApplicationContext(), dataExpirarii.toString(), Toast.LENGTH_SHORT).show();
//                } catch (ParseException e) {
////                    e.printStackTrace();
//                    tietDataExpirarii.setError("input gresit");
//                    tietDataExpirarii.requestFocus();
//                    return;
//                }

                firebaseService.databaseReference
                        .child(factura.getIdFactura())
                        .child("factura")
                        .child("status")
                        .setValue(getString(R.string.achitata));


                tietNumeTitular.setText("");
                tietNumeTitular.clearFocus();

                tietNumarCard.setText("");
                tietNumarCard.clearFocus();

                tietDataExpirarii.setText("");
                tietDataExpirarii.clearFocus();

                tietCvv.setText("");
                tietCvv.clearFocus();

                dialogPlata.dismiss();

                Toast.makeText(getApplicationContext(), "Plata a fost efectuată cu succes!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view);
        dialogPlata = builder.create();
    }


    private void seteazaRecyclerView() {
        rwFacturi.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwFacturi.setLayoutManager(layoutManager);
        adaptor = new FacturaAdaptor(facturi, FacturiActivity.this, FacturiActivity.this);
        rwFacturi.setAdapter(adaptor);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        ryNicioFactura = findViewById(R.id.ryNicioFactura);
        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);

        facturi = new ArrayList<>();
        rwFacturi = findViewById(R.id.rwFacturi);

        actvStatus = findViewById(R.id.actvStatus);
    }

    private ValueEventListener preiaFacturi() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facturi = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdPacient().equals(idPacient) && p.getFactura() != null) {
                        facturi.add(p.getFactura());
                    }
                }

                if (!facturi.isEmpty()) {
                    Collections.reverse(facturi);
                    ryNicioFactura.setVisibility(View.GONE);
                    seteazaAdaptorFacturi(facturi);
                    rwFacturi.setVisibility(View.VISIBLE);
                } else {
                    rwFacturi.setVisibility(View.GONE);
                    ryNicioFactura.setVisibility(View.VISIBLE);
                }

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareFacturi", error.getMessage());
            }
        };
    }

    private void seteazaAdaptorFacturi(List<Factura> facturi) {
        adaptor = new FacturaAdaptor(facturi, FacturiActivity.this, FacturiActivity.this);
        rwFacturi.setAdapter(adaptor);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBtnPlataClick(int position) {
        factura = facturi.get(position);
        String text = getString(R.string.plateste) + " " + factura.getValoare() + " RON";
        btnAdauga.setText(text);
        dialogPlata.show();

//        double sumaDePlata = factura.getValoare();
//        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(sumaDePlata)), "EUR",
//                "Plătește factura", PayPalPayment.PAYMENT_INTENT_SALE);
//        Intent intent = new Intent(this, PaymentActivity.class);
//        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, configuration);
//        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
//        startActivityForResult(intent, REQUEST_CODE_PAYPAL);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_PAYPAL) {
//            if (resultCode == RESULT_OK) {
//                PaymentConfirmation confirmare = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
//                if (confirmare != null) {
//                    try {
//                        Log.i("PayPalJSON", confirmare.toJSONObject().toString(4));
//                        Log.i("PayPalPayment", confirmare.getPayment().toJSONObject().toString(4));
//                        Toast.makeText(getApplicationContext(), "Plata a fost efectuată cu succes!", Toast.LENGTH_SHORT).show();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//            } else if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(getApplicationContext(), "Plata a fost anulată!", Toast.LENGTH_SHORT).show();
//            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
//                Toast.makeText(getApplicationContext(), "Plata invalidă!", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }

//    @Override
//    protected void onDestroy() {
//        stopService(new Intent(this, PayPalService.class));
//        super.onDestroy();
//    }
}