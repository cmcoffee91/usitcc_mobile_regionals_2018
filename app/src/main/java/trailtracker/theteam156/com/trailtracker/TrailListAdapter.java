package trailtracker.theteam156.com.trailtracker;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class TrailListAdapter extends RecyclerView.Adapter<TrailListAdapter.MyViewHolder> {

    private List<Trail> trailList;

    private final int REQUEST_REFRESH = 7;

    private final ItemAdapterOnClickHandler mClickHandler;


    private Context mContext;


    public interface ItemAdapterOnClickHandler {
        void onClick(Trail itemObj, int position, boolean isDelete);
    }





    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView date, name, coords;
        ImageView imageView;

        public MyViewHolder(View view) {
            super(view);

            date = view.findViewById(R.id.date);
            name = view.findViewById(R.id.name);
            coords = view.findViewById(R.id.coords);


            imageView = view.findViewById(R.id.imageView);



            view.setOnClickListener(this);
            view.setOnLongClickListener(this);


        }

        @Override
        public void onClick(View v) {

            int position = getAdapterPosition();
            Log.e("ItemListAdapter", "regClick");
            mClickHandler.onClick(trailList.get(position), position, false);
        }

        @Override
        public boolean onLongClick(View v) {
            int position = getAdapterPosition();
            Log.e("ItemListAdapter", "longClick");
            mClickHandler.onClick(trailList.get(position), position, true);
            return false;
        }
    }


    public void setData(List<Trail> trails)
    {
        trailList = trails;
        notifyDataSetChanged();
    }



    public TrailListAdapter(List<Trail> videoList, ItemAdapterOnClickHandler clickHandler, Activity activity) {
        this.trailList = videoList;
        this.mClickHandler = clickHandler;
        this.mContext = activity;



    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trail_adapter_item, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Trail currentItem = trailList.get(position);




        holder.date.setText( currentItem.getDate() );
        holder.name.setText( currentItem.getName() );
        holder.coords.setText( currentItem.getLatCoord() + " " + currentItem.getLongCoord());



        File f = new File(currentItem.getPicLocation());
        Glide.with(mContext)
                .load(new File(f.getPath())) // Uri of the picture
                .into(holder.imageView);



    }







    @Override
    public int getItemCount() {
        return trailList.size();
    }


}