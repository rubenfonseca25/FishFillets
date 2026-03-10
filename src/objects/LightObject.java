package objects;

import pt.iscte.poo.game.Room;

public abstract class LightObject extends MovableObject implements Pushable, Fallable {

    public LightObject(Room room) {
        super(room);
    }

    @Override
    public Weight getWeight() {
        return Weight.LIGHT;
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
