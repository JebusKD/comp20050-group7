package player;

import java.util.Scanner;

import model.QuaxBoard;
import types.Octagon;
import types.QuaxTileColour;

public class HumanPlayer extends QuaxPlayer {
	
	private String name;
	
	public HumanPlayer(String name, QuaxTileColour colour) {
		super(colour);
		this.name = name;
	}
	
	@Override
	public int[] movePrompt() {
		Scanner s = new Scanner(System.in);

		System.out.println(name + "'s turn.");
        String move = s.nextLine();
        
        s.close();

        return Octagon.stringToCoords(move);
	}
}
