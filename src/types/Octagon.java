package types;

public class Octagon extends QuaxTile {

	private final int xPos;
	private final int yPos;
	
	public Octagon(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
	
	public Octagon(String coordinates) {
		int[] coords = stringToCoords(coordinates);
		this.xPos = coords[0];
		this.yPos = coords[1];
	}
	
	public String getCoordinates() {
		return coordsToString(xPos, yPos);
	}
	
	public static int[] stringToCoords(String coords) {
		int[] pair = new int[2];
		pair[0] = coords.charAt(0) - 'a';
		pair[1] = Integer.parseInt(coords.substring(1));
		return pair;
	}
	
	public static String coordsToString(int x, int y) {
		if (y < 1 || y > 11) throw new IllegalArgumentException("y must be in range [0, 10]");
		
		String returnStr = switch (x) {
		case 0 -> "a";
		case 1 -> "b";
		case 2 -> "c";
		case 3 -> "d";
		case 4 -> "e";
		case 5 -> "f";
		case 6 -> "g";
		case 7 -> "h";
		case 8 -> "i";
		case 9 -> "j";
		case 10 -> "k";
		default -> throw new IllegalArgumentException("x must be in range [0,10]");
		};
		
		return returnStr + (y+1);
	}
	
	@Override
	public boolean onLow() {
		if (isFree()) return false;
		
		if (getColour() == QuaxTileColour.BLACK)
			return yPos == 0;
		else return xPos == 0;
	}
	
	@Override
	public boolean onHigh() {
		if (isFree()) return false;
		
		if (getColour() == QuaxTileColour.BLACK)
			return yPos == 10;
		else return xPos == 10;
	}
		
}
