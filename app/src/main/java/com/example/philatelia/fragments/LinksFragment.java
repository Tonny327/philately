package com.example.philatelia.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.philatelia.R;
import com.example.philatelia.adapters.LinksAdapter;
import com.example.philatelia.models.Link;
import java.util.ArrayList;
import java.util.List;

public class LinksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_links, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
        if (((AppCompatActivity) requireActivity()).getSupportActionBar() != null) {
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle("Полезные ссылки");
        }
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());


        RecyclerView recyclerView = view.findViewById(R.id.links_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Link> linksList = new ArrayList<>();
        linksList.add(new Link("Белпочта - Каталог марок", "https://shop.belpost.by/catalog/pochtovaya_produktsiya_/marki/"));
        linksList.add(new Link("Коллекция марок Беларуси", "https://belarussiancollection.com/stamps/"));

        LinksAdapter adapter = new LinksAdapter(getContext(), linksList);
        recyclerView.setAdapter(adapter);

        return view;
    }
} 