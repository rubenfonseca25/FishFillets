package objects;

import pt.iscte.poo.game.Room;

public class Log extends FixedObject {

    public Log(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "trunk";
    }

    @Override
    public int getLayer() {
        return 1;
    }
}
