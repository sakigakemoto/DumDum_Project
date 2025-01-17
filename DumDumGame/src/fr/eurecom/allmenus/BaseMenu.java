package fr.eurecom.allmenus;

//
import java.util.LinkedList;

import fr.eurecom.dumdumgame.Button;
import fr.eurecom.dumdumgame.DynamicBitmap;
import android.graphics.Canvas;
import android.graphics.Point;


public abstract class BaseMenu {
	
	// Variables
	protected LinkedList<Button> buttonList = new LinkedList<Button>();
	protected DynamicBitmap bmpBackground;

	// Constructor
	public BaseMenu(DynamicBitmap bmpBackground) {
		this.bmpBackground = bmpBackground;
	}

	// Methods
	protected void AddButton(Button btn) {
		buttonList.add(btn);
	}

	public void Show(Canvas canvas) {
		bmpBackground.show(canvas);

		for (Button btn : buttonList) {
			btn.show(canvas);
		}
	}

	protected Object ClickedButton(Point p) {
		for (Button btn : buttonList) {
			if (btn.isClicked(p))
				return btn.getID();
		}

		return null;
	}

	public abstract boolean Action(Point p, Object o);
	
	public void recycle() {
		for (Button btn : buttonList) { 
			btn.recycle();
		}
		bmpBackground.recycle();
	}
}
