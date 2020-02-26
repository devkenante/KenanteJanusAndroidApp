package vcims.com.vrapid.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import vcims.com.vrapid.R;
import vcims.com.vrapid.models.KenanteUser;

public class AddUsersExpandableAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> titles;
    private HashMap<String, ArrayList<KenanteUser>> users;
    private ExpandableItemSelector itemSelector;

    public AddUsersExpandableAdapter(Context context, List<String> titles, HashMap<String, ArrayList<KenanteUser>> users) {
        this.context = context;
        this.titles = titles;
        this.users = users;
    }

    public void registerItemSelector(ExpandableItemSelector itemSelector){
        this.itemSelector = itemSelector;
    }

    @Override
    public int getGroupCount() {
        return users.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return users.get(this.titles.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.users.get(this.titles.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        ExpandableListView eLV = (ExpandableListView) parent;
        eLV.expandGroup(groupPosition);

        String headerTitle = (String) getGroup(groupPosition);
        GroupViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.expandable_listview_title,null);
            holder = new GroupViewHolder();
            holder.title = convertView.findViewById(R.id.expandableListViewTitle);
            convertView.setTag(holder);
        }
        else
            holder = (GroupViewHolder) convertView.getTag();

        holder.title.setText(headerTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        ChildViewHolder holder;
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.add_users_expandable_row, null);
            holder = new ChildViewHolder();
            holder.userName = convertView.findViewById(R.id.userName);
            holder.selectableImage = convertView.findViewById(R.id.selectableImage);
            convertView.setTag(holder);
        }
        else{
            holder = (ChildViewHolder) convertView.getTag();
        }

        KenanteUser user = (KenanteUser) getChild(groupPosition, childPosition);
        holder.userName.setText(user.getName());
        holder.selectableImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.selectableImage.getTag() == null){
                    holder.selectableImage.setImageResource(R.drawable.checked_tick);
                    holder.selectableImage.setTag(0);
                    itemSelector.onItemSelected(groupPosition, childPosition,true);
                }
                else{
                    holder.selectableImage.setImageResource(R.drawable.uncheck_tick);
                    holder.selectableImage.setTag(null);
                    itemSelector.onItemSelected(groupPosition, childPosition, false);
                }
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupViewHolder{
        private TextView title;
    }

    class ChildViewHolder{
        private TextView userName;
        private ImageView selectableImage;
    }

    public interface ExpandableItemSelector{
        public void onItemSelected(int groupPosition, int childPosition, boolean selection);
    }

}
