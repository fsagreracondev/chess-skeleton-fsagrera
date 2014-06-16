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

    int tryParseInt(String value){
        try
        {
           int result = Integer.parseInt(value);
           return result;
        } catch(NumberFormatException nfe)
        {
            return -1;
        }
    }

    public static void main(String[] args) {
        CLI cli = new CLI(System.in, System.out);
        cli.startEventLoop();
    }
}
