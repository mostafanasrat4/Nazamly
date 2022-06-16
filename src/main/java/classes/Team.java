package classes;

import java.util.*;

public class Team implements Comparable<Team>{
    private final String id;
    private String name;
    private int points;
    private int ga;
    private int gf;
    private int gd;
    private int matchesPlayed;
    private int position;

    public Team() {
        this.id = UUID.randomUUID().toString();
    }

    public Team(String name) {
        this();
        this.name = name;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getGa() {
        return ga;
    }

    public void setGa(int ga) {
        this.ga = ga;
    }

    public int getGf() {
        return gf;
    }

    public void setGf(int gf) {
        this.gf = gf;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGd() {
        return gd;
    }

    public void setGd(int gd) {
        this.gd = gd;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int compareTo(Team team) {
        return Integer.compare(this.getPoints(), team.getPoints());
    }
}
