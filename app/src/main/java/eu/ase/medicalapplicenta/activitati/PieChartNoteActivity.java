package eu.ase.medicalapplicenta.activitati;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.adaptori.MedicAdaptor;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class PieChartNoteActivity extends AppCompatActivity {
    private final FirebaseService firebaseService = new FirebaseService("Medici");
    private final int[] totalNote = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // vector de frecventa a notelor
    private final String idMedicConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private Toolbar toolbar;
    private PieChart pieChart;
    private List<Integer> noteFeedback = new ArrayList<>();
    private double medieNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_note);

        initializeazaAtribute();

        seteazaToolbar();

        firebaseService.preiaObiectDinFirebase(preiaMedic(), idMedicConectat);

    }

    private ValueEventListener preiaMedic() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Medic medic = snapshot.getValue(Medic.class);
                noteFeedback = medic.getNoteFeedback();
                medieNote = medic.getNotaFeedback();
                if (noteFeedback != null) {
                    for (Integer nota : noteFeedback) {
                        totalNote[nota - 1]++;
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
        ArrayList<PieEntry> note = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (totalNote[i] != 0) {
                note.add(new PieEntry(totalNote[i], String.valueOf(i + 1)));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(note, "Note acordate");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Medie: " + MedicAdaptor.NUMBER_FORMAT.format(medieNote));
//        pieChart.animate();
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });
        pieChart.invalidate();
        pieChart.animateY(1000, Easing.EaseInQuad);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);
        pieChart = findViewById(R.id.pieChart);
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}