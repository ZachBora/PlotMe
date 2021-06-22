package com.worldcretornica.plotme.utils;

public class MinecraftFontWidthCalculator {
    private static String charWidthIndexIndex;
    private static int[] charWidths;
    
    public static int getStringWidth(final String s) {
        int i = 0;
        if (s != null) {
            for (int j = 0; j < s.length(); ++j) {
                i += getCharWidth(s.charAt(j));
            }
        }
        return i;
    }
    
    public static int getCharWidth(final char c) {
        final int k = MinecraftFontWidthCalculator.charWidthIndexIndex.indexOf((int)c);
        if (c != '§' && k >= 0) {
            return MinecraftFontWidthCalculator.charWidths[k];
        }
        return 0;
    }
    
    static {
        MinecraftFontWidthCalculator.charWidthIndexIndex = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~\u2302\u00c7\u00fc\u00e9\u00e2\u00e4\u00e0\u00e5\u00e7\u00ea\u00eb\u00e8\u00ef\u00ee\u00ec\u00c4\u00c5\u00c9\u00e6\u00c6\u00f4\u00f6\u00f2\u00fb\u00f9\u00ff\u00d6\u00dc\u00f8£\u00d8\u00d7\u0192\u00e1\u00ed\u00f3\u00fa\u00f1\u00d1ªº¿®¬½¼¡«»";
        MinecraftFontWidthCalculator.charWidths = new int[] { 16, 6, 18, 15, 18, 18, 18, 18, 20, 20, 20, 24, 8, 24, 8, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 8, 8, 20, 24, 20, 24, 28, 24, 24, 24, 24, 24, 24, 24, 24, 16, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 18, 24, 16, 24, 24, 12, 24, 24, 24, 24, 24, 20, 24, 24, 8, 24, 20, 12, 24, 24, 24, 24, 24, 24, 24, 16, 24, 24, 24, 24, 24, 24, 20, 8, 20, 28, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 16, 24, 12, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 16, 24, 24, 12, 24, 24, 24, 24, 24, 24, 24, 28, 24, 24, 24, 8, 24, 24, 32, 36, 36, 24, 24, 24, 32, 32, 24, 32, 32, 32, 32, 32, 24, 24, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 36, 24, 36, 36, 36, 20, 36, 36, 32, 28, 28, 32, 28, 32, 32, 32, 28, 32, 32, 28, 36, 36, 24, 28, 28, 28, 28, 28, 36, 24, 28, 32, 28, 24, 24, 36, 28, 24, 28, 4 };
    }
}
