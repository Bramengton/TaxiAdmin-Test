package org.brmnt.taxiadmin.test;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import org.brmnt.taxiadmin.test.instance.Orders;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Bramengton on 12/02/2018.
 */
public class RecyclerListAdapter  extends RecyclerView.Adapter<ViewListItemHolder> implements Filterable {
    private LayoutInflater mInflater;
    private LinearLayoutManager mLinearLayoutManager;
    private List<Orders> mOrders;
    private List<Orders> mOrdersFiltered;

    RecyclerListAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.mLinearLayoutManager = new LinearLayoutManager(context);
        this.mLinearLayoutManager.setOrientation(OrientationHelper.VERTICAL);
        this.mOrders = this.mOrdersFiltered = new ArrayList<>();
    }

    public LinearLayoutManager getLayoutManager(){
        return this.mLinearLayoutManager;
    }

    public void setDataChanged(List<Orders> list) {
        this.mOrders = this.mOrdersFiltered = list;
    }

    public List<Orders> getData(){
        return this.mOrders;
    }

    @Override
    public int getItemCount() {
        return (this.mOrdersFiltered==null || this.mOrdersFiltered.isEmpty()) ? 0 : this.mOrdersFiltered.size();
    }

    private Orders getItem(int position) {
        return getItemCount()>0 ? this.mOrdersFiltered.get(position) : null;
    }

    @Override
    public ViewListItemHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = this.mInflater.inflate(R.layout.item_child, parent, false);
        return new ViewListItemHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewListItemHolder holder, int position){
        if (holder != null) {
            int pos = position % getItemCount();
            holder.setRecentView(getItem(pos));
        }
    }

    public void switchList(int tab){
        switch (tab){
            case 0:
                getFilter().filter("order");
                break;
            case 1:
                getFilter().filter("pr_order");
                break;
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                List<Orders> filteredList = new ArrayList<>();
                if (!charSequence.toString().isEmpty() ) {
                    for (Orders row : mOrders) {
                        if(row.getType().equals(charSequence.toString())) filteredList.add(row);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults results) {
                mOrdersFiltered = (ArrayList<Orders>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

