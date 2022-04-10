package eu.ase.medicalapplicenta.activitati;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class CalculatorBmiActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String PACIENTI = "Pacienti";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.0");
    private static final double GREUTATE_MAXIMA = 250.0;
    private static final double GREUTATE_MINIMA = 30.0;
    private static final int INALTIME_MAXIMA = 250;
    private static final int INALTIME_MINIMA = 100;
    private Toolbar toolbar;

    private TextView tvInaltimeCurenta;
    private TextView tvGreutateCurenta;
//    private TextView tvVarstaCurenta;

    private SeekBar sbInaltime;
    private SeekBar sbGreutate;

    private ImageView minusInaltime;
    private ImageView plusInaltime;
    private ImageView minusGreutate;
    private ImageView plusGreutate;
//    private ImageView minusVarsta;
//    private ImageView plusVarsta;

//    private AppCompatButton btnCalculeaza;

    private LinearLayout llRezultat;
    private ImageView ivBmi;
    private TextView tvBmiGrad;
    private TextView tvBmiText;

    private FirebaseService firebaseService = new FirebaseService(PACIENTI);
    private FirebaseUser userConectat;
    private String idUserConectat;

    private Pacient pacient;

    private int inaltimeCurenta = INALTIME_MINIMA;
    //    private int varstaCurenta;
    private double greutateCurenta = GREUTATE_MINIMA;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_bmi);

        initializeazaAtribute();

        seteazaToolbar();

        minusInaltime.setOnClickListener(this);
        plusInaltime.setOnClickListener(this);
        minusGreutate.setOnClickListener(this);
        plusGreutate.setOnClickListener(this);
//        minusVarsta.setOnClickListener(this);
//        plusVarsta.setOnClickListener(this);

//        btnCalculeaza.setOnClickListener(this);

        seteazaSeekbars();

        Intent intent = getIntent();
        if (intent.hasExtra(MainActivity.PACIENT)) {
            firebaseService.preiaObiectDinFirebase(preiaPacient(), idUserConectat);
        } else {
            tvInaltimeCurenta.setText(R.string.inaltime_minima);
            sbInaltime.setProgress(100);
            tvGreutateCurenta.setText(R.string.greutate_minima);
            sbGreutate.setProgress(300);
        }

        calculeazaBmi();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void seteazaSeekbars() {
        sbInaltime.setMax(250);
        sbInaltime.setMin(100);
        sbInaltime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                inaltimeCurenta = i;
                tvInaltimeCurenta.setText(String.valueOf(inaltimeCurenta));
                calculeazaBmi();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sbGreutate.setMax(2500);
        sbGreutate.setMin(300);
        sbGreutate.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                greutateCurenta = (double) i / 10;
                tvGreutateCurenta.setText(DECIMAL_FORMAT.format(greutateCurenta));
                //todo merge din 2 in 2 nush dc.. si la regima maria tot asa face hmm
                calculeazaBmi();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void calculeazaBmi() {
        double inaltimeMetri = (float) inaltimeCurenta / 100;

        double bmi = greutateCurenta / (inaltimeMetri * inaltimeMetri);
        tvBmiGrad.setText(DECIMAL_FORMAT.format(bmi));
        if (bmi < 18.5) {
            tvBmiText.setText(R.string.bmi_subponderal);
            ivBmi.setBackgroundResource(R.drawable.ic_warning);
        } else if (bmi >= 18.5 && bmi <= 24.9) {
            tvBmiText.setText(R.string.bmi_normoponderal);
            ivBmi.setBackgroundResource(R.drawable.ic_ok_80);
        } else if (bmi >= 25.0 && bmi <= 29.9) {
            tvBmiText.setText(R.string.bmi_supraponderal);
            ivBmi.setBackgroundResource(R.drawable.ic_warning);
        } else if (bmi >= 30.0 && bmi <= 34.9) {
            tvBmiText.setText(R.string.bmi_obezitate1);
            ivBmi.setBackgroundResource(R.drawable.ic_danger);
        } else if (bmi >= 35.5 && bmi <= 39.9) {
            tvBmiText.setText(R.string.bmi_obezitate2);
            ivBmi.setBackgroundResource(R.drawable.ic_danger);
        } else {
            tvBmiText.setText(R.string.bmi_obezitate_morbida);
            ivBmi.setBackgroundResource(R.drawable.ic_danger);
        }
    }


    private ValueEventListener preiaPacient() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pacient = snapshot.getValue(Pacient.class);
                if (pacient.getInaltime() != 0.0) {
                    int inaltime = (int) (pacient.getInaltime() * 100);
                    tvInaltimeCurenta.setText(String.valueOf(inaltime));
                    sbInaltime.setProgress(inaltime);
                    inaltimeCurenta = inaltime;
                } else {
                    tvInaltimeCurenta.setText(R.string.inaltime_minima);
                    sbInaltime.setProgress(100);
                }


                greutateCurenta = pacient.getGreutate();
                if (pacient.getGreutate() != 0.0) {
                    tvGreutateCurenta.setText(DECIMAL_FORMAT.format(greutateCurenta));
                    sbGreutate.setProgress((int) (greutateCurenta * 10));
                } else {
                    tvGreutateCurenta.setText(R.string.greutate_minima);
                    sbGreutate.setProgress(300);
                }
//                greutate = pacient.getGreutate();

//                tvVarstaCurenta.setText(String.valueOf(pacient.getVarsta()));
//                varstaCurenta = pacient.getVarsta();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initializeazaAtribute() {
        toolbar = findViewById(R.id.toolbar);

        tvInaltimeCurenta = findViewById(R.id.tvInaltimeCurenta);
        tvGreutateCurenta = findViewById(R.id.tvGreutateCurenta);
//        tvVarstaCurenta = findViewById(R.id.tvVarstaCurenta);

        sbInaltime = findViewById(R.id.sbInaltime);
        sbGreutate = findViewById(R.id.sbGreutate);

        minusInaltime = findViewById(R.id.minusInaltime);
        plusInaltime = findViewById(R.id.plusInaltime);
        minusGreutate = findViewById(R.id.minusGreutate);
        plusGreutate = findViewById(R.id.plusGreutate);
//        minusVarsta = findViewById(R.id.minusVarsta);
//        plusVarsta = findViewById(R.id.plusVarsta);

//        btnCalculeaza = findViewById(R.id.btnCalculeaza);

        llRezultat = findViewById(R.id.llRezultat);
        ivBmi = findViewById(R.id.ivBmi);
        tvBmiGrad = findViewById(R.id.tvBmiGrad);
        tvBmiText = findViewById(R.id.tvBmiText);

        userConectat = FirebaseAuth.getInstance().getCurrentUser();
        idUserConectat = userConectat.getUid();
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
            case R.id.minusInaltime:
                if (inaltimeCurenta > INALTIME_MINIMA) {
                    inaltimeCurenta--;
                    tvInaltimeCurenta.setText(String.valueOf(inaltimeCurenta));
                    sbInaltime.setProgress(inaltimeCurenta);
                }
                break;
            case R.id.plusInaltime:
                if (inaltimeCurenta < INALTIME_MAXIMA) {
                    inaltimeCurenta++;
                    tvInaltimeCurenta.setText(String.valueOf(inaltimeCurenta));
                    sbInaltime.setProgress(inaltimeCurenta);
                }
                break;
            case R.id.minusGreutate:
                if (greutateCurenta > GREUTATE_MINIMA) {
                    greutateCurenta -= 0.1;
                    tvGreutateCurenta.setText(DECIMAL_FORMAT.format(greutateCurenta));
                    sbGreutate.setProgress((int) (greutateCurenta * 10));
                }
                break;
            case R.id.plusGreutate:
                if (greutateCurenta < GREUTATE_MAXIMA) {
                    greutateCurenta += 0.1;
                    tvGreutateCurenta.setText(DECIMAL_FORMAT.format(greutateCurenta));
                    sbGreutate.setProgress((int) (greutateCurenta * 10));
                }
                break;
        }
    }
//todo poate fac sa se modifice direct indicele in timp ce schimba greutatea sau inaltime si sa renunt la buton
// sau daca pastrez butonul sa dispara ce se calculase deja pana apasa iar pe buton

//    @Override
//    public void finish() {
//        super.finish();
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
//    }
}