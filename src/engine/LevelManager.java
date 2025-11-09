package engine;

import enums.BrickType;
import javafx.scene.layout.Pane;
import object.brick.Brick;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static enums.BrickType.*;
import static utils.Constants.*;

public class LevelManager {
    private List<Brick> bricks;
    private int currentLevel;
    private boolean dynamicSpawning;
    private long lastSpawnTime;
    private boolean nextSpawnPattern;
    private final double SPAWN_INTERVAL = 15;

    public LevelManager() {
        this.bricks = new ArrayList<>();
        this.dynamicSpawning = false;
        this.lastSpawnTime = 0;
        this.nextSpawnPattern = true;
    }

    public void loadLevel(int levelNumber) {
        bricks.clear();
        this.currentLevel = levelNumber;
        this.dynamicSpawning = false;

        if (levelNumber == 0) {
            this.dynamicSpawning = true;
            this.lastSpawnTime = 0;
            this.nextSpawnPattern = true;

            addNewRowAtTop(this.nextSpawnPattern);
            this.nextSpawnPattern = !this.nextSpawnPattern;
            return;

        } else if (levelNumber == 1) {
            loadLevel1();
        } else if (levelNumber == 2) {
            loadLevel2();
        } else {
            loadLevelFromFile(levelNumber);
        }
    }

    private void loadLevel1() {
        int[][] map = {
                {1, 1, 1, 1, 5, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 5, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1, 1, 5, 1},
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 1, 1, 5, 4, 1, 1, 1}
        };
        createBricksFromMap(map);
    }

    private void loadLevel2() {
        int[][] map = {
                {0, 0, 2, 2, 2, 2, 0, 0},
                {0, 0, 2, 2, 2, 2, 0, 0},
                {0, 2, 2, 0, 0, 2, 2, 0},
                {0, 2, 2, 0, 0, 2, 2, 0},
                {0, 2, 2, 2, 2, 2, 2, 0},
                {0, 2, 2, 2, 2, 2, 2, 0},
                {0, 2, 2, 0, 0, 2, 2, 0},
                {0, 2, 2, 0, 0, 2, 2, 0}
        };
        createBricksFromMap(map);
    }

    private void loadLevelFromFile(int levelNumber) {
        String levelFile = "assets/levels/level_" + levelNumber + ".txt";

        try (Scanner scanner = new Scanner(new File(levelFile))) {
            double currentY = BRICK_START_Y;
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] brickTypes = line.split(" ");
                double currentX = BRICK_START_X;

                for (String type : brickTypes) {
                    BrickType brickType = getBrickTypeFromString(type);
                    if (brickType != null) {
                        Brick newBrick = Brick.createBrick(brickType, currentX, currentY);
                        bricks.add(newBrick);
                    }
                    currentX += BRICK_WIDTH + BRICK_PADDING;
                }
                currentY += BRICK_HEIGHT + BRICK_PADDING;
            }
        } catch (FileNotFoundException e) {
            System.err.println("Không tìm thấy file màn chơi: " + levelFile);
            e.printStackTrace();
        }
    }

    private void createBricksFromMap(int[][] map) {
        for (int j = 0; j < map.length; j++) {
            for (int i = 0; i < map[j].length; i++) {
                int brickCode = map[j][i];
                BrickType brickType = getBrickTypeFromCode(brickCode);

                if (brickType != null) {
                    double x = BRICK_START_X + i * (BRICK_WIDTH + BRICK_PADDING);
                    double y = BRICK_START_Y + j * (BRICK_HEIGHT + BRICK_PADDING);

                    Brick newBrick = Brick.createBrick(brickType, x, y);
                    bricks.add(newBrick);
                }
            }
        }
    }

    private BrickType getBrickTypeFromString(String type) {
        switch (type) {
            case "1": return NORMAL;
            case "2": return STRONG;
            case "3": return INDESTRUCTIBLE;
            case "4": return ELECTRIC;
            case "5": return EXPLODING;
            default: return null;
        }
    }

    private BrickType getBrickTypeFromCode(int code) {
        switch (code) {
            case 1: return NORMAL;
            case 2: return STRONG;
            case 3: return INDESTRUCTIBLE;
            case 4: return ELECTRIC;
            case 5: return EXPLODING;
            default: return null;
        }
    }

    public void updateDynamicSpawning(long now) {
        if (!dynamicSpawning) return;

        if (lastSpawnTime == 0) {
            lastSpawnTime = now;
        }

        double elapsedTime = (now - lastSpawnTime) / 1_000_000_000.0;

        if (elapsedTime >= SPAWN_INTERVAL) {
            moveAllBricksDown();
            addNewRowAtTop(nextSpawnPattern);
            nextSpawnPattern = !nextSpawnPattern;
            lastSpawnTime = now;
        }
    }

    public void moveAllBricksDown() {
        System.out.println("Đang đẩy gạch xuống...");
        double paddleTopY = PADDLE_POS_Y;

        for (Brick brick : bricks) {
            double newY = brick.getY() + (BRICK_HEIGHT + BRICK_PADDING);

            if (newY + BRICK_HEIGHT > paddleTopY) {
                System.out.println("Gạch đã chạm tới người chơi! Game Over.");
                return;
            }

            brick.setY(newY);
        }
    }

    public void addNewRowAtTop(boolean strongFirst) {
        System.out.println("Đang sinh hàng gạch mới ở trên cùng");
        double y = BRICK_START_Y;
        for (int i = 0; i < 8; i++) {
            double x = BRICK_START_X + i * (BRICK_WIDTH + BRICK_PADDING);
            BrickType brickType;
            if (strongFirst) {
                brickType = (i % 2 == 0) ? STRONG : NORMAL;
            } else {
                brickType = (i % 2 == 0) ? NORMAL : STRONG;
            }

            Brick newBrick = Brick.createBrick(brickType, x, y);
            bricks.add(newBrick);
        }
    }

    public List<Brick> getBricks() {
        return bricks;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public boolean isDynamicSpawning() {
        return dynamicSpawning;
    }

    public void setDynamicSpawning(boolean dynamicSpawning) {
        this.dynamicSpawning = dynamicSpawning;
    }

    public void clearBricks(Pane root) {
        for (Brick brick : bricks) {
            root.getChildren().removeAll(brick.getImageView(), brick.getCollisionShape());
        }
        bricks.clear();
    }

    public void removeBrick(Brick brick) {
        bricks.remove(brick);
    }
}