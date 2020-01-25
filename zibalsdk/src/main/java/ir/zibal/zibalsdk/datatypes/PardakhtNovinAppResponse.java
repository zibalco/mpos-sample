package ir.zibal.zibalsdk.datatypes;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by NaviD on 3/8/2018.
 */

public class PardakhtNovinAppResponse implements Parcelable {

    final public static int COSTUMER_TRANSACTION = 0;
    final public static int CLUB_TRANSACTION = 1;
    String MerchantName;//nam e pazirande
    String DateTime;//zaman e tarakonesh
    int status;//vaziat e tarakonesh
    String TerminalId;//shomare e Terminal
    String Amount;//mablagh
    String Description;//khata
    String responseCode;//code e pasokh
    String customerCardNO;//shomare e cart
    String stan;//shomare e peygiri
    String rrn;//shomare e marja

    int transaction_source;

    public PardakhtNovinAppResponse() {
    }

    protected PardakhtNovinAppResponse(Parcel in) {
        MerchantName = in.readString();
        DateTime = in.readString();
        status = in.readInt();
        TerminalId = in.readString();
        Amount = in.readString();
        Description = in.readString();
        responseCode = in.readString();
        customerCardNO = in.readString();
        stan = in.readString();
        rrn = in.readString();
        transaction_source = in.readInt();
    }

    public static final Creator<PardakhtNovinAppResponse> CREATOR = new Creator<PardakhtNovinAppResponse>() {
        @Override
        public PardakhtNovinAppResponse createFromParcel(Parcel in) {
            return new PardakhtNovinAppResponse(in);
        }

        @Override
        public PardakhtNovinAppResponse[] newArray(int size) {
            return new PardakhtNovinAppResponse[size];
        }
    };

    public String getMerchantName() {
        return MerchantName;
    }

    public void setMerchantName(String merchantName) {
        MerchantName = merchantName;
    }

    public String getDateTime() {
        return DateTime;
    }

    public void setDateTime(String dateTime) {
        DateTime = dateTime;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getTerminalId() {
        return TerminalId;
    }

    public void setTerminalId(String terminalId) {
        TerminalId = terminalId;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getTransaction_source() {
        return transaction_source;
    }

    public void setTransaction_source(int transaction_source) {
        this.transaction_source = transaction_source;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getCustomerCardNO() {
        return customerCardNO;
    }

    public void setCustomerCardNO(String customerCardNO) {
        this.customerCardNO = customerCardNO;
    }

    public String getStan() {
        return stan;
    }

    public void setStan(String stan) {
        this.stan = stan;
    }

    public String getRrn() {
        return rrn;
    }

    public String getTimeStamp(){
//        String date[], time[];
//        date = DateTime.split(" ")[1].split("/");
//        time = DateTime.split(" ")[0].split(":");
//        Calendar calendar = Calendar.getInstance();
//        calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
//        calendar.set(Integer.valueOf(date[0]), Integer.valueOf(date[1]) - 1, Integer.valueOf(date[2]), Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]));
        return (System.currentTimeMillis()/1000) + "";//String.valueOf(calendar.getTimeInMillis() / 1000);
    }

    public void setRrn(String rrn) {
        this.rrn = rrn;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(MerchantName);
        dest.writeString(DateTime);
        dest.writeInt(status);
        dest.writeString(TerminalId);
        dest.writeString(Amount);
        dest.writeString(Description);
        dest.writeString(responseCode);
        dest.writeString(customerCardNO);
        dest.writeString(stan);
        dest.writeString(rrn);
        dest.writeInt(transaction_source);
    }
}
