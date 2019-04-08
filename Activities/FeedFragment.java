package gp.whatuwant.anthony.social.media.untitled.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import gp.whatuwant.anthony.social.media.untitled.Adapters.FeedAdapter;
import gp.whatuwant.anthony.social.media.untitled.R;

public class FeedFragment extends Fragment {
    private DatabaseReference mDatabase;
    private RecyclerView feed;
    public FeedFragment() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.main_feed_fragment, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        final View myView=view;
        feed=(RecyclerView)myView.findViewById(R.id.recycler);
        feed.setLayoutManager(new LinearLayoutManager(getContext()));
        mDatabase= FirebaseDatabase.getInstance().getReference().child("InstaApp");
        FeedAdapter adapter=new FeedAdapter(mDatabase);
        feed.setAdapter(adapter);
    }
}
