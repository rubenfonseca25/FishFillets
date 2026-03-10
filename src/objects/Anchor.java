package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class Anchor extends HeavyObject {

    public Anchor(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "anchor";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean canBePushed(Direction dir) {
        //Só pode ser movido horizontalmente
        return dir == Direction.LEFT || dir == Direction.RIGHT;
    }
}
