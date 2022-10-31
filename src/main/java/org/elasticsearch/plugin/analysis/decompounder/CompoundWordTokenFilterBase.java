package org.elasticsearch.plugin.analysis.decompounder;

import java.io.IOException;
import java.util.LinkedList;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

public abstract class CompoundWordTokenFilterBase extends TokenFilter {

    public static final int DEFAULT_MIN_WORD_SIZE = 5;

    public static final int DEFAULT_MIN_SUBWORD_SIZE = 2;

    public static final int DEFAULT_MAX_SUBWORD_SIZE = 15;

    protected final CharArraySet dictionary;
    protected final LinkedList<CompoundToken> tokens;
    protected final int minWordSize;
    protected final int minSubwordSize;
    protected final int maxSubwordSize;
    protected final boolean onlyLongestMatch;

    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);

    private State current;

    protected CompoundWordTokenFilterBase(TokenStream input, CharArraySet dictionary, int minWordSize, int minSubwordSize, int maxSubwordSize, boolean onlyLongestMatch) {
        super(input);
        this.tokens = new LinkedList<>();
        if (minWordSize < 0) {
            throw new IllegalArgumentException("minWordSize cannot be negative");
        }
        this.minWordSize = minWordSize;
        if (minSubwordSize < 0) {
            throw new IllegalArgumentException("minSubwordSize cannot be negative");
        }
        this.minSubwordSize = minSubwordSize;
        if (maxSubwordSize < 0) {
            throw new IllegalArgumentException("maxSubwordSize cannot be negative");
        }
        this.maxSubwordSize = maxSubwordSize;
        this.onlyLongestMatch = onlyLongestMatch;
        this.dictionary = dictionary;
    }

    @Override
    public final boolean incrementToken() throws IOException {
        if (!tokens.isEmpty()) {
            assert current != null;
            CompoundToken token = tokens.removeFirst();
            restoreState(current);
            termAtt.setEmpty().append(token.txt);
            offsetAtt.setOffset(token.startOffset, token.startOffset + token.txt.length());
            return true;
        }

        current = null;
        if (input.incrementToken()) {
            if (termAtt.length() >= this.minWordSize) {
                decompose();
                if (!tokens.isEmpty()) {
                    current = captureState();
                    incrementToken();
                }
            }
            return true;
        } else {
            return false;
        }
    }

    protected abstract void decompose();

    @Override
    public void reset() throws IOException {
        super.reset();
        tokens.clear();
        current = null;
    }

}

