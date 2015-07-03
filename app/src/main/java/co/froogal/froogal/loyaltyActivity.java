package co.froogal.froogal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import co.froogal.froogal.adapter.LoyaltyAdapter;
import co.froogal.froogal.adapter.loyaltyInfo;

/**
 * Created by akhil on 3/7/15.
 */
public class loyaltyActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my);

        setContentView(R.layout.loyalty_recylerview);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        LoyaltyAdapter ca = new LoyaltyAdapter(createList(30));
        recList.setAdapter(ca);
    }





    private List<loyaltyInfo> createList(int size) {

        List<loyaltyInfo> result = new ArrayList<loyaltyInfo>();
        for (int i=1; i <= size; i++) {
            loyaltyInfo ci = new loyaltyInfo();
            ci.info = "jE nasd commen parle" + String.valueOf(i);
            ci.visit = String.valueOf(i);

            result.add(ci);

        }

        return result;
    }

}

