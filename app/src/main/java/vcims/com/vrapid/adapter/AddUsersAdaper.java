package vcims.com.vrapid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.models.KenanteUser;

public class AddUsersAdaper extends BaseAdapter{

    Context context;
    ArrayList<KenanteUser> users;

    public AddUsersAdaper(Context context, ArrayList<KenanteUser> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
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

        ViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.admin_adapter_row, null);
            holder = new ViewHolder();
            holder.admin_name = convertView.findViewById(R.id.admin_name);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        //Now do things you want with holder
        KenanteUser user = users.get(position);
        holder.admin_name.setText(user.getName());

        return convertView;
    }

    public void removeAllElements(){
        users.clear();
        notifyDataSetChanged();
    }

    class ViewHolder{
        private TextView admin_name;
    }
}
