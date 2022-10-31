package org.elasticsearch.plugin.analysis.decompounder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class WordDecompounderTest {

    private static Set<String> dictionary;
    private static List<String> terms;

    @BeforeAll
    static void setUp() throws IOException {
        Path dictionaryPath = Paths.get("src/test/resources/title-and-taxonomy-words-3chars.txt");
        dictionary = new HashSet<>(Files.readAllLines(dictionaryPath));
        // System.out.println(dictionary.stream().limit(50).collect(Collectors.toList()));

        Path termsPath = Paths.get("src/test/resources/ingredients-with-no-matches-product-service-list.txt");
        terms = Files.readAllLines(termsPath);
        // System.out.println(terms.stream().limit(50).collect(Collectors.toList()));
    }

    @Test
    void decomposeSingleLongest() {
        WordDecompounder decompounder = new WordDecompounder(3, 3, 20, true, dictionary);

        CharTermAttribute term = new CharTermAttributeImpl();
        term.append("roodbaarsfilet");
        List<CompoundToken> tokens = decompounder.decompose(term, 0, term.length());
        System.out.println(tokens);
    }

    @Test
    void decomposeSingle() {
        WordDecompounder decompounder = new WordDecompounder(3, 3, 20, false, dictionary);

        CharTermAttribute term = new CharTermAttributeImpl();
        term.append("roodbaarsfilet");
        List<CompoundToken> tokens = decompounder.decompose(term, 0, term.length());
        Assertions.assertEquals(toTokenText(tokens), List.of("rood", "roodbaars", "roodbaarsfilet", "fil", "filet", "let"));
    }

    private List<CharSequence> toTokenText(List<CompoundToken> tokens) {
        return tokens.stream().map(it -> it.txt).collect(Collectors.toList());
    }

    @Test
    void decomposeListLongest() {
        WordDecompounder decompounder = new WordDecompounder(3, 3, 20, true, dictionary);
        decomposeList(decompounder);
    }

    @Test
    void decomposeList() {
        WordDecompounder decompounder = new WordDecompounder(3, 3, 20, false, dictionary);
        decomposeList(decompounder);
    }

    private void decomposeList(WordDecompounder decomposer) {
        terms.subList(0, 50).stream()
                .map(it ->
                        {
                            CharTermAttributeImpl term = new CharTermAttributeImpl();
                            term.append(it);
                            return term;
                        }
                )
                .forEach(it ->
                        {
                            System.out.print(it + "\t -> \t");
                            System.out.println(decomposer.decompose(it, 0, it.length()));
                        }
                );
    }
}