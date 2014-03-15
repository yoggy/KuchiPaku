package net.sabamiso.android.kuchipaku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class KuchiPakuView extends View {

	boolean enable_face_color = false;
	Paint p_fg;
	Paint p_bg;

	Paint p_lip;
	Paint p_inner;
	Paint p_skin;

	float p_w = 0.8f;
	float p_h = 0.1f;

	Handler handler = new Handler();

	KalmanFilter filter = new KalmanFilter(1.0, 3.0);

	public KuchiPakuView(Context context) {
		super(context);
		setFocusable(true);

		p_fg = new Paint();
		p_fg.setColor(Color.WHITE);
		p_fg.setAntiAlias(true);
		p_fg.setStrokeWidth(50);
		p_fg.setStyle(Paint.Style.STROKE);

		p_bg = new Paint();
		p_bg.setStyle(Paint.Style.FILL);
		p_bg.setColor(Color.BLACK);

		p_lip = new Paint();
		p_lip.setColor(Color.rgb(255, 64, 64));
		p_lip.setAntiAlias(true);
		p_lip.setStrokeWidth(60);
		p_lip.setStyle(Paint.Style.STROKE);

		p_inner = new Paint();
		p_inner.setStyle(Paint.Style.FILL);
		p_inner.setColor(Color.rgb(0, 0, 0));

		p_skin = new Paint();
		p_skin.setStyle(Paint.Style.FILL);
		p_skin.setColor(Color.rgb(255, 200, 150));
	}

	public void setEnableFaceColor(boolean val) {
		enable_face_color = val;
	}

	public boolean getEnableFaceColor() {
		return enable_face_color;
	}

	protected void clearBackground(Canvas canvas) {
		if (enable_face_color) {
			canvas.drawRect(0, 0, this.getWidth(), getHeight(), p_skin);
		} else {
			canvas.drawRect(0, 0, this.getWidth(), getHeight(), p_bg);
		}
	}

	protected void drawLip(Canvas canvas) {
		float half_w = this.getWidth() / 2;
		float half_h = this.getHeight() / 2;
		float w = half_w * p_w;
		float h = half_h * p_h;

		RectF rect = new RectF(half_w - w, half_h - h, half_w - w + w * 2,
				half_h - h + h * 2);

		if (enable_face_color) {
			canvas.drawOval(rect, p_inner);
			canvas.drawOval(rect, p_lip);
		} else {
			canvas.drawOval(rect, p_fg);
		}
	}

	public void onSizeChanged(int w, int h, int old_w, int old_h) {
		invalidate();
	}

	@Override
	public void onDraw(Canvas canvas) {
		clearBackground(canvas);
		drawLip(canvas);
	}

	Runnable _longPressed = new Runnable() { 
	    public void run() {
			enable_face_color = !enable_face_color;
	    }   
	};
		
	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		switch (evt.getAction()) {
		case MotionEvent.ACTION_DOWN:
			handler.postDelayed(_longPressed, 1000);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			handler.removeCallbacks(_longPressed);
			break;
		case MotionEvent.ACTION_CANCEL:
			handler.removeCallbacks(_longPressed);
			break;
		}
		return true;
	}

	public void setVolumeLevel(int val) {

		// normalize value
		double v = (double) val / (double) Short.MAX_VALUE * 1.5;
		if (v < 0.2)
			v = 0;

		// kalman filter
		v = filter.predict_and_correct(v);

		// clipping
		if (v < 0.0)
			v = 0.0;
		if (v > 1.0)
			v = 1.0;

		// apply lip size
		p_h = (float) v * 0.8f + 0.1f;

		handler.post(new Runnable() {
			@Override
			public void run() {
				invalidate();
			}
		});
	}
}
