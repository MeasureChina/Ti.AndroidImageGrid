// This is a test harness for your module
// You should do something interesting in this harness 
// to test out the module and to provide instructions 
// to users on how to use it by example.


// open a single window
var module = require('com.tripvi.imagegrid');

var win = Ti.UI.createWindow({
	backgroundColor: 'white',
	navBarHidden: true,
});

var grid = module.createImageGrid({
	
});





win.add(grid);
win.open();
