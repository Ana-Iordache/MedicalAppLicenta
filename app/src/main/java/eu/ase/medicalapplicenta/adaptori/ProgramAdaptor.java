package eu.ase.medicalapplicenta.adaptori;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import eu.ase.medicalapplicenta.R;
import eu.ase.medicalapplicenta.entitati.ZiDeLucru;

public class ProgramAdaptor extends ArrayAdapter<ZiDeLucru> {
    private Context context;
    private int resource;
    private List<ZiDeLucru> program;
    private LayoutInflater inflater;

    public ProgramAdaptor(@NonNull Context context, int resource, @NonNull List<ZiDeLucru> program, LayoutInflater inflater) {
        super(context, resource, program);
        this.context = context;
        this.resource = resource;
        this.program = program;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        ZiDeLucru zi = program.get(position);

        if (zi != null) {
            TextView tvZiDeLucru = view.findViewById(R.id.tvZiDeLucru);
            String ziDeLucru = zi.getZi() + ": " + zi.getOraInceput() + " - " + zi.getOraSfarsit();
            tvZiDeLucru.setText(ziDeLucru);
        }

        return view;
    }
}
