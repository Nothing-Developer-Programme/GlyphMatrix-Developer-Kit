package com.example.cricketglyph;

/**
 * Data model for cricket match information
 */
public class CricketMatch {

    // Match identification
    public String id;
    public String name;
    public String matchType; // T20, ODI, Test
    public String venue;

    // Teams
    public String team1;
    public String team2;

    // Scores
    public String score1;
    public String score2;
    public String wickets1;
    public String wickets2;
    public String overs1;
    public String overs2;

    // Match state
    public boolean isLive;
    public String status; // "live", "upcoming", "completed", etc.

    // Statistics
    public double currentRunRate;
    public double requiredRunRate;

    // Additional info
    public String tossWinner;
    public String tossDecision;
    public long matchDate;

    public CricketMatch() {
        this.currentRunRate = 0.0;
        this.requiredRunRate = 0.0;
        this.isLive = false;
    }

    /**
     * Get short display name for the match
     */
    public String getShortName() {
        if (team1 != null && team2 != null) {
            String t1 = getTeamAbbreviation(team1);
            String t2 = getTeamAbbreviation(team2);
            return t1 + " vs " + t2;
        }
        return name != null ? name : "Unknown Match";
    }

    /**
     * Get team abbreviation (e.g., "India" -> "IND")
     */
    private String getTeamAbbreviation(String teamName) {
        if (teamName == null || teamName.isEmpty()) return "???";

        // Common team abbreviations
        switch (teamName.toLowerCase()) {
            case "india": return "IND";
            case "australia": return "AUS";
            case "england": return "ENG";
            case "pakistan": return "PAK";
            case "south africa": return "RSA";
            case "new zealand": return "NZ";
            case "sri lanka": return "SL";
            case "west indies": return "WI";
            case "bangladesh": return "BAN";
            case "afghanistan": return "AFG";
            case "zimbabwe": return "ZIM";
            case "ireland": return "IRE";
            default:
                // Take first 3 letters and uppercase
                return teamName.substring(0, Math.min(3, teamName.length())).toUpperCase();
        }
    }

    /**
     * Check if match data is complete
     */
    public boolean isComplete() {
        return team1 != null && team2 != null &&
               score1 != null && overs1 != null;
    }

    @Override
    public String toString() {
        return "CricketMatch{" +
                "name='" + name + '\'' +
                ", team1='" + team1 + '\'' +
                ", team2='" + team2 + '\'' +
                ", score1='" + score1 + "/" + wickets1 + '\'' +
                ", score2='" + score2 + "/" + wickets2 + '\'' +
                ", isLive=" + isLive +
                ", status='" + status + '\'' +
                '}';
    }
}
