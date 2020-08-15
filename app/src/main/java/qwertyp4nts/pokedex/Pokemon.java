package qwertyp4nts.pokedex;
//value class - just a container for these 2 data types

public class Pokemon {
    private String name;
    private String url;

    Pokemon(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
