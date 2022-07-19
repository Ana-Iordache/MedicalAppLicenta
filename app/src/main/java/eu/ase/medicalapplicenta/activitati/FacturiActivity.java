package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.FacturaAdaptor;
import eu.ase.medicalapplicenta.entitati.Factura;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class FacturiActivity extends AppCompatActivity implements FacturaAdaptor.OnBtnPlataClickListener,
        AdapterView.OnItemClickListener, View.OnClickListener {
//    public static final int REQUEST_CODE_PAYPAL = 500;
//    public static final String CLIENT_ID_PAYPAL = "ARKg_wK_PVka1gDkXGMprZ35nLNJfz5ZalPsHz0i5tm2365NVOUqsUxFqbJzPlv5ZsTMVw3LYWdTKS9S";
//    private static PayPalConfiguration configuration = new PayPalConfiguration()
//            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
//            .clientId(CLIENT_ID_PAYPAL);

    public static final String PROGRAMARI = "Programari";
    private static final String SECRET_KEY = "sk_test_51LFdevC8SoTmNAWDqHBO8bXpGotQFCTuAj2Qb7rkxXqx6sR1k3jAWJvFjB6fxSzpuUMHyaNa42Qrpy2G9agWDsmO00X128EyOl";
    private static final String PUBLISHABLE_KEY = "pk_test_51LFdevC8SoTmNAWDHX6kirGW8OkSrYbnAqxETmLnl3PnTOWsUlohJx8nc9nyCBOr4T43bUoaNdUDBpuEwCBABw9R00IAOICftH";
    private final String idPacient = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public PaymentSheet paymentSheet;
    private String customerId;
    private String ephemeralKey;
    private String clientSecret;

    private Toolbar toolbar;

    private RecyclerView rwFacturi;
    private FacturaAdaptor adaptor;
    private List<Factura> facturi;
    private RecyclerView.LayoutManager layoutManager;

    private FirebaseService firebaseService = new FirebaseService(PROGRAMARI);

    private RelativeLayout ryNicioFactura;

    private ProgressBar progressBar;

    private Factura factura;
    private AutoCompleteTextView actvStatus;

    private FloatingActionButton fabBarChart;

    private List<Factura> facturiFiltered = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facturi);

        initializeazaAtribute();

        seteazaToolbar();

        seteazaRecyclerView();

        firebaseService.preiaDateDinFirebase(preiaFacturi());

        seteazaAdaptorStatus();

        actvStatus.setOnItemClickListener(this);
        fabBarChart.setOnClickListener(this);

        PaymentConfiguration.init(this, PUBLISHABLE_KEY);
        paymentSheet = new PaymentSheet(this, this::onPaymentResult);
//        preiaCustomerID();

    }

    private void initializeazaAtribute() {
        ryNicioFactura = findViewById(R.id.ryNicioFactura);
        progressBar = findViewById(R.id.progressBar);

        toolbar = findViewById(R.id.toolbar);

        facturi = new ArrayList<>();
        rwFacturi = findViewById(R.id.rwFacturi);

        actvStatus = findViewById(R.id.actvStatus);

        fabBarChart = findViewById(R.id.fabBarChart);
    }

    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            firebaseService.databaseReference
                    .child(factura.getIdFactura())
                    .child("factura")
                    .child("status")
                    .setValue(getString(R.string.achitata));
            Toast.makeText(getApplicationContext(), "Plata a fost efectuată cu succes!", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Toast.makeText(getApplicationContext(), "Plata a eșuat!", Toast.LENGTH_SHORT).show();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Toast.makeText(getApplicationContext(), "Plata a fost anulată!", Toast.LENGTH_SHORT).show();
        }
    }

    private void preiaCustomerID() {
        // creare request de tip POST pentru crearea clientilor
        StringRequest request = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    // preluarea raspunsului de la server
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            customerId = object.getString("id");
                            preiaEphemeralKey(customerId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) { // setarea elementelor din header transmise prin request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY);
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(FacturiActivity.this);
        requestQueue.add(request);
    }

    private void preiaEphemeralKey(String customerId) {
        // creare request de tip POST pentru crearea cheii necesare
        StringRequest request = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    // preluarea raspunsului de la server
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            ephemeralKey = object.getString("id");
                            preiaClientSecret(customerId, ephemeralKey);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) { // setarea elementelor din header transmise prin request
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY);
                headers.put("Stripe-Version", "2020-08-27");
                return headers;
            }

            // setarea parametrilor transmisi prin request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(FacturiActivity.this);
        requestQueue.add(request);
    }

    private void preiaClientSecret(String customerId, String ephemeralKey) {
        StringRequest request = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    // preluarea raspunsului de la server
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            clientSecret = object.getString("client_secret");
                            afiseazaFormularDateCard();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + SECRET_KEY);
                return headers;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerId);
                String[] suma = String.valueOf(factura.getValoare()).split("\\.");
                if (suma[1].length() == 1) {
                    suma[1] += "0";
                }
                params.put("amount", suma[0] + suma[1]);
                params.put("currency", "ron");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(FacturiActivity.this);
        requestQueue.add(request);
    }

    private void afiseazaFormularDateCard() {
        paymentSheet.presentWithPaymentIntent(clientSecret,
                new PaymentSheet.Configuration("Clinica Medicala",
                        new PaymentSheet.CustomerConfiguration(customerId, ephemeralKey))
        );
    }

    private void seteazaAdaptorStatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_item, getResources().getStringArray(R.array.statusuri_facturi));
        actvStatus.setAdapter(adapter);
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


    private ValueEventListener preiaFacturi() {
        loading(true);
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
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

                fabBarChart.setEnabled(facturi.stream()
                        .anyMatch(f -> f.getStatus().equals(getString(R.string.achitata))));

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
        if (facturiFiltered.isEmpty()) {
            factura = facturi.get(position);
        } else {
            factura = facturiFiltered.get(position);
        }
//        String text = getString(R.string.plateste) + " " + factura.getValoare() + " RON";
//        btnAdauga.setText(text);
//        dialogPlata.show();

//        Toast.makeText(getApplicationContext(), String.valueOf(factura.getValoare()), Toast.LENGTH_SHORT).show();
        preiaCustomerID();
//        paymentFlow();
        actvStatus.setText(R.string.toate);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != 0) {
            facturiFiltered = facturi.stream()
                    .filter(f -> f.getStatus().startsWith(actvStatus.getText().toString().toLowerCase().substring(0, 4)))
                    .collect(Collectors.toList());
            if (!facturiFiltered.isEmpty()) {
                ryNicioFactura.setVisibility(View.GONE);
                rwFacturi.setVisibility(View.VISIBLE);
                seteazaAdaptorFacturi(facturiFiltered);
            } else {
                ryNicioFactura.setVisibility(View.VISIBLE);
                rwFacturi.setVisibility(View.GONE);
            }
        } else {
            facturiFiltered.clear();
            if (!facturi.isEmpty()) {
                ryNicioFactura.setVisibility(View.GONE);
                rwFacturi.setVisibility(View.VISIBLE);
                seteazaAdaptorFacturi(facturi);
            } else {
                ryNicioFactura.setVisibility(View.VISIBLE);
                rwFacturi.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onClick(View view) {
        startActivity(new Intent(getApplicationContext(), PlatiLunareActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        seteazaAdaptorStatus();
    }
}