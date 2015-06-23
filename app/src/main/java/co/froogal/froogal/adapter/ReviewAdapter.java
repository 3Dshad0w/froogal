package co.froogal.froogal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import co.froogal.froogal.R;
import co.froogal.froogal.util.ImageUtil;

import co.froogal.froogal.util.basic_utils;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_ITEM = 0;

    private List<ReviewInfo> reviewList;

    public ReviewAdapter(List<ReviewInfo> reviewList) {
        super();
        this.reviewList = reviewList;
    }

    public void addItems(List<ReviewInfo> list) {
        reviewList.addAll(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        View view;
        if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.recycler_header, viewGroup, false);
            return new ReviewHeaderViewHolder(view);
        } else if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.reviewcard_layout, viewGroup, false);
            return new ReviewViewHolder(view);
        }

        throw new RuntimeException("Invalid view type " + viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position > 0) {
            ReviewViewHolder reviewViewHolder = (ReviewViewHolder) viewHolder;
            ReviewInfo ci = reviewList.get(position - 1);
            reviewViewHolder.userName.setText(ci.userName);
            ImageUtil.displayRoundImage(reviewViewHolder.userImage, ci.image_url, null);
            reviewViewHolder.date.setText(ci.date);
            reviewViewHolder.title.setText(ci.title);
            reviewViewHolder.title.setText(ci.description);
            /*restaurantViewHolder.resName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Restaurantchild " + v.getTag(), Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 1 : reviewList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? TYPE_HEADER : TYPE_ITEM;
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        protected TextView userName;
        protected TextView date;
        protected TextView title;
        protected TextView description;

        protected ImageView userImage;

        public ReviewViewHolder(View v) {
            super(v);
            userName =  (TextView) v.findViewById(R.id.username);
            userImage = (ImageView)  v.findViewById(R.id.userimage);
            date =  (TextView) v.findViewById(R.id.date);
            title =  (TextView) v.findViewById(R.id.title);
            description =  (TextView) v.findViewById(R.id.description);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }

    private static class ReviewHeaderViewHolder extends RecyclerView.ViewHolder {

        public ReviewHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

}

