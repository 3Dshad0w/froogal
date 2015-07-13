package co.froogal.froogal.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import co.froogal.froogal.R;
import co.froogal.froogal.ResDetailsActivity;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.util.ImageUtil;
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
                    .inflate(R.layout.recycler_header, viewGroup, false);
            return new bogoOfferViewHolder(view);
        }
        else if (viewType == TYPE_DISCOUNT) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_header, viewGroup, false);
            return new discountOfferViewHolder(view);
        }
        else if (viewType == TYPE_REWARDS) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_header, viewGroup, false);
            return new rewardOfferViewHolder(view);
        }

        else if (viewType == TYPE_LOYALTY) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_header, viewGroup, false);
            return new loyaltyOfferViewHolder(view);
        }



        throw new RuntimeException("Invalid view type " + viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {



        if (position > 1) {
           /* //if(position - 1 != dontDisplay) {
                OfferViewHolder OfferViewHolder = (OfferViewHolder) viewHolder;
                OfferInfo ci = OfferList.get(position - 2);
                OfferViewHolder.userName.setText(ci.userName);
                ImageUtil.displayRoundImage(OfferViewHolder.userImage, ci.image_url, null);
                OfferViewHolder.date.setText(ci.date);
                OfferViewHolder.title.setText(ci.title);
                OfferViewHolder.description.setText(ci.description);
                OfferViewHolder.ratingbar.setRating(Float.parseFloat(ci.rating));
                if(cii.uid.equals(ci.uid)){
                    OfferViewHolder.editOffer.setVisibility(View.VISIBLE);
                    OfferViewHolder.editOffer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            showEditPopupWindow(v.getRootView(), v.getContext(), Float.valueOf(cii.rating), cii.title, cii.description);
                        }
                    });

                }
            */
        }

        else if(position == 1){
           /* if(isOfferPresent.equals("0")) {

                if(isOfferAdded.equals("0")) {
                    OfferRatingViewHolder ratingHolder = (OfferRatingViewHolder) viewHolder;
                    ratingHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        public void onRatingChanged(RatingBar ratingBar, float rating,
                                                    boolean fromUser) {

                            Toast.makeText(ratingBar.getContext(), String.valueOf(rating), Toast.LENGTH_LONG).show();
                            showPopupWindow(ratingBar.getRootView(), ratingBar.getContext(), rating);

                        }
                    });
                }


                else{
                    OfferRatingViewHolder ratingHolder = (OfferRatingViewHolder) viewHolder;
                    ratingHolder.itemView.setVisibility(View.GONE);
                    ratingHolder.ratingBar.setVisibility(View.GONE);
                    ratingHolder.cardView.setVisibility(View.GONE);
                }
            }
            else{
                        OfferViewHolder OfferViewHolder = (OfferViewHolder) viewHolder;
                        OfferViewHolder.userName.setText(cii.userName);
                        ImageUtil.displayRoundImage(OfferViewHolder.userImage, cii.image_url, null);
                        OfferViewHolder.date.setText(cii.date);
                        OfferViewHolder.title.setText(cii.title);
                        OfferViewHolder.description.setText(cii.description);
                        OfferViewHolder.ratingbar.setRating(Float.parseFloat(cii.rating));
                        OfferViewHolder.editOffer.setVisibility(View.VISIBLE);
                        OfferViewHolder.editOffer.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                showEditPopupWindow(v.getRootView(), v.getContext(), Float.valueOf(cii.rating), cii.title, cii.description);
                            }
                        });




            }
*/
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

        protected TextView userName;
        protected TextView date;
        protected TextView title;
        protected TextView description;
        protected RatingBar ratingbar;
        protected ImageView userImage;
        protected TextView editOffer;

        public bogoOfferViewHolder(View v) {
            super(v);
            userName =  (TextView) v.findViewById(R.id.username);
            userImage = (ImageView)  v.findViewById(R.id.itemImage);
            date =  (TextView) v.findViewById(R.id.date);
            title =  (TextView) v.findViewById(R.id.title);
            description =  (TextView) v.findViewById(R.id.description);
            ratingbar = (RatingBar) v.findViewById(R.id.ratingBarIndicator);
            editOffer.setVisibility(View.INVISIBLE);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }

    public class discountOfferViewHolder extends RecyclerView.ViewHolder {

        protected TextView userName;
        protected TextView date;
        protected TextView title;
        protected TextView description;
        protected RatingBar ratingbar;
        protected ImageView userImage;
        protected TextView editOffer;

        public discountOfferViewHolder(View v) {
            super(v);
            userName =  (TextView) v.findViewById(R.id.username);
            userImage = (ImageView)  v.findViewById(R.id.itemImage);
            date =  (TextView) v.findViewById(R.id.date);
            title =  (TextView) v.findViewById(R.id.title);
            description =  (TextView) v.findViewById(R.id.description);
            ratingbar = (RatingBar) v.findViewById(R.id.ratingBarIndicator);
            editOffer.setVisibility(View.INVISIBLE);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }

    public class rewardOfferViewHolder extends RecyclerView.ViewHolder {

        protected TextView userName;
        protected TextView date;
        protected TextView title;
        protected TextView description;
        protected RatingBar ratingbar;
        protected ImageView userImage;
        protected TextView editOffer;

        public rewardOfferViewHolder(View v) {
            super(v);
            userName =  (TextView) v.findViewById(R.id.username);
            userImage = (ImageView)  v.findViewById(R.id.itemImage);
            date =  (TextView) v.findViewById(R.id.date);
            title =  (TextView) v.findViewById(R.id.title);
            description =  (TextView) v.findViewById(R.id.description);
            ratingbar = (RatingBar) v.findViewById(R.id.ratingBarIndicator);
            editOffer.setVisibility(View.INVISIBLE);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }
    public class loyaltyOfferViewHolder extends RecyclerView.ViewHolder {

        protected TextView userName;
        protected TextView date;
        protected TextView title;
        protected TextView description;
        protected RatingBar ratingbar;
        protected ImageView userImage;
        protected TextView editOffer;

        public loyaltyOfferViewHolder(View v) {
            super(v);
            userName =  (TextView) v.findViewById(R.id.username);
            userImage = (ImageView)  v.findViewById(R.id.itemImage);
            date =  (TextView) v.findViewById(R.id.date);
            title =  (TextView) v.findViewById(R.id.title);
            description =  (TextView) v.findViewById(R.id.description);
            ratingbar = (RatingBar) v.findViewById(R.id.ratingBarIndicator);
            editOffer.setVisibility(View.INVISIBLE);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }



}

