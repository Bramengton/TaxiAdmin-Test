package org.brmnt.taxiadmin.test.instance;

import android.os.Parcel;
import android.os.Parcelable;

import java.nio.charset.Charset;
import java.util.Comparator;

/**
 * @author Bramengton on 12/02/2018.
 */
public class Orders implements Parcelable {
/*
ЭТО ВСЯ ИНФОРМАЦИЯ ЧТО ВОЗВРАЩАЕТ СЕРВЕР.
Для order:
Order is: 4884728||ЛЕНИНА ПР-Т Д:5||||0|0|46.9683|31.9626|0|0|686|5|2018-02-08 23:30:10|0||0|0|
Order is: 4884727||РАДИО ПЕР Д:5||||0|0|46.9714|31.97|0|0|686|5|2018-02-08 23:29:30|0||0|0|
Order is: 4884725||ЧКАЛОВА УЛ Д:5||||0|0|46.9667|31.9767|0|0|686|5|2018-02-08 23:28:19|0||0|0|
Order is: 4855875||ЧЕРНЫШЕВСКОГО УЛ Д:5|ЧКАЛОВА УЛ Д:5|||0|0|46.9198|32.0565|0|0|645|5|2018-01-30 10:44:35|10.1||0|0|
Order is: 4843034||ЧЕРНЫШЕВСКОГО УЛ Д:5|||2018-01-22 05:00:00|1|0|46.9199|32.0565|0|10|645|5|2018-01-22 02:09:16|0||0|0|
Order is: 4843031||ЧКАЛОВА УЛ Д:5|||2018-01-22 06:00:00|1|0|46.9668|31.9767|0|170|686|5|2018-01-22 02:07:50|0||0|0|
Order is: 4824415||САДОВАЯ УЛ Д:5||10$, ||1|0|0|0|0|10|0|5|2018-01-10 01:54:49|0|||0|

Для pr-order cтруктура повторется, так как незнаю что значит большинство полей.. Даты одинаковы но что они означают???
Что такое sinfo... ?????

Делаю скидку на то что задание тестовое..
*/

    private int _id;
    private String _lat = "";
    private String _lon = "";
    private String _date = "";
    private String _address = "";
    private String _address_to = "";
    private String _comment = "";

    private String _type;

    public Orders(String[] line, String type){
        if(line.length>0) {
            this._id = Integer.valueOf(line[0]);
            this._address = line[2];
            this._address_to = line[3];
            this._comment = line[4];
            this._lat = line[8];
            this._lon = line[9];
            this._date = line[14];
        }
        this._type = type;
    }

    public int getId() {
        return _id;
    }

    public String getLatLon() {
        return String.format("%s/%s", _lat, _lon);
    }

    public String getDate() {
        return _date;
    }

    public String getAddress() {
        return _address.toUpperCase();
    }

    public String setComment() {
        return this._comment;
    }

    public String getType() {
        return _type;
    }

    public int describeContents() {
        return 0;
    }

    public static Comparator<Orders> CompareOrder(){
        return new Comparator<Orders>() {
            @Override
            public int compare(Orders lhs, Orders rhs) {
                return (lhs._id < rhs._id) ? -1 : ((lhs._id == rhs._id) ? 0 : 1);
            }
        };
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this._id);
        writeString(parcel, this._lat);
        writeString(parcel, this._lon);
        writeString(parcel, this._date);
        writeString(parcel, this._address);
        writeString(parcel, this._address_to);
        writeString(parcel, this._comment);
        writeString(parcel, this._type);
    }

    @SuppressWarnings("rawtypes")
    public static final Parcelable.Creator<Orders> CREATOR = new Parcelable.Creator<Orders>()
    {
        public Orders createFromParcel(Parcel in){
            return new Orders(in);
        }

        public Orders[] newArray(int size) {
            return new Orders[size];
        }
    };

    private Orders(Parcel in) {
        this._id = in.readInt();
        this._lat = readString(in);
        this._lon = readString(in);
        this._date = readString(in);
        this._address = readString(in);
        this._address_to = readString(in);
        this._comment = readString(in);
        this._type = readString(in);
    }

    //Удобно для хранения больших текстов.
    //В данной ситуации не обязательно, но просто удобно..

    private String readString(Parcel in){
        byte[] b = new byte[in.readInt()];
        in.readByteArray(b);
        return new String(b, Charset.forName("UTF-8"));
    }

    private void writeString(Parcel arg0, String obj){
        byte[] b = obj.getBytes(Charset.forName("UTF-8"));
        arg0.writeInt(b.length);
        arg0.writeByteArray(b);
    }
}
