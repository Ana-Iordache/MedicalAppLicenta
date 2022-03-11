package eu.ase.medicalapplicenta.adaptori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.ase.medicalapplicenta.R;

public class OraDisponibilaAdaptor extends RecyclerView.Adapter<OraDisponibilaAdaptor.OraDisponibilaViewHolder> {
    private final List<String> oreDisponibile;
    private final OnOraClickListener onOraClickListener;

    public OraDisponibilaAdaptor(List<String> oreDisponibile, OnOraClickListener onOraClickListener) {
        this.oreDisponibile = oreDisponibile;
        this.onOraClickListener = onOraClickListener;
    }

    @NonNull
    @Override
    public OraDisponibilaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_ore_disponibile, parent, false);
        return new OraDisponibilaViewHolder(view, onOraClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OraDisponibilaViewHolder holder, int position) {
        String ora = oreDisponibile.get(position);
        if (ora != null) {
            holder.tvOra.setText(ora);
        }
    }

    @Override
    public int getItemCount() {
        return oreDisponibile.size();
    }

    public interface OnOraClickListener {
        void onOraClick(int position);
    }

    public class OraDisponibilaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvOra;
        CardView cwOra;

        OnOraClickListener onOraClickListener;

        public OraDisponibilaViewHolder(@NonNull View itemView, OnOraClickListener onOraClickListener) {
            super(itemView);
            tvOra = itemView.findViewById(R.id.tvOra);
            cwOra = itemView.findViewById(R.id.cwOra);

            this.onOraClickListener = onOraClickListener;
            cwOra.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onOraClickListener.onOraClick(getAdapterPosition());
        }
    }
}
