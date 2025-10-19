package view;

import java.util.List;

public class ConsoleUI {
    public static void printHeader(String title) {
        String bar = repeat('═', 46);
        System.out.println("\n╔" + bar + "╗");
        System.out.println("║" + padCenter(title, 46) + "║");
        System.out.println("╠" + bar + "╣");
    }

    public static void printFooter() {
        String bar = repeat('═', 46);
        System.out.println("╚" + bar + "╝");
    }

    public static void printSection(String title) {
        String line = repeat('─', 46);
        System.out.println("├" + line + "┤");
        System.out.println("│" + padCenter(title, 46) + "│");
        System.out.println("├" + line + "┤");
    }

    public static void printMenu(List<String> items) {
        for (String item : items) {
            System.out.println("│ " + padRight(item, 46) + " │");
        }
    }

    public static String promptLabel(String label) {
        return label + ": ";
    }
    
    public static void pause() {
        System.out.println("\nNhấn Enter để tiếp tục...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignore
        }
    }

    private static String repeat(char c, int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) sb.append(c);
        return sb.toString();
    }

    private static String padRight(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        StringBuilder sb = new StringBuilder(width);
        sb.append(s);
        for (int i = s.length(); i < width; i++) sb.append(' ');
        return sb.toString();
    }

    private static String padCenter(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s.substring(0, width);
        int left = (width - s.length()) / 2;
        int right = width - s.length() - left;
        StringBuilder sb = new StringBuilder(width);
        for (int i = 0; i < left; i++) sb.append(' ');
        sb.append(s);
        for (int i = 0; i < right; i++) sb.append(' ');
        return sb.toString();
    }
}


