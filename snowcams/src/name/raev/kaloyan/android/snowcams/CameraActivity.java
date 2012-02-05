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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

public class CameraActivity extends Activity implements OnClickListener {

	private ViewPager mPager;
	
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
        setContentView(R.layout.camera);
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new CameraPageAdapter());
        mPager.setPageMargin(8);
        
        mPager.setOnClickListener(this);
        mPager.setOnTouchListener(gestureListener);
        
        // find the initial page index - first check if passed with the intent
        int initialPage = getIntent().getIntExtra("index", 0);
        // check if there is a cached state
        Object cachedObject = getLastNonConfigurationInstance();
        if (cachedObject != null) {
        	// cache found - scroll to the cached page index
        	initialPage = (Integer) cachedObject;
        }
        
        // scroll to the initial page
    	mPager.setCurrentItem(initialPage);
    }
    
	/*
	 * Cache the current page index.
	 */
    @Override
	public Object onRetainNonConfigurationInstance() {
    	return mPager.getCurrentItem();
	}

	@Override
	public void onClick(View v) {
		// do nothing here - the gesture listener will process the event
	}
	
	public CameraPage getCameraPage(int index) {
		for (int i = 0; i < mPager.getChildCount(); i++) {
			CameraPage page = (CameraPage) mPager.getChildAt(i);
			if (page.getIndex() == index) {
				return page;
			}
		}
		return null;
	}
	
	private class CameraPageAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return Camera.count();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			CameraPage page = new CameraPage(CameraActivity.this, position);
			container.addView(page);
			page.createContent();
			return page;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((FrameLayout) object);
		}
		
	}
	
	private class DoubleTapDetector extends SimpleOnGestureListener {
		
		/*
		 * Show camera name. 
		 */
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			String label = Camera.labelFor(mPager.getCurrentItem());
			Toast.makeText(getApplicationContext(), label, Toast.LENGTH_SHORT).show();
			return true;
		}

		/*
		 * Reload camera. 
		 */
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// reload the camera image on double tap
			new DownloadImageTask(CameraActivity.this, mPager.getCurrentItem()).execute();
			return true;
		}
		
	}
	
}