# PROJECT_REPORT.md 

#CIT 5940 Final Project — *In-Memory Tech News Search Engine*

**Author:** Yeager Ye
**Student ID: ** [STUDENT_ID_REDACTED]
**Program:** MCIT Online
**Course:** CIT 5940 — Data Structures & Software Design
**Submission Date:** November 10, 2025
**Development Environment:** macOS (MacBook Pro) using Eclipse IDE 2025-06 Release, JDK 21, and Maven

---

##  1. Introduction

I am the sole developer and submitter of this final project.
All coding, testing, and debugging were conducted independently on my MacBook Pro using Eclipse IDE 2025-06 (4.35) with Java 21 and Maven project. As requirement, this project integrates the core concepts from HW5, HW6, and HW8 into a cohesive software system, namely an In-Memory Tech News Search Engine capable of:

1. Efficient keyword-based searches,
2. Title autocompletion using a Trie,
3. Chronological trend and topic analysis, and
4. Date-based article queries with real-time CLI interaction.

The development journey was intense and hands-on. I repeatedly ran the project on my MacBook’s Eclipse terminal and command line to verify correctness, logging, and user interface behavior. I eventually ran it over 30 times local IDE and uploaded at least 10 iterations to Canvas gradescope before achieving full Autograder success. This project not only strengthened my technical confidence but also taught me patience and persistence under tight deadlines. Although we were given about three to four weeks to complete this project, my professional commitments left me with only around a week to ten days to finish it. Aware of the tight schedule, I refused to give myself any excuse to procrastinate. I stayed disciplined and focused, striving to balance work and life while dedicating roughly four and a half to six hours each day to steady, consistent progress on the project.

## ️ 2. Compilation and Execution Instructions

### Environment Setup
OS: macOS Sonoma
IDE: Eclipse 2025-06 (4.35) Release
JDK Version: 21
Build Tool: Maven
Project Structure: `edu.upenn.cit5940_project`

### This structure was carefully follow the cit5940-project-skeleton zip document.
cit5940-project/
├── pom.xml
├── logs/
│   └── tech_news_search.log
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── edu/
│   │   │       └── upenn/
│   │   │           └── cit5940/
│   │   │               ├── ui/
│   │   │               │   └── CLI.java
│   │   │               ├── processor/
│   │   │               │   ├── SearchProcessor.java
│   │   │               │   ├── TopicAnalysisProcessor.java
│   │   │               │   └── ArticleQueryProcessor.java
│   │   │               ├── datamanagement/
│   │   │               │   ├── CSVDataLoader.java
│   │   │               │   ├── JSONDataLoader.java
│   │   │               │   ├── DataLoaderFactory.java
│   │   │               │   └── ArticleRepository.java
│   │   │               ├── common/
│   │   │               │   ├── Article.java
│   │   │               │   └── StopWords.java
│   │   │               ├── logging/
│   │   │               │   └── Logger.java
│   │   │               └── Main.java
│   │   └── resources/
│   │       ├── articles_small.csv
│   │       ├── articles.csv
│   │       ├── articles_small.json
│   │       ├── articles.json
│   │       └── stop_words.txt
│   └── test/
│       └── java/
│           └── edu/
│               └── upenn/
│                   └── cit5940/
│                       └── tests/
│                           ├── SearchProcessorTest.java
│                           ├── TrieTest.java
│                           └── DataLoaderTest.java
└── target/
    └── cit5940-project-0.0.1-SNAPSHOT.jar



The project was imported as a Maven project in Eclipse using the official starter files provided by the CIT 5940 teaching team, including the preconfigured `pom.xml` file. I did not modify the XML — instead, my main challenge was correctly linking the Maven build system and JDK 21 in Eclipse, ensuring that package recognition and directory indexing worked properly before the first successful compilation.

### Compilation

To compile the project manually:

```bash
mvn clean compile
```

To create the executable JAR:

```bash
mvn clean package
```

The packaged file is generated at:

```
target/cit5940-project-0.0.1-SNAPSHOT.jar
```

---

### Execution

The program accepts two optional command-line arguments:

1. Path to data file (`.csv` or `.json`)
2. Path to log file

Defaults:

Data: `data/articles.csv`
Log: `tech_news_search.log`

#### Example Runs:

```bash
# Default execution
java -jar target/cit5940-project-0.0.1-SNAPSHOT.jar

# Specify dataset
java -jar target/cit5940-project-0.0.1-SNAPSHOT.jar data/articles.json

# Specify dataset and log file
java -jar target/cit5940-project-0.0.1-SNAPSHOT.jar data/articles.csv logs/my_run.log
```

#### Sample Startup Output

```
=== Tech News Search Engine ===
Initializing n-tier architecture...
Loading articles from: data/articles.csv
1000 articles loaded
Architecture initialization complete!
```

---

## 3. System Architecture

I implemented a clear **four-tier architecture** to ensure modularity and maintainability.

UI Layer (Presentation) – edu.upenn.cit5940.ui
Responsible for all user-facing interactions. This includes parsing user inputs, displaying menus, providing command-line interface (CLI) prompts, and guiding users through the system in an intuitive way. It ensures smooth communication between the user and the application.

Logic Layer (Application) – edu.upenn.cit5940.processor
Implements the core business logic of the application. This includes processing user requests, performing searches on the dataset, generating analytics, identifying topic trends, and orchestrating workflow between the UI and data layers. It acts as the “brain” of the system.

Data Layer (Management) – edu.upenn.cit5940.datamanagement
Handles all data-related operations, including loading data from CSV or JSON files, ensuring robustness through error handling, validating data formats, and creating efficient in-memory structures for fast access. It abstracts the complexity of data management from the rest of the system.

Common Layer (Utilities) – edu.upenn.cit5940.common, edu.upenn.cit5940.logging
Provides reusable utility components shared across the system. This includes data object definitions, helper functions, and a global logging mechanism for consistent message tracking, debugging, and monitoring system behavior.


Dependency direction:

```
CLI → Processors → Data Management → Models + Logger
```

Initialization sequence in `Main.java`:

```java
public static void main(String[] args) {
    String dataFile = (args.length > 0) ? args[0] : "data/articles.csv";
    String logFile  = (args.length > 1) ? args[1] : "tech_news_search.log";
    Logger.init(logFile);

    DataLoader loader = DataLoaderFactory.createDataLoader(dataFile);
    List<Article> articles = loader.loadArticles(dataFile);

    DataRepository repo = new DataRepository(articles);
    SearchProcessor searchProcessor = new SearchProcessor(repo);
    CLI cli = new CLI(searchProcessor, repo);
    cli.run();
}
```

---

##  4. Data Structures and Algorithms

To make the system efficient and scalable, I selected in-memory structures carefully:

Keyword Search – HashMap<String, Set<Article>>
Implements an inverted index where each keyword maps to a set of articles containing it. This structure allows extremely fast lookups, averaging O(1) time, and replaces a traditional BST approach to improve performance for large datasets. It ensures that searching for articles by keywords is efficient and scalable.

Title Autocomplete – Trie
Uses a prefix tree (Trie) to efficiently store and retrieve article titles. When a user types a partial title, the Trie allows the system to return up to 10 relevant suggestions almost instantly. This improves user experience by providing quick, accurate autocomplete functionality.

Date Queries – TreeMap<LocalDate, List<Article>>
Stores articles sorted by publication date, enabling fast retrieval of articles for specific dates or ranges. The subMap() method supports range queries efficiently, making it ideal for features like “articles from the last week” or “articles between two dates.”

Topic Analysis – HashMap<String, Integer> + PriorityQueue
Tracks the frequency of topics across all articles using a HashMap, and then uses a PriorityQueue to extract the top-N most frequent topics. This combination ensures both fast counting and efficient retrieval of trending topics for analytics or dashboard features.

Stopword Filtering – HashSet<String>
Maintains a set of common stopwords to exclude them during text processing. Using a HashSet allows constant-time checks to quickly skip irrelevant words, improving the accuracy and efficiency of keyword searches and topic analysis.
               |

#### Example: Keyword Search Implementation

```java
public List<Article> search(String... keywords) {
    List<Set<Article>> sets = new ArrayList<>();
    for (String word : keywords) {
        word = word.toLowerCase();
        if (index.containsKey(word)) sets.add(index.get(word));
    }
    if (sets.isEmpty()) return Collections.emptyList();

    Set<Article> result = new HashSet<>(sets.get(0));
    for (Set<Article> s : sets.subList(1, sets.size())) result.retainAll(s);
    return new ArrayList<>(result);
}
```

This approach allows **AND-style** keyword matching and efficiently scales with the dataset size.

---

## 5. Design Patterns

### Singleton — `Logger.java`

Ensures a single, thread-safe instance for all logging operations.

```java
public class Logger {
    private static Logger instance;
    private PrintWriter writer;

    private Logger(String path) { ... }

    public static void init(String path) {
        if (instance == null) instance = new Logger(path);
    }

    public static Logger getInstance() { return instance; }

    public synchronized void log(String level, String msg) {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        writer.printf("[%s] %s  %s%n", time, level, msg);
        writer.flush();
    }
}
```

### Factory — `DataLoaderFactory.java`

Used to abstract away file format differences.

```java
public class DataLoaderFactory {
    public static DataLoader createDataLoader(String file) {
        if (file.endsWith(".csv")) return new CSVDataLoader();
        if (file.endsWith(".json")) return new JSONDataLoader();
        throw new IllegalArgumentException("Unsupported file format.");
    }
}
```

This pattern makes it easy to add future extensions like XML or Parquet loaders.

---

## 6. Logging and Error Handling

Logging is implemented comprehensively to meet course requirements:

```
[2025-11-06 18:10:02] INFO  Application starting
[2025-11-06 18:10:05] INFO  Loaded 1000 articles
[2025-11-06 18:10:25] WARN  Skipping malformed record at line 984442
[2025-11-06 18:10:30] INFO  User command: "search AI chips"
```

Error handling ensures no unhandled exceptions:

* Invalid menu input → “Invalid choice. Please enter 1–4.”
* Malformed date → “Error: please use YYYY-MM-DD format.”
* Data inconsistencies → Skipped and logged, not fatal.

---

##  7. Integration of Homework Components

| Homework             | Integration Description                                                            | ---------------------------------------------------------------------------------- |
| HW5 (Data Parsing)   | Reused and improved CSV/JSON readers; added line-skipping logic for malformed data |
| HW6 (Inverted Index) | Replaced inefficient BST structure with HashMap for speed                          |
| HW8 (Trie & TreeMap) | Added Trie for autocompletion and TreeMap for chronological analysis               |

---

##  8. Personal Experience and Struggles

Developing this project on my MacBook using Eclipse was a rigorous but rewarding experience.

1. aven and Project Structure Challenges:
   The official starter package worked correctly, but Eclipse’s Maven integration initially failed to recognize the `src/main/java` hierarchy and package paths. I had to reimport the project several times and reset the build path to align with JDK 21. Once fixed, compilation and testing worked seamlessly.

2. Data Parsing Difficulties:
   Many records in the large dataset were malformed — missing commas, invalid dates, or incomplete fields. Initially, this caused the loader to throw exceptions. I enhanced the parsing logic using multiple `DateTimeFormatter` patterns and explicit error handling to skip malformed lines while keeping accurate load counts in the log.

3. CLI Precision for Autograder:
   The Autograder tested exact string matches, which meant even small deviations (like a missing “goodbye” or “main menu”) would fail tests. I spent hours revising prompts and outputs, running each build through local console checks. It took over 10 submission attempts before achieving complete match success.

4. Debugging Marathon:
   I ran the program 30+ times locally, using both Eclipse Run configurations and direct terminal commands.
   Eclipse’s step debugger (`Run → Debug As → Java Application`) helped me trace variable states and resolve null references in `SearchProcessor` and `CLI`.

5. Final Passing Moment:
   When I finally saw “Autograder Score 50.0 / 50.0” in Canvas gradescope after so many retries, I genuinely felt a sense of grater accomplishment. 

---

## 9. Known Limitations and Future Enhancements

Current tokenization is basic (split by whitespace); a future version could include stemming or NLP preprocessing.
The system loads all data into memory; large-scale datasets could benefit from a persistent or lazy-loading backend.
Logging is text-based; future versions could implement SLF4J for advanced log management.
CLI lacks automated unit tests — integration with JUnit 5 and mock input streams would be a natural next step.

---

## 10. Conclusion

Developing the In-Memory Tech News Search Engine from scratch was the most challenging and transformative experience of my CIT 5940 journey. Through trial, debugging, and continuous refinement, I transformed a fragmented set of assignments into a complete, modular, and robust Java application.

I learned:

1. How to structure multi-layered Java systems,
2. How to gracefully handle real-world data inconsistencies,
3. How to meet strict automated grading requirements, and
4. Most importantly, how persistence and clear architecture design lead to success.

This project marks the point where I truly felt like an engineer rather than a student — debugging, reasoning, and delivering under constraints.

By Yeager Ye
MCIT Online Student, University of Pennsylvania
November 10 2025


