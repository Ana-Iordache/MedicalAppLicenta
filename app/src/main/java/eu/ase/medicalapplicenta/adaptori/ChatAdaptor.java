package eu.ase.medicalapplicenta.adaptori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Mesaj;

public class ChatAdaptor extends RecyclerView.Adapter<ChatAdaptor.ChatViewHolder> {
    public static final int TIP_MESAJ_PRIMIT = 0;
    public static final int TIP_MESAJ_TRIMIS = 1;
    private final List<Mesaj> mesaje;

    private String idUserConectat;

    public ChatAdaptor(List<Mesaj> mesaje) {
        this.mesaje = mesaje;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == TIP_MESAJ_TRIMIS) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_mesaj_trimis, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_mesaj_primit, parent, false);
        }
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Mesaj mesaj = mesaje.get(position);
        if (mesaj != null) {
            holder.tvMesaj.setText(mesaj.getText());
        }
    }

    @Override
    public int getItemCount() {
        return mesaje.size();
    }

    @Override
    public int getItemViewType(int position) {
        idUserConectat = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (mesaje.get(position).getIdEmitator().equals(idUserConectat)) {
            return TIP_MESAJ_TRIMIS;
        } else {
            return TIP_MESAJ_PRIMIT;
        }
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMesaj;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMesaj = itemView.findViewById(R.id.tvMesaj);
        }
    }
}
