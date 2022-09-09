package de.kaufland.moviesearch.indexservice.searchservice.model.dto;


import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResultDto<t> {
    @ApiModelProperty(notes = "The models of result found")
    private List<t> results = new ArrayList<>();
    @ApiModelProperty(notes = "The total number of the result found")
    private long total = 0;
    @ApiModelProperty(notes = "The time of the result found (millisecond)")
    private long time = 0;
}
