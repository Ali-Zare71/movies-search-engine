package de.kaufland.moviesearch.indexservice.searchservice.model.dto;


import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResultsDto<t> {
    private List<t> results = new ArrayList<>();
    private long total = 0;
    private long time = 0;
}
