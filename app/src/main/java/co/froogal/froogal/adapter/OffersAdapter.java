package co.froogal.froogal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import co.froogal.froogal.R;
import co.froogal.froogal.util.basic_utils;

public class OffersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 1;

    private static final int TYPE_BOGO = 2;
    private static final int TYPE_DISCOUNT = 3;
    private static final int TYPE_REWARDS = 4;
    private static final int TYPE_LOYALTY = 5;





    basic_utils bu;


    private List<OfferInfo> OfferList;

    public OffersAdapter(List<OfferInfo> OfferList) {
        super();
        this.OfferList = OfferList;

    }

    public void addItems(List<OfferInfo> list) {
        OfferList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_header, viewGroup, false);
            return new OfferHeaderViewHolder(view);
        }
        else if (viewType == TYPE_BOGO) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.offer_card_item, viewGroup, false);
            return new bogoOfferViewHolder(view);
        }
        else if (viewType == TYPE_DISCOUNT) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.offer_card_item, viewGroup, false);
            return new discountOfferViewHolder(view);
        }
        else if (viewType == TYPE_REWARDS) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.offer_card_item, viewGroup, false);
            return new rewardOfferViewHolder(view);
        }

        else if (viewType == TYPE_LOYALTY) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.offer_card_item, viewGroup, false);
            return new loyaltyOfferViewHolder(view);
        }



        throw new RuntimeException("Invalid view type " + viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {



        if (position > 1) {

            if(OfferList.get(position-1).type.equals("bogo")){
                bogoOfferViewHolder bogoOfferViewHolder = (bogoOfferViewHolder) viewHolder;
                OfferInfo ci = OfferList.get(position-1);
                String text = "Buy "+ ci.buy + " Get" + ci.get;
                bogoOfferViewHolder.offer.setText(text);

            }
            else if(OfferList.get(position-1).type.equals("rewards")){
                rewardOfferViewHolder rewardsOfferViewHolder = (rewardOfferViewHolder) viewHolder;
                OfferInfo ci = OfferList.get(position-1);
                String text = "Get " + ci.rewardPerecentage + "On Your Bill";
                rewardsOfferViewHolder.offer.setText(text);

            }
            else if(OfferList.get(position-1).type.equals("discount")){
                discountOfferViewHolder discountOfferViewHolder = (discountOfferViewHolder) viewHolder;
                OfferInfo ci = OfferList.get(position-1);
                String text = "Get " + ci.discount + " discount  On "+ ci.productID;
                discountOfferViewHolder.offer.setText(text);

            }
            else if(OfferList.get(position-1).type.equals("loyaltyCard")){

                loyaltyOfferViewHolder loyaltyCardOfferViewHolder = (loyaltyOfferViewHolder) viewHolder;
                OfferInfo ci = OfferList.get(position-1);
                String text = ci.loyaltyCardID;
                loyaltyCardOfferViewHolder.offer.setText(text);
            }


        }


        }



    @Override
    public int getItemCount() {
        return OfferList == null ? 1 : OfferList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else if (OfferList.get(position - 1).type.equals("bogo")) {
            return TYPE_BOGO;
        }
        else if (OfferList.get(position-1).type.equals("discount")) {

            return TYPE_DISCOUNT;
        }
        else if (OfferList.get(position-1).type.equals("rewards")) {
            return TYPE_REWARDS;

        }

        return TYPE_LOYALTY;

    }


    private static class OfferHeaderViewHolder extends RecyclerView.ViewHolder {

        public OfferHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public class bogoOfferViewHolder extends RecyclerView.ViewHolder {

        protected TextView offer;

        public bogoOfferViewHolder(View v) {
            super(v);
            offer =  (TextView) v.findViewById(R.id.OfferTextView);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }

    public class discountOfferViewHolder extends RecyclerView.ViewHolder {

        protected TextView offer;

        public discountOfferViewHolder(View v) {
            super(v);
            offer =  (TextView) v.findViewById(R.id.OfferTextView);
            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }

    public class rewardOfferViewHolder extends RecyclerView.ViewHolder {

        protected TextView offer;

        public rewardOfferViewHolder(View v) {
            super(v);
            offer =  (TextView) v.findViewById(R.id.OfferTextView);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }
    public class loyaltyOfferViewHolder extends RecyclerView.ViewHolder {

        protected TextView offer;

        public loyaltyOfferViewHolder(View v) {
            super(v);
            offer =  (TextView) v.findViewById(R.id.OfferTextView);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }



}

