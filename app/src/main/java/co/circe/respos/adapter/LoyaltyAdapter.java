package co.circe.respos.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import co.circe.respos.R;

/**
 * Created by akhil on 3/7/15.
 */
public class LoyaltyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private List<loyaltyInfo> LoyaltyList;
    List<ColorDrawable> colors;
    private static final int TYPE_LEFT = 1;
    private static final int TYPE_RIGHT = 0;

    private static final int TYPE_EXPIRY = -1;
    public LoyaltyAdapter(List<loyaltyInfo> LoyaltyList) {
        this.LoyaltyList = LoyaltyList;


    }


    @Override
    public int getItemCount() {
        return LoyaltyList.size() + 1;
    }

    @Override
    public void onBindViewHolder( RecyclerView.ViewHolder viewHolder, int i) {
        if(i != LoyaltyList.size()) {
            LoyaltyViewHolder LoyaltyViewHolder = (LoyaltyViewHolder) viewHolder;
            loyaltyInfo ci = LoyaltyList.get(i);
            LoyaltyViewHolder.info.setText(ci.info);
            LoyaltyViewHolder.visit.setText(ci.visit);
            int r = (int) (0xff * Math.random());
            int g = (int) (0xff * Math.random());
            int b = (int) (0xff * Math.random());

            int randomColor = Color.rgb(r, g, b);
            //LoyaltyViewHolder.card.setCardBackgroundColor(randomColor);
            //LoyaltyViewHolder.visitLayout.setBackgroundColor(randomColor);
//        LoyaltyViewHolder.card.setCardBackgroundColor(Color.parseColor("#FF1B1A6C"));
            if(ci.status == "true"){
                LoyaltyViewHolder.checkCircle.setVisibility(View.VISIBLE);
            }
            else{
                LoyaltyViewHolder.checkCircle.setVisibility(View.INVISIBLE);
            }

        }
        else{
            LoyaltyExpiryViewHolder LoyaltyExpiryViewHolder = (LoyaltyExpiryViewHolder) viewHolder;
            LoyaltyExpiryViewHolder.expiryDate.setText("5555/66/01");
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView;
        if(i == TYPE_RIGHT) {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.loyalty_card_right, viewGroup, false);

            return new LoyaltyViewHolder(itemView);
        }
        else if (i == TYPE_LEFT){
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.loyalty_card_left, viewGroup, false);

            return new LoyaltyViewHolder(itemView);
        }
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.loyalty_expiry_view, viewGroup, false);
        return new LoyaltyExpiryViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {


        if (position == LoyaltyList.size()){
            return  TYPE_EXPIRY;
        }
        else if(position%2 == 0){
            return TYPE_LEFT;
        }

        else{
            return TYPE_RIGHT;
        }
    }


    public static class LoyaltyViewHolder extends RecyclerView.ViewHolder {

        protected TextView info;
        protected TextView visit;
        protected CardView card;
        protected ImageView checkCircle;
        protected RelativeLayout visitLayout;

        public LoyaltyViewHolder(View v) {
            super(v);
            info =  (TextView) v.findViewById(R.id.loyaltyInfo);
            visit = (TextView)  v.findViewById(R.id.visitCount);
            card = (CardView) v.findViewById(R.id.card_view);
            visitLayout = (RelativeLayout) v.findViewById(R.id.visitLayout);
            checkCircle = (ImageView) v.findViewById(R.id.checkCircle);
        }
    }


    public static class LoyaltyExpiryViewHolder extends RecyclerView.ViewHolder {

        protected TextView expiryDate;

        public LoyaltyExpiryViewHolder(View v) {
            super(v);
            expiryDate =  (TextView) v.findViewById(R.id.expiryDate);
        }
    }
}
