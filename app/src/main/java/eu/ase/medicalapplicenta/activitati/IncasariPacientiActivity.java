package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

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
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class IncasariPacientiActivity extends AppCompatActivity implements View.OnClickListener {
    private final FirebaseService firebaseServiceProgramari = new FirebaseService("Programari");
    private final FirebaseService firebaseServicePacienti = new FirebaseService("Pacienti");
    private final HSSFWorkbook workbook = new HSSFWorkbook();
    private Toolbar toolbar;
    private HorizontalBarChart horizontalBarChart;
    private List<Factura> facturiPlatite = new ArrayList<>();
    private String idMedicConectat;
    private List<String> numePacienti;
    private HashMap<String, String> mapPacienti;
    private HashMap<String, Double> valoareFacturi;
    private AppCompatButton btnExportaDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incasari_pacienti);

        initializeazaAtribute();

        seteazaToolbar();

        btnExportaDate.setOnClickListener(this);

        firebaseServiceProgramari.preiaDateDinFirebase(preiaIduriPacienti());
    }

    private ValueEventListener preiaIduriPacienti() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mapPacienti = new HashMap<>();
                valoareFacturi = new HashMap<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare p = dataSnapshot.getValue(Programare.class);
                    if (p.getIdMedic().equals(idMedicConectat)) {
                        mapPacienti.put(p.getIdPacient(), "");
                        valoareFacturi.put(p.getIdPacient(), 0.0);
                    }
                }

                firebaseServicePacienti.preiaDateDinFirebase(preiaPacienti());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener preiaPacienti() {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                numePacienti = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Pacient p = dataSnapshot.getValue(Pacient.class);
                    if (mapPacienti.containsKey(p.getIdPacient()))
                        mapPacienti.replace(p.getIdPacient(), p.getPrenume() + " " + p.getNume());
//                        numePacienti.add(p.getPrenume() + " " + p.getNume().charAt(0));
                }

                firebaseServiceProgramari.preiaDateDinFirebase(preiaProgramari());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener preiaProgramari() {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare programare = dataSnapshot.getValue(Programare.class);
                    Factura factura = programare.getFactura();
                    for (String idPacient : valoareFacturi.keySet()) {
                        if (programare.getIdMedic().equals(idMedicConectat) && programare.getIdPacient().equals(idPacient)
                                && factura.getStatus().equals(getString(R.string.achitata))) {
                            valoareFacturi.replace(idPacient, valoareFacturi.get(idPacient) + factura.getValoare());
                            break;
                        }
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
        int i = 0;
        for (String idPacient : valoareFacturi.keySet()) {
            barEntries.add(new BarEntry(i, (float) (double) valoareFacturi.get(idPacient)));
            i++;
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Total încasări per pacient");
        barDataSet.setColors(getResources().getColor(R.color.custom_blue));
        barDataSet.setValueTextColor(Color.BLACK);
        barDataSet.setValueTextSize(14f);

        Description description = new Description();
        description.setText("Pacienti");
        horizontalBarChart.setDescription(description);

        BarData barData = new BarData(barDataSet);

        horizontalBarChart.setFitBars(true);
        horizontalBarChart.setData(barData);
        horizontalBarChart.getDescription().setText("");
        horizontalBarChart.animateY(1000);

        List<String> numePacienti = new ArrayList<>();
        for (String idPacient : mapPacienti.keySet()) {
            String nume = mapPacienti.get(idPacient);
            String[] numePrenume = nume.split(" ");

            if (numePrenume[0].contains("-")) {
                numePacienti.add(numePrenume[0].split("-")[0] +
                        " " + numePrenume[numePrenume.length - 1].charAt(0));
            } else if(numePrenume[numePrenume.length - 1].length() != 0) {
                numePacienti.add(numePrenume[0] + " "
                        + numePrenume[numePrenume.length - 1].charAt(0));
            }
        }

        XAxis xAxis = horizontalBarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(numePacienti));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(numePacienti.size());
        xAxis.setLabelRotationAngle(270);
        horizontalBarChart.invalidate();
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        horizontalBarChart = findViewById(R.id.horizontalBarChart);
        idMedicConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
        btnExportaDate = findViewById(R.id.btnExportaDate);
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
    public void onClick(View view) {
        String denumireSheet = "Incasari_totale_pacienti";
        HSSFSheet sheet = workbook.getSheet(denumireSheet);
        if (sheet == null) {
            sheet = workbook.createSheet(denumireSheet);
        }

        HSSFRow row = sheet.createRow(0);

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Nume pacient");

        cell = row.createCell(1);
        cell.setCellValue("Suma");

        int i = 0;
        for (String idPacient : valoareFacturi.keySet()) {
            row = sheet.createRow(i + 1);

            cell = row.createCell(0);
            cell.setCellValue(mapPacienti.get(idPacient));

            cell = row.createCell(1);
            cell.setCellValue(valoareFacturi.get(idPacient));

            i++;
        }

        String denumireFisier = "/Incasari_totale_pacienti.xls";
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