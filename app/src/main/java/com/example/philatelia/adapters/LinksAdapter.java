package com.example.philatelia.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.philatelia.R;
import com.example.philatelia.models.Link;
import java.util.List;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.LinkViewHolder> {

    private final List<Link> links;
    private final Context context;

    public LinksAdapter(Context context, List<Link> links) {
        this.context = context;
        this.links = links;
    }

    @NonNull
    @Override
    public LinkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_link, parent, false);
        return new LinkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LinkViewHolder holder, int position) {
        Link link = links.get(position);
        holder.title.setText(link.getTitle());
        holder.itemView.setOnClickListener(v -> {
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(link.getUrl()));
                context.startActivity(browserIntent);
            } catch (Exception e) {
                // Optionally handle exception, e.g. if no browser is available
            }
        });
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    static class LinkViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        LinkViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.link_title);
        }
    }
} 