package com.example.medicapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.RecetaViewHolder> {
    private List<Receta> recetaList;

    public RecetaAdapter(List<Receta> recetaList) {
        this.recetaList = recetaList;
    }

    @NonNull
    @Override
    public RecetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receta, parent, false);
        return new RecetaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecetaViewHolder holder, int position) {
        Receta receta = recetaList.get(position);

        // Nombre y Dosis
        holder.nombre.setText(receta.getNombre());
        holder.dosis.setText(receta.getDosis());

        // Frecuencia con el texto "Cada X horas"
        String frecuenciaText = "Cada " + receta.getFrecuencia() + " horas";
        holder.frecuencia.setText(frecuenciaText);

        // Tratamiento con el texto "Tratamiento: X días"
        String tratamientoText = "Tratamiento: " + receta.getTratamiento() + " días";
        holder.tratamiento.setText(tratamientoText);

        // Notas
        holder.notas.setText(receta.getNotas().isEmpty() ? "No hay notas" : receta.getNotas());

        // Hora de inicio, formateado a fecha legible
        if (receta.getHora_inicio() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault());
            String formattedDate = dateFormat.format(receta.getHora_inicio().toDate());
            holder.horaInicio.setText(formattedDate);
        } else {
            holder.horaInicio.setText("Fecha no disponible");
        }
    }

    @Override
    public int getItemCount() {
        return recetaList.size();
    }

    static class RecetaViewHolder extends RecyclerView.ViewHolder {
        TextView dosis, frecuencia, horaInicio, nombre, notas, tratamiento;

        public RecetaViewHolder(@NonNull View itemView) {
            super(itemView);
            dosis = itemView.findViewById(R.id.dosis);
            frecuencia = itemView.findViewById(R.id.frecuencia);
            horaInicio = itemView.findViewById(R.id.horaInicio);
            nombre = itemView.findViewById(R.id.nombre);
            notas = itemView.findViewById(R.id.notas);
            tratamiento = itemView.findViewById(R.id.tratamiento);
        }
    }
}
