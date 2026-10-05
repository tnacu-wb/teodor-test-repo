package uk.co.whitbread.shared.cdh;

import static uk.co.whitbread.shared.cdh.properties.UriPaths.FETCH_APPLICATIONS_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.FETCH_APPLICATION_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.START_APPLICATION_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.UPDATE_APPLICATION_CARD_HOLDERS_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.UPDATE_APPLICATION_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriPaths.UPDATE_APPLICATION_STATUS_ENDPOINT;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.APPLICATION_GUID;
import static uk.co.whitbread.shared.cdh.properties.UriQueryParams.APPLICATION_ID;
import static uk.co.whitbread.shared.cdh.utils.RequestUtils.buildHeaders;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.co.whitbread.shared.cdh.model.StartApplicationRequest;
import uk.co.whitbread.shared.cdh.model.StartApplicationResponse;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationRequest;
import uk.co.whitbread.shared.cdh.model.UpdateApplicationResponse;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusRequest;
import uk.co.whitbread.shared.cdh.model.applications.UpdateApplicationStatusResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.ApplicationResponse;
import uk.co.whitbread.shared.cdh.model.spending.application.UpdateApplicationCardHoldersRequest;
import uk.co.whitbread.shared.cdh.properties.CdhApiOauthProperties;
import uk.co.whitbread.shared.cdh.properties.CdhApiProperties;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicationDataService {

    private final CustomerDataHubClient cdhClient;
    private final CdhApiProperties cdhApiProperties;
    private final CdhApiOauthProperties cdhApiOauthProperties;

    /**
     * Start application
     *
     * @param request       details of application
     * @param accessedBy    information about who is making the request
     * @param accessContext information about who is making the request
     * @return status of application
     */
    public StartApplicationResponse startApplication(StartApplicationRequest request,
            String accessedBy, String accessContext) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                cdhApiProperties.getHost() + START_APPLICATION_ENDPOINT);

        return cdhClient.postCDH(
                builder.build().toUriString(),
                request,
                buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
                StartApplicationRequest.class,
                StartApplicationResponse.class);
    }

    /**
     * Update application
     *
     * @param request       details of application
     * @param accessedBy    information about who is making the request
     * @param accessContext information about who is making the request
     * @return status of application
     */
    public UpdateApplicationResponse updateApplication(UpdateApplicationRequest request,
            String accessedBy, String accessContext) {

        final UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(cdhApiProperties.getHost() + UPDATE_APPLICATION_ENDPOINT);

        return cdhClient.postCDH(
                builder.build().toUriString(),
                request,
                buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
                UpdateApplicationRequest.class,
                UpdateApplicationResponse.class);
    }

    /**
     * Fetch applications for user.
     *
     * @param companyId     ID of the company
     * @param participantId ID of the participant
     * @param accessedBy    information about who is making the request
     * @param accessContext information about the context of the request
     *
     * @return list of applications for specific user
     */
    public List<ApplicationResponse> fetchApplicationsByUser(String companyId, String participantId,
                                                             String accessedBy, String accessContext) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
            cdhApiProperties.getHost() + FETCH_APPLICATIONS_ENDPOINT);

        return cdhClient.getListCDH(
            builder.buildAndExpand(companyId, participantId).toUriString(),
            buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
            ApplicationResponse.class
        );
    }

    /**
     * Fetch application.
     *
     * @param applicationId     ID of the application
     * @param applicationGuid   GUID of the application
     * @param accessedBy        information about who is making the request
     * @param accessContext     information about the context of the request
     *
     * @return application details, participants and card holders for the provided application IDs
     */
    public List<ApplicationResponse> fetchApplication(String applicationId,
        String applicationGuid, String accessedBy, String accessContext) {

        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
            cdhApiProperties.getHost() + FETCH_APPLICATION_ENDPOINT);
        builder.queryParam(APPLICATION_ID, applicationId);
        builder.queryParam(APPLICATION_GUID, applicationGuid);

        return cdhClient.getListCDH(
            builder.toUriString(),
            buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
            ApplicationResponse.class
        );
    }

    /**
     * Update application status
     *
     * @param request       details of application
     * @param accessedBy    information about who is making the request
     * @param accessContext information about who is making the request
     * @return action status and message
     */
    public UpdateApplicationStatusResponse updateApplicationStatus(UpdateApplicationStatusRequest request,
                                                                   String accessedBy, String accessContext) {

        final UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(cdhApiProperties.getHost() + UPDATE_APPLICATION_STATUS_ENDPOINT);

        return cdhClient.postCDH(
            builder.build().toUriString(),
            request,
            buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
            UpdateApplicationStatusRequest.class,
            UpdateApplicationStatusResponse.class);
    }

    /**
     * Update application cardHolders
     *
     * @param request       details of application
     * @param accessedBy    information about who is making the request
     * @param accessContext information about the context of the request
     * @return Void
     */
    public Void updateApplicationCardHolders(
        UpdateApplicationCardHoldersRequest request,
        String accessedBy, String accessContext) {

        final UriComponentsBuilder builder = UriComponentsBuilder
            .fromUriString(cdhApiProperties.getHost() + UPDATE_APPLICATION_CARD_HOLDERS_ENDPOINT);

        return cdhClient.postCDH(
            builder.build().toUriString(),
            request,
            buildHeaders(cdhApiOauthProperties.getSubscriptionKey(), accessedBy, accessContext),
            UpdateApplicationCardHoldersRequest.class,
            Void.class);
    }

}
