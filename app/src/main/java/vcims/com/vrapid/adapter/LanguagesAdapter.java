package vcims.com.vrapid.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.fragments.ChangeLanguageFragment;

public class LanguagesAdapter extends RecyclerView.Adapter<LanguagesAdapter.CurrentViewHolder> {

    private Context context;
    private ArrayList<ChangeLanguageFragment.ChangeLanguageModel> languages;
    private OnEventClickListener listener;

    public LanguagesAdapter(Context context, ArrayList<ChangeLanguageFragment.ChangeLanguageModel> languages, OnEventClickListener listener){
        this.context = context;
        this.languages = languages;
        this.listener = listener;
        setHasStableIds(true);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public CurrentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_languages_row, parent, false);
        CurrentViewHolder holder = new CurrentViewHolder(view);
        holder.languageClickLL.setOnClickListener(view1 -> listener.onItemClick(holder, holder.getAdapterPosition()));
        return holder;
    }

    @Override
    public void onBindViewHolder(CurrentViewHolder holder, int position) {

        resetHolder(holder);
        ChangeLanguageFragment.ChangeLanguageModel language = languages.get(position);
        holder.localeCode = language.getLocaleCode();
        holder.languageNameTV.setText(language.getLanguageName());
        listener.onBindItem(holder, position);

    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    private void resetHolder(CurrentViewHolder holder){
        holder.localeCode = "";
        holder.languageNameTV.setText("");
    }

    public interface OnEventClickListener{
        void onBindItem(CurrentViewHolder holder, int position);
        void onItemClick(CurrentViewHolder holder, int position);
    }

    public class CurrentViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout languageClickLL;
        public TextView languageNameTV;
        public Switch changeLanguageSwitch;
        public String localeCode = "";

        public CurrentViewHolder(View itemView) {
            super(itemView);
            languageClickLL = itemView.findViewById(R.id.languageClickLL);
            languageNameTV = itemView.findViewById(R.id.languageNameTV);
            changeLanguageSwitch = itemView.findViewById(R.id.changeLanguageSwitch);
        }
    }
}
