package co.froogal.froogal;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import co.froogal.froogal.adapter.MenuDisplayAdapter;


public class ScrollViewFragment extends ScrollTabHolderFragment implements NotifyingScrollView.OnScrollChangedListener {

    private static final String ARG_POSITION = "position";


    private NotifyingScrollView mScrollView;

    TextView title;
    TextView titleShortDescription;
    TextView titleDescription;
    TextView textSendEmail;
    TextView textContact;
    TextView textEmail;
    LinearLayout layout1;
    LinearLayout layout2;

    ImageView titleImage;

    private int mPosition;
    private CardView cardView;

    public static Fragment newInstance(int position) {
        ScrollViewFragment f = new ScrollViewFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v;
        if(mPosition != 3) {
            v = inflater.inflate(R.layout.fragment_scrollview, null);


            mScrollView = (NotifyingScrollView) v.findViewById(R.id.scrollview);
            mScrollView.setOnScrollChangedListener(this);
            layout1 = (LinearLayout) v.findViewById(R.id.layout1);
            layout2 = (LinearLayout) v.findViewById(R.id.layout2);
            titleDescription = (TextView) v.findViewById(R.id.titleDescription);
            titleShortDescription = (TextView) v.findViewById(R.id.titleShortDescription);
            title = (TextView) v.findViewById(R.id.title);

            textContact = (TextView) v.findViewById(R.id.textContact);
            textEmail = (TextView) v.findViewById(R.id.textEmail);
            textSendEmail = (TextView) v.findViewById(R.id.textSendEmail);


            textSendEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = textEmail.getText().toString();
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setType("text/plain");
                    emailIntent.setData(Uri.parse("mailto:" + email));
                    emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(emailIntent);
                }
            });

            titleImage = (ImageView) v.findViewById(R.id.titleImage);
            cardView = (CardView) v.findViewById(R.id.cardView);
            cardView.setPadding(30, 30, 30, 30);

            if (mPosition == 0) {
                layout1.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                layout2.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                titleDescription.setText("sample text ");
                title.setText("one");
                titleShortDescription.setText("Hello");
                titleImage.setImageResource(R.drawable.ic_launcher);
            }
            if (mPosition == 1) {
                layout1.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                layout2.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                titleDescription.setText("sample text ");
                title.setText("Two");
                titleShortDescription.setText("Hello");
                titleImage.setImageResource(R.drawable.ic_launcher);

            }
            if (mPosition == 2) {
                layout1.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                layout2.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                titleDescription.setText("sample text ");
                title.setText("Three");
                titleShortDescription.setText("hello213");
                titleImage.setImageResource(R.drawable.ic_launcher);

            }
            if (mPosition == 3) {
                layout1.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                layout2.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
                titleDescription.setText("sample text ");
                title.setText("Froyo");
                titleShortDescription.setText("Api level 8");
                titleImage.setImageResource(R.drawable.ic_launcher);

            }

        }
        else{
            v = inflater.inflate(R.layout.fragment_scrollview3, null);

            mScrollView = (NotifyingScrollView) v.findViewById(R.id.scrollview);
            mScrollView.setOnScrollChangedListener(this);


            MenuDisplayAdapter theAdapter;
            theAdapter = new MenuDisplayAdapter(getActivity().getBaseContext());


            for (int i = 1; i < 30; i++) {
                theAdapter.addItem("Row Item #" + i);
                if (i % 4 == 0) {
                    theAdapter.addSectionHeaderItem("Section #" + i);
                }
            }

            ListView theListView = (ListView) v.findViewById(R.id.listView);

            theListView.setAdapter(theAdapter);


        }


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



    }

    @Override
    public void adjustScroll(int scrollHeight, int headerTranslationY)
    {
        mScrollView.setScrollY(headerTranslationY - scrollHeight);
    }

    @Override
    public void onScrollChanged(ScrollView view, int l, int t, int oldl, int oldt)
    {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, l, t, oldl, oldt, mPosition);

    }



}
