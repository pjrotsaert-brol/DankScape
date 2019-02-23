package dankscape.api.internal.projection.screen;

import dankscape.api.internal.projection.ProjectionHelpers;

import java.util.Optional;

/**
 * Created by Zachary Herridge on 7/10/2017.
 */
public class Screen3DLocation {

    private int x;
    private int y;
    private int z;

    public Screen3DLocation(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public int getZ() {
        return z;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Optional<ScreenLocation> toScreenLocation(){
        return ProjectionHelpers.fineToScreen(x, y, z);
    }

    public Screen3DLocation rotate(int orientation) {
        int sin = ProjectionHelpers.SINE[orientation];
        int cos = ProjectionHelpers.COSINE[orientation];
        return new Screen3DLocation(x * cos + z * sin >> 16, y, z * cos - x * sin >> 16);
    }

    public int[] toArray() {
        return new int[]{x, y, z};
    }
}
