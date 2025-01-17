package fr.eurecom.data;

import fr.eurecom.dumdumgame.DynamicBitmap;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;

public class Map extends DynamicBitmap {
    private Rect screen;

    public Map(Bitmap[] images, Point position, int startIndex, int width, int height, Rect screen)
    {
    	super(images, position, startIndex, width, height);
        this.screen = screen;
    } 
    
    public Map(Bitmap image, Point position, int startIndex, int width, int height, Rect screen)
    {
    	super(image, position, startIndex, width, height);
        this.screen = screen;
    }   
    
    public Map(Bitmap[] images, Point position, Rect screen)
    {
    	super(images, position);
        this.screen = screen;
    }
    
    public Map(Bitmap image, Point position, Rect screen)
    {
    	super(image, position);
        this.screen = screen;
    }

    public void updateView(Point point)
    {
        Point screenCenter = new Point((screen.left + screen.right) / 2, (screen.top + screen.bottom) / 2);
        Point movingVector = new Point(screenCenter.x - point.x, screenCenter.y - point.y);
        Point newPosition = new Point(super.getPosition());
        newPosition.x += movingVector.x;
        newPosition.y += movingVector.y;
        super.setPosition(newPosition);
    }
}