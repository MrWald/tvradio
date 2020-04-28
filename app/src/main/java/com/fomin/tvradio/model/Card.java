package com.fomin.tvradio.model;

import com.google.gson.annotations.SerializedName;

public class Card {

    @SerializedName("title")
    private String mTitle = null;
    @SerializedName("localImageResource")
    private String mLocalImageResource = null;
    @SerializedName("stream")
    private String mStream = null;
    @SerializedName("type")
    private Card.Type mType = null;
    @SerializedName("position")
    private int mPosition;

    public String getTitle() {
        return mTitle;
    }

    public Type getType() {
        return mType;
    }

    public int getPosition(){
        return mPosition;
    }

    public String getStream() {
        return mStream;
    }

    public String getLocalImageResourceName() {
        return mLocalImageResource;
    }

    public enum Type {
        RADIO_ITEM,
        EXIT_ITEM
    }
}
