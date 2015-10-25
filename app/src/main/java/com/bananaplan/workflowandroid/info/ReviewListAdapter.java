package com.bananaplan.workflowandroid.info;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bananaplan.workflowandroid.R;
import com.bananaplan.workflowandroid.data.Task;
import com.bananaplan.workflowandroid.data.WorkingData;
import com.bananaplan.workflowandroid.data.task.actions.FailTaskCommand;
import com.bananaplan.workflowandroid.data.task.actions.PassTaskCommand;

import java.util.List;


/**
 * @author Danny Lin
 * @since 2015/10/21.
 */
public class ReviewListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Task> mData;


    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView caseName;
        public TextView taskName;
        public TextView pic;
        public TextView passButton;
        public TextView failButton;


        public ItemViewHolder(View view) {
            super(view);
            findViews(view);
            setupButtons();
        }

        private void findViews(View view) {
            caseName = (TextView) view.findViewById(R.id.main_information_list_title_case_name);
            taskName = (TextView) view.findViewById(R.id.main_information_list_title_task_name);
            pic = (TextView) view.findViewById(R.id.main_information_list_title_pic);
            passButton = (TextView) view.findViewById(R.id.main_information_pass_button);
            failButton = (TextView) view.findViewById(R.id.main_information_unpass_button);
        }

        private void setupButtons() {
            passButton.setOnClickListener(this);
            failButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_information_pass_button:
                    PassTaskCommand passTaskCommand = new PassTaskCommand(mContext, mData.get(getAdapterPosition()).id);
                    passTaskCommand.execute();
                    break;
                case R.id.main_information_unpass_button:
                    FailTaskCommand failTaskCommand = new FailTaskCommand(mContext, mData.get(getAdapterPosition()).id);
                    failTaskCommand.execute();
                    break;
            }
        }
    }

    public ReviewListAdapter(Context context, List<Task> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.main_information_list_review_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemVH = (ItemViewHolder) holder;

        itemVH.caseName.setText(WorkingData.getInstance(mContext).getCaseById(mData.get(position).caseId).name);
        itemVH.taskName.setText(mData.get(position).name);
        itemVH.pic.setText(WorkingData.getInstance(mContext).getWorkerById(mData.get(position).workerId).name);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
