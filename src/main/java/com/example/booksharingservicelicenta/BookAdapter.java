package com.example.booksharingservicelicenta;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
public class BookAdapter extends BaseAdapter {
    private ArrayList<BookDataClass> dataList;
    private Context context;
    LayoutInflater layoutInflater;
    public BookAdapter(Context context, ArrayList<BookDataClass> dataList) {
        this.context = context;
        this.dataList = dataList;
    }
    @Override
    public int getCount() {
        return dataList.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null){
            view = layoutInflater.inflate(R.layout.grid_item, null);
        }
        ImageView gridImage = view.findViewById(R.id.gridImage);
        TextView gridTitle = view.findViewById(R.id.gridTitle);
        TextView gridAuthor = view.findViewById(R.id.gridAuthor);
        TextView gridState = view.findViewById(R.id.state);

        Glide.with(context).load(dataList.get(i).getImageURL()).into(gridImage);
        gridTitle.setText(dataList.get(i).getTitle());
        gridAuthor.setText(dataList.get(i).getAuthor());

        if(dataList.get(i).getState() != null)
        {

            if(dataList.get(i).getState().equals("Pending"))
            {
                gridState.setTextColor(Color.GRAY);
                gridState.setText(dataList.get(i).getState());
            }
            else if(dataList.get(i).getState().equals("Accepted"))
            {
                gridState.setTextColor(Color.GREEN);
                gridState.setText(dataList.get(i).getState());
            }
            else
            {
                gridState.setTextColor(Color.RED);
                gridState.setText(dataList.get(i).getState());
            }



        }

        return view;
    }
}