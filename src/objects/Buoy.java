package objects;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public class Buoy extends MovableObject implements Floatable, Pushable {

    public Buoy(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "buoy";
    }

    @Override
    public int getLayer() {
        return 2;
    }

    @Override
    public Weight getWeight() {
        return Weight.NOWEIGHT;
    }

    public boolean blocksSmall() {
        return true;
    }

    public boolean blocksBig() {
        return true;
    }

    @Override
    public boolean canBePushed(Direction dir) {
        return true;
    }
}
