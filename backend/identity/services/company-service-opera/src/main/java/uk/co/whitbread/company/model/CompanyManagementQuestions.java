package uk.co.whitbread.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyManagementQuestions {
    private ManagementInformationQuestion purchaseOrderManagement;
    private ManagementInformationQuestion customerReferenceManagement;
    private List<ManagementInformationQuestion> userDefinedManagement;
}
