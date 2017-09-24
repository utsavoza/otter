package com.utsavoza.otter;

import android.app.Activity;
import android.os.Bundle;

@OtterActivity public class BarActivity extends Activity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }
}
