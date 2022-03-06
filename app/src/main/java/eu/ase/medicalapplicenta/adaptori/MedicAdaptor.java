package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class MedicAdaptor extends RecyclerView.Adapter<MedicAdaptor.MedicViewHolder> {
    public static final String SPECIALITATI = "Specialitati";
    private final List<Medic> medici;
    private final Context context;
    private final FirebaseService firebaseService;
    private final OnDoctorClickListener onDoctorClickListener;

    public MedicAdaptor(List<Medic> medici, Context context, OnDoctorClickListener onDoctorClickListener) {
        this.medici = medici;
        this.context = context;
        firebaseService = new FirebaseService(SPECIALITATI);
        this.onDoctorClickListener = onDoctorClickListener;
    }

    @NonNull
    @Override
    public MedicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_medici, parent, false);
        return new MedicViewHolder(view, onDoctorClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicViewHolder holder, int position) {
        holder.seteazaDateMedic(medici.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return medici.size();
    }

    // trimit informatia de click activitatii
    public interface OnDoctorClickListener {
        void onDoctorClick(int position);
    }

    public class MedicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ciwPozaProfilMedic;
        TextView tvNumeMedic;
        TextView tvGradProfesional;
        TextView tvSpecialitate;
        TextView tvNota;
        TextView tvNrFeedbackuri;

        OnDoctorClickListener onDoctorClickListener;

        public MedicViewHolder(@NonNull View itemView, OnDoctorClickListener onDoctorClickListener) {
            super(itemView);
            ciwPozaProfilMedic = itemView.findViewById(R.id.ciwPozaProfilMedic);
            tvNumeMedic = itemView.findViewById(R.id.tvNumeMedic);
            tvGradProfesional = itemView.findViewById(R.id.tvGradProfesional);
            tvSpecialitate = itemView.findViewById(R.id.tvSpecialitate);
            tvNota = itemView.findViewById(R.id.tvNota);
            tvNrFeedbackuri = itemView.findViewById(R.id.tvNrFeedbackuri);

            this.onDoctorClickListener = onDoctorClickListener;
            itemView.setOnClickListener(this);
        }

        void seteazaDateMedic(Medic m, MedicViewHolder holder) {
            if (!m.getUrlPozaProfil().equals(""))
                Glide.with(context).load(m.getUrlPozaProfil()).into(holder.ciwPozaProfilMedic);

            String numeComplet = "Dr. " + m.getNume() + " " + m.getPrenume();
            holder.tvNumeMedic.setText(numeComplet);

            if (m.getGradProfesional().equals("Nespecificat")) {
                holder.tvGradProfesional.setText("");
            } else holder.tvGradProfesional.setText(m.getGradProfesional());

            firebaseService.databaseReference.child(m.getIdSpecialitate()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Specialitate s = snapshot.getValue(Specialitate.class);
                    holder.tvSpecialitate.setText(s.getDenumire());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            if (m.getNotaFeedback() == 0.0) {
                holder.tvNota.setText("");
                holder.tvNrFeedbackuri.setText("");
            } else {
                holder.tvNota.setText(String.valueOf(m.getNotaFeedback()));
//                tvNrFeedbackuri.setText(..); todo
            }
        }

        // cand dau click pe item se apeleaza asta
        @Override
        public void onClick(View view) {
            onDoctorClickListener.onDoctorClick(getAdapterPosition());
        }
    }
}

// pt listView
//public class MedicAdaptor extends ArrayAdapter<Medic> {
//    public static final String SPECIALITATI = "Specialitati";
//    private Context context;
//    private int resource;
//    private List<Medic> medici;
//    private LayoutInflater inflater;
//    private FirebaseService firebaseService;
//
//    public MedicAdaptor(@NonNull Context context, int resource, @NonNull List<Medic> medici, LayoutInflater inflater) {
//        super(context, resource, medici);
//        this.context = context;
//        this.resource = resource;
//        this.medici = medici;
//        this.inflater = inflater;
//        firebaseService = new FirebaseService(SPECIALITATI);
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View view = inflater.inflate(resource, parent, false);
//
//        Medic m = medici.get(position);
//
//        if (m != null) {
//            CircleImageView ciwPozaProfilMedic = view.findViewById(R.id.ciwPozaProfilMedic);
//            if (!m.getUrlPozaProfil().equals("")) {
//                Glide.with(view).load(m.getUrlPozaProfil()).into(ciwPozaProfilMedic);
//            }
//
//            TextView tvNumeMedic = view.findViewById(R.id.tvNumeMedic);
//            String numeComplet = "Dr. " + m.getNume() + " " + m.getPrenume();
//            tvNumeMedic.setText(numeComplet);
//
//            TextView tvGradProfesional = view.findViewById(R.id.tvGradProfesional);
//            if (m.getGradProfesional().equals("Nespecificat")) {
//                tvGradProfesional.setText("");
//            } else {
//                tvGradProfesional.setText(m.getGradProfesional());
//            }
//
//            TextView tvSpecialitate = view.findViewById(R.id.tvSpecialitate);
//            firebaseService.databaseReference.child(m.getIdSpecialitate()).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    Specialitate s = snapshot.getValue(Specialitate.class);
//                    tvSpecialitate.setText(s.getDenumire());
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//
//            TextView tvNota = view.findViewById(R.id.tvNota);
//            TextView tvNrFeedbackuri = view.findViewById(R.id.tvNrFeedbackuri);
//            if (m.getNotaFeedback() == 0.0) {
//                tvNota.setText("");
//                tvNrFeedbackuri.setText("");
//            } else {
//                tvNota.setText(String.valueOf(m.getNotaFeedback()));
////                tvNrFeedbackuri.setText();
//            }
//        }
//
//        return view;
//    }
//}
