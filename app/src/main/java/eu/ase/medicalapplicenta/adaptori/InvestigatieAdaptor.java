package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.helper.widget.Layer;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Investigatie;
import eu.ase.medicalapplicenta.entitati.Medic;

public class InvestigatieAdaptor extends RecyclerView.Adapter<InvestigatieAdaptor.InvestigatieViewHolder> {
    private final List<Investigatie> investigatii;
    private final Context context;

    public InvestigatieAdaptor(List<Investigatie> investigatii, Context context) {
        this.investigatii = investigatii;
        this.context = context;
    }

    @NonNull
    @Override
    public InvestigatieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_investigatii, parent, false);
        return new InvestigatieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestigatieViewHolder holder, int position) {
        Investigatie i = investigatii.get(position);
        if (i != null) {
            holder.tvDenumire.setText(i.getDenumire());
            String pret = String.valueOf(i.getPret()) + " RON";
            holder.tvPret.setText(pret);
        }
    }

    @Override
    public int getItemCount() {
        return investigatii.size();
    }

    public class InvestigatieViewHolder extends RecyclerView.ViewHolder {
        TextView tvDenumire;
        TextView tvPret;

        public InvestigatieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDenumire = itemView.findViewById(R.id.tvDenumire);
            tvPret = itemView.findViewById(R.id.tvPret);
        }
    }
}
