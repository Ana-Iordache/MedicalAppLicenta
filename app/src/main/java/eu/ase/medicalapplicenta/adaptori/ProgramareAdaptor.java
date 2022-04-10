package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ProgramareAdaptor extends RecyclerView.Adapter<ProgramareAdaptor.ProgramareAdaptorViewHolder> {
    public static final String SPECIALITATI = "Specialitati";
    public static final String MEDICI = "Medici";
    public static final String PACIENTI = "Pacienti";
    private final FirebaseService firebaseServiceSpecialitati;
    private final FirebaseService firebaseServiceMedici;
    private final FirebaseService firebaseServicePacienti;
    private final List<Programare> programari;
    private final OnProgramareClickListener onProgramareClickListener;

    private final String tipUser;

    private Context context;

    public ProgramareAdaptor(List<Programare> programari, String tipUser, OnProgramareClickListener onProgramareClickListener, Context context) {
        this.programari = programari;
        this.tipUser = tipUser;
        this.onProgramareClickListener = onProgramareClickListener;
        this.context = context;
        firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
        firebaseServiceMedici = new FirebaseService(MEDICI);
        firebaseServicePacienti = new FirebaseService(PACIENTI);
    }

    @NonNull
    @Override
    public ProgramareAdaptorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_programari, parent, false);
        return new ProgramareAdaptorViewHolder(view, onProgramareClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramareAdaptorViewHolder holder, int position) {

        Programare p = programari.get(position);
        if (p != null) {
            holder.tvDataProgramarii.setText(p.getData());
            holder.tvOraProgramarii.setText(p.getOra());
            holder.tvStatus.setText(p.getStatus());

            if (p.getStatus().equals(context.getString(R.string.status_anulata))
                    || p.getStatus().equals(context.getString(R.string.status_neonorata))) {
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel, 0, 0, 0);
            }

            if (tipUser.equals("pacient")) {
                seteazaDateMedic(p, holder);
            } else {
                holder.tvSpecialitate.setVisibility(View.GONE);
                seteazaNumePacient(p, holder);
            }

        }

    }

    private void seteazaNumePacient(Programare p, ProgramareAdaptorViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        firebaseServicePacienti.preiaObiectDinFirebase(preiaPacient(holder), p.getIdPacient());
    }

    private ValueEventListener preiaPacient(ProgramareAdaptorViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pacient pacient = snapshot.getValue(Pacient.class);
                if (pacient != null) {
                    String numeComplet = pacient.getNume() + " " + pacient.getPrenume();
                    holder.tvNume.setText(numeComplet);
                    holder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluarePacientProg", error.getMessage());
            }
        };
    }

    private void seteazaDateMedic(Programare p, ProgramareAdaptorViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        firebaseServiceMedici.databaseReference.child(p.getIdMedic()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Medic m = snapshot.getValue(Medic.class);
                String numeComplet = "Dr. " + m.getNume() + " " + m.getPrenume();
                holder.tvNume.setText(numeComplet);

                firebaseServiceSpecialitati.preiaObiectDinFirebase(preiaSpecialitate(holder), m.getIdSpecialitate());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedicProg", error.getMessage());
            }
        });
    }

    private ValueEventListener preiaSpecialitate(ProgramareAdaptorViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Specialitate s = snapshot.getValue(Specialitate.class);
                holder.tvSpecialitate.setText(s.getDenumire());
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecProg", error.getMessage());
            }
        };
    }

    @Override
    public int getItemCount() {
        return programari.size();
    }

    public interface OnProgramareClickListener {
        void onProgramareClick(int position);
    }

    public class ProgramareAdaptorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNume;
        TextView tvSpecialitate;
        TextView tvDataProgramarii;
        TextView tvOraProgramarii;
        TextView tvStatus;
        ProgressBar progressBar;

        CardView cwProgramare;
        OnProgramareClickListener onProgramareClickListener;

        public ProgramareAdaptorViewHolder(@NonNull View itemView, OnProgramareClickListener onProgramareClickListener) {
            super(itemView);
            tvNume = itemView.findViewById(R.id.tvNume);
            tvSpecialitate = itemView.findViewById(R.id.tvSpecialitate);
            tvDataProgramarii = itemView.findViewById(R.id.tvDataProgramarii);
            tvOraProgramarii = itemView.findViewById(R.id.tvOraProgramarii);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            progressBar = itemView.findViewById(R.id.progressBar);

            cwProgramare = itemView.findViewById(R.id.cwProgramare);
            cwProgramare.setOnClickListener(this);

            this.onProgramareClickListener = onProgramareClickListener;
        }


        @Override
        public void onClick(View view) {
            onProgramareClickListener.onProgramareClick(getAdapterPosition());
        }
    }
}
