package com.barbrdo.app.dataobject;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SendInviteInput {
    @SerializedName("invite_as")
    @Expose
    public String inviteAs;
    @SerializedName("referee_phone_number")
    @Expose
    public String refereePhoneNumber;
    @SerializedName("referee_email")
    @Expose
    public String refereeEmail;
}