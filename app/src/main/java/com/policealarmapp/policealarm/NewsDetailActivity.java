package com.policealarmapp.policealarm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class NewsDetailActivity extends AppCompatActivity {


    private TextView txtTitle;
    private TextView txtContent;
    private Button btnBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initView();
    }

    private  void initView()
    {
        txtTitle=findViewById(R.id.policeNewsTitle);
        txtContent=findViewById(R.id.policeNewsContent);
        btnBack=findViewById(R.id.btnPoliceNewsReturn);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsDetailActivity.this.finish();
            }
        });





    }

    @Override
    protected void onStart() {
        super.onStart();

        txtContent.setText(getIntent().getStringExtra("NewsContent"));
        txtTitle.setText(getIntent().getStringExtra("NewsTitle"));

    }
}
