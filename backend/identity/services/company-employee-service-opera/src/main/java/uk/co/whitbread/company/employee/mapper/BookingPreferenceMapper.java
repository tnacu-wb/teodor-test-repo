package uk.co.whitbread.company.employee.mapper;

import static uk.co.whitbread.company.employee.utils.EnumConverter.getEnum;

import java.math.BigDecimal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import uk.co.whitbread.company.employee.model.BookingPreference;
import uk.co.whitbread.company.employee.model.RoomType;
import uk.co.whitbread.shared.cdh.model.BusinessBookingPreference;

@Mapper(componentModel = "spring")
public interface BookingPreferenceMapper {

    @Mapping(target = "adults", source = "roomRequirements.adults")
    @Mapping(target = "children", source = "roomRequirements.children")
    @Mapping(target = "cotRequired", source = "roomRequirements.cotRequired")
    @Mapping(target = "roomType", qualifiedByName = "toRoomType", source = "roomRequirements.type")
    @Mapping(target = "electronicInvoice", source = "electronicInvoiceRequired")
    BookingPreference toBookingPreference(BusinessBookingPreference businessBookingPreference);

    @Mapping(target = "roomRequirements.adults", source = "adults")
    @Mapping(target = "roomRequirements.children", source = "children")
    @Mapping(target = "roomRequirements.cotRequired", source = "cotRequired")
    @Mapping(target = "roomRequirements.type", source = "roomType")
    @Mapping(target = "electronicInvoiceRequired", source = "electronicInvoice")
    BusinessBookingPreference toBookingPreference(BookingPreference bookingPreference);

    @Named("toRoomType")
    default RoomType toRoomTypeEnum(String roomType) {
        return getEnum(RoomType.class, roomType);
    }

    @Named("toNumberOfInfants")
    default BigDecimal toNumberOfInfants(Boolean cotRequired) {
        return (cotRequired != null && cotRequired) ? BigDecimal.ONE : BigDecimal.ZERO;
    }

    @Named("toCotRequired")
    default Boolean toCotRequired(BigDecimal numberOfInfants) {
        return (numberOfInfants != null && numberOfInfants.compareTo(BigDecimal.ZERO) > 0) ? true : false;
    }
}
