package com.dicoding.nearbymessageapi;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sidiqpermana on 10/27/16.
 */

public class NearbyMessage implements Parcelable {
    private String type;
    private String message;
    private String senderEmail;
    private String receiverEmail;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getReceiverEmail() {
        return receiverEmail;
    }

    public void setReceiverEmail(String receiverEmail) {
        this.receiverEmail = receiverEmail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.type);
        dest.writeString(this.message);
        dest.writeString(this.senderEmail);
        dest.writeString(this.receiverEmail);
    }

    public NearbyMessage() {
    }

    protected NearbyMessage(Parcel in) {
        this.type = in.readString();
        this.message = in.readString();
        this.senderEmail = in.readString();
        this.receiverEmail = in.readString();
    }

    public static final Parcelable.Creator<NearbyMessage> CREATOR = new Parcelable.Creator<NearbyMessage>() {
        @Override
        public NearbyMessage createFromParcel(Parcel source) {
            return new NearbyMessage(source);
        }

        @Override
        public NearbyMessage[] newArray(int size) {
            return new NearbyMessage[size];
        }
    };


}
