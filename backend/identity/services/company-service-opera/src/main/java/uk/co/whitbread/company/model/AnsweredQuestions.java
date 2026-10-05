package uk.co.whitbread.company.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class AnsweredQuestions {
    private ManagementInformationQuestionAnswered purchaseOrderManagement;
    private ManagementInformationQuestionAnswered customerReferenceManagement;
    private List<UserDefinedQuestion> userDefinedQuestions;

}
