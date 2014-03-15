package net.sabamiso.android.kuchipaku;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class KuchiPakuMainActivity extends Activity implements Runnable{

	KuchiPakuView view;

	AudioRecord rec;
	Thread thread;
	boolean break_flag;
	short[] rec_buf;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		
		super.onCreate(savedInstanceState);

		view = new KuchiPakuView(this);
		setContentView(view);
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		rec_buf = new short[44100 / 10];
		int buf_size = AudioRecord.getMinBufferSize(
				44100,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);
		
		rec = new AudioRecord(
				MediaRecorder.AudioSource.DEFAULT,
				44100,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT, 
				buf_size);
		rec.startRecording();

		thread = new Thread(this, "KuchiPaku::AudioReadThread");
		thread.start();
	}
	
	@Override
	protected void onPause() {
		super.onPause();

		break_flag = true;
		try {
			thread.join();
		} catch (InterruptedException e) {
		}
		
        rec.stop();
        rec.release();
        rec = null;
        
		finish();
	}

	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) return false;

    	return super.onKeyDown(keyCode, event);
    }
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		int action = event.getAction();
		int keyCode = event.getKeyCode();
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
			if (action == KeyEvent.ACTION_UP) {
			}
			if (action == KeyEvent.ACTION_DOWN) {
			}
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if (action == KeyEvent.ACTION_UP) {
			}
			if (action == KeyEvent.ACTION_DOWN) {
			}
			return true;
		default:
			return super.dispatchKeyEvent(event);
		}
	}

	@Override
	public void run() {
    	android.os.Process.setThreadPriority(
  	          android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
    	
		break_flag = false;
		while(!break_flag) {
			int size = rec.read(rec_buf, 0, rec_buf.length);
			if (size <= 0) {
				return;
			}
			
			int max_val = 0;
			for (int i = 0; i < size; ++i) {
				int val = Math.abs(rec_buf[i]);
				if (val > max_val) max_val = val;
			}
			
			if (view != null) {
				view.setVolumeLevel(max_val);
			}
		}		
	}
}
