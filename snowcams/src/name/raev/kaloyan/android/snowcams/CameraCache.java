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

import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

public class CameraCache {
	
	private static Map<Integer, Bitmap> cacheMap = new HashMap<Integer, Bitmap>();
	
	public static Bitmap getCachedBitmap(int index) {
		return cacheMap.get(index);
	}
	
	public static Bitmap cacheBitmap(int index, Bitmap image) {
		return cacheMap.put(index, image);
	}

}
