package me.chievent.appstats;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.chievent.stats.AppStatsHelper;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    MyAdapter mMyAdapter;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        doRefresh();
    }

    private void doRefresh() {
        mProgressBar.setVisibility(View.VISIBLE);
        AppStatsHelper.getStats(this, new IPackageStatsObserver.Stub() {
            @Override
            public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
                mProgressBar.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mProgressBar.setVisibility(View.GONE);
                    }
                }, 500);

                if (mMyAdapter == null) {
                    mMyAdapter = new MyAdapter(pStats);
                    mRecyclerView.setAdapter(mMyAdapter);
                } else {
                    mMyAdapter.setpStats(pStats);
                }
            }
        });
    }

    public void onClickRefresh(View view) {
        doRefresh();
    }

    private class MyAdapter extends RecyclerView.Adapter {

        PackageStats pStats;

        MyAdapter(PackageStats pStats) {
            this.pStats = pStats;
        }

        public void setpStats(PackageStats pStats) {
            this.pStats = pStats;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            holder.itemView.setBackgroundColor(position % 2 == 0 ? 0xff333333 : 0xff444444);
            MyViewHolder myHolder = (MyViewHolder) holder;
            switch (position) {
                case 0:
                    if (pStats == null) {
                        myHolder.nameTextView.setText("No Valid Data");
                        break;
                    }
                    myHolder.nameTextView.setText("PackageName");
                    myHolder.valueTextView.setText(pStats.packageName);
                    break;
                case 1:
                    myHolder.nameTextView.setText("describeContents");
                    myHolder.valueTextView.setText(pStats.describeContents() + "");
                    break;
                case 2:
                    myHolder.nameTextView.setText("codeSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.codeSize) + " (" + pStats.codeSize + ")");
                    break;
                case 3:
                    myHolder.nameTextView.setText("dataSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.dataSize) + " (" + pStats.dataSize + ")");
                    break;
                case 4:
                    myHolder.nameTextView.setText("externalCacheSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.externalCacheSize) + " (" + pStats.externalCacheSize + ")");
                    break;
                case 5:
                    myHolder.nameTextView.setText("externalCodeSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.externalCodeSize) + " (" + pStats.externalCodeSize + ")");
                    break;
                case 6:
                    myHolder.nameTextView.setText("externalDataSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.externalDataSize) + " (" + pStats.externalDataSize + ")");
                    break;
                case 7:
                    myHolder.nameTextView.setText("externalMediaSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.externalMediaSize) + " (" + pStats.externalMediaSize + ")");
                    break;
                case 8:
                    myHolder.nameTextView.setText("externalObbSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.externalObbSize) + " (" + pStats.externalObbSize + ")");
                    break;
                case 9:
                    myHolder.nameTextView.setText("cacheSize");
                    myHolder.valueTextView.setText(AppStatsHelper.formatSize(MainActivity.this, pStats.cacheSize) + " (" + pStats.cacheSize + ")");
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return pStats == null ? 1 : 10;
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView;
        TextView valueTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            valueTextView = (TextView) itemView.findViewById(R.id.value);
        }
    }
}
