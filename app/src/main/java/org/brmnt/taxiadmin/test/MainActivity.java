package org.brmnt.taxiadmin.test;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;
import org.brmnt.taxiadmin.test.instance.Orders;
import org.brmnt.taxiadmin.test.service.FillingService;
import org.brmnt.taxiadmin.test.service.OnFillingRequestListener;
import org.brmnt.taxiadmin.test.view.StatefulRecyclerView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MaterialTabListener {

    private RecyclerListAdapter mAdapter;

    private FillingService mService;
    private boolean mBound = false;
    private MaterialTabHost mFilterTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initTabs(this);
        mFilterTab.setSelectedNavigationItem(0);

        final StatefulRecyclerView mRecyclerView = (StatefulRecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setEmptyView(findViewById(R.id.empty_view));
        mAdapter = new RecyclerListAdapter(this);
        mRecyclerView.setLayoutManager(mAdapter.getLayoutManager());
        mRecyclerView.setAdapter(mAdapter);

        new PermissionUtils(getApplicationContext()).requestPermission(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("CURRENT_TAB", mFilterTab.getSelectedNavigationIndex());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mFilterTab.setSelectedNavigationItem(savedInstanceState.getInt("CURRENT_TAB"));
    }

    private ServiceConnection mServiceConnector = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            FillingService.LocalBinder binder = (FillingService.LocalBinder) service;
            mService = binder.getService();
            mService.registerListener(mRequestListener);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(!new PermissionUtils(this).checkPermission())
        bindService(new Intent(this, FillingService.class), mServiceConnector, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBound) {
            if (mService != null) mService.unregisterListener();
            unbindService(mServiceConnector);
            mBound = false;
        }
    }

    private OnFillingRequestListener mRequestListener = new OnFillingRequestListener() {
        @Override
        public void onCompleted(List<Orders> orders) {
            mAdapter.setDataChanged(orders);
            mAdapter.switchList(mFilterTab.getSelectedNavigationIndex());
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onError(final Exception e) {
            /*Эфективность оповещения об ошибке в таком виде крайне сомнительна..*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private void initTabs(MaterialTabListener listener) {
        mFilterTab = (MaterialTabHost) findViewById(R.id.FilterTabs);
        if (mFilterTab != null && listener != null) {
            removeTabs();
            int[] icons = new int[]{R.drawable.access_point, R.drawable.alarm};
            int[] label = new int[]{R.string.tab_ether, R.string.tab_advance};
            for(int i = 0; i<2; i++){
                MaterialTab tab = mFilterTab.newTab();
                tab.setIcon(icons[i]);
                tab.setLabel(label[i]);
                tab.setTabListener(listener);
                mFilterTab.addTab(tab);
            }
        }
    }

    private void removeTabs() {
        if (mFilterTab != null) {
            if (mFilterTab.getTabCount() > 0)
                mFilterTab.removeAllViews();
        }
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        if(mFilterTab !=null && mFilterTab.getTabCount()>0)
            mFilterTab.setSelectedNavigationItem(tab.getPosition());
        mAdapter.switchList(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {}

    @Override
    public void onTabUnselected(MaterialTab tab) {}

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PermissionUtils.REQUEST_CODE && grantResults.length > 0){
            String[] mPermissionsDenied = new String[0];
            for (int i=0; i<permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    String[] result = Arrays.copyOf(mPermissionsDenied, mPermissionsDenied.length+1);
                    result[mPermissionsDenied.length] = permissions[i];
                    mPermissionsDenied = result;
                }
            }
            if(mPermissionsDenied.length==0){
                this.startService(new Intent(this, FillingService.class));
                onStart();
            }
        }
    }
}
