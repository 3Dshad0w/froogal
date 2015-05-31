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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;


public class ScrollViewFragment1 extends ScrollTabHolderFragment implements NotifyingScrollView.OnScrollChangedListener {

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
        //mPosition = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_scrollview1, null);

        mScrollView = (NotifyingScrollView) v.findViewById(R.id.scrollview);
        mScrollView.setOnScrollChangedListener(this);
        layout1=(LinearLayout) v.findViewById(R.id.layout1);
        layout2=(LinearLayout) v.findViewById(R.id.layout2);
        titleDescription =(TextView) v.findViewById(R.id.titleDescription);
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
                emailIntent.setData(Uri.parse("mailto:"+email));
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(emailIntent);
            }
        });

        titleImage =(ImageView) v.findViewById(R.id.titleImage);
        cardView =(CardView) v.findViewById(R.id.cardView);
        cardView.setPadding(30,30,30,30);

            layout1.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
            layout2.setBackgroundColor(getResources().getColor(R.color.material_blue_100));
            titleDescription.setText("sample text ");
            title.setText("Donut");
            titleShortDescription.setText("Api level 4");
            titleImage.setImageResource(R.drawable.ic_launcher);



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
