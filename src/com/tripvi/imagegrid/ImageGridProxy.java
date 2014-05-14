package com.tripvi.imagegrid;

import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;


@Kroll.proxy(creatableInModule=ImagegridModule.class)
public class ImageGridProxy extends TiViewProxy {
	
	private static final String TAG = "Tripvi.ImageGridProxy";
	private ImageGrid grid;
	
	public ImageGridProxy() {
		super();
	}
	
	@Override
	public TiUIView createView(Activity activity) {
		initImageLoader(activity);
		
		grid = new ImageGrid(this);
		return grid;
	}
	
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	
	@Kroll.method
	public void appendImages(Object obj) {
		if (obj != null) {
			grid.appendImages((String [])obj);
		}
	}
	
	@Kroll.method
	public void resetImages() {
		grid.resetImages();
	}
	
}
