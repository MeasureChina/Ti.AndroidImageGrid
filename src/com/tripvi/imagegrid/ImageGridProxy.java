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
		initImageGrid();
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
	
	public void initImageGrid() {
		if (grid == null) {
			grid = new ImageGrid(this);
		}
	}
	
	
	@Kroll.method
	public void appendImages(Object args) {
		Object [] argArray = (Object[]) args;
		String [] images = new String [argArray.length];
		
		for (int i=0; i < argArray.length; i++) {
			images[i] = (String) argArray[i];
		}
		
		initImageGrid();
		grid.appendImages(images);
	}
	
	@Kroll.method
	public void resetImages() {
		initImageGrid();
		grid.resetImages();
	}
	
}
