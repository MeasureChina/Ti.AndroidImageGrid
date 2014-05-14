package com.tripvi.imagegrid;

import org.appcelerator.kroll.KrollDict;
import android.util.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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
	
	List<String> imageUrls = new ArrayList<String>();
	Map<Integer,Integer> selectedImages = new HashMap<Integer,Integer>();
	DisplayImageOptions options;
	
	// Static Properties
	private int layout_image_grid;
	private int layout_image_grid_item;
	private int id_image;
	private int id_overlay;
	private int drawable_ic_stub;
	private int drawable_ic_empty;
	private int drawable_ic_error;
	
	private static final String TAG = "Grid";
	
	public ImageGrid(final ImageGridProxy proxy) {
		super(proxy);
		
		try {
			layout_image_grid = TiRHelper.getResource("layout.image_grid");
			layout_image_grid_item = TiRHelper.getResource("layout.image_grid_item");
			id_image = TiRHelper.getResource("id.image");
			id_overlay = TiRHelper.getResource("id.overlay");
			drawable_ic_stub = TiRHelper.getResource("drawable.ic_stub");
			drawable_ic_empty = TiRHelper.getResource("drawable.ic_empty");
			drawable_ic_error = TiRHelper.getResource("drawable.ic_error");
		}
		catch (ResourceNotFoundException e) {
			Log.e(TAG, "XML resources could not be found!!!");
		}
		
		//
		options = new DisplayImageOptions.Builder()
			.showImageOnLoading(drawable_ic_stub)
			.showImageForEmptyUri(drawable_ic_empty)
			.showImageOnFail(drawable_ic_error)
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
				// toggle selected image overlay
				ViewHolder holder = (ViewHolder) view.getTag();
				boolean selected;
				
				if (selectedImages.get(position) == null) {
					selectedImages.put(position, 1);
					holder.overlay.setVisibility(View.VISIBLE);
					selected = true;
				} else {
					selectedImages.remove(position);
					holder.overlay.setVisibility(View.GONE);
					selected = false;
				}
				
				// trigger event
				if (proxy.hasListeners("click")) {
					KrollDict eventData = new KrollDict();
					eventData.put("index", position);
					eventData.put("selected", selected);
					proxy.fireEvent("click", eventData);
				}
			}
		});
		view.setOnScrollListener(new OnScrollListener() {
			int _lastVisibleItem = 0;
			int _totalItemCount = 0;
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (_lastVisibleItem == firstVisibleItem && _totalItemCount == totalItemCount) {
					// 동일한 이벤트는 무시한다
				} else {
					// trigger event
					if (proxy.hasListeners("scroll")) {
						KrollDict eventData = new KrollDict();
						eventData.put("firstVisibleItem", firstVisibleItem);
						eventData.put("visibleItemCount", visibleItemCount);
						eventData.put("totalItemCount", totalItemCount);
						proxy.fireEvent("scroll", eventData);
					}
					
					_lastVisibleItem = firstVisibleItem;
					_totalItemCount = totalItemCount;
				}
			}
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
		});
	}
	
	public class ImageAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return imageUrls.size();
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
				holder.overlay = (ViewGroup) view.findViewById(id_overlay);
				view.setTag(holder);
			}
			else {
				holder = (ViewHolder) view.getTag();
			}
			
			if (selectedImages.get(position) == null) {
				holder.overlay.setVisibility(View.GONE);
			} else {
				// 선택된 position이면 overlay 표시
				holder.overlay.setVisibility(View.VISIBLE);
			}
			
			imageLoader.displayImage(imageUrls.get(position), holder.imageView, options, new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						
					}
            		
					@Override
					public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
						// holder.progressBar.setVisibility(View.GONE);
					}
            		
					@Override
					public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
						// holder.progressBar.setVisibility(View.GONE);
					}
				}, new ImageLoadingProgressListener() {
					@Override
					public void onProgressUpdate(String imageUri, View view, int current, int total) {
						
					}
				}
			);
			
			return view;
		}
	}
	
	class ViewHolder {
		ImageView imageView;
		ViewGroup overlay;
	}
	
	public void appendImages(String [] images) {
		for (int i=0; i < images.length; i++) {
			imageUrls.add(images[i]);
		}
	}
	
	public void resetImages() {
		imageUrls.clear();
	}
}
