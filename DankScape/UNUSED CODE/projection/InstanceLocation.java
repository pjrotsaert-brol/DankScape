package dankscape.api.internal.projection;

//import com.acuity.api.rs.interfaces.Locatable;

import dankscape.api.internal.projection.interfaces.Locatable;
import dankscape.api.rs.RSClient;

//import com.acuity.api.rs.utils.LocalPlayer;

/**
 * Created by Zachary Herridge on 7/27/2017.
 */
public class InstanceLocation implements Locatable {

    private int instanceX, instanceY, plane;

    public InstanceLocation(int instanceX, int instanceY, int plane) {
        this.instanceX = instanceX;
        this.instanceY = instanceY;
        this.plane = plane;
    }

    public int getInstanceX() {
        return instanceX;
    }

    public int getInstanceY() {
        return instanceY;
    }

    @Override
    public int getPlane() {
        return plane;
    }

    @Override
    public WorldLocation getWorldLocation() {
        
        WorldLocation worldLocation = new WorldLocation(RSClient.getLocalPlayer().getX(), RSClient.getLocalPlayer().getY());
        int worldX = worldLocation.getWorldX() - (worldLocation.getWorldX() % 192) + instanceX;
        int worldY = worldLocation.getWorldY() - (worldLocation.getWorldY() % 192) + instanceY;
        return new WorldLocation(worldX, worldY, plane);
    }
}
