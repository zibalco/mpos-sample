package ir.zibal.zibalsdk;

import java.io.Serializable;

/**
 * Created by mohammad on 26/07/2017.
 */

public class TerminalINF implements Serializable {
    String terminalID;
    String merchantID;


    public TerminalINF(String terminalID, String merchantID) {
        this.terminalID = terminalID;
        this.merchantID = merchantID;
    }
}
