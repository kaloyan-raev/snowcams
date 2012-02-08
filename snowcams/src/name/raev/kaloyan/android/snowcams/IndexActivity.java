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
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class IndexActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.index);

	    GridView grid = (GridView) findViewById(R.id.gridview);
	    grid.setAdapter(new ImageAdapter(this));
	    
	    grid.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            Intent intent = new Intent(IndexActivity.this, CameraActivity.class);
	            // pass the position where user clicked - this will open directly the camera at that position. 
	            intent.putExtra("index", position);
	            startActivity(intent);
	        }
	    });
	}
	
	public class ImageAdapter extends BaseAdapter {
		
		private Context mContext;
		private LayoutInflater mInflater;

		public ImageAdapter(Context context) {
			mContext = context;
			mInflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			return Camera.count();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = mInflater.inflate(R.layout.thumb, null);
			ImageView image = (ImageView) convertView.findViewById(R.id.thumbImage);
			TextView text = (TextView) convertView.findViewById(R.id.thumbText);
			
            int spacing = getResources().getDimensionPixelSize(R.dimen.index_spacing);
            int columnWidth = getResources().getDimensionPixelSize(R.dimen.index_column_width);
            // calculate number of columns in the grid
            int numColumns = parent.getWidth() / (columnWidth + spacing);
            // calculate the size of the thumbnail so it best fits in the grid view
            int width = (numColumns == 0) ? 0 : (parent.getWidth() - numColumns * spacing) / numColumns;
            int height = width * 3 / 4;
            
            // set the size of the thumbnail
            convertView.setLayoutParams(new GridView.LayoutParams(width, height));
            
            // adjust the size of the camera label
            text.setHeight(height / 3);
            // set the camera label
            text.setText(Camera.labelFor(position));

	        // check if there is a cached image
	        Bitmap cachedBitmap = CameraCache.getCachedBitmap(position);
	        if (cachedBitmap != null) {
	        	// cached image found - set it to the image view
	        	image.setImageBitmap(cachedBitmap);
	        } else {
	        	image.setImageResource(android.R.drawable.ic_menu_camera);
	        	// load the image from network in a separate thread
		        new DownloadThumbnailTask(image, position).execute();
	        }
	        
	        return convertView;
		}

	}

}
