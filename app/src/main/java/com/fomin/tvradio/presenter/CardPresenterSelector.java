package com.fomin.tvradio.presenter;

import android.content.Context;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.PresenterSelector;

import java.util.HashMap;

import com.fomin.tvradio.fragment.RadioFragment;
import com.fomin.tvradio.model.Card;

public class CardPresenterSelector extends PresenterSelector {

    private final Context mContext;
    private final HashMap<Card.Type, Presenter> presenters = new HashMap<>();
    private RadioFragment mFragment;

    public CardPresenterSelector(Context context, RadioFragment fragment) {
        mContext = context;
        mFragment = fragment;
    }

    @Override
    public Presenter getPresenter(Object item) {
        if (!(item instanceof Card)) throw new RuntimeException(
                String.format("The PresenterSelector only supports data items of type '%s'",
                        Card.class.getName()));
        Card card = (Card) item;
        Presenter presenter = presenters.get(card.getType());
        if (presenter == null) {
            presenter = new ImageCardViewPresenter(mContext, mFragment);
            presenters.put(card.getType(), presenter);
        }
        return presenter;
    }
}
