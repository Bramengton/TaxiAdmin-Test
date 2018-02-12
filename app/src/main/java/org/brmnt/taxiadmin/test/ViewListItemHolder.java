package org.brmnt.taxiadmin.test;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import org.brmnt.taxiadmin.test.instance.Orders;

/**
 * @author Bramengton on 12/02/2018.
 */
public class ViewListItemHolder extends RecyclerView.ViewHolder {
    private TextView mLatLon;
    private TextView mDate;
    private TextView mAdress;
    private TextView mComment;

    ViewListItemHolder(View itemView) {
        super(itemView);
        mLatLon = (TextView) itemView.findViewById(R.id.latlon);
        mDate = (TextView) itemView.findViewById(R.id.date);
        mAdress = (TextView) itemView.findViewById(R.id.address);
        mComment = (TextView) itemView.findViewById(R.id.comment);
    }

    void setRecentView(Orders order){
        if(order==null) return;
        mLatLon.setText(order.getLatLon());
        mDate.setText(order.getDate());
        mAdress.setText(order.getAddress());
        mComment.setText(order.setComment());
    }
}
