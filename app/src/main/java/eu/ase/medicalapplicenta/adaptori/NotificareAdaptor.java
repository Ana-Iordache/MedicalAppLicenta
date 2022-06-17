package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Medic;
import eu.ase.medicalapplicenta.entitati.Notificare;
import eu.ase.medicalapplicenta.entitati.Pacient;
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class NotificareAdaptor extends RecyclerView.Adapter<NotificareAdaptor.NotificareAdaptorViewHolder> {
    public static final String PACIENTI = "Pacienti";
    public static final String MEDICI = "Medici";
    public static final String NOTIFICARI = "Notificari";
    public static final String PACIENT = "pacient";
    public static final String MEDIC = "medic";
    private final FirebaseService firebaseServicePacienti;
    private final FirebaseService firebaseServiceMedici;
    private final FirebaseService firebaseServiceNotificari;
    private final List<Notificare> notificari;
    private final String tipUser;
    private final Context context;

    public NotificareAdaptor(List<Notificare> notificari, String tipUser, Context context) {
        this.notificari = notificari;
        this.tipUser = tipUser;
        this.context = context;
        firebaseServicePacienti = new FirebaseService(PACIENTI);
        firebaseServiceMedici = new FirebaseService(MEDICI);
        firebaseServiceNotificari = new FirebaseService(NOTIFICARI);
    }

    @NonNull
    @Override
    public NotificareAdaptorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_notificari, parent, false);
        return new NotificareAdaptorViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull NotificareAdaptorViewHolder holder, int position) {
        Notificare notificare = notificari.get(position);
        if (notificare != null) {
            if (!notificare.isNotificareCitita()) {
//                firebaseServiceNotificari.databaseReference
//                        .child(notificare.getIdNotificare())
//                        .child("notificareCitita")
//                        .setValue(true);
                holder.cwNotificare.setCardBackgroundColor(context.getColor(R.color.custom_light_blue));
            } else {
                holder.cwNotificare.setCardBackgroundColor(context.getColor(R.color.colorDivider));
            }

            holder.tvTitluNotificare.setText(notificare.getTitlu());
            holder.tvDataNotificare.setText(notificare.getData());

            if (tipUser.equals(PACIENT)) {
                // daca e conectat un pacient atunci el e receptorul iar medicul e emitatorul
                // deci trebuie sa preiau notificarile care au id-ul pacientului ca receptor
                // si toate notificarile contin ca emitator diferiti medici deci trebuie sa preiau numele medicului
                firebaseServiceMedici.preiaObiectDinFirebase(preiaMedic(holder, notificare), notificare.getIdEmitator());
            } else if (tipUser.equals(MEDIC)) {
                firebaseServicePacienti.preiaObiectDinFirebase(preiaPacient(holder, notificare), notificare.getIdEmitator());
            }

//            if (!notificare.isNotificareCitita()) {
//                notificare.setNotificareCitita(true);
//
//            }
        }
    }


    private ValueEventListener preiaPacient(NotificareAdaptorViewHolder holder, Notificare notificare) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Pacient pacient = snapshot.getValue(Pacient.class);
                String numeComplet = pacient.getNume() + " " + pacient.getPrenume();
                String mesaj = numeComplet;
                if (notificare.getTitlu().equals(context.getString(R.string.programare_anulata))) {
                    mesaj += " a anulat programarea din " + notificare.getDataProgramarii() + " de la ora " + notificare.getOraProgramarii() + ".";
                } else if (notificare.getTitlu().equals(context.getString(R.string.programare_noua))) {
                    mesaj += " a adăugat o nouă programare pe " + notificare.getDataProgramarii() + " la ora " + notificare.getOraProgramarii() + ".";
                }
                holder.tvMesajNotificare.setText(mesaj);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener preiaMedic(NotificareAdaptorViewHolder holder, Notificare notificare) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Medic medic = snapshot.getValue(Medic.class);
                String numeComplet = "Dr. " + medic.getNume() + " " + medic.getPrenume();
                String mesaj = numeComplet;
                if (notificare.getTitlu().equals(context.getString(R.string.programare_anulata))) {
                    mesaj += " a anulat programarea din " + notificare.getDataProgramarii() + " de la ora " + notificare.getOraProgramarii() + ".";
                    holder.tvMesajNotificare.setText(mesaj);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }


    @Override
    public int getItemCount() {
        return notificari.size();
    }

    public class NotificareAdaptorViewHolder extends RecyclerView.ViewHolder {
        //        RelativeLayout rlNotificare;
        TextView tvTitluNotificare;
        TextView tvMesajNotificare;
        TextView tvDataNotificare;
        CardView cwNotificare;

        public NotificareAdaptorViewHolder(@NonNull View itemView) {
            super(itemView);
//            rlNotificare = itemView.findViewById(R.id.rlNotificare);
            tvTitluNotificare = itemView.findViewById(R.id.tvTitluNotificare);
            tvMesajNotificare = itemView.findViewById(R.id.tvMesajNotificare);
            tvDataNotificare = itemView.findViewById(R.id.tvDataNotificare);
            cwNotificare = itemView.findViewById(R.id.cwNotificare);
        }
    }
}
