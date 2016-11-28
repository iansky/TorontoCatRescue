package com.toddburgessmedia.torontocatrescue;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.toddburgessmedia.torontocatrescue.data.Pet;
import com.toddburgessmedia.torontocatrescue.model.PetListModel;
import com.toddburgessmedia.torontocatrescue.view.RecyclerViewPetListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 21/11/16.
 */

public class MainFragment extends Fragment {

    @BindView(R.id.tcr_fragment_rv)
    RecyclerView rv;

    @BindView(R.id.tcr_fragment_swiperefresh)
    SwipeRefreshLayout swipe;

    RecyclerViewPetListAdapter adapter;
    ArrayList<Pet> petList;

    @Inject
    PetListModel petListModel;

    @Override
    public void onStart() {
        super.onStart();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((TorontoCatRescue) getActivity().getApplication()).getTcrComponent().inject(this);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.tcrmain_fragment, container, false);
        ButterKnife.bind(this,view);

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setHasFixedSize(true);

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                petListModel.fetchPetList();
            }
        });

        petListModel.fetchPetList();

        return view;

    }

    @Subscribe
    public void updatePetListView(PetListModel.PetListMessage message) {

        petList = message.getPets();
        adapter = new RecyclerViewPetListAdapter(getContext(), petList);
        swipe.setRefreshing(false);

        rv.setAdapter(adapter);
    }

    public void getPetsbyAge(String age) {

        if (petList == null) {
            return;
        }
        rv.invalidate();
        adapter.updateList(getPetsByAge(age));
    }

    public void getPetsbySex(String sex) {

        if (petList == null) {
            return;
        }
        rv.invalidate();
        adapter.updateList(getPetsBySex(sex));
    }


    private ArrayList<Pet> getPetsByAge(String age) {

        ArrayList<Pet> newList = new ArrayList<>();
        Pet pet;
        for (int i = 0; i < petList.size(); i++) {
            pet = petList.get(i);
            if (pet.getAge().equals(age.toLowerCase())) {
                newList.add(pet);
            } else if (age.equals("Any Age")) {
                newList.add(pet);
            }

        }
        return newList;
    }

    private ArrayList<Pet> getPetsBySex(String sex) {

        Log.d("TCR", "getPetsBySex: " + sex);
        ArrayList<Pet> newList = new ArrayList<>();
        String newSex = sex.toLowerCase().substring(0,1);
        Pet pet;
        for (int i = 0; i < petList.size(); i++) {
            pet = petList.get(i);
            Log.d("TCR", "getPetsBySex: "+ pet.getSex() + " / " + newSex);
            if (pet.getSex().equals(newSex)) {
                newList.add(pet);
            } else if (sex.equals("Male and Female")) {
                newList.add(pet);
            }

        }
        return newList;
    }
}
