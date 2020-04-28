package com.fomin.tvradio.presenter;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.Presenter;

import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.ViewGroup;

import com.fomin.tvradio.application.RadioApp;
import com.fomin.tvradio.fragment.RadioFragment;
import com.squareup.picasso.Picasso;

import com.fomin.tvradio.R;
import com.fomin.tvradio.RadioActivity;
import com.fomin.tvradio.Radio;
import com.fomin.tvradio.model.Card;

import org.jetbrains.annotations.NotNull;

import static android.view.KeyEvent.KEYCODE_DPAD_DOWN;
import static android.view.KeyEvent.KEYCODE_DPAD_LEFT;
import static android.view.KeyEvent.KEYCODE_DPAD_RIGHT;
import static android.view.KeyEvent.KEYCODE_DPAD_UP;

public class ImageCardViewPresenter extends Presenter {

    private final Context mContext;

    private Radio mSingleton;
    private RadioFragment mFragment;

    private ImageCardViewPresenter(Context context, int cardThemeResId, RadioFragment fragment) {

        mContext = new ContextThemeWrapper(context, cardThemeResId);
        mSingleton = Radio.getInstance(getContext());

        mFragment = fragment;
    }

    private Context getContext() {
        return mContext;
    }

    @NotNull
    @Override
    public final ViewHolder onCreateViewHolder(ViewGroup parent) {
        ImageCardView cardView = onCreateView();
        return new ViewHolder(cardView);
    }

    @Override
    public final void onBindViewHolder(@NotNull ViewHolder viewHolder, Object item) {
        Card card = (Card) item;
        onBindViewHolder(card, (ImageCardView) viewHolder.view);
    }

    @Override
    public final void onUnbindViewHolder(ViewHolder viewHolder) {
        //onUnbindViewHolder((ImageCardView) viewHolder.view);
    }

//    private void onUnbindViewHolder(ImageCardView cardView) {
//        // Nothing to clean up. Override if necessary.
//    }

    ImageCardViewPresenter(Context context, RadioFragment fragment) {
        this(context, R.style.DefaultCardTheme, fragment);
    }

    private ImageCardView onCreateView() {
        return new ImageCardView(getContext());
    }

    private void onBindViewHolder(final Card card, @NotNull final ImageCardView cardView) {
        cardView.setTag(card);
        cardView.setTitleText(card.getTitle());
        cardView.setContentText("");
        cardView.setInfoAreaBackgroundColor(ContextCompat.getColor(RadioApp.getAppContext(), R.color.card_color));

        if (card.getLocalImageResourceName() != null) {
            if (card.getType() == Card.Type.RADIO_ITEM) {
                Picasso.get().load(card.getLocalImageResourceName()).fit().into(cardView.getMainImageView());
            } else {
                int resourceId = getContext().getResources()
                        .getIdentifier(card.getLocalImageResourceName(),
                                "drawable", getContext().getPackageName());
                Picasso.get().load(resourceId).fit().into(cardView.getMainImageView());
            }
        }

        cardView.setOnClickListener(v -> {

            Intent intent;

            switch (card.getType()) {
                case RADIO_ITEM:
                    intent = new Intent(getContext(), RadioActivity.class);
                    intent.putExtra("EXTRA_STREAM", card.getStream());
                    intent.putExtra("EXTRA_ICON", card.getLocalImageResourceName());
                    intent.putExtra("EXTRA_TITLE", card.getTitle());
                    getContext().startActivity(intent);
                    break;
                case EXIT_ITEM:
                    mSingleton.closePlayer();
                    android.os.Process.killProcess(android.os.Process.myPid());
                    break;
            }
        });

        //Enhancing navigation
        if(card.getPosition() == 0) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_LEFT) {
                    mFragment.setSelectedPosition(RadioFragment.COLUMNS-1);
                    return true;
                } else if(keyCode == KEYCODE_DPAD_UP) {
                    mFragment.setSelectedPosition(RadioFragment.COLUMNS*(mFragment.getRows()-1));
                    return true;
                }
                return false;
            });
        } else if(card.getType() == Card.Type.EXIT_ITEM) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_RIGHT) {
                    mFragment.setSelectedPosition(card.getPosition()+1 - RadioFragment.COLUMNS);
                    return true;
                } else if(keyCode == KEYCODE_DPAD_DOWN) {
                    mFragment.setSelectedPosition(RadioFragment.COLUMNS-1);
                    return true;
                }
                return false;
            });
        } else if(card.getPosition() == RadioFragment.COLUMNS - 1) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_RIGHT) {
                    mFragment.setSelectedPosition(0);
                    return true;
                } else if(keyCode == KEYCODE_DPAD_UP) {
                    mFragment.setSelectedPosition(RadioFragment.COLUMNS*(mFragment.getRows()-1) + card.getPosition());
                    return true;
                }
                return false;
            });
        } else if(card.getPosition() == (mFragment.getRows()-1)*RadioFragment.COLUMNS) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_LEFT) {
                    mFragment.setSelectedPosition(card.getPosition()-1 + RadioFragment.COLUMNS);
                    return true;
                } else if(keyCode == KEYCODE_DPAD_DOWN) {
                    mFragment.setSelectedPosition(0);
                    return true;
                }
                return false;
            });
        } else if(card.getPosition()+1 % RadioFragment.COLUMNS == 0) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_RIGHT) {
                    mFragment.setSelectedPosition(card.getPosition()+1 - RadioFragment.COLUMNS);
                    return true;
                }
                return false;
            });
        } else if(card.getPosition()+1 % RadioFragment.COLUMNS == 1) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_LEFT) {
                    mFragment.setSelectedPosition(card.getPosition()-1 + RadioFragment.COLUMNS);
                    return true;
                }
                return false;
            });
        } else if(card.getPosition() < RadioFragment.COLUMNS) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_UP) {
                    mFragment.setSelectedPosition(RadioFragment.COLUMNS*(mFragment.getRows()-1) + card.getPosition());
                    return true;
                }
                return false;
            });
        } else if(card.getPosition() >= RadioFragment.COLUMNS*(mFragment.getRows()-1)) {
            cardView.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction()!=KeyEvent.ACTION_DOWN)
                    return true;
                if(keyCode == KEYCODE_DPAD_DOWN) {
                    mFragment.setSelectedPosition(card.getPosition()%RadioFragment.COLUMNS);
                    return true;
                }
                return false;
            });
        }

        if (mSingleton.getStreamUrl().equals(card.getStream())) {
            cardView.setContentText(getContext().getString(R.string.currently_playing));
            cardView.setInfoAreaBackgroundColor(ContextCompat.getColor(RadioApp.getAppContext(), R.color.playing_card_color));
        }
    }
}
