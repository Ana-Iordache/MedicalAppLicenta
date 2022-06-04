package eu.ase.medicalapplicenta.activitati;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Investigatie;
import eu.ase.medicalapplicenta.entitati.Specialitate;

public class AdminActivity extends AppCompatActivity {
    private final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Specialitati");
    EditText et1, et2, et3;
    Button button1, button2;
    List<Investigatie> investigatii = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        et1 = findViewById(R.id.et1);
        et2 = findViewById(R.id.et2);
        et3 = findViewById(R.id.et3);
        button1 = findViewById(R.id.button1);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Investigatie i = new Investigatie(et2.getText().toString().trim(), Double.parseDouble(et3.getText().toString().trim()));
                investigatii.add(i);

                et2.setText("");
                et3.setText("");
            }
        });
        button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Specialitate specialitate = new Specialitate(null, et1.getText().toString().trim(), investigatii);
                String idSpecialitate = reference.push().getKey();
                specialitate.setIdSpecialitate(idSpecialitate);
                reference.child(specialitate.getIdSpecialitate()).setValue(specialitate);

                et1.setText("");
            }
        });


    }
}