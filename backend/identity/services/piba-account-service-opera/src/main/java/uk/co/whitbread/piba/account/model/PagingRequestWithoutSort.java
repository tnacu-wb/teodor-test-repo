package uk.co.whitbread.piba.account.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagingRequestWithoutSort {
    @Valid
    @Min(value = 1, message = "Page number cannot be 0 or negative")
    protected int page;
    @Valid
    @Min(value = 1, message = "Number of rows cannot be 0 or negative")
    protected int maximumDisplayRows;
}
