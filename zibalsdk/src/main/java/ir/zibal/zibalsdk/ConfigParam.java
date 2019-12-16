package ir.zibal.zibalsdk;

import java.io.Serializable;


public class ConfigParam implements Serializable {

    String mposLanguage;
    String webServiceURL;
    String merchant_mobile;
    boolean mposManualFlag;
    boolean topUpSecMobileNo;


    public ConfigParam(String mposLanguage, String webServiceURL, String merchant_mobile, boolean mposManualFlag, boolean topUpSecMobileNo) {
        this.mposLanguage = mposLanguage;
        this.webServiceURL = webServiceURL;
        this.merchant_mobile = merchant_mobile;
        this.mposManualFlag = mposManualFlag;
        this.topUpSecMobileNo = topUpSecMobileNo;
    }
}
