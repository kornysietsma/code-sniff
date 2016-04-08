# code-sniff

A set of clojure tools for combining data from several kinds of code quality tools,
and munging it together into a format useable by visualisation tools such as d3

## Usage

FIXME

## Philosophy

In the interest of "libraries not frameworks" I wanted to be able to get code metrics from lots of sources.  There are a million little tools out there, and rather than trying to build an all encompassing framework, I'd prefer to have an ecosystem of tools that can be built and run independantly
 
 As such, I'll try to focus on data formats - if we can move data via files or unix-style pipes, then other people can write their own adaptors for new languages and new metrics that I haven't thought of.
 
### Initial tools that I'd like to support

* cloc for simple counts of lines of code
* code-maat - code-maat has some great ways of viewing vcs logs and gathering powerful metrics
* language specific tools:
* * jscomplexity or eslint
* * checkstyle for Java

### outputs

Initially I'm going to create flare format JSON files to play nicely with d3 visualisations.  I might want to extend these though as there are several metrics that track correlation between files - coupling, similarity, etc. - and Flare has no easy way to identify/show links between leaf nodes in the data tree.

### Things to cover later

* file-system crawling, in case there are cases where we want all the files not just those spotted by cloc or similar
* duplication checks - simian? Or something.
* possibly where there isn't a good tool for a language, write some simple parsers?  For example, class size and method size are good quick metrics that don't need a good language parser in many cases

## Data formats

Intermediate data is a very simple JSON format for now:
`[{"filename":"foo/bar/baz.clj", "data": {"keyword":"value","baz":"bat"}}
 {"filename":"and/so/on.clj","data": {}}]`

I'll leave namespacing up to the tools, but it makes sense for most of these to nest something under "data" to avoid collisions when information is merged.  So the `cloc` parser produces records more like:

  "filename" : "test/code_sniff/t_schemas.clj",
  "data" : {
    "cloc" : {
      "blank" : 8,
      "comment" : 1,
      "code" : 24,
      "language" : "Clojure"
    }
  }

Merged output is based on the d3 "flare" files, though as "size" is a nebulous concept I'm not worring about that yet.

A typical output would be something like:

  {"name":"flare"  ; flare needs a root, which is annoying, so I insert one
   "children": [
      {"name": "foo"
       "children": [
         {"name": "bar.clj"
          "data": { "cloc": { "code" : 24, "language": "Clojure" } } } ] } ] }
          
Watch this space for more!

 


Exernal tool (e.g. cloc, code-maat) -> custom data format -> code-sniff converter -> generic format -> code-sniff combiner -> flare data.



## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.

## TODO

- handle windows path separators?