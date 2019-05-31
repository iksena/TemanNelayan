package xyz.iksena.temannelayan.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;
import xyz.iksena.temannelayan.R;
import xyz.iksena.temannelayan.TrackingActivity;
import xyz.iksena.temannelayan.models.ActivityLog;

public class ActivityFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private Realm realm;

    public ActivityFragment() {
    }

    @BindView(R.id.rv_logs)
    RecyclerView rvLogs;
    @BindView(R.id.fab_add)
    FloatingActionButton fabAdd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_activity, container, false);
        ButterKnife.bind(this, view);
        RealmResults<ActivityLog> logs = realm.where(ActivityLog.class)
                .sort("createdAt", Sort.DESCENDING).findAll();
        logs.addChangeListener(newLogs->
                rvLogs.setAdapter(new ActivityAdapter(getContext(),newLogs, mListener)));
        rvLogs.setAdapter(new ActivityAdapter(getContext(),logs, mListener));
        fabAdd.setOnClickListener(v->{
            mListener.onListFragmentInteraction(null);
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        realm = Realm.getDefaultInstance();
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ActivityLog item);
    }
}
