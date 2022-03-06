package eu.ase.medicalapplicenta.activitati;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;

//todo
public class InformatiiMedicActivity extends AppCompatActivity {
    private Medic medic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informatii_medic);

        final Intent intent = getIntent();
        medic = (Medic) intent.getSerializableExtra(ListaMediciActivity.INFORMATII_MEDIC);
        Toast.makeText(getApplicationContext(), medic.toString(), Toast.LENGTH_SHORT).show();
    }
}