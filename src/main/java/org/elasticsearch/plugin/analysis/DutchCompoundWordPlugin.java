package org.elasticsearch.plugin.analysis;

import java.util.HashMap;
import java.util.Map;
import org.elasticsearch.index.analysis.TokenFilterFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugin.analysis.decompounder.dictionary.DictionaryDecompounderTokenFilterFactory;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

public class DutchCompoundWordPlugin extends Plugin implements AnalysisPlugin {

    public DutchCompoundWordPlugin() {
        super();
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
        Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> filters = new HashMap<>();
        filters.put("dutch_dictionary_decompounder", DictionaryDecompounderTokenFilterFactory::new);
        return filters;
    }
}
