package uk.co.whitbread.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessQuestions {
    private ManagementInformationQuestion purchaseOrderManagement;
    private ManagementInformationQuestion customerReferenceManagement;
}
