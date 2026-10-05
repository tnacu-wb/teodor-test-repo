package uk.co.whitbread.feedback.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.co.whitbread.feedback.client.AuthClient;
import uk.co.whitbread.feedback.client.CRMClient;
import uk.co.whitbread.feedback.model.Feedback;
import uk.co.whitbread.feedback.model.FeedbackResponse;
import uk.co.whitbread.feedback.properties.ConfigProperties;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final AuthClient authClient;
    private final CRMClient crmClient;
    private final ConfigProperties configProperties;

    public FeedbackResponse addFeedback(Feedback feedback) {

        long start = System.currentTimeMillis();

        if(isBlank(feedback.getWhb_wfsource())) {

            String defaultSource = configProperties.getDefaultFeedbackSource();
            log.debug("Setting default source {} for feedback {}", defaultSource, feedback);
            feedback.setWhb_wfsource(defaultSource);
        }

        String token = authClient.getAuthorisationCode();
        long authTime = System.currentTimeMillis();
        log.debug("Token created: {}", token);

        String incidentGuid = crmClient.postFeedback(token, feedback);
        long incidentCreation = System.currentTimeMillis();
        log.debug("Incident created: {}", incidentGuid);

        String caseId = crmClient.retrieveCaseId(token, incidentGuid);
        long caseIdRetrival = System.currentTimeMillis();

        if (configProperties.getLogRequestTime()) {
            log.info("[FEEDBACK-RESPONSE-TIME] Total: {} - Token creation takes {}ms, feedback creation {}ms and case id retrieval {}ms for {}",
                    (caseIdRetrival - start),
                    (authTime - start),
                    (incidentCreation - authTime),
                    (caseIdRetrival - incidentCreation),
                    caseId);
        }

        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setCaseReferenceId(caseId);

        log.info("Feedback created with CaseId {}", caseId);

        return feedbackResponse;
    }
}