package dankscape.api.internal.projection.interfaces;


import dankscape.api.internal.projection.screen.geometry.ScreenPolygon;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by Zachary Herridge on 7/10/2017.
 */
public interface Projectable {

    Supplier<ScreenPolygon> getProjectionSupplier();

    default Optional<ScreenPolygon> projectToScreen(){
        return Optional.ofNullable(getProjectionSupplier().get());
    }
}
