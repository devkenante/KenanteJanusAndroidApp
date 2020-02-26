package vcims.com.vrapid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.models.NotificationModel;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    Context context;
    ArrayList<NotificationModel> arrayList;

    public NotificationAdapter(Context context, ArrayList<NotificationModel> arrayList){
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.notification_row,parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        NotificationModel model = arrayList.get(position);

        holder.notificationTitle.setText(model.getTitle());
        holder.notificationMessage.setText(model.getMessage());
        holder.notificationMinutesAgo.setText(model.getTime());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView notificationTitle, notificationMinutesAgo, notificationMessage;
        public ViewHolder(View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationMinutesAgo = itemView.findViewById(R.id.notificationMinutesAgo);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
        }
    }
}
