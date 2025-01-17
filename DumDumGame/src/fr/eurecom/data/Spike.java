package fr.eurecom.data;

import android.graphics.Canvas;
import android.graphics.Point;
import fr.eurecom.dumdumgame.DynamicBitmap;
import fr.eurecom.utility.Helper;
import fr.eurecom.utility.Parameters;

public class Spike {

	private Point center;
	private DynamicBitmap spikeImg;
	private int radius;

	public Spike(Point center) {
		this(center, Parameters.dZoomParam);
	}

	public Spike(Point center, int radius) {
		this.center = center;
		this.radius = radius;
		Point imgTopLeft = new Point(center.x - radius, center.y - radius);
		spikeImg = new DynamicBitmap(Parameters.bmpSpike, imgTopLeft,
				Parameters.randomGenerator.nextInt(Parameters.bmpSpike.length),
				2 * radius, 2 * radius);
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(int centerX, int centerY) {
		this.center.x = centerX;
		this.center.y = centerY;

		Point imgTopLeft = new Point(center.x - radius, center.y - radius);
		this.spikeImg.setPosition(imgTopLeft);
	}

	public void show(Canvas canvas, Point offset) {
		spikeImg.show(canvas, offset);
		spikeImg.updateToTheNextImage();
	}

	public Boolean isOverlapped(Point objPos, int range) {
		return Helper.Point_GetDistanceFrom(objPos, this.center) < range
				+ radius / 2 ? true : false;
	}
}
