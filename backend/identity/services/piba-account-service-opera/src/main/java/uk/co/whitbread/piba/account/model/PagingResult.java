package uk.co.whitbread.piba.account.model;

import lombok.Data;

@Data
public class PagingResult {
    protected int fromRecord;
    protected int toRecord;
    protected int totalRecordCount;
    protected int lastPage;
}
