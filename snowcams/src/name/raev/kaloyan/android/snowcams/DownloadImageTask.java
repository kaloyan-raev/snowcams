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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class DownloadImageTask extends AsyncTask<Void, Void, Bitmap> {
	
	private CameraActivity activity;
	private int index;
	
	public DownloadImageTask(CameraActivity activity, int index) {
		this.activity = activity;
		this.index = index;
	}

	@Override
	protected void onPreExecute() {
		CameraPage page = activity.getCameraPage(index);
		if (page != null) {
			// show the progress bar before starting loading the image
	        page.getProgressBar().setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		try {
			URL url = new URL(Camera.urlFor(index));
			InputStream is = (InputStream) url.getContent();
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (IOException e) {
			Log.w("CameraActivity", "Cannot load image from camera '" + Camera.labelFor(index) + "'.", e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		CameraPage page = activity.getCameraPage(index);
		if (page != null) {
			ImageView imageView = page.getImageView();
			if (result == null) {
				// no image was loaded
				imageView.setImageResource(R.drawable.no_camera);
				imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
			} else {
				// set the loaded image
				imageView.setImageBitmap(result);
				imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
				// cache the loaded image
				CameraCache.cacheBitmap(index, result);
			}
			// hide the progress bar
			page.getProgressBar().setVisibility(View.INVISIBLE);
		}
	}

}
