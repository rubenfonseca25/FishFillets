package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Direction;

public abstract class Fish extends GameCharacter {

    protected int carriedLight = 0;
    protected int carriedHeavy = 0;
    protected int maxLightCarry;
    protected int maxHeavyCarry;

    public Fish(Room room) {
        super(room);
    }

    //Saber se o peixe pode mover o objeto na suposta direcção
    public abstract boolean canPush(GameObject obj, Direction dir);

    //Consegue suportar o objeto sem morrer esmagado
    public boolean canSupport(GameObject obj) {
        if (!(obj instanceof MovableObject))
            return true;

        Weight w = obj.getWeight();

        if (w == Weight.LIGHT)
            return carriedLight + 1 <= maxLightCarry;

        if (w == Weight.HEAVY)
            return carriedHeavy + 1 <= maxHeavyCarry;

        return true;
    }

    //Adiciona o objecto que está a suportar aos contadores
    public void addSupportedObject(GameObject obj) {
        Weight w = obj.getWeight();
        if (w == Weight.LIGHT)
            carriedLight++;
        else if (w == Weight.HEAVY)
            carriedHeavy++;
    }

    //Limpa os contadores de objectos suportados
    public void clearSupport() {
        carriedLight = 0;
        carriedHeavy = 0;
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
