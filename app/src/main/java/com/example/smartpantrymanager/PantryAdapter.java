package com.example.smartpantrymanager;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.*;

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
        Context ctx = holder.itemView.getContext();
        PantryItem item = items.get(position);

        String name = item.getName();
        holder.tvInitial.setText(name.isEmpty() ? "?" : name.substring(0, 1).toUpperCase(Locale.getDefault()));
        holder.tvName.setText(name);
        holder.tvQty.setText(Units.format(item.getQuantity()) + " " + item.getUnit());

        boolean hasExpiry = item.getExpiry() != null && !item.getExpiry().isEmpty();
        if (!hasExpiry) {
            holder.tvExpiry.setVisibility(View.GONE);
        } else {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            boolean soon = highlightExpiring && item.isExpiringSoon(3);
            int colour = ContextCompat.getColor(ctx, soon ? R.color.sp_danger : R.color.sp_text_secondary);
            holder.tvExpiry.setText(soon ? "Use soon \u00B7 " + item.getExpiry() : "Expires " + item.getExpiry());
            holder.tvExpiry.setTextColor(colour);
            holder.tvExpiry.setCompoundDrawableTintList(ColorStateList.valueOf(colour));
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitial, tvName, tvQty, tvExpiry;
        ImageButton btnDelete;

        ViewHolder(View v) {
            super(v);
            tvInitial = v.findViewById(R.id.tvItemInitial);
            tvName = v.findViewById(R.id.tvItemName);
            tvQty = v.findViewById(R.id.tvItemQuantity);
            tvExpiry = v.findViewById(R.id.tvItemExpiry);
            btnDelete = v.findViewById(R.id.btnItemDelete);
        }
    }
}
