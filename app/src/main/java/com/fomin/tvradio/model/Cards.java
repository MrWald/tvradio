package com.fomin.tvradio.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Cards {
    @SerializedName("cards")
    private List<Card> mCards = null;

    public List<Card> getCards() {
        return mCards;
    }
}
