package chess;

import chess.pieces.Piece;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Map;
/**
 * This class provides the basic CLI interface to the Chess game.
 */
public class CLI {
    private static final String NEWLINE = System.getProperty("line.separator");

    private final BufferedReader inReader;
    private final PrintStream outStream;

    private GameState gameState = null;

    public CLI(InputStream inputStream, PrintStream outStream) {
        this.inReader = new BufferedReader(new InputStreamReader(inputStream));
        this.outStream = outStream;
        writeOutput("Welcome to Chess!");
    }

    /**
     * Write the string to the output
     * @param str The string to write
     */
    private void writeOutput(String str) {
        this.outStream.println(str);
    }

    /**
     * Retrieve a string from the console, returning after the user hits the 'Return' key.
     * @return The input from the user, or an empty-length string if they did not type anything.
     */
    private String getInput() {
        try {
            this.outStream.print("> ");
            return inReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException("Failed to read from input: ", e);
        }
    }

    void startEventLoop() {
        writeOutput("Type 'help' for a list of commands.");
        doNewGame();

        while (true) {
            showBoard();
            writeOutput(gameState.getCurrentPlayer() + "'s Move");

            String input = getInput();
            if (input == null) {
                break; // No more input possible; this is the only way to exit the event loop
            } else if (input.length() > 0) {
                if (input.equals("help")) {
                    showCommands();
                } else if (input.equals("new")) {
                    doNewGame();
                } else if (input.equals("quit")) {
                    writeOutput("Goodbye!");
                    System.exit(0);
                } else if (input.equals("board")) {
                    writeOutput("Current Game:");
                } else if (input.equals("list")) {
                    displayMoveList();
                } else if (input.startsWith("move")) {
                    performMove(input);
                    if(isInDraw()) {
                         writeOutput("DRAW");
                         doNewGame();
                         return;
                    }
                    if(isInCheckmate()) {
                        doNewGame();
                    }
                } else {
                    writeOutput("I didn't understand that.  Type 'help' for a list of commands.");
                }
            }
        }
    }

    private void doNewGame() {
        gameState = new GameState();
        gameState.reset();
    }

    private void showBoard() {
        writeOutput(getBoardAsString());
    }

    private void showCommands() {
        writeOutput("Possible commands: ");
        writeOutput("    'help'                       Show this menu");
        writeOutput("    'quit'                       Quit Chess");
        writeOutput("    'new'                        Create a new game");
        writeOutput("    'board'                      Show the chess board");
        writeOutput("    'list'                       List all possible moves");
        writeOutput("    'move <colrow> <colrow>'     Make a move");
    }

    /**
     * Display the board for the user(s)
     */
    String getBoardAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append(NEWLINE);

        printColumnLabels(builder);
        for (int i = Position.MAX_ROW; i >= Position.MIN_ROW; i--) {
            printSeparator(builder);
            printSquares(i, builder);
        }

        printSeparator(builder);
        printColumnLabels(builder);

        return builder.toString();
    }


    private void printSquares(int rowLabel, StringBuilder builder) {
        builder.append(rowLabel);

        for (char c = Position.MIN_COLUMN; c <= Position.MAX_COLUMN; c++) {
            Piece piece = gameState.getPieceAt(String.valueOf(c) + rowLabel);
            char pieceChar = piece == null ? ' ' : piece.getIdentifier();
            builder.append(" | ").append(pieceChar);
        }
        builder.append(" | ").append(rowLabel).append(NEWLINE);
    }

    private void printSeparator(StringBuilder builder) {
        builder.append("  +---+---+---+---+---+---+---+---+").append(NEWLINE);
    }

    private void printColumnLabels(StringBuilder builder) {
        builder.append("   ");
        for (char c = Position.MIN_COLUMN; c <= Position.MAX_COLUMN; c++) {
            builder.append(" ").append(c).append("  ");
        }

        builder.append(NEWLINE);
    }

    private void displayMoveList(){
        Map<Position, Piece> map = gameState.getGameState();
        Player currentPlayer = gameState.getCurrentPlayer();

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Piece piece = (Piece)pairs.getValue();
            Position pos = (Position)pairs.getKey();

            if(piece.getOwner() == currentPlayer){
                List<Position> positions = piece.getPossiblePositions(pos, map, currentPlayer);
                if(positions != null){
                    for (Position p : positions){
                       writeOutput( pos.toString() + " " + p.toString());
                    }
                }
            }
        }
    }

    private void performMove(String input){
        if(!verifyMoveInput(input)) {
            writeOutput("Invalid input for move command");
            return;
        }

        input = input.replace("move", "").trim();

        char colOrigin = input.charAt(0);
        int rowOrigin = this.tryParseInt(input.substring(1, 2));
        Position origin = new Position(colOrigin, rowOrigin);

        input = input.substring(2).trim();

        char colDest = input.charAt(0);
        int rowDest = this.tryParseInt(input.substring(1, 2));
        Position destination = new Position(colDest, rowDest);

        Move move = new Move(origin, destination);
        if (!move.VerifyOrigin(gameState.getGameState(), gameState.getCurrentPlayer())) {
            writeOutput("invalid origin for move command");
            return;
        }

        if (!move.VerifyDestination(gameState.getGameState(), gameState.getCurrentPlayer())) {
            writeOutput("invalid destination for move command");
            return;
        }

        gameState.movePiece(move);
    }

    private boolean verifyMoveInput(String input) {
        if (input.startsWith("move")) ;
        input = input.replace("move", "").trim();

        if(input.length() > 1) {
            int colOrigin = (int) input.charAt(0);
            if (colOrigin > 104 || colOrigin < 97) return false;

            int rowOrigin = this.tryParseInt(input.substring(1, 2));
            if (rowOrigin > 8 || rowOrigin < 1) return false;
        }

        input = input.substring(2).trim();
        if(input.length() > 1) {
            int colDest = (int) input.charAt(0);
            if (colDest > 104 || colDest < 97) return false;

            int rowDest = this.tryParseInt(input.substring(1, 2));
            if (rowDest > 8 || rowDest < 1) return false;
        }
        else
            return false;

        return true;
    }

    private int tryParseInt(String value){
        try
        {
            int result = Integer.parseInt(value);
            return result;
        } catch(NumberFormatException nfe)
        {
            return -1;
        }
    }

    private boolean isInCheckmate(){
        Player currentPlayer = gameState.getCurrentPlayer();
        Player attackingPlayer = Player.Black;

        if(currentPlayer == Player.Black)
            attackingPlayer = Player.White;

        List<Position> attackerPositions = getPositions(attackingPlayer);
        List<Position> kingPositions = getKingPositions(currentPlayer);
        List<Position> matchedPositions = new LinkedList<Position>();

        for (Position kp : kingPositions) {
            for (Position ap : attackerPositions) {
                if (kp.getColumn() == ap.getColumn() && kp.getRow() == ap.getRow()) {
                    matchedPositions.add(ap);
                    break;
                }
            }
        }

        //not in danger
        if(matchedPositions.size() == 0) return false;

        //if all king positions can be matched by the opponent
        if(matchedPositions.size() == kingPositions.size()) {
            writeOutput("Checkmate - " +attackingPlayer + " WINS!");
            return true;
        }

        return false;
    }

    private boolean isInDraw(){
        //if checkmate is not possible
        Map<Position, Piece> blackMap = new HashMap<Position, Piece>();
        Map<Position, Piece> whiteMap = new HashMap<Position, Piece>();
        Map<Position, Piece> map = gameState.getGameState();

        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Piece piece = (Piece)pairs.getValue();
            Position pos = (Position)pairs.getKey();

            if(piece.getOwner() == Player.Black)
               blackMap.put(pos, piece);
            else
               whiteMap.put(pos, piece);
        }

        //both are king
        if(blackMap.size() == 1 && whiteMap.size()== 1) return true;

        //if the combinations yield draws
        if(!validPairs(blackMap, whiteMap)) return true;
        if(!validPairs(whiteMap, blackMap)) return true;

        return false;
    }

    private boolean validPairs(Map<Position, Piece> pair1,  Map<Position, Piece> pair2){
        if(pair1.size() != 2) return true;
        if(pair2.size() > 2) return true;

        Iterator itPair1 = pair1.entrySet().iterator();
        while (itPair1.hasNext()) {
            Map.Entry pairs = (Map.Entry) itPair1.next();
            Piece piece = (Piece) pairs.getValue();
            Position pos = (Position) pairs.getKey();

            if(piece.getIdentifier() == 'b' ||  piece.getIdentifier() == 'n') {
                if (pair2.size() == 1) return false;
                if (piece.getIdentifier() == 'n') return true;
                else{
                    Iterator itPair2 = pair2.entrySet().iterator();
                    while (itPair2.hasNext()) {
                        Map.Entry pairs2 = (Map.Entry) itPair2.next();
                        Piece piece2 = (Piece) pairs2.getValue();
                        Position pos2 = (Position) pairs2.getKey();
                        if(piece2.getIdentifier() != 'b') return true;

                        //check if bishops are both the same color
                        if(pos.isBlack() && pos2.isBlack()) return false;
                        if(!pos.isBlack() && !pos2.isBlack()) return false;
                    }
                }
            }
        }

        return true;
    }

    private List<Position> getPositions(Player player){
        List<Position> positions = new LinkedList<Position>();
        Map<Position, Piece> map = gameState.getGameState();
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Piece piece = (Piece)pairs.getValue();
            Position pos = (Position)pairs.getKey();

            if(piece.getOwner() == player) {
                List<Position> piecePositions = piece.getPossiblePositions(pos, map, player);
                for (Position p : piecePositions){
                    positions.add(p);
                }
            }
        }

        return positions;
    }

    private List<Position> getKingPositions(Player player){
        List<Position> positions = new LinkedList<Position>();
        Map<Position, Piece> map = gameState.getGameState();
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Piece piece = (Piece)pairs.getValue();
            if(piece.getIdentifier() != 'k') continue;

            Position pos = (Position)pairs.getKey();

            if(piece.getOwner() == player) {
                List<Position> piecePositions = piece.getPossiblePositions(pos, map, player);
                for (Position p : piecePositions){
                    positions.add(p);
                }

                //add current position because king doesn't have to move if not in danger
                positions.add(pos);
            }
        }

        return positions;
    }

    private List<Position> getDefensivePositions(Player player){
        List<Position> positions = new LinkedList<Position>();
        Map<Position, Piece> map = gameState.getGameState();
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            Piece piece = (Piece)pairs.getValue();
            if(piece.getIdentifier() == 'k') continue;

            Position pos = (Position)pairs.getKey();

            if(piece.getOwner() == player) {
                List<Position> piecePositions = piece.getPossiblePositions(pos, map, player);
                for (Position p : piecePositions){
                    positions.add(p);
                }
            }
        }

        return positions;
    }

    public static void main(String[] args) {
        CLI cli = new CLI(System.in, System.out);
        cli.startEventLoop();
    }
}
