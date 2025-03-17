package com.example.philatelia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.philatelia.R;
import com.example.philatelia.models.Stamp;
import com.example.philatelia.models.StampSet;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private Context context;
    private List<Object> cartItems;

    public CartAdapter(Context context, List<Object> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object item = cartItems.get(position);

        if (item instanceof Stamp) {
            Stamp stamp = (Stamp) item;
            holder.title.setText(stamp.getName());
            holder.price.setText(stamp.getPrice() + " BYN");
            Glide.with(context).load(stamp.getImage()).into(holder.image);
        } else if (item instanceof StampSet) {
            StampSet set = (StampSet) item;
            holder.title.setText(set.getName());
            holder.price.setText(set.getPrice() + " BYN");
            Glide.with(context).load(set.getImage()).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvCartItemName);
            price = itemView.findViewById(R.id.tvCartItemPrice);
            image = itemView.findViewById(R.id.ivCartItem);
        }
    }
}

