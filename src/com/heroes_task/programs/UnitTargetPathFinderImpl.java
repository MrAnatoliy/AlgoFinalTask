package com.heroes_task.programs;

import com.battle.heroes.army.Unit;
import com.battle.heroes.army.programs.Edge;
import com.battle.heroes.army.programs.UnitTargetPathFinder;

import java.util.*;

public class UnitTargetPathFinderImpl implements UnitTargetPathFinder {
    
    private static final int WIDTH = 27;
    private static final int HEIGHT = 21;
    
    // 8 направлений движения (включая диагонали)
    private static final int[][] DIRECTIONS = {
        {-1, -1}, {-1, 0}, {-1, 1},
        {0, -1},           {0, 1},
        {1, -1},  {1, 0},  {1, 1}
    };
    
    private static class Node {
        int x, y;
        int g, h, f;
        Node parent;
        
        Node(int x, int y, int g, int h, Node parent) {
            this.x = x;
            this.y = y;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }
    }

    @Override
    public List<Edge> getTargetPath(Unit attackUnit, Unit targetUnit, List<Unit> existingUnitList) {
        int startX = attackUnit.getxCoordinate();
        int startY = attackUnit.getyCoordinate();
        int targetX = targetUnit.getxCoordinate();
        int targetY = targetUnit.getyCoordinate();

        if (!isValid(startX, startY) || !isValid(targetX, targetY)) {
            System.err.println("❌ Неверные координаты: start=(" + startX + "," + startY + 
                             "), target=(" + targetX + "," + targetY + ")");
            return Collections.emptyList();
        }

        boolean[][] blocked = createBlockedGrid(existingUnitList, attackUnit, targetUnit);

        if (blocked[startX][startY]) {
            System.err.println("❌ Стартовая позиция заблокирована!");
            return Collections.emptyList();
        }

        System.out.println("\n🔍 Поиск пути: (" + startX + "," + startY + ") -> (" + targetX + "," + targetY + ")");
        
        // A* алгоритм
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));
        boolean[][] closed = new boolean[WIDTH][HEIGHT];
        Node[][] nodes = new Node[WIDTH][HEIGHT];
        
        Node startNode = new Node(startX, startY, 0, heuristic(startX, startY, targetX, targetY), null);
        openSet.add(startNode);
        nodes[startX][startY] = startNode;
        
        int iterations = 0;
        final int MAX_ITERATIONS = 10000; // Защита от зацикливания
        
        while (!openSet.isEmpty() && iterations++ < MAX_ITERATIONS) {
            Node current = openSet.poll();
            
            // Достигли цели
            if (current.x == targetX && current.y == targetY) {
                List<Edge> path = reconstructPath(current);
                System.out.println("✅ Путь найден! Длина: " + path.size() + ", итераций: " + iterations);
                return path;
            }
            
            // Помечаем текущий узел как обработанный
            closed[current.x][current.y] = true;
            
            // Обрабатываем всех соседей (8 направлений)
            for (int[] dir : DIRECTIONS) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                
                // Проверка границ, препятствий и закрытого списка
                if (!isValid(nx, ny) || blocked[nx][ny] || closed[nx][ny]) {
                    continue;
                }
                
                // Новая стоимость пути
                int newG = current.g + 1; // Все переходы стоят 1
                
                Node neighbor = nodes[nx][ny];
                
                if (neighbor == null) {
                    // Новый узел, создаём и добавляем
                    neighbor = new Node(nx, ny, newG, heuristic(nx, ny, targetX, targetY), current);
                    nodes[nx][ny] = neighbor;
                    openSet.add(neighbor);
                } else if (newG < neighbor.g) {
                    // Нашли лучший путь к существующему узлу
                    neighbor.g = newG;
                    neighbor.f = newG + neighbor.h;
                    neighbor.parent = current;
                    
                    // Обновляем в PriorityQueue
                    openSet.remove(neighbor);
                    openSet.add(neighbor);
                }
            }
        }
        
        System.err.println("❌ Путь не найден! Итераций: " + iterations);
        return Collections.emptyList();
    }
    
    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================
    
    private boolean isValid(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }
    
    private boolean[][] createBlockedGrid(List<Unit> existingUnitList, Unit attackUnit, Unit targetUnit) {
        boolean[][] blocked = new boolean[WIDTH][HEIGHT];
        for (Unit unit : existingUnitList) {
            if (unit.isAlive() && unit != attackUnit && unit != targetUnit) {
                int x = unit.getxCoordinate();
                int y = unit.getyCoordinate();
                if (isValid(x, y)) {
                    blocked[x][y] = true;
                }
            }
        }
        return blocked;
    }
    
    private int heuristic(int x1, int y1, int x2, int y2) {
        // Чебышёвское расстояние - оптимально для 8-направленного движения
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }
    
    private List<Edge> reconstructPath(Node targetNode) {
        List<Edge> path = new ArrayList<>();
        Node current = targetNode;
        
        // 🎯 Собираем путь от цели к старту
        while (current != null) {
            path.add(new Edge(current.x, current.y));
            current = current.parent;
        }
        
        // 🎯 Разворачиваем для правильного порядка (от старта к цели)
        Collections.reverse(path);
        
        // 🎯 DEBUG: Вывод пути
        System.out.println("📍 Найденный путь:");
        for (Edge e : path) {
            System.out.println("   [" + e.getX() + "," + e.getY() + "]");
        }
        
        return path;
    }
}