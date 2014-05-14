package com.tripvi.imagegrid;

import org.appcelerator.kroll.KrollDict;
import android.util.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;


public class ImageGrid extends TiUIView {
	
	private FrameLayout layout;
	private GridView view;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	
	String[] imageUrls = new String[0];
	DisplayImageOptions options;
	
	// Static Properties
	// public static final String PROPERTY_LEFT_VIEW = "leftView";
	// public static final String PROPERTY_CENTER_VIEW = "centerView";
	// public static final String PROPERTY_RIGHT_VIEW = "rightView";
	// public static final String PROPERTY_LEFT_VIEW_WIDTH = "leftDrawerWidth";
	// public static final String PROPERTY_RIGHT_VIEW_WIDTH = "rightDrawerWidth";
	private int layout_image_grid;
	private int layout_image_grid_item;
	private int id_image;
	private int id_progress;
	
	private static final String TAG = "Grid";
	
	public ImageGrid(final ImageGridProxy proxy) {
		super(proxy);
		
		try {
			layout_image_grid = TiRHelper.getResource("layout.image_grid");
			layout_image_grid_item = TiRHelper.getResource("layout.image_grid_item");
			id_image = TiRHelper.getResource("id.image");
			id_progress = TiRHelper.getResource("id.progress");
		}
		catch (ResourceNotFoundException e) {
			Log.e(TAG, "XML resources could not be found!!!");
		}
		
		//
		options = new DisplayImageOptions.Builder()
			// .showImageOnLoading(R.drawable.ic_stub)
			// .showImageForEmptyUri(R.drawable.ic_empty)
			// .showImageOnFail(R.drawable.ic_error)
			.cacheInMemory(false)
			.cacheOnDisk(false)
			.considerExifParams(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
		
		//
		Activity activity = proxy.getActivity();
		layout = new FrameLayout(activity);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(layoutParams);
		
		// grid를 만든다.
		view = (GridView) activity.getLayoutInflater().inflate(layout_image_grid, layout, false);
		layout.addView(view);
		
		//
		setNativeView(layout);
		
		//
		view.setAdapter(new ImageAdapter());
		view.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (proxy.hasListeners("click")) {
					KrollDict eventData = new KrollDict();
					eventData.put("index", position);
					proxy.fireEvent("click", eventData);
				}
			}
		});
	}
	
	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			View view = convertView;
			if (view == null) {
				Activity activity = proxy.getActivity();
				view = activity.getLayoutInflater().inflate(layout_image_grid_item, parent, false);
				holder = new ViewHolder();
				assert view != null;
				holder.imageView = (ImageView) view.findViewById(id_image);
				holder.progressBar = (ProgressBar) view.findViewById(id_progress);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			
			imageLoader.displayImage(imageUrls[position], holder.imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						holder.progressBar.setProgress(0);
						holder.progressBar.setVisibility(View.VISIBLE);
					}
            		
					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						holder.progressBar.setVisibility(View.GONE);
					}
            		
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						holder.progressBar.setVisibility(View.GONE);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current, int total) {
						holder.progressBar.setProgress(Math.round(100.0f * current / total));
					}
				}
			);
			
			return view;
		}

		class ViewHolder {
			ImageView imageView;
			ProgressBar progressBar;
		}
	}
	
	public void appendImages(String [] images) {
		int len1 = imageUrls.length;
		int len2 = images.length;
		String [] foo = new String[len1 + len2];
		System.arraycopy(imageUrls, 0, foo, 0, len1);
		System.arraycopy(len2, 0, foo, len1, len2);
		imageUrls = foo;
	}
	
	public void resetImages() {
		imageUrls = new String[0];
	}
}
