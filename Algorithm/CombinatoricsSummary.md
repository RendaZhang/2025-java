# 🎯 Combinatorics Summary (Unified Perspective · Enhanced Version)

Most problems in combinatorics can be understood as **special cases or transformations of multiset permutations**. This document summarizes all major counting types and explains how they relate under one unified framework.

______________________________________________________________________

## 🧩 Multiset Permutations — The Unifying Core

**Definition:**
For a set with repeated elements, the number of distinct permutations is:

```
Multiset Permutations = n! / (k1! × k2! × ... × kr!)
```

Where:

- n is the total number of elements
- ki is the number of elements of type i

______________________________________________________________________

## ✅ Unified View of Combinatorics Types

| Type | A Form of Multiset Permutation? | Formula | Description |
|---------------------|----------------------------------|--------------------------------------------|-------------|
| Full permutation | ✅ Yes | n! | All elements are distinct |
| k-permutation | ✅ Yes | n! / (n - k)! | Choose k and order them |
| Combination | ✅ Yes | n! / (k! × (n - k)!) | Equivalent to arranging k 1’s and (n - k) 0’s |
| Multiset permutation| ✅ Core model | n! / (k1! × k2! × ... × kr!) | Permutations with repeated elements |
| Combination with replacement | ✅ Yes | (n + k - 1)! / (k! × (n - 1)!) | "Stars and Bars" → k stars and (n - 1) bars |
| Derangement | ❌ Not directly | D(n) = n! × (1 - 1/1! + 1/2! - 1/3! + ...) | Permutations with positional constraints |
| Circular permutation| ✅ (via equivalence classes) | (n - 1)! | Remove overcounted rotations |
| Catalan number | ✅ (indirectly) | Cn = (1 / (n + 1)) × (2n choose n) | Structured combinatorics (e.g., valid parentheses) |
| Stirling number | ✅ (set partitioning) | S(n, k) | Ways to partition n items into k non-empty subsets |
| Integer partition | ✅ (numerical multisets) | No closed-form formula | Ways to split a number into positive integers |

______________________________________________________________________

## 🔍 Deep Dive: Why Combination is a Multiset Permutation

### Regular combination C(n, k)

Equivalent to arranging a multiset of:

- k ones (1) → selected
- (n - k) zeros (0) → not selected

Permutation count:

```
n! / (k! × (n - k)!) = C(n, k)
```

→ Combination = binary (0/1) multiset permutation

______________________________________________________________________

### Combination with replacement C(n + k - 1, k)

Model using "stars and bars":

- k stars (selected items)
- n - 1 bars (to divide types)

This becomes a multiset with (k + n - 1) elements:

```
(n + k - 1)! / (k! × (n - 1)!) = C(n + k - 1, k)
```

→ Also a multiset permutation!

______________________________________________________________________

## 🧠 Knowledge Tree (Text Version)

```
Combinatorics
├── Full Permutations (n!) ← All unique elements
├── k-Permutations (P(n, k)) ← Choose and order k
├── Combinations (C(n, k)) ← Equivalent to 01 multiset permutation
├── Multiset Permutations (Core model)
│   ├── Combination with Replacement (stars + bars)
│   └── General multisets (e.g. MISSISSIPPI)
├── Circular Permutations (n! divided by rotations)
├── Catalan Numbers (structured combinations like parentheses)
├── Stirling Numbers (set partitions)
└── Integer Partitions (number-theoretic decompositions)
```

______________________________________________________________________

## 🧪 Practice Tips

- Model combinations using "01 encoding"
- Understand combinations with replacement via "stars and bars"
- Reduce complex problems into multiset permutation format
- Know the key difference: **Do we care about order?**

______________________________________________________________________

## ✅ Python Example: Multiset Permutation Counter

```python
import math
from collections import Counter

def multiset_permutation_count(elements):
    freq = Counter(elements)
    total = sum(freq.values())
    denom = math.prod(math.factorial(v) for v in freq.values())
    return math.factorial(total) // denom

# Example: AABBB → ['A','A','B','B','B']
print(multiset_permutation_count(['A','A','B','B','B']))  # Output: 10
```

______________________________________________________________________

## 📚 Keywords Recap

- Multiset Permutation（多重集合排列）
- Stars and Bars（星星与隔板法）
- Combination with Replacement（可重组合）
- Binary Encoding of Combinations（01编码）
- Derangement（错排）
- Stirling Number（斯特林数）
- Partition Number（整数拆分数）

______________________________________________________________________
