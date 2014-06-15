package chess.pieces;

import chess.Player;
import chess.Position;
import java.util.*;

/**
 * A base class for chess pieces
 */
public abstract class Piece {
    private final Player owner;

    protected Piece(Player owner) {
        this.owner = owner;
    }

    public char getIdentifier() {
        char id = getIdentifyingCharacter();
        if (owner.equals(Player.White)) {
            return Character.toLowerCase(id);
        } else {
            return Character.toUpperCase(id);
        }
    }

    public Player getOwner() {
        return owner;
    }

    protected abstract char getIdentifyingCharacter();

    public abstract int[][] getPossibleMoves();

    public abstract List<Position> getPossiblePositions(Position position,  Map<Position, Piece> gameState, Player currentPlayer);

    protected boolean isWithinBounds(int col, int row){
        if(col > 104 || col < 97 || row > 8 || row < 1 )
            return false;

        return true;
    }

    protected boolean isInPositon(int col, int row, Position position ){
        if(col == (int)position.getColumn() && row == position.getRow())
            return true;
        return false;
    }

    protected boolean SameSign(int x, int y){
        return (x >= 0) ^ (y < 0);
    }
}
