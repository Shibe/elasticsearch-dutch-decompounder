package org.elasticsearch.plugin.analysis.decompounder;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class WordDecompounderTest {

    private static Set<String> dictionary;
    private static List<String> terms;

    @BeforeAll
    static void setUp() throws IOException {
        Path dictionaryPath = Paths.get("src/test/resources/dictionary.txt");
        dictionary = new HashSet<>(Files.readAllLines(dictionaryPath));

        Path termsPath = Paths.get("src/test/resources/compound-terms.txt");
        terms = Files.readAllLines(termsPath);
    }

    @Test
    void decomposeSingleLongest() {
        WordDecompounder decompounder = new WordDecompounder(3, dictionary);

        CharTermAttribute term = new CharTermAttributeImpl();
        term.append("roodbaarsfilet");
        List<CompoundToken> tokens = decompounder.decompose(term, 0, term.length());
        System.out.println(tokens);
    }

    @Test
    void decomposeListLongest() {
        WordDecompounder decompounder = new WordDecompounder(3, dictionary);
        decomposeList(decompounder);
    }

    @Test
    void decomposeList() {
        WordDecompounder decompounder = new WordDecompounder(3, dictionary);
        decomposeList(decompounder);
    }

    private void decomposeList(WordDecompounder decomposer) {
        terms.stream()
                .map(it -> {
                            CharTermAttributeImpl term = new CharTermAttributeImpl();
                            term.append(it);
                            return term;
                        }
                )
                .forEach(it -> {
                            System.out.print(it + "\t -> \t");
                            System.out.println(decomposer.decompose(it, 0, it.length()));
                        }
                );
    }
}