package eu.ase.medicalapplicenta.adaptori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.ase.medicalapplicenta.R;

public class OraDisponibilaAdaptor extends RecyclerView.Adapter<OraDisponibilaAdaptor.OraDisponibilaViewHolder> {
    private final List<String> oreDisponibile;
    private final OnButtonClickListener onButtonClickListener;

    public OraDisponibilaAdaptor(List<String> oreDisponibile, OnButtonClickListener onButtonClickListener) {
        this.oreDisponibile = oreDisponibile;
        this.onButtonClickListener = onButtonClickListener;
    }

    @NonNull
    @Override
    public OraDisponibilaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_ore_disponibile, parent, false);
        return new OraDisponibilaViewHolder(view, onButtonClickListener);
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

    public interface OnButtonClickListener{
        void onButtonClick(int position);
    }

    public class OraDisponibilaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvOra;
        AppCompatButton btnProgrameaza;

        OnButtonClickListener onButtonClickListener;

        public OraDisponibilaViewHolder(@NonNull View itemView, OnButtonClickListener onButtonClickListener) {
            super(itemView);
            tvOra = itemView.findViewById(R.id.tvOra);
            btnProgrameaza = itemView.findViewById(R.id.btnProgrameaza);

            this.onButtonClickListener = onButtonClickListener;
            btnProgrameaza.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onButtonClickListener.onButtonClick(getAdapterPosition());
        }
    }
}
