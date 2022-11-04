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
            int minSubwordSize
    ) {
        super(input, dictionary, minWordSize, minSubwordSize);
        if (dictionary == null) {
            throw new IllegalArgumentException("dictionary must not be null");
        }
        decompounder = new WordDecompounder(minSubwordSize, dictionary);
    }

    @Override
    protected void decompose() {
        List<CompoundToken> decomposed = decompounder.decompose(
                termAtt,
                offsetAtt.startOffset(),
                offsetAtt.endOffset()
        );
        tokens.addAll(decomposed);
    }
}