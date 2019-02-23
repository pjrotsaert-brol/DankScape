package dankscape.api.internal.projection.interfaces;



import dankscape.api.internal.projection.LocalPlayer;
import dankscape.api.internal.projection.ProjectionHelpers;
import dankscape.api.internal.projection.Scene;
import dankscape.api.internal.projection.WorldLocation;
import dankscape.api.internal.projection.screen.ScreenLocation;
import java.util.Optional;

/**
 * Created by Eclipseop.
 * Date: 6/8/2017.
 */
public interface Locatable {

    WorldLocation getWorldLocation();

    default Optional<ScreenLocation> getScreenLocation(){
        return ProjectionHelpers.worldToScreen(getWorldLocation());
    }

    default int getPlane() {
        return Scene.getPlane();
    }

    default int distance() {
        return distance(LocalPlayer.getWorldLocation());
    }

    default boolean isOnMiniMap() {
        return getWorldLocation().isOnMiniMap();
    }

    default int distance(Locatable locatable) {
        return Math.toIntExact(Math.round(distancePrecise(locatable)));
    }

    default double distancePrecise() {
        return distancePrecise(LocalPlayer.getWorldLocation());
    }

    default double distancePrecise(Locatable locatable) {
        if(locatable == null)
            return 0.0;

        WorldLocation location1 = locatable.getWorldLocation();
        WorldLocation location2 = getWorldLocation();

        if (location1.getPlane() != location2.getPlane()) {
            return Integer.MAX_VALUE - 1;
        }

        return Math.hypot(location1.getWorldY() - location2.getWorldY(), location1.getWorldX() - location2.getWorldX());
    }
}
