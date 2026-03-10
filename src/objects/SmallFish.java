package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class SmallFish extends Fish {

    private static SmallFish sf = new SmallFish(null);

    private boolean facingRight = false;

    private SmallFish(Room room) {
        super(room);
        this.maxLightCarry = 1;
        this.maxHeavyCarry = 0;
    }

    public static SmallFish getInstance() {
        return sf;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    @Override
    public String getName() {
        return facingRight ? "smallFishRight" : "smallFishLeft";
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean canPush(GameObject obj, Direction dir) {
        return (obj.getWeight() == Weight.LIGHT || obj.getWeight() == Weight.NOWEIGHT) && obj instanceof MovableObject;
    }
}
