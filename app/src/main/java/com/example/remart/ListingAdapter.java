package com.example.remart;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

public class ListingAdapter extends FirestoreRecyclerAdapter<Listings, ListingAdapter.ListingHolder>{


    private OnItemClickListener listener;

    public ListingAdapter(@NonNull FirestoreRecyclerOptions<Listings> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ListingHolder holder, int position, @NonNull Listings model) {
        holder.textViewName.setText(model.getListingTitle());
        holder.textViewDescription.setText(model.getDescription());
    }

    @NonNull
    @Override
    public ListingHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.listing_list, viewGroup, false);
        return new ListingHolder(v);
    }

    class ListingHolder extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewDescription;

        public ListingHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.task_view_name);
            textViewDescription = itemView.findViewById(R.id.task_view_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION && listener != null){
                        listener.onItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }


}
