package org.github.ckroeger;

public class MainQuiet {
    public static void main(String[] args) {
        // quietMode in Main aktivieren
        try {
            java.lang.reflect.Field quietField = Main.class.getDeclaredField("quietMode");
            quietField.setAccessible(true);
            quietField.setBoolean(null, true);
        } catch (Exception e) {
            // Falls Reflection fehlschlägt, ignorieren
        }
        Main.main(args);
    }
}

