Otter
=====

A lightweight library for Android which uses annotation processing to generate static factory 
methods to create intents and start activities.

Purpose
-------
The project is just an excuse to explore the annotation processing API in Java. Simultaneous effort
was also to make this project somewhat useful.

```java
class FooActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.foo_activity);
  }
}

class BarActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bar_activity);

    Intent intent = new Intent(this, FooActivity.class);
    startActivity(intent);
  }
}
```

Traditional intent calls are replaced with a bit more concise and clear static factory methods to 
start an annotated Activity.

```java
@OtterActivity class FooActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.foo_activity);    
  }
}

class BarActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.bar_activity);
    
    Otter.startFooActivity(this);
  }
}
```

Also
-----
TODO: Include a helper class that provides a familiar API that allows chaining multiple calls 
inorder to create a `Bundle` object. It would be better to provide a way to combine the two 
approaches in some way.

```java
class MainActivity extends Activity {
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    
    Bundle bundle = Otter.newBundle()
      .putChar(CHAR_KEY, "a")
      .putByte(BYTE_KEY, 127)
      .putShort(SHORT_KEY, 32767)
      .putInt(INT_KEY, 2147483)
       ...
      .build();    
  }
}
  
```