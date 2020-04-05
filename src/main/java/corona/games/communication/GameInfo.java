package corona.games.communication;

/**
 * Created by noamannenberg
 * on 4/5/20.
 */
public class GameInfo {
    public final String name;
    public final int minPlayers;
    public final int maxPlayers;

    public GameInfo(String name, int minPlayers, int maxPlayers) {
        this.name = name;
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
    }
}
