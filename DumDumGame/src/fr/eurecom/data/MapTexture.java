package fr.eurecom.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import fr.eurecom.dumdumgame.Obstacles;
import fr.eurecom.dumdumgame.Platforms;
import fr.eurecom.engine.Game.ObstacleIdx;
import fr.eurecom.engine.Polygon;
import fr.eurecom.engine.Segment;
import fr.eurecom.utility.Parameters;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.util.Log;

public class MapTexture {
	private LinkedList<Segment> conveyorList = new LinkedList<Segment>();
	private LinkedList<Point> teleporterList = new LinkedList<Point>();
	private LinkedList<Polygon> wallList = new LinkedList<Polygon>();
	private LinkedList<Polygon> internalWallList = new LinkedList<Polygon>();
	private LinkedList<Polygon> grassList = new LinkedList<Polygon>();
	private LinkedList<Polygon> sandList = new LinkedList<Polygon>();
	private LinkedList<Polygon> waterList = new LinkedList<Polygon>();
	private boolean rain = false;
	private Point startPos = new Point(0, 0);
	private Point holePos = new Point(0, 0);
	private Point mapBottomRight = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
	
	
	public MapTexture(int fileID) {
		try {
			String[] arr;
			InputStream inputStream = Parameters.resource.openRawResource(fileID);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			int n;
			
			
			// create temporary variable to read from files
			LinkedList<Segment> reflectorList = new LinkedList<Segment>();
			

			// start reading data
			// read rain status
			if (Integer.parseInt(reader.readLine()) == 1)
				rain = true;
			else
				rain = false;

			// read start pos
			arr = reader.readLine().split(" ");
			startPos = new Point(Integer.parseInt(arr[0]),
					Integer.parseInt(arr[1]));

			// read hole pos
			arr = reader.readLine().split(" ");
			holePos = new Point(Integer.parseInt(arr[0]),
					Integer.parseInt(arr[1]));

			/************* for engine ***************/
			// read reflector pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				reflectorList.add(ReadSegment(reader)); // LHAn: old version
			}

			// read conveyor pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				conveyorList.add(ReadSegment(reader));
			}

			// read teleporter pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				arr = reader.readLine().split(" ");
				teleporterList.add(new Point(Integer.parseInt(arr[0]), Integer
						.parseInt(arr[1])));
			}

			/************* for graphic ***************/
			// read wall pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				wallList.add(ReadPolygon(reader));
			}

			// read internal wall pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				internalWallList.add(ReadPolygon(reader));
			}

			// read grass pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				grassList.add(ReadPolygon(reader));
			}

			// read sand pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				sandList.add(ReadPolygon(reader));
			}

			// read water pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				waterList.add(ReadPolygon(reader));
			}

			// Shift map downward 1 to nullify negative number
			ShiftAll(1);

			// zoom
			ZoomAll(Parameters.dZoomParam);

			// Shift map downward to move the map to the center
			ShiftAll(Parameters.dShiftParam);
			
			

			//Collections.sort(reflectorList, new SegmentComparable());

			reader.close();
		} catch (Exception ex) {
			Log.e("Map Reader", ex.getMessage());
		}
	}

	
	public Obstacles[] readMapData(int fileID) {
		Obstacles[] obstacleList = null;
		try {
			String[] arr;
			InputStream inputStream = Parameters.resource.openRawResource(fileID);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			int n;
			
			
			// create temporary variable to read from files
			LinkedList<Segment> reflectorList = new LinkedList<Segment>();
			

			// start reading data
			// read rain status
			reader.readLine();

			// read start pos
			reader.readLine();

			// read hole pos
			reader.readLine();

			/************* for engine ***************/
			// read reflector pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				reflectorList.add(ReadSegment(reader)); // LHAn: old version
			}
			
			// read conveyor pos
			reader.readLine();

			// read teleporter pos
			reader.readLine();

			/************* for graphic ***************/
			// read wall pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				wallList.add(ReadPolygon(reader));
			}

			// read internal wall pos
			n = Integer.parseInt(reader.readLine());
			for (int i = 0; i < n; ++i) {
				internalWallList.add(ReadPolygon(reader));
			}

			// read grass pos
			reader.readLine();

			// read sand pos
			reader.readLine();

			// read water pos
			reader.readLine();

			// Shift map downward 1 to nullify negative number

			// zoom

			// Shift map downward to move the map to the center			
			

			reader.close();
			
			
			obstacleList = new Obstacles[ObstacleIdx.numObstacleType()];
			obstacleList[ObstacleIdx.Platform.getValue()] = new Platforms();
			obstacleList[ObstacleIdx.Platform.getValue()].addData(reflectorList, Parameters.dZoomParam, Parameters.dShiftParam);
			mapBottomRight = ((Platforms)(obstacleList[ObstacleIdx.Platform.getValue()])).getBottomRight();

		} catch (Exception ex) {
			Log.e("Map Reader", ex.getMessage());
		}
		
		return obstacleList;
		
	}
	
	

	public Point getMapBottomRight() {
		return mapBottomRight;
	}

//	// public void setReflectorList(LinkedList<Segment> reflectorList) {
//	// this.reflectorList = reflectorList;
//	// }
//
//	// make sure everytime a reflectorList is created, this function must be
//	// called
//	public void addtoReflectorList(Segment reflector) {
//		for (int i = 0; i < this.reflectorList.size(); ++i)
//			if ((this.reflectorList.get(i).getFirstPoint().x > reflector
//					.getFirstPoint().x)
//					|| (this.reflectorList.get(i).getFirstPoint().x == reflector
//							.getFirstPoint().x && this.reflectorList.get(i)
//							.getFirstPoint().y > reflector.getFirstPoint().y)) {
//				this.reflectorList.add(i, reflector);
//				return;
//			}
//		reflectorList.addLast(reflector);
//	}


	public void setConveyorList(LinkedList<Segment> conveyorList) {
		this.conveyorList = conveyorList;
	}

	public LinkedList<Segment> getConveyorList() {
		return conveyorList;
	}

	public void setTeleporterList(LinkedList<Point> teleporterList) {
		this.teleporterList = teleporterList;
	}

	public LinkedList<Point> getTeleporterList() {
		return teleporterList;
	}

	public void setWallList(LinkedList<Polygon> wallList) {
		this.wallList = wallList;
	}

	public LinkedList<Polygon> getWallList() {
		return wallList;
	}

	public void setInternalWallList(LinkedList<Polygon> internalWallList) {
		this.internalWallList = internalWallList;
	}

	public LinkedList<Polygon> getInternalWallList() {
		return internalWallList;
	}

	public void setGrassList(LinkedList<Polygon> grassList) {
		this.grassList = grassList;
	}

	public LinkedList<Polygon> getGrassList() {
		return grassList;
	}

	public void setSandList(LinkedList<Polygon> sandList) {
		this.sandList = sandList;
	}

	public LinkedList<Polygon> getSandList() {
		return sandList;
	}

	public void setWaterList(LinkedList<Polygon> waterList) {
		this.waterList = waterList;
	}

	public LinkedList<Polygon> getWaterList() {
		return waterList;
	}

	public void setRain(boolean rain) {
		this.rain = rain;
	}

	public boolean isRain() {
		return rain;
	}

	public void setStartPos(Point startPos) {
		this.startPos = startPos;
	}

	public Point getStartPos() {
		return startPos;
	}

	public void setHolePos(Point holePos) {
		this.holePos = holePos;
	}

	public Point getHolePos() {
		return holePos;
	}

	private Point ZoomPoint(Point p, int zoomParam) {
		return new Point(p.x * zoomParam, p.y * zoomParam);
	}

	private void ZoomAll(int zoomParam) {
		startPos = ZoomPoint(startPos, zoomParam);
		holePos = ZoomPoint(holePos, zoomParam);

		for (int i = 0; i < conveyorList.size(); ++i) {
			// conveyorList.get(i).setFirstPoint(ZoomPoint(conveyorList.get(i).getFirstPoint(),
			// zoomParam));
			// conveyorList.get(i).setSecondPoint(ZoomPoint(conveyorList.get(i).getSecondPoint(),
			// zoomParam));
		}

		for (int i = 0; i < teleporterList.size(); ++i) {
			Point point = ZoomPoint(teleporterList.get(i), zoomParam);
			teleporterList.get(i).set(point.x, point.y);
		}

		for (int i = 0; i < wallList.size(); ++i) {
			for (int j = 0; j < wallList.get(i).getPoints().size(); ++j) {
				Point point = ZoomPoint(wallList.get(i).getPoints().get(j),
						zoomParam);
				wallList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}

		for (int i = 0; i < internalWallList.size(); ++i) {
			for (int j = 0; j < internalWallList.get(i).getPoints().size(); ++j) {
				Point point = ZoomPoint(internalWallList.get(i).getPoints()
						.get(j), zoomParam);
				internalWallList.get(i).getPoints().get(j)
						.set(point.x, point.y);
			}
		}

		for (int i = 0; i < grassList.size(); ++i) {
			for (int j = 0; j < grassList.get(i).getPoints().size(); ++j) {
				Point point = ZoomPoint(grassList.get(i).getPoints().get(j),
						zoomParam);
				grassList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}

		for (int i = 0; i < sandList.size(); ++i) {
			for (int j = 0; j < sandList.get(i).getPoints().size(); ++j) {
				Point point = ZoomPoint(sandList.get(i).getPoints().get(j),
						zoomParam);
				sandList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}

		for (int i = 0; i < waterList.size(); ++i) {
			for (int j = 0; j < waterList.get(i).getPoints().size(); ++j) {
				Point point = ZoomPoint(waterList.get(i).getPoints().get(j),
						zoomParam);
				waterList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}
	}

	private Point ShiftPoint(Point p, int shiftParam) {
		return new Point(p.x + shiftParam, p.y + shiftParam);
	}

	private void ShiftAll(int shiftParam) {
		startPos = ShiftPoint(startPos, shiftParam);
		holePos = ShiftPoint(holePos, shiftParam);



		for (int i = 0; i < teleporterList.size(); ++i) {
			Point point = ShiftPoint(teleporterList.get(i), shiftParam);
			teleporterList.get(i).set(point.x, point.y);
		}

		for (int i = 0; i < wallList.size(); ++i) {
			for (int j = 0; j < wallList.get(i).getPoints().size(); ++j) {
				Point point = ShiftPoint(wallList.get(i).getPoints().get(j),
						shiftParam);
				wallList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}

		for (int i = 0; i < internalWallList.size(); ++i) {
			for (int j = 0; j < internalWallList.get(i).getPoints().size(); ++j) {
				Point point = ShiftPoint(internalWallList.get(i).getPoints()
						.get(j), shiftParam);
				internalWallList.get(i).getPoints().get(j)
						.set(point.x, point.y);
			}
		}

		for (int i = 0; i < grassList.size(); ++i) {
			for (int j = 0; j < grassList.get(i).getPoints().size(); ++j) {
				Point point = ShiftPoint(grassList.get(i).getPoints().get(j),
						shiftParam);
				grassList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}

		for (int i = 0; i < sandList.size(); ++i) {
			for (int j = 0; j < sandList.get(i).getPoints().size(); ++j) {
				Point point = ShiftPoint(sandList.get(i).getPoints().get(j),
						shiftParam);
				sandList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}

		for (int i = 0; i < waterList.size(); ++i) {
			for (int j = 0; j < waterList.get(i).getPoints().size(); ++j) {
				Point point = ShiftPoint(waterList.get(i).getPoints().get(j),
						shiftParam);
				waterList.get(i).getPoints().get(j).set(point.x, point.y);
			}
		}
	}

	private Segment ReadSegment(BufferedReader reader) throws IOException {
		String[] arr;
		arr = reader.readLine().split(" ");
		Point firstPoint = new Point(Integer.parseInt(arr[0]),
				Integer.parseInt(arr[1]));
		Point secondPoint = new Point(Integer.parseInt(arr[2]),
				Integer.parseInt(arr[3]));
		return new Segment(firstPoint, secondPoint);
	}

	private Polygon ReadPolygon(BufferedReader reader) throws IOException {
		String[] arr;
		arr = reader.readLine().split(" ");
		Polygon myPolygon = new Polygon();
		for (int j = 0; j < arr.length; j += 2)
			myPolygon.getPoints().add(
					new Point(Integer.parseInt(arr[j]), Integer
							.parseInt(arr[j + 1])));
		return myPolygon;
	}

//	public static class SegmentComparable implements Comparator<Segment> {
//		@Override
//		public int compare(Segment a, Segment b) {
//			int aX = a.getFirstPoint().x;
//			int bX = b.getFirstPoint().x;
//
//			if (aX > bX)
//				return 1;
//			else if (aX < bX)
//				return -1;
//			return 0;
//		}
//	}

	public void Show(Canvas canvas) {
		// Show outer walls
		for (int i = 0; i < this.wallList.size(); ++i) {
			this.wallList.get(i).Fill(Parameters.bmpTextureWall, canvas);
		}
		// Show grass
		for (int i = 0; i < this.grassList.size(); ++i) {
			// this.grassList.get(i).Fill(Parameters.bmpTextureGrass, canvas);
			this.grassList.get(i).Fill(Parameters.bmpTextureScenery, canvas,
					TileMode.MIRROR, TileMode.MIRROR);
		}
		// Show inner walls
		for (int i = 0; i < this.internalWallList.size(); ++i) {
			this.internalWallList.get(i)
					.Fill(Parameters.bmpTextureWall, canvas);
		}
		// Show sand
		for (int i = 0; i < this.sandList.size(); ++i) {
			this.sandList.get(i).FillWithImage(Parameters.bmpSand, canvas);
		}
		// Show water
		for (int i = 0; i < this.waterList.size(); ++i) {
			this.waterList.get(i).FillWithImage(Parameters.bmpWater, canvas);
		}
		//
		// for(Segment tmp : reflectorList) {
		// System.out.println(reflectorList.indexOf(tmp));
		// System.out.println("1st pnt: " + tmp.getFirstPoint().x + " " +
		// tmp.getFirstPoint().y);
		// System.out.println("2nd pnt: " + tmp.getSecondPoint().x + " " +
		// tmp.getSecondPoint().y);
		// }
		//
		

		// Show target hole
		/*
		 * Point hole = this.holePos; int holeRadius = Parameters.dBallRadius;
		 * Paint paint = new Paint(); paint.setColor(Color.BLACK);
		 * canvas.drawCircle(hole.x, hole.y, holeRadius, new Paint());
		 */
	}
}