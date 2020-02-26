package vcims.com.vrapid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.models.FirebaseVideoModel;

public class FirebaseVideoAdapter extends BaseAdapter {

    Context context;
    ArrayList<FirebaseVideoModel> arrayList;

    public FirebaseVideoAdapter(Context context, ArrayList<FirebaseVideoModel> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,null);
        TextView tv = view.findViewById(android.R.id.text1);
        FirebaseVideoModel item = arrayList.get(position);
        tv.setText(item.name);

        return view;
    }
}
