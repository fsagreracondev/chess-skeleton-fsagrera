package chess;

import chess.pieces.Piece;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
/**
 * Created by Fabi on 6/15/2014.
 */
public class Move {
    Position origin;
    Position destination;

    public Move(Position origin, Position destination){
        this.origin = origin;
        this.destination = destination;
    }

    public boolean VerifyOrigin(Map<Position, Piece> map, Player currentPlayer) {
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            Piece piece = (Piece) pairs.getValue();
            Position pos = (Position) pairs.getKey();

            if (piece.getOwner() == currentPlayer) {
                if (pos.getColumn() == origin.getColumn() && pos.getRow() == origin.getRow()) {
                    List<Position> positions = piece.getPossiblePositions(pos, map, currentPlayer);
                    if (positions == null || positions.size() < 1)
                        return false;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean VerifyDestination(Map<Position, Piece> map, Player currentPlayer){
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Piece piece = (Piece)pairs.getValue();
            Position pos = (Position)pairs.getKey();

            if(pos.getColumn() == this.origin.getColumn() &&  pos.getRow() == this.origin.getRow() && piece.getOwner() == currentPlayer){
                List<Position> positions = piece.getPossiblePositions(pos, map, currentPlayer);
                if(positions != null){
                    for (Position p : positions){
                        if (p.getColumn() == destination.getColumn() && p.getRow() == destination.getRow())
                            return true;
                    }
                }
            }
        }

        return false;
    }
}
