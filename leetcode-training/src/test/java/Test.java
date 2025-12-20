import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class Test {

    public static void main(String[] args) {
        int[] a = new int[] {1, 2, 3, 1, 2, 1, 3, 1, 9};
        HashMap<Integer, Integer> counterMap = new HashMap<>();
        for (int i = 0; i < a.length; i++) {
            if (counterMap.containsKey(a[i])) {
                int tmp = counterMap.get(a[i]);
                counterMap.put(a[i], tmp + 1);
            } else {
                counterMap.put(a[i], 1);
            }
        }
        System.out.println(Arrays.toString(a));
        Set<Integer> set = counterMap.keySet();
        int result = -1;
        for (Integer i : set) {
            if (counterMap.get(i) % 2 != 0) {
                // odd
                result = i;
            }
        }
        System.out.println(result);
        // Complexity : O(n)
    }
}
