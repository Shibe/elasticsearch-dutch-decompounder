# Dutch compound word token filter
### Compatible versions
Only `7.17.7`

### Adding the plugin
Generate a zip containing the plugin:
`mvn clean install`

Install the plugin: `./bin/elasticsearch-plugin install file://PATH_TO_ZIP`

### Removing the plugin
`./bin/elasticsearch-plugin remove compound-word-token-filter-plugin`

### Debugging the plugin
Start ElasticSearch with: `ES_JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 ./bin/elasticsearch`

Create a new remote debugging configuration that attaches to a remote JVM.

### ElasticSearch example

```json
GET _analyze
{
  "tokenizer": "whitespace",
  "filter": [
    {
      "type": "dutch_dictionary_decompounder",
      "word_list": ["zalm", "alm", "file", "filet"],
      "only_longest_match": true
    }
  ],
  "text": ["zalmfilet"]
}
```

Outputs
```json
{
  "tokens" : [
    {
      "token" : "zalm",
      "start_offset" : 0,
      "end_offset" : 4,
      "type" : "word",
      "position" : 0
    },
    {
      "token" : "filet",
      "start_offset" : 4,
      "end_offset" : 9,
      "type" : "word",
      "position" : 1
    }
  ]
}
```