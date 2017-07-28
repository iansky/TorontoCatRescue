package com.toddburgessmedia.torontocatrescue.model;

import com.toddburgessmedia.torontocatrescue.data.LimitedPet;
import com.toddburgessmedia.torontocatrescue.data.LimitedPetDetail;
import com.toddburgessmedia.torontocatrescue.data.PetDetail;
import com.toddburgessmedia.torontocatrescue.data.PetDetailInfo;
import com.toddburgessmedia.torontocatrescue.data.PetList;
import com.toddburgessmedia.torontocatrescue.presenter.PetListPresenter;
import com.toddburgessmedia.torontocatrescue.view.PetListView;

import org.greenrobot.eventbus.EventBus;

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
    private PetDetail petDetail;
    private LimitedPet limitedPet;

    PetListView petListView;
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

    public void fetchPetDetail(String petID) {

        PetDetailAPI petDetailAPI = retrofit.create(PetDetailAPI.class);
        Single<Response<PetDetail>> petDetailSingle;
        petDetailSingle = petDetailAPI.getPetDetail(petID, apikey, shelterID);

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

    public void fetchLimtedPetDetail(String petID, final boolean flag) {

        PetDetailAPI petDetailAPI = retrofit.create(PetDetailAPI.class);
        Single<Response<LimitedPet>> limitedSingle;
        limitedSingle = petDetailAPI.getLimitedPetDetail(petID, apikey, shelterID);

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

    public PetList getPetList() {
        return petList;
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


