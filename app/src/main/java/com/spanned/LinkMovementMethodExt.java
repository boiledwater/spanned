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
    private static LinkMovementMethod sInstance;
    private WeakReference<Handler> handlerWeakReference = null;
    private Class spanClass = null;

    public static MovementMethod getInstance(Handler _handler, Class _spanClass) {
        if (sInstance == null) {
            sInstance = new LinkMovementMethodExt();
            ((LinkMovementMethodExt) sInstance).handlerWeakReference = new WeakReference<Handler>(_handler);
            ((LinkMovementMethodExt) sInstance).spanClass = _spanClass;
        }

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
            Object[] spans = buffer.getSpans(off, off, spanClass);
            Handler handler = handlerWeakReference.get();
            if (handler == null) {
                return false;
            }
            if (spans.length != 0) {
                if (action == MotionEvent.ACTION_DOWN) {
                    Selection.setSelection(buffer,
                            buffer.getSpanStart(spans[0]),
                            buffer.getSpanEnd(spans[0]));
                    MessageSpan obj = new MessageSpan();
                    obj.setObj(spans);
                    obj.setView(widget);
                    Message message = handler.obtainMessage();
                    message.obj = obj;
                    message.what = 100;
                    message.sendToTarget();
                    return true;
                } else if (action == MotionEvent.ACTION_UP) {
                    MessageSpan obj = new MessageSpan();
                    obj.setView(widget);
                    Message message = handler.obtainMessage();
                    message.obj = obj;
                    message.what = 200;
                    message.sendToTarget();
                    return true;
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
