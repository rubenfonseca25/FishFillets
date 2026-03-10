package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class Cup extends LightObject {

    public Cup(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "concha";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean canBePushed(Direction dir) {
        //Pode ser movido em todas as direções
        return true;
    }
}
