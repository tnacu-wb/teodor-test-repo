package uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper;

import java.util.concurrent.atomic.AtomicReference;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackageCodeType;
import uk.co.whitbread.ohip.domain.model.packages.out.Meal;

@Mapper(componentModel = "spring")
public abstract class MealResponseOhipMapper {

  @Mapping(source = "code", target = "id")
  @Mapping(source = "header.primaryDetails.description", target = "title")
  @Mapping(source = "header.postingAttributes.calculatedPrice", target = "price")
  @Mapping(expression = "java(getCurrency(packageCodeHeaderType, packagesCurrency))", target = "currency")
  public abstract Meal toModel(PackageCodeType packageCodeHeaderType,
      @Context AtomicReference<String> packagesCurrency);

  protected String getCurrency(PackageCodeType packageCodeHeaderType, AtomicReference<String> packagesCurrency) {
    String currency = packageCodeHeaderType.getHeader().getTransactionDetails().getCurrency();
    if (currency != null) {
      packagesCurrency.set(currency);
    }
    if (packageCodeHeaderType.getCode().equals("BBIB")) {
      packagesCurrency.set("EUR");
    }
    return packagesCurrency.get();
  }
}
