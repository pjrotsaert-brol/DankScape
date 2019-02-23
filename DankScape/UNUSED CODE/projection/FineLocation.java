package dankscape.api.internal.projection;


import dankscape.api.internal.projection.interfaces.Locatable;
import dankscape.api.internal.projection.screen.ScreenLocation;

import java.util.Optional;

/**
 * Created by Zach on 7/1/2017.
 */
public class FineLocation implements Locatable {

    private int fineX, fineY, plane;
    private int baseX, baseY;

    public FineLocation(int fineX, int fineY, int plane) {
        this(fineX, fineY, plane, Scene.getBaseX(), Scene.getBaseY());
    }

    public FineLocation(int fineX, int fineY, int plane, int baseX, int baseY) {
        this.fineX = fineX;
        this.fineY = fineY;
        this.plane = plane;
        this.baseX = baseX;
        this.baseY = baseY;
    }

    public int getPlane() {
        return plane;
    }

    public int getFineX() {
        return fineX;
    }

    public int getFineY() {
        return fineY;
    }

    public SceneLocation getSceneLocation(){
        return new SceneLocation(fineX / ProjectionHelpers.TILE_PIXEL_SIZE, fineY / ProjectionHelpers.TILE_PIXEL_SIZE, plane, baseX, baseY);
    }

    @Override
    public WorldLocation getWorldLocation(){
        return getSceneLocation().getWorldLocation();
    }

    @Override
    public Optional<ScreenLocation> getScreenLocation() {
        return ProjectionHelpers.fineToScreen(this);
    }
}
