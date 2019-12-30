package ir.zibal.zibalsdk.Encryption; /**
 * Created by Mohammad on 3/1/2018.
 */

import java.util.HashMap;

public class TLV {

    private HashMap<String, String> data;

    /**
     * For Encoding a TLV use this Empty constructor and then use add(String tag, String Value) method to put everything you want
     * After all thing added method serialize() will return the Encoded String
     */
    public TLV() {
        data = new HashMap<>();
    }

    /**
     * For Decoding data use this constructor and then you can use get(String Tag) method or getAll() method
     * If the input message is not Valid then getAll() method will return null!
    */
    public TLV(String message) {
        try {
            data = new HashMap<>();
            String[] tags = {"9F01", "9F02", "9F10", "9F11", "9F12", "9F13", "9F14", "9F20", "9F00", "9F30", "9F31", "9F32", "9F33"};
            int i = 0;
            message = message.replace(" ", "");
            while (i < message.length()) {
                String key = message.substring(i, i + 4);
                i += 4;
                int lenght = Integer.parseInt(message.substring(i, i + 2), 16);
                i += 2;
                String value = message.substring(i, i + lenght*2);
                i += lenght*2;
                data.put(key, value);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            data = null;
        }
    }

    public void add(String tag, String value) {
        data.put(tag, value);
    }

    public String serialize() {
        String out = "";
        for (String key : data.keySet()) {
            out += key;
            String val = data.get(key);
            out += String.format("%02X", val.length() / 2);
            out += val;
        }
        return out;
    }

    public HashMap<String, String> getAll(){
        return data;
    }

    /**
     * @param tag
     * @return a String -> the Value of tag if tag is present in the message, otherwise return null
     */
//    String get(String tag){
//        if(data != null){
//            return data.getOrDefault(tag,null);
//        }
//        else{
//            return null;
//        }
//    }
}

