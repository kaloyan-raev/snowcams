package name.raev.kaloyan.android.snowcams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class CamActivity extends Activity {
	
	private ImageView mImageView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mImageView = (ImageView) findViewById(R.id.camImage);
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				final Drawable drawable = loadImageFromNetwork();
				mImageView.post(new Runnable() {
					@Override
					public void run() {
						mImageView.setImageDrawable(drawable);
					}
				});
			}
        }).start();
    }

	private Drawable loadImageFromNetwork() {
		try {
			URL url = new URL("http://cam.dir.bg/sf/c2/image.jpg");
			InputStream is = (InputStream) url.getContent();
			Drawable drawable = Drawable.createFromStream(is, null);
			return drawable;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}