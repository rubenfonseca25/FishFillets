package objects;

import pt.iscte.poo.game.Room;

public abstract class HeavyObject extends MovableObject implements Pushable, Fallable {

    public HeavyObject(Room room) {
        super(room);
    }

    @Override
    public Weight getWeight() {
        return Weight.HEAVY;
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
