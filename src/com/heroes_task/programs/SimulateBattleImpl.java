package com.heroes_task.programs;

import com.battle.heroes.army.Army;
import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.PrintBattleLog;
import com.battle.heroes.army.programs.SimulateBattle;
import java.util.*;
import java.util.stream.Collectors;

public class SimulateBattleImpl implements SimulateBattle {
    private PrintBattleLog printBattleLog;

    private boolean hasAttackCapableUnits(Army army) {
        return army.getUnits().stream().anyMatch(Unit::isAlive);
    }

    private List<Unit> getSortedAliveUnits(Army playerArmy, Army computerArmy) {
        List<Unit> allAliveUnits = new ArrayList<>();
        
        allAliveUnits.addAll(playerArmy.getUnits().stream()
            .filter(Unit::isAlive)
            .collect(Collectors.toList()));
        allAliveUnits.addAll(computerArmy.getUnits().stream()
            .filter(Unit::isAlive)
            .collect(Collectors.toList()));
        
        allAliveUnits.sort((a, b) -> {
            int attackCompare = Integer.compare(b.getBaseAttack(), a.getBaseAttack());
            return attackCompare != 0 ? attackCompare : Integer.compare(b.getHealth(), a.getHealth());
        });
        
        return allAliveUnits;
    }

    @Override
    public void simulate(Army playerArmy, Army computerArmy) throws InterruptedException {
        int round = 1;
        
        // Основной цикл симуляции
        while (hasAttackCapableUnits(playerArmy) && hasAttackCapableUnits(computerArmy)) {
            System.out.println("\n=== РАУНД " + round + " ===");
            
            List<Unit> turnOrder = getSortedAliveUnits(playerArmy, computerArmy);
            
            System.out.println("Ходов в раунде: " + turnOrder.size());

            for (int i = 0; i < turnOrder.size(); i++) {
                Unit currentUnit = turnOrder.get(i);
                
                if (!currentUnit.isAlive()) {
                    System.out.println("  [МЁРТВ] " + currentUnit.getName() + " пропускает ход");
                    continue;
                }
                
                String armyType = playerArmy.getUnits().contains(currentUnit) ? "[ИГРОК]" : "[КОМП]";
                System.out.print(armyType + " " + currentUnit.getName() + " (⚔" + currentUnit.getBaseAttack() + " ❤" + currentUnit.getHealth() + ") атакует: ");
                
                Unit target = currentUnit.getProgram().attack();
                
                if (target != null) {
                    // Логируем атаку
                    printBattleLog.printBattleLog(currentUnit, target);
                    System.out.println(target.getName() + " (❤" + target.getHealth() + ")");
                    
                    if (!target.isAlive()) {
                        String targetArmyType = playerArmy.getUnits().contains(target) ? "[ИГРОК]" : "[КОМП]";
                        System.out.println("  ☠ " + targetArmyType + " " + target.getName() + " УБИТ!");
                    }
                } else {
                    System.out.println("не нашёл цель, пропускает ход");
                }
                
                if (!hasAttackCapableUnits(playerArmy) || !hasAttackCapableUnits(computerArmy)) {
                    System.out.println("\n[РАУНД ПРЕРВАН: одна из армий потеряла всех юнитов]");
                    break;
                }
            }
            
            round++;
        }
        
        System.out.println("\n🏁 БИТВА ЗАВЕРШЕНА! 🏁");
        boolean playerAlive = hasAttackCapableUnits(playerArmy);
        boolean computerAlive = hasAttackCapableUnits(computerArmy);
        
        if (playerAlive && !computerAlive) {
            System.out.println("✅ ПОБЕДА ИГРОКА!");
        } else if (!playerAlive && computerAlive) {
            System.out.println("❎ ПОБЕДА КОМПЬЮТЕРА!");
        } else {
            System.out.println("🤝 НИЧЬЯ (обе армии уничтожены)");
        }
        
        System.out.println("Выжившие юниты игрока: " + 
            playerArmy.getUnits().stream().filter(Unit::isAlive).count());
        System.out.println("Выжившие юниты компьютера: " + 
            computerArmy.getUnits().stream().filter(Unit::isAlive).count());
    }
}

