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
import android.os.AsyncTask;
import android.widget.ImageView;

public class DownloadThumbnailTask extends AsyncTask<Void, Void, Bitmap> {
	
	private ImageView mImage;
	private int mIndex;
	
	public DownloadThumbnailTask(ImageView image, int index) {
		this.mImage = image;
		this.mIndex = index;
	}

	@Override
	protected Bitmap doInBackground(Void... params) {
		return ImageUtil.download(Camera.urlFor(mIndex));
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (result == null) {
			// no image was loaded
			mImage.setImageResource(R.drawable.no_camera);
		} else {
			// set the loaded image
			mImage.setImageBitmap(result);
			// cache the loaded image
			CameraCache.cacheBitmap(mIndex, result);
		}
	}

}
