package com.example.pawmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class MedicalRecordAdapter
        extends RecyclerView.Adapter<MedicalRecordAdapter.ViewHolder> {


    private ArrayList<MedicalRecord> recordList;

    private OnMedicalRecordActionListener listener;


    public interface OnMedicalRecordActionListener {

        void onEdit(MedicalRecord record);

        void onDelete(MedicalRecord record);
    }


    public MedicalRecordAdapter(
            ArrayList<MedicalRecord> recordList,
            OnMedicalRecordActionListener listener) {

        this.recordList = recordList;
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
                                R.layout.medical_record_item,
                                parent,
                                false
                        );

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        MedicalRecord record =
                recordList.get(position);


        // Diagnosis

        holder.tvDiagnosis.setText(
                record.getDiagnosis()
        );


        // Date

        holder.tvDate.setText(
                "Date: " + record.getDate()
        );


        // Vet

        holder.tvVetName.setText(
                "Veterinarian: " +
                        record.getVetName()
        );


        // Notes

        String notes = record.getNotes();

        if (notes == null || notes.isEmpty()) {

            holder.tvNotes.setVisibility(
                    View.GONE
            );

        } else {

            holder.tvNotes.setVisibility(
                    View.VISIBLE
            );

            holder.tvNotes.setText(notes);
        }


        // Edit

        holder.btnEdit.setOnClickListener(v -> {

            if (listener != null) {

                listener.onEdit(record);
            }
        });


        // Delete

        holder.btnDelete.setOnClickListener(v -> {

            if (listener != null) {

                listener.onDelete(record);
            }
        });
    }


    @Override
    public int getItemCount() {

        return recordList.size();
    }


    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView tvDiagnosis;
        TextView tvDate;
        TextView tvVetName;
        TextView tvNotes;

        MaterialButton btnEdit;
        MaterialButton btnDelete;


        public ViewHolder(
                @NonNull View itemView) {

            super(itemView);


            tvDiagnosis =
                    itemView.findViewById(
                            R.id.tvDiagnosis
                    );


            tvDate =
                    itemView.findViewById(
                            R.id.tvDate
                    );


            tvVetName =
                    itemView.findViewById(
                            R.id.tvVetName
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