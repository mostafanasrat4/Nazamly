package classes;

import java.util.UUID;

public class Player implements Comparable<Player>{
    private final String id;
    private String name;
    private int goals;
    private String teamId;

    public Player() {
        id = UUID.randomUUID().toString();
    }

    public Player(String name) {
        this();
        this.name = name;
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public String getId() {

        return id;
    }

    public String getName() {
        return name;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    @Override
    public int compareTo(Player player) {
        return Integer.compare(this.goals, player.goals);
    }
}
