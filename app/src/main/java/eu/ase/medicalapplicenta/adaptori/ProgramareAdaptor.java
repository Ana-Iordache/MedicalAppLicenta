package eu.ase.medicalapplicenta.adaptori;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
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
    public static final String MEDIC = "medic";
    public static final String PACIENTI = "Pacienti";
    public static final String PACIENT = "pacient";
    private final FirebaseService firebaseServiceSpecialitati;
    private final FirebaseService firebaseServiceMedici;
    private final FirebaseService firebaseServicePacienti;
    private final List<Programare> programari;
    private final OnProgramareClickListener onProgramareClickListener;
    private final OnProgramareLongClickListener onProgramareLongClickListener;
    private final OnBtnFeedbackClickListener onBtnFeedbackClickListener;

    private final String tipUser;

    private final Context context;

    public ProgramareAdaptor(List<Programare> programari, String tipUser, OnProgramareClickListener onProgramareClickListener,
                             OnProgramareLongClickListener onProgramareLongClickListener,
                             OnBtnFeedbackClickListener onBtnFeedbackClickListener, Context context) {
        this.programari = programari;
        this.tipUser = tipUser;
        this.onProgramareClickListener = onProgramareClickListener;
        this.onProgramareLongClickListener = onProgramareLongClickListener;
        this.onBtnFeedbackClickListener = onBtnFeedbackClickListener;
        this.context = context;
        firebaseServiceSpecialitati = new FirebaseService(SPECIALITATI);
        firebaseServiceMedici = new FirebaseService(MEDICI);
        firebaseServicePacienti = new FirebaseService(PACIENTI);
    }

    @NonNull
    @Override
    public ProgramareAdaptorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_programari, parent, false);
        return new ProgramareAdaptorViewHolder(view, onProgramareClickListener, onProgramareLongClickListener, onBtnFeedbackClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                holder.btnFeedback.setEnabled(false);
                holder.btnFeedback.setTextColor(context.getColor(R.color.custom_light_blue));
            } else if (p.getStatus().equals(context.getString(R.string.status_necompletat))) {
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_important, 0, 0, 0);
                if (tipUser.equals(MEDIC)) {
                    holder.tvStatus.setTextColor(Color.parseColor("#F5CE85"));
                } else {
                    holder.tvStatus.setText("");
                }
            } else if (p.getStatus().equals(context.getString(R.string.status_onorata))) {
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                holder.btnFeedback.setEnabled(true);
                holder.btnFeedback.setTextColor(context.getColor(R.color.custom_blue));
            } else if (p.getStatus().equals(context.getString(R.string.status_noua))) {
                holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok, 0, 0, 0);
                holder.btnFeedback.setEnabled(false);
                holder.btnFeedback.setTextColor(context.getColor(R.color.custom_light_blue));
            }

            if (p.isFeedbackAcordat()) {
                holder.btnFeedback.setEnabled(false);
                holder.btnFeedback.setTextColor(context.getColor(R.color.custom_light_blue));
            }

            if (tipUser.equals(PACIENT)) {
                seteazaDateMedic(p, holder);
            } else {
                holder.tvSpecialitate.setVisibility(View.GONE);
                holder.btnFeedback.setVisibility(View.GONE);
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

    public interface OnProgramareLongClickListener {
        void onProgramareLongClick(int position);
    }

    public interface OnBtnFeedbackClickListener {
        void onBtnFeedbackClick(int position);
    }

    public class ProgramareAdaptorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvNume;
        TextView tvSpecialitate;
        TextView tvDataProgramarii;
        TextView tvOraProgramarii;
        TextView tvStatus;
        AppCompatButton btnFeedback;
        ProgressBar progressBar;

        CardView cwProgramare;
        OnProgramareClickListener onProgramareClickListener;
        OnProgramareLongClickListener onProgramareLongClickListener;
        OnBtnFeedbackClickListener onBtnFeedbackClickListener;

        public ProgramareAdaptorViewHolder(@NonNull View itemView, OnProgramareClickListener onProgramareClickListener,
                                           OnProgramareLongClickListener onProgramareLongClickListener, OnBtnFeedbackClickListener onBtnFeedbackClickListener) {
            super(itemView);
            tvNume = itemView.findViewById(R.id.tvNume);
            tvSpecialitate = itemView.findViewById(R.id.tvSpecialitate);
            tvDataProgramarii = itemView.findViewById(R.id.tvDataProgramarii);
            tvOraProgramarii = itemView.findViewById(R.id.tvOraProgramarii);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnFeedback = itemView.findViewById(R.id.btnFeedback);
            progressBar = itemView.findViewById(R.id.progressBar);
            cwProgramare = itemView.findViewById(R.id.cwProgramare);

            cwProgramare.setOnClickListener(this);
            cwProgramare.setOnLongClickListener(this);
            btnFeedback.setOnClickListener(this);

            this.onProgramareClickListener = onProgramareClickListener;
            this.onProgramareLongClickListener = onProgramareLongClickListener;
            this.onBtnFeedbackClickListener = onBtnFeedbackClickListener;
        }


        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cwProgramare:
                    onProgramareClickListener.onProgramareClick(getAdapterPosition());
                    break;
                case R.id.btnFeedback:
                    onBtnFeedbackClickListener.onBtnFeedbackClick(getAdapterPosition());
                    break;
            }
        }

        @Override
        public boolean onLongClick(View view) {
            onProgramareLongClickListener.onProgramareLongClick(getAdapterPosition());
            return true;
        }
    }
}
