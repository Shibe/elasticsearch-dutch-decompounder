package org.elasticsearch.plugin.analysis.decompounder;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class CompoundToken {
    public final CharSequence txt;
    public final int offset, startOffset, endOffset;

    public CompoundToken(
        int offset,
        int length,
        int startOffset,
        int endOffset,
        CharTermAttribute term
    ) {
        this.txt = term.subSequence(offset, offset + length);
        this.offset = offset;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    @Override
    public String toString() {
        return txt.toString();
    }
}
