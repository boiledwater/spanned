package com.example.spanned;

import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.TextView;

public class LinkMovementMethodExt extends LinkMovementMethod {
	private static LinkMovementMethod sInstance;
	private  Handler handler = null;
	private  Class spanClass = null;
	
	public static  MovementMethod getInstance(Handler _handler,Class _spanClass) {
		if (sInstance == null) {
			sInstance = new LinkMovementMethodExt();
			((LinkMovementMethodExt)sInstance).handler = _handler;
			((LinkMovementMethodExt)sInstance).spanClass = _spanClass;
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
	            Object [] spans = buffer.getSpans(off, off, spanClass);
	            if (spans.length != 0) {
	                if (action == MotionEvent.ACTION_UP) {
	                	Message message = handler.obtainMessage();
	                			message.obj = spans;
	                			message.what = 100;
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
