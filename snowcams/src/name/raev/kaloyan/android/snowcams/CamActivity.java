package name.raev.kaloyan.android.snowcams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import uk.co.jasonfry.android.tools.ui.SwipeView;
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

	private static final String[] mUrls = {
		"http://cam.dir.bg/sf/c2/image.jpg", 
		"http://media.borovets-bg.com/cams/channel?channel=41"
	};
	
	private static final String[] mLabels = {
		"х. Алеко", 
		"Боровец"
	};
	
	private SwipeView mSwipeView;
	
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
        mSwipeView = (SwipeView) findViewById(R.id.swipe_view);
        mSwipeView.setOnClickListener(this);
        mSwipeView.setOnTouchListener(gestureListener);
        
        // initialize the swipe view with empty children
        for (int i = 0; i < 2; i++) {
        	mSwipeView.addView(new FrameLayout(this));
        }
        
        // load the images of the first two cameras
        createCameraView(0);
        createCameraView(1);
        
        // check if page index is cached
        Object cachedObject = getLastNonConfigurationInstance();
        if (cachedObject != null) {
        	// cache found - scroll to the cached page index
        	SwipeViewCache cache = (SwipeViewCache) cachedObject;
        	mSwipeView.scrollToPage(cache.currentPage);
        }
    }
    
	/*
	 * Cache the current page and all images of the swipe view.
	 */
    @Override
	public Object onRetainNonConfigurationInstance() {
    	SwipeViewCache cache = new SwipeViewCache();
    	cache.currentPage = mSwipeView.getCurrentPage();
    	
		int count = mSwipeView.getChildContainer().getChildCount();
		cache.bitmaps = new Bitmap[count];
		for (int i = 0; i < count; i++) {
			// retrieve the next child of the swipe view
			FrameLayout layout = ((FrameLayout) mSwipeView.getChildContainer().getChildAt(i));
			// find the image view
			ImageView imageView = (ImageView) layout.getChildAt(0);
			if (imageView != null) {
				// extract the bitmap from the image view
				cache.bitmaps[i] = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
			}
		}
		
		return cache;
	}
	
	private void createCameraView(int index) {
		FrameLayout layout = ((FrameLayout) mSwipeView.getChildContainer().getChildAt(index));
		
		// create the image view
		ImageView imageView = new ImageView(this);
		imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
		imageView.setVisibility(View.VISIBLE);
		layout.addView(imageView);
		
		// create the progress bar
		ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleLarge);
		progress.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		progress.setVisibility(View.INVISIBLE);
		layout.addView(progress);
		        
        // check if there is a cached image
        Bitmap cachedBitmap = getCachedBitmap(index);
        if (cachedBitmap != null) {
        	// cached image found - set it to the image view
        	imageView.setImageBitmap(cachedBitmap);
        } else {
        	// load the image from network in a separate thread
	        loadCamera(index);
        }
	}
	
	private Bitmap getCachedBitmap(int index) {
		Object cachedObject = getLastNonConfigurationInstance();
        if (cachedObject != null) {
        	SwipeViewCache cache = (SwipeViewCache) cachedObject;
        	return cache.bitmaps[index];
        }
        return null;
	}
	
	private void loadCamera(final int index) {
		FrameLayout layout = getFrameLayout(index);
		final ImageView imageView = getImageView(layout);
		final ProgressBar progressBar = getProgressBar(layout);
		
		// show the progress bar before starting loading the image
        progressBar.setVisibility(View.VISIBLE);
        
        // load the image in a separate thread
        new Thread(new Runnable() {
			@Override
			public void run() {
				final Bitmap bitmap = loadImageFromNetwork(index);
				imageView.post(new Runnable() {
					@Override
					public void run() {
						if (bitmap == null) {
							// no image was loaded
							imageView.setImageResource(R.drawable.no_camera);
							imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
						} else {
							// set the loaded image
							imageView.setImageBitmap(bitmap);
							imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
						}
						// hide the progress bar
						progressBar.setVisibility(View.INVISIBLE);
					}
				});
			}
        }).start();
	}

	private Bitmap loadImageFromNetwork(int index) {
		try {
			URL url = new URL(mUrls[index]);
			InputStream is = (InputStream) url.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	class SwipeViewCache {
		int currentPage;
		Bitmap[] bitmaps;
	}
	
	class DoubleTapDetector extends SimpleOnGestureListener {
		
		/*
		 * Show camera name. 
		 */
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			String label = mLabels[mSwipeView.getCurrentPage()];
			Toast.makeText(getApplicationContext(), label, Toast.LENGTH_SHORT).show();
			return true;
		}

		/*
		 * Reload camera. 
		 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// reload the camera image on double tap
			loadCamera(mSwipeView.getCurrentPage());
			return true;
		}
		
	}

	@Override
	public void onClick(View v) {
		// do nothing here - the gesture listener will process the event
	}
	
	private FrameLayout getFrameLayout(int index) {
		return (FrameLayout) mSwipeView.getChildContainer().getChildAt(index);
	}
	
	private ImageView getImageView(FrameLayout layout) {
		return (ImageView) layout.getChildAt(0);
	}
	
	private ProgressBar getProgressBar(FrameLayout layout) {
		return (ProgressBar) layout.getChildAt(1);
	}

}