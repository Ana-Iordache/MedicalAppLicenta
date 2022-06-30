package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.stripe.android.paymentsheet.PaymentSheet;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.Factura;

public class FacturaAdaptor extends RecyclerView.Adapter<FacturaAdaptor.FacturaAdaptorViewHolder> {
    private final List<Factura> facturi;
    private final Context context;
    private final OnBtnPlataClickListener onBtnPlataClickListener;

    public FacturaAdaptor(List<Factura> facturi, Context context, OnBtnPlataClickListener onBtnPlataClickListener) {
        this.facturi = facturi;
        this.context = context;
        this.onBtnPlataClickListener = onBtnPlataClickListener;
    }

    @NonNull
    @Override
    public FacturaAdaptorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_facturi, parent, false);
        return new FacturaAdaptorViewHolder(view, onBtnPlataClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull FacturaAdaptorViewHolder holder, int position) {
        Factura f = facturi.get(position);
        if (f != null) {
            holder.tvStatus.setText(f.getStatus());
            String valoare = f.getValoare() + " RON";
            holder.tvValoare.setText(valoare);
            holder.tvDataEmiterii.setText(f.getDataEmiterii());
            holder.tvDataScadenta.setText(f.getDataScadenta());
            if (f.getStatus().equals(context.getString(R.string.achitata))) {
                holder.ivAchitata.setVisibility(View.VISIBLE);
                holder.ivNeachitata.setVisibility(View.GONE);
                holder.btnPlata.setEnabled(false);
                holder.btnPlata.setTextColor(context.getColor(R.color.custom_light_blue));
            } else if (f.getStatus().equals(context.getString(R.string.neachitata))) {
                holder.ivAchitata.setVisibility(View.GONE);
                holder.ivNeachitata.setVisibility(View.VISIBLE);
                holder.btnPlata.setEnabled(true);
                holder.btnPlata.setTextColor(context.getColor(R.color.custom_blue));
            } else if (f.getStatus().equals(context.getString(R.string.status_anulata))) {
                holder.ivAchitata.setVisibility(View.GONE);
                holder.ivNeachitata.setVisibility(View.GONE);
                holder.btnPlata.setEnabled(false);
                holder.btnPlata.setTextColor(context.getColor(R.color.custom_light_blue));
            }
        }
    }

    @Override
    public int getItemCount() {
        return facturi.size();
    }

    public interface OnBtnPlataClickListener {
        void onBtnPlataClick(int position);
    }

    public class FacturaAdaptorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvStatus;
        TextView tvValoare;
        TextView tvDataEmiterii;
        TextView tvDataScadenta;
        ImageView ivNeachitata;
        ImageView ivAchitata;
        AppCompatButton btnPlata;

        OnBtnPlataClickListener onBtnPlataClickListener;

        public FacturaAdaptorViewHolder(@NonNull View itemView, OnBtnPlataClickListener onBtnPlataClickListener) {
            super(itemView);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvValoare = itemView.findViewById(R.id.tvValoare);
            tvDataEmiterii = itemView.findViewById(R.id.tvDataEmiterii);
            tvDataScadenta = itemView.findViewById(R.id.tvDataScadenta);
            ivNeachitata = itemView.findViewById(R.id.ivNeachitata);
            ivAchitata = itemView.findViewById(R.id.ivAchitata);
            btnPlata = itemView.findViewById(R.id.btnPlata);

            btnPlata.setOnClickListener(this);

            this.onBtnPlataClickListener = onBtnPlataClickListener;
        }

        @Override
        public void onClick(View view) {
            onBtnPlataClickListener.onBtnPlataClick(getAdapterPosition());
        }
    }
}
