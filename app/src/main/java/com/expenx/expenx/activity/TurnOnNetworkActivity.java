package com.expenx.expenx.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.expenx.expenx.R;

public class TurnOnNetworkActivity extends AppCompatActivity {

    ImageButton mImageButtonRetryToConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turn_on_network);

        mImageButtonRetryToConnect = (ImageButton) findViewById(R.id.imageButtonRetryToConnect);
        mImageButtonRetryToConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TurnOnNetworkActivity.this, ExpenxActivity.class));
                LoginActivity.isExpenxActivityLaunched = true;
                TurnOnNetworkActivity.this.finish();
            }
        });
    }
}
