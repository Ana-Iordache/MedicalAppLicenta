package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.util.Log;
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

import java.text.DecimalFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class MedicAdaptor extends RecyclerView.Adapter<MedicAdaptor.MedicViewHolder> {
    public static final String SPECIALITATI = "Specialitati";
    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.00");
    private final List<Medic> medici;
    private final Context context;
    private final FirebaseService firebaseService;
    private final OnDoctorClickListener onDoctorClickListener;

    public MedicAdaptor(List<Medic> medici, Context context,
                        OnDoctorClickListener onDoctorClickListener) {
        this.medici = medici;
        this.context = context;
        firebaseService = new FirebaseService(SPECIALITATI);
        this.onDoctorClickListener = onDoctorClickListener;
    }

    @NonNull
    @Override
    public MedicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_medici,
                parent, false);
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

    private ValueEventListener preiaSpecialitate(MedicViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Specialitate s = snapshot.getValue(Specialitate.class);
                holder.tvSpecialitate.setText(s.getDenumire());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitate", error.getMessage());
            }
        };
    }

    public interface OnDoctorClickListener {
        void onDoctorClick(int position);
    }

    public class MedicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ciwPozaProfilMedic;
        TextView tvNumeMedic;
        TextView tvGradProfesional;
        TextView tvSpecialitate;
        TextView tvNota;

        OnDoctorClickListener onDoctorClickListener;

        public MedicViewHolder(@NonNull View itemView, OnDoctorClickListener onDoctorClickListener) {
            super(itemView);
            ciwPozaProfilMedic = itemView.findViewById(R.id.ciwPozaProfilMedic);
            tvNumeMedic = itemView.findViewById(R.id.tvNumeMedic);
            tvGradProfesional = itemView.findViewById(R.id.tvGradProfesional);
            tvSpecialitate = itemView.findViewById(R.id.tvSpecialitate);
            tvNota = itemView.findViewById(R.id.tvNota);

            this.onDoctorClickListener = onDoctorClickListener;
            itemView.setOnClickListener(this);
        }

        void seteazaDateMedic(Medic medic, MedicViewHolder holder) {
            if (!medic.getUrlPozaProfil().equals("")) {
                Glide.with(context).load(medic.getUrlPozaProfil()).into(holder.ciwPozaProfilMedic);
            } else {
                Glide.with(context).load(R.drawable.ic_account).into(holder.ciwPozaProfilMedic);
            }

            String numeComplet = "Dr. " + medic.getNume() + " " + medic.getPrenume();
            holder.tvNumeMedic.setText(numeComplet);

            if (medic.getGradProfesional().equals("Nespecificat")) {
                holder.tvGradProfesional.setText("");
            } else holder.tvGradProfesional.setText(medic.getGradProfesional());

            firebaseService.preiaObiectDinFirebase(preiaSpecialitate(holder), medic.getIdSpecialitate());

            if (medic.getNotaFeedback() == 0.0) {
                holder.tvNota.setText("");
            } else {
                holder.tvNota.setText(NUMBER_FORMAT.format(medic.getNotaFeedback()));
            }
        }

        @Override
        public void onClick(View view) {
            onDoctorClickListener.onDoctorClick(getAdapterPosition());
        }
    }
}
