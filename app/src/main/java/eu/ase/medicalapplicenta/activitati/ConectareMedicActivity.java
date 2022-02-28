package eu.ase.medicalapplicenta.activitati;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import eu.ase.medicalapplicenta.R;

public class ConectareMedicActivity extends AppCompatActivity implements View.OnClickListener{
    TextInputEditText tietLoginEmailMedic;
    TextInputEditText tietLoginParolaMedic;

    TextView tvResetareParola;
    TextView tvCreareCont;

    CheckBox cbRamaiAutentificat;

    Button btnLoginMedic;

    ImageView ivPacient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conectare_medic);

        tietLoginEmailMedic = findViewById(R.id.tietLoginEmailMedic);
        tietLoginParolaMedic = findViewById(R.id.tietLoginParolaMedic);

        tvResetareParola = findViewById(R.id.tvResetareParola);
        tvResetareParola.setOnClickListener(this);

        tvCreareCont = findViewById(R.id.tvCreareCont);
        tvCreareCont.setOnClickListener(this);

        cbRamaiAutentificat = findViewById(R.id.cbRamaiAutentificat);

        btnLoginMedic = findViewById(R.id.btnLoginMedic);
        btnLoginMedic.setOnClickListener(this);

        ivPacient = findViewById(R.id.ivPacient);
        ivPacient.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCreareCont:
                //TODO
//                startActivity(new Intent(getApplicationContext(), InregistrareMedicActivity.class));
                Toast.makeText(getApplicationContext(), "tvCreareCont", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnLoginMedic:
                //TODO
//                conectareMedic();
                Toast.makeText(getApplicationContext(), "btnLoginMedic", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tvResetareParola:
                //TODO daca functioneaza la fel
//                startActivity(new Intent(getApplicationContext(), ResetareParolaActivity.class));
                Toast.makeText(getApplicationContext(), "tvResetareParola", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ivPacient:
                finish();
                break;
        }
    }
}