package com.tripvi.imagegrid;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;

@Kroll.module(name="Imagegrid", id="com.tripvi.imagegrid")
public class ImagegridModule extends KrollModule
{
	// Standard Debugging variables
	private static final String TAG = "Tripvi.ImageGridModule";
	
	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;
	
	public ImagegridModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		// put module init code that needs to run when the application is created
	}

}
