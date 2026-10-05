package uk.co.whitbread.employee.bulk.model;

import lombok.Data;

import java.util.List;

@Data
public class CountriesResponse {

    private List<Country> countries;
}

