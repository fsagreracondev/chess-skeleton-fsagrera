package chess.pieces;

import chess.Player;
import chess.Position;
import java.util.*;

/**
 * The 'Bishop' class
 */
public class Bishop extends Piece {
    public Bishop(Player owner) {
        super(owner);
    }

    @Override
    protected char getIdentifyingCharacter() {
        return 'b';
    }

    @Override
    public int[][] getPossibleMoves(){
        int [][] moves = new int[28][2];

        int col = 1;
        int row = 1;
        int i = 0;
        while(row < 8){
            moves[i][0]= col;
            moves[i][1]= row;
            row++;
            col++;
            i++;
        }

        col = -1;
        row = -1;

        while(row > -8){
            moves[i][0]= col;
            moves[i][1]= row;
            row--;
            col--;
            i++;
        }

        col = -1;
        row = 1;
        while(col > -8){
            moves[i][0]= col;
            moves[i][1]= row;
            row++;
            col--;
            i++;
        }

        col = 1;
        row = -1;

        while(col < 8){
            moves[i][0]= col;
            moves[i][1]= row;
            col++;
            row--;
            i++;
        }

        return moves;
    }

    @Override
    public List<Position> getPossiblePositions(Position position,  Map<Position, Piece> gameState, Player currentPlayer){
        List<Position> positions= new LinkedList<Position>();
        int[][]moves = this.getPossibleMoves();

        int column = (int)position.getColumn();
        int row = position.getRow();

        int c = 0;
        int r = 0;

        int prevC = 0;
        int prevR = 0;

        boolean pieceAlong = false;
        for(int i = 0; i < moves.length; i++) {
            c = moves[i][0] + column;
            r = moves[i][1] + row;

            if(!isWithinBounds(c,r)) {
                prevC = c;
                prevR = r;
                continue;
            }

            if(i != 0){
                if(this.SameSign(prevC, c) && this.SameSign(prevR, r)) {
                    if (pieceAlong) {
                        prevC = c;
                        prevR = r;
                        continue;
                    }
                }
                else
                    pieceAlong = false;
            }

            prevC = c;
            prevR = r;

            boolean pieceInPosition = false;
            boolean pieceToEat = false;
            Iterator it = gameState.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry) it.next();
                Piece piece = (Piece) pairs.getValue();
                Position pos = (Position) pairs.getKey();
                if (this.isInPositon(c, r, pos)) {
                    if (piece.getOwner() != currentPlayer) {
                        pieceToEat = true;
                    }
                    pieceAlong = true;
                    pieceInPosition = true;
                    break;
                }
            }

            if (pieceInPosition == false || (pieceInPosition == true && pieceToEat == true))
                positions.add(new Position((char) c, r));
        }

        return positions;
    }
}
