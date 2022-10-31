package org.elasticsearch.plugin.analysis.decompounder;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class WordDecompounder {
    private CharArraySet dictionary;
    private final int minWordSize;
    private final int minSubwordSize;
    private final int maxSubwordSize;
    private final boolean onlyLongestMatch;

    public WordDecompounder(int minWordSize, int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch, Set<String> dictionary) {

        this.minWordSize = minWordSize;
        this.minSubwordSize = minSubwordSize;
        this.maxSubwordSize = maxSubwordSize;
        this.onlyLongestMatch = onlyLongestMatch;
        this.dictionary = new CharArraySet(dictionary, true);
    }

    public WordDecompounder(int minWordSize, int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch, CharArraySet dictionary) {

        this.minWordSize = minWordSize;
        this.minSubwordSize = minSubwordSize;
        this.maxSubwordSize = maxSubwordSize;
        this.onlyLongestMatch = onlyLongestMatch;
        this.dictionary = dictionary;
    }

    public List<CompoundToken> decompose(CharTermAttribute term, int startOffset, int endOffset) {
        List<CompoundToken> tokens = new LinkedList<>();
        final int len = term.length();
        CompoundToken longestMatchToken = null;
        for (int i = 0; i <= len - this.minSubwordSize; ++i) {
            for (int j = this.minSubwordSize; j <= this.maxSubwordSize; ++j) {
                if (i + j > len) {
                    break;
                }
                if (dictionary.contains(term.buffer(), i, j)) {
                    if (this.onlyLongestMatch) {
                        if (longestMatchToken != null) {
                            if (longestMatchToken.txt.length() < j) {
                                longestMatchToken = new CompoundToken(i, j, startOffset, term);
                            }
                        } else {
                            longestMatchToken = new CompoundToken(i, j, startOffset, term);
                        }
                    } else {
                        tokens.add(new CompoundToken(i, j, startOffset, term));
                    }
                }
            }
        }
        if (this.onlyLongestMatch && longestMatchToken != null) {
            if (longestMatchToken.txt.length() != term.length()) {
                if (longestMatchToken.startOffset == startOffset) {
                    tokens.add(longestMatchToken);
                    tokens.add(new CompoundToken(longestMatchToken.txt.length(), len - longestMatchToken.txt.length(), startOffset, term));
                } else if (longestMatchToken.endOffset == endOffset) {
                    tokens.add(new CompoundToken(0, len - longestMatchToken.txt.length(), startOffset, term));
                    tokens.add(longestMatchToken);
                } else {
                    tokens.add(new CompoundToken(0, longestMatchToken.startOffset - startOffset, startOffset, term));
                    tokens.add(longestMatchToken);
                    tokens.add(new CompoundToken(longestMatchToken.startOffset - startOffset + longestMatchToken.txt.length(), endOffset - longestMatchToken.endOffset, startOffset, term));
                }
            } else {
                tokens.add(longestMatchToken);
            }
        }
        return tokens;
    }
}
