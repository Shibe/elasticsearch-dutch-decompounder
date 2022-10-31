package org.elasticsearch.plugin.analysis.decompounder;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class CompoundToken {
    public final CharSequence txt;
    public final int startOffset, endOffset;

    public CompoundToken(int offset, int length, int startOffset, CharTermAttribute term) {
        this.txt = term.subSequence(offset, offset + length);

        this.startOffset = startOffset + offset;
        this.endOffset = startOffset + offset + txt.length();
    }

    @Override
    public String toString() {
        return txt.toString();
    }
}
