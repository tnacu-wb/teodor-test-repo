package uk.co.whitbread.hotel.payment.model;


import lombok.Data;

@Data
public class BusinessAccount{

    private String purchaseOrder;
    private String customerReference;
    private Boolean cardNotPresentAuth;
    private String atosUsername;
    private String atosPassword;
    private Integer breakfastCode;
    private Price dinnerAllowance;
    private Boolean alcoholAllowed;
    private Boolean carParkingAllowed;
    private Boolean wifiAccessAllowed;
    private Boolean otherChargesAllowed;

}
