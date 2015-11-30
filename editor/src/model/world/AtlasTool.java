package model.world;

import java.util.ArrayList;
import java.util.List;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

import model.ModelManager;
import model.world.terrain.atlas.Atlas;
import model.world.terrain.atlas.AtlasExplorer;
import model.world.terrain.atlas.AtlasLayer;
import util.LogUtil;
import util.geometry.geom2d.Point2D;

public class AtlasTool extends PencilTool {
	public enum OPERATION {
		Add_Delete, Propagate_Smooth
	}
	
	private OPERATION operation = OPERATION.Add_Delete;

	public AtlasTool(WorldData world) {
		super(world);
		
	}

	public OPERATION getOperation() {
		return operation;
	}

	public void setOperation(OPERATION operation) {
		this.operation = operation;
	}
	
	@Override
	public void onPrimaryActionStart() {
		switch (operation) {
			case Add_Delete:
				currentWork = new Increment();
				break;
			case Propagate_Smooth:
				currentWork = new Propagate();
				break;
		}
	}

	@Override
	public void onSecondaryActionStart() {
		switch (operation) {
		case Add_Delete:
			currentWork = new Decrement();
			break;
		case Propagate_Smooth:
			currentWork = new Smooth();
			break;
		}
	}
	
	@Override
	public void onPrimaryActionEnd() {
		currentWork = null;
	}

	@Override
	public void onSecondaryActionEnd() {
		currentWork = null;
	}

	private class Increment implements Runnable{

		@Override
		public void run() {
//			LogUtil.info("atlas toolinnnnngggg !!!!");
//			Map<Height, Point2D> heights = getHeights();
			Atlas toPaint = world.getRegions(coord).get(0).getTerrain().getAtlas();
			AtlasLayer layer = toPaint.getLayers().get(1);
			
			
			for (Point2D p : getInvolvedPixels()) {
				AtlasArtisanUtil.incrementPixel(toPaint, p, layer, getAttenuatedIncrement(p));
			}

			world.getTerrainDrawer(world.getRegions(coord).get(0)).updateAtlas();
//			updateParcelsFor(heights);
		}
	}
	
	private class Decrement implements Runnable{

		@Override
		public void run() {
//			Map<Height, Point2D> heights = getHeights(); 
//			for (Height t : heights.keySet()) {
//				t.elevate(-getAttenuatedAmplitude(heights.get(t)));
//			}
//			updateParcelsFor(heights);
		}
	}

	private class Propagate implements Runnable{

		@Override
		public void run() {
//			Map<Height, Point2D> heights = getHeights(); 
//			for (Height t : heights.keySet()) {
//				t.elevate(RandomUtil.between(-1.0, 1.0) * getAttenuatedAmplitude(heights.get(t)));
//			}
//			updateParcelsFor(heights);
		}
	}

	private class Smooth implements Runnable{

		@Override
		public void run() {
//			Map<Height, Point2D> heights = getHeights(); 
//			for (Height t : heights.keySet()) {
//				t.elevate(RandomUtil.between(-1.0, 1.0) * getAttenuatedAmplitude(heights.get(t)));
//			}
//			updateParcelsFor(heights);
		}
	}

	private double getAttenuatedIncrement(Point2D p){
		return Math.round(30*strength * getApplicationRatio(AtlasExplorer.getInTerrainSpace(world.getRegions(p).get(0).getTerrain(), p)));
	}
	
//	public ArrayList<Point2D> getInvolvedPixels() {
//		switch (shape) {
//			case Circle:
//				return explorer.getPixelsInMapSpaceCircle(pencil.getCoord(), pencil.size / 2);
//			case Diamond:
//				return explorer.getPixelsInMapSpaceDiamond(pencil.getCoord(), pencil.size / 2);
//			case Square:
//				return explorer.getPixelsInMapSpaceSquare(pencil.getCoord(), pencil.size / 2);
//			default:
//				throw new RuntimeException();
//		}
//	}

	
	public List<Point2D> getInvolvedPixels() {
		switch (shape) {
			case Circle:
				return AtlasExplorer.getPixelsInAtlasSpaceCircle(world.getRegions(coord).get(0).getTerrain(), coord, size / 2);
			case Diamond:
				return AtlasExplorer.getPixelsInAtlasSpaceDiamond(world.getRegions(coord).get(0).getTerrain(), coord, size / 2);
			case Square:
				return AtlasExplorer.getPixelsInAtlasSpaceSquare(world.getRegions(coord).get(0).getTerrain(), coord, size / 2);
			default:
				throw new RuntimeException();
		}
	}
	
	
}
