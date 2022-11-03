package org.elasticsearch.plugin.analysis.decompounder;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class WordDecompounder {
    private CharArraySet dictionary;
    private final int minSubwordSize;
    private final int maxSubwordSize;
    private final boolean onlyLongestMatch;

    public WordDecompounder(int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch, Set<String> dictionary) {
        this.minSubwordSize = minSubwordSize;
        this.maxSubwordSize = maxSubwordSize;
        this.onlyLongestMatch = onlyLongestMatch;
        this.dictionary = new CharArraySet(dictionary, true);
    }

    public WordDecompounder(int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch, CharArraySet dictionary) {
        this.minSubwordSize = minSubwordSize;
        this.maxSubwordSize = maxSubwordSize;
        this.onlyLongestMatch = onlyLongestMatch;
        this.dictionary = dictionary;
    }


    private CompoundToken findLongestMatch(CharTermAttribute term, int startOffset, int endOffset) {
        CompoundToken longestMatch = null;
        for (int offset = 0; offset <= term.length() - this.minSubwordSize; offset++) {
            for (int len = this.minSubwordSize; len <= term.length(); len++) {
                if (offset + len > term.length()) {
                    break;
                }
                if (dictionary.contains(term.buffer(), offset, len)) {
                    int hitStartOffset = startOffset + offset;
                    CompoundToken hit = new CompoundToken(
                            offset,
                            len,
                            hitStartOffset,
                            hitStartOffset + len,
                            term
                    );
                    if (longestMatch == null) {
                        longestMatch = hit;
                    } else if (longestMatch.txt.length() < len) {
                        longestMatch = hit;
                    }
                }
            }
        }
        return longestMatch;
    }

    private CompoundToken prefixToken(CompoundToken compoundToken, int startOffset, CharTermAttribute term) {
        return new CompoundToken(
                0,
                compoundToken.startOffset - startOffset,
                startOffset,
                compoundToken.startOffset,
                term
        );
    }

    private CompoundToken suffixToken(CompoundToken compoundToken, int startOffset, int endOffset, CharTermAttribute term) {
        // Certain characters might have been removed, resulting in a bigger offset.
        int offsetDifference = endOffset - startOffset - term.length();
        return new CompoundToken(
                compoundToken.offset + compoundToken.txt.length(),
                endOffset - compoundToken.endOffset - offsetDifference,
                compoundToken.endOffset,
                endOffset,
                term
        );
    }


    public List<CompoundToken> decompose(CharTermAttribute term, int startOffset, int endOffset) {
        List<CompoundToken> tokens = new LinkedList<>();

        CompoundToken longestMatchToken = findLongestMatch(term, startOffset, endOffset);

        // Sometimes the endOffset is bigger, because characters were removed from the token.
        int termEndOffset = startOffset + term.length();

        if (longestMatchToken != null) {
            if (longestMatchToken.txt.length() == term.length()) {
                // Longest match is equal to the term.
                tokens.add(
                    new CompoundToken(
                        longestMatchToken.offset,
                        longestMatchToken.txt.length(),
                        longestMatchToken.startOffset,
                        endOffset,
                        term
                    )
                );
            } else if (longestMatchToken.startOffset == startOffset) {
                // Longest match was found at the beginning of the term.
                tokens.add(longestMatchToken);
                tokens.add(suffixToken(longestMatchToken, startOffset, endOffset, term));
            } else if (longestMatchToken.endOffset == termEndOffset) {
                // Longest match was found at the end of the term.
                tokens.add(prefixToken(longestMatchToken, startOffset, term));
                tokens.add(
                        new CompoundToken(
                                longestMatchToken.offset,
                                longestMatchToken.txt.length(),
                                longestMatchToken.startOffset,
                                endOffset,
                                term
                        )
                );
            } else {
                // Longest match was found in the middle of the term.
                tokens.add(prefixToken(longestMatchToken, startOffset, term));
                tokens.add(longestMatchToken);
                tokens.add(suffixToken(longestMatchToken, startOffset, endOffset, term));
            }
        }

        return tokens;
    }
}
