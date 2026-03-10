package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class BigFish extends Fish {

    private static BigFish bf = new BigFish(null);
    private boolean facingRight = false;

    private BigFish(Room room) {
        super(room);
        this.maxLightCarry = 99; //Não tenho a certeza se faz sentido mas no enunciado não existe limite
        this.maxHeavyCarry = 1;
    }

    public static BigFish getInstance() {
        return bf;
    }

    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }

    @Override
    public String getName() {
        return facingRight ? "bigFishRight" : "bigFishLeft";
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public boolean canPush(GameObject obj, Direction dir) {
        return obj instanceof MovableObject;
    }
}
