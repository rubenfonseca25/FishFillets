package objects;

import pt.iscte.poo.game.Room;

public class SteelVertical extends FixedObject {

    public SteelVertical(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "steelVertical";
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
