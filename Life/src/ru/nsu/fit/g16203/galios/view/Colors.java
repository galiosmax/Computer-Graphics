package ru.nsu.fit.g16203.galios.view;

import ru.nsu.fit.g16203.galios.model.Condition;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Colors {

    private static Map<Condition, Color> colorMap = new HashMap<>();

    public Colors() {
        colorMap.put(Condition.DEAD, new Color(0xc19a6b));
        colorMap.put(Condition.ALIVE, new Color(0xF13A13));
    }

    public Color getColor(Condition condition) {
        return colorMap.get(condition);
    }

}
