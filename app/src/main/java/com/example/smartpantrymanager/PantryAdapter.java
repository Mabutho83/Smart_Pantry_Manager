package com.example.smartpantrymanager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import model.PantryItem;


/** Binds the list of pantry items to item_pantry.xml rows. */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface Listener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private final List<PantryItem> items = new ArrayList<>();
    private final Listener listener;
    private boolean highlightExpiring = true;

    public PantryAdapter(Listener listener) {
        this.listener = listener;
    }

    public void setData(List<PantryItem> newItems, boolean highlightExpiring) {
        items.clear();
        items.addAll(newItems);
        this.highlightExpiring = highlightExpiring;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvQty.setText(Units.format(item.getQuantity()) + " " + item.getUnit());

        boolean hasExpiry = item.getExpiry() != null && !item.getExpiry().isEmpty();
        if (!hasExpiry) {
            holder.tvExpiry.setVisibility(View.GONE);
        } else {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            if (highlightExpiring && item.isExpiringSoon(3)) {
                holder.tvExpiry.setText("Expires: " + item.getExpiry() + "  (expiring soon!)");
                holder.tvExpiry.setTextColor(Color.RED);
            } else {
                holder.tvExpiry.setText("Expires: " + item.getExpiry());
                holder.tvExpiry.setTextColor(Color.GRAY);
            }
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvExpiry;
        ImageButton btnDelete;

        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvItemName);
            tvQty = v.findViewById(R.id.tvItemQuantity);
            tvExpiry = v.findViewById(R.id.tvItemExpiry);
            btnDelete = v.findViewById(R.id.btnItemDelete);
        }
    }
}
