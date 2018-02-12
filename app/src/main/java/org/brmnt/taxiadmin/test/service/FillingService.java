package org.brmnt.taxiadmin.test.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.*;
import android.support.v7.app.NotificationCompat;
import org.brmnt.taxiadmin.test.MainActivity;
import org.brmnt.taxiadmin.test.R;
import org.brmnt.taxiadmin.test.instance.Orders;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Bramengton on 12/02/2018.
 */
public class FillingService extends LocationService {
    private final String TAG = "Filling service";

    private List<Orders> mOrders;

    private static final int UPDATE_REQUEST_MESSAGE = 2018;

    private int mLastId = 0;
    private NotificationManager mManager;
    private ManageTimerHandler mHandler;
    private ManageTimer mManageTimer;
    private boolean isRunning = false;

    private OnFillingRequestListener mListener;
    private static IBinder mBind;

    public class LocalBinder extends Binder {
        public FillingService getService() {
            return FillingService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (mBind == null) mBind = new LocalBinder();
        return mBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void registerListener(OnFillingRequestListener listener) {
        mListener = listener;
        if(mOrders!=null && !mOrders.isEmpty()) mListener.onCompleted(mOrders);
    }

    public void unregisterListener() {
        mListener = null;
    }

    @Override
    public void onCreate() {
        mHandler = new ManageTimerHandler(getApplicationContext().getMainLooper(), this);
        mManageTimer  = new ManageTimer();
        if(mManager ==null) mManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mOrders = new ArrayList<>();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!isRunning){
            if(!mManageTimer.isInterrupted()) mManageTimer.interrupt();
            mManageTimer.start();
            isRunning = true;
        }
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    private class ManageTimer extends Thread{
        static final long DELAY = 3000;
        @Override
        public void run(){
            while(isRunning){
                try {
                    if(!checkPermission()){
                        getParseHTML(
                                api_construct("1","taxi_admin","1",
                                        String.valueOf(getCurrent().getLatitude()),
                                        String.valueOf(getCurrent().getLongitude()))
                        );
                    }else {
                        isRunning = false;
                        if(mListener!=null) mListener.onError(new Exception("Houston, we have a problem! No permission to start."));
                    }
                    Thread.sleep(DELAY);
                } catch (InterruptedException  | IOException e) {
                    isRunning = false;
                    if(mListener!=null) mListener.onError(e);
                    e.printStackTrace();
                }
            }
        }
    }

    //http://89.184.67.115/taxi/index.php?id_car=1&pass=taxi_admin&get_order=1&x=31.994583&y=46.975033
    private Uri api_construct(final String... params) throws UnsupportedEncodingException {
        return Uri.parse("http://89.184.67.115/taxi/index.php")
                .buildUpon()
                .appendQueryParameter("id_car", URLEncoder.encode(params[0], "UTF-8"))
                .appendQueryParameter("pass", URLEncoder.encode(params[1], "UTF-8"))
                .appendQueryParameter("get_order", URLEncoder.encode(params[2], "UTF-8"))
                .appendQueryParameter("x", URLEncoder.encode(params[3], "UTF-8"))
                .appendQueryParameter("y", URLEncoder.encode(params[4], "UTF-8"))
                .build();
    }

    private static class ManageTimerHandler extends Handler {
        private final WeakReference<FillingService> localHandler;

        ManageTimerHandler(Looper looper, FillingService service) {
            super(looper);
            localHandler = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            FillingService manageThread = localHandler.get();
            if (manageThread != null && msg.what == UPDATE_REQUEST_MESSAGE){
                manageThread.showNotification();
                if(manageThread.mListener!=null) manageThread.mListener.onCompleted(manageThread.mOrders);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mManageTimer!=null && !mManageTimer.isInterrupted()) mManageTimer.interrupt();
    }

    private void getParseHTML(Uri uri) throws IOException{
        Document doc = Jsoup.connect(uri.toString()).get();
        Elements[] body = new Elements[]{doc.getElementsByTag("order"), doc.getElementsByTag("pr_order")} ;
        for (Elements elements : body) {
            for (Element line : elements) {
                String orders = line.getElementsByTag(line.tagName()).html().replaceFirst("<br>", "").trim();
                for (String order : orders.split("<br>")) {
                    add(new Orders(order.trim().split("\\|"), line.tagName()));
                }
            }
        }
    }

    /*так как я не знаю принцип работы сервера заказов,
     * оставляю проверку уже добавленых в список заказов.
     * Во избежание дублей
     * */
    private void add(final Orders order){
        if(order!=null){
            for (Orders local : mOrders) {
                if((local.getId() == order.getId())){
                    return;
                }
            }
            mOrders.add(order);
            Collections.sort(mOrders, Orders.CompareOrder());
            //Собираем только обновления..
            mHandler.obtainMessage(UPDATE_REQUEST_MESSAGE).sendToTarget();
        }
    }

    private void showNotification(){
        //Первый запуск покажет массу оповещений, отменим их все...
        mManager.cancelAll();

        final Intent mIntent = new Intent()
                .setClass(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent mClick = PendingIntent.getActivity(this, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentIntent(mClick);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle(getString(R.string.new_order_title));
        mBuilder.setContentText(getString(R.string.new_order_message));
        final Notification notification = mBuilder.build();
        mManager.notify(mLastId, notification);
        mLastId++;
    }

}
