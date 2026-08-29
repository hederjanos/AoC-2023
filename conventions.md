# Conventions

## 1. Solver Implementation & Data State
* **Solver Naming:** The core logic class must be named `Day<N>Solver` (e.g., `Day1Solver`).
* **Inheritance:** The solver must extend the abstract class `util.common.Solver<V>`, where `V` is the return type of the answers (usually `Long` or `Integer`).
* **Parsed Data Reusability:** 
  * Only store parsed data as `private final` instance fields in the solver if the exact same data structure is reusable across both `solvePartOne()` and `solvePartTwo()`. 
  * If parsing logic or the resulting models differ between part one and part two, parse the input inline within the respective solver methods rather than at the class level.

## 2. Encapsulation, Immutability, and Models
Domain concepts specific to the day should be encapsulated as inner types. Immutability should be the default approach.

* **Class vs. Record:**
  * **Use `private static final class`:** If the domain class contains a lot of logic or behavior, it should be a regular class.
  * **Use `private record`:** If the domain object is primarily a data carrier, or if it will be used in hash-based collections. It is acceptable for a record to contain one or two small helper methods.
* **True Immutability:** 
  * Marking fields as `final` (or using a `record`) only provides *reference* immutability. It does not prevent the contents of mutable reference types (like `List`, `Set`, `Map`, or arrays) from being modified. 
  * To achieve true deep immutability, ensure that mutable reference types are wrapped in unmodifiable views (e.g., `List.copyOf()`, `Collections.unmodifiableMap()`) when assigning them in constructors or returning them via getters.
  
## 3. Visibility Modifiers
* **Inner Types:** All helper classes, records, and enums must be `private` to avoid polluting the global namespace. Classes should also be `static` and `final`.
* **Inner Fields & Methods:** For consistency, always use the explicit `private` modifier for all fields, even within `private` inner classes. Factory methods accessed by the outer class can be package-private.

## 4. Parsing Logic
* **Factory Methods:** Complex parsing of the `puzzle` list should be delegated to static factory methods on the inner domain classes rather than cluttering the solver's constructor.
