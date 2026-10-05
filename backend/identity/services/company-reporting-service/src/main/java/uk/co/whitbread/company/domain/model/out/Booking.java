package uk.co.whitbread.company.domain.model.out;

public record Booking(

    String bookingReference,
    String bookingStatus,
    Price totalCost,
    Price roomCost,
    Price upsellTotalCost,
    Integer rooms,
    Integer nights,
    Integer noOfAdults,
    Integer noOfChildren,
    String bookingDate,
    String arrivalDate,
    String departureDate,
    Integer leadTime,
    String customerReference,
    String purchaseOrder,
    String rateCategory,
    Booker booker,
    String cardType,
    String cardNumber,
    String hotelName,
    Booker guest,
    EmployeeAnswers employeeAnswers) {

}
