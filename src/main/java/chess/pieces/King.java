package chess.pieces;

import chess.Player;
import chess.Position;
import java.util.*;
/**
 * The King class
 */
public class King extends Piece {
    public King(Player owner) {
        super(owner);
    }

    @Override
    protected char getIdentifyingCharacter() {
        return 'k';
    }

    @Override
    public int[][] getPossibleMoves(){

        int [][] moves = new int[][] {{0,1},{1,1},{1,0},{1,-1},{0, -1},{-1,-1},{-1, 0},{-1, 1}};

        return moves;
    }

    @Override
    public  List<Position> getPossiblePositions(Position position,  Map<Position, Piece> gameState, Player currentPlayer){
        List<Position> positions= new LinkedList<Position>();
        int[][]moves = this.getPossibleMoves();

        int column = (int)position.getColumn();
        int row = position.getRow();

        int c = 0;
        int r = 0;

        for(int i = 0; i < moves.length; i++){
            c = moves[i][0] + column;
            r = moves[i][1] + row;

            if(!isWithinBounds(c,r)) continue;

            boolean pieceInPosition = false;
            boolean pieceToEat = false;

            Iterator it = gameState.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                Piece piece = (Piece)pairs.getValue();
                Position pos = (Position)pairs.getKey();
                if(this.isInPositon(c,r, pos)){
                    if(piece.getOwner() != currentPlayer) {
                        pieceToEat = true;
                    }
                    pieceInPosition = true;
                    break;
                }
            }

            if (pieceInPosition == false || pieceInPosition == true && pieceToEat == true)
                positions.add(new Position((char) c, r));
        }

        return positions;
    }
}
