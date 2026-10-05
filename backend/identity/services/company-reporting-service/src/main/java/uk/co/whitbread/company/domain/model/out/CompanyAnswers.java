package uk.co.whitbread.company.domain.model.out;

public record CompanyAnswers(

    String questionId,
    Integer position,
    String label,
    String header,
    String answer) {
}
