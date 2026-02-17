package types;

public class Rhombus extends QuaxTile {

	private final int xPos;
	private final int yPos;
	
	public Rhombus(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
	
	@Override
	public boolean onLow() {
		return false;
	}

	@Override
	public boolean onHigh() {
		return false;
	}

}
