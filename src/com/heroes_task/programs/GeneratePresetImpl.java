package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.GeneratePreset;

import java.util.*;
import java.util.stream.Collectors;

public class GeneratePresetImpl implements GeneratePreset {

    private static final int FIELD_WIDTH = 3;
    private static final int FIELD_HEIGHT = 21;
    private static final int MAX_UNITS_PER_TYPE = 11;

    private static class UnitEfficiency {
        Unit unit;
        double efficiency;

        UnitEfficiency(Unit unit, double efficiency) {
            this.unit = unit;
            this.efficiency = efficiency;
        }
    }

    private static class Vector2 {
        int x;
        int y;

        Vector2(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vector2 vector2 = (Vector2) o;
            return x == vector2.x && y == vector2.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    private Unit cloneUnit(Unit original) {
        try {
            return new Unit(
                original.getName(),
                original.getUnitType(),
                original.getHealth(),
                original.getBaseAttack(),
                original.getCost(),
                original.getAttackType(),
                original.getAttackBonuses(),
                original.getDefenceBonuses(),
                original.getxCoordinate(),
                original.getyCoordinate()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to clone Unit", e);
        }
    }

    @Override
    public Army generate(List<Unit> unitList, int maxPoints) {
        List<Unit> resultArmy = new ArrayList<>();
        
        List<Vector2> availablePositions = generateAllAvailablePositions();
        Collections.shuffle(availablePositions, new Random(System.currentTimeMillis()));
        
        List<UnitEfficiency> unitEfficiencies = unitList.stream()
            .map(unit -> new UnitEfficiency(
                unit, 
                (double) (0.8 * (unit.getBaseAttack() / (double) unit.getCost()) + 
                         0.2 * (unit.getHealth() / (double) unit.getCost()))
            ))
            .sorted((a, b) -> Double.compare(b.efficiency, a.efficiency)) // УБЫВАНИЕ!
            .collect(Collectors.toList());
        
        int remainingPoints = maxPoints;
        
        for (UnitEfficiency unitEfficiency : unitEfficiencies) {
            int maxUnitsOfThisType = Math.min(
                MAX_UNITS_PER_TYPE, 
                remainingPoints / unitEfficiency.unit.getCost()
            );
            
            for (int i = 0; i < maxUnitsOfThisType && !availablePositions.isEmpty(); i++) {
                Vector2 position = availablePositions.remove(availablePositions.size() - 1);
                
                Unit newUnit = cloneUnit(unitEfficiency.unit);
                newUnit.setName(unitEfficiency.unit.getUnitType() + " " + (i + 1));
                newUnit.setxCoordinate(position.x);
                newUnit.setyCoordinate(position.y);
                
                resultArmy.add(newUnit);
                remainingPoints -= unitEfficiency.unit.getCost();
            }
        }
        
        // Debug output
        System.out.println("Сгенерировано " + resultArmy.size() + " юнитов, осталось " + remainingPoints + " очков");
        for (Unit unit : resultArmy) {
            System.out.println("(Name: " + unit.getName() + " [" + unit.getxCoordinate() + ", " + unit.getyCoordinate() + "])");
        }
        
        return new Army(resultArmy);
    }
    
    private List<Vector2> generateAllAvailablePositions() {
        List<Vector2> positions = new ArrayList<>(FIELD_WIDTH * FIELD_HEIGHT);
        for (int x = 0; x < FIELD_WIDTH; x++) {
            for (int y = 0; y < FIELD_HEIGHT; y++) {
                positions.add(new Vector2(x, y));
            }
        }
        return positions;
    }
}