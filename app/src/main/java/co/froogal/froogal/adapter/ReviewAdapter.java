package co.froogal.froogal.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import co.froogal.froogal.MainActivity;
import co.froogal.froogal.R;
import co.froogal.froogal.ResDetailsActivity;
import co.froogal.froogal.fragment.ReviewFragment;
import co.froogal.froogal.library.UserFunctions;
import co.froogal.froogal.util.ImageUtil;

import co.froogal.froogal.util.basic_utils;
import co.froogal.froogal.view.FloatLabeledEditText;

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 1;
    private static final int TYPE_RATING = 2;
    private static final int TYPE_ITEM = 0;
    private  String isReviewPresent;
    private  String isReviewAdded;
    private final String userID;
    private final String fname;
    private final String imageURL;


    private PopupWindow popupWindow;

    basic_utils bu;

    private Button mBtnCancel;
    private Context baseContext;
    int dontDisplay = -1;

    ReviewInfo cii;
    private List<ReviewInfo> reviewList;

    public ReviewAdapter(List<ReviewInfo> reviewList, String isReviewPresent, String uid, String fname, String image_url) {
        super();
        this.reviewList = reviewList;
        this.isReviewPresent = isReviewPresent;
        this.userID = uid;
        this.fname= fname;
        this.imageURL = image_url;
        isReviewAdded = "0";
        if(isReviewPresent.equals("1")) {
            int val = this.reviewList.size();
            int i;
            for (i = 0; i < val; i++) {
                if (this.reviewList.get(i).uid.equals(userID)) {
                    cii = this.reviewList.get(i);
                    this.reviewList.remove(i);
                    //dontDisplay = i;
                    break;
                }
            }
        }
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
        }

        else if (viewType == TYPE_RATING) {

            if(isReviewPresent.equals("0")) {
                view = LayoutInflater.from(context)
                        .inflate(R.layout.ratingcardview, viewGroup, false);
                return new ReviewRatingViewHolder(view);
            }
            else{
                view = LayoutInflater.from(context)
                        .inflate(R.layout.reviewcard_layout, viewGroup, false);
                return new ReviewViewHolder(view);
            }
        }

        else if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(context)
                    .inflate(R.layout.reviewcard_layout, viewGroup, false);
            return new ReviewViewHolder(view);
        }

        throw new RuntimeException("Invalid view type " + viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {
        if (position > 1) {
            if(position - 2 != dontDisplay) {
                ReviewViewHolder reviewViewHolder = (ReviewViewHolder) viewHolder;
                ReviewInfo ci = reviewList.get(position - 2);
                reviewViewHolder.userName.setText(ci.userName);
                ImageUtil.displayRoundImage(reviewViewHolder.userImage, ci.image_url, null);
                reviewViewHolder.date.setText(ci.date);
                reviewViewHolder.title.setText(ci.title);
                reviewViewHolder.description.setText(ci.description);
                reviewViewHolder.ratingbar.setRating(Float.parseFloat(ci.rating));
            /*restaurantViewHolder.resName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "Restaurantchild " + v.getTag(), Toast.LENGTH_SHORT).show();
                }
            });*/
            }
        }

        else if(position == 1){
            if(isReviewPresent.equals("0")) {
                ReviewRatingViewHolder ratingHolder = (ReviewRatingViewHolder) viewHolder;
                if(isReviewAdded.equals("0")) {
                    ratingHolder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        public void onRatingChanged(RatingBar ratingBar, float rating,
                                                    boolean fromUser) {

                            Toast.makeText(ratingBar.getContext(), String.valueOf(rating), Toast.LENGTH_LONG).show();
                            showPopupWindow(ratingBar.getRootView(), ratingBar.getContext(), rating);

                        }
                    });
                }
                else{
                    ratingHolder.ratingBar.setVisibility(View.GONE);
                    ratingHolder.cardView.setVisibility(View.GONE);
                }
            }
            else{
                        ReviewViewHolder reviewViewHolder = (ReviewViewHolder) viewHolder;
                        reviewViewHolder.userName.setText(cii.userName);
                        ImageUtil.displayRoundImage(reviewViewHolder.userImage, cii.image_url, null);
                        reviewViewHolder.date.setText(cii.date);
                        reviewViewHolder.title.setText(cii.title);
                        reviewViewHolder.description.setText(cii.description);
                        reviewViewHolder.ratingbar.setRating(Float.parseFloat(cii.rating));



            }

        }

    }

    @Override
    public int getItemCount() {
        return reviewList == null ? 2 : reviewList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0){
            return  TYPE_HEADER;
        }
        else if (position == 1){
            return TYPE_RATING;
        }
        return TYPE_ITEM;
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        protected TextView userName;
        protected TextView date;
        protected TextView title;
        protected TextView description;
        protected RatingBar ratingbar;
        protected ImageView userImage;

        public ReviewViewHolder(View v) {
            super(v);
            userName =  (TextView) v.findViewById(R.id.username);
            userImage = (ImageView)  v.findViewById(R.id.userimage);
            date =  (TextView) v.findViewById(R.id.date);
            title =  (TextView) v.findViewById(R.id.title);
            description =  (TextView) v.findViewById(R.id.description);
            ratingbar = (RatingBar) v.findViewById(R.id.ratingBarIndicator);

            //left.setOnClickListener(RestaurantAdapter.this);


        }


    }

    private static class ReviewHeaderViewHolder extends RecyclerView.ViewHolder {

        public ReviewHeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private  class ReviewRatingViewHolder extends RecyclerView.ViewHolder {

        protected RatingBar ratingBar;
        protected CardView cardView;

        public ReviewRatingViewHolder(View itemView) {
            super(itemView);
            ratingBar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            cardView = (CardView) itemView.findViewById(R.id.card_view_rating);
        }
    }


    private void showPopupWindow(View V, Context context, float rating){



        LayoutInflater mLayoutInflater=LayoutInflater.from(context);

        final View popupView =mLayoutInflater.inflate(R.layout.review_pop_up_layout, null);

       popupWindow = new PopupWindow(popupView,
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        //popupWindow.setWidth(popupWindow.getWidth() - 5);



        popupWindow.setFocusable(true);

        Drawable d = new ColorDrawable(Color.TRANSPARENT);

        d.setAlpha(1000);

        final RatingBar ratingBar = (RatingBar) popupView.findViewById(R.id.ratingBar);
        ratingBar.setRating(rating);

        popupWindow.setBackgroundDrawable(d);
        ImageView userImage = (ImageView) popupView.findViewById(R.id.userimage);
        final EditText title = (EditText) popupView.findViewById(R.id.titleEditTextView);
        final EditText description = (EditText) popupView.findViewById(R.id.descriptionEditTextView);
        final Button submitReview = (Button) popupView.findViewById(R.id.submitReview);
        submitReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ProcessReview(userID, String.valueOf(ratingBar.getRating()), title.getText().toString(), description.getText().toString(), v).execute();

            }
        });

        TextView ratingStatus = (TextView) popupView.findViewById(R.id.ratingStatus);
        if(rating == 1.0){
            ratingStatus.setText("Dont Visit");
        }
        else if(rating == 2.0){
            ratingStatus.setText("Bad");
        }
        else if(rating == 3.0){
            ratingStatus.setText("Not Bad");
        }
        else if(rating == 4.0){
            ratingStatus.setText("good");
        }
        else if(rating == 5.0){
            ratingStatus.setText("Great!!");
        }
        else{
            ratingStatus.setText("haha!!");
        }

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                TextView ratingStatus = (TextView) popupView.findViewById(R.id.ratingStatus);

                if(rating == 1.0){
                    ratingStatus.setText("Dont Visit");
                }
                else if(rating == 2.0){
                    ratingStatus.setText("Bad");
                }
                else if(rating == 3.0){
                    ratingStatus.setText("Not Bad");
                }
                else if(rating == 4.0){
                    ratingStatus.setText("good");
                }
                else if(rating == 5.0){
                    ratingStatus.setText("Great!!");
                }
                else{
                    ratingStatus.setText("haha!!");
                }

            }
        });



        ImageUtil.displayRoundImage(userImage, this.imageURL, null);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        V.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(V, Gravity.CENTER,
                0, 0);

        View container = (View) popupWindow.getContentView().getParent();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(container, p);

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                ratingBar.setRating(0.0f);
            }
        });
    }





    private class ProcessReview extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        String userID,rating,title,description;

        View v;

        public ProcessReview(String userId, String rating, String title, String description, View v) {
            this.userID = userId;
            this.rating = rating;
            this.title = title;
            this.description = description;
            this.v =v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(v.getContext());
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Processing Review ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected JSONObject doInBackground(String... args) {

            UserFunctions userFunction = new UserFunctions();

            JSONObject json = userFunction.addReview(userID, ResDetailsActivity.resID ,rating, title, description);

            Log.d("keysuccess", json.toString());

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {

            ReviewInfo ci = new ReviewInfo();

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            //get current date time with Date()
            Date date = new Date();

            ci.userName = fname;
            ci.image_url = imageURL;
            ci.date = dateFormat.format(date);
            ci.description = description;
            ci.title = title;
            ci.rating = rating;
            ci.uid = userID;
            cii = ci;

            reviewList.add(ci);
            //isReviewPresent = "1";
            isReviewAdded = "1";
            notifyDataSetChanged();
            pDialog.dismiss();
            popupWindow.dismiss();

        }
    }


}

