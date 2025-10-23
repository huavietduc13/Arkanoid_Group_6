import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import object.Ball;
import object.Paddle;
import object.brick.Brick;
import enums.CollisionSide;

public class CollisionDetector {
    // Avoid floating-point precision errors
    private static final double EPSILON = 0.001;

    // Safe distance to push ball out of brick after collision
    private static final double SEPARATION_OFFSET = 0.5;

    private static final double MIN_SPEED = 6.0;
    private static final double MAX_SPEED = 8.0;

    // Handle collision between ball and brick
    public static boolean handleCollision(Ball ball, Brick brick) {
        if (brick.isDestroyed()) {
            return false;
        }

        // Get ball properties
        Circle ballShape = ball.getCollisionShape();
        double ballCenterX = ballShape.getCenterX();
        double ballCenterY = ballShape.getCenterY();
        double radius = ballShape.getRadius();
        double vx = ball.getVx();
        double vy = ball.getVy();

        // Get brick bounds
        Rectangle brickShape = brick.getCollisionShape();
        double brickLeft = brickShape.getX();
        double brickRight = brickShape.getX() + brickShape.getWidth();
        double brickTop = brickShape.getY();
        double brickBottom = brickShape.getY() + brickShape.getHeight();

        // Find closest point on brick to ball center
        double closestX = clamp(ballCenterX, brickLeft, brickRight);
        double closestY = clamp(ballCenterY, brickTop, brickBottom);

        // Calculate distance from ball center to closest point on brick
        double distanceX = ballCenterX - closestX;
        double distanceY = ballCenterY - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        // Check if collision occurred
        if (distanceSquared > radius * radius + EPSILON) {
            return false;
        }

        // Get collision side
        CollisionSide side = getCollisionSide(
                ballCenterX, ballCenterY, radius,
                vx, vy,
                brickLeft, brickRight, brickTop, brickBottom,
                closestX, closestY
        );

        // Apply bounce based on collision side
        applyBounce(ball, side);

        // Separate ball from brick to prevent sticking
        separateBallFromBrick(ball, brickLeft, brickRight, brickTop, brickBottom, side, radius);

        return true;
    }

    // Determine collision side based on current position and direction
    private static CollisionSide getCollisionSide(
            double ballCenterX, double ballCenterY, double radius,
            double vx, double vy,
            double brickLeft, double brickRight, double brickTop, double brickBottom,
            double closestX, double closestY
    ) {
        double brickCenterX = (brickLeft + brickRight) / 2;
        double brickCenterY = (brickTop + brickBottom) / 2;
        double brickHalfWidth = (brickRight - brickLeft) / 2;
        double brickHalfHeight = (brickBottom - brickTop) / 2;

        // Check if ball center is inside brick (should be rare but possible with high speed)
        boolean insideBrick = (ballCenterX > brickLeft && ballCenterX < brickRight &&
                ballCenterY > brickTop && ballCenterY < brickBottom);

        // Calculate relative position from brick center
        double relativeX = ballCenterX - brickCenterX;
        double relativeY = ballCenterY - brickCenterY;

        // Determine if this is a corner collision
        boolean isCornerCollision = isCornerHit(
                vx, vy,
                ballCenterX, ballCenterY, radius,
                brickLeft, brickRight, brickTop, brickBottom
        );

        if (isCornerCollision) {
            return CollisionSide.CORNER;
        }

        // If ball is inside brick, use velocity to determine exit direction
        if (insideBrick) {
            if (Math.abs(vx) > Math.abs(vy)) {
                return vx > 0 ? CollisionSide.LEFT : CollisionSide.RIGHT;
            } else {
                return vy > 0 ? CollisionSide.TOP : CollisionSide.BOTTOM;
            }
        }

        // Calculate penetration depth for each side
        double penetrationLeft = (ballCenterX + radius) - brickLeft;
        double penetrationRight = brickRight - (ballCenterX - radius);
        double penetrationTop = (ballCenterY + radius) - brickTop;
        double penetrationBottom = brickBottom - (ballCenterY - radius);

        // Find minimum penetration
        double minPenetration = Math.min(
                Math.min(penetrationLeft, penetrationRight),
                Math.min(penetrationTop, penetrationBottom)
        );

        // Use velocity direction as a tiebreaker
        if (minPenetration == penetrationTop && vy > 0) {
            return CollisionSide.TOP;
        } else if (minPenetration == penetrationBottom && vy < 0) {
            return CollisionSide.BOTTOM;
        } else if (minPenetration == penetrationLeft && vx > 0) {
            return CollisionSide.LEFT;
        } else if (minPenetration == penetrationRight && vx < 0) {
            return CollisionSide.RIGHT;
        }

        // Fallback: use aspect ratio to determine side
        double normalizedX = relativeX / brickHalfWidth;
        double normalizedY = relativeY / brickHalfHeight;

        if (Math.abs(normalizedX) > Math.abs(normalizedY)) {
            return relativeX > 0 ? CollisionSide.RIGHT : CollisionSide.LEFT;
        } else {
            return relativeY > 0 ? CollisionSide.BOTTOM : CollisionSide.TOP;
        }
    }


    // Check if ball is hitting a corner of a brick
    private static boolean isCornerHit(
            double vx, double vy,
            double ballCenterX, double ballCenterY, double radius,
            double brickLeft, double brickRight, double brickTop, double brickBottom
    ) {
        // Check if ball center is in corner regions
        boolean inLeftRegion = ballCenterX + 6 < brickLeft;     // ballRight < brickLeft
        boolean inRightRegion = ballCenterX - 6 > brickRight;   // ballLeft < brickRight
        boolean inTopRegion = ballCenterY + 6 < brickTop;       // ballBottom < brickTop
        boolean inBottomRegion = ballCenterY - 6 > brickBottom; // ballTop > brickBottom

        // Corner hit if ball is in both horizontal and vertical outer regions
        boolean isCorner = (
                (inTopRegion && inLeftRegion && vx > 0 && vy > 0) ||
                (inTopRegion && inRightRegion && vx < 0 && vy < 0) ||
                (inBottomRegion && inLeftRegion && vx > 0 && vy < 0) ||
                (inBottomRegion && inRightRegion && vx < 0 && vy < 0));

        if (!isCorner) {
            return false;
        }

        // Calculate distance to nearest corner
        double cornerX = inLeftRegion ? brickLeft : brickRight;
        double cornerY = inTopRegion ? brickTop : brickBottom;

        double dx = ballCenterX - cornerX;
        double dy = ballCenterY - cornerY;
        double distanceToCorner = Math.sqrt(dx * dx + dy * dy);

        // It's a corner hit if the ball is close enough to the corner
        return distanceToCorner <= radius * 1.1;
    }

    // Apply bounce to ball based on collision side
    private static void applyBounce(Ball ball, CollisionSide side) {
        switch (side) {
            case TOP:
            case BOTTOM:
                ball.reverseY();
                break;
            case LEFT:
            case RIGHT:
                ball.reverseX();
                break;
            case CORNER:
                ball.reverseX();
                ball.reverseY();
                break;
        }
    }


    // Separate ball from brick to prevent it from getting stuck
    private static void separateBallFromBrick(
            Ball ball,
            double brickLeft, double brickRight, double brickTop, double brickBottom,
            CollisionSide side, double radius
    ) {
        double brickCenterX = (brickLeft + brickRight) / 2;
        double brickCenterY = (brickTop + brickBottom) / 2;

        switch (side) {
            case TOP:
                ball.setCenterY(brickTop - radius - SEPARATION_OFFSET);
                break;
            case BOTTOM:
                ball.setCenterY(brickBottom + radius + SEPARATION_OFFSET);
                break;
            case LEFT:
                ball.setCenterX(brickLeft - radius - SEPARATION_OFFSET);
                break;
            case RIGHT:
                ball.setCenterX(brickRight + radius + SEPARATION_OFFSET);
                break;
            case CORNER:
                // Push ball away from nearest corner
                double dx = ball.getCenterX() - brickCenterX;
                double dy = ball.getCenterY() - brickCenterY;

                // Normalize and push ball out
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance > EPSILON) {
                    double pushX = (dx / distance) * (radius + SEPARATION_OFFSET);
                    double pushY = (dy / distance) * (radius + SEPARATION_OFFSET);

                    // Determine which corner
                    double cornerX = dx > 0 ? brickRight : brickLeft;
                    double cornerY = dy > 0 ? brickBottom : brickTop;

                    ball.setCenterX(cornerX + pushX);
                    ball.setCenterY(cornerY + pushY);
                } else {
                    // Fallback: push based on velocity direction
                    if (ball.getVx() > 0) {
                        ball.setCenterX(brickRight + radius + SEPARATION_OFFSET);
                    } else {
                        ball.setCenterX(brickLeft - radius - SEPARATION_OFFSET);
                    }

                    if (ball.getVy() > 0) {
                        ball.setCenterY(brickBottom + radius + SEPARATION_OFFSET);
                    } else {
                        ball.setCenterY(brickTop - radius - SEPARATION_OFFSET);
                    }
                }
                break;
        }
    }


    // Clamp value between min and max
    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

     // Handle collision between ball and paddle with angle variation
     // Bounce angle depends on hit position
     // Still developing
    public static boolean handlePaddleCollision(Ball ball, Paddle paddle) {
        // Get ball properties
        double ballCenterX = ball.getCenterX();
        double ballCenterY = ball.getCenterY();
        double radius = ball.getRadius();
        double vx = ball.getVx();
        double vy = ball.getVy();

        // Get paddle bounds from ImageView
        double paddleLeft = paddle.getX();
        double paddleRight = paddle.getX() + paddle.getWidth();
        double paddleTop = paddle.getY();
        double paddleBottom = paddle.getY() + paddle.getHeight();

        // Quick rejection test
        if (ballCenterY - radius > paddleBottom) {
            return false; // Ball is below paddle
        }
        if (ballCenterY + radius < paddleTop) {
            return false; // Ball is above paddle
        }
        if (ballCenterX + radius < paddleLeft) {
            return false; // Ball is left of paddle
        }
        if (ballCenterX - radius > paddleRight) {
            return false; // Ball is right of paddle
        }

        // Find closest point on paddle to ball center
        double closestX = clamp(ballCenterX, paddleLeft, paddleRight);
        double closestY = clamp(ballCenterY, paddleTop, paddleBottom);

        // Calculate distance
        double distanceX = ballCenterX - closestX;
        double distanceY = ballCenterY - closestY;
        double distanceSquared = distanceX * distanceX + distanceY * distanceY;

        // Check collision
        if (distanceSquared > radius * radius + EPSILON || vy < 0) {
            return false;
        }

        CollisionSide side = getPaddleCollisionSide(
                ballCenterX, ballCenterY, radius,
                vx, vy,
                paddleLeft, paddleRight, paddleTop
        );

        switch(side) {
            case TOP:
                handleTopCollision(ball, paddle, paddleLeft, paddleRight, paddleTop, radius);
                break;
            case LEFT:
            case RIGHT:
                ball.reverseX();
                break;
            case CORNER:
                ball.reverseX();
                ball.reverseY();
                break;
        }

        return true;
    }

    private static CollisionSide getPaddleCollisionSide(
            double ballCenterX, double ballCenterY, double radius,
            double vx, double vy,
            double paddleLeft, double paddleRight, double paddleTop
    ) {
        boolean inLeftRegion = ballCenterX < paddleLeft;
        boolean inRightRegion = ballCenterX > paddleRight;
        boolean inTopRegion = ballCenterY < paddleTop;

        if ((inLeftRegion && inTopRegion && vx > 0) ||
                (inRightRegion && inTopRegion && vx < 0)) {
            return CollisionSide.CORNER;
        }

        if (inLeftRegion && !inTopRegion && vx > 0) {
            return CollisionSide.LEFT;
        }

        if (inRightRegion && !inTopRegion && vx < 0) {
            return CollisionSide.RIGHT;
        }

        return CollisionSide.TOP;
    }

    public static void handleTopCollision(Ball ball, Paddle paddle,
                                             double paddleLeft, double paddleRight,
                                             double paddleTop, double radius) {

        double paddleWidth = paddleRight - paddleLeft;
        double hitPosition = (ball.getCenterX() - paddleLeft) / paddleWidth;

        if (0 <= hitPosition && hitPosition <= 1.0) {

            applyPaddleBounce(ball, hitPosition, paddle);

            ball.setCenterY(paddleTop - radius - SEPARATION_OFFSET);
        }
    }

    public static void handleCornerCollision(
            Ball ball,
            double paddleLeft, double paddleRight, double cornerY, double radius,
            boolean inLeftRegion, boolean inTopRegion
    ) {
        double ballCenterX = ball.getCenterX();
        double ballCenterY = ball.getCenterY();
        double vx = ball.getVx();
        double vy = ball.getVy();
        double cornerX = inLeftRegion ? paddleLeft : paddleRight;

        double dx = ballCenterX - cornerX;
        double dy = ballCenterY - cornerY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance <= radius + EPSILON) {
            double reflectAngle = Math.atan2(dy, dx) + Math.PI;
            double currentSpeed = Math.sqrt(vx * vx + vy * vy);
            double variation = Math.toRadians((Math.random() - 0.5) * 20);
            reflectAngle += variation;

            double newVx = currentSpeed * Math.cos(reflectAngle);
            double newVy = -currentSpeed * Math.sin(reflectAngle);

            double boost = 1.1;
            newVx *= boost;
            newVy *= boost;

            double finalSpeed = Math.sqrt(newVx * newVx + newVy * newVy);

            if (finalSpeed < MIN_SPEED) {
                double ratio = MIN_SPEED / finalSpeed;

                newVx *= ratio;
                newVy *= ratio;
            }

            if (finalSpeed > MAX_SPEED) {

                double ratio = MAX_SPEED / finalSpeed;

                newVx *= ratio;
                newVy *= ratio;
            }

            ball.setVx(newVx);
            ball.setVy(newVy);
        }
    }

     // Apply paddle bounce with angle variation based on hit position
     // - Center hits: steep angle (mostly vertical)
     // - Edge hits: shallow angle (more horizontal)
     private static void applyPaddleBounce(Ball ball, double hitPosition, Paddle paddle) {
         // Normalize hit position (-1.0 = left, +1.0 = right)
         double normalizedPosition = (hitPosition - 0.5) * 2.0;

         // Get current ball speed
         double currentSpeed = Math.sqrt(ball.getVx() * ball.getVx() + ball.getVy() * ball.getVy());

         // Calculate bounce angle based on hit position
         double minAngle = 45.0;  // Minimum angle at edges (degrees)
         double maxAngle = 65.0;  // Maximum angle at center (degrees)

         double bounceAngle = minAngle + (maxAngle - minAngle) * (1.0 - Math.abs(normalizedPosition));
         double bounceAngleInRadians = Math.toRadians(bounceAngle);

         // Calculate new velocity components from angle
         double newVx;

         if (-0.05 <= normalizedPosition && normalizedPosition <= 0.05) {
             newVx = currentSpeed * Math.sin(bounceAngleInRadians) * Math.signum(paddle.getVx());
         } else {
             newVx = currentSpeed * Math.sin(bounceAngleInRadians) * Math.signum(normalizedPosition);
         }

         double newVy = -currentSpeed * Math.cos(bounceAngleInRadians); // Negative = upward

         // Transfer 30% of paddle's velocity to ball
         double paddleVelocity = paddle.getVx();
         double velocityInfluence = paddleVelocity * 0.3;
         newVx += velocityInfluence;

         double finalSpeed = Math.sqrt(newVx * newVx + newVy * newVy);
         if (finalSpeed < MIN_SPEED) {
             double ratio = MIN_SPEED / finalSpeed;

             newVx *= ratio;
             newVy *= ratio;
         }

         if (finalSpeed > MAX_SPEED) {
             double ratio = MAX_SPEED / finalSpeed;

             newVx *= ratio;
             newVy *= ratio;

         }

         // Apply final velocity
         ball.setVx(newVx);
         ball.setVy(newVy);
     }
}
