package uk.co.whitbread.basket.infrastructure.repository.mapper;

import java.util.List;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import uk.co.whitbread.basket.domain.model.basket.in.AddBasketItemType;
import uk.co.whitbread.basket.domain.model.basket.out.Basket;
import uk.co.whitbread.basket.domain.model.basket.out.BasketError;
import uk.co.whitbread.basket.domain.model.basket.out.BasketItem;
import uk.co.whitbread.basket.domain.model.basket.out.BasketStatus;
import uk.co.whitbread.basket.domain.model.basket.out.BookingAllowance;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketErrorEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketItemEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketItemTypeEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BasketStatusEntity;
import uk.co.whitbread.basket.infrastructure.repository.model.BookingAllowanceEntity;



@Mapper(componentModel = "spring", uses = {BasketEntityItemTypesMapper.class,
    BookingAllowanceBudgetMapper.class}, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface BasketEntityMapper {

  @Mapping(source = "itemTypes", target = "itemTypes", qualifiedByName = "toDomainModelItemTypes")
  Basket toDomainModel(BasketEntity basketEntity);

  @Mapping(source = "itemTypes", target = "itemTypes", qualifiedByName = "toEntityModelItemTypes")
  BasketEntity toEntityModel(Basket basket);

  BasketErrorEntity toEntityModel(BasketError basketError);

  BasketItemEntity toEntityModel(BasketItem basketItem);

  BasketItemTypeEntity toEntityModel(AddBasketItemType basketItemEntity);

  List<BasketItemEntity> toBasketItemEntityModel(List<BasketItem> basketItems);

  List<BasketItemTypeEntity> toBasketItemTypeEntityModel(List<AddBasketItemType> basketItemEntity);

  BasketStatus toBasketStatusModel(BasketStatusEntity basketStatusEntity);

  @Mapping(source = "budget", target = "budget", qualifiedByName = "toDomainModelBudget")
  BookingAllowance toBookingAllowanceModel(BookingAllowanceEntity bookingAllowanceEntity);
}