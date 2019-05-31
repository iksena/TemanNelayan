package xyz.iksena.temannelayan.fragments;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xyz.iksena.temannelayan.R;
import xyz.iksena.temannelayan.fragments.ActivityFragment.OnListFragmentInteractionListener;
import xyz.iksena.temannelayan.models.ActivityLog;
import xyz.iksena.temannelayan.utils.WeatherUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private Context context;
    private final List<ActivityLog> logs;
    private final OnListFragmentInteractionListener mListener;

    public ActivityAdapter(Context context, List<ActivityLog> items, OnListFragmentInteractionListener listener) {
        this.context = context;
        logs = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.log = logs.get(position);
        ActivityLog log = holder.log;
        if (log != null){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm', 'dd MMM yyyy", Locale.getDefault());
            holder.textDate.setText(sdf.format(log.getCreatedAt()));
            holder.textCatches.setText(context.getString(R.string.format_catches, log.getCatches()));
            holder.textDistance.setText(context.getString(R.string.format_distance, log.getRouteDistance()));
            holder.textDuration.setText(context.getString(R.string.format_duration,
                    TimeUnit.MILLISECONDS.toHours(log.getDuration()),
                    TimeUnit.MILLISECONDS.toMinutes(log.getDuration()) -
                            TimeUnit.MINUTES.toMinutes(TimeUnit.MILLISECONDS.toHours(log.getDuration()))));
            holder.btnDel.setOnClickListener(v -> {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(log);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return logs!=null? logs.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        @BindView(R.id.text_date) TextView textDate;
        @BindView(R.id.text_message) TextView textMessage;
        @BindView(R.id.text_catches) TextView textCatches;
        @BindView(R.id.text_route) TextView textDistance;
        @BindView(R.id.text_duration) TextView textDuration;
        @BindView(R.id.btn_del)
        Button btnDel;
        ActivityLog log;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            mView = view;
        }
    }
}
