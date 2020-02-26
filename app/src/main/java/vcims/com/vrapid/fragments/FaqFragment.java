package vcims.com.vrapid.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import vcims.com.vrapid.R;
import vcims.com.vrapid.adapter.FaqAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class FaqFragment extends Fragment implements FaqAdapter.OnFaqAdapterListener {


    private RecyclerView faqRV;
    private ImageButton faqBackButton;
    private TextView expandCollapseTV;
    private Boolean expanded = false;
    private ArrayList<FaqModel> faqs;
    private HashMap<Integer, FaqAdapter.ViewHolder> faqsHolder;
    private Boolean[] expandedLists;
    private FaqAdapter adapter;

    public FaqFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_faq, container, false);

        initialize(view);
        getFaqs();
        bindAdapter();
        clickListener();

        return view;
    }

    private void initialize(View view) {
        expandCollapseTV = view.findViewById(R.id.expandCollapseTV);
        faqBackButton = view.findViewById(R.id.faqBackButton);
        faqRV = view.findViewById(R.id.faqRV);
        faqs = new ArrayList<>();
        faqsHolder = new HashMap<>();
    }

    private void clickListener() {
        faqBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });

        expandCollapseTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!expanded) {
                    for(int i = 0; i<faqsHolder.size(); i++){
                        FaqAdapter.ViewHolder holder = faqsHolder.get(i);
                        holder.getFaqAnswerLL().setVisibility(View.VISIBLE);
                        holder.getFaqQuestion().setTextColor(Color.parseColor("#4A4A4A"));
                    }
                    expanded = true;
                    adapter.setAllExpanded(true);
                    expandCollapseTV.setText(getString(R.string.collapse_all));
                } else {
                    for(int i = 0; i<faqsHolder.size(); i++){
                        FaqAdapter.ViewHolder holder = faqsHolder.get(i);
                        holder.getFaqAnswerLL().setVisibility(View.GONE);
                        holder.getFaqQuestion().setTextColor(Color.parseColor("#357a7e"));
                    }
                    expanded = false;
                    adapter.setAllExpanded(false);
                    expandCollapseTV.setText(getString(R.string.expand_all));
                }
            }
        });
    }

    private void getFaqs() {
        FaqModel faq1 = new FaqModel();
        faq1.setTitle(getString(R.string.faq_ques_1));
        faq1.setBody(getString(R.string.faq_ans_1));
        faqs.add(faq1);

        FaqModel faq2 = new FaqModel();
        faq2.setTitle(getString(R.string.faq_ques_2));
        faq2.setBody(getString(R.string.faq_ans_2));
        faqs.add(faq2);

        FaqModel faq3 = new FaqModel();
        faq3.setTitle(getString(R.string.faq_ques_3));
        faq3.setBody(getString(R.string.faq_ans_3));
        faqs.add(faq3);

        FaqModel faq4 = new FaqModel();
        faq4.setTitle(getString(R.string.faq_ques_4));
        faq4.setBody(getString(R.string.faq_ans_4));
        faqs.add(faq4);

        FaqModel faq5 = new FaqModel();
        faq5.setTitle(getString(R.string.faq_ques_5));
        faq5.setBody(getString(R.string.faq_ans_5));
        faqs.add(faq5);

        FaqModel faq6 = new FaqModel();
        faq6.setTitle(getString(R.string.faq_ques_6));
        faq6.setBody(getString(R.string.faq_ans_6));
        faqs.add(faq6);

        FaqModel faq7 = new FaqModel();
        faq7.setTitle(getString(R.string.faq_ques_7));
        faq7.setBody(getString(R.string.faq_ans_7));
        faqs.add(faq7);

        FaqModel faq8 = new FaqModel();
        faq8.setTitle(getString(R.string.faq_ques_8));
        faq8.setBody(getString(R.string.faq_ans_8));
        faqs.add(faq8);

        FaqModel faq9 = new FaqModel();
        faq9.setTitle(getString(R.string.faq_ques_9));
        faq9.setBody(getString(R.string.faq_ans_9));
        faqs.add(faq9);

        FaqModel faq10 = new FaqModel();
        faq10.setTitle(getString(R.string.faq_ques_10));
        faq10.setBody(getString(R.string.faq_ans_10));
        faqs.add(faq10);

        FaqModel faq11 = new FaqModel();
        faq11.setTitle(getString(R.string.faq_ques_11));
        faq11.setBody(getString(R.string.faq_ans_11));
        faqs.add(faq11);

        FaqModel faq12 = new FaqModel();
        faq12.setTitle(getString(R.string.faq_ques_12));
        faq12.setBody(getString(R.string.faq_ans_12));
        faqs.add(faq12);

        FaqModel faq13 = new FaqModel();
        faq13.setTitle(getString(R.string.faq_ques_13));
        faq13.setBody(getString(R.string.faq_ans_13));
        faqs.add(faq13);

        FaqModel faq14 = new FaqModel();
        faq14.setTitle(getString(R.string.faq_ques_14));
        faq14.setBody(getString(R.string.faq_ans_14));
        faqs.add(faq14);

        expandedLists = new Boolean[faqs.size()];
        for(int i = 0; i<expandedLists.length; i++){
            expandedLists[i] = false;
        }
    }

    private void bindAdapter() {
        adapter = new FaqAdapter(getActivity(), faqs, expanded);
        adapter.registerListener(this);
        faqRV.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        faqRV.setAdapter(adapter);
    }

    @Override
    public void onItemSelected(int position, Boolean expand) {

        expandedLists[position] = true;
        FaqAdapter.ViewHolder holder = faqsHolder.get(position);
        if (expand) {
            holder.getFaqAnswerLL().setVisibility(View.VISIBLE);
        }
        else {
            holder.getFaqAnswerLL().setVisibility(View.GONE);
        }

        if(allExpanded()) {
            expandCollapseTV.setText(getString(R.string.collapse_all));
            expanded = true;
        }
        else{
            expanded = false;
            expandCollapseTV.setText(getString(R.string.expand_all));
        }
    }

    private Boolean allExpanded(){
        for(Boolean value : expandedLists){
            if(!value)
                return false;
        }
        return true;
    }

    @Override
    public void onBindViewHolder(FaqAdapter.ViewHolder holder, int position) {
        faqsHolder.put(position, holder);
    }

    public class FaqModel {
        String title;
        String body;


        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getBody() {
            return body;
        }

        public void setBody(String body) {
            this.body = body;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        faqs = null;
        faqsHolder = null;
        faqRV = null;
        adapter = null;
    }
}
