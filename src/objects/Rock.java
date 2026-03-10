package objects;
import java.util.List;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Rock extends HeavyObject {

    public Rock(Room room) {
        super(room);
    }

    @Override
    public void setPosition(Point2D position) {
        Point2D old = getPosition();
        super.setPosition(position);
        //Sempre que a pedra se move tenta spawnar um caranguejo na casa em cima
        if (old != null && !old.equals(position))
            spawnKrab();
    }

    @Override
    public String getName() {
        return "stone";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean canBePushed(Direction dir) {
        return true;
    }

    //Tenta spawnar um caranguejo na posição acima da pedra
    private void spawnKrab() {
        Room room = getRoom();
        if (room == null)
            return;
        Point2D pos = getPosition();
        if (pos == null)
            return;
        //Posição acima da pedra
        Point2D above = new Point2D(pos.getX(), pos.getY() - 1);
        //Estava a spawnar fora do mapa portanto adicionei esta verificação
        if (above.getY() < 0 || above.getY() > 9 || above.getX() < 0 || above.getX() > 9)
            return;

        List<GameObject> objs = room.getObjectsAt(above);
        for (GameObject o : objs) {
            //Se não for água não spawna o caranguejo
            if (!(o instanceof Water))
                return;
        }

        //Se tudo correr bem spawna o caranguejo
        Krab krab = new Krab(room);
        krab.setPosition(above);
        room.addObject(krab);
    }
}
