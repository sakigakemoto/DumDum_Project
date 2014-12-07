package fr.eurecom.dumdumgame;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;

import fr.eurecom.allmenus.*;
import fr.eurecom.data.User;
import fr.eurecom.engine.Game;
import fr.eurecom.utility.DataReader;
import fr.eurecom.utility.DataWriter;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;
import fr.eurecom.utility.UserReader;
import fr.eurecom.utility.UserWriter;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class MainActivity extends ActionBarActivity {

	// --------------------------------------------------------------------------
	// System variables
	private RelativeLayout mainLayout;
	private MyView mainView;
	private int timeInterval = 40; // what is that, check later
	private boolean timerOn;

	// --------------------------------------------------------------------------
	// Game variables
	private MainMenu mainMenu;
	// private HelpMenu helpMenu;
	// private UserMenu userMenu;
	// private HighScoreMenu highScoreMenu;
	// private StartMenu startMenu;
	private LoadMenu loadMenu;
	private PauseMenu pauseMenu;

	// private LinkedList<User> userList = new LinkedList<User>();
	private User user;

	private MediaPlayer spMenu;
	private MediaPlayer spBackground;
	private MediaPlayer spVictory;
	private boolean soundOn = true;

	private MssgBox mssgBox;
	private CongratBox congratBox;

	private String currentUserName;
	private int chosenLevel;

	private Game game;
	private Object gameActivity;

	private Point size;

	// public enum StateList {
	// MAIN_MENU, USER_MENU, HIGH_SCORE_MENU, HELP_MENU, START_MENU, LOAD_MENU,
	// PAUSE_MENU, GAME, MSSG_BOX, CONGRAT_BOX
	// }

	public enum StateList {
		MAIN_MENU, LOAD_MENU, PAUSE_MENU, GAME, MSSG_BOX, CONGRAT_BOX
	}

	private StateList state = StateList.MAIN_MENU;

	private Bitmap screenShot = null;
	private boolean isWallThroughAllowed = false;

	// --------------------------------------------------------------------------
	// Private methods
	private void CreateMenus() {
		mainMenu = new MainMenu(new DynamicBitmap(Parameters.bmpBkMainMenu,
				new Point(0, 0), 0, size.x, size.y));

		// helpMenu = new HelpMenu(new DynamicBitmap(Parameters.bmpBkSubMenu,
		// new Point(0, 0), 0, size.x, size.y));
		//
		// userMenu = new UserMenu(new DynamicBitmap(Parameters.bmpBkSubMenu,
		// new Point(0, 0), 0, size.x, size.y), userList);
		//
		// highScoreMenu = new HighScoreMenu(new DynamicBitmap(
		// Parameters.bmpBkSubMenu, new Point(0, 0), 0, size.x, size.y),
		// userList);
		//
		// startMenu = new StartMenu(new DynamicBitmap(Parameters.bmpBkSubMenu,
		// new Point(0, 0), 0, size.x, size.y), this);

		try {
			// loadMenu = new LoadMenu(new
			// DynamicBitmap(Parameters.bmpBkSubMenu,
			// new Point(0, 0), 0, size.x, size.y), getCurrentUser()
			// .getUnlockedLevel());

			loadMenu = new LoadMenu(new DynamicBitmap(Parameters.bmpBkSubMenu,
					new Point(0, 0), 0, size.x, size.y),
					user.getUnlockedLevel());
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		pauseMenu = new PauseMenu(new DynamicBitmap(
				Parameters.bmpBtnTransparent, new Point(0, 0), 0, size.x,
				size.y));

		int tmp;
		tmp = (size.y - Parameters.bmpMssgBox.getHeight()) / 2;
		mssgBox = new MssgBox(new DynamicBitmap(Parameters.bmpMssgBox,
				new Point(0, tmp)));

		congratBox = new CongratBox(new DynamicBitmap(Parameters.bmpCongrat,
				new Point(0, 0), 0, size.x, size.y), this);
	}

	// --------------------------------------------------------------------------
	// Public methods

	public StateList getState() {
		return state;
	}

	public void setState(StateList state) {
		this.state = state;
	}

	public int getChosenLevel() {
		return chosenLevel;
	}

	public void setChosenLevel(int chosenLevel) {
		this.chosenLevel = chosenLevel;
	}

	public String getCurrentUserName() {
		return currentUserName;
	}

	public void setCurrentUserName(String currentUserName) {
		this.currentUserName = currentUserName;
	}

	// public LinkedList<User> getUserList() {
	// return userList;
	// }
	//
	// public void setUserList(LinkedList<User> userList) {
	// this.userList = userList;
	// }

	public User getUser() {
		return this.user;
	}

	public MssgBox getMssgBox() {
		return mssgBox;
	}

	public PauseMenu getPauseMenu() {
		return pauseMenu;
	}

	public void setPauseMenu(PauseMenu pauseMenu) {
		this.pauseMenu = pauseMenu;
	}

	public void captureTheScreen() {
		screenShot = Helper.getScreenShot(screenShot, mainView);
	}

	public MyView getMainView() {
		return mainView;
	}

	public Game getGame() {
		return game;
	}

	public void initGame() {
		game = new Game(gameActivity);
	}

	public void switchSoundOnOff() {
		soundOn = !soundOn;
	}

	public void shutdownApp() {
		// DataWriter.WriteData(userList, Parameters.pthUserData,
		// currentUserName);
		UserWriter.writeUserData(this.getUser(), Parameters.pthUserData);
		flushSound();
		timerOn = false;
		if (screenShot != null) {
			screenShot.recycle();
		}

		// System.runFinalizersOnExit(true);
		System.exit(0);
		this.finish();
	}

	public void flushSound() {
		if (spMenu != null) {
			if (spMenu.isPlaying())
				spMenu.stop();
			spMenu.release();
			spMenu = null;
		}

		if (spBackground != null) {
			if (spBackground.isPlaying())
				spBackground.stop();
			spBackground.release();
			spBackground = null;
		}

		if (spVictory != null) {
			if (spVictory.isPlaying())
				spVictory.stop();
			spVictory.release();
			spVictory = null;
		}
	}

	// public User getCurrentUser() {
	// for (int i = 0; i < userList.size(); ++i) {
	// if (userList.get(i).getName().contentEquals(this.currentUserName))
	// return userList.get(i);
	// }
	// return null;
	// }

	public void updateContent() throws Exception {
		// userMenu.SpawnUserButton();
		// highScoreMenu.FindTotalScore(userList);
		// loadMenu.SpawnLevel(getCurrentUser().getUnlockedLevel());

		loadMenu.SpawnLevel(user.getUnlockedLevel());
	}

	public boolean isWallThroughAllowed() {
		return this.isWallThroughAllowed;
	}

	// --------------------------------------------------------------------------
	// Override methods
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Make activity full-screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		setContentView(R.layout.activity_main);

		// fixed rotation: landscape
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// Get the main layout (self-defined) and add a view to it
		// add id from activity_main.xml --> R.id.mainLayout
		mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
		mainView = new MyView(this);
		mainLayout.addView(mainView);
		mainView.bringToFront();

		gameActivity = this; // ///////////////////////////////////////

		// Initialize system
		App.setMyContext(this);
		Display mainDisplay = getWindowManager().getDefaultDisplay();
		size = new Point();
		mainDisplay.getSize(size);
		try {
			Parameters.initParameters(new Rect(0, 0, size.x, size.y),
					timeInterval);
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		// If internally stored file exists, read that file
		/*
		 * try { FileInputStream fin = openFileInput(Parameters.pthUserData);
		 * fin.close(); currentUserName = DataReader.ReadData(userList,
		 * Parameters.pthUserData); } catch (FileNotFoundException e2) { int tmp
		 * = Parameters.dDataID; currentUserName =
		 * DataReader.ReadRawData(userList, tmp); } catch (IOException e) {
		 * e.printStackTrace(); }
		 */
		try {
			FileInputStream fin = openFileInput(Parameters.pthUserData);
			fin.close(); // try if the file exists
			user = UserReader.readUserData(Parameters.pthUserData);
		} catch (FileNotFoundException e2) {
			user = UserReader.readUserData(Parameters.dUserData);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Create menus
		CreateMenus();

		// Load sound track
		spMenu = MediaPlayer.create(this, Parameters.dMenuSoundtrack);
		spBackground = MediaPlayer.create(this,
				Parameters.dBackgroundSoundtrack);
		spVictory = MediaPlayer.create(this, Parameters.dVictorySoundtrack);

		spMenu.setLooping(true);
		spBackground.setLooping(true);
		spVictory.setLooping(false);

		// Timer for the game
		timerOn = true;
		Thread timerThread = new Thread(new Runnable() {
			@Override
			public void run() {
				while (timerOn) {
					// Do a down on the mutex
					try {
						Parameters.mutex.acquire();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}

					// Critical region---------------------------------
					if (state == StateList.LOAD_MENU)
						mainView.postInvalidate();
					else if (state == StateList.GAME && game.isRunning())
						mainView.postInvalidate();
					// ----------------------------------------------

					// Do an up on the mutex
					Parameters.mutex.release();

					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
		timerThread.setPriority(Thread.MAX_PRIORITY);
		timerThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			if (state == StateList.GAME && !game.isBallRunning()) {
				// Capture the screen
				captureTheScreen();

				state = StateList.PAUSE_MENU;
				mainView.invalidate();
			} else if (state == StateList.PAUSE_MENU) {
				state = StateList.GAME;
				game.resume();
				mainView.invalidate();
			}
		} else if (keyCode == KeyEvent.KEYCODE_0) {
			isWallThroughAllowed = !isWallThroughAllowed;
		} else if (keyCode == KeyEvent.KEYCODE_9) {
			if (state == StateList.GAME && game != null) {
				try {
					// Do a down on the mutex
					Parameters.mutex.acquire();

					// Critical region
					game.levelUp();
					// ------------------

					// Do an up on the mutex
					Parameters.mutex.release();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return true;
	}

	// ///////////////////////////////////////////////////////////////////////////////////
	public class MyView extends View {
		public MyView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {

			Canvas myCanvas = canvas;

			switch (state) {
			case MAIN_MENU:
				// mainMenu.Show(myCanvas, currentUserName, getCurrentUser()
				// .getCurrentLevel());
				mainMenu.Show(myCanvas);
				break;
			// case USER_MENU:
			// userMenu.Show(myCanvas);
			// break;
			// case HIGH_SCORE_MENU:
			// highScoreMenu.Show(myCanvas);
			// break;
			// case HELP_MENU:
			// helpMenu.Show(myCanvas);
			// break;
			// case START_MENU:
			// startMenu.Show(myCanvas);
			// break;
			case LOAD_MENU:
				loadMenu.Show(myCanvas);
				break;
			case GAME:
				try {
					game.show(myCanvas);
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case PAUSE_MENU:
				if (screenShot != null) {
					canvas.drawBitmap(screenShot, 0, 0, new Paint());
					canvas.drawARGB(80, 0, 0, 0);
				}
				pauseMenu.Show(myCanvas);
				break;
			case MSSG_BOX:
				if (screenShot != null) {
					canvas.drawBitmap(screenShot, 0, 0, new Paint());
					canvas.drawARGB(80, 0, 0, 0);
				}
				mssgBox.Show(myCanvas);
				break;
			case CONGRAT_BOX:
				congratBox.Show(myCanvas);
				break;
			}

			if (!soundOn) {
				if (spBackground.isPlaying())
					spBackground = Helper.stopMediaPlayer(spBackground,
							Parameters.dBackgroundSoundtrack);
				if (spVictory.isPlaying())
					spVictory = Helper.stopMediaPlayer(spVictory,
							Parameters.dVictorySoundtrack);
				if (spMenu.isPlaying())
					spMenu = Helper.stopMediaPlayer(spMenu,
							Parameters.dMenuSoundtrack);
			} else {
				switch (state) {
				case MAIN_MENU:
					// case USER_MENU:
					// case HIGH_SCORE_MENU:
					// case HELP_MENU:
					// case START_MENU:
				case LOAD_MENU:
					if (spBackground.isPlaying())
						spBackground = Helper.stopMediaPlayer(spBackground,
								Parameters.dBackgroundSoundtrack);
					if (spVictory.isPlaying())
						spVictory = Helper.stopMediaPlayer(spVictory,
								Parameters.dVictorySoundtrack);
					if (!spMenu.isPlaying())
						spMenu.start();
					break;
				case PAUSE_MENU:
				case GAME:
					if (spVictory.isPlaying())
						spVictory = Helper.stopMediaPlayer(spVictory,
								Parameters.dVictorySoundtrack);
					if (spMenu.isPlaying())
						spMenu = Helper.stopMediaPlayer(spMenu,
								Parameters.dMenuSoundtrack);
					if (!spBackground.isPlaying())
						spBackground.start();
					break;
				case MSSG_BOX:
					break;
				case CONGRAT_BOX:
					if (spBackground.isPlaying())
						spBackground = Helper.stopMediaPlayer(spBackground,
								Parameters.dBackgroundSoundtrack);
					if (spMenu.isPlaying())
						spMenu = Helper.stopMediaPlayer(spMenu,
								Parameters.dMenuSoundtrack);
					if (!spVictory.isPlaying())
						spVictory.start();
					break;
				default:
					break;
				}
			}
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			// Depend on current state, the mouse position invokes different
			// actions
			Point mousePos = new Point((int) event.getX(), (int) event.getY());

			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				switch (state) {
				case MAIN_MENU:
					mainMenu.Action(mousePos, gameActivity);
					break;
				// case USER_MENU:
				// userMenu.Action(mousePos, gameActivity);
				// break;
				// case HIGH_SCORE_MENU:
				// highScoreMenu.Action(mousePos, gameActivity);
				// break;
				// case HELP_MENU:
				// helpMenu.Action(mousePos, gameActivity);
				// break;
				// case START_MENU:
				// startMenu.Action(mousePos, gameActivity);
				// break;
				case LOAD_MENU:
					loadMenu.Action(mousePos, gameActivity);
					break;
				case GAME:
					game.Action(mousePos, gameActivity,
							Game.MouseState.MOUSE_UP);
					break;
				case PAUSE_MENU:
					pauseMenu.Action(mousePos, gameActivity);
					break;
				case MSSG_BOX:
					mssgBox.Action(mousePos, gameActivity);
					break;
				case CONGRAT_BOX:
					congratBox.Action(mousePos, gameActivity);
					break;
				}
				break;
			case MotionEvent.ACTION_DOWN:
				switch (state) {
				case GAME:
					game.Action(mousePos, gameActivity,
							Game.MouseState.MOUSE_DOWN);
					break;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				switch (state) {
				case GAME:
					game.Action(mousePos, gameActivity,
							Game.MouseState.MOUSE_MOVE);
					break;
				}
				break;
			default:
				break;
			}

			return true;
		}
	}
}
