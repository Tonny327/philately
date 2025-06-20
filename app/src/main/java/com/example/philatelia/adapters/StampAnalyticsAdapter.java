package com.example.philatelia.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.philatelia.R;
import com.example.philatelia.models.StampAnalytics;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class StampAnalyticsAdapter extends RecyclerView.Adapter<StampAnalyticsAdapter.ViewHolder> {

    private List<StampAnalytics> analyticsList;

    public StampAnalyticsAdapter(List<StampAnalytics> analyticsList) {
        this.analyticsList = analyticsList;
    }
    
    public void updateData(List<StampAnalytics> newAnalyticsList) {
        this.analyticsList.clear();
        this.analyticsList.addAll(newAnalyticsList);
        
        // Сортировка по годам в обратном порядке (с 2022 по 2001)
        Collections.sort(this.analyticsList, new Comparator<StampAnalytics>() {
            @Override
            public int compare(StampAnalytics a1, StampAnalytics a2) {
                return Integer.compare(a2.getYear(), a1.getYear()); // Обратная сортировка
            }
        });
        
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stamp_analytics, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StampAnalytics analytics = analyticsList.get(position);
        holder.bind(analytics);
    }

    @Override
    public int getItemCount() {
        return analyticsList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvYear;
        TextView tvPollCount;
        TextView tvFirstPlace;
        TextView tvSecondPlace;
        TextView tvThirdPlace;

        ViewHolder(View itemView) {
            super(itemView);
            tvYear = itemView.findViewById(R.id.tv_year);
            tvPollCount = itemView.findViewById(R.id.tv_poll_count);
            tvFirstPlace = itemView.findViewById(R.id.tv_first_place);
            tvSecondPlace = itemView.findViewById(R.id.tv_second_place);
            tvThirdPlace = itemView.findViewById(R.id.tv_third_place);
        }

        void bind(StampAnalytics analytics) {
            Context context = itemView.getContext();
            tvYear.setText(String.valueOf(analytics.getYear()));
            tvPollCount.setText(context.getString(R.string.poll_count_format, analytics.getPollCount()));

            tvFirstPlace.setText(formatPlaceTextShort(context, analytics.getFirstPlace()));
            tvSecondPlace.setText(formatPlaceTextShort(context, analytics.getSecondPlace()));
            tvThirdPlace.setText(formatPlaceTextShort(context, analytics.getThirdPlace()));
        }

        private String formatPlaceTextShort(Context context, StampAnalytics.Place place) {
            if (place == null) {
                return "Нет данных";
            }
            
            // Укорачиваем название, если оно слишком длинное
            String name = place.getName();
            if (name.length() > 60) {
                name = name.substring(0, 57) + "...";
            }
            
            return String.format(Locale.getDefault(), "%s (%d) — %s", 
                    name, place.getVotes(), place.getAuthor());
        }
    }
} 