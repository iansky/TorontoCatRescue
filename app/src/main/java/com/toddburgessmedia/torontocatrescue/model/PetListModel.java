package com.toddburgessmedia.torontocatrescue.model;

import com.toddburgessmedia.torontocatrescue.data.LimitedPet;
import com.toddburgessmedia.torontocatrescue.data.LimitedPetDetail;
import com.toddburgessmedia.torontocatrescue.data.Pet;
import com.toddburgessmedia.torontocatrescue.data.PetDetail;
import com.toddburgessmedia.torontocatrescue.data.PetDetailInfo;
import com.toddburgessmedia.torontocatrescue.data.PetList;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import retrofit2.Response;
import retrofit2.Retrofit;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 21/11/16.
 */

public class PetListModel {

    Retrofit retrofit;

    int start = 1;
    int end = 700;

    String apikey;
    String shelterID;

    PetList petList;
    private PetDetail petDetail;
    private LimitedPet limitedPet;

    public PetListModel (Retrofit retrofit, String apikey, String shelterID) {

        this.retrofit = retrofit;
        this.apikey = apikey;
        this.shelterID = shelterID;
    }

    public void fetchPetList() {

        PetListAPI petListAPI = retrofit.create(PetListAPI.class);
        Observable<Response<PetList>> petListObservable;
        petListObservable = petListAPI.getAllPets(apikey, shelterID, start, end);

        petListObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<PetList>>() {
                    @Override
                    public void onCompleted() {
                        petList.sortPetList();
                        EventBus.getDefault().postSticky(new PetListMessage(petList.getPetList()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(e);
                    }

                    @Override
                    public void onNext(Response<PetList> petListResponse) {

                        if (petListResponse.isSuccessful()) {
                            petList = petListResponse.body();
                        }
                    }
                });

    }

    public void fetchPetListSingle () {

        PetListAPI petListAPI = retrofit.create(PetListAPI.class);
        Single<Response<PetList>> petListSingle;
        petListSingle = petListAPI.getAllPetsSingle(apikey, shelterID, start, end);

        petListSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Response<PetList>>() {
                    @Override
                    public void onSuccess(Response<PetList> value) {
                        petList = value.body();
                        petList.sortPetList();
                        EventBus.getDefault().postSticky(new PetListMessage(petList.getPetList()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(e);
                    }
                });

    }

    public void fetchPetDetail(String petID) {

        PetDetailAPI petDetailAPI = retrofit.create(PetDetailAPI.class);
        Observable<Response<PetDetail>> petDetailObservable;
        petDetailObservable = petDetailAPI.getPetDetail(petID, apikey, shelterID);

        petDetailObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<PetDetail>>() {
                    @Override
                    public void onCompleted() {
                        EventBus.getDefault().post(new PetDetailMessage(petDetail.getPetDetailInfo()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(e);
                    }

                    @Override
                    public void onNext(Response<PetDetail> petDetailResponse) {
                        if (petDetailResponse.isSuccessful()) {
                            petDetail = petDetailResponse.body();
                        }
                    }
                });
    }

    public void fetchPetDetailSingle(String petID) {

        PetDetailAPI petDetailAPI = retrofit.create(PetDetailAPI.class);
        Single<Response<PetDetail>> petDetailSingle;
        petDetailSingle = petDetailAPI.getPetDetailSingle(petID, apikey, shelterID);

        petDetailSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Response<PetDetail>>() {
                    @Override
                    public void onSuccess(Response<PetDetail> value) {
                        petDetail = value.body();
                        EventBus.getDefault().post(new PetDetailMessage(petDetail.getPetDetailInfo()));
                    }

                    @Override
                    public void onError(Throwable e) {
                        EventBus.getDefault().post(e);
                    }
                });

    }



    public void fetchLimtedPetDetail(String petID,final boolean flag) {

        PetDetailAPI petDetailAPI = retrofit.create(PetDetailAPI.class);
        final Observable<Response<LimitedPet>> limitedObservable;
        limitedObservable = petDetailAPI.getLimitedPetDetail(petID,apikey, shelterID);

        limitedObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<LimitedPet>>() {
                    @Override
                    public void onCompleted() {
                        EventBus.getDefault().post(new LimitedPetDetailMessage(limitedPet.getLimitedPetDetail(),flag));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Response<LimitedPet> petResponse) {

                        if (petResponse.isSuccessful()) {
                            limitedPet = petResponse.body();
                        }
                    }
                });

    }

    public void fetchLimtedPetDetailSingle(String petID,final boolean flag) {

        PetDetailAPI petDetailAPI = retrofit.create(PetDetailAPI.class);
        Single<Response<LimitedPet>> limitedSingle;
        limitedSingle = petDetailAPI.getLimitedPetDetailSingle(petID, apikey, shelterID);

        limitedSingle.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<Response<LimitedPet>>() {
                    @Override
                    public void onSuccess(Response<LimitedPet> value) {
                        limitedPet = value.body();
                        EventBus.getDefault().post(new LimitedPetDetailMessage(limitedPet.getLimitedPetDetail(),flag));
                    }

                    @Override
                    public void onError(Throwable error) {

                    }
                });

    }



        public class PetListMessage {

        ArrayList<Pet> pets;

        PetListMessage (ArrayList<Pet> pets) {
            this.pets = pets;
        }

        public ArrayList<Pet> getPets() {
            return pets;
        }
    }

    public class PetDetailMessage {

        PetDetailInfo petDetail;

        PetDetailMessage(PetDetailInfo petDetail) {
            this.petDetail = petDetail;
        }

        public PetDetailInfo getPetDetail() {
            return petDetail;
        }
    }

    public class LimitedPetDetailMessage {

        LimitedPetDetail limitedPetDetail;
        boolean flag;

        LimitedPetDetailMessage(LimitedPetDetail limitedPetDetail,boolean flag) {
            this.limitedPetDetail = limitedPetDetail;
            this.flag = flag;
        }

        public LimitedPetDetail getLimitedPetDetail() {
            return limitedPetDetail;
        }

        public boolean getFlag() {
            return flag;
        }
    }
}
