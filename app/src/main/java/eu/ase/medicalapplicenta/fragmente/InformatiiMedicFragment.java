package eu.ase.medicalapplicenta.fragmente;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.activitati.ListaMediciActivity;
import eu.ase.medicalapplicenta.entitati.Medic;

public class InformatiiMedicFragment extends Fragment {

    private static final String INFORMATII_MEDIC = "informatiiMedic";

    private Medic medic;

    private TextView tvNumeMedic;
    private TextView tvGradProfesional;
    private RelativeLayout rlFragment;

    public InformatiiMedicFragment() {
        // Required empty public constructor
    }


//    public static InformatiiMedicFragment newInstance(String param1, String param2) {
//        InformatiiMedicFragment fragment = new InformatiiMedicFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informatii_medic, container, false);
        tvNumeMedic = view.findViewById(R.id.tvNumeMedic);
        tvGradProfesional = view.findViewById(R.id.tvGradProfesional);
        rlFragment = view.findViewById(R.id.rlFragment);

        rlFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        medic = (Medic) getArguments().getSerializable(ListaMediciActivity.INFORMATII_MEDIC);

        String numeComplet = "Dr. " + medic.getNume() + " " + medic.getPrenume();
        tvNumeMedic.setText(numeComplet);
        tvGradProfesional.setText(medic.getGradProfesional());

        return view;
    }
}