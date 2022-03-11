package eu.ase.medicalapplicenta.adaptori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Factura;

public class FacturaAdaptor extends RecyclerView.Adapter<FacturaAdaptor.FacturaAdaptorViewHolder> {
    private final List<Factura> facturi;

    public FacturaAdaptor(List<Factura> facturi) {
        this.facturi = facturi;
    }

    @NonNull
    @Override
    public FacturaAdaptorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_facturi, parent, false);
        return new FacturaAdaptorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FacturaAdaptorViewHolder holder, int position) {
        Factura f = facturi.get(position);
        if (f != null) {
            holder.tvStatus.setText(f.getStatus());
            String valoare = f.getValoare() + " RON";
            holder.tvValoare.setText(valoare);
            holder.tvDataEmiterii.setText(f.getDataEmiterii());
            holder.tvDataScadenta.setText(f.getDataScadenta());
        }
    }

    @Override
    public int getItemCount() {
        return facturi.size();
    }

    public class FacturaAdaptorViewHolder extends RecyclerView.ViewHolder {
        TextView tvStatus;
        TextView tvValoare;
        TextView tvDataEmiterii;
        TextView tvDataScadenta;

        public FacturaAdaptorViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvValoare = itemView.findViewById(R.id.tvValoare);
            tvDataEmiterii = itemView.findViewById(R.id.tvDataEmiterii);
            tvDataScadenta = itemView.findViewById(R.id.tvDataScadenta);
        }
    }
}
