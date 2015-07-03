package co.froogal.froogal.adapter;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import co.froogal.froogal.R;

/**
 * Created by akhil on 3/7/15.
 */
public class LoyaltyAdapter extends RecyclerView.Adapter<LoyaltyAdapter.LoyaltyViewHolder>{


    private List<loyaltyInfo> LoyaltyList;
    List<ColorDrawable> colors;
    private static final int TYPE_LEFT = 1;
    private static final int TYPE_RIGHT = 0;
    public LoyaltyAdapter(List<loyaltyInfo> LoyaltyList) {
        this.LoyaltyList = LoyaltyList;


    }


    @Override
    public int getItemCount() {
        return LoyaltyList.size();
    }

    @Override
    public void onBindViewHolder(LoyaltyViewHolder LoyaltyViewHolder, int i) {
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


    }

    @Override
    public LoyaltyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView;
        if(i == TYPE_RIGHT) {
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.loyalty_card_right, viewGroup, false);
        }
        else{
            itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.loyalty_card_left, viewGroup, false);

        }
        return new LoyaltyViewHolder(itemView);
    }


    @Override
    public int getItemViewType(int position) {

        if(position%2 == 0){
            return TYPE_LEFT;
        }
        else{
            return  TYPE_RIGHT;
        }
    }


    public static class LoyaltyViewHolder extends RecyclerView.ViewHolder {

        protected TextView info;
        protected TextView visit;
        protected CardView card;
        protected LinearLayout visitLayout;

        public LoyaltyViewHolder(View v) {
            super(v);
            info =  (TextView) v.findViewById(R.id.loyaltyInfo);
            visit = (TextView)  v.findViewById(R.id.visitCount);
            card = (CardView) v.findViewById(R.id.card_view);
            visitLayout = (LinearLayout) v.findViewById(R.id.visitLayout);
        }
    }
}
