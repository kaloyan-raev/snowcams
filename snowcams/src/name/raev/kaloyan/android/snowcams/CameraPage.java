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

import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class CameraPage extends FrameLayout {
	
	private int index;

	public CameraPage(CameraActivity activity, int index) {
		super(activity);
		this.index = index;
	}
	
	public void createContent() {
		CameraActivity activity = (CameraActivity) getContext();
		
		// create the image view
		ImageView imageView = new ImageView(activity);
		imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
		imageView.setVisibility(View.VISIBLE);
		addView(imageView);
		
		// create the progress bar
		ProgressBar progress = new ProgressBar(activity, null, android.R.attr.progressBarStyleLarge);
		progress.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
		progress.setVisibility(View.INVISIBLE);
		addView(progress);
		        
        // check if there is a cached image
        Bitmap cachedBitmap = CameraCache.getCachedBitmap(index);
        if (cachedBitmap != null) {
        	// cached image found - set it to the image view
        	imageView.setImageBitmap(cachedBitmap);
        } else {
        	// load the image from network in a separate thread
	        new DownloadImageTask(activity, index).execute();
        }
	}
	
	public int getIndex() {
		return index;
	}
	
	public ImageView getImageView() {
		return (ImageView) getChildAt(0);
	}
	
	public ProgressBar getProgressBar() {
		return (ProgressBar) getChildAt(1);
	}

}
