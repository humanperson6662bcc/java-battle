package app.javaJostle;

public class ReadOnlyMap implements MapReadOnly {
    private final Map map;

    public ReadOnlyMap(Map map) {
        this.map = map;
    }

    @Override
    public int[][] getTiles() {
        return map.getTiles();
    }
}
