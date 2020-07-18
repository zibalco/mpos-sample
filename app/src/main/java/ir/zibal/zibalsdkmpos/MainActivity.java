package ir.zibal.zibalsdkmpos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ir.zibal.zibalsdk.ZibalActivity;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btn_payment = findViewById(R.id.payBtn);
        final EditText et_zibalId = findViewById(R.id.zibalIdEditText);
        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String zibalId = et_zibalId.getText().toString();
                if(zibalId.length() == 0){
                    Toast.makeText(MainActivity.this,"لطفا شناسه زیبال را وارد کنید.",Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Intent intent = new Intent(MainActivity.this, ZibalActivity.class);
                    intent.putExtra("zibalId",zibalId);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } catch (Exception e) {
                    Log.d("error happened", e.toString());
                }


            }
        });
    }
}

