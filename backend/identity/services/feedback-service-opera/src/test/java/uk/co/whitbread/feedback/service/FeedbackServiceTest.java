package uk.co.whitbread.feedback.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import uk.co.whitbread.feedback.client.AuthClient;
import uk.co.whitbread.feedback.client.CRMClient;
import uk.co.whitbread.feedback.model.Feedback;
import uk.co.whitbread.feedback.model.FeedbackResponse;
import uk.co.whitbread.feedback.properties.ConfigProperties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FeedbackServiceTest {

    public static final String AUTORISATION_CODE = "autorisation-code-test";
    public static final String INCIDENT_GUI_ID = "incident-gui-id-test";
    public static final String CASE_ID = "case-id-test";
    public static final String SOURCE = "source-test";
    public static final String DEFAULT_SOURCE = "default-source-test";
    @Mock private AuthClient authClientMock;
    @Mock private CRMClient crmClientMock;
    @Mock private ConfigProperties configPropertiesMock;
    @Mock private Feedback feedbackMock;

    @InjectMocks
    @Spy private FeedbackService testObj;

    @BeforeEach
    public void setUp() {

        when(authClientMock.getAuthorisationCode()).thenReturn(AUTORISATION_CODE);
        when(crmClientMock.postFeedback(AUTORISATION_CODE, feedbackMock)).thenReturn(INCIDENT_GUI_ID);
        when(crmClientMock.retrieveCaseId(AUTORISATION_CODE, INCIDENT_GUI_ID)).thenReturn(CASE_ID);
        when(feedbackMock.getWhb_wfsource()).thenReturn(SOURCE);
        when(configPropertiesMock.getDefaultFeedbackSource()).thenReturn(DEFAULT_SOURCE);
    }

    @Test
    public void shouldAddFeedBack() {

        FeedbackResponse result = testObj.addFeedback(feedbackMock);
        assertNotNull(result);
        assertEquals(CASE_ID, result.getCaseReferenceId());

        verify(feedbackMock, never()).setWhb_wfsource(anyString());
    }

    @Test
    public void shouldAddFeedBackWithDefaultSource() {

        when(feedbackMock.getWhb_wfsource()).thenReturn(null);

        FeedbackResponse result = testObj.addFeedback(feedbackMock);
        assertNotNull(result);
        assertEquals(CASE_ID, result.getCaseReferenceId());

        verify(feedbackMock).setWhb_wfsource(DEFAULT_SOURCE);
    }
    
    @Test
    public void shouldAddFeedBackWithSleepReported() {
        
        FeedbackResponse result = testObj.addFeedback(feedbackMock);
        assertNotNull(result);
        assertEquals(CASE_ID, result.getCaseReferenceId());

    }

}