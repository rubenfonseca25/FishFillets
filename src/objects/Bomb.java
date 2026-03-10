package objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;
import pt.iscte.poo.utils.Point2D;

public class Bomb extends LightObject implements Hazard {

    private boolean wasFalling = false;

    public Bomb(Room room) {
        super(room);
    }

    @Override
    public String getName() {
        return "bomb";
    }

    @Override
    public int getLayer() {
        return 1;
    }

    @Override
    public boolean canBePushed(Direction dir) {
        return true;
    }

    @Override
    public void onCollision(Fish fish) {

    }

    //Marca que a bomba está a cair
    public void markFalling() {
        wasFalling = true;
    }

    //Verifica se a bomba caiu em cima da parede e eplode se for o caso
    public void handleLanding(List<GameObject> supportObjects) {
        if (!wasFalling)
            return;

        boolean landed = false;
        //Verifica se caiu em cima de uma parede
        for (GameObject o : supportObjects) {
            if (o instanceof Water)
                continue;
            if (!(o instanceof Fish)) {
                landed = true;
                break;
            }
        }

        //Se caiu em cima de uma parede explode 
        if (landed)
            explode();

        wasFalling = false;
    }

    //Executa a explosão da bomba
    private void explode() {
        Room room = getRoom();
        Point2D pos = getPosition();

        if (room == null || pos == null)
            return;
        
        //Lista de posições a afetar pela explosão
        List<Point2D> spots = Arrays.asList(pos, new Point2D(pos.getX(), pos.getY() - 1), new Point2D(pos.getX(), pos.getY() + 1), 
        new Point2D(pos.getX() - 1, pos.getY()), new Point2D(pos.getX() + 1, pos.getY()));

        //Lista de objetos a remover
        List<GameObject> toRemove = new ArrayList<>();
        //Lista de posições válidas para spawnar explosões
        List<Point2D> validSpots = new ArrayList<>();

        for (Point2D p : spots) {
            if (!room.isInside(p))
                continue;
            //Adicionar à lista de posições válidas para a explosão
            validSpots.add(p);
            //Lista de objetos na posição validada
            List<GameObject> objs = room.getObjectsAt(p);
            //Eliminar os objectos que não sejam paredes rígidas e água, se for peixe mata o pwixe
            for (GameObject o : objs) {
                if (o instanceof Water || isRigidWall(o))
                    continue;
                if (o instanceof Fish) {
                    room.killFish((Fish) o);
                    continue;
                }
                toRemove.add(o);
            }
        }

        //Remover os objectos afetados pela explosão
        for (GameObject o : toRemove) {
            room.removeObject(o);
        }

        //Spawnar as explosões nas direcções válidas
        for (Point2D p : validSpots) {
            Explosion explosion = new Explosion(room);
            explosion.setPosition(p);
            room.addObject(explosion);
        }
    }

    //Verifica se o objecto é uma parede rígida
    private boolean isRigidWall(GameObject obj) {
        return obj instanceof Wall || obj instanceof SteelHorizontal || obj instanceof SteelVertical;
    }
}
