package objects;

import pt.iscte.poo.game.Room;
import pt.iscte.poo.utils.Point2D;
import pt.iscte.poo.utils.Vector2D;

public abstract class GameCharacter extends GameObject {
	
	public GameCharacter(Room room) {
		super(room);
	}
	
	public void move(Vector2D dir) {
		Point2D destination = new Point2D(getPosition().getX() + dir.getX(), getPosition().getY() + dir.getY()); 
		Room room = getRoom();
        if (room == null) {
            setPosition(destination);
            return;
        }
        if (room.canMove(this, destination)) {
            setPosition(destination);
        }		
	}

	@Override
	public int getLayer() {
		return 2;
	}

	@Override
    public boolean isMovable() {
        return true;
    }

	@Override
    public Weight getWeight() {
        return Weight.FIXED;
    }
	
}