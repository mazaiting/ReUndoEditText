package com.mazaiting;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;
import java.util.Stack;

/**
 * 可撤销与恢复的编辑器
 * Created by mazaiting on 2017/9/13.
 */
public class ReUndoEditText extends EditText {
  /**
   * 操作序号
   */
  private int index = 0;
  /**
   * 自动操作标志，防止重复回调,导致无限撤销
   */
  private boolean isAuto = false;
  private Editable mEditable;
  /**
   * 历史记录撤销栈
   */
  private Stack<Action> mHistory = new Stack<Action>();
  /**
   * 历史记录恢复栈
   */
  private Stack<Action> mHistoryBack = new Stack<Action>();

  public ReUndoEditText(Context context) {
    this(context, null);
  }

  public ReUndoEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  /**
   * 初始化
   */
  private void init() {
    mEditable = this.getText();
    this.setSelection(0,mEditable.length());
    this.addTextChangedListener(new Watcher());
  }

  /**
   * 清楚历史
   */
  public final void clearHistory() {
    mHistory.clear();
    mHistoryBack.clear();
  }

  /**
   * 撤销
   */
  public final void undo() {
    if (mHistory.empty()) {
      return;
    }
    // 锁定操作
    isAuto = true;
    Action action = mHistory.pop();
    mHistoryBack.push(action);
    if (action.isAdd) {
      // 撤销添加
      mEditable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
      this.setSelection(action.startCursor, action.startCursor);
    } else {
      // 撤销删除
      mEditable.insert(action.startCursor, action.actionTarget);
      if (action.endCursor == action.startCursor) {
        this.setSelection(action.startCursor + action.actionTarget.length());
      } else {
        this.setSelection(action.startCursor, action.endCursor);
      }
    }
    // 释放操作
    isAuto = false;
    // 判断是否是下一个动作是否和本动作是同一个操作，直到不同为止
    if (!mHistory.empty() && mHistory.peek().index == action.index){
      undo();
    }
  }

  /**
   * 重做
   */
  public final void redo() {
    if (mHistoryBack.empty()) {
      return;
    }
    // 锁定操作
    isAuto = true;
    Action action = mHistoryBack.pop();
    mHistory.push(action);
    if (action.isAdd){
      // 恢复添加
      mEditable.insert(action.startCursor, action.actionTarget);
      if (action.endCursor == action.startCursor){
        this.setSelection(action.startCursor + action.actionTarget.length());
      } else {
        this.setSelection(action.startCursor, action.endCursor);
      }
    } else {
      // 恢复删除
      mEditable.delete(action.startCursor, action.startCursor + action.actionTarget.length());
      this.setSelection(action.startCursor, action.startCursor);
    }
    isAuto = false;
    //判断是否是下一个动作是否和本动作是同一个操作
    if (!mHistoryBack.empty() && mHistoryBack.peek().index == action.index){
      redo();
    }
  }

  /**
   * 文本观察者
   */
  private class Watcher implements TextWatcher {

    /**
     * 文本改变之前
     *
     * @param s 文本
     * @param start 开始位置
     * @param count 选择数量
     * @param after 替换增加的文字数
     */
    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {
      if (isAuto){return;}
      int end = start + count;
      if (end > start && end <= s.length()) {
        CharSequence charSequence = s.subSequence(start, end);
        // 删除了文字
        if (charSequence.length() > 0) {
          Action action = new Action(charSequence, start, false);
          if (count > 1) {
            //如果一次超过一个字符，说名用户选择了，然后替换或者删除操作
            action.setSelectCount(count);
          } else if (count == 1 && count == after) {
            // 一个字符替换
            action.setSelectCount(count);
          }
          mHistory.push(action);
          mHistoryBack.clear();
          action.setIndex(++index);
        }
      }
    }

    /**
     * 文本改变
     *
     * @param s 文本
     * @param start 起始坐标
     * @param before 选择数量
     * @param count 增加数量
     */
    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
      if (isAuto){return;}
      int end = start + count;
      if (end > start) {
        CharSequence charSequence = s.subSequence(start, end);
        //添加文字
        if (charSequence.length() > 0) {
          Action action = new Action(charSequence, start, true);
          mHistory.push(action);
          mHistoryBack.clear();
          if (before > 0) {
            //文字替换（先删除再增加），删除和增加是同一个操作，所以不需要增加序号
            action.setIndex(index);
          } else {
            action.setIndex(++index);
          }
        }
      }
    }

    @Override public void afterTextChanged(Editable s) {
      if (s != mEditable) {
        mEditable = s;
      }
    }
  }

  private class Action {
    /** 改变字符 */
    CharSequence actionTarget;
    /** 光标位置 */
    int startCursor;
    int endCursor;
    /** 标志增加操作 */
    boolean isAdd;
    /** 操作序号 */
    int index;

    public Action(CharSequence actionTarget, int startCursor, boolean isAdd) {
      this.actionTarget = actionTarget;
      this.startCursor = startCursor;
      this.endCursor = startCursor;
      this.isAdd = isAdd;
    }

    public void setIndex(int index) {
      this.index = index;
    }

    public void setSelectCount(int count) {
      this.endCursor = endCursor + count;
    }
  }
}
