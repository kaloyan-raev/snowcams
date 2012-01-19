package name.raev.kaloyan.android.snowcams;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class CamActivity extends Activity {

	private ProgressBar mProgressBar;
	private ImageView mImageView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mProgressBar = (ProgressBar) findViewById(R.id.progress);
        mImageView = (ImageView) findViewById(R.id.camImage);
        
        // show the progress bar before starting loading the image
        mProgressBar.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.INVISIBLE);
        
        new Thread(new Runnable() {
			@Override
			public void run() {
				final Drawable drawable = loadImageFromNetwork();
				mImageView.post(new Runnable() {
					@Override
					public void run() {
						if (drawable == null) {
							// no image was loaded
							mImageView.setImageResource(R.drawable.no_camera);
							mImageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
						} else {
							// set the loaded image
							mImageView.setImageDrawable(drawable);
							mImageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
						}
						// hide the progress bar
						mImageView.setVisibility(View.VISIBLE);
						mProgressBar.setVisibility(View.INVISIBLE);
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