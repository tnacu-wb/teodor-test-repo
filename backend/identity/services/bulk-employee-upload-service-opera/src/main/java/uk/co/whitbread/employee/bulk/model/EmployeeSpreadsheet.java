package uk.co.whitbread.employee.bulk.model;

import com.poiji.annotation.ExcelCell;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class EmployeeSpreadsheet {

    protected String id;

    @NotEmpty
    @ExcelCell(0)
    protected String title;

    @NotEmpty
    @ExcelCell(1)
    protected String firstName;

    @NotEmpty
    @ExcelCell(2)
    protected String lastName;

    @ExcelCell(3)
    protected String textConfirmation;

    @ExcelCell(4)
    private String centralCardId;

    @NotEmpty
    @ExcelCell(5)
    protected String accessLevel;

    @ExcelCell(6)
    private String addressLine1;

    @ExcelCell(7)
    private String addressLine2;

    @ExcelCell(8)
    private String addressLine3;

    @ExcelCell(9)
    private String addressLine4;

    @ExcelCell(10)
    private String addressLine5;

    @ExcelCell(11)
    private String postcode;

    @ExcelCell(12)
    private String country;

    @NotEmpty
    @ExcelCell(13)
    protected String emailAddress;

    @ExcelCell(14)
    @NotEmpty
    private String phoneNumber;

    @ExcelCell(15)
    private String mobileNumber;
}
