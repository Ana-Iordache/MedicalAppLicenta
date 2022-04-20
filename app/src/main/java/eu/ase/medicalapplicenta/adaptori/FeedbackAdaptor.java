package eu.ase.medicalapplicenta.adaptori;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.activitati.HomeMedicActivity;
import eu.ase.medicalapplicenta.activitati.ListaMediciActivity;
import eu.ase.medicalapplicenta.entitati.Feedback;
import eu.ase.medicalapplicenta.entitati.Programare;

public class FeedbackAdaptor extends RecyclerView.Adapter<FeedbackAdaptor.FeedbackViewHolder> {
    private final List<Programare> programari;

    public FeedbackAdaptor(List<Programare> programari) {
        this.programari = programari;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_lista_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Programare programare = programari.get(position);
        if (programare != null) {
            Feedback feedback = programare.getFeedback();
            holder.tvDataProgramarii.setText(programare.getData());
            String nota = feedback.getNota() + " / 10";
            holder.tvNota.setText(nota);
            holder.tvRecenzie.setText(feedback.getRecenzie());
        }
    }

    @Override
    public int getItemCount() {
        return programari.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvDataProgramarii;
        TextView tvNota;
        TextView tvRecenzie;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDataProgramarii = itemView.findViewById(R.id.tvDataProgramarii);
            tvNota = itemView.findViewById(R.id.tvNota);
            tvRecenzie = itemView.findViewById(R.id.tvRecenzie);
        }
    }
}
