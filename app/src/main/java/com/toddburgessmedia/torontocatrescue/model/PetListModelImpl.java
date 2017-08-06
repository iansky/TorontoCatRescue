package com.toddburgessmedia.torontocatrescue.model;

import android.os.Parcelable;

import com.toddburgessmedia.torontocatrescue.data.Pet;
import com.toddburgessmedia.torontocatrescue.data.PetList;
import com.toddburgessmedia.torontocatrescue.presenter.PetListPresenter;

import java.util.ArrayList;

import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Single;
import rx.SingleSubscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 21/11/16.
 */

public class PetListModelImpl implements PetListModel {

    Retrofit retrofit;

    int start = 1;
    int end = 700;

    String apikey;
    String shelterID;

    PetList petList;

    PetListPresenter presenter;

    public PetListModelImpl(Retrofit retrofit, String apikey, String shelterID) {

        this.retrofit = retrofit;
        this.apikey = apikey;
        this.shelterID = shelterID;
    }

    @Override
    public void setPresenter(PetListPresenter presenter) {
        this.presenter = presenter;
    }

    public void fetchPetList() {

        PetListAPI petListAPI = retrofit.create(PetListAPI.class);
        Single<Response<PetList>> petListSingle;
        petListSingle = petListAPI.getAllPets(apikey, shelterID, start, end);

        petListSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Response<PetList>>() {
                    @Override
                    public void onSuccess(Response<PetList> value) {
                        petList = value.body();
                        petList.sortPetList();
                        presenter.updatePetList();
                    }

                    @Override
                    public void onError(Throwable e) {

                        presenter.onError();
                    }
                });

    }

    @Override
    public Parcelable getPetListParcelable() {

        return (Parcelable) petList;
    }

    @Override
    public void setPetListParcelable(Parcelable petListParcelable) {

        if (petListParcelable instanceof PetList) {
            petList = (PetList) petListParcelable;
        }
    }

    public boolean isPetListEmpty () {
        if (petList == null) {
            return true;
        } else {
            return false;
        }
    }

    public PetList getPetsbySexAge(String sex, String age)  {

        ArrayList<Pet> newList = new ArrayList<>();
        ArrayList<Pet> workList = petList.getPetList();

        String newSex = sex.toLowerCase().substring(0,1);
        Pet pet;
        for (int i = 0; i < workList.size(); i++) {
            pet = workList.get(i);
            if (sex.equals("Any Gender") && age.equals("Any Age")) {
                newList.add(pet);
            } else if (pet.getSex().equals(newSex) && pet.getAge().equalsIgnoreCase(age)) {
                newList.add(pet);
            } else if ((pet.getSex().equals(newSex)) && age.equals("Any Age")) {
                newList.add(pet);
            } else if (sex.equals("Any Gender") && pet.getAge().equalsIgnoreCase(age)) {
                newList.add(pet);
            }
        }

        PetList pets = new PetList();
        pets.setPetList(newList);

        return pets;
    }


    public PetList getPetList() {
        return petList;
    }

}


