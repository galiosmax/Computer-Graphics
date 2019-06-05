package ru.nsu.fit.g16203.galios.model;

class Cell {

    private Condition condition;
    private double impact = 0;

    Cell() {
        condition = Condition.DEAD;
    }

    Condition getCondition() {
        return condition;
    }

    void setCondition(Condition condition) {
        this.condition = condition;
    }

    double getImpact() {
        return impact;
    }

    void setImpact(double impact) {
        this.impact = impact;
    }
}
