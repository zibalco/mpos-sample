package ir.zibal.zibalsdk.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by NaviD on 3/8/2018.
 */

public class ZibalInitialResponse implements Parcelable {

    private int innerStatus = 0;
    private int price = -1;//9F10
    private String status = "0x";//9F00
    private String factorNo = "0x";//9F11
    private String sellerName = "0x";//9F12
    private String errorCode = "0x";//9F14

    public static final int READY_TO_PAY = 1;
    public static final int INVALID = 2;
    public static final int PAID = 3;
    public static final int DISCONNECT = 4;

    public ZibalInitialResponse() {
    }

    protected ZibalInitialResponse(Parcel in) {
        innerStatus = in.readInt();
        price = in.readInt();
        status = in.readString();
        factorNo = in.readString();
        sellerName = in.readString();
        errorCode = in.readString();
    }

    public static final Creator<ZibalInitialResponse> CREATOR = new Creator<ZibalInitialResponse>() {
        @Override
        public ZibalInitialResponse createFromParcel(Parcel in) {
            return new ZibalInitialResponse(in);
        }

        @Override
        public ZibalInitialResponse[] newArray(int size) {
            return new ZibalInitialResponse[size];
        }
    };

    private String digit2fa(int num)
    {
        NumberFormat format = new DecimalFormat("#,###");
        return format.format(num);
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = Integer.valueOf(price);
    }

    public int getInnerStatus() {
        return innerStatus;
    }

    public void setInnerStatus(int innerStatus) {
        this.innerStatus = innerStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFactorNo() {
        return factorNo;
    }

    public void setFactorNo(String factorNo) {
        this.factorNo = factorNo;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getPriceToShow() {
        return digit2fa(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(innerStatus);
        dest.writeInt(price);
        dest.writeString(status);
        dest.writeString(factorNo);
        dest.writeString(sellerName);
        dest.writeString(errorCode);
    }
}
