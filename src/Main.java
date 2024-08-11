import java.util.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();
        Thread maxValueThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int maxKey = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
                    System.out.println("Самое частое количество повторений  " + maxKey + " (встретилось " + sizeToFreq.get(maxKey) + " раз)");
                }
            }
        });
        maxValueThread.start();

        for (int i = 0; i < 1000; i++) {
            Thread thread = new Thread(() -> {
                String s = generateRoute("RLRFR", 100);
                int counter = 0;
                char[] chars = s.toCharArray();
                for (int j = 0; j < chars.length; j++) {
                    if (chars[j] == 'R') {
                        counter++;
                    }
                }
                synchronized (sizeToFreq) {
                    if (sizeToFreq.get(counter) != null) {
                        sizeToFreq.put(counter, sizeToFreq.get(counter) + 1);
                    } else {
                        sizeToFreq.put(counter, 1);
                    }
                    sizeToFreq.notify();
                }
                System.out.println(counter);
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join(); // зависаем, ждём когда поток объект которого лежит в thread завершится
        }
        maxValueThread.interrupt();

        int maxKey = Collections.max(sizeToFreq.entrySet(), Map.Entry.comparingByValue()).getKey();
        System.out.println("Самое частое количество повторений  " + maxKey + " (встретилось " + sizeToFreq.get(maxKey) + " раз)");
        System.out.println("Другие размеры:");
        for (Map.Entry<Integer, Integer> entry : sizeToFreq.entrySet()) {
            if (entry.getKey() != maxKey) {
                System.out.println("-" + entry.getKey() + " (" + entry.getValue() + " раз)");
            }
        }
    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}