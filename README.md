# Dutch compound word token filter

### Compatible versions

See [releases](https://github.com/Shibe/elasticsearch-dutch-decompounder/releases)

### Adding the plugin

Generate a zip containing the plugin:
`mvn clean install`

Install the plugin: `./bin/elasticsearch-plugin install file://PATH_TO_ZIP`

### Removing the plugin

`./bin/elasticsearch-plugin remove compound-word-token-filter-plugin`

### Debugging the plugin

Start ElasticSearch
with: `ES_JAVA_OPTS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 ./bin/elasticsearch`

Create a new remote debugging configuration that attaches to a remote JVM.

### ElasticSearch example

```json
{
  "tokenizer": "whitespace",
  "filter": [
    {
      "type": "dutch_dictionary_decompounder",
      "word_list": [
        "zalm",
        "alm",
        "file",
        "filet"
      ]
    }
  ],
  "text": [
    "zalmfilet"
  ]
}
```

Outputs

```json
{
  "tokens": [
    {
      "token": "zalm",
      "start_offset": 0,
      "end_offset": 4,
      "type": "word",
      "position": 0
    },
    {
      "token": "filet",
      "start_offset": 4,
      "end_offset": 9,
      "type": "word",
      "position": 1
    }
  ]
}
```

#### Configurable parameters

> word_list
>> A list of words to find in the provided tokens. 
>> Either word_list or word_list_path is required.
```json5
{
  "type": "dutch_dictionary_decompounder",
  "word_list": [
    "zalm",
    "alm",
    "file",
    "filet"
  ]
}
```

> word_list_path
>> Path to a file containing list of words to find in the provided tokens.
>> The path must be absolute or relative to the config location of ElasticSearch.
>> Either word_list or word_list is required.
```json5
{
  "type": "dutch_dictionary_decompounder",
  "word_list_path": "dictionary.txt"
}
```
