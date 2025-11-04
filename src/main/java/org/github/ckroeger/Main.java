package org.github.ckroeger;

import java.awt.*;
import java.util.Random;
import java.util.logging.Logger;

public class Main {

    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static final int STANDARD_MODE = 2;

    // Modus 1: Wartezeit in Millisekunden (60 Sekunden = 60000 ms)
    private static final int DELAY_MS = 1000;
    // Modus 1: Bewegung in Pixel
    private static final int PIXELS_TO_MOVE = STANDARD_MODE;

    // Modus 2: Konfiguration
    private static final int MOVE_RANGE = 100; // Maximale Distanz in Pixeln vom Startpunkt
    private static final int STEP_MIN = STANDARD_MODE; // Minimale Schrittweite in Pixeln
    private static final int STEP_MAX = 3; // Maximale Schrittweite in Pixeln
    private static final int STEP_DELAY_MIN_MS = 5; // Minimale Verzögerung zwischen Schritten
    private static final int STEP_DELAY_MAX_MS = 20; // Maximale Verzögerung zwischen Schritten
    private static final int PAUSE_MIN_MS = 2000; // Minimale Pause nach einer Bewegung (2 Sek)
    private static final int PAUSE_MAX_MS = 8000; // Maximale Pause nach einer Bewegung (8 Sek)

    private static final int USER_MOVE_TOLERANCE = 2; // Pixel
    private static final int INACTIVITY_TIME_MS = 30_000; // 30 Sekunden
    private static final int INACTIVITY_CHECK_INTERVAL_MS = 500;

    private static final Random SHARED_RANDOM = new Random();

    public static void main(String[] args) {
        int mode = STANDARD_MODE;
        // Modus aus Argumenten bestimmen
        if (args != null && args.length > 0) {
            String input = args[0].trim();
            try {
                mode = Integer.parseInt(input);
                if (mode != 1 && mode != 2) {
                    logger.warning(String.format("Ungueltiger Modus: %s. Standardmodus %d wird verwendet.", input, STANDARD_MODE));
                    mode = STANDARD_MODE;
                }
            } catch (NumberFormatException e) {
                logger.warning(String.format("Ungueltiges Argument: %s. Standardmodus %d wird verwendet.", input, STANDARD_MODE));
                mode = STANDARD_MODE;
            }
        }

        logger.info("--- Simple Activiti gestartet ---");
        logger.info("Es werden zwei Modus angeboten:");
        logger.info("1: " + (mode == 1 ? "(aktiv)" : "") + " Maus alle " + (DELAY_MS / 1000) + " Sekunden um " + PIXELS_TO_MOVE + " Pixel bewegen (wie bisher)");
        logger.info("2: " + (mode == 2 ? "(aktiv)" : "") + " Maus langsam und natuerlich in einem Bereich bewegen");
        logger.info(String.format("Modus kann als Argument uebergeben werden (1 oder 2). Standard: %d", STANDARD_MODE));
        logger.info("Druecken Sie STRG+C, um das Programm zu beenden.");

        try {
            Robot robot = new Robot();
            if (mode == 1) {
                runSimpleMode(robot);
            } else {
                runNaturalMode(robot);
            }
        } catch (AWTException e) {
            logger.severe("Fehler beim Erstellen der Robot-Instanz. Moeglicherweise fehlen Berechtigungen.");
            logger.severe(e.toString());
        } catch (InterruptedException e) {
            logger.info("--- Simple Activiti beendet ---");
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement") // Endlosschleife ist hier beabsichtigt
    private static void runSimpleMode(Robot robot) throws InterruptedException {
        logger.info("Einfacher Modus gestartet: Maus wird alle " + (DELAY_MS / 1000) + " Sekunden bewegt.");
        while (true) {
            Point currentPos = MouseInfo.getPointerInfo().getLocation();
            int currentX = currentPos.x;
            int currentY = currentPos.y;

            // 2. Maus um PIXELS_TO_MOVE (z.B. 1) Pixel verschieben
            robot.mouseMove(currentX + PIXELS_TO_MOVE, currentY);

            // 3. Warten
            Thread.sleep(DELAY_MS);

            // 4. Maus an die ursprüngliche Position zurückbewegen,
            //    aber mit einer kleinen Verzögerung, um die Aktivität zu simulieren.
            robot.mouseMove(currentX, currentY);

            // 5. Warten bis zur nächsten Bewegung
            Thread.sleep(DELAY_MS);
        }
    }

    @SuppressWarnings("InfiniteLoopStatement") // Endlosschleife ist hier beabsichtigt
    private static void runNaturalMode(Robot robot) throws InterruptedException {
        logger.info("Natuerlicher Modus gestartet: Maus wird langsam und zufaellig bewegt.");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Point startPos = MouseInfo.getPointerInfo().getLocation();
        int baseX = startPos.x;
        int baseY = startPos.y;
        while (true) { // Endlosschleife ist hier beabsichtigt
            Point target = getRandomTarget(baseX, baseY, screenSize, SHARED_RANDOM);
            boolean userMoved = moveMouseToTarget(robot, target, screenSize, SHARED_RANDOM);
            if (userMoved) {
                Point newBase = waitForUserInactivity();
                // Nach Inaktivität: Mauszeiger in die Bildschirmmitte bewegen
                int centerX = screenSize.width / 2;
                int centerY = screenSize.height / 2;
                robot.mouseMove(centerX, centerY);
                baseX = centerX;
                baseY = centerY;
            } else {
                int pause = SHARED_RANDOM.nextInt(PAUSE_MAX_MS - PAUSE_MIN_MS + STANDARD_MODE) + PAUSE_MIN_MS;
                Thread.sleep(pause);
            }
        }
    }

    private static Point getRandomTarget(int baseX, int baseY, Dimension screenSize, Random random) {
        int minX = Math.max(0, baseX - MOVE_RANGE);
        int maxX = Math.min(screenSize.width - STANDARD_MODE, baseX + MOVE_RANGE);
        int minY = Math.max(0, baseY - MOVE_RANGE);
        int maxY = Math.min(screenSize.height - STANDARD_MODE, baseY + MOVE_RANGE);

        int rangeX = maxX - minX + STANDARD_MODE;
        int rangeY = maxY - minY + STANDARD_MODE;

        int targetX = (rangeX > 0) ? (minX + random.nextInt(rangeX)) : minX;
        int targetY = (rangeY > 0) ? (minY + random.nextInt(rangeY)) : minY;

        return new Point(targetX, targetY);
    }

    private static boolean moveMouseToTarget(Robot robot, Point target, Dimension screenSize, Random random) throws InterruptedException {
        Point currentPos = MouseInfo.getPointerInfo().getLocation();
        int currentX = currentPos.x;
        int currentY = currentPos.y;
        while ((currentX != target.x || currentY != target.y)) {
            // Prüfen, ob Nutzer die Maus bewegt hat
            Point checkPos = MouseInfo.getPointerInfo().getLocation();
            if (Math.abs(checkPos.x - currentX) > USER_MOVE_TOLERANCE || Math.abs(checkPos.y - currentY) > USER_MOVE_TOLERANCE) {
                logger.info("Nutzer hat die Maus bewegt. Automatische Bewegung wird abgebrochen.");
                return true;
            }
            int dx = target.x - currentX;
            int dy = target.y - currentY;
            int stepX = calcStep(dx, random);
            int stepY = calcStep(dy, random);
            currentX += stepX;
            currentY += stepY;
            // Begrenzung auf Bildschirm
            currentX = Math.max(0, Math.min(currentX, screenSize.width - STANDARD_MODE));
            currentY = Math.max(0, Math.min(currentY, screenSize.height - STANDARD_MODE));
            robot.mouseMove(currentX, currentY);
            // Sleep in Schleife ist hier ok, da GUI-Thread und keine CPU-Last
            Thread.sleep(random.nextInt(STEP_DELAY_MAX_MS - STEP_DELAY_MIN_MS + STANDARD_MODE) + STEP_DELAY_MIN_MS);
        }
        return false;
    }

    private static int calcStep(int d, Random random) {
        if (Math.abs(d) < STEP_MAX) {
            return d;
        }
        int step = random.nextInt(STEP_MAX - STEP_MIN + STANDARD_MODE) + STEP_MIN;
        return d > 0 ? step : -step;
    }

    private static Point waitForUserInactivity() throws InterruptedException {
        logger.info("Warte auf 30 Sekunden Maus-Inaktivitaet...");
        long inactiveStart = System.currentTimeMillis();
        Point lastCheck = MouseInfo.getPointerInfo().getLocation();
        while (true) { // Endlosschleife ist hier beabsichtigt
            Thread.sleep(INACTIVITY_CHECK_INTERVAL_MS); // Sleep in Schleife ist hier ok
            Point now = MouseInfo.getPointerInfo().getLocation();
            if (Math.abs(now.x - lastCheck.x) > USER_MOVE_TOLERANCE || Math.abs(now.y - lastCheck.y) > USER_MOVE_TOLERANCE) {
                inactiveStart = System.currentTimeMillis();
                lastCheck = now;
            }
            if (System.currentTimeMillis() - inactiveStart >= INACTIVITY_TIME_MS) {
                logger.info("30 Sekunden Maus-Inaktivitaet erkannt. Automatische Bewegung wird fortgesetzt.");
                return now;
            }
        }

    }
}