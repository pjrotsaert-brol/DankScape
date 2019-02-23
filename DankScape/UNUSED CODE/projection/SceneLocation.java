package dankscape.api.internal.projection;

import dankscape.api.internal.projection.interfaces.Locatable;
import dankscape.api.internal.projection.screen.ScreenLocation;
import java.util.Optional;

/**
 * Created by Eclipseop.
 * Date: 6/8/2017.
 */
public class SceneLocation implements Locatable {

	private int baseX, baseY;
	private int sceneX;
	private int sceneY;
	private int plane;

	public SceneLocation(int sceneX, int sceneY, int plane) {
		this(sceneX, sceneY, plane, Scene.getBaseX(), Scene.getBaseX());
	}

	public SceneLocation(int sceneX, int sceneY, int plane, int baseX, int baseY) {
		this.sceneX = sceneX;
		this.sceneY = sceneY;
		this.plane = plane;
		this.baseX = baseX;
		this.baseY = baseY;
	}

	public boolean isLoaded() {
		return getSceneX() > 3 && getSceneX() <= 98 && getSceneY() > 3 && getSceneY() <= 98;
	}

	public int getSceneX() {
		return sceneX;
	}

	public int getSceneY() {
		return sceneY;
	}

	public int getBaseX() {
		return baseX;
	}

	public int getBaseY() {
		return baseY;
	}

        @Override
	public int getPlane() {
		return plane;
	}

	public FineLocation getFineLocation(){
        return new FineLocation(getSceneX() * ProjectionHelpers.TILE_PIXEL_SIZE, getSceneY() * ProjectionHelpers.TILE_PIXEL_SIZE, getPlane(), getBaseX(), getBaseY());
        }

        @Override
	public WorldLocation getWorldLocation() {
		return new WorldLocation(getSceneX() + getBaseX(), getSceneY() + getBaseY(), getPlane());
	}


        @Override
	public Optional<ScreenLocation> getScreenLocation() {
		return ProjectionHelpers.sceneToScreen(this);
	}

        @Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof SceneLocation)) return false;

		SceneLocation that = (SceneLocation) o;

		if (getBaseX() != that.getBaseX()) return false;
		if (getBaseY() != that.getBaseY()) return false;
		if (getSceneX() != that.getSceneX()) return false;
		if (getSceneY() != that.getSceneY()) return false;
        return getPlane() == that.getPlane();
        }

        @Override
	public int hashCode() {
		int result = getBaseX();
		result = 31 * result + getBaseY();
		result = 31 * result + getSceneX();
		result = 31 * result + getSceneY();
		result = 31 * result + getPlane();
		return result;
	}
}
