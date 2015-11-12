package model.world;

import java.util.ArrayList;
import java.util.List;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;

import model.ES.serial.EntityInstance;
import util.LogUtil;
import util.geometry.geom2d.Point2D;

public class WorldData {
	private List<Region> drawnRegions = new ArrayList<Region>();
	private Region lastRegion;
	private RegionManager regionManager = new RegionManager();
	private final EntityData ed;
	private EntityId worldEntity;
	
	public WorldData(EntityData ed) {
		this.ed = ed;
	}
	
	public void setWorldEntity(EntityId eid){
		this.worldEntity = eid;
	}
	
	public void setCoord(Point2D coord){
		Region actualRegion = regionManager.getRegion(coord);
		
		if(actualRegion != lastRegion){
			// We pass from a region to another
			lastRegion = actualRegion;
			List<Region> toDraw = get9RegionsAround(coord);
			for(Region r : toDraw)
				if(!drawnRegions.contains(r))
					drawRegion(r);
			
			for(Region r : drawnRegions)
				if(!toDraw.contains(r))
					undrawRegion(r);
			
			drawnRegions = toDraw;
		}
	}
	
	public void drawRegion(Region region){
		LogUtil.info("draw region "+region.getId());
		for(EntityInstance ei : region.getEntities())
			ei.instanciate(ed, worldEntity);
	}
	
	private void undrawRegion(Region region){
		LogUtil.info("undraw region "+region.getId());
		for(EntityInstance ei : region.getEntities())
			ei.uninstanciate(ed);
	}
	
	private List<Region> get9RegionsAround(Point2D coord){
		List<Region> res = new ArrayList<Region>();
		int r = Region.RESOLUTION;
		for(int x = -r; x <= r; x += r)
			for(int y = -r; y <= r; y += r)
				res.add(regionManager.getRegion(coord.getAddition(x, y)));
		return res;
	}
	
	public Region getRegion(Point2D coord){
		return regionManager.getRegion(coord);
	}
}