package scy.com.a9lockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    LockView2 lockView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lockView2 = (LockView2) findViewById(R.id.lockview);
        //设置的密码
        final String str = "056183274";
        lockView2.setOnDrawFinishedListener(new LockView2.OnDrawFinishedListener() {
            @Override
            public boolean onDrawFinished(List<Integer> passPositions) {
                StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < passPositions.size(); i++) {
                    buffer.append(passPositions.get(i));
                }
                Log.e("--main--", buffer.toString());
                if(buffer.toString().equals(str)){
                    Toast.makeText(MainActivity.this, "跳转中", Toast.LENGTH_SHORT).show();
                    return true;
                }
                Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
