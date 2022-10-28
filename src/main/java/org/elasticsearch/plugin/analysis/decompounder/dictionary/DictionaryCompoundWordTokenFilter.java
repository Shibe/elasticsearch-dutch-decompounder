package org.elasticsearch.plugin.analysis.decompounder.dictionary;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.CharArraySet;
import org.elasticsearch.plugin.analysis.decompounder.CompoundWordTokenFilterBase;

public class DictionaryCompoundWordTokenFilter extends CompoundWordTokenFilterBase {

    public DictionaryCompoundWordTokenFilter(TokenStream input, CharArraySet dictionary,
                                             int minWordSize, int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch) {
        super(input, dictionary, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
        if (dictionary == null) {
            throw new IllegalArgumentException("Dictionary must not be null.");
        }
    }

    @Override
    protected void decompose() {
        final int len = termAtt.length();
        CompoundToken longestMatchToken = null;
        for (int i = 0; i <= len - this.minSubwordSize; ++i) {
            for (int j = this.minSubwordSize; j <= this.maxSubwordSize; ++j) {
                if (i + j > len) {
                    break;
                }
                if (dictionary.contains(termAtt.buffer(), i, j)) {
                    if (this.onlyLongestMatch) {
                        if (longestMatchToken != null) {
                            if (longestMatchToken.txt.length() < j) {
                                longestMatchToken = new CompoundToken(i, j);
                            }
                        } else {
                            longestMatchToken = new CompoundToken(i, j);
                        }
                    } else {
                        tokens.add(new CompoundToken(i, j));
                    }
                }
            }
        }

        if (this.onlyLongestMatch && longestMatchToken != null) {
            if (longestMatchToken.txt.length() != this.termAtt.length()) {
                if (longestMatchToken.startOffset == this.offsetAtt.startOffset()) {
                    tokens.add(longestMatchToken);
                    tokens.add(new CompoundToken(longestMatchToken.txt.length(), len - longestMatchToken.txt.length()));
                } else if (longestMatchToken.endOffset == this.offsetAtt.endOffset()) {
                    tokens.add(new CompoundToken(0, len - longestMatchToken.txt.length()));
                    tokens.add(longestMatchToken);
                } else {
                    tokens.add(new CompoundToken(0, longestMatchToken.startOffset - this.offsetAtt.startOffset()));
                    tokens.add(longestMatchToken);
                    tokens.add(new CompoundToken(longestMatchToken.startOffset - this.offsetAtt.startOffset() + longestMatchToken.txt.length(), this.offsetAtt.endOffset() - longestMatchToken.endOffset));
                }
            } else {
                tokens.add(longestMatchToken);
            }
        }
    }
}