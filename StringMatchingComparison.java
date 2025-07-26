import java.util.*;

public class StringMatchingComparison {
    // Implementación del algoritmo KMP
    public static List<Integer> kmpSearch(String text, String pattern) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        
        if (m == 0) return matches;
        
        int[] lps = computeLPS(pattern);
        int i = 0; // Índice para text
        int j = 0; // Índice para pattern
        
        while (i < n) {
            if (text.charAt(i) == pattern.charAt(j)) {
                i++;
                j++;
            }
            if (j == m) {
                matches.add(i - j);
                j = lps[j - 1];
            } else if (i < n && text.charAt(i) != pattern.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        return matches;
    }
    
    private static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;
        
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    // Implementación del algoritmo Rabin-Karp
    public static List<Integer> rabinKarpSearch(String text, String pattern, int prime) {
        List<Integer> matches = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();
        
        if (n < m || m == 0) return matches;
        
        int base = 10; // Tamaño del alfabeto
        int h = 1;
        int patternHash = 0;
        int windowHash = 0;
        
        // Calcular h = base^(m-1) mod prime
        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % prime;
        }
        
        // Calcular hash del patrón y primera ventana
        for (int i = 0; i < m; i++) {
            patternHash = (base * patternHash + (pattern.charAt(i) - '0')) % prime;
            windowHash = (base * windowHash + (text.charAt(i) - '0')) % prime;
        }
        
        for (int i = 0; i <= n - m; i++) {
            // Verificar si los hashes coinciden
            if (patternHash == windowHash) {
                // Verificar caracter por caracter
                boolean match = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    matches.add(i);
                }
            }
            
            // Calcular hash para la siguiente ventana
            if (i < n - m) {
                windowHash = (base * (windowHash - (text.charAt(i) - '0') * h) + 
                            (text.charAt(i + m) - '0')) % prime;
                
                if (windowHash < 0) {
                    windowHash += prime;
                }
            }
        }
        return matches;
    }

    // Generar cadena aleatoria de dígitos
    public static String generateRandomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // Dígitos del 0 al 9
        }
        return sb.toString();
    }

    // Evaluar rendimiento
    public static void evaluatePerformance() {
        int prime = 101; // Número primo para Rabin-Karp
        int warmupRuns = 3;
        int testRuns = 10;
        
        // Escenario 1: Texto variable, patrón fijo
        System.out.println("Escenario 1: Texto variable, patrón fijo (tamaño 5)");
        System.out.println("Tamaño Texto\tKMP (ns)\tRabin-Karp (ns)");
        
        int[] textSizes = {1000, 10000, 50000, 100000, 500000, 1000000};
        String fixedPattern = "12345"; // Patrón fijo
        
        for (int size : textSizes) {
            String text = generateRandomString(size);
            
            long kmpTotal = 0;
            long rkTotal = 0;
            
            for (int i = 0; i < warmupRuns + testRuns; i++) {
                // Medir KMP
                long start = System.nanoTime();
                kmpSearch(text, fixedPattern);
                long end = System.nanoTime();
                if (i >= warmupRuns) kmpTotal += (end - start);
                
                // Medir Rabin-Karp
                start = System.nanoTime();
                rabinKarpSearch(text, fixedPattern, prime);
                end = System.nanoTime();
                if (i >= warmupRuns) rkTotal += (end - start);
            }
            
            System.out.printf("%d\t\t%d\t\t%d\n", 
                              size,
                              kmpTotal / testRuns,
                              rkTotal / testRuns);
        }
        
        // Escenario 2: Texto fijo, patrón variable
        System.out.println("\nEscenario 2: Texto fijo (100,000), patrón variable");
        System.out.println("Tamaño Patrón\tKMP (ns)\tRabin-Karp (ns)");
        
        int fixedTextSize = 100000;
        String fixedText = generateRandomString(fixedTextSize);
        int[] patternSizes = {2, 5, 10, 20, 50, 100, 200, 500, 1000};
        
        for (int size : patternSizes) {
            String pattern = generateRandomString(size);
            
            long kmpTotal = 0;
            long rkTotal = 0;
            
            for (int i = 0; i < warmupRuns + testRuns; i++) {
                // Medir KMP
                long start = System.nanoTime();
                kmpSearch(fixedText, pattern);
                long end = System.nanoTime();
                if (i >= warmupRuns) kmpTotal += (end - start);
                
                // Medir Rabin-Karp
                start = System.nanoTime();
                rabinKarpSearch(fixedText, pattern, prime);
                end = System.nanoTime();
                if (i >= warmupRuns) rkTotal += (end - start);
            }
            
            System.out.printf("%d\t\t%d\t\t%d\n", 
                              size,
                              kmpTotal / testRuns,
                              rkTotal / testRuns);
        }
    }

    public static void main(String[] args) {
        evaluatePerformance();
    }
}