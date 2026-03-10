package objects;

import pt.iscte.poo.game.Room;

public class CrackedWall extends FixedObject {

    public CrackedWall(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "holedWall";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean blocksSmall() {
        return false;
    }

    @Override
    public boolean blocksBig() {
        return true;
    }
}
