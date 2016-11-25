package com.toddburgessmedia.torontocatrescue.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 25/11/16.
 */

public class LimitedPetDetail {

    @SerializedName("pet_id")
    String petID;

    @SerializedName("pet_name")
    String petName;

    @SerializedName("images")
    ArrayList<PetDetailImage> images;

    public ArrayList<PetDetailImage> getImages() {
        return images;
    }

    public void setImages(ArrayList<PetDetailImage> images) {
        this.images = images;
    }

    public String getPetID() {
        return petID;
    }

    public void setPetID(String petID) {
        this.petID = petID;
    }

    public String getPetName() {
        return petName;
    }

    public void setPetName(String petName) {
        this.petName = petName;
    }
}
