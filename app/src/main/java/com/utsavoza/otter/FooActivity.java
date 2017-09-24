package com.utsavoza.otter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import com.utsavoza.otter.factory.Otter;

public class FooActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_foo);

    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        Otter.startBarActivity(FooActivity.this);
      }
    });
  }
}
