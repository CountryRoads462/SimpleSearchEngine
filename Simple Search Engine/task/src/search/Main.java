package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.System.exit;

public class Main {
    static Map<String, Set<Integer>> map = new HashMap<>();
    static Map<Integer, String> people = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);

    static void menu() {
        while (true) {
            System.out.println("=== Menu ===\n" +
                    "1. Find a person\n" +
                    "2. Print all people\n" +
                    "0. Exit");
            switch (scanner.nextLine()) {
                case "1":
                    System.out.println();
                    selectStrategy();
                    break;
                case "2":
                    System.out.println();
                    printAllPeople();
                    break;
                case "0":
                    System.out.print("\nBye!");
                    exit(0);
                default:
                    System.out.println("\nIncorrect option! Try again.");
                    break;
            }
            System.out.println();
        }
    }

    static void printAllPeople() {
        System.out.println("=== List of people ===");
        for (var entry :
                people.entrySet()) {
            System.out.println(entry.getValue());
        }
    }

    static void selectStrategy() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        String strategy = scanner.nextLine();

        System.out.println("\nEnter a name or email to search all suitable people.");
        String[] query = scanner.nextLine().split(" ");
        System.out.println();

        Map<String, Set<Integer>> queryMap = new HashMap<>();
        String keyWithMinSize = null;
        int minSize = Integer.MAX_VALUE;

        for (String word :
                query) {
            if (map.containsKey(word)) {
                int size = map.get(word).size();
                if (size < minSize) {
                    minSize = size;
                    keyWithMinSize = word;
                }
                queryMap.put(word, map.get(word));
            }
        }

        List<String> foundPeople = new ArrayList<>();
        switch (strategy) {
            case "ALL":
                if (keyWithMinSize != null) {
                    foundPeople = allStrategy(queryMap, keyWithMinSize);
                }
                break;
            case "ANY":
                foundPeople = anyStrategy(queryMap);
                break;
            case "NONE":
                foundPeople = noneStrategy(queryMap);
                break;
            default:
                break;
        }

        if (foundPeople.size() == 0) {
            System.out.println("No matching people found.");
        } else {
            System.out.printf("%d persons found:\n", foundPeople.size());
            for (String person :
                    foundPeople) {
                System.out.println(person);
            }
        }
    }

    static List<String> allStrategy(Map<String, Set<Integer>> queryMap, String keyWithMinSize) {
        List<String> foundPeople = new ArrayList<>();
        boolean flag1 = false;
        for (Integer num :
                queryMap.get(keyWithMinSize)) {
            boolean isMatch = false;
            for (var entry :
                    queryMap.entrySet()) {
                if (!entry.getKey().equals(keyWithMinSize)) {
                    if (entry.getValue().contains(num)) {
                        isMatch = true;
                    } else {
                        isMatch = false;
                        flag1 = true;
                    }
                }
                if (flag1) {
                    break;
                }
            }

            if (isMatch) {
                foundPeople.add(people.get(num));
            }
        }

        return foundPeople;
    }

    static List<String> anyStrategy(Map<String, Set<Integer>> queryMap) {
        List<String> foundPeople = new ArrayList<>();
        SortedSet<Integer> numbers = new TreeSet<>();
        for (var entry :
                queryMap.entrySet()) {
            numbers.addAll(entry.getValue());
        }

        for (Integer num :
                numbers) {
            foundPeople.add(people.get(num));
        }
        
        return foundPeople;
    }

    static List<String> noneStrategy(Map<String, Set<Integer>> queryMap) {
        List<String> foundPeople = new ArrayList<>();
        SortedSet<Integer> numbers = new TreeSet<>();
        for (var entry :
                queryMap.entrySet()) {
            numbers.addAll(entry.getValue());
        }

        for (var entry :
                people.entrySet()) {
            if (!numbers.contains(entry.getKey())) {
                foundPeople.add(entry.getValue());
            }
        }

        return foundPeople;
    }

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i += 2) {
            if (args[i].equals("--data")) {
                File inputFile = new File(args[i + 1]);
                try (Scanner fileScanner = new Scanner(inputFile)) {
                    int numberOfRow = 0;
                    while (fileScanner.hasNextLine()) {
                        String line = fileScanner.nextLine();
                        people.put(numberOfRow, line);
                        
                        String[] wordsInLine = line.toLowerCase(Locale.ROOT).split(" ");
                        for (String word :
                                wordsInLine) {
                            if (map.containsKey(word)) {
                                Set<Integer> set = map.get(word);
                                set.add(numberOfRow);
                                map.put(word, set);
                            } else {
                                map.put(word.toLowerCase(Locale.ROOT), new LinkedHashSet<>(Set.of(numberOfRow)));
                            }
                        }

                        numberOfRow++;
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("No file found: " + inputFile.getPath());
                }
            }
        }

        menu();
    }
}
