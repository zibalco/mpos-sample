package ir.zibal.zibalsdkmpos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class InputActivity extends Activity {
    EditText amountRQ;
    Button start_payment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_input);

        amountRQ= (EditText) findViewById(R.id.topup_amount);
        start_payment= (Button) findViewById(R.id.start_payment);
        start_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                );
                Intent returnIntent = new Intent();
                returnIntent.putExtra("zibalId",amountRQ.getText().toString());
                setResult(Activity.RESULT_OK,returnIntent);
//                Main.mySpinnerDialog.show();
                finish();
            }
        });


    }
}
