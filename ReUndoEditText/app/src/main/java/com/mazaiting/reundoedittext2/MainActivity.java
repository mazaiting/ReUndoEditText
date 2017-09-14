package com.mazaiting.reundoedittext2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.mazaiting.ReUndoEditText;

public class MainActivity extends AppCompatActivity {
  ReUndoEditText mReUndoEditText;
  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mReUndoEditText = (ReUndoEditText) findViewById(R.id.editText);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_editor, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()){
      case R.id.action_undo:
        mReUndoEditText.undo();
        return true;
      case R.id.action_redo:
        mReUndoEditText.redo();
        return true;
      case R.id.action_clear:
        mReUndoEditText.clearHistory();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
