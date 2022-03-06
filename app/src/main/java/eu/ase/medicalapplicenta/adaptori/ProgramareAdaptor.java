package eu.ase.medicalapplicenta.adaptori;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.entitati.Specialitate;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ProgramareAdaptor extends RecyclerView.Adapter<ProgramareAdaptor.ProgramareAdaptorViewHolder> {
    public static final String SPECIALITATI = "Specialitati";
    public static final String MEDICI = "Medici";
    private final FirebaseService firebaseServiceSpecialitati;
    private final FirebaseService firebaseServiceMedici;
    private final List<Programare> programari;

    public ProgramareAdaptor(List<Programare> programari) {
        this.programari = programari;
        firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
        firebaseServiceMedici = new FirebaseService(MEDICI);
    }

    @NonNull
    @Override
    public ProgramareAdaptorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_programari, parent, false);
        return new ProgramareAdaptorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgramareAdaptorViewHolder holder, int position) {
        Programare p = programari.get(position);
        if (p != null) {
            holder.tvDataProgramarii.setText(p.getData());
            holder.tvOraProgramarii.setText(p.getOra());

            firebaseServiceMedici.databaseReference.child(p.getIdMedic()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Medic m = snapshot.getValue(Medic.class);
                    String numeComplet = "Dr. " + m.getNume() + " " + m.getPrenume();
                    holder.tvNumeMedic.setText(numeComplet);


                    firebaseServiceSpecialitati.databaseReference.child(m.getIdSpecialitate()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Specialitate s = snapshot.getValue(Specialitate.class);
                            holder.tvSpecialitate.setText(s.getDenumire());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("preluareSpecProg", error.getMessage());
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("preluareMedicProg", error.getMessage());
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return programari.size();
    }

    public class ProgramareAdaptorViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeMedic;
        TextView tvSpecialitate;
        TextView tvDataProgramarii;
        TextView tvOraProgramarii;

        public ProgramareAdaptorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeMedic = itemView.findViewById(R.id.tvNumeMedic);
            tvSpecialitate = itemView.findViewById(R.id.tvSpecialitate);
            tvDataProgramarii = itemView.findViewById(R.id.tvDataProgramarii);
            tvOraProgramarii = itemView.findViewById(R.id.tvOraProgramarii);
        }
    }
}
