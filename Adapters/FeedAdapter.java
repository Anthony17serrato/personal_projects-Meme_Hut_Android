package gp.whatuwant.anthony.social.media.untitled.Adapters;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Map;

import gp.whatuwant.anthony.social.media.untitled.R;

/**
 * Created by anthony on 3/17/2018.
 */

public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<String> text = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> post_type = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtView;
        public ImageView img;


        public MyViewHolder(View view) {
            super(view);
            txtView = (TextView) view.findViewById(R.id.textView);
            img=(ImageView)view.findViewById(R.id.imageView);

        }
    }
    public class VideoViewHolder extends RecyclerView.ViewHolder {
        public VideoView myvid;
        public TextView txt;
        public VideoViewHolder(View view) {
            super(view);
            myvid=(VideoView) view.findViewById(R.id.videoView);
            txt= (TextView) view.findViewById(R.id.textView);
        }
    }
    public FeedAdapter(DatabaseReference dr){
        dr.limitToFirst(100).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                text.clear();
                image.clear();
                getContent((Map<String,Object>) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    /*@Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item_feed, parent, false);
        return new MyViewHolder(itemView);
    }*/
    @Override
    public int getItemViewType(int position) {
        if(post_type.get(position).equals("image")){
            return 0;
        }else{
            return 2;
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        switch (holder.getItemViewType()) {
            case 0:
                MyViewHolder mvh = (MyViewHolder) holder;
                Glide.with(mvh.img.getContext()).load(Uri.parse(image.get(position))).into(mvh.img);
                mvh.txtView.setText(text.get(position));
                break;

            case 2:
                final VideoViewHolder mvh2 = (VideoViewHolder) holder;
                mvh2.myvid.setVideoURI(Uri.parse(image.get(position)));
                mvh2.myvid.start();
                mvh2.txt.setText(text.get(position));
                //Video Loop
                mvh2.myvid.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mvh2.myvid.start(); //need to make transition seamless.
                    }
                });
                break;
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0: return new MyViewHolder(
                    LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.image_item_feed, parent, false)
            );
            case 2: return new VideoViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.video_item_feed, parent, false));

        }
        return null;
    }
    @Override
    public int getItemCount() {
        return text.size();
    }

    private void getContent(Map<String, Object> users) {
        if (users != null) {
            //iterate through each user, ignoring their UID
            for (Map.Entry<String, Object> entry : users.entrySet()) {

                //Get user map
                Map singleUser = (Map) entry.getValue();
                if(singleUser.get("title")!=null&&singleUser.get("image")!=null) {
                    //Get phone field and append to list
                    text.add( singleUser.get("title").toString());
                    image.add( singleUser.get("image").toString());
                    post_type.add(singleUser.get("post_type").toString());
                }
            }
            notifyDataSetChanged();
        }
    }
}
