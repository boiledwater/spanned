package com.spanned;

import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class LinkMovementMethodExt extends LinkMovementMethod {
    public static final int LinkMovementMethod_Down = 1001;
    public static final int LinkMovementMethod_Up = 2002;
    private static LinkMovementMethod sInstance;
    private Class mSpanClass = null;
    private WeakReference<Handler> mWeakReference = null;

    public static MovementMethod getInstance(Handler handler, Class spanClass) {
        if (sInstance == null) {
            sInstance = new LinkMovementMethodExt();
        }
        ((LinkMovementMethodExt) sInstance).mWeakReference = new WeakReference<>(handler);
        ((LinkMovementMethodExt) sInstance).mSpanClass = spanClass;
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(TextView widget, Spannable buffer,
                                MotionEvent event) {
        int action = event.getAction();

        if (action == MotionEvent.ACTION_UP ||
                action == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            x -= widget.getTotalPaddingLeft();
            y -= widget.getTotalPaddingTop();

            x += widget.getScrollX();
            y += widget.getScrollY();

            Layout layout = widget.getLayout();
            int line = layout.getLineForVertical(y);
            int off = layout.getOffsetForHorizontal(line, x);
            /**
             * get you interest span
             */
            Object[] spans = buffer.getSpans(off, off, mSpanClass);
            if (spans.length != 0) {
                if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer, buffer.getSpanStart(spans[0]), buffer.getSpanEnd(spans[0]));
                    MessageSpan obj = new MessageSpan();
                    obj.setObj(spans);
                    obj.setView(widget);
                    Handler handler = mWeakReference.get();
                    if (handler != null) {
                        Message message = handler.obtainMessage();
                        message.obj = obj;
                        message.what = LinkMovementMethod_Down;
                        message.sendToTarget();
                        return true;
                    }
                    return false;
                } else if (action == MotionEvent.ACTION_UP) {
                    Handler handler = mWeakReference.get();
                    if (handler != null) {
                        MessageSpan obj = new MessageSpan();
                        obj.setView(widget);
                        Message message = handler.obtainMessage();
                        message.obj = obj;
                        message.what = LinkMovementMethod_Up;
                        message.sendToTarget();
                        return true;
                    }
                    return false;
                }
            }
        }

        return super.onTouchEvent(widget, buffer, event);
    }

    public boolean canSelectArbitrarily() {
        return true;
    }

    public boolean onKeyUp(TextView widget, Spannable buffer, int keyCode,
                           KeyEvent event) {
        return false;
    }
}
