package vcims.com.vrapid.view_pager;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import vcims.com.vrapid.R;
import vcims.com.vrapid.models.DialogModel;

public class DialogViewPager extends PagerAdapter {

    private Context context;
    private ArrayList<DialogModel> dialogs;
    private OnViewPagerEventListener viewPagerListener;
    private int screenWidth = 0, screenHeight = 0;

    public DialogViewPager(Context context, ArrayList<DialogModel> dialogs, int viewPagerWidth, int viewPagerHeight) {
        this.context = context;
        this.dialogs = dialogs;
        this.screenWidth = viewPagerWidth;
        this.screenHeight = viewPagerHeight;
    }

    public void setViewPagerListener(OnViewPagerEventListener listener) {
        this.viewPagerListener = listener;
    }

    @Override
    public int getItemPosition(Object object) {
        int index = dialogs.indexOf(object);
        if (index == -1)
            return POSITION_NONE;
        else
            return index;
    }

    @Override
    public int getCount() {
        return dialogs.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object == view;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        DialogModel dialog = dialogs.get(position);
        int type = dialog.getStatus();
        if(type == 3){
            view = LayoutInflater.from(context).inflate(R.layout.dialog_row_expired_layout, container, false);
        }
        else{
            view = LayoutInflater.from(context).inflate(R.layout.dialog_row_layout, container, false);
        }
        viewPagerListener.onInstantiateItem(position, view);
        Button alarm = view.findViewById(R.id.alarmButton);
        setMeasurements(view);
        container.addView(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerListener.onItemClick(position, view);
            }
        });

        alarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPagerListener.onAddToCalender(position);
            }
        });


        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void addAllItem(ArrayList<DialogModel> dialogs) {
        this.dialogs = dialogs;
        notifyDataSetChanged();
    }

    public void addItem(DialogModel dialog) {
        dialogs.add(dialog);
        notifyDataSetChanged();
    }

    public void removeAllItems() {
        if(dialogs!=null) {
            dialogs.clear();
            notifyDataSetChanged();
        }
    }

    public DialogModel getItem(int position) {
        return dialogs.get(position);
    }

    private void setMeasurements(View view) {
        CardView cardViewVP = view.findViewById(R.id.cardViewVP);
        int cardViewWidthHeight = screenWidth * 3/4;
        cardViewVP.setLayoutParams(new LinearLayout.LayoutParams(cardViewWidthHeight, ViewGroup.LayoutParams.WRAP_CONTENT));
        LinearLayout ringLL = view.findViewById(R.id.ringLL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(cardViewWidthHeight * 5/6, cardViewWidthHeight * 5/6);
        int diff = cardViewWidthHeight - cardViewWidthHeight * 5/6;
        params.setMargins(0, diff/2, 0, 0);
        ringLL.setLayoutParams(params);
        LinearLayout confNameLL = view.findViewById(R.id.confNameLL);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.setMargins(0,30,0,30);
        confNameLL.setLayoutParams(params1);
    }

    public interface OnViewPagerEventListener {
        void onInstantiateItem(int position, View view);
        void onItemClick(int position, View view);
        void onAddToCalender(int position);
    }


}
