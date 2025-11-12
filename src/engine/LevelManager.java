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
    private boolean scrollingEnabled = false;
    private boolean spawningFromPattern = false;
    private int[][] levelPattern;
    private int currentRowToSpawn;
    private boolean isScrollingActive = false;
    private int initialBreakableBricks = 0;
    private List<Brick> justSpawnedBricks = new ArrayList<>();

    public LevelManager() {
        this.bricks = new ArrayList<>();
        this.dynamicSpawning = false;
        this.lastSpawnTime = 0;
        this.nextSpawnPattern = true;
        this.scrollingEnabled = false;
        this.spawningFromPattern = false;
        this.currentRowToSpawn = 0;
    }

    public void loadLevel(int levelNumber) {
        bricks.clear();
        this.currentLevel = levelNumber;
        this.dynamicSpawning = false;
        this.scrollingEnabled = false;
        this.spawningFromPattern = false;
        this.justSpawnedBricks.clear();
        this.currentRowToSpawn = 0;
        this.levelPattern = null;
        this.isScrollingActive = false;
        this.initialBreakableBricks = 0;

        if (levelNumber == 0) {
            loadLevel0();

        }  else if (levelNumber == 1) {
            loadLevel1();
        }
        else if (levelNumber == 2) {
            loadLevel2();
        }
        else if (levelNumber == 3) {
            loadLevel3();
        }
        else if (levelNumber == 4) {
            loadLevel4();
        } else if (levelNumber == 5) {
            loadLevel5();
        } else {
            loadLevelFromFile(levelNumber);
        }
    }

    private void loadLevel0() {
        int[][] mapLevel0 = {
                {1, 1, 1, 1, 1, 1, 1, 1},
                {1, 2, 4, 4, 4, 4, 2, 1},
                {2, 2, 3, 3, 3, 3, 2, 2},
                {1, 2, 5, 5, 5, 5, 2, 1},
                {1, 1, 1, 1, 1, 1, 1, 1}
        };

        createBricksFromMap(mapLevel0);
        this.initialBreakableBricks = countBricksInMap(mapLevel0);
    }

    private void loadLevel1() {
        int[][] mapStatic_G = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 4, 2, 2, 4, 0, 0},
                {0, 4, 2, 0, 0, 0, 0, 0},
                {0, 2, 2, 0, 0, 0, 0, 0},
                {0, 5, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 5, 2, 2, 0, 0}
        };

        int[][] mapDynamic_Rest = {
                {0, 0, 2, 2, 2, 2, 0, 0},
                {0, 2, 5, 0, 0, 4, 2, 0},
                {0, 2, 2, 4, 0, 2, 5, 0},
                {0, 4, 2, 0, 0, 4, 2, 0},
                {0, 2, 5, 0, 0, 5, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

                {0, 2, 2, 2, 2, 0, 0, 0},
                {0, 2, 4, 0, 0, 2, 0, 0},
                {0, 2, 2, 5, 0, 5, 0, 0},
                {0, 4, 2, 0, 4, 2, 0, 0},
                {0, 5, 2, 2, 5, 0, 0, 0}
        };
        createBricksFromMap(mapStatic_G);

        this.levelPattern = mapDynamic_Rest;
        this.spawningFromPattern = true;
        this.lastSpawnTime = 0;
        this.initialBreakableBricks = countBricksInMap(mapStatic_G) + countBricksInMap(mapDynamic_Rest);
    }

    private void loadLevel2() {
        int[][] mapStatic_G = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 2, 4, 2, 5, 2, 2, 0},
                {0, 0, 0, 2, 5, 2, 0, 0},
                {0, 0, 0, 2, 2, 2, 0, 0},
                {0, 0, 0, 4, 2, 4, 0, 0},
                {0, 0, 0, 5, 2, 5, 0, 0}
        };

        int[][] mapDynamic_Rest = {
                {0, 2, 4, 2, 5, 4, 2, 0},
                {0, 2, 2, 0, 0, 0, 0, 0},
                {0, 2, 5, 2, 4, 2, 0, 0},
                {0, 2, 2, 0, 0, 0, 0, 0},
                {0, 2, 5, 2, 5, 2, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

                {0, 2, 2, 2, 2, 2, 2, 0},
                {0, 0, 0, 4, 2, 4, 0, 0},
                {0, 0, 0, 2, 5, 2, 0, 0},
                {0, 0, 0, 2, 2, 2, 0, 0},
                {0, 2, 4, 2, 4, 2, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

                {0, 0, 0, 2, 2, 0, 0, 0},
                {0, 0, 2, 5, 2, 0, 0, 0},
                {0, 2, 4, 0, 0, 2, 5, 0},
                {0, 2, 2, 4, 0, 2, 2, 0},
                {0, 5, 2, 0, 0, 2, 5, 0}
        };
        createBricksFromMap(mapStatic_G);

        this.levelPattern = mapDynamic_Rest;
        this.spawningFromPattern = true;
        this.lastSpawnTime = 0;
        this.initialBreakableBricks = countBricksInMap(mapStatic_G) + countBricksInMap(mapDynamic_Rest);
    }

    private void loadLevel3() {
        int[][] mapStatic_G = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 2, 4, 2, 5, 2, 2, 0},
                {0, 0, 0, 2, 5, 0, 0, 0},
                {0, 0, 0, 2, 2, 0, 0, 0},
                {0, 0, 0, 4, 2, 0, 0, 0},
                {0, 0, 0, 2, 5, 0, 0, 0}
        };

        int[][] mapDynamic_Rest = {
                {0, 2, 2, 4, 2, 2, 2, 0},
                {0, 2, 2, 0, 0, 0, 0, 0},
                {0, 2, 5, 2, 4, 2, 0, 0},
                {0, 2, 2, 0, 0, 0, 0, 0},
                {0, 2, 5, 2, 2, 5, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

                {0, 2, 2, 2, 2, 2, 2, 0},
                {0, 0, 0, 4, 2, 2, 0, 0},
                {0, 0, 0, 2, 5, 2, 0, 0},
                {0, 0, 0, 4, 2, 4, 0, 0},
                {0, 2, 2, 2, 2, 2, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

                {0, 0, 0, 2, 2, 0, 0, 0},
                {0, 0, 2, 5, 2, 0, 0, 0},
                {0, 2, 2, 0, 0, 2, 2, 0},
                {0, 2, 4, 4, 0, 2, 5, 0},
                {0, 2, 2, 0, 5, 2, 2, 0}
        };
        createBricksFromMap(mapStatic_G);

        this.levelPattern = mapDynamic_Rest;
        this.spawningFromPattern = true;
        this.lastSpawnTime = 0;
        this.initialBreakableBricks = countBricksInMap(mapStatic_G) + countBricksInMap(mapDynamic_Rest);
    }

    private void loadLevel4() {
        int[][] mapStatic_G = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 4, 2, 2, 4, 0, 0},
                {0, 4, 2, 0, 0, 0, 0, 0},
                {0, 2, 2, 0, 0, 0, 0, 0},
                {0, 5, 2, 0, 0, 0, 0, 0},
                {0, 0, 2, 5, 2, 2, 0, 0}
        };

        int[][] mapDynamic_Rest = {
                {0, 0, 2, 2, 2, 2, 0, 0},
                {0, 2, 5, 0, 0, 4, 2, 0},
                {0, 2, 2, 4, 0, 2, 5, 0},
                {0, 4, 2, 0, 0, 4, 2, 0},
                {0, 2, 5, 0, 0, 5, 2, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},

                {0, 2, 2, 2, 2, 0, 0, 0},
                {0, 2, 4, 0, 0, 2, 0, 0},
                {0, 2, 2, 5, 0, 5, 0, 0},
                {0, 4, 2, 0, 4, 2, 0, 0},
                {0, 5, 2, 2, 5, 0, 0, 0}
        };
        createBricksFromMap(mapStatic_G);

        this.levelPattern = mapDynamic_Rest;
        this.spawningFromPattern = true;
        this.lastSpawnTime = 0;
        this.initialBreakableBricks = countBricksInMap(mapStatic_G) + countBricksInMap(mapDynamic_Rest);
    }


    private void loadLevel5() {
        int[][] mapLevel5 = {
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {3, 3, 0, 0, 0, 0, 3, 3},
        };

        createBricksFromMap(mapLevel5);
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

    public boolean updateDynamicSpawning(long now) {
        if ((!dynamicSpawning && !scrollingEnabled && !spawningFromPattern) || !isScrollingActive) {
            return false;
        }

        double elapsedTime = (now - lastSpawnTime) / 1_000_000_000.0;

        if (elapsedTime >= SPAWN_INTERVAL) {
            if (moveAllBricksDown()) {
                return true;
            }

            if (dynamicSpawning) {
                addNewRowAtTop(nextSpawnPattern);
            } else if (spawningFromPattern) {
                spawnNextRowFromPattern();
            }

            lastSpawnTime = now;
        }

        return false;
    }

    public boolean moveAllBricksDown() {
        System.out.println("Đang đẩy gạch xuống...");
        double paddleTopY = PADDLE_POS_Y;

        for (Brick brick : bricks) {
            if (brick.isDestroyed()) {
                continue;
            }

            double newY = brick.getY() + (BRICK_HEIGHT + BRICK_PADDING);

            if (newY + BRICK_HEIGHT > paddleTopY) {
                System.out.println("Gạch đã chạm tới người chơi! Game Over.");
                return true;
            }

            brick.setY(newY);
        }

        return false;
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
            justSpawnedBricks.add(newBrick);
        }
    }

    private void spawnNextRowFromPattern() {
        if (levelPattern == null || currentRowToSpawn >= levelPattern.length) {
            spawningFromPattern = false;
            System.out.println("Đã sinh xong toàn bộ map pattern.");
            return;
        }

        int[] rowToSpawn = levelPattern[currentRowToSpawn];
        double y = BRICK_START_Y;

        for (int i = 0; i < rowToSpawn.length; i++) {
            int brickCode = rowToSpawn[i];
            BrickType brickType = getBrickTypeFromCode(brickCode);

            if (brickType != null) {
                double x = BRICK_START_X + i * (BRICK_WIDTH + BRICK_PADDING);
                Brick newBrick = Brick.createBrick(brickType, x, y);

                bricks.add(newBrick);
                justSpawnedBricks.add(newBrick);
            }
        }

        currentRowToSpawn++;
    }

    public List<Brick> getJustSpawnedBricks() {
        if (justSpawnedBricks.isEmpty()) {
            return new ArrayList<>(); // Trả về list rỗng
        }

        List<Brick> newBricks = new ArrayList<>(justSpawnedBricks);
        justSpawnedBricks.clear();
        return newBricks;
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

    public void activateScrolling() {
        if ((this.dynamicSpawning || this.spawningFromPattern) && !this.isScrollingActive) {
            this.isScrollingActive = true;
            this.lastSpawnTime = System.nanoTime();
        }
    }

    private int countBricksInMap(int[][] map) {
        int count = 0;
        if (map == null) {
            return 0;
        }

        for (int j = 0; j < map.length; j++) {
            for (int i = 0; i < map[j].length; i++) {
                int brickCode = map[j][i];
                if (brickCode != 0 && brickCode != 3) {
                    count++;
                }
            }
        }
        return count;
    }

    public int getInitialBreakableBricks() {
        return this.initialBreakableBricks;
    }

    public int countRemainingBreakableBricks() {
        int remaining = 0;
        for (Brick brick : bricks) {
            if (brick.getType() != enums.BrickType.INDESTRUCTIBLE && !brick.isDestroyed()) {
                remaining++;
            }
        }
        return remaining;
    }
}