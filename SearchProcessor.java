package edu.upenn.cit5940.processor;

import java.util.*;
import java.util.stream.Collectors;

import edu.upenn.cit5940.common.Article;
import edu.upenn.cit5940.datamanagement.Trie;
import edu.upenn.cit5940.logging.Logger;

public class SearchProcessor {
    private final Map<String, Set<Article>> invertedIndex;
    private final Trie trie;
    private final Logger logger;

    public SearchProcessor(Map<String, Set<Article>> invertedIndex, Trie trie, Logger logger) {
        this.invertedIndex = invertedIndex;
        this.trie = trie;
        this.logger = logger;
    }

    /**
     * 搜索包含所有关键词的文章（AND操作）
     */
    public List<String> search(String keywords) {
        logger.log("INFO", "User search query: \"" + keywords + "\"");
        
        if (keywords == null || keywords.trim().isEmpty()) {
            return new ArrayList<>();
        }

        String[] searchTerms = keywords.toLowerCase().split("\\s+");
        List<Set<Article>> articleSets = new ArrayList<>();

        // 获取每个关键词对应的文章集合
        for (String term : searchTerms) {
            Set<Article> articles = invertedIndex.get(term);
            if (articles != null) {
                articleSets.add(articles);
            } else {
                // 如果有一个关键词没有匹配，直接返回空结果
                return new ArrayList<>();
            }
        }

        if (articleSets.isEmpty()) {
            return new ArrayList<>();
        }

        // 执行AND操作：取所有集合的交集
        Set<Article> resultSet = new HashSet<>(articleSets.get(0));
        for (int i = 1; i < articleSets.size(); i++) {
            resultSet.retainAll(articleSets.get(i));
        }

        return resultSet.stream()
                .map(Article::getTitle)
                .collect(Collectors.toList());
    }

    /**
     * 获取前缀补全建议（最多10个）
     */
    public List<String> autocomplete(String prefix) {
        logger.log("INFO", "Autocomplete query: \"" + prefix + "\"");
        
        List<String> suggestions = trie.getWordsWithPrefix(prefix);
        
        // 限制返回最多10个建议
        if (suggestions.size() > 10) {
            return suggestions.subList(0, 10);
        }
        return suggestions;
    }

    /**
     * 获取搜索统计信息
     */
    public Map<String, Object> getSearchStats() {
        return Map.of(
            "indexedWords", invertedIndex.size(),
            "trieSize", trie.getSize() // 假设Trie有getSize方法
        );
    }
}