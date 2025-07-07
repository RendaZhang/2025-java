# 🧠 Java Bit Manipulation Cheatsheet

## ✨ Common Bitwise Operators and Their Meanings

| Operator | Name | Example | Description |
|----------|-----------------|-----------|------------------------------------------|
| `&` | Bitwise AND | `x & y` | Result is 1 if both bits are 1 |
| `\|` | Bitwise OR | `x \| y` | Result is 1 if either bit is 1 |
| `^` | Bitwise XOR | `x ^ y` | Result is 1 if bits are different (toggle) |
| `~` | Bitwise NOT | `~x` | Inverts all bits |
| `<<` | Left Shift | `x << k` | Equivalent to `x * 2^k` (multiplication) |
| `>>` | Right Shift (Signed) | `x >> k` | Equivalent to `x / 2^k`, preserves sign |
| `>>>` | Unsigned Right Shift | `x >>> k` | Right shift and fill with 0 (unsigned division) |

______________________________________________________________________

## 🧮 Common Bit Manipulation Techniques

### ✅ Check Even/Odd

```
x & 1 == 0  // Even
x & 1 == 1  // Odd
```

### ✅ Swap Two Numbers (XOR Method)

```
a = a ^ b;
b = a ^ b;
a = a ^ b;
```

### ✅ Check if Power of 2

```
x > 0 && (x & (x - 1)) == 0
```

### ✅ Extract the Lowest Set Bit

```
x & -x
```

### ✅ Clear the Lowest Set Bit

```
x & (x - 1)
```

### ✅ Set the i-th Bit to 1

```
x |= (1 << i);
```

### ✅ Clear the i-th Bit to 0

```
x &= ~(1 << i);
```

### ✅ Toggle the i-th Bit

```
x ^= (1 << i);
```

### ✅ Check if the i-th Bit is 1

```
(x & (1 << i)) != 0
```

______________________________________________________________________

## 📊 Utility Methods in Java (Integer Class)

| Method | Purpose |
| -------------------------------------- | ------------------- |
| `Integer.bitCount(x)` | Count number of 1s in x |
| `Integer.toBinaryString(x)` | Convert to binary string |
| `Integer.highestOneBit(x)` | Return the highest set bit (power of 2) |
| `Integer.lowestOneBit(x)` | Return the lowest set bit (power of 2) |
| `Integer.reverse(x)` | Reverse all bits |
| `Integer.numberOfLeadingZeros(x)` | Count leading zeros |
| `Integer.numberOfTrailingZeros(x)` | Count trailing zeros |

______________________________________________________________________

## 💡 Common Interview Applications

| Type | Example LeetCode Problem | Common Bit Manipulation Technique |
| ---------- | -------------------------------- | --------------------------------- |
| Count 1s | 191. Number of 1 Bits | `x & (x - 1)` loop |
| Check Power of 2 | 231. Power of Two | `x > 0 && x & (x - 1) == 0` |
| Find Unique Number | 136. Single Number | `a ^ b` |
| Subset Enumeration | 78. Subsets | `mask = 0..(1<<n)` |
| State Compression DP | Traveling Salesman Problem | `mask` to represent sets |
| Space Compression | Boolean DP with multiple states | BitSet |

______________________________________________________________________

## 📌 Quick Mnemonics

- **AND clears, OR sets, XOR toggles just right**
- **Extract lowest: x & -x, Clear lowest: x & (x - 1)**
- **Check even/odd: x & 1, Check power of 2: x & (x - 1) == 0**
- **Enumerate all subsets: Iterate mask from 0 to (1 \<< n) - 1**

______________________________________________________________________

## 🧠 Advanced Examples

```java
int mid = (l + r) >>> 1; // Unsigned right shift to avoid integer overflow
int sign = x >> 31;      // Get the sign bit (0 for positive, -1 for negative)
```

______________________________________________________________________

## 🛠 Suggested Practice

1. Write a function to check if a number is a power of 2.
1. Write a function to count the number of 1s in the binary representation of an integer.
1. Implement subset enumeration to generate all possible combinations (using bitmask).
1. Use `BitSet` to compress a boolean array for efficient storage and manipulation.

______________________________________________________________________

## 🧠 Example Implementations

### 1. Check if a Number is a Power of 2

```java
public boolean isPowerOfTwo(int x) {
    return x > 0 && (x & (x - 1)) == 0;
}
```

### 2. Count the Number of 1s in a Binary Representation

```java
public int countOnes(int x) {
    int count = 0;
    while (x != 0) {
        x = x & (x - 1); // Clear the lowest set bit
        count++;
    }
    return count;
}
```

### 3. Subset Enumeration Using Bitmask

```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    int n = nums.length;
    for (int mask = 0; mask < (1 << n); mask++) {
        List<Integer> subset = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if ((mask & (1 << i)) != 0) { // Check if the i-th bit is set
                subset.add(nums[i]);
            }
        }
        result.add(subset);
    }
    return result;
}
```

### 4. Using `BitSet` to Compress a Boolean Array

```java
import java.util.BitSet;

public class BitSetExample {
    public static void main(String[] args) {
        BitSet bitSet = new BitSet(10); // Create a BitSet of size 10
        bitSet.set(2); // Set the 2nd bit to true
        bitSet.set(5); // Set the 5th bit to true
        System.out.println(bitSet.get(2)); // Check if the 2nd bit is set (true)
        System.out.println(bitSet.get(3)); // Check if the 3rd bit is set (false)
    }
}
```

______________________________________________________________________

## 📌 Final Tips for Practice

1. **Understand the Basics**: Make sure you fully grasp how each bitwise operator works and their common use cases.
1. **Practice LeetCode Problems**: Solve problems like "Number of 1 Bits," "Power of Two," and "Single Number" to reinforce your understanding.
1. **Experiment with Bitmasks**: Use bitmasks to represent subsets, states, or flags in your code.
1. **Optimize with Bitwise Operations**: Look for opportunities to replace arithmetic operations with bitwise ones for better performance.
1. **Test Edge Cases**: Always test your code with edge cases, such as negative numbers, zero, and large values.

______________________________________________________________________

By mastering these techniques and practicing regularly, you'll become proficient in using bit manipulation to solve complex problems efficiently. Good luck! 🚀
