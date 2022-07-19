package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Factura;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class IncasariActivity extends AppCompatActivity implements View.OnClickListener {
    private final FirebaseService firebaseService = new FirebaseService("Programari");
    private final HSSFWorkbook workbook = new HSSFWorkbook();
    private final String[] lunileAnului = new String[]{"Ianuarie", "Februarie", "Martie", "Aprilie",
            "Mai", "Iunie", "Iulie", "August",
            "Septembrie", "Octombie", "Noiembrie", "Decembrie"};
    private Toolbar toolbar;
    private BarChart barChart;
    private List<Factura> facturiPlatite = new ArrayList<>();
    private String idMedicConectat;
    private double[] incasariPeLuna = new double[12];
    private List<String> dateProgramari = new ArrayList<>();
    private AutoCompleteTextView actvAni;
    private int anSelectat;
    private AppCompatButton btnExportaDate;
    private AppCompatButton btnIncasariPacient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incasari);

        initializeazaAtribute();

        seteazaToolbar();

        firebaseService.preiaDateDinFirebase(preiaProgramari());

        seteazaAdaptorAni();

        actvAni.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                anSelectat = Integer.parseInt(actvAni.getText().toString());
                firebaseService.preiaDateDinFirebase(preiaProgramari());
            }
        });

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

        btnExportaDate.setOnClickListener(this);
        btnIncasariPacient.setOnClickListener(this);

        deseneazaGrafic();
    }

    private void seteazaAdaptorAni() {
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(),
                R.layout.dropdown_item, getResources().getStringArray(R.array.ani));
        actvAni.setAdapter(adapter);
        anSelectat = Integer.parseInt(actvAni.getText().toString());
    }

    private ValueEventListener preiaProgramari() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                facturiPlatite.clear();
                dateProgramari.clear();
                incasariPeLuna = new double[12];
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare programare = dataSnapshot.getValue(Programare.class);
                    Factura factura = programare.getFactura();
                    if (programare.getIdMedic().equals(idMedicConectat) && factura.getStatus().equals(getString(R.string.achitata))) {
                        facturiPlatite.add(factura);
                        dateProgramari.add(programare.getData());
                    }
                }

                if (facturiPlatite.isEmpty()) {
                    btnExportaDate.setEnabled(false);
                    btnIncasariPacient.setEnabled(false);
                    btnIncasariPacient.setTextColor(getResources().getColor(R.color.custom_light_blue));
                } else {
                    btnExportaDate.setEnabled(true);
                    btnIncasariPacient.setEnabled(true);
                    btnIncasariPacient.setTextColor(getResources().getColor(R.color.custom_blue));
                }

                for (int i = 0; i < facturiPlatite.size(); i++) {
                    int luna = Integer.parseInt(dateProgramari.get(i).split("/")[1]);
                    int an = Integer.parseInt(dateProgramari.get(i).split("/")[2]);
                    if (an == anSelectat) {
                        incasariPeLuna[luna - 1] += facturiPlatite.get(i).getValoare();
                    }
                }

                deseneazaGrafic();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void deseneazaGrafic() {
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < incasariPeLuna.length; i++) {
            barEntries.add(new BarEntry(i, (float) incasariPeLuna[i]));
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Total încasări pe lună");
        barDataSet.setColors(getResources().getColor(R.color.custom_blue));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(14f);

        Description description = new Description();
        description.setText("Lunile anului");
        barChart.setDescription(description);

        BarData barData = new BarData(barDataSet);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setText("");
        barChart.animateY(1000);

        // setarea lunilor anului ca index pe axa x
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(lunileAnului));
        // setarea pozitiei lunilor anului
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(lunileAnului.length);
        xAxis.setLabelRotationAngle(300);
        barChart.invalidate();
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        barChart = findViewById(R.id.barChart);
        idMedicConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
        actvAni = findViewById(R.id.actvAni);
        btnExportaDate = findViewById(R.id.btnExportaDate);
        btnIncasariPacient = findViewById(R.id.btnIncasariPacient);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnExportaDate:
                exportaDateInExcel();
                break;
            case R.id.btnIncasariPacient:
                startActivity(new Intent(getApplicationContext(), IncasariPacientiActivity.class));
                break;
        }
    }

    private void exportaDateInExcel() {
        String denumireSheet = "Incasari" + anSelectat;
        HSSFSheet sheet = workbook.getSheet(denumireSheet);
        if (sheet == null) {
            sheet = workbook.createSheet(denumireSheet);
        }
        HSSFRow row = sheet.createRow(0);

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("An");

        cell = row.createCell(1);
        cell.setCellValue(anSelectat);

        for (int i = 0; i < incasariPeLuna.length; i++) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(lunileAnului[i]);

            cell = row.createCell(1);
            cell.setCellValue(incasariPeLuna[i]);
        }

        String denumireFisier = "/Incasari_clinica_medicala.xls";
        File filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + denumireFisier);

        try {
            if (!filePath.exists()) {
                filePath.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            workbook.write(fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();
            Toast.makeText(getApplicationContext(), "Date exportate: /Spatiu de stocare intern/Documents" + denumireFisier,
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}