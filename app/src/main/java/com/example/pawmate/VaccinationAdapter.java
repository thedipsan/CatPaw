package com.example.pawmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

public class VaccinationAdapter
        extends RecyclerView.Adapter<VaccinationAdapter.ViewHolder> {

    private List<Vaccination> vaccinationList;

    private OnVaccinationActionListener listener;


    public interface OnVaccinationActionListener {

        void onEdit(Vaccination vaccination);

        void onDelete(Vaccination vaccination);
    }


    public VaccinationAdapter(
            List<Vaccination> vaccinationList,
            OnVaccinationActionListener listener) {

        this.vaccinationList = vaccinationList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view =
                LayoutInflater.from(parent.getContext())
                        .inflate(
                                R.layout.item_vaccination,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        Vaccination vaccination =
                vaccinationList.get(position);


        holder.tvVaccineName.setText(
                vaccination.getName()
        );


        holder.tvVaccineDate.setText(
                "Given: " +
                        vaccination.getDate()
        );


        holder.tvNextDate.setText(
                "Next vaccination: " +
                        vaccination.getNextDate()
        );


        holder.tvNotes.setText(
                vaccination.getNotes()
        );


        // Edit button

        holder.btnEdit.setOnClickListener(v -> {

            if (listener != null) {

                listener.onEdit(vaccination);
            }
        });


        // Delete button

        holder.btnDelete.setOnClickListener(v -> {

            if (listener != null) {

                listener.onDelete(vaccination);
            }
        });
    }


    @Override
    public int getItemCount() {

        return vaccinationList.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvVaccineName;
        TextView tvVaccineDate;
        TextView tvNextDate;
        TextView tvNotes;

        MaterialButton btnEdit;
        MaterialButton btnDelete;


        public ViewHolder(@NonNull View itemView) {

            super(itemView);


            tvVaccineName =
                    itemView.findViewById(
                            R.id.tvVaccineName
                    );


            tvVaccineDate =
                    itemView.findViewById(
                            R.id.tvVaccineDate
                    );


            tvNextDate =
                    itemView.findViewById(
                            R.id.tvNextDate
                    );


            tvNotes =
                    itemView.findViewById(
                            R.id.tvNotes
                    );


            btnEdit =
                    itemView.findViewById(
                            R.id.btnEdit
                    );


            btnDelete =
                    itemView.findViewById(
                            R.id.btnDelete
                    );
        }
    }
}