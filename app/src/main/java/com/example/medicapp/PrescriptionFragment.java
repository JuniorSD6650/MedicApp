package com.example.medicapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionFragment extends Fragment {

    private RecyclerView recetasRecyclerView;
    private RecetaAdapter recetaAdapter;
    private List<Receta> recetaList;
    private FirebaseFirestore firestore;

    public PrescriptionFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_prescription_fragment, container, false);

        recetasRecyclerView = view.findViewById(R.id.recyclerView);
        recetasRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        recetaList = new ArrayList<>();
        recetaAdapter = new RecetaAdapter(recetaList);
        recetasRecyclerView.setAdapter(recetaAdapter);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("recetas")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        recetaList.clear();
                        QuerySnapshot querySnapshot = task.getResult();
                        if (querySnapshot != null) {
                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                Receta receta = document.toObject(Receta.class);
                                recetaList.add(receta);
                            }
                        }
                        recetaAdapter.notifyDataSetChanged();
                    } else {
                        // Manejo de errores
                    }
                });

        return view;
    }
}