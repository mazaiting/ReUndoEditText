# ReUndoEditText
一个可以撤销，可以恢复的EditText

使用方法：
布局中：
```
  <com.mazaiting.ReUndoEditText
      android:id="@+id/editText"
      android:layout_width="match_parent"
      android:layout_height="wrap_content"
      />
```

Activity中：
```
// 撤销
mReUndoEditText.undo();
// 重做
mReUndoEditText.redo();
// 清楚历史记录
mReUndoEditText.clearHistory();
```


