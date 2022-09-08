package de.kaufland.moviesearch.indexservice.searchservice.model.elasticsearch;

public enum ElasticsearchFields {
    ID("id"),
    TITLE("title"),
    YEAR("year"),
    RUNTIME("runtime"),
    GENRES("genres"),
    DIRECTOR("director"),
    ACTORS("actors"),
    PLOT("plot"),
    POSTER_URL("posterUrl");

    private String fieldName;

    ElasticsearchFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

}
