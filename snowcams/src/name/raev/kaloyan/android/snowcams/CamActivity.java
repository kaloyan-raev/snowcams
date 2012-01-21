package name.raev.kaloyan.android.snowcams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

public class CamActivity extends Activity implements OnClickListener {

	private ProgressBar mProgressBar;
	private ImageView mImageView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Gesture detection
        final GestureDetector gestureDetector = new GestureDetector(new DoubleTapDetector());
        OnTouchListener gestureListener = new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        // create view
        setContentView(R.layout.main);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mImageView = (ImageView) findViewById(R.id.camImage);
        mImageView.setOnClickListener(this);
        mImageView.setOnTouchListener(gestureListener);
        
        // check if there is a cached image
        Object cachedImage = getLastNonConfigurationInstance();
        if (cachedImage != null) {
        	mImageView.setImageBitmap((Bitmap) cachedImage);
        } else {
	        loadCamera();
        }
    }
    
	@Override
	public Object onRetainNonConfigurationInstance() {
		// cache the image
		return ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
	}
	
	private void loadCamera() {
		// show the progress bar before starting loading the image
        mProgressBar.setVisibility(View.VISIBLE);
        
        // load the image in a separate thread
        new Thread(new Runnable() {
			@Override
			public void run() {
				final Bitmap bitmap = loadImageFromNetwork();
				mImageView.post(new Runnable() {
					@Override
					public void run() {
						if (bitmap == null) {
							// no image was loaded
							mImageView.setImageResource(R.drawable.no_camera);
							mImageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
						} else {
							// set the loaded image
							mImageView.setImageBitmap(bitmap);
							mImageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
						}
						// hide the progress bar
						mProgressBar.setVisibility(View.INVISIBLE);
					}
				});
			}
        }).start();
	}

	private Bitmap loadImageFromNetwork() {
		try {
			URL url = new URL("http://cam.dir.bg/sf/c2/image.jpg");
			InputStream is = (InputStream) url.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	class DoubleTapDetector extends SimpleOnGestureListener {
		
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			// show the camera name on single tap
			Toast.makeText(getApplicationContext(), "Алеко", Toast.LENGTH_SHORT).show();
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// reload the camera image on double tap
			loadCamera();
			return true;
		}
		
	}

	@Override
	public void onClick(View v) {
		// do nothing here - the gesture listener will process the event
	}

}