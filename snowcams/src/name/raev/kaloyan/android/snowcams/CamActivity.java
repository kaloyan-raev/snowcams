/***************************************************************************
 * Copyright (C) 2012  Kaloyan Raev
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***************************************************************************/
package name.raev.kaloyan.android.snowcams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import uk.co.jasonfry.android.tools.ui.SwipeView;
import uk.co.jasonfry.android.tools.ui.SwipeView.OnPageChangedListener;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
        for (int i = 0; i < Camera.count(); i++) {
        	mSwipeView.addView(new FrameLayout(this));
        }
        
        // find the initial page index
        int initialPage = 0;
        // check if there is a cached state
        Object cachedObject = getLastNonConfigurationInstance();
        if (cachedObject != null) {
        	// cache found - scroll to the cached page index
        	SwipeViewCache cache = (SwipeViewCache) cachedObject;
        	initialPage = cache.currentPage;
        }
        
        // scroll to the initial page
    	mSwipeView.scrollToPage(initialPage);
    	
    	// load the images of the initial page and one page before and after
        createCameraView(initialPage);
        if (initialPage != mSwipeView.getPageCount() - 1) {
        	createCameraView(initialPage + 1);
        }
        if (initialPage != 0) {
        	createCameraView(initialPage - 1);
        }
        
        // add listener for dynamically load images on every swipe event
        SwipeImageLoader mSwipeImageLoader = new SwipeImageLoader();
        mSwipeView.setOnPageChangedListener(mSwipeImageLoader);
    }
    
	/*
	 * Cache the current page and all images of the swipe view.
	 */
    @Override
	public Object onRetainNonConfigurationInstance() {
    	SwipeViewCache cache = new SwipeViewCache();
    	cache.currentPage = mSwipeView.getCurrentPage();
    	
		int count = mSwipeView.getPageCount();
		cache.bitmaps = new Bitmap[count];
		for (int i = 0; i < count; i++) {
			// retrieve the next child of the swipe view
			FrameLayout layout = ((FrameLayout) mSwipeView.getChildContainer().getChildAt(i));
			// find the image view
			ImageView imageView = (ImageView) layout.getChildAt(0);
			if (imageView != null) {
				// extract the bitmap from the image view
				BitmapDrawable drawable = ((BitmapDrawable) imageView.getDrawable());
				if (drawable != null) {
					cache.bitmaps[i] = drawable.getBitmap();
				}
			}
		}
		
		return cache;
	}
	
	private void createCameraView(int index) {
		FrameLayout layout = ((FrameLayout) mSwipeView.getChildContainer().getChildAt(index));
		
		// check if the view on this page is already created
		if (layout.getChildCount() == 0) {
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
		        new DownloadImageTask(index).execute();
	        }
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
	
	private class SwipeViewCache {
		int currentPage;
		Bitmap[] bitmaps;
	}
	
	private class SwipeImageLoader implements OnPageChangedListener {

		@Override
		public void onPageChanged(int oldPage, int newPage) {
			if (newPage > oldPage) { 
				// going forwards
				if (newPage != (mSwipeView.getPageCount() - 1)) {
					// load the view after the new page
					createCameraView(newPage + 1);
				}
			} else { 
				// going backwards
				if(newPage != 0) {
					// load the view before the new page
					createCameraView(newPage - 1);
				}
			}
		}
    	
    }
	
	private class DoubleTapDetector extends SimpleOnGestureListener {
		
		/*
		 * Show camera name. 
		 */
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			String label = Camera.labelFor(mSwipeView.getCurrentPage());
			Toast.makeText(getApplicationContext(), label, Toast.LENGTH_SHORT).show();
			return true;
		}

		/*
		 * Reload camera. 
		 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// reload the camera image on double tap
			new DownloadImageTask(mSwipeView.getCurrentPage()).execute();
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
	
	class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
		
		private int index;
		private ImageView imageView;
		private ProgressBar progressBar;
		
		public DownloadImageTask(int index) {
			this.index = index;

			FrameLayout layout = getFrameLayout(index);
			imageView = getImageView(layout);
			progressBar = getProgressBar(layout);
		}

		@Override
		protected void onPreExecute() {
			// show the progress bar before starting loading the image
	        progressBar.setVisibility(View.VISIBLE);
		}

		@Override
		protected Bitmap doInBackground(Void... params) {
			try {
				URL url = new URL(Camera.urlFor(index));
				InputStream is = (InputStream) url.getContent();
				Bitmap bitmap = BitmapFactory.decodeStream(is);
				return bitmap;
			} catch (IOException e) {
				Log.w("CamActivity", "Cannot load image from camera '" + Camera.labelFor(index) + "'.", e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			if (result == null) {
				// no image was loaded
				imageView.setImageResource(R.drawable.no_camera);
				imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
			} else {
				// set the loaded image
				imageView.setImageBitmap(result);
				imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
			}
			// hide the progress bar
			progressBar.setVisibility(View.INVISIBLE);
		}

	}

}