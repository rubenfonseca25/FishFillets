package objects;

import pt.iscte.poo.game.Room;

public class Wall extends FixedObject {

    public Wall(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "wall";
    }

    @Override
    public int getLayer() {
        return 1;
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
