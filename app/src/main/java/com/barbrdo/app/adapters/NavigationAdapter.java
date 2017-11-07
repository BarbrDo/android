package com.barbrdo.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.barbrdo.app.R;
import com.barbrdo.app.activities.AppActivity;
import com.barbrdo.app.dataobject.Navigation;
import com.barbrdo.app.interfaces.OnItemClickListener;

public class NavigationAdapter extends BaseRecyclerViewAdapter<Navigation> {

    private AppActivity mAppActivity;
    private OnItemClickListener onItemClickListener;

    public NavigationAdapter(AppActivity context) {
        super(context);
        this.mAppActivity = context;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public View createView(AppActivity context, ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_drawer, viewGroup, false);
        return view;
    }

    @Override
    protected void bindView(final Navigation item, ViewHolder viewHolder, final int position) {
        ImageView imageViewIcon = viewHolder.getView(R.id.iv_icon);
        TextView textViewItem = viewHolder.getView(R.id.tv_item);
        RelativeLayout relativeLayoutParent = viewHolder.getView(R.id.rl_parent);

        textViewItem.setText(item.title);
        imageViewIcon.setImageResource(item.icon);

        relativeLayoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(item, position);
            }
        });
    }
}