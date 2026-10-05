package uk.co.whitbread.hotelcountries.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
public class CountriesResponse implements Serializable {

    private static final long serialVersionUID = -3155368630743003706L;

    private final List<Country> countries;

}
