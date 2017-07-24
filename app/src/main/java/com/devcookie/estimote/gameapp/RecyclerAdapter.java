package com.devcookie.estimote.gameapp;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private List<FoundBeacon> mFoundBeacon;
    private Context mContext;

    public RecyclerAdapter(Context context, List<FoundBeacon> foundBeacons) {
        mFoundBeacon = foundBeacons;
        mContext = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(RecyclerAdapter.ViewHolder holder, int position) {
        CardView card = holder.cardView;

        FoundBeacon foundBeacon = mFoundBeacon.get(position);

        TextView textView = (TextView) card.findViewById(R.id.text_view);

        textView.setText(foundBeacon.timestamp + "\n Your clue: "
                + foundBeacon.message + "\n");

    }

    @Override
    public int getItemCount() {
        return mFoundBeacon.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;

        }

    }

}
