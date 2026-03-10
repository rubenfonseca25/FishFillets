package objects;

import pt.iscte.poo.game.Room;

public class Water extends GameObject {

    public Water(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "water";
    }

    @Override
    public int getLayer() {
        return 0;
    }

    @Override
    public Weight getWeight() {
        return Weight.FIXED;
    }

    @Override
    public boolean blocksSmall() {
        return false;
    }

    @Override
    public boolean blocksBig() {
        return false;
    }
}
