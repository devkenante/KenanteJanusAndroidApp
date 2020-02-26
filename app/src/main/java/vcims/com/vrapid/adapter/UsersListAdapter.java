package vcims.com.vrapid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Integer> users;
    private UsersListAdapterInterface listener;

    public UsersListAdapter(Context context, ArrayList<Integer> users){
        this.context = context;
        this.users = users;
        setHasStableIds(true);
    }

    public void setAdapterListener(UsersListAdapterInterface listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_users_on_call_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        listener.onBindHolder(holder, position);
    }

    public interface UsersListAdapterInterface{
        void onBindHolder(ViewHolder holder, int position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userOnCallNameTV, userOnCallStatus;

        public ViewHolder(View itemView) {
            super(itemView);
            userOnCallNameTV = itemView.findViewById(R.id.userOnCallNameTV);
            userOnCallStatus = itemView.findViewById(R.id.userOnCallStatus);
        }

        public TextView getUserOnCallNameTV() {
            return userOnCallNameTV;
        }

        public void setUserOnCallNameTV(TextView userOnCallNameTV) {
            this.userOnCallNameTV = userOnCallNameTV;
        }

        public TextView getUserOnCallStatus() {
            return userOnCallStatus;
        }

        public void setUserOnCallStatus(TextView userOnCallStatus) {
            this.userOnCallStatus = userOnCallStatus;
        }
    }
}
