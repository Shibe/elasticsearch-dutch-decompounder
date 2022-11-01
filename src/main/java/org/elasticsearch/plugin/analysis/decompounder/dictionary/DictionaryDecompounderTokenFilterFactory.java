package org.elasticsearch.plugin.analysis.decompounder.dictionary;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenFilterFactory;
import org.elasticsearch.plugin.analysis.decompounder.CompoundWordTokenFilterBase;

public class DictionaryDecompounderTokenFilterFactory extends AbstractTokenFilterFactory {

    private final boolean onlyLongestMatch;
    private final Path wordListFile;
    private List<String> wordList;

    public DictionaryDecompounderTokenFilterFactory(
            IndexSettings indexSettings,
            Environment environment,
            String name,
            Settings settings
    ) {
        super(indexSettings, name, settings);
        onlyLongestMatch = settings.getAsBoolean("only_longest_match", false);
        wordList = settings.getAsList("word_list", Collections.emptyList());

        String wordListPath = settings.get("word_list_path", null);
        wordListFile = resolveFile(environment, wordListPath);
    }

    private Path resolveFile(Environment environment, String path) {
        if (path != null) {
            return environment.configFile().resolve(path);
        }
        return null;
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        if (wordListFile == null && wordList.isEmpty()) {
            throw new IllegalArgumentException("'word_list_path' or 'word_list' is a required parameter.");
        }

        if (wordListFile != null) {
            try {
                InputStream in = Files.newInputStream(wordListFile);
                wordList = Collections.unmodifiableList(IOUtils.readLines(in, "UTF-8"));
            } catch (Exception e) {
                throw new IllegalArgumentException("Exception while reading word_list_path.", e);
            }
        }

        CharArraySet dictionary = new CharArraySet(wordList, true);
        return new DictionaryCompoundWordTokenFilter(
                tokenStream,
                dictionary,
                CompoundWordTokenFilterBase.DEFAULT_MIN_WORD_SIZE,
                CompoundWordTokenFilterBase.DEFAULT_MIN_SUBWORD_SIZE,
                CompoundWordTokenFilterBase.DEFAULT_MAX_SUBWORD_SIZE,
                onlyLongestMatch
        );
    }
}
