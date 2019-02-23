package dankscape.api.internal.projection.screen.geometry;

import dankscape.api.Misc;
import dankscape.api.internal.projection.screen.ScreenLocation;

import java.awt.*;
import java.util.Collection;

/**
 * Created by Zachary Herridge on 7/10/2017.
 */
public class ScreenPolygon {

    private ScreenLocation[] locations;
    private Polygon polygon;

    public ScreenPolygon(ScreenLocation... locations) {
        this.locations = locations;
    }

    public ScreenPolygon(Collection<ScreenLocation> locations) 
    {
        this.locations = locations.toArray(new ScreenLocation[locations.size()]);
    }

    public ScreenLocation[] getLocations() {
        return locations;
    }

    /*public ScreenLocation randomLocation(){
        return Random.nextLocation(this);
    }*/
    
    public ScreenLocation randomLocation()
    {
        if (locations.length <= 2) 
            return (locations[Misc.random(0, locations.length)]);

        Rectangle r = toPolygon().getBounds();
        int x, y;
        int attempts = 0;
        do {
            x = (int) Misc.random(r.getX(), r.getX() + r.getWidth());
            y = (int) Misc.random(r.getY(), r.getY() + r.getHeight());
            attempts++;
        } while(!contains(x, y) && attempts < 1000); // This is pretty shit, rewrite this.
        return new ScreenLocation(x, y);
    }

    public Polygon toPolygon(){
        if (polygon == null){
            polygon = new Polygon();
            for (ScreenLocation location : locations) {
                polygon.addPoint(location.getX(), location.getY());
            }
        }
        return polygon;
    }

    public ScreenRectangle getBounds(){
        Rectangle bounds = toPolygon().getBounds();
        return new ScreenRectangle(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public boolean contains(ScreenLocation location){
        return contains(location.getX(), location.getY());
    }

    public boolean contains(int x, int y) {
        return toPolygon().contains(x, y);
    }

    public double size() {
        if (locations.length <= 2) return 0;

        // TODO: 11/25/2017 Make better
        return getBounds().size();
    }
}
