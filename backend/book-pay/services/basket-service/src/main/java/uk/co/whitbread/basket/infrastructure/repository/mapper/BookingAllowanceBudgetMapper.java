package uk.co.whitbread.basket.infrastructure.repository.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public class BookingAllowanceBudgetMapper {

  @Named("toDomainModelBudget")
  public String toModel(String budget) {
    return budget != null && !budget.equalsIgnoreCase("null") ? budget : null;
  }

}
