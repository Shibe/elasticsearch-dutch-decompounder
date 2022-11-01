package org.elasticsearch.plugin.analysis.decompounder.dictionary;

import java.util.List;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.plugin.analysis.decompounder.CompoundToken;
import org.elasticsearch.plugin.analysis.decompounder.CompoundWordTokenFilterBase;
import org.elasticsearch.plugin.analysis.decompounder.WordDecompounder;

public class DictionaryCompoundWordTokenFilter extends CompoundWordTokenFilterBase {
    private final WordDecompounder decompounder;

    public DictionaryCompoundWordTokenFilter(
            TokenStream input,
            CharArraySet dictionary,
            int minWordSize,
            int minSubwordSize,
            int maxSubwordSize,
            boolean onlyLongestMatch
    ) {
        super(input, dictionary, minWordSize, minSubwordSize, maxSubwordSize, onlyLongestMatch);
        if (dictionary == null) {
            throw new IllegalArgumentException("dictionary must not be null");
        }
        decompounder = new WordDecompounder(minSubwordSize, maxSubwordSize, onlyLongestMatch, dictionary);
    }

    @Override
    protected void decompose() {
        int startOffset = offsetAtt.startOffset();
        List<CompoundToken> decomposed = decompounder.decompose(termAtt, startOffset, startOffset + termAtt.length());
        tokens.addAll(decomposed);
    }
}