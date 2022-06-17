package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
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
import eu.ase.medicalapplicenta.utile.FirebaseService;

public class ChatAdaptor extends RecyclerView.Adapter<ChatAdaptor.ChatViewHolder> {
    public static final int TIP_MESAJ_PRIMIT = 0;
    public static final int TIP_MESAJ_TRIMIS = 1;
    private final List<Mesaj> mesaje;
    private final Context context;

    private String idUserConectat;

    public ChatAdaptor(List<Mesaj> mesaje, Context context) {
        this.mesaje = mesaje;
        this.context = context;
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

            if (position == mesaje.size() - 1 && getItemViewType(position) == TIP_MESAJ_TRIMIS) {
                holder.tvStatusMesaj.setVisibility(View.VISIBLE);
                if (mesaj.isMesajCitit()) {
                    holder.tvStatusMesaj.setText(context.getString(R.string.mesaj_citit));
                    holder.tvStatusMesaj.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mesaj_citit, 0);
                } else {
                    holder.tvStatusMesaj.setText(context.getString(R.string.mesaj_livrat));
                    holder.tvStatusMesaj.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_mesaj_livrat, 0);
                }
            } else {
                holder.tvStatusMesaj.setVisibility(View.GONE);
            }
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
        TextView tvStatusMesaj;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMesaj = itemView.findViewById(R.id.tvMesaj);
            tvStatusMesaj = itemView.findViewById(R.id.tvStatusMesaj);
        }
    }
}
