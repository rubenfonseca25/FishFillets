package objects;

import pt.iscte.poo.game.Room;

public abstract class MovableObject extends GameObject {

    public MovableObject(Room room) {
        super(room);
    }

    @Override
    public boolean isMovable() {
        return true;
    }
}
