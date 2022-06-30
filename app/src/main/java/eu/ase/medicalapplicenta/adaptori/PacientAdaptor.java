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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.entitati.Programare;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class PacientAdaptor extends RecyclerView.Adapter<PacientAdaptor.PacientViewHolder> {
    public static final String PROGRAMARI = "Programari";
    private final List<Pacient> pacienti;
    private final Context context;
    private final OnPacientClickListener onPacientClickListener;
    private final FirebaseService firebaseService;
    private final String idMedic = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private TreeSet<Date> dateProgramari = new TreeSet<>(); // TreeSet imi pune in ordine sper..

    public PacientAdaptor(List<Pacient> pacienti, Context context, OnPacientClickListener onPacientClickListener) {
        this.pacienti = pacienti;
        this.context = context;
        this.onPacientClickListener = onPacientClickListener;
        firebaseService = new FirebaseService(PROGRAMARI);
    }

    @NonNull
    @Override
    public PacientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_pacienti, parent, false);
        return new PacientViewHolder(view, onPacientClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PacientViewHolder holder, int position) {
        Pacient pacient = pacienti.get(position);
        if (pacient != null) {
            if (!pacient.getUrlPozaProfil().equals("")) {
                Glide.with(context).load(pacient.getUrlPozaProfil()).into(holder.ciwPozaProfilPacient);
            } else {
                Glide.with(context).load(R.drawable.ic_account).into(holder.ciwPozaProfilPacient);
            }

            String numeComplet = pacient.getNume() + " " + pacient.getPrenume();
            holder.tvNumePacient.setText(numeComplet);

            holder.tvCnpValoare.setText(String.valueOf(pacient.getCnp()));

            firebaseService.preiaDateDinFirebase(preiaProgramari(pacient, holder));
        }
    }

    private ValueEventListener preiaProgramari(Pacient pacient, PacientViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dateProgramari.clear(); // ca sa nu cumuleze programarile de la toti pacientii
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Programare programare = dataSnapshot.getValue(Programare.class);
                    if (programare.getIdPacient().equals(pacient.getIdPacient()) && programare.getIdMedic().equals(idMedic) &&
                            (programare.getStatus().equals(context.getString(R.string.status_onorata)) /*||
                                    programare.getStatus().equals(context.getString(R.string.status_noua))*/)) {
                        try {
                            Date data = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(programare.getData());
                            dateProgramari.add(data);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!dateProgramari.isEmpty()) {
                    holder.tvUltimaProgramareData.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(dateProgramari.last()));
                } else {
                    holder.tvUltimaProgramareData.setText(" - ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareDateProgramari", error.getMessage());
            }
        };
    }

    @Override
    public int getItemCount() {
        return pacienti.size();
    }

    public interface OnPacientClickListener {
        void onPacientClick(int position);
    }

    public class PacientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ciwPozaProfilPacient;
        TextView tvNumePacient;
        TextView tvCnpValoare;
        TextView tvUltimaProgramareData;

        OnPacientClickListener onPacientClickListener;

        public PacientViewHolder(@NonNull View itemView, OnPacientClickListener onPacientClickListener) {
            super(itemView);
            ciwPozaProfilPacient = itemView.findViewById(R.id.ciwPozaProfilPacient);
            tvNumePacient = itemView.findViewById(R.id.tvNumePacient);
            tvCnpValoare = itemView.findViewById(R.id.tvCnpValoare);
            tvUltimaProgramareData = itemView.findViewById(R.id.tvUltimaProgramareData);

            this.onPacientClickListener = onPacientClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPacientClickListener.onPacientClick(getAdapterPosition());
        }
    }
}

