package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.SuitableForAttackUnitsFinder;

import java.util.ArrayList;
import java.util.List;

public class SuitableForAttackUnitsFinderImpl implements SuitableForAttackUnitsFinder {

    private static final int ROWS_COUNT = 3; // Количество рядов (y = 0,1,2)
    
    // Диапазоны X для армий на поле 27x3
    private static final int LEFT_ARMY_MIN_X = 0;   // Армия игрока (левая)
    private static final int LEFT_ARMY_MAX_X = 2;
    private static final int RIGHT_ARMY_MIN_X = 24; // Армия компьютера (правая)
    private static final int RIGHT_ARMY_MAX_X = 26;

    @Override
    public List<Unit> getSuitableUnits(List<List<Unit>> unitsByRow, boolean isLeftArmyTarget) {
        List<Unit> suitableUnits = new ArrayList<>();
        
        if (unitsByRow == null || unitsByRow.size() != ROWS_COUNT) {
            return suitableUnits;
        }
        
        // Определяем параметры поиска в зависимости от цели атаки
        final int searchMinX, searchMaxX;
        final boolean checkLeftBlocking; // true = проверять блокировку слева, false = справа
        
        if (isLeftArmyTarget) {
            // Атакуем левую армию игрока (x = 0..2)
            searchMinX = LEFT_ARMY_MIN_X;
            searchMaxX = LEFT_ARMY_MAX_X;
            checkLeftBlocking = false; // Проверяем соседа СПРАВА (x+1)
        } else {
            // Атакуем правую армию компьютера (x = 24..26)
            searchMinX = RIGHT_ARMY_MIN_X;
            searchMaxX = RIGHT_ARMY_MAX_X;
            checkLeftBlocking = true; // Проверяем соседа СЛЕВА (x-1)
        }
        
        // Проходим по каждому ряду (y = 0,1,2)
        for (int rowY = 0; rowY < ROWS_COUNT; rowY++) {
            List<Unit> row = unitsByRow.get(rowY);
            
            // Проверяем, что ряд существует и достаточно длинный
            if (row == null || row.size() <= searchMaxX) {
                continue; // Пропускаем поврежденные данные
            }
            
            // Проходим по столбцам в диапазоне целевой армии
            for (int x = searchMinX; x <= searchMaxX; x++) {
                Unit currentUnit = row.get(x);
                
                // Пропускаем пустые клетки и мёртвых юнитов
                if (currentUnit == null || !currentUnit.isAlive()) {
                    continue;
                }
                
                // Проверяем, не закрыт ли юнит другим юнитом
                boolean isBlocked = false;
                
                if (checkLeftBlocking) {
                    // Для правой армии: проверяем соседа СЛЕВА
                    if (x > searchMinX) { // Не для самого левого столбца
                        Unit leftUnit = row.get(x - 1);
                        isBlocked = (leftUnit != null && leftUnit.isAlive());
                    }
                } else {
                    // Для левой армии: проверяем соседа СПРАВА
                    if (x < searchMaxX) { // Не для самого правого столбца
                        Unit rightUnit = row.get(x + 1);
                        isBlocked = (rightUnit != null && rightUnit.isAlive());
                    }
                }
                
                // Если юнит не заблокирован - добавляем в результат
                if (!isBlocked) {
                    suitableUnits.add(currentUnit);
                }
            }
        }
        
        return suitableUnits;
    }
}