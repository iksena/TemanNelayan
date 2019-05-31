package xyz.iksena.temannelayan.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.RealmList;
import xyz.iksena.temannelayan.R;
import xyz.iksena.temannelayan.models.Extreme;
import xyz.iksena.temannelayan.utils.WeatherUtils;

public class TidesExtremeAdapter extends RecyclerView.Adapter<TidesExtremeAdapter.ExtremeViewHolder> {

    private Context context;
    private List<Extreme> extremes;

    public TidesExtremeAdapter(Context context, List<Extreme> extremes) {
        this.context = context;
        this.extremes = extremes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExtremeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tide_height, parent, false);
        return new ExtremeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExtremeViewHolder holder, int position) {
        Extreme extreme = extremes.get(position);
        if (extreme!=null){
            holder.textTime.setText(WeatherUtils.getDateFromSeconds(extreme.dt,"HH:mm, dd MMM"));
            String tide = "Pasang";
            holder.textHeight.setTextColor(context.getResources().getColor(R.color.green_900));
            if (extreme.type.equalsIgnoreCase("Low")) {
                tide = "Surut";
                holder.textHeight.setTextColor(context.getResources().getColor(R.color.red_900));
            }
            holder.textHeight.setText(context.getString(R.string.format_tides_extreme, tide, extreme.height));
        }
    }

    @Override
    public int getItemCount() {
        return extremes!=null ? extremes.size() : 0;
    }

    class ExtremeViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_time)
        TextView textTime;
        @BindView(R.id.text_height)
        TextView textHeight;

        public ExtremeViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
