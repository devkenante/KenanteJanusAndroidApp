package vcims.com.vrapid.adapter;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.fragments.FaqFragment;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.ViewHolder> {

    Context context;
    ArrayList<FaqFragment.FaqModel> arrayList;
    OnFaqAdapterListener listener;
    Boolean isMainExpanded;

    public FaqAdapter(Context context, ArrayList<FaqFragment.FaqModel> arrayList, Boolean isMainExpanded){
        this.context = context;
        this.arrayList = arrayList;
        this.isMainExpanded = isMainExpanded;
        setHasStableIds(true);
    }

    public void registerListener(OnFaqAdapterListener listener){
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.faq_adapter, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!holder.getExpanded()) {
                    listener.onItemSelected(holder.getAdapterPosition(), true);
                    holder.setExpanded(true);
                    holder.getFaqQuestion().setTextColor(Color.parseColor("#4A4A4A"));
                }
                else{
                    listener.onItemSelected(holder.getAdapterPosition(), false);
                    holder.setExpanded(false);
                    holder.getFaqQuestion().setTextColor(Color.parseColor("#357a7e"));
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        FaqFragment.FaqModel model = arrayList.get(position);
        holder.faqQuestion.setText(model.getTitle());
        holder.faqAnswer.setText(model.getBody());
        if (isMainExpanded) {
            holder.getFaqAnswerLL().setVisibility(View.VISIBLE);
            holder.getFaqQuestion().setTextColor(Color.parseColor("#4A4A4A"));
        }
        else {
            holder.getFaqAnswerLL().setVisibility(View.GONE);
            holder.getFaqQuestion().setTextColor(Color.parseColor("#357a7e"));
        }

        listener.onBindViewHolder(holder, position);
    }

    public void setAllExpanded(Boolean expanded){
        this.isMainExpanded = expanded;
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
        return arrayList.size();
    }

    public interface OnFaqAdapterListener{
        void onBindViewHolder(ViewHolder holder, int position);
        void onItemSelected(int position, Boolean expand);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        Boolean expanded = false;
        TextView faqQuestion, faqAnswer;
        LinearLayout faqAnswerLL;

        public ViewHolder(View itemView) {
            super(itemView);
            faqQuestion = itemView.findViewById(R.id.faqQuestion);
            faqAnswer = itemView.findViewById(R.id.faqAnswer);
            faqAnswerLL = itemView.findViewById(R.id.faqAnswerLL);
        }

        public Boolean getExpanded() {
            return expanded;
        }

        public void setExpanded(Boolean expanded) {
            this.expanded = expanded;
        }

        public TextView getFaqQuestion() {
            return faqQuestion;
        }

        public void setFaqQuestion(TextView faqQuestion) {
            this.faqQuestion = faqQuestion;
        }

        public TextView getFaqAnswer() {
            return faqAnswer;
        }

        public void setFaqAnswer(TextView faqAnswer) {
            this.faqAnswer = faqAnswer;
        }

        public LinearLayout getFaqAnswerLL() {
            return faqAnswerLL;
        }

        public void setFaqAnswerLL(LinearLayout faqAnswerLL) {
            this.faqAnswerLL = faqAnswerLL;
        }
    }
}
