package objects;

import pt.iscte.poo.game.Room;

public class Trap extends FixedObject implements Hazard {

    public Trap(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "trap";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    // Small fish passes through; big fish dies.
    @Override
    public boolean blocksSmall() {
        return false;
    }

    @Override
    public boolean blocksBig() {
        return false;
    }

    @Override
    public void onCollision(Fish fish) {
        if (fish instanceof BigFish) {
            fish.getRoom().killFish(fish);
        }
    }
}
