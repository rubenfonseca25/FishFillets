package objects;

import pt.iscte.poo.game.Room;

public abstract class FixedObject extends GameObject {

    public FixedObject(Room room) {
        super(room);
    }

    @Override
    public Weight getWeight() {
        return Weight.FIXED;
    }

    @Override
    public boolean isMovable() {
        return false;
    }

    @Override
    public boolean blocksSmall() {
        return true;
    }

    @Override
    public boolean blocksBig() {
        return true;
    }
}
