package com.example.medicapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private static final String TAG = "RECETAS_MA";
    private CalendarView calendarView;
    private FirebaseFirestore firestore;

    public HomeFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.activity_home_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);
        firestore = FirebaseFirestore.getInstance();

        fetchAndMarkDates();
    }

    private void fetchAndMarkDates() {
        firestore.collection("recetas")
                .get()
                .addOnSuccessListener(documents -> {
                    List<Long> markedDates = new ArrayList<>();

                    for (QueryDocumentSnapshot document : documents) {
                        // Obtiene datos de Firestore
                        Timestamp startTimestamp = document.getTimestamp("hora_inicio");
                        Long frecuencia = document.getLong("frecuencia");
                        Long tratamientoDias = document.getLong("tratamiento");

                        // Log para revisar los datos en Logcat
                        Log.d(TAG, "Documento ID: " + document.getId());
                        Log.d(TAG, "Inicio: " + startTimestamp);
                        Log.d(TAG, "Frecuencia: " + frecuencia);
                        Log.d(TAG, "Duración: " + tratamientoDias);

                        if (startTimestamp != null && frecuencia != null && tratamientoDias != null) {
                            Date startDate = startTimestamp.toDate();

                            // Calcula las fechas que se deben marcar
                            for (int i = 0; i < tratamientoDias; i++) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(startDate);
                                calendar.add(Calendar.HOUR, i * frecuencia.intValue()); // Incrementa según frecuencia
                                markedDates.add(calendar.getTimeInMillis());
                            }
                        }
                    }

                    // Maneja el evento de selección de fecha
                    calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
                        Calendar selectedDate = Calendar.getInstance();
                        selectedDate.set(year, month, dayOfMonth);
                        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
                        selectedDate.set(Calendar.MINUTE, 0);
                        selectedDate.set(Calendar.SECOND, 0);
                        selectedDate.set(Calendar.MILLISECOND, 0);

                        if (markedDates.contains(selectedDate.getTimeInMillis())) {
                            Toast.makeText(getContext(), "Tienes una medicación", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "No tienes medicación", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener datos: ", e);
                    Toast.makeText(getContext(), "Error al obtener datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}
