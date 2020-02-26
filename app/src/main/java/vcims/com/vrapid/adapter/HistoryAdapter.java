package vcims.com.vrapid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.models.HistoryModel;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    Context context;
    ArrayList<HistoryModel> arrayList;

    public HistoryAdapter(Context context, ArrayList<HistoryModel> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.history_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        HistoryModel model = arrayList.get(position);
        String status = "";
        if (model.getStatus().equals("1"))
            status = "Attended";
        else if(model.getStatus().equals("2"))
            status = "Expired";
        holder.historyStatus.setText(status);
        holder.historyConfName.setText(model.getConfName());
        holder.historyDate.setText(model.getDate());
        holder.historyDuration.setText(model.getDuration());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public void removeAll(){
        arrayList.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<HistoryModel> array){
        arrayList.addAll(array);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView historyStatus, historyConfName, historyDate, historyDuration;

        public ViewHolder(View itemView) {
            super(itemView);
            historyStatus = itemView.findViewById(R.id.historyStatus);
            historyConfName = itemView.findViewById(R.id.historyConfName);
            historyDate = itemView.findViewById(R.id.historyDate);
            historyDuration = itemView.findViewById(R.id.historyDuration);
        }
    }
}
