package ru.nsu.fit.g16203.galios.resources;

import ru.nsu.fit.g16203.galios.model.Condition;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Colors {

    private static Map<Condition, Color> colorMap = new HashMap<>();

    public Colors() {
        colorMap.put(Condition.DEAD, Color.cyan);
        colorMap.put(Condition.ALIVE, Color.GREEN);
    }

    public Color getColor(Condition condition) {
        return colorMap.get(condition);
    }

}
