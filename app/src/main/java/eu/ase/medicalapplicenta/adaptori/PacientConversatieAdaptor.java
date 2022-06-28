package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Conversatie;
import eu.ase.medicalapplicenta.entitati.Mesaj;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

// asta e ce apare medicului
public class PacientConversatieAdaptor extends RecyclerView.Adapter<PacientConversatieAdaptor.PacientConversatieViewHolder> {
    private final List<Pacient> pacienti;
    private final Context context;
    private final PacientAdaptor.OnPacientClickListener onPacientClickListener;
    private final FirebaseService firebaseService;
    private String idUserConectat;

    public PacientConversatieAdaptor(List<Pacient> pacienti, Context context, PacientAdaptor.OnPacientClickListener onPacientClickListener) {
        this.pacienti = pacienti;
        this.context = context;
        this.onPacientClickListener = onPacientClickListener;
        firebaseService = new FirebaseService("Conversatii");
        idUserConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @NonNull
    @Override
    public PacientConversatieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_conversatii, parent, false);
        return new PacientConversatieAdaptor.PacientConversatieViewHolder(view, onPacientClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PacientConversatieViewHolder holder, int position) {
        Pacient pacient = pacienti.get(position);
        if (pacient != null) {
            if (!pacient.getUrlPozaProfil().equals(""))
                Glide.with(context).load(pacient.getUrlPozaProfil()).into(holder.ciwPozaProfil);

            String numeComplet = pacient.getNume() + " " + pacient.getPrenume();
            holder.tvNume.setText(numeComplet);

            firebaseService.preiaDateDinFirebase(preiaConversatie(pacient.getIdPacient(), holder));
        }
    }

    private ValueEventListener preiaConversatie(String idPacient, PacientConversatieViewHolder holder) {
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Conversatie conversatie = dataSnapshot.getValue(Conversatie.class);
                    Mesaj ultimulMesaj = conversatie.getMesaje().get(conversatie.getMesaje().size() - 1);

                    String text;

                    if (ultimulMesaj.getIdEmitator().equals(idUserConectat) && ultimulMesaj.getIdReceptor().equals(idPacient)) {
                        text = ultimulMesaj.getText();
                        if (text.length() > 35) {
                            text = text.substring(0, 32) + "...";
                        }
                        holder.tvUltimulMesaj.setText(text);
                        if (ultimulMesaj.isMesajCitit()) {
                            holder.tvUltimulMesaj.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mesaj_citit, 0, 0, 0);
                        } else {
                            holder.tvUltimulMesaj.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_mesaj_livrat, 0, 0, 0);
                        }
                    } else if (ultimulMesaj.getIdEmitator().equals(idPacient) && ultimulMesaj.getIdReceptor().equals(idUserConectat)) {
                        text = ultimulMesaj.getText();
                        if (text.length() > 35) {
                            text = text.substring(0, 32) + "...";
                        }
                        holder.tvUltimulMesaj.setText(text);
                        if (!ultimulMesaj.isMesajCitit()) {
                            holder.tvUltimulMesaj.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD_ITALIC));
                            int mesajeNecitite = (int) conversatie.getMesaje().stream().filter(mesaj -> !mesaj.isMesajCitit()).count();
                            holder.btnMesajeNecitite.setVisibility(View.VISIBLE);
                            holder.btnMesajeNecitite.setText(String.valueOf(mesajeNecitite));
                        } else {
                            holder.tvUltimulMesaj.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                            holder.btnMesajeNecitite.setVisibility(View.GONE);
                        }
                    }
//                    break;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    @Override
    public int getItemCount() {
        return pacienti.size();
    }

    public class PacientConversatieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView ciwPozaProfil;
        TextView tvNume;
        TextView tvUltimulMesaj;
        AppCompatButton btnMesajeNecitite;

        PacientAdaptor.OnPacientClickListener onPacientClickListener;

        public PacientConversatieViewHolder(@NonNull View itemView, PacientAdaptor.OnPacientClickListener onPacientClickListener) {
            super(itemView);
            ciwPozaProfil = itemView.findViewById(R.id.ciwPozaProfil);
            tvNume = itemView.findViewById(R.id.tvNume);
            tvUltimulMesaj = itemView.findViewById(R.id.tvUltimulMesaj);
            btnMesajeNecitite = itemView.findViewById(R.id.btnMesajeNecitite);

            this.onPacientClickListener = onPacientClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPacientClickListener.onPacientClick(getAdapterPosition());
        }
    }
}
