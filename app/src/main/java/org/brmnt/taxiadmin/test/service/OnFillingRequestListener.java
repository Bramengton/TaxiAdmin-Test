package org.brmnt.taxiadmin.test.service;

import org.brmnt.taxiadmin.test.instance.Orders;

import java.util.List;

/**
 * @author Bramengton on 12/02/2018.
 */
public interface OnFillingRequestListener {
    void onCompleted(List<Orders> list);
    void onError(Exception e);
}
