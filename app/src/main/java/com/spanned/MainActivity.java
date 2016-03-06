package com.spanned;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.style.BackgroundColorSpan;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private BackgroundColorSpan color = null;
	private ArrayList<Boolean> list = new ArrayList<Boolean>();
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			int what = msg.what;
			if (what == 100) {
				MessageSpan ms = (MessageSpan) msg.obj;
				Object[] spans = (Object[])ms.getObj();
				TextView view = ms.getView();
				
				for (Object span : spans) {
					if (span instanceof URLSpan) {
						int start = Selection.getSelectionStart(view.getText());
						int end = Selection.getSelectionEnd(view.getText());
						System.out.println(((URLSpan) span).getURL());
						Toast.makeText(MainActivity.this,((URLSpan) span).getURL(), Toast.LENGTH_SHORT).show();
						Spannable _span = (Spannable)view.getText();
						color = new BackgroundColorSpan(view.getLinkTextColors().getDefaultColor());
						_span.setSpan(color, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						view.setText(_span);
					}
				}
			}else if (what == 200) {
				MessageSpan ms = (MessageSpan) msg.obj;
				TextView view = ms.getView();
				Spannable _span = (Spannable)view.getText();
				_span.removeSpan(color);
				view.setText(_span);
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		String a = "<a href='/a'>aaaa</a>123456<a href='/b'>bbbb</a>7890";
		View span = findViewById(R.id.span);
		((TextView) span).setText(Html.fromHtml(a));
		((TextView) span).setMovementMethod(LinkMovementMethodExt.getInstance(handler, URLSpan.class));
	}

}
