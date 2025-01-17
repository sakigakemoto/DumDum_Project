package fr.eurecom.utility;

import fr.eurecom.connectivity.DeviceDetailFragment;
import fr.eurecom.dumdumgame.App;
import fr.eurecom.dumdumgame.GameManager;
import fr.eurecom.engine.Character;
import fr.eurecom.engine.Line;
import fr.eurecom.utility.Parameters.tagConnect;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Paint.Style;
import android.media.MediaPlayer;
import android.widget.Toast;

public class Helper {
	public static double Point_GetDistanceFrom(Point p, Point otherPoint) {
		return Math.sqrt((p.x - otherPoint.x) * (p.x - otherPoint.x)
				+ (p.y - otherPoint.y) * (p.y - otherPoint.y));
	}

	public static double Point_GetDistanceFrom(Point p, Line line)
			throws Exception {
		Point projection = Point_GetProjectionOn(p, line);
		return Point_GetDistanceFrom(p, projection);
	}

	public static Point Point_GetProjectionOn(Point p, Line line)
			throws Exception {
		Line temp = new Line(p, line.GetDirectionalVector().x,
				line.GetDirectionalVector().y);
		return line.GetCommonPointWith(temp);
	}

	public static Point Point_GetMirrorFrom(Point p, Point otherPoint) {
		return new Point(2 * otherPoint.x - p.x, 2 * otherPoint.y - p.y);
	}

	public static void drawTextWithMultipleLines(Canvas canvas, String text,
			Point point, Paint paint) {
		String[] arr = text.split(System.getProperty("line.separator"));

		for (int i = 0; i < arr.length; ++i) {
			int offset = (int) paint.getTextSize() * i * (1 + 1 / 3);
			canvas.drawText(arr[i], point.x, point.y + offset, paint);
		}
	}

	public static MediaPlayer stopMediaPlayer(MediaPlayer m, int soundTrackId) {
		Context context = App.getMyContext();
		if (m == null)
			return null;

		boolean looping = m.isLooping();
		if (m.isPlaying())
			m.stop();
		m.release();

		MediaPlayer m1 = MediaPlayer.create(context, soundTrackId);
		m1.setLooping(looping);
		return m1;
	}

	// hue-range: [0, 360] -> Default = 0
	public static void changeHue(Bitmap bitmap, float hue) {
		Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
		final int width = newBitmap.getWidth();
		final int height = newBitmap.getHeight();
		float[] hsv = new float[3];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = newBitmap.getPixel(x, y);
				Color.colorToHSV(pixel, hsv);
				hsv[0] = hue;
				newBitmap.setPixel(x, y,
						Color.HSVToColor(Color.alpha(pixel), hsv));
			}
		}

		bitmap.recycle();
//		bitmap = null;
		bitmap = newBitmap;

//		return newBitmap;
	}

	static public void drawBlurCircle(Canvas canvas, Point position, Point offset, int alpha) {
		drawBlurCircle(canvas, position, offset, alpha, Parameters.dBallRadius);
	}
	
	static public void drawBlurCircle(Canvas canvas, Point position, Point offset, int alpha, int radius) {
		Point tmp = new Point(position.x + offset.x, position.y + offset.y);
		Paint paint = new Paint();
		paint.setStyle(Style.FILL_AND_STROKE);
		paint.setColor(Color.WHITE);
		paint.setAlpha(alpha);
		canvas.drawCircle(tmp.x, tmp.y, radius, paint);
	}
	

	
	
	
	/********* Message Senders for Duo Mode ************/
	static public void sendFinMoveDuoMode(Point position) {
		//for duoMode
		String msg = tagConnect.FINMOVE + position.x + "," + position.y;
		if(DeviceDetailFragment.server!=null) {
			DeviceDetailFragment.server.sendMessage(msg);
			try {
				Thread.sleep(Parameters.sleepPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			if (DeviceDetailFragment.client!= null) {
				DeviceDetailFragment.client.sendMessage(msg);
				try {
					Thread.sleep(Parameters.sleepPeriod);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static public void sendDirectionDuoMode(Point position, Point direction) {
		String msg = position.x + "," + position.y + "," + direction.x + "," + direction.y;
		if(DeviceDetailFragment.server!=null) {
			
			DeviceDetailFragment.server.sendMessage(tagConnect.STARTMOVE + msg);
			try {
				Thread.sleep(Parameters.sleepPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} else {
			if (DeviceDetailFragment.client!= null) {
				DeviceDetailFragment.client.sendMessage(tagConnect.STARTMOVE + msg);
				try {
					Thread.sleep(Parameters.sleepPeriod);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
//	static public void sendChangeGearDuoMode(Character.gearState gear) {
//		String msg = gear.toString();
//		if(DeviceDetailFragment.server!=null) {
//			
//			DeviceDetailFragment.server.sendMessage(tagConnect.CHANGEGEAR + msg);
//			try {
//				Thread.sleep(Parameters.sleepPeriod);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		} else {
//			if (DeviceDetailFragment.client!= null) {
//				DeviceDetailFragment.client.sendMessage(tagConnect.CHANGEGEAR + msg);
//				try {
//					Thread.sleep(Parameters.sleepPeriod);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
	static public void onConnectionError() {
		if (DeviceDetailFragment.client !=null)
			DeviceDetailFragment.client = null;
		if (DeviceDetailFragment.server !=null)
			DeviceDetailFragment.server = null;
		GameManager.setCurrentState(GameManager.GameState.MAIN_MENU);
		GameManager.mainView.invalidate();
		Toast.makeText(App.getMyContext(), "Connection Lost. Please reset your Wifi setting.", Toast.LENGTH_LONG).show();
	}
	
	static public Boolean isInDuoMode() {
		if (DeviceDetailFragment.client !=null || DeviceDetailFragment.server !=null)
			return true;
		return false;
	}
}
