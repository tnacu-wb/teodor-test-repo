package uk.co.whitbread.feedback.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {

    private String whb_wfsource;

    @NotBlank
    private String title;

    private String whb_wfbookingrefifapplicable;

    @NotBlank
    private String whb_wfcontactnumber;

    private String whb_wfdateofstayifapplicable;

    @NotBlank
    private String whb_wfemailaddress;

    @NotBlank
    private String whb_wfreasonforfeedback;

    @NotBlank
    private String whb_summary;

    @NotNull
    private Integer whb_wffeedbacktype;

    @NotBlank
    private String whb_wffirstname;

    @NotBlank
    private String whb_wflastname;

    @NotBlank
    private String whb_wfpostcode;

    private String whb_wfhotelname;

    @NotNull
    private Boolean whb_wfididntbookthroughpremierinncom;

    @NotNull
    private Integer whb_contacttype;

    @NotNull
    private Integer whb_reasonforcontact;

    private String whb_wftypeofvisit;

    private String whb_loyaltycardnumber;

    private String whb_wfchecknumber;

    private String whb_dateofvisit;
    
    private Boolean whb_wfsleep;
    
    private Boolean whb_wfreported;

}
