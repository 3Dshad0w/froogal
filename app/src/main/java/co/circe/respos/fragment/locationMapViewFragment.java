package co.circe.respos.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import co.circe.respos.R;

/**
 * Created by akhil on 24/5/15.
 */
public class locationMapViewFragment extends Fragment{

    public static locationMapViewFragment newInstance() {
        return new locationMapViewFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        return rootView;
    }
}
