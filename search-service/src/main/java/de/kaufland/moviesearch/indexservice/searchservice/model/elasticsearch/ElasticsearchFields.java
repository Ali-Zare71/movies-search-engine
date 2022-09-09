package de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch;

public enum ElasticsearchFields {
    ID("id"),
    TITLE("title"),
    YEAR("year"),
    RUNTIME("runtime"),
    GENRES("genres"),
    DIRECTOR("director"),
    ACTORS("actors"),
    PARSED_ACTORS("parsedActors"),
    PLOT("plot"),
    POSTER_URL("posterUrl"),
    TITLE_ACTORS_SEARCH("titleActorsSearch");

    private String fieldName;

    ElasticsearchFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
