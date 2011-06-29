// DO NOT EDIT THIS FILE - it is automatically generated, edit file under project/java dir
// This string is autogenerated by ChangeAppSettings.sh, do not change spaces amount
package com.tiger.nump;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.EditText;
import android.text.Editable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ViewFlipper;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.view.View.OnKeyListener;
import android.view.animation.AnimationUtils;
import android.text.method.TextKeyListener;
import java.util.LinkedList;
import java.util.Locale;
import java.io.SequenceInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import com.tiger.utils.AssetFileHelper;
import com.tiger.utils.AssetSoundPlayer;

public class MainActivity extends Activity 	implements	SharedPreferences.OnSharedPreferenceChangeListener{
	
	final static String LOG_TAG="MainActivity";
	final static boolean DBG=!AppConfig.RELEASE;
	
	private boolean mSoundEnabled=true;
	private String mSoundTrack;
	
	AssetSoundPlayer mSp;
	boolean mIsLoading=true;
	
	private static final int DIALOG_QUIT_GAME = 1;

	private static final Uri HELP_URI = Uri.parse(
		"file:///android_asset/help/help.html");

	private static final Uri HELP_URI_CN = Uri.parse(
		"file:///android_asset/help/help_cn.html");	

	private final static String PREF_LAST_LEVEL ="LAST_SAVE_LEVEL";
	SharedPreferences mSharedPref;
	
	int mLastLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// fullscreen mode
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//if(Globals.InhibitSuspend)
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		//final SharedPreferences prefs = sharedPrefs;
		//prefs.registerOnSharedPreferenceChangeListener(this);

		mSharedPref =PreferenceManager.getDefaultSharedPreferences(this);
		mSharedPref.registerOnSharedPreferenceChangeListener(this);
		
/*
			
		System.out.println("libSDL: Creating startup screen");
		_layout = new LinearLayout(this);
		_layout.setOrientation(LinearLayout.VERTICAL);
		_layout.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		_layout2 = new LinearLayout(this);
		_layout2.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

		_btn = new Button(this);
		_btn.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		_btn.setText(getResources().getString(R.string.device_change_cfg));
		class onClickListener implements View.OnClickListener
		{
				public MainActivity p;
				onClickListener( MainActivity _p ) { p = _p; }
				public void onClick(View v)
				{
					setUpStatusLabel();
					System.out.println("libSDL: User clicked change phone config button");
					Settings.showConfig(p);
				}
		};
		_btn.setOnClickListener(new onClickListener(this));

		_layout2.addView(_btn);

		_layout.addView(_layout2);
		
		ImageView img = new ImageView(this);

		img.setScaleType(ImageView.ScaleType.FIT_CENTER);
		img.setImageResource(R.drawable.publisherlogo);
		img.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		_layout.addView(img);
*/

		setContentView(R.layout.main);

		//mMainLayout = new FrameLayout(this);
		//mMainLayout =  (LinearLayout)findViewById(R.layout.main);
		
		mGameScreenLayout = (LinearLayout) findViewById(R.id.gamescreen);
		//_videoLayout.addView(_layout);
		
		
		//save handler for static notify method
		notifyHandler = mMainHandler;

		if(mAudioThread == null) // Starting from background (should not happen)
		{
			System.out.println("libSDL: Loading libraries");
			LoadLibraries();
			mAudioThread = new AudioThread(this);
			System.out.println("libSDL: Loading settings");
			Settings.Load(this);
		}

		/*
		if(  !Settings.settingsChanged )
		{
			System.out.println("libSDL: 3-second timeout in startup screen");
			class Callback implements Runnable
			{
				MainActivity p;
				Callback( MainActivity _p ) { p = _p; }
				public void run()
				{
					try {
						Thread.sleep(3000);
					} catch( InterruptedException e ) {};
					if( Settings.settingsChanged )
						return;
					System.out.println("libSDL: Timeout reached in startup screen, process with downloader");
					p.startDownloader();
				}
			};
			Thread changeConfigAlertThread = null;
			changeConfigAlertThread = new Thread(new Callback(this));
			changeConfigAlertThread.start();
		}
		*/
		
		loadFlashScreen();
		
		mSp = new AssetSoundPlayer(this);
		
		//manually load setting
		loadAppSettings(mSharedPref);
		
		//TIGERKING, call initSDL directly
		//initSDL(); //--moved to handler message MSG_START_INIT_GAME
		mMainHandler.sendEmptyMessage(MSG_START_INIT_GAME);
		
		//android.R.drawable.ic_menu_sort_alphabetically
		//android.R.drawable.ic_media_previous
		
	}
	
	private void loadAppSettings(SharedPreferences prefs){
		final String[] prefKeys = {
				"backgroundSoundEnable",
				"soundTrack",
		};

		for (String key : prefKeys)
			onSharedPreferenceChanged(prefs, key);	
		
		//read last played level
		//mLastLevel = prefs.getInt(PREF_LAST_LEVEL, 0);
	}

	public void setUpStatusLabel()
	{
		if(DBG) Log.d(LOG_TAG, "ENTER setUpStatusLabel");
		MainActivity Parent = this; // Too lazy to rename
		if( Parent._btn != null )
		{
			Parent._layout2.removeView(Parent._btn);
			Parent._btn = null;
		}
		if( Parent._tv == null )
		{
			if(DBG) Log.d(LOG_TAG, "CK1 setUpStatusLabel");
			Parent._tv = new TextView(Parent);
			Parent._tv.setMaxLines(1);
			if(DBG) Log.d(LOG_TAG, "CK2 setUpStatusLabel");
			Parent._tv.setText(R.string.init);
			
			if(DBG) Log.d(LOG_TAG, "CK3 setUpStatusLabel");
			if(Parent._layout2!=null) { //++TIGERKING
				Parent._layout2.addView(Parent._tv);
			}
		}
		if(DBG) Log.d(LOG_TAG, "LEAVE setUpStatusLabel");
	}

	public void startDownloader()
	{
		System.out.println("libSDL: Starting data downloader");
		class Callback implements Runnable
		{
			public MainActivity Parent;
			public void run()
			{
				setUpStatusLabel();
				System.out.println("libSDL: Starting downloader");
				if( Parent.downloader == null )
					Parent.downloader = new DataDownloader(Parent, Parent._tv);
			}
		}
		Callback cb = new Callback();
		cb.Parent = this;
		this.runOnUiThread(cb);
	}

        private void initAssets(){
        		if(DBG) Log.d(LOG_TAG,"ENTER initAssets...");
                File dataDir = getDir("data", Context.MODE_PRIVATE);
                File flagFile = new File(dataDir.getAbsolutePath() +"/assetCopied_01");
                if(!flagFile.exists()) {
                		if(DBG) Log.i(LOG_TAG, "++++++++++ init assets to data folder...");
                        try {
                                flagFile.createNewFile();
                        } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                        }

                        AssetFileHelper assetHlp = new AssetFileHelper(this);
                        assetHlp.copyAssets2Data();
                        
                        if(DBG) Log.d(LOG_TAG, "copy assets to data done");
                }else{
                	if(DBG) Log.i(LOG_TAG, "---------- assets already inited. ignore (flag=" + flagFile.getAbsolutePath() +")");
                }
        }

	public void initSDL()
	{
		if(DBG) Log.d(LOG_TAG, "ENTER intiSDL");
		
		initAssets();

		if(sdlInited)
			return;
		System.out.println("libSDL: Initializing video and SDL application");
		sdlInited = true;
		if(true || Globals.UseAccelerometerAsArrowKeys)
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		/*//comment out by TIGERKING
		_videoLayout.removeView(_layout);
		_layout = null;
		_layout2 = null;
		_btn = null;
		_tv = null;
		*/
		
		//_videoLayout = new FrameLayout(this); //--TIGERKING
		//setContentView(_videoLayout);   //--TIGERKING
		
		if(DBG) Log.d(LOG_TAG, "create DemoGLSurfaceView ...");
		mGLView = new DemoGLSurfaceView(this);
		if(DBG) Log.d(LOG_TAG, "create DemoGLSurfaceView done");
		
		//mMainLayout.addView(mGLView);
		//LinearLayout gamescreen =(LinearLayout) findViewById(R.id.gamescreen);
		mGameScreenLayout.addView(mGLView);
		
		//++TIGERKING
		//mGLView.setVisibility(View.INVISIBLE);
		
		// Receive keyboard events
		mGLView.setFocusableInTouchMode(true);
		mGLView.setFocusable(true);
		mGLView.requestFocus();
		
		//show loading screen --TIGERKING
		//mMainLayout.removeView(this._layout);
		//mMainLayout.addView(_layout);
		//++ TIGERKING DONE
		
		//read current level and mov
		mLastLevel = mSharedPref.getInt(PREF_LAST_LEVEL, 0);
		if(DBG) Log.d(LOG_TAG,"read out last saved level is "+ mLastLevel);
		
		Settings.gotoLevel(mLastLevel);
		
		if(DBG) Log.d(LOG_TAG, "initSDL(java) done.");
	}

	@Override
	protected void onPause() {
		if( downloader != null ) {
			synchronized( downloader ) {
				downloader.setStatusField(null);
			}
		}
		_isPaused = true;
		if( mGLView != null )
			mGLView.onPause();
		super.onPause();
		
		mSp.pause();
	}

	@Override
	protected void onResume() {
		if(DBG) Log.d(LOG_TAG, "onResume");
		super.onResume();
		if( mGLView != null )
			mGLView.onResume();
		else
		if( downloader != null ) {
			if(DBG) Log.w(LOG_TAG, "init SDL again...");
			synchronized( downloader ) {
				downloader.setStatusField(_tv);
				if( downloader.DownloadComplete )
					initSDL();
			}
		}
		_isPaused = false;
		if(mSoundEnabled)
		    mSp.resume();
	}
	
	public boolean isPaused()
	{
		return _isPaused;
	}

	@Override
	protected void onDestroy() 
	{
		if(DBG) Log.d(LOG_TAG, "onDestroy...");
		if( downloader != null ) {
			synchronized( downloader ) {
				downloader.setStatusField(null);
			}
		}
		if( mGLView != null )
			mGLView.exitApp();
		super.onDestroy();
		
		mSp.stop();
		
		mLastLevel = Settings.nativeGetCurrentLevel();
		if(DBG) Log.d(LOG_TAG, "SAVE last level " + mLastLevel +" to preferrence");
		Editor editor = mSharedPref.edit();
		editor.putInt(PREF_LAST_LEVEL, mLastLevel);
		editor.commit();
		if(DBG) Log.d(LOG_TAG, "SAVE last level " + mLastLevel +" to preferrence done.");
		
		System.exit(0);
	}

	public void hideScreenKeyboard()
	{
		if(_screenKeyboard == null)
			return;

		synchronized(textInput)
		{
			String text = _screenKeyboard.getText().toString();
			for(int i = 0; i < text.length(); i++)
			{
				DemoRenderer.nativeTextInput( (int)text.charAt(i), (int)text.codePointAt(i) );
			}
		}
		DemoRenderer.nativeTextInputFinished();
		mGameScreenLayout.removeView(_screenKeyboard);
		_screenKeyboard = null;
		mGLView.setFocusableInTouchMode(true);
		mGLView.setFocusable(true);
		mGLView.requestFocus();
	};
	
	public void showScreenKeyboard(final String oldText, boolean sendBackspace)
	{
		if(_screenKeyboard != null)
			return;
		class myKeyListener implements OnKeyListener 
		{
			MainActivity _parent;
			boolean sendBackspace;
			myKeyListener(MainActivity parent, boolean sendBackspace) { _parent = parent; this.sendBackspace = sendBackspace; };
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if ((event.getAction() == KeyEvent.ACTION_UP) && ((keyCode == KeyEvent.KEYCODE_ENTER) || (keyCode == KeyEvent.KEYCODE_BACK)))
				{
					_parent.hideScreenKeyboard();
					return true;
				}
				if ((sendBackspace && event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_DEL || keyCode == KeyEvent.KEYCODE_CLEAR))
				{
					synchronized(textInput) {
						DemoRenderer.nativeTextInput( 8, 0 ); // Send backspace to native code
					}
					return false; // and proceed to delete text in keyboard input field
				}
				return false;
			}
		};
		_screenKeyboard = new EditText(this);
		mGameScreenLayout.addView(_screenKeyboard);
		_screenKeyboard.setOnKeyListener(new myKeyListener(this, sendBackspace));
		_screenKeyboard.setHint(R.string.text_edit_click_here);
		_screenKeyboard.setText(oldText);
		final Window window = getWindow();
		_screenKeyboard.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus)
			{
				if (hasFocus)
					window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
			}
		});
		_screenKeyboard.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.NONE, false));
		_screenKeyboard.setFocusableInTouchMode(true);
		_screenKeyboard.setFocusable(true);
		_screenKeyboard.requestFocus();
	};
	
	@Override
	public boolean onKeyDown(int keyCode, final KeyEvent event) {
		//if(DBG) Log.d(LOG_TAG,"KeyDown(code=" +keyCode +")");
		
		//++TIGERKING
		if( filterKey(keyCode)) return super.onKeyDown(keyCode, event);
		
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(DBG) Log.d(LOG_TAG,"BACK key fired");
			showDialog(DIALOG_QUIT_GAME);
			return true;
		}
		
		// Overrides Back key to use in our app
		if(_screenKeyboard != null)
			_screenKeyboard.onKeyDown(keyCode, event);
		else
		if( mGLView != null )
			 mGLView.nativeKey( keyCode, 1 );
		else
		if( keyCode == KeyEvent.KEYCODE_BACK && downloader != null )
		{ 
			if( downloader.DownloadFailed )
				System.exit(1);
			if( !downloader.DownloadComplete )
			 onStop();
		}
		else
		if( keyListener != null )
		{
			keyListener.onKeyEvent(keyCode);
		}
		return true;
	}
	
	@Override
	public boolean onKeyUp(int keyCode, final KeyEvent event) {
		//if(DBG) Log.d(LOG_TAG,"KeyUp(code=" +keyCode +")");

		if(filterKey(keyCode)) return super.onKeyUp(keyCode, event);
		
		if (_screenKeyboard != null) {
			_screenKeyboard.onKeyUp(keyCode, event);
		} else if (mGLView != null) {
			mGLView.nativeKey(keyCode, 0);
		}
		return true;
	}
	
	@Override
	public boolean dispatchTouchEvent(final MotionEvent ev) {
		if(_screenKeyboard != null)
			_screenKeyboard.dispatchTouchEvent(ev);
		else
		if(mGLView != null)
			mGLView.onTouchEvent(ev);
		else
		if( _btn != null )
			return _btn.dispatchTouchEvent(ev);
		else
		if( touchListener != null )
			touchListener.onTouchEvent(ev);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		if(DBG) Log.d(LOG_TAG,"onConfigurationChange..." );
		super.onConfigurationChanged(newConfig);
		// Do nothing here
		
		
	}
	
	public void setText(final String t)
	{
		class Callback implements Runnable
		{
			MainActivity Parent;
			public String text;
			public void run()
			{
				Parent.setUpStatusLabel();
				if(Parent._tv != null)
					Parent._tv.setText(text);
			}
		}
		Callback cb = new Callback();
		cb.text = new String(t);
		cb.Parent = this;
		this.runOnUiThread(cb);
	}

	public void showTaskbarNotification()
	{
		showTaskbarNotification("SDL application paused", "SDL application", "Application is paused, click to activate");
	}

	// Stolen from SDL port by Mamaich
	public void showTaskbarNotification(String text0, String text1, String text2)
	{
		NotificationManager NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		Intent intent = new Intent(this, MainActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		Notification n = new Notification(R.drawable.icon, text0, System.currentTimeMillis());
		n.setLatestEventInfo(this, text1, text2, pendingIntent);
		NotificationManager.notify(NOTIFY_ID, n);
	}

	public void hideTaskbarNotification()
	{
		NotificationManager NotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		NotificationManager.cancel(NOTIFY_ID);
	}
	
	public void LoadLibraries()
	{
		try
		{
			for(String l : Globals.AppLibraries)
			{
				System.loadLibrary(l);
			}
		}
		catch ( UnsatisfiedLinkError e )
		{
			try {
				System.out.println("libSDL: Extracting APP2SD-ed libs");
				
				InputStream in = null;
				try
				{
					for( int i = 0; ; i++ )
					{
						InputStream in2 = getAssets().open("bindata" + String.valueOf(i));
						if( in == null )
							in = in2;
						else
							in = new SequenceInputStream( in, in2 );
					}
				}
				catch( IOException ee ) { }

				if( in == null )
					throw new RuntimeException("libSDL: Extracting APP2SD-ed libs failed, the .apk file packaged incorrectly");

				ZipInputStream zip = new ZipInputStream(in);

				File cacheDir = getCacheDir();
				try {
					cacheDir.mkdirs();
				} catch( SecurityException ee ) { };
				
				byte[] buf = new byte[16384];
				while(true)
				{
					ZipEntry entry = null;
					entry = zip.getNextEntry();
					/*
					if( entry != null )
						System.out.println("Extracting lib " + entry.getName());
					*/
					if( entry == null )
					{
						System.out.println("Extracting libs finished");
						break;
					}
					if( entry.isDirectory() )
					{
						System.out.println("Warning '" + entry.getName() + "' is a directory");
						continue;
					}

					OutputStream out = null;
					String path = cacheDir.getAbsolutePath() + "/" + entry.getName();

					System.out.println("Saving to file '" + path + "'");

					out = new FileOutputStream( path );
					int len = zip.read(buf);
					while (len >= 0)
					{
						if(len > 0)
							out.write(buf, 0, len);
						len = zip.read(buf);
					}

					out.flush();
					out.close();
				}

				for(String l : Globals.AppLibraries)
				{
					String libname = System.mapLibraryName(l);
					File libpath = new File(cacheDir, libname);
					System.out.println("libSDL: loading lib " + libpath.getPath());
					System.load(libpath.getPath());
					libpath.delete();
				}
			}
			catch ( Exception ee )
			{
				System.out.println("libSDL: Error: " + e.toString());
			}
		}
	};

	public  LinearLayout getVideoLayout() { return mGameScreenLayout; }

	static int NOTIFY_ID = 12367098; // Random ID

	private static DemoGLSurfaceView mGLView = null;
	private static AudioThread mAudioThread = null;
	private static DataDownloader downloader = null;

	private TextView _tv = null;
	private Button _btn = null;
	private LinearLayout _layout = null;
	private LinearLayout _layout2 = null;

	//private LinearLayout mMainLayout = null;
	private	LinearLayout  mGameScreenLayout = null;
	
	private EditText _screenKeyboard = null;
	private boolean sdlInited = false;
	public Settings.TouchEventsListener touchListener = null;
	public Settings.KeyEventsListener keyListener = null;
	boolean _isPaused = false;

	public LinkedList<Integer> textInput = new LinkedList<Integer> ();
	
	
	//================== MENU ====================

	private void notifyEmptyEvent(){
		//tigger a empty event
		//DemoGLSurfaceView.nativeKey( 1, 1 );
	}
	
	private void resetGame(){
		if(DBG) Log.d(LOG_TAG,"java reset game...");
		Settings.nativeSetEnv("RESET", "true");
		notifyEmptyEvent();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (DBG)Log.d(LOG_TAG, "item id " + item.getItemId());
		switch (item.getItemId()) {
		case R.id.menu_reset:

			resetGame();
			return true;

		case R.id.menu_prev:
			Settings.nativeSetEnv("PREV_LEVEL", "true");
			return true;

		case R.id.menu_next:
			Settings.nativeSetEnv("NEXT_LEVEL", "true");
			return true;
			
		case R.id.menu_help:
			startActivity(new Intent(this, com.tiger.utils.HelpActivity.class).setData(getHelpUri()));
			return true;

		case R.id.menu_settings:
			startActivity(new Intent(this, SettingActivity.class));
			return true;

		case R.id.menu_close:
			//System.exit(0);
			saveAppDataBeforeQuit();
			
			mMainHandler.sendEmptyMessageDelayed(MSG_QUIT_APP_FIN, 1);
			finish();
			
			
			// startActivity(new Intent(this, EmulatorSettings.class));
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_QUIT_GAME:
			return createQuitGameDialog();

		}
		return super.onCreateDialog(id);
	}
	
	private Dialog createQuitGameDialog() {
		DialogInterface.OnClickListener l =
			new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0:
							//do nothing
							break;
						case 1:
							finish();
							break;
						}
					}
			};

		return new AlertDialog.Builder(this).
				setTitle(R.string.quit_game_title).
				setItems(R.array.exit_game_options, l).
				create();
	}

	void reloadSettings(){
		
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
		if(DBG) Log.d(LOG_TAG, "onSharedPreferenceChanged(key="+key+")");
		if ("backgroundSoundEnable".equals(key)) {
			mSoundEnabled=prefs.getBoolean(key, true);
			if(DBG) Log.d(LOG_TAG, "sound enabled is " + mSoundEnabled);
			if(mSoundEnabled && (!mIsLoading)){
				mSp.play();
			}else{
				mSp.stop();
			}

		} else if ("soundTrack".equals(key)) {
			mSoundTrack = prefs.getString(key, null);
			if(DBG) Log.d(LOG_TAG,"new selected sound is " + mSoundTrack);
			if(mSoundEnabled && (!mIsLoading)){
				mSp.play(mSoundTrack);
			}

		} else if ("XXXX".equals(key)) {
		}
	}
	
	
	//// for UI sync update
	
	final static int MSG_HIDE_FLASH_SCREEN = 1;
	final static int MSG_START_INIT_GAME  =2;
	final static int MSG_QUIT_APP_FIN = 999;
	private final Handler mMainHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_HIDE_FLASH_SCREEN:
				if(DBG) Log.d(LOG_TAG, "hide loading screen");
				//Hide flash view 
				//if(_videoLayout.findViewById(FLASH_VIEW_ID)!=null){
					//mMainLayout.removeView(_layout);
					//_videoLayout.addView(mGLView);
					//mGLView.setVisibility(View.VISIBLE);
					
				//}
				switch2GameView();
					
				if(mSoundEnabled && (!mIsLoading)) {
					//if(DBG) Log.d(LOG_TAG, "play sound track " + mSoundTrack);
					mSp.play(mSoundTrack);
				}

				break;
			case MSG_START_INIT_GAME:
				
				initSDL();
				break;
			
			case 100:
				if(DBG) Log.d(LOG_TAG,"force to game screen after 3 seconds timeout");
				switch2GameView();
				break;
				
			case MSG_QUIT_APP_FIN:
				System.exit(0);
				break;
				
			default:
				if (DBG) Log.d(LOG_TAG, "Receive UNKNOWN message " +msg.what);				
				break;
			}
		}
	};	
	
	//called by video when native loading is done . notify main to hide loading screen
	static Handler notifyHandler=null;
	public static void onGameStart(){
		if(notifyHandler!=null){
			notifyHandler.sendEmptyMessage(MSG_HIDE_FLASH_SCREEN);
		}		
	}

	private final static int FLASH_VIEW_ID=123;
	void loadFlashScreen(){
/*
		//_videoLayout
		_layout = new LinearLayout(this);
		//_layout.setOrientation(LinearLayout.VERTICAL);
		_layout.setLayoutParams(new LinearLayout.LayoutParams( ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
		_layout.setGravity(Gravity.CENTER);
		_layout.setBackgroundColor(0xffffffff);
		
		ImageView loading = new ImageView(this);
		//loading.setImageResource(R.drawable.loading);
		loading.setImageResource(R.drawable.loading40);
		_layout.addView(loading);

		_layout.setId(FLASH_VIEW_ID);
		
		mMainLayout.addView(_layout);
*/
		//mMainHandler.sendEmptyMessageDelayed(100, 3000);
	}

	
	void switch2GameView(){
		mIsLoading =false;
		/*
		if(DBG) Log.d(LOG_TAG,"switch2GameView ...");
        // Get the ViewFlipper from the layout
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.viewflipper);
        // Set an animation from res/anim: I pick push left out
        vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.zoom_enter));
        vf.showNext();
        */
		 LinearLayout flashLayout = (LinearLayout)findViewById(R.id.flash);
		 flashLayout.setVisibility(View.INVISIBLE);
   }
	
	
	private boolean filterKey(int keyCode){
		return( (keyCode == KeyEvent.KEYCODE_MENU) 
		||(keyCode == KeyEvent.KEYCODE_VOLUME_UP) 
		||(keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) 
		||(keyCode == KeyEvent.KEYCODE_CAMERA)
		);
		
	}
	private Uri getHelpUri(){
		
		String contry = getResources().getConfiguration().locale.getCountry();
		if(DBG) Log.d(LOG_TAG, "contry=[" + contry +"]" + " chinese=" + Locale.CHINESE.getCountry() );
		if(contry.equals(Locale.CHINESE.getCountry()) || contry.equals(Locale.CHINA.getCountry())) {
				return HELP_URI_CN;
		}
		else {
				return HELP_URI;
		}
	}
	
	private void saveAppDataBeforeQuit(){
		mLastLevel = Settings.nativeGetCurrentLevel();
		if(DBG) Log.d(LOG_TAG, "SAVE last level " + mLastLevel +" to preferrence");
		Editor editor = mSharedPref.edit();
		editor.putInt(PREF_LAST_LEVEL, mLastLevel);
		editor.commit();
		if(DBG) Log.d(LOG_TAG, "SAVE last level " + mLastLevel +" to preferrence done.");
	}
}