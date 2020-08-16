package qwertyp4nts.pokedex;
//value class - just a container for these 2 data types

public class Pokemon {
    private String name;
    private String url;
    private boolean caught;

    Pokemon(String name, String url, boolean caught) {
        this.name = name;
        this.url = url;
        this.caught = caught;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public boolean getCaughtStatus() { return caught; }
}
