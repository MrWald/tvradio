package com.fomin.tvradio.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.leanback.app.VerticalGridSupportFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.FocusHighlight;
import androidx.leanback.widget.PresenterSelector;
import androidx.leanback.widget.VerticalGridPresenter;

import com.google.gson.Gson;

import com.fomin.tvradio.R;
import com.fomin.tvradio.Radio;
import com.fomin.tvradio.model.Card;
import com.fomin.tvradio.model.Cards;
import com.fomin.tvradio.presenter.CardPresenterSelector;
import com.fomin.tvradio.utils.Utils;

import java.util.List;

public class RadioFragment extends VerticalGridSupportFragment {

    public static final int COLUMNS = 2;
    private static final int ZOOM_FACTOR = FocusHighlight.ZOOM_FACTOR_MEDIUM;

    private ArrayObjectAdapter mAdapter;

    private int nRows;
    private Radio mRadio;

    private Cards mCards;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.grid_title));
        setupRowAdapter();
        setOnItemViewSelectedListener((itemViewHolder, item, rowViewHolder, row) -> {
            if(item == null)
                return;
            if (((Card)item).getPosition() < COLUMNS && ((Card)item).getType() != Card.Type.EXIT_ITEM){
                getTitleView().setVisibility(View.VISIBLE);
            } else {
                getTitleView().setVisibility(View.INVISIBLE);
            }
        });
        mRadio = Radio.getInstance(getActivity());
    }

    public int getRows(){
        return nRows;
    }

    private void setupRowAdapter() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter(ZOOM_FACTOR);
        gridPresenter.setNumberOfColumns(COLUMNS);
        setGridPresenter(gridPresenter);

        PresenterSelector cardPresenterSelector = new CardPresenterSelector(getActivity(), this);
        mAdapter = new ArrayObjectAdapter(cardPresenterSelector);
        setAdapter(mAdapter);

        prepareEntranceTransition();
        new Handler().postDelayed(() -> {
            createRows();
            startEntranceTransition();
        }, 1000);
    }

    private void createRows() {
        String json = Utils.inputStreamToString(getResources()
                .openRawResource(R.raw.grid_example));
        mCards = new Gson().fromJson(json, Cards.class);
        List<Card> row = mCards.getCards();
        nRows = row.size()/COLUMNS;
        mAdapter.addAll(0, row);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mCards != null && !mRadio.getStreamUrl().equals("")) {

            Card card = null;

            for (int i = 0; i < mCards.getCards().size(); i++) {
                if (mRadio.getStreamUrl().equals(mCards.getCards().get(i).getStream())) {
                    card = mCards.getCards().get(i);
                }
            }
            if (card != null) {
                mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
            }
        }
    }
}
