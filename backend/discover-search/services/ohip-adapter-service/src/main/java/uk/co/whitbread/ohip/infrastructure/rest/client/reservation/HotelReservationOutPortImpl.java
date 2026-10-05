package uk.co.whitbread.ohip.infrastructure.rest.client.reservation;

import static java.util.Collections.singletonList;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType.COMPANY;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType.GUEST;
import static uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType.RESERVATIONCONTACT;
import static uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption.ACCOUNT_COMPANY;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.BUSINESS_BOOKER_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CNP_ALERT_CODE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.CNP_ALERT_DESCRIPTION_EN;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.DISTRIBUTION_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EMPTY_STR;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REF_MIGRATION_ID_CONTEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FALSE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FOLIO_WINDOW_2;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.FOLIO_WINDOW_3;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LANGUAGE_UNDEFINED;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.LIMIT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.PREPAID_CHANNEL;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RESERVATION_IDS_PARAM;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_08;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_16;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_PURCHASE_ORDER_NAME;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFN_01;
import static uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.ProfileUtils.needToUpdateOriginalProfile;

import com.nimbusds.jose.util.Pair;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import uk.co.whitbread.commons.exceptions.utils.ExceptionLogger;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertAreaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AttachmentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CompanyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ConfigPostingAttributesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DateRangeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteria;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DiscountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ExternalReferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ItemInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.NumericUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PayeeInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PaymentCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInDetailsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResAttachedProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResCancellationPolicyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResDepositPolicyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestAdditionalInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResInventoryItemType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResInventoryItemsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationArrivalInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationId;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPoliciesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPreCheckInDetailsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInfoTypeFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TimeSpanType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CountryNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerTypeIdentifications;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Status;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.StayFutureListType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.StayHistoryListType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.StayReservationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.med.FileToUpload;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageCodeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfo;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleDto;
import uk.co.whitbread.hotel.rules.agent.generated.models.VatRuleResponseDto;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.model.availability.in.ItemInventoryRequest;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.AccompanyingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.Alert;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItems;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswer;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CopyReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.DepositFolioRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.LinkReservationToLeisureCustomerRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelectionScheduled;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.QuestionAndAnswerTypeEnum;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.Reservation;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackages;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationProfiles;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomsSelections;
import uk.co.whitbread.ohip.domain.model.reservation.in.SpecialRequests;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateBookerEmailRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPolicyRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReasonForStayRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.BookingAllowance;
import uk.co.whitbread.ohip.domain.model.reservation.out.BookingAllowancesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelInformationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancelReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CancellationPoliciesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CopyReservationsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolio;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.PreCheckInResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAlerts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationAmounts;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationByBasketRefResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCityTaxInfo;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationDetailsEnhancedResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationEventPreference;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationIdDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.UpdateReasonForStayResponse;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitution;
import uk.co.whitbread.ohip.domain.model.rules.model.out.RoomSubstitutionRuleResponse;
import uk.co.whitbread.ohip.domain.ports.secondary.HotelReservationOutPort;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.AvailabilityRequestDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.ApiLimitsService;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip.OhipFrontDeskClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipPackagesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.DiscountInvalidAmountException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelReservationNotFound;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.AttachReservationProfileRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.BookerProfileOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.BusinessItemsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CancelReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CancelReservationResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ChangeReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CompanyProfileOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CompanyQuestionAndAnswerRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ConfirmReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ConfirmationResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CopyReservationsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.CopyReservationsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositFolioCriteriaMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositFoliosRequestMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositFoliosRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.DepositsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.GuestDetailsMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.LinkReservationToLeisureCustomerRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.MarketingPreferencesResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.MemosOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.MovePaymentDetailsOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PackagesRequestDtoOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PackagesSelectionMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PayOnArrivalReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PostedDepositsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.PriceBreakdownOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.RatePlanChangeRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationAccompanyingGuestProfileRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationAccompanyingGuestRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationAlertMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationBookerRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationCompanyRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationGuestRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationGuestResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationPackagesOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.ReservationResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.RoomRateOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.RoomTypeChangeRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.SearchBookingsResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.SpecialReqRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateBookerEmailOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateBookerReservationProfileOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateCancellationPolicyRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateCustomReferenceNumberOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateDiscountRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdatePreferencesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReasonForStayRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReasonForStayResponseOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationAttachedProfilesRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationCcAgentIdRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationOverrideReasonsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.mapper.UpdateReservationRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.FolioWindowsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.FoliosResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.ReservationFolioInformationDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.ReservationStatusOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.model.in.UniqueIdTypeEnumDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.PaymentUtils;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search.BookingSearchInputBuilder;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search.SearchBookingsInput;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.utils.search.SearchBookingsResultsFilterChain;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.RulesAgentClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.mapper.RoomSubstitutionResponseMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties.RulesAgentProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.ReservationPaymentMethodUtils;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@RequiredArgsConstructor
@Slf4j
public class HotelReservationOutPortImpl implements HotelReservationOutPort {

  public static final String CHRTY = "CHRTY";
  public static final String ZCHRY = "ZCHRY";
  public static final String ZR0705 = "ZR0705";
  private static final String ZCHR10 = "ZCHR10";
  private static final String ZCHR11 = "ZCHR11";
  private static final String ZCHR12 = "ZCHR12";
  private static final String ZCHR13 = "ZCHR13";
  public static final String HSATWN = "HSATWN";
  public static final String CITYTAX_ID = "CITYTAX";
  //max opera reservations limit is 200 but I had to reduce it to 100 to avoid out of memory exception
  public static final int MAX_OPERA_PAGE_LIMIT = 100;
  public static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
  public static final String TIME_FORMAT = "HH:mm:ss";
  public static final String OFFSET_FORMAT = "xxx";
  public static final int PURCHASE_ORDER_NUMBER_INDEX = 0;
  public static final int CUSTOMER_REFERENCE_INDEX = 1;
  public static final String RESERVATION = "Reservation";
  public static final String EXTERNAL_REFERENCE_ID_SEPARATOR = "-";
  public static final int SEARCH_RESULTS_LIMIT = 100;
  public static final String BUSINESS_NOTES = "BUSINESS NOTES";
  public static final String SOURCE_TYPE_PACKAGE = "PACKAGE";
  public static final String COMMENT = "Comment";
  public static final String ENGLISH = "en";
  public static final String ADDRESS_TYPE_BUSINESS = "BUSINESS";
  public static final String ADDTYPE_BILLING = "BILLING";
  private static final String RULES_PMS_CODE = "OP";
  private static final String ALLOWANCE_ALCOHOL = "alcohol";
  private static final String ALLOWANCE_DINNER = "dinner";
  private static final String COT_ITEM_NAME = "Cot";
  private static final String PROFILE = "Profile";
  private static final String ID_TYPE_PASSPORT = "PASSPORT";
  public static final String PACKAGE_CODE_ACCOMMODATION = "accommodation";
  private static final String DEPOSIT_RECEIVED_GUARANTEE = "DRV";
  private static final String SUCCESS = "Success";
  private static final String ERROR = "Error";
  private static final int MAX_FILE_SIZE_MB = 10;
  private static final int MAX_FILE_SIZE_BYTES = MAX_FILE_SIZE_MB * 1024 * 1024;
  public static final String RESERVATION_CHECKIN_ALERT_MSG_EN =
      "Do not print registration card, Pre-Check-In completed";
  public static final String RESERVATION_CHECKIN_ALERT_MSG_DE =
      "Kein Meldeschein drucken, Pre-Check-In vollständig";
  public static final String LANGUAGE_DE = "DE";
  public static final String PRE_CHECK_IN = "Pre-Check-In";
  public static final String REG_RES_PREFIX = "REG_RES";
  public static final String CONTROL_CHARACTER_REGEX = "\\p{Cntrl}";
  private static final int FETCH_RESERVATION_MAX_ATTEMPTS = 3;
  private final OhipReservationClient ohipReservationClient;
  private final OhipAvailabilityClient ohipAvailabilityClient;
  private final ReservationResponseOhipMapper reservationResponseOhipMapper;
  private final ReservationRequestOhipMapper reservationRequestOhipMapper;
  private final ConfirmReservationRequestOhipMapper confirmReservationRequestOhipMapper;
  private final RatePlanChangeRequestOhipMapper rateChangeRequestOhipMapper;
  private final RoomTypeChangeRequestOhipMapper roomTypeChangeRequestOhipMapper;
  private final ConfirmationResponseOhipMapper confirmationResponseOhipMapper;
  private final RoomRateOhipMapper roomRateOhipMapper;
  private final ReservationOhipProperties reservationOhipProperties;
  private final ReservationGuestRequestOhipMapper reservationGuestRequestOhipMapper;
  private final ReservationAccompanyingGuestRequestOhipMapper reservationAccompanyingGuestRequestOhipMapper;
  private final ReservationGuestResponseOhipMapper reservationGuestResponseOhipMapper;
  private final ReservationBookerRequestOhipMapper reservationBookerRequestOhipMapper;
  private final ReservationCompanyRequestOhipMapper reservationCompanyRequestOhipMapper;
  private final ReservationPackagesOhipMapper reservationPackagesOhipMapper;
  private final OhipPackagesClient ohipPackagesClient;
  private final PackagesRequestDtoOhipMapper packagesRequestDtoOhipMapper;
  private final DepositFoliosRequestOhipMapper depositFoliosRequestOhipMapper;
  private final PackageGroupsRequestOhipMapper packageGroupsRequestOhipMapper;
  private final PackagesSelectionMapper packagesSelectionMapper;
  private final CancelReservationRequestOhipMapper cancelReservationRequestOhipMapper;
  private final CancelReservationResponseOhipMapper cancelReservationResponseOhipMapper;
  private final PriceBreakdownOhipMapper priceBreakdownOhipMapper;
  private final UpdateDiscountRequestOhipMapper updateDiscountRequestOhipMapper;
  private final RulesAgentClient rulesAgentClient;
  private final DepositFoliosRequestMapper depositFoliosRequestMapper;
  private final DepositFolioCriteriaMapper depositFolioCriteriaMapper;
  private final SearchBookingsResponseOhipMapper searchBookingsResponseOhipMapper;
  private final UpdateReasonForStayRequestOhipMapper updateReasonForStayRequestOhipMapper;
  private final UpdateReasonForStayResponseOhipMapper updateReasonForStayResponseOhipMapper;
  private final BusinessItemsRequestOhipMapper businessItemsRequestOhipMapper;
  private final SpecialReqRequestOhipMapper specialReqRequestOhipMapper;
  private final UpdateReservationOverrideReasonsRequestOhipMapper
      updateReservationOverrideReasonsRequestOhipMapper;
  private final UpdateReservationCcAgentIdRequestOhipMapper
      updateReservationCcAgentIdRequestOhipMapper;
  private final DepositsResponseOhipMapper depositsResponseOhipMapper;
  private final PostedDepositsResponseOhipMapper postedDepositsResponseOhipMapper;
  private final OhipFrontDeskClient frontDeskClient;
  private final UpdateCancellationPolicyRequestOhipMapper updateCancellationPolicyRequestOhipMapper;
  private final MarketingPreferencesResponseOhipMapper marketingPreferencesResponseOhipMapper;
  private final CopyReservationsRequestOhipMapper copyReservationsRequestOhipMapper;
  private final CopyReservationsResponseOhipMapper copyReservationsResponseOhipMapper;
  private final RulesAgentProperties rulesAgentProperties;
  private final UpdateReservationRequestOhipMapper updateReservationRequestOhiMapper;
  private final PayOnArrivalReservationRequestOhipMapper payOnArrivalReservationRequestOhipMapper;
  private final MovePaymentDetailsOhipMapper movePaymentDetailsOhipMapper;
  private final BookerProfileOhipMapper bookerProfileOhipMapper;
  private final CompanyProfileOhipMapper companyProfileOhipMapper;
  private final UpdateReservationAttachedProfilesRequestOhipMapper updateReservationAttachedProfilesRequestOhipMapper;
  private final OhipProperties ohipProperties;
  private final UpdateBookerReservationProfileOhipMapper updateBookerReservationProfileOhipMapper;
  private final UpdateBookerEmailOhipMapper updateBookerEmailOhipMapper;
  private final CompanyQuestionAndAnswerRequestOhipMapper companyQuestionAndAnswerRequestOhipMapper;
  private final MemosOhipMapper memosOhipMapper;
  private final AttachReservationProfileRequestOhipMapper attachReservationProfileRequestOhipMapper;
  private final UpdateCustomReferenceNumberOhipMapper updateCustomReferenceNumberOhipMapper;
  private final UnleashWrapper<FeatureFlag> unleashWrapper;
  private final ConcurrentTracer concurrentTracer;
  private final ReservationAccompanyingGuestProfileRequestOhipMapper
      reservationAccompanyingGuestProfileRequestOhipMapper;
  private final LinkReservationToLeisureCustomerRequestOhipMapper
      linkReservationToLeisureCustomerRequestOhipMapper;
  private final UpdatePreferencesRequestOhipMapper updatePreferencesRequestOhipMapper;
  private final ChangeReservationRequestOhipMapper changeReservationRequestOhipMapper;
  private final RoomSubstitutionResponseMapper roomSubstitutionResponseMapper;
  private final GuestDetailsMapper guestDetailsMapper;
  private final ReservationAlertMapper reservationAlertMapper;
  private final ApiLimitsService apiLimitsService;

  @Override
  public ReservationResponse createReservation(ReservationRequest reservationRequest) {

    // Check if required number of cots are available
    log.debug("Entered createReservation for {} reservations",
        (reservationRequest.getReservations() == null ? "null"
            : reservationRequest.getReservations().size()));

    var hotelId = reservationRequest.getReservations().get(0).getHotelId();

    var bookingChannelInfo = rulesAgentClient.getBookingChannelInfo(
        reservationRequest.getBookingChannel());

    reservationRequest.getReservations()
        .forEach(r -> r.setSourceCode(bookingChannelInfo.getSourceId()));

    var totalCotsRequired = reservationRequest.getReservations().stream()
        .filter(r -> Boolean.TRUE.equals(r.getCotRequired())).count();

    Optional<ItemInventoryResponseDto> itemInventory = Optional.empty();

    if (totalCotsRequired > 0) {
      itemInventory = Optional.of(apiLimitsService.getItemInventoryResponses(
          ItemInventoryRequest.builder()
              .hotelId(hotelId)
              .startDate(reservationRequest.getReservations().get(0).getArrival())
              .endDate(reservationRequest.getReservations().get(0).getDeparture())
              .build()));
    }

    // Pass the cot item code from Opera to createReservation method
    // This is done here in order to avoid multiple calls to OHIP
    // TO CONSIDER: more trips to OHIP (i.e. "getItemsInventory") might result in more accurate results
    // although it might reduce performance
    final var cotCode = itemInventory.map(inv -> inv.getItemsInventory().stream()
            .filter(i -> i.getName().equals(COT_ITEM_NAME)
                && i.getInventories().stream().allMatch(a -> a.getAvailable() >= totalCotsRequired))
            .findFirst()
            .map(ItemInventoryDto::getCode)
            .orElseThrow(
                () -> {
                  var exception = new HotelReservationException(
                      ErrorCode.DIGITAL_NOT_ENOUGHT_COTS_EXCEPTION, "Not enough cots available");
                  ExceptionLogger.log(log, exception);
                  return exception;
                }))
        .orElse(null);

    final Set<String> reservationsWithCot = new HashSet<>();
    final Set<String> createdReservationIds = new HashSet<>();
    ReservationResponse reservationsResponse;

    if (unleashWrapper.isEnabled(
        unleashWrapper.featureFlag().getCreateReservationWithSoftBundles())) {
      log.info("Feature flag createReservationWithSoftBundles is enabled!");
      removePackageGroups(reservationRequest);
    }

    var reservationCreationFlux = Flux.fromIterable(reservationRequest.getReservations())
        .flatMap(i -> createReservation(i, cotCode, reservationsWithCot,
                reservationRequest.getBookingChannel(), reservationRequest.getReasonForStay())
                .map(ReservationStatusOhipDto::getUniqueIdReservation)
                .map(reservationResponse -> Pair.of(i, reservationResponse)),
            reservationOhipProperties.getMaxConcurrency())
        .flatMap(pair -> setFixedRate(pair.getLeft(), pair.getRight(), hotelId)
            .thenReturn(pair.getRight()))
        .doOnNext(createdReservationIds::add)
        .collect(toSet());
    // FIX ME: Temporary solution is to limit number of concurrent calls to 1. See "maxConcurrency" property
    if (reservationRequest.isGetReservationsByIds()) {
      var reservations = reservationCreationFlux
          .flatMapMany(i -> ohipReservationClient.getReservations(
              reservationRequest.getReservations().get(0).getHotelId(), i))
          .collectList()
          .block();

      reservationsResponse = reservationResponseOhipMapper.toReservationResponseModel(
          reservations);
      //FIX ME: Workaround since getReservation with 'InventoryItems' fetch instructions doesn't work
      reservationsResponse.getReservations().stream()
          .filter(r -> reservationsWithCot.contains(r.getReservationId()))
          .forEach(r -> r.getRoomStay().setCot(true));

    } else {
      var reservationIds = reservationCreationFlux
          .block();
      reservationsResponse = reservationResponseOhipMapper.toReservationResponseLightModel(
          reservationIds, hotelId);
    }

    return reservationsResponse;
  }

  private Mono<ReservationStatusOhipDto> createReservation(Reservation reservation,
      String cotCode,
      Set<String> reservationsWithCot,
      BookingChannel bookingChannel, String reasonForStay) {

    var createReservation = ohipReservationClient
        .sendCreateReservationRequest(reservation.getHotelId(),
            mapToCreateReservationOhip(reservation, bookingChannel, cotCode, reasonForStay));

    if (Boolean.TRUE.equals(reservation.getCotRequired())) {
      return createReservation.map(i -> {

        //FIX ME: Workaround since getReservation with 'InventoryItems' fetch instructions doesn't work
        // This should be mapped directly from the sendGetReservationsByIds response (using mapstruct)
        reservationsWithCot.add(i.getUniqueIdReservation());
        return i;
      });
    }

    return createReservation;
  }

  private void removePackageGroups(ReservationRequest reservationRequest) {
    Flux.fromIterable(reservationRequest.getReservations())
        .index()
        .flatMap(tuple -> {
          var res = tuple.getT2();
          var packagesSelection = Optional.ofNullable(res.getReservationPackages())
              .orElseGet(Collections::emptyList)
              .stream()
              .map(pkg -> new PackagesSelection(
                  pkg.getPackageCode(),
                  Optional.ofNullable(pkg.getConsumptionDetails())
                      .map(cd -> Optional.of(cd.getTotalQuantity()).orElse(0))
                      .orElse(0),
                  null)
              ).toList();

          List<PackagesSelection> newPackages = addPackagesFromGroup(packagesSelection, res.getHotelId());
          removePackageGroupsFromCreateReservationRequest(newPackages, res);
          return Mono.just(res);
        }, reservationOhipProperties.getMaxConcurrency())
        .collectList()
        .block();
  }

  private Mono<ChangeReservationDetails> setFixedRate(Reservation reservation, String reservationId, String hotelId) {
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getFixedRate())) {
      var changeReservationRequest = mapToChangeReservation(reservation, reservationId);

      return ohipReservationClient.sendChangeReservationRequest(hotelId, reservationId, changeReservationRequest);
    }
    return Mono.empty();
  }

  @Override
  public ReservationsDetailsResponse getReservationsByExternalReferenceIds(String hotelId,
      List<String> externalReferenceIds,
      int limit, int offset) {
    log.debug(
        "Entered getReservationsByExternalReferenceIds for hotelId={} and {} external reference ids",
        sanitizeInput(hotelId), externalReferenceIds == null ? "null" : externalReferenceIds.size());
    var reservationsDetailsOhip =
        ohipReservationClient.sendGetReservationsByExternalReferenceIdsRequest(
            hotelId, externalReferenceIds, limit, offset);
    return reservationResponseOhipMapper.toReservationsDetailsResponseModel(
        reservationsDetailsOhip);
  }

  @Override
  public List<ReservationsPaymentCardType> getReservationPaymentMethod(
      String hotelId,
      List<String> reservationIds) {

    if (reservationIds == null || reservationIds.isEmpty()) {
      return Collections.emptyList();
    }

    var reservations = Flux.fromIterable(reservationIds)
        .flatMap(reservationId -> ohipReservationClient.getReservationPaymentMethods(hotelId,
                reservationId)
            .onErrorResume(exp -> {
              log.error(
                  "Reservation payment method exception from Ohip for reservationId = {} : {}",
                  reservationId, exp.getMessage());
              return Mono.empty();
            }), 6)

        .collectList()
        .block();

    if (reservations != null && !reservations.isEmpty()) {
      return reservations.stream()
          .map(modelReservation ->
              reservationResponseOhipMapper.toPaymentCardReservationModel(
                  modelReservation.getReservations().getReservation().get(0).getReservationIdList(),
                  getReservationCardDetails(hotelId, List.of(modelReservation)),
                  modelReservation))
          .toList();
    }

    return Collections.emptyList();
  }

  @Override
  public ConfirmReservationResponse confirmReservation(
      ConfirmReservationRequest confirmReservationRequest) {
    final String reservationId = confirmReservationRequest.getReservationId();
    final String hotelId = confirmReservationRequest.getHotelId();
    log.debug("Entered confirmReservation for hotelId={} and reservationId={}",
        hotelId, reservationId);
    ConfirmReservationResponse response;
    var updatedReservationDetails = ohipReservationClient.getReservation(
            hotelId, reservationId).block();
    var initialGuaranteeCode = updatedReservationDetails.getReservations().getReservation()
            .get(0).getRoomStay().getGuarantee().getGuaranteeCode();
    var routingInstructions = updatedReservationDetails.getReservations().getReservation()
            .get(0).getRoutingInstructions();
    var folio = Optional.ofNullable(routingInstructions).flatMap(list -> list.stream().findFirst())
            .map(RoutingInfoType::getFolio).orElse(null);
    var folioWindowNo = Optional.ofNullable(folio).map(RoutingInfoTypeFolio::getFolioWindowNo).orElse(null);

    CompletableFuture<Void> updateDepositFolioWithNonDigitalPaymentMethodFuture = null;

    if (confirmReservationRequest.getPaymentOption().equals(PaymentOption.PAY_NOW)) {

      if (unleashWrapper.isEnabled(
          unleashWrapper.featureFlag().getDepositFolioPostAfterDisableOnHold())) {
        updateDepositFolioWithNonDigitalPaymentMethodFuture =
            saveDepositFoliosAndSetReservationNonDigitalPaymentMethod(
                confirmReservationRequest, reservationId, hotelId, updatedReservationDetails);
      } else {
        updateDepositFolioWithNonDigitalPaymentMethodFuture =
            saveDepositFoliosAndSetReservationNonDigitalPaymentMethodLegacy(
                confirmReservationRequest, reservationId, hotelId);
      }

      if (folioWindowNo != null && folioWindowNo == FOLIO_WINDOW_3) {
        BusinessItemsRequest businessItemsRequest = new BusinessItemsRequest();
        businessItemsRequest.setReservationIds(Collections.singleton(reservationId));
        businessItemsRequest.setHotelId(hotelId);
        businessItemsRequest.setChannel(PREPAID_CHANNEL);
        updateBusinessItems(businessItemsRequest);
      }

      response = confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          updatedReservationDetails.getReservations().getReservation().get(0));

    } else {
      var reservationsDetailsOhip =
          ohipReservationClient.sendChangeReservationRequest(
                  hotelId, reservationId, mapToChangeReservation(confirmReservationRequest))
              .block();
      Objects.requireNonNull(reservationsDetailsOhip);
      response = confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          reservationsDetailsOhip.getReservations());
    }
    if (isCnpReservation(hotelId, reservationId)) {
      movePaymentDetails(hotelId, Set.of(reservationId));
      if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getSetCnpBookingAlerts())) {
        setCnpReservationAlert(hotelId, reservationId);
      }
    }

    if (Objects.nonNull(routingInstructions)
            && paymentOptionUpdated(initialGuaranteeCode, confirmReservationRequest.getPaymentOption()))  {
      updateRoutingInstructionsWithPayeeInfo(hotelId, Set.of(reservationId));
    }

    updateCcAgentId(confirmReservationRequest.getCcAgentId(), reservationId, hotelId,
        updateDepositFolioWithNonDigitalPaymentMethodFuture);

    return response;
  }

  private void updateCcAgentId(String ccAgentId, String reservationId,
      String hotelId, CompletableFuture<Void> updateDepositFolioWithNonDigitalPaymentMethodFuture) {

    if (StringUtils.isNotBlank(ccAgentId)) {
      if (updateDepositFolioWithNonDigitalPaymentMethodFuture != null) {
        CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
          try {
            updateDepositFolioWithNonDigitalPaymentMethodFuture.get();
          } catch (InterruptedException | ExecutionException e) {
            // the scope is only to wait for the future's completion, no need to handle this here
          }
          updateReservationCcAgentId(UpdateReservationCcAgentIdRequest.builder().hotelId(hotelId)
              .reservationIds(Set.of(reservationId)).ccAgentId(ccAgentId)
              .build(), true);
        }));
      } else {
        updateReservationCcAgentId(UpdateReservationCcAgentIdRequest.builder().hotelId(hotelId)
            .reservationIds(Set.of(reservationId)).ccAgentId(ccAgentId)
            .build(), true);
      }
    }
  }

  private boolean paymentOptionUpdated(String initialGuaranteeCode, PaymentOption requestPaymentOption) {
    return reservationOhipProperties.getNonGuaranteeCode().equals(initialGuaranteeCode)
            && ACCOUNT_COMPANY.equals(requestPaymentOption);
  }

  private CompletableFuture<Void> saveDepositFoliosAndSetReservationNonDigitalPaymentMethodLegacy(
      ConfirmReservationRequest confirmReservationRequest, String reservationId, String hotelId) {

    var reservation = ohipReservationClient.getReservation(hotelId, reservationId)
        .block();

    if (Objects.isNull(reservation)) {
      return null;
    }

    var vatRulesDetails = rulesAgentClient.getVatCodes(
        getVatRegion(reservation), getPackagesCodesForVat(reservation));
    var totalReservationAmounts = getReservationAmounts(hotelId,
        Collections.singleton(reservationId), null);
    if (Objects.nonNull(totalReservationAmounts) && totalReservationAmounts.getTotalCostOfStay()
        .compareTo(totalReservationAmounts.getOutStandingCostOfStay()) == 0) {
      var depositRequest = depositFoliosRequestMapper.toDepositFolioRequestModel(
          confirmReservationRequest);
      setDigitalPaymentMethodForPayNow(confirmReservationRequest, hotelId, depositRequest);
      depositRequest.setTotalCostOfStay(totalReservationAmounts.getTotalCostOfStay());
      var depositFolioCriteria = mapToDepositCriteria(depositRequest, reservation,
          vatRulesDetails, getCityTaxDetailsPerDay(hotelId, reservationId, reservation), null);
      if (unleashWrapper.isEnabled(
          unleashWrapper.featureFlag().getDepositFolioCreationVerification())) {
        sendDepositFoliosWithVerification(hotelId, reservationId, depositFolioCriteria);
      } else {
        ohipReservationClient.sendDepositFoliosRequest(hotelId, reservationId, depositFolioCriteria);
      }

      return updateDepositFolioWithNonDigitalPaymentMethod(confirmReservationRequest, reservationId,
          hotelId);
    }
    return null;
  }

  private CompletableFuture<Void> saveDepositFoliosAndSetReservationNonDigitalPaymentMethod(
      ConfirmReservationRequest confirmReservationRequest, String reservationId, String hotelId,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {
    var vatRulesDetails = rulesAgentClient.getVatCodes(
        getVatRegion(reservation), getPackagesCodesForVat(reservation));
    var totalReservationAmounts = getReservationAmounts(hotelId,
        Collections.singleton(reservationId), null);
    if (Objects.nonNull(totalReservationAmounts) && totalReservationAmounts.getTotalCostOfStay()
        .compareTo(totalReservationAmounts.getOutStandingCostOfStay()) == 0) {
      var depositRequest = depositFoliosRequestMapper.toDepositFolioRequestModel(
          confirmReservationRequest);
      setDigitalPaymentMethodForPayNow(confirmReservationRequest, hotelId, depositRequest);
      depositRequest.setTotalCostOfStay(totalReservationAmounts.getTotalCostOfStay());

      ohipReservationClient.sendChangeReservationRequest(
              hotelId, reservationId, mapToChangeReservation(confirmReservationRequest))
          .block();

      var oldDepositPolicy = findFirstDepositPolicy(reservation).orElse(null);

      var updatedReservation = fetchReservationWithUpdatedDepositPolicy(hotelId,
          reservationId, oldDepositPolicy);

      if (Objects.isNull(updatedReservation)) {
        throw new HotelReservationException(
            ErrorCode.DIGITAL_FETCH_RESERVATION_DETAILS_EXCEPTION,
            "An error was returned by OHIP: Could not fetch reservation details with updated deposit policy"
                + " after change reservation request.");
      }

      var depositFolioCriteria = mapToDepositCriteria(depositRequest, updatedReservation,
          vatRulesDetails, getCityTaxDetailsPerDay(hotelId, reservationId, updatedReservation),
          null);
      if (unleashWrapper.isEnabled(
          unleashWrapper.featureFlag().getDepositFolioCreationVerification())) {
        sendDepositFoliosWithVerification(hotelId, reservationId, depositFolioCriteria);
      } else {
        ohipReservationClient.sendDepositFoliosRequest(hotelId, reservationId, depositFolioCriteria);
      }

      return updateDepositFolioWithNonDigitalPaymentMethod(confirmReservationRequest, reservationId,
          hotelId);
    }
    return null;
  }

  /**
   * Attempts to create a deposit folio via OPERA. If the initial request fails, checks whether
   * the folio was already created by calling {@link OhipReservationClient#getFoliosAciAmount} and
   * matching the {@code paymentMethod} string from the given {@code criteria} against the
   * {@code paymentMethod} field inside each folio window's payment method. If the folio is
   * confirmed as already created, the flow continues normally. If the verification call itself
   * fails, or if the folio is not found, a single retry is performed.
   * If the retry also fails, the original exception is re-thrown.
   */
  private void sendDepositFoliosWithVerification(String hotelId, String reservationId,
      DepositFolioCriteria criteria) {
    try {
      ohipReservationClient.sendDepositFoliosRequest(hotelId, reservationId, criteria);
    } catch (Exception initialException) {
      log.warn(
          "sendDepositFoliosRequest failed for hotelId={}, reservationId={}. "
              + "Checking if deposit folio was already created in OPERA.",
          sanitizeInput(hotelId), sanitizeInput(reservationId), initialException);

      var expectedPaymentMethod = Optional.ofNullable(criteria.getCriteria())
          .map(DepositFolioCriteriaType::getPaymentMethod)
          .map(ReservationPaymentMethodType::getPaymentMethod)
          .orElse(null);

      FoliosResponseDto folios;
      try {
        folios = ohipReservationClient.getFoliosAciAmount(hotelId, reservationId);
      } catch (Exception foliosException) {
        log.warn("Failed to fetch folios for verification. hotelId={}, reservationId={}.",
            sanitizeInput(hotelId), sanitizeInput(reservationId), foliosException);
        folios = null;
      }

      boolean folioAlreadyCreated = expectedPaymentMethod != null
          && Optional.ofNullable(folios)
          .map(FoliosResponseDto::getReservationFolioInformation)
          .map(ReservationFolioInformationDto::getFolioWindowType)
          .orElse(Collections.emptyList())
          .stream()
          .map(FolioWindowsDto::getPaymentMethod)
          .filter(Objects::nonNull)
          .map(ReservationPaymentMethodType::getPaymentMethod)
          .anyMatch(expectedPaymentMethod::equals);

      if (folioAlreadyCreated) {
        log.info(
            "Deposit folio with paymentMethod={} already exists in OPERA for hotelId={}, "
                + "reservationId={}. Continuing flow.",
            expectedPaymentMethod, sanitizeInput(hotelId), sanitizeInput(reservationId));
      } else {
        log.info(
            "Deposit folio not found in OPERA for hotelId={}, reservationId={}. "
                + "Retrying sendDepositFoliosRequest.",
            sanitizeInput(hotelId), sanitizeInput(reservationId));
        try {
          ohipReservationClient.sendDepositFoliosRequest(hotelId, reservationId, criteria);
        } catch (Exception retryException) {
          log.error(
              "Retry of sendDepositFoliosRequest also failed for hotelId={}, reservationId={}.",
              sanitizeInput(hotelId), sanitizeInput(reservationId), retryException);
          throw initialException;
        }
      }
    }
  }

  /**
   * Polls Opera for the reservation after a {@code ChangeReservation} until the deposit policy differs
   * from {@code oldDepositPolicyCode} or retries (exponential backoff; see {@code FETCH_RESERVATION_MAX_ATTEMPTS})
   * are exhausted. Returns the updated reservation, otherwise {@code null}.
   *
   * @param hotelId hotel identifier
   * @param reservationId reservation identifier
   * @param oldDepositPolicyCode original deposit policy code (nullable)
   * @return updated reservation, or {@code null} if not updated in time
   */
  private uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation fetchReservationWithUpdatedDepositPolicy(
      String hotelId, String reservationId, ResDepositPolicyType oldDepositPolicyCode) {

    final long baseDelayMs = reservationOhipProperties.getFetchReservationThreadSleep();

    for (int attempt = 0; attempt < FETCH_RESERVATION_MAX_ATTEMPTS; attempt++) {
      var reservation = ohipReservationClient.getReservation(hotelId, reservationId).block();
      if (reservation != null && depositPolicyUpdated(reservation, oldDepositPolicyCode)) {
        return reservation;
      }

      if (shouldSleepBeforeNextAttempt(attempt)) {
        log.debug(
            "Deposit policy not updated yet; backing off before retry. hotelId={}, reservationId={}, attempt={}",
            sanitizeInput(hotelId),
            sanitizeInput(reservationId),
            attempt);
        sleepWithBackoff(baseDelayMs, attempt);
      }
    }

    log.warn(
        "Exhausted retries while waiting for deposit policy update. hotelId={}, reservationId={}, maxAttempts={}",
        sanitizeInput(hotelId),
        sanitizeInput(reservationId),
        FETCH_RESERVATION_MAX_ATTEMPTS);

    return null;
  }

  private boolean shouldSleepBeforeNextAttempt(int attempt) {
    return attempt < FETCH_RESERVATION_MAX_ATTEMPTS - 1;
  }

  private void sleepWithBackoff(long baseDelayMs, int attempt) {
    final long delayMs = computeExponentialBackoffDelayMs(baseDelayMs, attempt);
    try {
      Thread.sleep(delayMs);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      final var message = "Thread interrupted while waiting to fetch updated reservation details";
      final var exception = new HotelReservationException(
          ErrorCode.DIGITAL_FETCH_RESERVATION_DETAILS_EXCEPTION,
          message
      );
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private long computeExponentialBackoffDelayMs(long baseDelayMs, int attempt) {
    return Math.multiplyExact(baseDelayMs, 1L << attempt);
  }

  private boolean depositPolicyUpdated(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation,
      ResDepositPolicyType oldDepositPolicyCode) {
    var currentDepositPolicyCode = findFirstDepositPolicy(reservation).orElse(null);
    return !Objects.equals(oldDepositPolicyCode, currentDepositPolicyCode);
  }

  private static Optional<ResDepositPolicyType> findFirstDepositPolicy(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {

    return Optional.ofNullable(reservation)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation::getReservations)
        .map(HotelReservationsType::getReservation)
        .filter(list -> !list.isEmpty())
        .map(list -> list.get(0))
        .map(HotelReservationType::getReservationPolicies)
        .map(ReservationPoliciesType::getDepositPolicies)
        .flatMap(depositPolicies -> depositPolicies.stream().findFirst());
  }

  /**
   * Given that digital payment methods are used, when the customer checks in at the hotel and wants
   * to perform changes to its reservation which result in extra charges or refunds those can’t be
   * automatically sent by Opera to the payment provider. All payment related actions done by Front
   * Desk can’t be automatically be performed by Opera’s payment provider. This resulted in a
   * multitude of reservations with refunds done manually.
   *
   * <p>In order to solve this issue the switch to non-digital payment method for the overall
   * reservation is required.
   *
   * <p>Check out this page for more details <a href="ls:">https://whitbreadis.atlassian.net
   * /wiki/spaces/DSA/pages/4221173773/Switch+to+non-digital+payment+methods#2.3-Amend</a>
   */
  private CompletableFuture<Void>  updateDepositFolioWithNonDigitalPaymentMethod(
      ConfirmReservationRequest confirmReservationRequest,
      String reservationId,
      String hotelId) {
    if (ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(
        reservationOhipProperties,
        hotelId)) {
      return CompletableFuture.runAsync(concurrentTracer.wrap(() -> {
        log.info("Entered set non-digital payment method for hotelId = {} and reservationId = {}",
            hotelId, reservationId);
        try {
          Thread.sleep(reservationOhipProperties.getNonDigitalThreadSleep());
        } catch (InterruptedException e) {
          log.error("Failed to sleep before update to non-digital payment method", e);
          var message = "Error while trying to sleep before update to non-digital payment method";
          var exception = new HotelReservationException(ErrorCode.DIGITAL_NO_DELAY_NON_DIGITAL_PAY_EXCEPTION,
              message, e);
          ExceptionLogger.log(log, exception);
          Thread.currentThread().interrupt();
          throw exception;
        }
        log.info("Call after delay set non-digital payment method for hotelId = {} and reservationId = {}",
            hotelId, reservationId);
        ChangeReservation changeReservation = mapToChangeReservation(
            confirmReservationRequest);
        if (confirmReservationRequest.getPaymentMethod().startsWith("K")) {
          changeReservation.getReservations().forEach(res ->
              res.setRoomStay(null));
        }
        ohipReservationClient.sendChangeReservationRequest(hotelId, reservationId,
            changeReservation).block();
      }));
    }
    return null;
  }

  private void setDigitalPaymentMethodForPayNow(ConfirmReservationRequest confirmReservationRequest,
      String hotelId,
      DepositFolioRequest depositRequest) {
    var digitalPaymentMethod = confirmReservationRequest.getDigitalPaymentMethod();
    if (ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(
        reservationOhipProperties,
        hotelId) && Objects.nonNull(digitalPaymentMethod)) {
      depositRequest.setPaymentMethod(digitalPaymentMethod);
    }
  }

  private void setDigitalPaymentMethodForPayNow(CancelReservationRequest cancelReservationRequest,
      DepositFolioCriteria depositFolioCriteriaNegativeAmounts) {
    var digitalPaymentMethod = cancelReservationRequest.getDigitalPaymentMethod();
    if (ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(
        reservationOhipProperties,
        cancelReservationRequest.getHotelId())
        && Objects.nonNull(digitalPaymentMethod)) {
      depositFolioCriteriaNegativeAmounts.getCriteria().getPaymentMethod()
          .setPaymentMethod(digitalPaymentMethod);
      depositFolioCriteriaNegativeAmounts.getCriteria().getPayments()
          .forEach(paymentCriteriaType -> Optional.ofNullable(paymentCriteriaType)
              .map(PaymentCriteriaType::getPaymentMethod)
              .ifPresent(
                  paymentMethodType -> paymentMethodType.setPaymentMethod(digitalPaymentMethod)));
    }
  }

  @Override
  public DepositFoliosResponse getDepositFolioForReservations(String hotelId,
      Set<String> reservationIds) {

    var reservations = ohipReservationClient.getReservations(hotelId, reservationIds)
        .collectList()
        .block();

    return mapDepositFoliosResponse(reservations, hotelId);
  }

  @Override
  public void createDepositFolios(DepositFoliosResponse depositFolios) {

    depositFolios.getDepositFolios().forEach(depositFolio -> {
      var reservationDetails = ohipReservationClient.getReservations(
              depositFolio.getHotelId(),
              new HashSet<>(List.of(depositFolio.getReservationId()))).collectList()
          .block();

      //retrieve card information
      ReservationPaymentMethodType resPaymentMethod = reservationDetails.get(0).getReservations()
          .getReservation().get(0)
          .getReservationPaymentMethods().stream().filter(r -> r.getPaymentCard() != null
              && r.getPaymentCard().getCardId() != null)
          .findFirst().orElse(null);
      CreditCardInfo cardInfo = getCardInformation(depositFolio.getHotelId(),
          resPaymentMethod.getPaymentCard().getCardId().getId());

      resPaymentMethod.getPaymentCard().setCardNumber(cardInfo.getCreditCard().getCardNumber());

      /*DNRQ-57778 For migrated bookings the saved payment method does not contain the channel and is not suitable for
      deposit folios. In this case, it must be replaced with defaultPaymentMethod to avoid multiple refunds*/
      if (resPaymentMethod.getPaymentMethod().length() == 2
          && StringUtils.isNotEmpty(depositFolio.getDefaultPaymentMethod())) {
        resPaymentMethod.setPaymentMethod(depositFolio.getDefaultPaymentMethod());
      }

      DepositFolioCriteria depositFolioCriteria = depositFoliosRequestOhipMapper
          .toDepositFolioCriteriaModel(depositFolio,
              reservationDetails.get(0).getReservations().getReservation().get(0),
              resPaymentMethod);
      depositFolioCriteria.getCriteria().getPayments().get(0)
          .setOverrideInsufficientCC(true);

      ohipReservationClient.sendDepositFoliosRequest(depositFolio.getHotelId(),
          depositFolio.getReservationId(), depositFolioCriteria);
    });
  }

  private DepositFoliosResponse mapDepositFoliosResponse(
          List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations, String hotelId) {
    if (CollectionUtils.isEmpty(reservations)) {
      return DepositFoliosResponse.builder().depositFolios(Collections.emptyList()).build();
    }

    List<DepositFolio> depositFolios = reservations.parallelStream()
            .map(res -> generateDepositFolioCriteriaForReservation(hotelId, res))
            .filter(Objects::nonNull)
            .map(depositFolioCriteriaMapper::toDepositFolioModel)
            .toList();

    Map<String, String> vatTypes = reservations.stream()
            .collect(Collectors.toMap(
                    res -> res.getReservations().getReservation().get(0).getReservationIdList().stream()
                            .filter(resIdType -> RESERVATION.equalsIgnoreCase(resIdType.getType()))
                            .map(UniqueIDType::getId)
                            .findAny()
                            .orElse(""),
                    this::getVatRegion));

    depositFolios.forEach(
            depositFolio -> depositFolio.setVatRegion(vatTypes.get(depositFolio.getReservationId())));

    return DepositFoliosResponse.builder()
            .depositFolios(depositFolios)
            .build();
  }

  private DepositFolioCriteria generateDepositFolioCriteriaForReservation(String hotelId,
      uk.co.whitbread.hotel.ohip.adapter.generated
          .models.Reservation reservation) {

    String reservationId = reservation.getReservations().getReservation().get(0)
        .getReservationIdList().stream()
        .filter(resIdType -> RESERVATION.equalsIgnoreCase(resIdType.getType()))
        .map(UniqueIDType::getId)
        .findAny()
        .orElse("");

    if (StringUtils.isBlank(reservationId)) {
      return null;
    }

    List<String> reservationPackages = getPackagesCodesForVat(reservation);
    var vatRulesDetails = rulesAgentClient.getVatCodes(getVatRegion(reservation),
        reservationPackages);

    var totalReservationAmounts = getReservationAmounts(hotelId, Set.of(reservationId), null);

    ConfirmReservationRequest confirmReservationRequest = ConfirmReservationRequest.builder()
        .hotelId(hotelId)
        .reservationId(reservationId)
        .paymentOption(PaymentOption.PAY_NOW).build();

    var depositRequest = depositFoliosRequestMapper.toDepositFolioRequestModel(
        confirmReservationRequest);

    if (Objects.nonNull(totalReservationAmounts)) {
      depositRequest.setTotalCostOfStay(totalReservationAmounts.getTotalCostOfStay());
    }

    return mapToDepositCriteriaForAmend(depositRequest, reservation,
        vatRulesDetails, getCityTaxDetailsPerDay(hotelId,
            reservationId, reservation));

  }

  @Override
  public ReservationByBasketRefResponse getReservationsByIds(String hotelId,
      Set<String> reservationIds,
      Boolean priceBreakdownNeeded, boolean rateInfoNeeded, Boolean operaUiCreatedRsv) {
    log.debug("Entered getReservationsByIds for hotelId={} and {} reservation ids",
        sanitizeInput(hotelId), reservationIds.size());
    final var reservationList =
        ohipReservationClient.getReservations(hotelId, reservationIds).collectList()
            .block();
    sortReservationListAsInitialSet(reservationIds, reservationList);

    Map<String, RateInfo> rateInfos = new HashMap<>();

    if (reservationList.isEmpty()) {
      var exception = new HotelReservationNotFound(
          ErrorCode.DIGITAL_RESERVATION_NOT_FOUND,
          "Could not find Opera reservation");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    var sourceId =
        reservationList.get(0).getReservations().getReservation().get(0).getRoomStay()
            .getRoomRates()
            .get(0).getSourceCode();
    var channelInfo = rulesAgentClient.getChannelSourceInfo(sourceId);
    if (nonNull(channelInfo)
        && DISTRIBUTION_CHANNEL.equals(channelInfo.getRequestDetails().getChannel())
        && (priceBreakdownNeeded)) {

      Flux.fromIterable(reservationList)
          .flatMap(reservation -> {
            var reservationId = reservation.getReservations().getReservation().get(0)
                .getReservationIdList()
                .stream().filter(idType -> RESERVATION.equals(idType.getType()))
                .findFirst().get().getId();
            var reservationDates = reservation.getReservations().getReservation().get(0)
                .getRoomStay()
                .getArrivalDate()
                .datesUntil(reservation.getReservations().getReservation().get(0).getRoomStay()
                    .getDepartureDate()).toList();
            reservationDates.forEach(date -> rateInfos.put(date.toString(),
                ohipReservationClient.getRateInfo(hotelId, reservationId, date.toString(),
                    FALSE)));
            return Mono.just(rateInfos);
          }).collectList().block();
    }
    calculateCityTaxValueWithVat(reservationList, hotelId, rateInfos);
    Map<String, Profile> profilesByIds = new HashMap<>();
    final Set<String> profileIds = getProfileIdsByType(reservationList, RESERVATIONCONTACT);
    profileIds.addAll(getProfileIdsByType(reservationList, GUEST));

    ReservationAmounts totalReservationAmounts = null;
    BigDecimal totalDepositFromAci = null;
    Map<String, PriceBreakdownDto> rateInfoMap = new HashMap<>();

    //stayers profileIds
    Set<String> stayersProfileIds = reservationList.stream()
        .flatMap(reservations ->
            Optional.ofNullable(reservations.getReservations())
                .map(HotelReservationsType::getReservation)
                .orElse(Collections.emptyList())
                .stream()
        )
        .flatMap(reservationGuest ->
            Optional.ofNullable(reservationGuest.getReservationGuests())
                .orElse(Collections.emptyList())
                .stream()
        )
        .flatMap(reservationGuestProfile ->
            Optional.ofNullable(reservationGuestProfile.getProfileInfo())
                .map(ResGuestTypeProfileInfo::getProfileIdList)
                .orElse(Collections.emptyList())
                .stream()
        )
        .filter(profileId -> profileId.getType().equals(PROFILE))
        .map(UniqueIDType::getId)
        .collect(Collectors.toSet());

    if (!stayersProfileIds.isEmpty()) {
      profileIds.addAll(stayersProfileIds);
    }
    if (!profileIds.isEmpty()) {
      profilesByIds = getProfilesMap(profileIds);
    }
    if (rateInfoNeeded) {
      totalReservationAmounts = getReservationAmounts(hotelId, reservationIds, rateInfoMap);

      Map<String, BigDecimal> depositFromAciForReservation = getReservationAciAmounts(hotelId,
          reservationList);
      totalDepositFromAci = depositFromAciForReservation.values()
          .stream().reduce(
              BigDecimal.valueOf(0), BigDecimal::add);
      log.info("Total Deposit through ACI={}", totalDepositFromAci);
    }
    BigDecimal discount = getReservationsDiscount(reservationList);
    if (Objects.nonNull(totalReservationAmounts)) {
      totalReservationAmounts.setDiscount(discount);
    }
    final var hotelConfigs = ohipReservationClient.getHotelConfig(hotelId);
    final var rsvCardInfo = getReservationCardDetails(hotelId, reservationList);
    if (Objects.nonNull(totalReservationAmounts) && Objects.nonNull(totalDepositFromAci)
        && BigDecimal.ZERO.compareTo(totalDepositFromAci) != 0) {
      totalReservationAmounts.setDeposit(totalDepositFromAci);
      totalReservationAmounts.setOutStandingCostOfStay(
          totalReservationAmounts.getTotalCostOfStay().subtract(totalDepositFromAci));
      log.debug("Deposit Amount={}", totalReservationAmounts.getDeposit());
      log.debug("OutStanding Amount={}", totalReservationAmounts.getOutStandingCostOfStay());
    }

    var userDefinedFields = reservationList.get(0).getReservations()
        .getReservation().get(0).getUserDefinedFields();
    var purchaseOrderNumber = Optional.ofNullable(userDefinedFields)
        .map(UserDefinedFieldsType::getCharacterUDFs)
        .orElse(Collections.emptyList())
        .stream()
        .filter(characterUDFType -> characterUDFType.getName().equals(UDFC_PURCHASE_ORDER_NAME))
        .findFirst().map(CharacterUDFType::getValue).orElse(null);
    var customReferenceNumber = reservationList.get(0).getReservations().getReservation()
        .get(0).getCustomReference();

    var reservationByBasketRefResponse = reservationResponseOhipMapper
            .toReservationByBasketRefResponseModel(reservationList,
            rateInfos, profilesByIds, hotelConfigs, totalReservationAmounts, rsvCardInfo, rateInfoMap,
            purchaseOrderNumber, customReferenceNumber,
            nonNull(channelInfo) ? channelInfo.getRequestDetails().getChannel() : "");

    if (Boolean.TRUE.equals(operaUiCreatedRsv)) {
      mapExtraInfoForOperaUiRsv(reservationList, reservationByBasketRefResponse, hotelId);
    }

    mapAditionalFieldsToResponse(reservationList, reservationByBasketRefResponse,
        this::mapPreference);
    mapAditionalFieldsToResponse(reservationList, reservationByBasketRefResponse,
        this::mapAlert);

    setContextId(reservationList, reservationByBasketRefResponse);

    return reservationByBasketRefResponse;
  }

  @Override
  public ReservationLightweightResponse getReservationsByIds(String hotelId, Set<String> reservationIds) {
    log.debug("Entered getReservationsByIds for hotelId={} and {} reservation ids", hotelId, reservationIds.size());
    var reservationList = ohipReservationClient.getReservations(hotelId, reservationIds).collectList().block();

    if (reservationList == null || reservationList.isEmpty()) {
      var exception = new HotelReservationNotFound(
          ErrorCode.DIGITAL_RESERVATION_NOT_FOUND,
          "Could not find Opera reservation");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    final var hotelConfigs = ohipReservationClient.getHotelConfig(hotelId);
    return reservationResponseOhipMapper.toReservationLightweightResponseModel(
        reservationList, hotelConfigs, hotelId);
  }

  public static void setContextId(
          List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationList,
          ReservationByBasketRefResponse reservationByBasketRefResponse) {
    var firstReservation = reservationList.get(0).getReservations().getReservation().get(0);
    if (firstReservation.getExternalReferences() != null) {
      firstReservation.getExternalReferences().stream()
          .map(ExternalReferenceType::getIdContext)
          .filter(Objects::nonNull)
          .filter(idContext -> !"OPERA".equalsIgnoreCase(idContext))
          .findFirst()
          .ifPresent(reservationByBasketRefResponse::setIdContext);
    }
  }

  private void mapAditionalFieldsToResponse(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationList,
      ReservationByBasketRefResponse reservationByBasketRefResponse,
      BiConsumer<HotelReservationType, ReservationByBasketRefResponse> mapper) {

    var preferenceTypeList = reservationList.stream()
        .filter(reservation -> nonNull(reservation.getReservations()))
        .filter(reservation -> nonNull(reservation.getReservations().getReservation()))
        .flatMap(reservation -> reservation.getReservations().getReservation().stream()).toList();

    preferenceTypeList.forEach(hotelReservationType -> mapper.accept(hotelReservationType,
        reservationByBasketRefResponse));
  }


  private void mapExtraInfoForOperaUiRsv(
          List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationList,
          ReservationByBasketRefResponse reservationByBasketRefResponse, String hotelId) {
    reservationList.forEach(reservation -> {
      String reservationId = reservation.getReservations().getReservation().get(0).getReservationIdList()
              .stream()
              .filter(uniqueId -> RESERVATION.equals(uniqueId.getType()))
              .map(UniqueIDType::getId)
              .findFirst()
              .get();

      var reservationByIdFinal = reservationByBasketRefResponse.getReservationByIdList()
              .stream()
              .filter(reservationById -> reservationById.getReservationId().equals(reservationId))
              .findFirst();
      reservationByIdFinal.ifPresent(reservationById -> {

        reservationById.setBookingAllowancesResponse(buildBookingAllowancesResponse(reservation, List.of()));

        if (DEPOSIT_RECEIVED_GUARANTEE.equals(reservationById.getGuarantee().getGuaranteeCode())) {
          var depositFoliosResponse = mapDepositFoliosResponse(reservationList, hotelId);

          var depositFolios = depositFoliosResponse.getDepositFolios().stream()
                  .filter(depositFolio -> depositFolio.getReservationId().equals(reservationId))
                  .toList();

          reservationById.setDepositFoliosResponse(DepositFoliosResponse.builder()
                  .depositFolios(depositFolios)
                  .build());
        }
      });
    });
  }

  //To get the ACI amounts from getFolios Opera API and set in reservation
  private Map<String, BigDecimal> getReservationAciAmounts(String hotelId,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models
          .Reservation> reservationIds) {
    Map<String, BigDecimal> aciAmountAgainstEachReservation = new HashMap<>();
    reservationIds.stream().forEach(
        reservation -> {
          var reservationId = reservation.getReservations().getReservation().get(0)
              .getReservationIdList()
              .stream().filter(idType -> RESERVATION.equals(idType.getType()))
              .findFirst().get().getId();
          BigDecimal amountPaid = extractAmountFromFolioWindows(
              ohipReservationClient.getFoliosAciAmount(hotelId, reservationId)).abs();
          if (amountPaid != null && BigDecimal.ZERO.compareTo(amountPaid) != 0) {
            aciAmountAgainstEachReservation.put(reservationId, amountPaid);
            List<ResDepositPolicyType> depositPolicy = reservation.getReservations()
                .getReservation().get(0).getReservationPolicies().getDepositPolicies();
            if (Objects.nonNull(depositPolicy)) {
              log.debug("Amount paid for reservation={} from getReservation depositPolicy = {}",
                  reservationId, depositPolicy.get(0).getAmountPaid().getAmount());
              depositPolicy.get(0).getAmountPaid().setAmount(amountPaid);
              log.debug("Amount paid for reservation after ACI = {}",
                  depositPolicy.get(0).getAmountPaid().getAmount());
              var amountDue = depositPolicy.get(0).getAmountDue().getAmount();
              log.debug("Amount Due for reservation={} from getReservation depositPolicy = {}",
                  reservationId, amountDue);
              depositPolicy.get(0).getAmountDue().setAmount(amountDue.subtract(amountPaid));
              log.debug("Amount Due for reservation after ACI = {}",
                  depositPolicy.get(0).getAmountDue().getAmount());
            }
          }
        });
    return aciAmountAgainstEachReservation;
  }

  //To Calculate the Amount deposited through different foliosWindows from getFolios Response
  private BigDecimal extractAmountFromFolioWindows(FoliosResponseDto foliosAciAmount) {
    log.debug("Entering extractAmountFromFolioWindows for the folioWindowResponse={}",
        foliosAciAmount);
    BigDecimal foliosWindowsAmountPaid = null;
    if (Objects.nonNull(foliosAciAmount)) {
      var folioWindows = foliosAciAmount.getReservationFolioInformation()
          .getFolioWindowType().stream()
          .filter(folioWindow -> folioWindow.getEmptyFolio().equals(false)).toList();
      foliosWindowsAmountPaid = folioWindows.stream().map(
              amount -> amount.getPayment().getAmount()).toList()
          .stream().reduce(BigDecimal.ZERO, BigDecimal::add);
      log.info("Amount Deposited through ACI={}", foliosWindowsAmountPaid);
      return foliosWindowsAmountPaid;
    }
    return foliosWindowsAmountPaid;
  }

  private void sortReservationListAsInitialSet(Set<String> reservationIds,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation>
          reservationList) {
    final var reservationIdsList = new ArrayList<>(reservationIds);
    reservationList.sort(Comparator.comparing(r -> reservationIdsList.indexOf(
        getOperaReservationId(r))));
  }

  private String getOperaReservationId(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation r) {
    return r.getReservations().getReservation().get(0).getReservationIdList().stream()
        .filter(id -> id.getType().equals(
            RESERVATION)).findAny().orElseThrow((() -> {
              var exception = new HotelReservationException(
                  ErrorCode.DIGITAL_OPERA_RESERVATION_EXCEPTION, "Reservation not found in Opera");
              ExceptionLogger.log(log, exception);
              return exception;
            })
        ).getId();
  }

  @Override
  public BookingAllowancesResponse getBookingAllowances(String hotelId, String reservationId,
      List<String> basketBookingAllowances) {

    var reservation = ohipReservationClient.getReservationWithRoutingInstructions(hotelId,
        reservationId);
    return buildBookingAllowancesResponse(reservation, basketBookingAllowances);
  }

  private BookingAllowancesResponse buildBookingAllowancesResponse(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation,
      List<String> basketBookingAllowances) {
    final List<BookingAllowance> bookingAllowances = new LinkedList<>();
    String businessNotes = null;

    if (!reservation.getReservations().getReservation().isEmpty()) {
      var hotelReservationType = reservation.getReservations().getReservation().get(0);
      if (!hotelReservationType.getRoutingInstructions().isEmpty()) {
        var instructions = hotelReservationType.getRoutingInstructions().get(0).getFolio()
            .getInstructions();
        var rules = rulesAgentClient.getBusinessAllowances().getBusinessAllowances();
        bookingAllowances.addAll(
            getBookingAllowancesList(instructions, rules,
                getReservationPackages(hotelReservationType), basketBookingAllowances));
      }
      if (ObjectUtils.isNotEmpty(hotelReservationType.getComments())) {
        var businessNotesComment = hotelReservationType.getComments().stream().filter(
                comment -> OhipConstants.BUSINESS_NOTES_COMMENT_TITLE.equals(
                    comment.getComment().getCommentTitle()))
            .findFirst();
        businessNotes =
            businessNotesComment.map(
                commentInfoType -> commentInfoType.getComment().getText().getValue()).orElse(null);
      }

    }
    return BookingAllowancesResponse.builder()
        .bookingAllowances(bookingAllowances)
        .businessNotes(businessNotes)
        .build();
  }

  private static Set<String> getReservationPackages(HotelReservationType hotelReservationType) {
    var reservationsPackages = new HashSet<String>();
    if (ObjectUtils.isNotEmpty(hotelReservationType.getReservationPackages())) {
      reservationsPackages.addAll(
          hotelReservationType.getReservationPackages().stream()
              .filter(packageType -> packageType.getScheduleList() != null
                  && packageType.getScheduleList().stream()
                  .anyMatch(schedule -> schedule.getTotalQuantity() != null
                      && schedule.getTotalQuantity() > 0))
              .map(ReservationPackageType::getPackageCode)
              .collect(toSet()));
    }
    return reservationsPackages;
  }

  private BigDecimal getReservationsDiscount(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationList) {
    return reservationList.stream().map(reservation ->
            reservation.getReservations().getReservation().stream()
                .map(reservationType -> Optional.ofNullable(reservationType.getRoomStay())
                    .map(RoomStayType::getRoomRates).orElseGet(Collections::emptyList).stream()
                    .map(roomRateType -> Optional.ofNullable(roomRateType.getRates())
                        .map(RatesType::getRate).orElseGet(Collections::emptyList).stream()
                        .map(rate -> Optional.ofNullable(rate.getDiscount())
                            .map(DiscountType::getAmount).orElse(BigDecimal.ZERO))
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
  }

  @Override
  public ReservationAmounts getReservationAmounts(String hotelId, Set<String> reservationIds) {
    ReservationAmounts reservationAmounts = getReservationAmounts(hotelId, reservationIds, null);
    BigDecimal totalDepositFromAci = reservationIds.stream().map(
        reservation -> extractAmountFromFolioWindows(
              ohipReservationClient.getFoliosAciAmount(hotelId, reservation)).abs())
        .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    log.info("Total deposit through ACI = {}", totalDepositFromAci);

    if (Objects.nonNull(reservationAmounts) && BigDecimal.ZERO.compareTo(totalDepositFromAci) != 0) {
      reservationAmounts.setDeposit(totalDepositFromAci);
      reservationAmounts.setOutStandingCostOfStay(
          reservationAmounts.getTotalCostOfStay().subtract(totalDepositFromAci));
      log.debug("Deposit amount = {}", reservationAmounts.getDeposit());
      log.debug("Outstanding amount = {}", reservationAmounts.getOutStandingCostOfStay());
    }

    return reservationAmounts;
  }

  private ReservationAmounts getReservationAmounts(String hotelId, Set<String> reservationIds,
      Map<String, PriceBreakdownDto> rateInfoMap) {
    return Flux.fromIterable(reservationIds)
        .flatMap(
            reservationId -> ohipReservationClient.getReservationAmounts(hotelId, reservationId))
        .doOnNext(response -> {
          if (rateInfoMap != null) {
            rateInfoMap.put(response.getT1(), response.getT2());
          }
        })
        .reduce(ReservationAmounts.buildResAmountWithZero(), ((result, currentAmounts) -> {
          result.setCurrencyCode(currentAmounts.getT2().getSummary().getCurrencyCode());
          result.setGross(result.getGross().add(currentAmounts.getT2().getSummary().getGross()));
          result.setNet(result.getNet().add(currentAmounts.getT2().getSummary().getNet()));
          result.setDeposit(
              result.getDeposit().add(currentAmounts.getT2().getSummary().getDeposit().negate()));
          result.setTotalCostOfStay(
              result.getTotalCostOfStay().add(currentAmounts.getT2().getSummary()
                  .getTotalCostOfStay()));
          result.setOutStandingCostOfStay(
              result.getOutStandingCostOfStay().add(currentAmounts.getT2().getSummary()
                  .getOutStandingCostOfStay()));
          return result;
        }))
        .block();
  }

  private Map<String, Profile> getProfilesMap(Set<String> profileIds) {
    Map<String, Profile> profilesByIds;
    final var bookerProfiles = ohipReservationClient.sendGetProfilesByProfileIds(profileIds);

    profilesByIds = bookerProfiles.stream().collect(
        Collectors.toMap(bookerProfile -> bookerProfile.getProfileIdList().get(0).getId(),
            Function.identity()));
    return profilesByIds;
  }

  private Set<String> getProfileIdsByType(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations,
      ResProfileTypeType profileType) {
    return reservations.stream()
        .filter(reservationById ->
            reservationById.getReservations().getReservation().get(0).getReservationProfiles()
                != null)
        .map(reservationById -> reservationById.getReservations().getReservation().get(0)
            .getReservationProfiles().getReservationProfile().stream()
            .filter(
                reservationProfileType -> profileType.equals(
                    reservationProfileType.getReservationProfileType()))
            .findFirst()
            .map(reservationProfileType -> reservationProfileType.getProfileIdList().get(0).getId())
            .orElse(null)
        ).filter(Objects::nonNull).collect(toSet());
  }

  private Set<String> getGuestProfileIdsByType(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations,
      ProfileTypeType profileType) {
    return reservations.stream()
        .filter(reservationById ->
            reservationById.getReservations().getReservation().get(0).getReservationProfiles()
                != null)
        .map(reservationById -> reservationById.getReservations().getReservation().get(0)
            .getReservationGuests().stream()
            .filter(
                reservationProfileType -> profileType.equals(
                    reservationProfileType.getProfileInfo().getProfile().getProfileType()))
            .findFirst()
            .map(
                reservationProfileType -> reservationProfileType.getProfileInfo().getProfileIdList()
                    .get(0).getId())
            .orElse(null)
        ).filter(Objects::nonNull).collect(toSet());
  }

  private Optional<String> getBookerProfileId(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations) {
    return reservations.stream()
        .filter(reservationById ->
            reservationById.getReservations().getReservation().get(0).getReservationGuests()
                != null)
        .flatMap(reservationById -> reservationById.getReservations().getReservation()
            .get(0).getReservationGuests().stream())
        .filter(resGuest -> resGuest.getPrimary().equals(Boolean.TRUE))
        .flatMap(booker -> booker.getProfileInfo().getProfileIdList().stream())
        .findFirst()
        .map(UniqueIDType::getId);
  }

  @Override
  public void updateReservationPackages(ReservationPackagesRequest reservationPackagesRequest) {
    final List<String> reservationsId = reservationPackagesRequest.getReservationsId();
    //get packages without the codes which are groups
    log.debug("Entered updateReservationPackages for hotelId={} and {} reservations",
        reservationPackagesRequest.getHotelId(),
        (reservationsId == null ? "null" :
            reservationsId.size()));
    if (isDonationPackagesSelection(reservationPackagesRequest)) {
      updateDonationPackagesToRemove(reservationPackagesRequest);
    }
    removePackagesFromReservations(reservationPackagesRequest, reservationsId);
    //add the new packages to the reservations
    PackagesResponseOhipDto packagesResponseOhipDto = getPackages(
        reservationPackagesRequest).block();
    addPackagesToReservations(reservationPackagesRequest, reservationsId,
        packagesResponseOhipDto);
  }

  public List<ChangeReservation> getUpdatePackageChangeReservation(
          ReservationPackagesRequest reservationPackagesRequest) {

    final List<String> reservationsId = reservationPackagesRequest.getReservationsId();

    if (isDonationPackagesSelection(reservationPackagesRequest)) {
      updateDonationPackagesToRemove(reservationPackagesRequest);
    }
    removePackagesFromReservations(reservationPackagesRequest, reservationsId);
    //add the new packages to the reservations
    PackagesResponseOhipDto packagesResponseOhipDto = getPackages(
            reservationPackagesRequest).block();

    return  reservationsId.stream().map(res ->
            mapToUpdateReservationPackagesOhip(reservationPackagesRequest,
                    reservationsId.indexOf(res),
                    packagesResponseOhipDto)).toList();
  }

  private Mono<PackagesResponseOhipDto> getPackages(
      ReservationPackagesRequest reservationPackagesRequest) {
    return ohipPackagesClient.getPackages(
        packagesRequestDtoOhipMapper.toDto(reservationPackagesRequest),
        new LinkedMultiValueMap<>());
  }

  private void addPackagesToReservations(ReservationPackagesRequest reservationPackagesRequest,
      List<String> reservationsId, PackagesResponseOhipDto packagesResponseOhipDto) {
    Flux.fromIterable(reservationsId)
        .flatMap(res -> ohipReservationClient.sendChangeReservationRequest(
            reservationPackagesRequest.getHotelId(),
            res,
            mapToUpdateReservationPackagesOhip(reservationPackagesRequest,
                reservationsId.indexOf(res),
                packagesResponseOhipDto)))
        .doOnError(e -> {
          var exception = new HotelReservationException(
              ErrorCode.DIGITAL_NOT_SAVE_PACKAGE_EXCEPTION, "Could not save selected packages");
          ExceptionLogger.log(log, exception);
          throw exception;
        })
        .collectList().block();
  }

  private void removePackagesFromReservations(ReservationPackagesRequest reservationPackagesRequest,
      List<String> reservationsId) {
    Flux.fromIterable(reservationsId)
        .filter(res -> !Objects.isNull(reservationPackagesRequest.getPreviousRoomsSelections())
            && !Objects.isNull(
            reservationPackagesRequest.getPreviousRoomsSelections().get(reservationsId.indexOf(res))
                .getPackagesSelection())
            && reservationPackagesRequest.getPreviousRoomsSelections()
            .get(reservationsId.indexOf(res))
            .getPackagesSelection().stream().noneMatch(p -> p.getId().equals(HSATWN)))
        .flatMap(res -> ohipReservationClient.sendChangeReservationRequest(
            reservationPackagesRequest.getHotelId(),
            res,
            mapToRemoveReservationPackagesOhip(reservationPackagesRequest,
                reservationsId.indexOf(res))))
        .doOnError(e -> {
          var exception = new HotelReservationException(
              ErrorCode.DIGITAL_NOT_REMOVE_PACKAGE_EXCEPTION,
              "Could not remove previous selected packages");
          ExceptionLogger.log(log, exception);
          throw exception;
        })
        .collectList().block();
  }

  @Override
  public void updateDiscount(UpdateDiscountRequest updateDiscountRequest) {

    var hotelId = updateDiscountRequest.getHotelId();
    var reservationIds = updateDiscountRequest.getReservationIds();
    var discount = updateDiscountRequest.getDiscountAmount();

    log.debug("Entered updateDiscount for hotelId={}, discount={} and {} reservations",
        hotelId, discount, reservationIds.size());

    var reservations = ohipReservationClient.getReservations(hotelId, reservationIds)
        .collectList()
        .block();

    if (CollectionUtils.isEmpty(reservations) || reservationIds.size() != reservations.size()) {
      var exception = new HotelReservationNotFound(
          ErrorCode.DIGITAL_OPERA_DISCOUNT_EXCEPTION,
          "Could not find Opera reservation to apply discount");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (discount.compareTo(getReservationsRoomRatesTotal(reservations)) > 0) {
      var exception = new DiscountInvalidAmountException(
          ErrorCode.DIGITAL_UPDATE_DISCOUNT_EXCEPTION,
          "Discount amount is larger than base amount");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    var changeReservation = mapToChangeReservationDiscount(
        updateDiscountRequest, reservations);

    Flux.fromIterable(updateDiscountRequest.getReservationIds())
        .flatMap(resId -> ohipReservationClient.sendChangeReservationRequest(hotelId, resId,
            changeReservation), reservationOhipProperties.getMaxConcurrency())
        .collectList().block();
  }

  @Override
  public void updateCompanyQuestionAndAnswerDetails(
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest) {

    var hotelId = companyQuestionAndAnswerDetailsRequest.getHotelId();
    var reservationIds = companyQuestionAndAnswerDetailsRequest.getReservationIds();

    log.debug(
        "Entered updateCompanyQuestionAndAnswerDetails for for hotelId={} with reservations {}",
        hotelId, (reservationIds == null ? "null" : reservationIds));

    var reservations = ohipReservationClient.getReservations(hotelId, reservationIds)
        .collectList()
        .block();

    if (validateReservationsAndQnA(reservations, companyQuestionAndAnswerDetailsRequest)) {
      Flux.fromIterable(reservationIds)
          .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(hotelId,
              reservationId,
              companyQuestionAndAnswerRequestOhipMapper.toChangeReservationDto(hotelId,
                  reservationId, companyQuestionAndAnswerDetailsRequest)))
          .collectList()
          .block();
    }
  }

  private boolean validateReservationsAndQnA(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations,
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest) {

    if (CollectionUtils.isEmpty(reservations) || reservations.get(0).getReservations() == null
        || CollectionUtils.isEmpty(reservations.get(0).getReservations().getReservation())) {
      log.error("Could not find Opera reservation to update the questions and answers");
      return false;
    }

    final List<CommentInfoType> existingComments =
        reservations.get(0).getReservations().getReservation().get(0).getComments();

    if (!CollectionUtils.isEmpty(existingComments)) {
      boolean questionAndAnswerExists = existingComments.stream()
          .anyMatch(comment ->
              QuestionAndAnswerTypeEnum.PUR_ORD_QNA.name().equals(comment.getComment().getType())
                  || QuestionAndAnswerTypeEnum.CUST_REF_QNA.name()
                  .equals(comment.getComment().getType())
                  || QuestionAndAnswerTypeEnum.USR_DEF_QNA.name()
                  .equals(comment.getComment().getType()));

      if (questionAndAnswerExists) {
        log.warn("Unable to update: as Company Questions And Answers already been added, no "
            + "questions and answers will be added");
        return false;
      }
    }

    if (companyQuestionAndAnswerDetailsRequest != null
        && companyQuestionAndAnswerDetailsRequest.getCompanyQuestionAndAnswerDetails() != null) {

      List<CompanyQuestionAndAnswer> qnas = getCompanyQuestionAndAnswers(
          companyQuestionAndAnswerDetailsRequest);

      if (CollectionUtils.isEmpty(qnas)) {
        log.warn(
            "Could not find any purchaseOrderQuestionAndAnswer, customerReferenceQuestionAndAnswer or "
                + "userDefinedQuestionAndAnswers, so nothing will be added");
        return false;
      }
    } else {
      log.warn("Could not find the questions and answers to add, so nothing will be added");
      return false;
    }

    return true;
  }

  private static List<CompanyQuestionAndAnswer> getCompanyQuestionAndAnswers(
      CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest) {
    List<CompanyQuestionAndAnswer> qnas = new ArrayList<>();

    CompanyQuestionAndAnswer purchaseOrderQuestionAndAnswer = companyQuestionAndAnswerDetailsRequest
        .getCompanyQuestionAndAnswerDetails().getPurchaseOrderQuestionAndAnswer();

    if (purchaseOrderQuestionAndAnswer != null) {
      qnas.add(purchaseOrderQuestionAndAnswer);
    }

    CompanyQuestionAndAnswer customerReferenceQuestionAndAnswer = companyQuestionAndAnswerDetailsRequest
        .getCompanyQuestionAndAnswerDetails().getCustomerReferenceQuestionAndAnswer();

    if (customerReferenceQuestionAndAnswer != null) {
      qnas.add(customerReferenceQuestionAndAnswer);
    }

    List<CompanyQuestionAndAnswer> userDefinedQuestionAndAnswers = companyQuestionAndAnswerDetailsRequest
        .getCompanyQuestionAndAnswerDetails().getUserDefinedQuestionAndAnswers();

    if (!CollectionUtils.isEmpty(userDefinedQuestionAndAnswers)) {
      qnas.addAll(userDefinedQuestionAndAnswers);
    }
    return qnas;
  }

  @Override
  public void updateBusinessItems(BusinessItemsRequest businessItemsRequest) {

    var hotelId = businessItemsRequest.getHotelId();
    var reservationIds = businessItemsRequest.getReservationIds();
    var businessItems = businessItemsRequest.getBusinessItems();

    log.debug(
        "Entered updateBusinessItems for hotelId={} and {} reservations, having {} business items",
        hotelId, reservationIds.size(),
        nonNull(businessItems) ? businessItems.getBusinessAllowances().size() : 0);

    var reservations = ohipReservationClient.getReservations(hotelId, reservationIds)
        .collectList()
        .block();

    /*
     * clear alerts because the put reservation call will duplicate the alerts
     * this will not delete the alerts from opera, it just prevents duplicating them
     */
    clearAlerts(reservations);

    if (CollectionUtils.isEmpty(reservations) || reservationIds.size() != reservations.size()) {
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_APPLY_BUSINESS_ITEMS_EXCEPTION,
          "Could not find Opera reservation to apply business items");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (nonNull(businessItems)) {
      //map missing fields for businessItems
      mapMissingFieldsForBusinessItems(businessItems, reservations);

      var businessAllowances = rulesAgentClient.getBusinessAllowances().getBusinessAllowances();

      Flux.fromIterable(reservations).flatMap(reservation ->
          ohipReservationClient.sendChangeReservationRequest(hotelId,
              getOperaReservationId(reservation),
              mapToChangeReservationBusiness(businessItemsRequest, reservation, businessAllowances))
      ).collectList().block();
    } else if (DISTRIBUTION_CHANNEL.equals(businessItemsRequest.getChannel())
        || BUSINESS_BOOKER_CHANNEL.equals(businessItemsRequest.getChannel())) {
      int folioWindowNo = PaymentUtils.resolveDistrFolioWindow(
          businessItemsRequest.getPibaCardPresent());
      Flux.fromIterable(reservations).flatMap(reservation ->
          ohipReservationClient.sendChangeReservationRequest(hotelId,
              getOperaReservationId(reservation),
              mapToChangeReservation(reservation.getReservations().getReservation().get(0),
                  businessItemsRequest.getCompanyId(), folioWindowNo))
      ).collectList().block();
    } else if (PREPAID_CHANNEL.equals(businessItemsRequest.getChannel())) {
      reservations.forEach(reservation -> {
        var reservationDetails = reservation.getReservations().getReservation().get(0);

        var reservationPaymentMethods = reservationDetails.getReservationPaymentMethods().stream()
            .filter(reservationPaymentMethodType -> reservationPaymentMethodType.getPaymentCard() != null
                && reservationPaymentMethodType.getPaymentCard().getCardId() != null)
            .toList();

        String paymentMethod = "";
        CreditCardInfo cardInfoResponse = new CreditCardInfo();
        if (!reservationPaymentMethods.isEmpty()) {
          var reservationPaymentMethod = reservationPaymentMethods.get(0);
          paymentMethod = reservationPaymentMethod.getPaymentMethod();

          cardInfoResponse = frontDeskClient.getCreditCardInfo(hotelId,
              reservationPaymentMethod.getPaymentCard().getCardId().getId());
        }

        // package code - routing/transaction code mapping for all packages on this reservation
        var transactionCodes = getTransactionCodesForReservation(reservation);

        var totalReservationAmounts = getReservationAmounts(hotelId, reservationIds, null);

        var changeReservation = businessItemsRequestOhipMapper.toChangeReservationDto(
            reservation.getReservations().getReservation().get(0),
            paymentMethod, transactionCodes, cardInfoResponse,
            totalReservationAmounts, FOLIO_WINDOW_3);

        ohipReservationClient.sendChangeReservationRequest(hotelId,
                getOperaReservationId(reservation),
                changeReservation
            ).block();
      });
    }
  }

  private void clearAlerts(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations) {

    if (Objects.isNull(reservations)) {
      return;
    }

    for (var reservation : reservations) {
      clearAlertsFromReservation(reservation);
    }
  }

  private void clearAlertsFromReservation(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {

    var hotelReservationType = reservation.getReservations();
    if (Objects.isNull(hotelReservationType) || Objects.isNull(
        hotelReservationType.getReservation())) {
      return;
    }

    for (var nestedReservation : hotelReservationType.getReservation()) {
      var alerts = nestedReservation.getAlerts();
      if (alerts != null) {
        alerts.clear();
      }
    }
  }

  // Given a reservation and the hotelId where it is booked, this method returns a list of
  // package code mappings for the packages paid for by the payer (including the room).
  private List<BusinessAllowanceRuleDto> getTransactionCodesForReservation(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {
    var packageCodes = getPackagesCodesForVat(reservation);
    var businessAllowances = rulesAgentClient.getBusinessAllowances();
    return businessAllowances.getBusinessAllowances().stream()
        .filter(ba -> (packageCodes.contains(ba.getSourceId())
            || ba.getSourceId().equals(PACKAGE_CODE_ACCOMMODATION)))
        .toList();
  }

  private List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> getBookingAllowanceDetails(
          String hotelId, Set<String> reservationIds) {
    var reservations = ohipReservationClient.getReservations(hotelId, reservationIds)
            .collectList()
            .block();

    if (CollectionUtils.isEmpty(reservations) || reservationIds.size() != reservations.size()) {
      var exception = new HotelReservationException(
              ErrorCode.DIGITAL_NO_RESERVATION_FOR_BUSINESS,
              "Could not find Opera reservation to apply business items");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return reservations;
  }

  @Override
  public List<ChangeReservation> getBookingAllowanceChangeReservation(BusinessItemsRequest businessItemsRequest) {
    var hotelId = businessItemsRequest.getHotelId();
    var reservationIds = businessItemsRequest.getReservationIds();
    var businessItems = businessItemsRequest.getBusinessItems();

    var reservations = getBookingAllowanceDetails(hotelId, reservationIds);

    List<ChangeReservation> bookingAllowancesChangeReservation = new ArrayList<>();

    if (nonNull(businessItems)) {
      //map missing fields for businessItems
      mapMissingFieldsForBusinessItems(businessItems, reservations);

      var businessAllowances = rulesAgentClient.getBusinessAllowances().getBusinessAllowances();

      reservations.forEach(reservation -> bookingAllowancesChangeReservation.add(
              mapToChangeReservationBusiness(businessItemsRequest,
                      reservation, businessAllowances)
      ));
    } else if (DISTRIBUTION_CHANNEL.equals(businessItemsRequest.getChannel())
            || BUSINESS_BOOKER_CHANNEL.equals(businessItemsRequest.getChannel())) {
      int folioWindowNo = PaymentUtils.resolveDistrFolioWindow(
          businessItemsRequest.getPibaCardPresent());
      reservations.forEach(reservation -> bookingAllowancesChangeReservation.add(
              mapToChangeReservation(reservation.getReservations().getReservation().get(0),
                      businessItemsRequest.getCompanyId(), folioWindowNo)
      ));
    } else if (PREPAID_CHANNEL.equals(businessItemsRequest.getChannel())) {
      reservations.forEach(reservation -> bookingAllowancesChangeReservation.add(
              mapToChangeReservation(reservation.getReservations().getReservation().get(0),
                      null, FOLIO_WINDOW_3)
      ));
    }

    return bookingAllowancesChangeReservation.isEmpty() ? null : bookingAllowancesChangeReservation;
  }

  @Override
  public void updateCustomReferenceNumber(
        UpdateCustomReferenceNumberRequest updateCustomReferenceNumberRequest) {
    var hotelId = updateCustomReferenceNumberRequest.getHotelId();
    Flux.fromIterable(updateCustomReferenceNumberRequest.getReservationIds())
        .flatMap(reservationId ->
            ohipReservationClient.sendChangeReservationRequest(hotelId, reservationId,
                updateCustomReferenceNumberOhipMapper.toChangeReservationDto(hotelId,
                    reservationId, updateCustomReferenceNumberRequest.getCustomReferenceNumber()))
        ).collectList().block();
  }

  @Override
  public void updateSpecialRequests(SpecialRequests specialRequests) {

    var hotelId = specialRequests.getHotelId();
    var reservationIds = specialRequests.getReservationIds();
    var specialRequestsList = specialRequests.getSpecialRequests();
    var bookingNotes = specialRequests.getBookingNotes();

    //Logic to retrieve reservation with comments only on SpecialNotes not businessNotes
    final var reservationList = ohipReservationClient.getReservations(hotelId,
            Set.copyOf(reservationIds)).collectList()
        .block();

    if (reservationList != null) {
      reservationList.forEach(reservation -> updateReservationComments(reservation, hotelId));
    }

    Flux.fromIterable(reservationIds)
        .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(hotelId,
            reservationId,
            specialReqRequestOhipMapper.toChangeReservationDto(hotelId, reservationId,
                specialRequestsList, bookingNotes)))
        .collectList()
        .block();
  }

  private void updateReservationComments(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation, String hotelId) {
    if (reservation.getReservations().getReservation().get(0).getComments() != null
        && !reservation.getReservations().getReservation().get(0).getComments().isEmpty()) {
      var commentId = reservation.getReservations().getReservation().get(0).getComments()
          .stream().filter(id -> id.getType().equals(COMMENT))
          .filter(commentTitle -> commentTitle.getComment().getCommentTitle()
              .equalsIgnoreCase(OhipConstants.SPECIAL_NOTES_COMMENT_TITLE))
          .map(CommentInfoType::getId)
          .toList();
      var reservationId = reservation.getReservations().getReservation().get(0)
          .getReservationIdList()
          .stream().filter(idType -> RESERVATION.equals(idType.getType()))
          .findFirst().map(UniqueIDType::getId).orElse(null);
      //Removing existing BookingNotes for the reservations
      ohipReservationClient.sendChangeReservationRequest(hotelId,
          reservationId,
          specialReqRequestOhipMapper.toChangeReservationRemoveExstCommentDto(hotelId,
              reservationId, commentId)).block();
    }
  }

  public ChangeReservation getSpecialRequestChangeReservation(SpecialRequests specialRequests) {

    if (!nonNull(specialRequests)) {
      return null;
    }
    var hotelId = specialRequests.getHotelId();
    var reservationIds = specialRequests.getReservationIds();
    var specialRequestsList = specialRequests.getSpecialRequests();
    var bookingNotes = specialRequests.getBookingNotes();

    //Logic to retrieve reservation with comments only on SpecialNotes not businessNotes
    final var reservationList = ohipReservationClient.getReservations(hotelId,
                    Set.copyOf(reservationIds)).collectList()
            .block();

    if (reservationList != null) {
      reservationList.forEach(reservation -> {
        if (reservation.getReservations().getReservation().get(0).getComments() != null
                && !reservation.getReservations().getReservation().get(0).getComments().isEmpty()) {
          var commentId = reservation.getReservations().getReservation().get(0).getComments()
                  .stream().filter(id -> id.getType().equals(COMMENT))
                  .filter(commentTitle -> commentTitle.getComment().getCommentTitle()
                          .equalsIgnoreCase(OhipConstants.SPECIAL_NOTES_COMMENT_TITLE))
                  .map(CommentInfoType::getId)
                  .toList();
          var reservationId = reservation.getReservations().getReservation().get(0)
                  .getReservationIdList()
                  .stream().filter(idType -> RESERVATION.equals(idType.getType()))
                  .findFirst().get().getId();
          //Removing existing BookingNotes for the reservations
          ohipReservationClient.sendChangeReservationRequest(hotelId,
                  reservationId,
                  specialReqRequestOhipMapper.toChangeReservationRemoveExstCommentDto(hotelId,
                          reservationId, commentId)).block();
        }
      });
    }

    return specialReqRequestOhipMapper
            .toChangeReservationDto(hotelId, reservationIds.get(0), specialRequestsList, bookingNotes);

  }

  @Override
  public void deleteRoutingInstruction(String hotelId, Set<String> reservationIds) {
    reservationIds.forEach(reservationId -> {
      var reservation =
          ohipReservationClient.getReservationWithRoutingInstructions(hotelId, reservationId);
      if (!reservation.getReservations().getReservation().isEmpty()) {
        var routingInstructions = reservation.getReservations().getReservation().get(0)
            .getRoutingInstructions();
        log.info("deleteRoutingInstruction: retrieved routing instructions for reservation ID {}",
            sanitizeInput(reservationId));
        if (!routingInstructions.isEmpty()) {
          routingInstructions.forEach(routingInstruction -> {
            var payeeId = routingInstruction.getFolio().getPayeeInfo().getPayeeId().getId();
            var folioWindowNo = routingInstruction.getFolio().getFolioWindowNo();
            var instructions = routingInstruction.getFolio().getInstructions();
            Flux.fromIterable(instructions).flatMap(instruction -> {
              var routingParams =
                  createRoutingInstructionMap(payeeId, folioWindowNo, instruction);
              log.info(
                  "deleteRoutingInstruction: sending delete request for routing instructions map {}",
                  routingParams);
              return ohipReservationClient.deleteRoutingInstruction(hotelId, reservationId,
                  routingParams);
            }).collectList().block();
          });
        }
      }
      deleteBusinessNotes(hotelId, reservationId, reservation);
    });
  }

  @Override
  public MemosResponse createMemo(CreateMemoRequest createMemoRequest) {
    final List<String> reservationsId = createMemoRequest.getReservationIds();

    Flux.fromIterable(reservationsId).flatMap(reservationId ->
        ohipReservationClient.sendChangeReservationRequest(createMemoRequest.getHotelId(),
            reservationId,
            mapToChangeReservation(reservationId, createMemoRequest))
    ).collectList().block();

    var reservationsWithComments = Flux.fromIterable(reservationsId)
        .flatMap(reservationId ->
            Mono.just(ohipReservationClient.getReservationWithRoutingInstructions(
                createMemoRequest.getHotelId(),
                reservationId)))
        .collectList().block();

    return memosOhipMapper.toModel(reservationsWithComments);
  }

  @Override
  public MemosResponse getMemos(String hotelId, Set<String> reservationIds) {

    var reservationsWithComments = Flux.fromIterable(reservationIds)
        .flatMap(reservationId ->
            Mono.just(ohipReservationClient.getReservationWithRoutingInstructions(hotelId,
                reservationId)))
        .collectList().block();

    return memosOhipMapper.toModel(reservationsWithComments);
  }

  @Override
  public void attachProfileToReservations(
      AttachReservationProfileRequest attachReservationProfileRequest) {
    var company = getCompanyProfileById(attachReservationProfileRequest.getProfileId());
    if (company == null) {
      var message = String.format(
          "Error while trying to attach profile %s",
          attachReservationProfileRequest.getProfileId());
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_ATTACH_PROFILE_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    Flux.fromIterable(attachReservationProfileRequest.getReservationIds())
        .flatMap(reservationId ->
            ohipReservationClient.sendPutReservationsGuestRequest(
                attachReservationProfileRequest.getHotelId(), reservationId,
                attachReservationProfileRequestOhipMapper.toDto(reservationId,
                    company.getProfileId().getId()))
        )
        .collectList()
        .block();
  }

  @Override
  public ConfirmReservationResponse updateReservationSingleCall(
      BusinessItemsRequest businessItemsRequest,
      SpecialRequests specialRequests, ReservationGuestRequest guestReservationRequest,
      ReservationPackagesRequest updateReservationPackageRequest,
      ConfirmReservationRequest confirmReservationRequest, boolean shouldUpdatePackages) {

    log.debug("Entered updateReservationSingleCall for the reservationId = {}",
        guestReservationRequest.getStayingGuests().get(0).getReservationId());


    StayingGuest stayingGuest = guestReservationRequest.getStayingGuests().get(0);
    var reservation = ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
            stayingGuest.getReservationId());
    if (reservation.block() != null && !stayingGuest.getSameAsBooker()) {
      if (reservation.block().getReservations() != null) {
        List<UniqueIDType> profileId = reservation.block().getReservations().getReservation()
                .get(0).getReservationGuests().get(0).getProfileInfo().getProfileIdList();
        Set<String> s = Collections.singleton(profileId.get(0).getId());
        Map<String, Profile> profilesByIds = getProfilesMap(s);
        Profile tempProfile = profilesByIds.values().stream().findFirst().orElse(null);
        //reuse TEMP profile for guest
        if (tempProfile != null) {
          var guestProfile = mapToCreateProfileGuestOhip(
                  stayingGuest.getStayingGuestDetails(), stayingGuest.getLanguage());
          guestProfile.setProfileIdList(tempProfile.getProfileIdList());

          ohipReservationClient.sendUpdateProfileRequest(
                  guestReservationRequest.getHotelId(),
                  guestProfile);
          guestReservationRequest.getStayingGuests().get(0).getStayingGuestDetails()
                  .setProfileId(profileId.get(0).getId());
        }
      }
    }
    int folioWindowNo = PaymentUtils.resolveDistrFolioWindow(
            confirmReservationRequest.getPibaCardPresent());

    ConfirmReservationResponse response;
    var changeGuestReservations = getChangeGuestReservationRequest(guestReservationRequest);
    var changePackageReservations = getChangePackageReservationRequest(
        updateReservationPackageRequest, shouldUpdatePackages);
    var changeSpecialRequestRes = getChangeSpecialRequestDetails(specialRequests);
    var changePaymentReqReservations = getChangePaymentReqReservation(confirmReservationRequest);
    var changeBusinessItemsResReq = getChangeBusinessItemResReq(businessItemsRequest, reservation,
        changeGuestReservations, folioWindowNo);

    //Logic to frame single PUT call request to OPERA
    var updateReservationRequest = getFinalUpdateReservationRequest(changeGuestReservations,
        changePackageReservations,
        changeSpecialRequestRes, changeBusinessItemsResReq, changePaymentReqReservations,
        confirmReservationRequest.getHotelId(), confirmReservationRequest.getReservationId());
    var reservationsDetailsOhip =
        ohipReservationClient.sendChangeReservationRequest(
            confirmReservationRequest.getHotelId(),
            confirmReservationRequest.getReservationId(), updateReservationRequest).block();
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getDistributionBookingFee())
        && validateBookingFeePackages(updateReservationPackageRequest)) {
      updateReservationPackages(updateReservationPackageRequest);
    }
    Objects.requireNonNull(reservationsDetailsOhip);
    response = confirmationResponseOhipMapper.toConfirmReservationResponseModel(
        reservationsDetailsOhip.getReservations());

    if (isCnpReservation(confirmReservationRequest.getHotelId(),
        confirmReservationRequest.getReservationId())) {
      movePaymentDetails(confirmReservationRequest.getHotelId(),
          Set.of(confirmReservationRequest.getReservationId()));
    }
    return response;
  }

  private boolean validateBookingFeePackages(ReservationPackagesRequest updateReservationPackageRequest) {
    return nonNull(updateReservationPackageRequest)
        && nonNull(updateReservationPackageRequest.getRoomsSelections())
        && updateReservationPackageRequest.getRoomsSelections().stream()
        .filter(Objects::nonNull)
        .map(RoomsSelections::getPackagesSelection)
        .filter(CollectionUtils::isNotEmpty)
        .flatMap(Collection::stream)
        .filter(Objects::nonNull)
        .map(PackagesSelection::getId)
        .filter(Objects::nonNull)
        .anyMatch(packageId -> packageId.startsWith("ZN") || packageId.startsWith("ZR"));
  }


  private ChangeReservation getChangeReservation(List<ChangeReservation> changeReservations, String reservationId) {
    ChangeReservation changeReservation = null;
    if (nonNull(changeReservations)) {
      var temp = changeReservations.stream().filter(item ->
              item.getReservations().get(0).getReservationIdList().get(0).getId()
                      .equalsIgnoreCase(reservationId)).findFirst();
      if (temp.isPresent()) {
        changeReservation = temp.get();
      }
    }
    return changeReservation;
  }

  public ReservationByBasketRefResponse confirmAmendSingleCall(
          List<SpecialRequests> specialRequests, BookerDetailsCnpRequest bookerDetailsCnpRequest,
          List<ChangeReservation> stayDateRequest,
          List<List<ChangeReservation>> editRoomRequest,
          BusinessItemsRequest bookingAllowanceRequest,
          ReservationPackagesRequest updatePackageRequest, boolean shouldUpdatePackage
  ) {
    // collecting change reservations for special requests
    List<ChangeReservation> specialRequestChangeReservations = null;
    if (nonNull(specialRequests)) {
      specialRequestChangeReservations = new ArrayList<>();
      List<ChangeReservation> finalSpecialRequestChangeReservations = specialRequestChangeReservations;
      specialRequests.forEach(specialRequest -> {
        ChangeReservation specialRequestChangeReservation = getSpecialRequestChangeReservation(specialRequest);
        finalSpecialRequestChangeReservations.add(specialRequestChangeReservation);
      });
    }

    // collecting change reservations for booking allowances
    List<ChangeReservation> bookingAllowanceChangeReservation = null;
    if (nonNull(bookingAllowanceRequest)) {
      bookingAllowanceChangeReservation = getBookingAllowanceChangeReservation(bookingAllowanceRequest);
    }

    if (nonNull(bookerDetailsCnpRequest)) {
      updateBookerDetails(bookerDetailsCnpRequest);
    }

    // collecting change reservations for updatePackage
    List<ChangeReservation> updatePackageChangeReservation = null;
    if (nonNull(updatePackageRequest) && shouldUpdatePackage) {
      updatePackageChangeReservation = getUpdatePackageChangeReservation(updatePackageRequest);
    }

    List<String> reservationIds = getReservationIdsToChange(stayDateRequest, editRoomRequest,
        updatePackageRequest);

    String hotelId = getHotelIdForAmend(stayDateRequest, editRoomRequest, updatePackageRequest);

    updateRequestForAmendSingleCall(
        stayDateRequest, editRoomRequest, reservationIds, updatePackageChangeReservation,
        specialRequestChangeReservations, bookingAllowanceChangeReservation, hotelId);

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getDistributionBookingFee())
        && shouldUpdatePackage
        && validateBookingFeePackages(updatePackageRequest)) {
      updateReservationPackages(updatePackageRequest);
    }

    return getReservationsByIds(hotelId, Set.copyOf(reservationIds), false, true, false);

  }

  private static String getHotelIdForAmend(List<ChangeReservation> stayDateRequest,
      List<List<ChangeReservation>> editRoomRequest,
      ReservationPackagesRequest updatePackageRequest) {
    if (CollectionUtils.isNotEmpty(editRoomRequest)) {
      return editRoomRequest.stream().findFirst().orElse(Collections.emptyList())
          .stream().findFirst().map(ChangeReservation::getReservations)
          .orElse(Collections.emptyList()).stream().findFirst().map(HotelReservationInstructionType::getHotelId)
          .orElse(nonNull(updatePackageRequest) ? updatePackageRequest.getHotelId() : "");
    }

    return nonNull(updatePackageRequest)
            ? updatePackageRequest.getHotelId()
            : stayDateRequest.stream().findFirst().map(ChangeReservation::getReservations)
                .orElse(Collections.emptyList()).stream().findFirst()
                .map(HotelReservationInstructionType::getHotelId).orElse("");
  }

  private static List<String> getReservationIdsToChange(List<ChangeReservation> stayDateRequest,
      List<List<ChangeReservation>> editRoomRequest,
      ReservationPackagesRequest updatePackageRequest) {

    if (nonNull(updatePackageRequest)) {
      return updatePackageRequest.getReservationsId();
    }

    if (CollectionUtils.isNotEmpty(editRoomRequest)) {
      return editRoomRequest.stream().map(item ->
          item.stream().findFirst().map(ChangeReservation::getReservations).orElse(Collections.emptyList())
              .stream().findFirst().map(HotelReservationInstructionType::getReservationIdList)
              .orElse(Collections.emptyList())
              .stream().findFirst().map(UniqueIDType::getId).orElse("")).toList();
    }

    return stayDateRequest.stream().map(item ->
              item.getReservations().stream().findFirst().map(HotelReservationInstructionType::getReservationIdList)
                  .orElse(Collections.emptyList())
                  .stream().findFirst().map(UniqueIDType::getId).orElse("")).toList();
  }

  private void updateRequestForAmendSingleCall(List<ChangeReservation> stayDateRequest,
      List<List<ChangeReservation>> editRoomRequest, List<String> reservationIds,
      List<ChangeReservation> updatePackageChangeReservation,
      List<ChangeReservation> specialRequestChangeReservations,
      List<ChangeReservation> bookingAllowanceChangeReservation, String hotelId) {
    reservationIds.forEach(reservationId -> {
      ChangeReservation stayDateChangeReservation = getChangeReservation(
          stayDateRequest, reservationId);

      ChangeReservation editRoomChangeReservation = CollectionUtils.isNotEmpty(editRoomRequest)
          ? editRoomRequest.stream()
          .map(editRoom -> getChangeReservation(editRoom, reservationId))
          .filter(Objects::nonNull)
          .findFirst()
          .orElse(null)
          : null;

      ChangeReservation packageChangeReservation = getChangeReservation(
          updatePackageChangeReservation, reservationId);

      ChangeReservation specialRequestChangeReservation = getChangeReservation(
          specialRequestChangeReservations, reservationId);

      ChangeReservation bookingChangeReservation = getChangeReservation(
          bookingAllowanceChangeReservation, reservationId);

      ChangeReservation finalConfirmAmendRequestForSingleCall = getFinalConfirmAmendRequestForSingleCall(
              stayDateChangeReservation,
              editRoomChangeReservation,
              packageChangeReservation,
              specialRequestChangeReservation,
              bookingChangeReservation);

      log.debug(String.valueOf(finalConfirmAmendRequestForSingleCall));

      ChangeReservationDetails block = ohipReservationClient.sendChangeReservationRequest(
              hotelId, reservationId, finalConfirmAmendRequestForSingleCall).block();

      log.info("SUCCESS -> {}", block);

    });
  }

  private ChangeReservation getChangeBusinessItemResReq(BusinessItemsRequest businessItemsRequest,
      Mono<uk.co.whitbread.hotel.ohip.adapter.generated.models
          .Reservation> reservation,
      ChangeReservation changeGuestReservations,
      int folioWindowNo) {
    //Logic for BusinessItems
    ChangeReservation changeBusinessItemsResReq = null;
    if (nonNull(businessItemsRequest)) {
      var businessItems = businessItemsRequest.getBusinessItems();
      var reservations = List.of(reservation.block());
      if (nonNull(businessItems)) {
        //map missing fields for businessItems
        mapMissingFieldsForBusinessItems(businessItems, reservations);
        var businessAllowances = rulesAgentClient.getBusinessAllowances().getBusinessAllowances();
        changeBusinessItemsResReq = mapToChangeReservationBusinessDistr(changeGuestReservations,
            businessItemsRequest,
            reservations.get(0),
            businessAllowances);
        log.debug(
            "Business Allowances to be updated for Reservation = {} and request = {}",
            businessItemsRequest.getReservationIds().toString(), changeBusinessItemsResReq);
      } else if (DISTRIBUTION_CHANNEL.equals(businessItemsRequest.getChannel())) {
        changeBusinessItemsResReq = mapToChangeReservationDistr(
            changeGuestReservations,
            reservations.get(0).getReservations().getReservation().get(0),
            folioWindowNo);
        log.debug(
            "Business Allowances to be updated for Reservation = {} and request = {}",
            businessItemsRequest.getReservationIds().toString(), changeBusinessItemsResReq);
      }
    }
    return changeBusinessItemsResReq;
  }

  private ChangeReservation getChangePaymentReqReservation(
      ConfirmReservationRequest confirmReservationRequest) {
    //Logic for Paymentdetails
    ChangeReservation changePaymentReqReservations = null;
    try {
      changePaymentReqReservations = getPaymentUpdateRequest(confirmReservationRequest);
      log.debug("Payment Requests to be updated for Reservation = {} and request = {}",
          confirmReservationRequest.getReservationId(), changePaymentReqReservations);
    } catch (RuntimeException error) {
      var message = String.format(
          "Error while trying to update reservation paymentDetails for hotelId=%s and "
              + "reservationId=%s and request=%s",
          confirmReservationRequest.getHotelId(),
          confirmReservationRequest.getReservationId());
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_UPDATE_RESERVATION_PAY_EXCEPTION, message, error);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return changePaymentReqReservations;
  }

  private ChangeReservation getChangeSpecialRequestDetails(SpecialRequests specialRequests) {
    //Logic for SpecialRequest
    ChangeReservation changeSpecialRequestRes = null;
    if (nonNull(specialRequests)) {
      try {
        changeSpecialRequestRes = getSpecialReqUpdateRequest(specialRequests);
        log.debug(
            "SpecialRequests and BookingNotes to be updated for Reservation = {} and request = {}",
            specialRequests.getReservationIds().get(0), changeSpecialRequestRes);
      } catch (RuntimeException error) {
        var message = String.format(
            "Error while trying to update reservation specialRequests for hotelId=%s and "
                + "reservationId=%s and specialRequest=%s and bookingNotes=%s",
            specialRequests.getHotelId(),
            specialRequests.getReservationIds().get(0),
            specialRequests.getBookingNotes(),
            specialRequests.getSpecialRequests());
        var exception = new HotelReservationException(ErrorCode.DIGITAL_UPDATE_SERVAION_REQ,
            message, error);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
    return changeSpecialRequestRes;
  }

  private ChangeReservation getChangePackageReservationRequest(
      ReservationPackagesRequest updateReservationPackageRequest,
      boolean shouldUpdatePackages) {
    //Logic for updating Package details
    ChangeReservation changePackageReservations = null;
    if (shouldUpdatePackages) {
      changePackageReservations = getPackagesUpdateRequest(updateReservationPackageRequest);
      log.debug("Package Details to be updated for Reservation = {} and request = {}",
          updateReservationPackageRequest.getReservationsId().get(0),
          changePackageReservations);
    }
    return changePackageReservations;
  }

  private ChangeReservation getChangeGuestReservationRequest(
      ReservationGuestRequest guestReservationRequest) {
    //Logic for updating Guest and Booker Details
    ChangeReservation changeGuestReservations = null;
    if (nonNull(guestReservationRequest) && nonNull(guestReservationRequest.getStayingGuests())) {
      try {
        changeGuestReservations = getGuestBookerUpdateRequest(guestReservationRequest);
        log.debug("Guest/Booker Details to be updated for Reservation = {} and request = {}",
            guestReservationRequest.getStayingGuests().get(0).getReservationId(),
            changeGuestReservations);
      } catch (RuntimeException error) {
        var message = String.format(
            "Error while trying to create reservation guest for hotelId=%s and %s guests",
            guestReservationRequest.getHotelId(),
            (guestReservationRequest.getStayingGuests() == null ? "null" :
                guestReservationRequest.getStayingGuests().size()));
        var exception = new HotelReservationException(
            ErrorCode.DIGITAL_CREATE_RESERVATION_GUEST_EXCEPTION, message, error);
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }
    return changeGuestReservations;
  }

  @Override
  public ReservationProfiles createProfiles(ReservationGuestRequest guestReservationRequest) {
    Status reservationCompanyResponse = null;
    if (StringUtils.isNotEmpty(
        guestReservationRequest.getBooker().getAddress().getCompanyName())) {
      reservationCompanyResponse = getCompanyProfileResponse(guestReservationRequest);
    }
    var finalGuestReservationRequest = setLanguageForBooker(guestReservationRequest);
    var reservationBookerResponse = ohipReservationClient.sendPostProfileRequest(
        getHotelId(finalGuestReservationRequest),
        reservationBookerRequestOhipMapper.toDto(finalGuestReservationRequest.getBooker()));

    var bookerProfileId = getProfileId(reservationBookerResponse);
    var companyProfileId = getProfileId(reservationCompanyResponse);
    log.debug("ProfileIds created :: bookerProfileId={}, companyProfileId={}",
        bookerProfileId, companyProfileId);
    return ReservationProfiles.builder().bookerProfileId(bookerProfileId)
        .companyProfileId(companyProfileId).build();
  }

  @Override
  public PreCheckInResponse addAttachmentToReservation(ReservationFileAttachmentRequest request) {
    log.info("Received request to add attachment to reservation: {}", request.getReservationId());
    byte[] fileAttachmentBytes = decodeBase64(request.getFileAttachment());
    if (ObjectUtils.isEmpty(fileAttachmentBytes)) {
      return buildPreCheckInResponse(false, "",
          "File attachment is not a valid base64 string");
    }
    if (fileAttachmentBytes.length > MAX_FILE_SIZE_BYTES) {
      return buildPreCheckInResponse(false, "",
          "File size exceeds the maximum limit of 10MB");
    }
    if (!isPdfFile(fileAttachmentBytes)) {
      return buildPreCheckInResponse(false, "",
          "File is not a valid PDF");
    }
    var fileToUpload = getFileToUploadRequest(request, fileAttachmentBytes);

    String savePdfResponse = ohipReservationClient.addAttachmentToReservation(fileToUpload);
    return buildPreCheckInResponse(SUCCESS.equals(savePdfResponse), "Attachment added successfully",
        "Error in adding attachment");
  }

  private FileToUpload getFileToUploadRequest(ReservationFileAttachmentRequest request,
      byte[] fileAttachmentBytes) {
    var fileToUpload = new FileToUpload();
    fileToUpload.setFileAttachment(fileAttachmentBytes);
    fileToUpload.setHotelId(request.getHotelId());
    fileToUpload.setFileName(request.getFileName());
    fileToUpload.setDescription(request.getDescription());
    fileToUpload.setGlobalYN(request.getGlobal() != null && request.getGlobal() ? "Y" : "N");
    fileToUpload.setLinkId(request.getReservationId());
    fileToUpload.setLinkType(RESERVATION);
    fileToUpload.setOverwriteExistingFileYN(
        request.getOverwriteExistingFile() != null && request.getOverwriteExistingFile() ? "Y"
            : "N");
    fileToUpload.setUserName(ohipProperties.getUsername());
    return fileToUpload;
  }

  private byte[] decodeBase64(String base64String) {
    try {
      log.debug("Entered decodeBase64");
      return Base64.getDecoder().decode(base64String);
    } catch (IllegalArgumentException e) {
      log.error("Error while trying to decode Base64 string", e);
      return new byte[0];
    }
  }

  private boolean isPdfFile(byte[] fileBytes) {
    log.debug("Entered isPdfFile");
    if (fileBytes == null || fileBytes.length == 0) {
      return false;
    }
    try (PDDocument document = Loader.loadPDF(fileBytes)) {
      PDDocumentInformation info = document.getDocumentInformation();
      log.debug("PDF details {}", info);
      return true;
    } catch (IOException e) {
      log.error("Error loading PDF: {}", e.getMessage());
      return false;
    }
  }

  @Override
  public PreCheckInResponse saveReservationPreCheckIn(PreCheckInRequest preCheckInRequest) {
    log.debug("Entered saveReservationPreCheckIn for {}", preCheckInRequest);
    PreCheckInReservation preCheckInReservation = buildPreCheckInRequest(preCheckInRequest.getArrivalTime(),
            preCheckInRequest.getHotelId());

    boolean isSuccess;
    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getMobilePreRegisteredRepurpose())) {
      log.info("mobile_preRegistered_repurpose enabled - Opera preCheckIn call for hotelId={}, reservationId={}",
              preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId());
      isSuccess = true;
    } else {
      log.info("mobile_preRegistered_repurpose disabled - Opera preCheckIn call for hotelId={}, reservationId={}",
              preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId());
      var preCheckInResponse = ohipReservationClient.savePreCheckInStatus(preCheckInReservation,
              preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId());
      isSuccess = ObjectUtils.isNotEmpty(preCheckInResponse) && ObjectUtils.isNotEmpty(preCheckInResponse.getLinks())
              && !preCheckInResponse.getLinks().isEmpty();
    }

    if (isSuccess) {
      log.info("isSuccess_true : setting alert for hotelId={}, reservationId={}", preCheckInRequest.getHotelId(),
              preCheckInRequest.getReservationId());
      String alertDescription = RESERVATION_CHECKIN_ALERT_MSG_EN;
      if (StringUtils.isNotEmpty(preCheckInRequest.getLanguage())
          && preCheckInRequest.getLanguage().equals(LANGUAGE_DE)) {
        alertDescription = RESERVATION_CHECKIN_ALERT_MSG_DE;
      }
      ohipReservationClient.sendPutReservationsGuestRequest(preCheckInRequest.getHotelId(),
          preCheckInRequest.getReservationId(),
          reservationAccompanyingGuestRequestOhipMapper.toAddOrDeleteAlertModel(
              preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId(),
              alertDescription, null)).block();
      log.info("Successfully added pre-checkIn reservation alert for language={}",
          preCheckInRequest.getLanguage());
    }

    return buildPreCheckInResponse(isSuccess, "Pre-CheckIn status saved successfully",
        "Error in saving Pre-CheckIn status");
  }

  @Override
  public void deleteReservationPreCheckIn(String hotelId, String reservationId) {
    log.info("Entered deleteReservationPreCheckIn for hotelId = {} and reservationId = {}",
        sanitizeInput(hotelId), sanitizeInput(reservationId));

    ohipReservationClient.deleteReservationPreCheckIn(hotelId, reservationId);
    log.info("Successfully updated pre-checkIn status for hotelId = {} and reservationId = {}",
        sanitizeInput(hotelId), sanitizeInput(reservationId));
  }

  @Override
  public void deleteRegCardAttachment(String hotelId, String reservationId) {
    log.info("Entered deleteRegCardAttachment for hotelId = {} and reservationId = {}",
        sanitizeInput(hotelId), sanitizeInput(reservationId));
    var reservationDetails = ohipReservationClient.sendGetReservationsByReservationId(hotelId,
        reservationId);
    if (ObjectUtils.isEmpty(reservationDetails)) {
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_CREATE_RESERVATION_GUEST_EXCEPTION, "Reservation attachment not found");
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    if (isPreCheckedIn(reservationDetails)) {
      var attachmentId = getAttachmentId(reservationDetails);

      if (attachmentId != null) {
        ohipReservationClient.deleteReservationAttachment(hotelId, reservationId, attachmentId);
        log.info(
            "Successfully deleted attachment with id {} for hotelId = {} and reservationId = {}",
            attachmentId, sanitizeInput(hotelId), sanitizeInput(reservationId));
      } else {
        log.warn(
            "No attachment with prefix 'REG_RES' found for hotelId = {} and reservationId = {}",
            sanitizeInput(hotelId), sanitizeInput(reservationId));
      }

      deletePreCheckInAlert(hotelId, reservationId, reservationDetails);
    }
  }

  @Override
  public void linkReservationToLeisureCustomer(
      LinkReservationToLeisureCustomerRequest linkReservationToLeisureCustomerRequest) {
    var hotelId = linkReservationToLeisureCustomerRequest.getHotelId();
    var reservationIds = linkReservationToLeisureCustomerRequest.getReservationIds();
    log.debug("Entered linkReservationToLeisureCustomer for hotelId={} and reservationIds={}",
        hotelId,
        String.join(", ", reservationIds));
    ChangeReservation changeReservationRequest = linkReservationToLeisureCustomerRequestOhipMapper.toDto(
        linkReservationToLeisureCustomerRequest);
    Flux.fromIterable(reservationIds)
        .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                hotelId, reservationId, changeReservationRequest),
            reservationOhipProperties.getMaxConcurrency())
        .collectList()
        .block();
  }

  @Override
  public void updateReservationPreferences(ReservationPreferencesRequest reservationPreferencesRequest) {
    String hotelId = reservationPreferencesRequest.getHotelId();
    List<String> reservationsIds = reservationPreferencesRequest.getReservationsIds();
    log.info("Entered updateReservationPreferences for hotelId = {} and reservationsIds = {}, "
            + "preferences = {}", hotelId, reservationsIds,
        reservationPreferencesRequest.getPreferencesCollections());
    Flux.fromIterable(reservationsIds)
        .flatMap(reservationId -> {
          ChangeReservation changeReservation = updatePreferencesRequestOhipMapper
              .toDto(reservationPreferencesRequest, reservationId);
          return ohipReservationClient.sendChangeReservationRequest(hotelId, reservationId, changeReservation);
        })
        .collectList()
        .block();
    log.info("Updated reservation preferences for hotelId = {}, reservationsIds = {}", hotelId, reservationsIds);
  }

  private boolean isPreCheckedIn(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationDetails) {
    return Optional.ofNullable(reservationDetails)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation::getReservations)
        .map(HotelReservationsType::getReservation)
        .filter(reservations -> !reservations.isEmpty())
        .flatMap(reservations -> reservations.stream().findFirst())
        .map(HotelReservationType::getPreRegistered)
        .orElse(false);
  }

  private String getAttachmentId(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationDetails) {
    return Optional.ofNullable(reservationDetails)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation::getReservations)
        .map(HotelReservationsType::getReservation)
        .filter(reservations -> !reservations.isEmpty())
        .flatMap(reservations -> reservations.stream().findFirst())
        .map(HotelReservationType::getAttachments)
        .filter(attachments -> !attachments.isEmpty())
        .flatMap(attachments -> attachments.stream()
            .filter(attachment -> attachment.getFileName().startsWith(REG_RES_PREFIX))
            .findFirst())
        .map(AttachmentType::getId)
        .orElse(null);
  }

  private void deletePreCheckInAlert(String hotelId, String reservationId,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationDetails) {
    log.info("Entered deletePreCheckInAlert for hotelId = {} and reservationId = {}",
        sanitizeInput(hotelId), sanitizeInput(reservationId));

    String alertId = getAlertId(reservationDetails);

    if (StringUtils.isNotEmpty(alertId)) {
      ohipReservationClient.sendPutReservationsGuestRequest(hotelId, reservationId,
          reservationAccompanyingGuestRequestOhipMapper.toAddOrDeleteAlertModel(hotelId,
              reservationId, null, alertId)).block();
      log.info(
          "Successfully deleted pre-checkIn alert for hotelId = {}, reservationId = {} and alertId = {}",
          sanitizeInput(hotelId), sanitizeInput(reservationId), alertId);
    }
  }

  private static String getAlertId(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationDetails) {
    return Optional.ofNullable(reservationDetails)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation::getReservations)
        .map(HotelReservationsType::getReservation)
        .filter(reservations -> !reservations.isEmpty())
        .flatMap(reservations -> reservations.stream().findFirst())
        .map(HotelReservationType::getAlerts)
        .filter(alertTypes -> !alertTypes.isEmpty())
        .flatMap(alertTypes -> alertTypes.stream()
            .filter(alert -> alert.getCode().equalsIgnoreCase(RESERVATION)
                && alert.getDescription().contains(PRE_CHECK_IN))
            .findFirst())
        .map(AlertType::getId)
        .orElse(null);
  }

  private PreCheckInResponse buildPreCheckInResponse(boolean isSuccess, String successMessage,
      String errorMessage) {
    return PreCheckInResponse.builder()
        .status(isSuccess ? SUCCESS : ERROR)
        .message(isSuccess ? successMessage : errorMessage)
        .build();
  }

  private ChangeReservation mapToChangeReservationBusinessDistr(
      ChangeReservation changeGuestReservations, BusinessItemsRequest businessItemsRequest,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation,
      List<BusinessAllowanceRuleDto> businessAllowances) {
    var reservationDetails = reservation.getReservations().getReservation();
    reservationDetails.get(0).setReservationProfiles(
        changeGuestReservations.getReservations().get(0).getReservationProfiles());
    return businessItemsRequestOhipMapper.toChangeReservationDto(businessItemsRequest,
        reservationDetails.get(0), businessAllowances);
  }

  private ChangeReservation mapToChangeReservationDistr(ChangeReservation changeGuestReservations,
      HotelReservationType hotelReservationType, int folioWindowNo) {
    hotelReservationType.setReservationProfiles(
        changeGuestReservations.getReservations().get(0).getReservationProfiles());
    return businessItemsRequestOhipMapper.toChangeReservationDto(hotelReservationType, null,
            folioWindowNo);
  }

  private ChangeReservation getFinalConfirmAmendRequestForSingleCall(
          ChangeReservation stayDateChangeReservation,
          ChangeReservation editRoomChangeReservation,
          ChangeReservation packageChangeReservation,
          ChangeReservation changeSpecialRequestRes,
          ChangeReservation changeBusinessItemsResReq
  ) {
    log.debug("Entered getFinalConfirmAmendRequestForSingleCall for Single PUT call");

    ChangeReservation changeFinalReservationReq;
    changeFinalReservationReq = new ChangeReservation();
    HotelReservationInstructionType hotelReservationInstructionType =
            new HotelReservationInstructionType();

    var reservation = getReservationToChange(stayDateChangeReservation, editRoomChangeReservation,
        packageChangeReservation);

    if (reservation.isPresent()) {
      HotelReservationInstructionType res = reservation.get();
      res.getReservationIdList().get(0).setType(RESERVATION);
      hotelReservationInstructionType.setReservationIdList(res.getReservationIdList());

      var roomStayToChange = Optional.ofNullable(res.getRoomStay()).orElse(new RoomStayType());
      hotelReservationInstructionType.setRoomStay(roomStayToChange);

      hotelReservationInstructionType.getRoomStay().setGuestCounts(res.getRoomStay()
              .getGuestCounts());
      hotelReservationInstructionType.setHotelId(res.getHotelId());
      hotelReservationInstructionType.setReservationGuests(res.getReservationGuests());
      hotelReservationInstructionType.setOverrideInventoryCheck(false);
    }

    if (nonNull(packageChangeReservation)) {
      hotelReservationInstructionType.setReservationPackages(
              packageChangeReservation.getReservations().get(0).getReservationPackages());
    }

    List<CommentInfoType> commentInfoTypes = new ArrayList<>();

    if (nonNull(changeSpecialRequestRes)
            && nonNull(changeSpecialRequestRes.getReservations().get(0).getComments())) {
      hotelReservationInstructionType.setPreferenceCollection(
              changeSpecialRequestRes.getReservations().get(0).getPreferenceCollection());
      commentInfoTypes.addAll(changeSpecialRequestRes.getReservations().get(0).getComments());
    }

    if (nonNull(changeBusinessItemsResReq)
            && !changeBusinessItemsResReq.getReservations().get(0).getComments().isEmpty()) {
      getRoutingInstructions(changeBusinessItemsResReq, commentInfoTypes, hotelReservationInstructionType);
    }

    hotelReservationInstructionType.setComments(commentInfoTypes);

    changeFinalReservationReq.setReservations(List.of(hotelReservationInstructionType));
    return changeFinalReservationReq;

  }

  private Optional<HotelReservationInstructionType> getReservationToChange(
      ChangeReservation stayDateChangeReservation, ChangeReservation editRoomChangeReservation,
      ChangeReservation packageChangeReservation) {

    return Optional.ofNullable(editRoomChangeReservation)
            .or(() -> Optional.ofNullable(stayDateChangeReservation))
            .or(() -> Optional.ofNullable(packageChangeReservation))
            .map(ChangeReservation::getReservations)
            .flatMap(reservations -> reservations.stream().findFirst());
  }

  private static void getRoutingInstructions(ChangeReservation changeBusinessItemsResReq,
      List<CommentInfoType> commentInfoTypes, HotelReservationInstructionType hotelReservationInstructionType) {
    commentInfoTypes.add(
            changeBusinessItemsResReq.getReservations().get(0).getComments().get(0));
    hotelReservationInstructionType.setRoutingInstructions(
            changeBusinessItemsResReq.getReservations().get(0).getRoutingInstructions());
    hotelReservationInstructionType.setRoutingInstructions(
            changeBusinessItemsResReq.getReservations().get(0).getRoutingInstructions());
  }

  private ChangeReservation getFinalUpdateReservationRequest(
      ChangeReservation changeGuestReservations,
      ChangeReservation changePackageReservations,
      ChangeReservation changeSpecialRequestRes,
      ChangeReservation changeBusinessItemsResReq,
      ChangeReservation changePaymentReqReservations,
      String hotelId, String reservationId) {
    log.debug("Entered getFinalUpdateReservationRequest for Single PUT call");

    ChangeReservation changeFinalReservationReq = new ChangeReservation();
    HotelReservationInstructionType hotelReservationInstructionType =
        new HotelReservationInstructionType();
    try {
      hotelReservationInstructionType.setReservationIdList(
          changePaymentReqReservations.getReservations().get(0).getReservationIdList());
      hotelReservationInstructionType.setReservationGuests(
          changeGuestReservations.getReservations().get(0).getReservationGuests());
      hotelReservationInstructionType.setAdditionalGuestInfo(
          changeGuestReservations.getReservations().get(0).getAdditionalGuestInfo());
      hotelReservationInstructionType.setReservationProfiles(
          changeGuestReservations.getReservations().get(0).getReservationProfiles());

      UserDefinedFieldsType userDefinedFields =
              Optional.ofNullable(changeGuestReservations.getReservations().get(0).getUserDefinedFields())
                      .orElse(new UserDefinedFieldsType());
      if (userDefinedFields.getCharacterUDFs() == null) {
        userDefinedFields.setCharacterUDFs(new ArrayList<>());
      }

      var businessReservations = changeBusinessItemsResReq != null
              ? changeBusinessItemsResReq.getReservations() : null;

      if (CollectionUtils.isNotEmpty(businessReservations)) {
        var businessUserDefinedFields = businessReservations.get(0).getUserDefinedFields();

        if (businessUserDefinedFields != null
                && businessUserDefinedFields.getCharacterUDFs() != null
                && !businessUserDefinedFields.getCharacterUDFs().isEmpty()) {

          var businessCharacterUdfs = businessUserDefinedFields.getCharacterUDFs();

          businessCharacterUdfs.stream()
              .filter(udf -> UDFC_PURCHASE_ORDER_NAME.equals(udf.getName()))
              .findFirst()
              .ifPresent(udf -> userDefinedFields.addCharacterUDFsItem(
                  createCharacterUdf(udf.getName(), udf.getValue())));
        }

        hotelReservationInstructionType.setCustomReference(businessReservations.get(0).getCustomReference());
      }

      hotelReservationInstructionType.setUserDefinedFields(userDefinedFields);

      if (nonNull(changePackageReservations)) {
        hotelReservationInstructionType.setReservationPackages(
            changePackageReservations.getReservations().get(0).getReservationPackages());
      }

      List<CommentInfoType> commentInfoTypes = new ArrayList<>();

      if (nonNull(changeSpecialRequestRes)
          && nonNull(changeSpecialRequestRes.getReservations().get(0).getComments())) {
        hotelReservationInstructionType.setPreferenceCollection(
            changeSpecialRequestRes.getReservations().get(0).getPreferenceCollection());
        changeSpecialRequestRes.getReservations().get(0).getComments().forEach(comments ->
            commentInfoTypes.add(comments));
      }
      if (nonNull(changeBusinessItemsResReq)
          && changeBusinessItemsResReq.getReservations().get(0).getComments().size() > 0) {
        commentInfoTypes.add(
            changeBusinessItemsResReq.getReservations().get(0).getComments().get(0));
        hotelReservationInstructionType.setRoutingInstructions(
            changeBusinessItemsResReq.getReservations().get(0).getRoutingInstructions());
      }

      hotelReservationInstructionType.setComments(commentInfoTypes);
      hotelReservationInstructionType.setReservationPaymentMethods(
          changePaymentReqReservations.getReservations().get(0).getReservationPaymentMethods());
      hotelReservationInstructionType.setRoomStay(
          changePaymentReqReservations.getReservations().get(0).getRoomStay());
      hotelReservationInstructionType.setHotelId(
          changePaymentReqReservations.getReservations().get(0).getHotelId());
      hotelReservationInstructionType.setRoutingInstructions(
          changeBusinessItemsResReq.getReservations().get(0).getRoutingInstructions());
      changeFinalReservationReq.setReservations(List.of(hotelReservationInstructionType));
    } catch (RuntimeException error) {
      var message = String.format(
          "Error while trying to create request to OPERA for reservation and hotelId=%s and reservationId=%s",
          reservationId,
          hotelId);
      var exception = new HotelReservationException(ErrorCode.CREATE_OPERA_REQUEST_EXCPETION,
          message, error);
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return changeFinalReservationReq;
  }

  private CharacterUDFType createCharacterUdf(String name, String value) {
    final var characterUDF = new CharacterUDFType();
    characterUDF.setName(name);
    characterUDF.setValue(value);
    return characterUDF;
  }

  private ChangeReservation getPaymentUpdateRequest(
      ConfirmReservationRequest confirmReservationRequest) {
    return mapToChangeReservation(confirmReservationRequest);
  }

  private ChangeReservation getSpecialReqUpdateRequest(SpecialRequests specialRequests) {
    return specialReqRequestOhipMapper.toChangeReservationDto(
        specialRequests.getHotelId(), specialRequests.getReservationIds().get(0),
        specialRequests.getSpecialRequests(), specialRequests.getBookingNotes());
  }

  private ChangeReservation getPackagesUpdateRequest(
      ReservationPackagesRequest updateReservationPackageRequest) {
    return mapToUpdateReservationPackagesOhip(
        updateReservationPackageRequest, 0,
        getPackages(
            updateReservationPackageRequest).block());
  }

  private ChangeReservation getGuestBookerUpdateRequest(
      ReservationGuestRequest guestReservationRequest) {
    return mapToCreateReservationGuestOhip(guestReservationRequest,
        guestReservationRequest.getStayingGuests().get(0),
        guestReservationRequest.getBookerProfileId(),
        guestReservationRequest.getCompanyProfileId());
  }

  private MultiValueMap<String, String> createRoutingInstructionMap(final String payeeId,
      final Integer folioWindowNo,
      final RoutingInstructionType instruction) {
    final MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.put("payeeId", singletonList(payeeId));
    params.put("folioWindowNo", singletonList(folioWindowNo.toString()));
    if (!Objects.isNull(instruction.getRoutingLinkId())) {
      params.put("routingLinkId", singletonList(instruction.getRoutingLinkId().getId()));
    }
    if (!Objects.isNull(instruction.getCreditLimit())) {
      params.put("creditLimit", singletonList(instruction.getCreditLimit().toString()));
    }
    params.put("daily", singletonList(instruction.getDuration().getDaily().toString()));
    params.put("sunday", singletonList(instruction.getDuration().getSunday().toString()));
    params.put("monday", singletonList(instruction.getDuration().getMonday().toString()));
    params.put("tuesday", singletonList(instruction.getDuration().getTuesday().toString()));
    params.put("wednesday", singletonList(instruction.getDuration().getWednesday().toString()));
    params.put("thursday", singletonList(instruction.getDuration().getThursday().toString()));
    params.put("friday", singletonList(instruction.getDuration().getFriday().toString()));
    params.put("saturday", singletonList(instruction.getDuration().getSaturday().toString()));
    if (instruction.getDuration().getDaily()) {
      params.put("startDate",
          singletonList(instruction.getDuration().getTimeSpan().getStartDate().toString()));
      params.put("endDate",
          singletonList(instruction.getDuration().getTimeSpan().getEndDate().toString()));
    }
    params.put("retrievePostingsForRoomRouting", singletonList(FALSE));

    Optional.ofNullable(instruction.getTransactionCodes()).ifPresent(transactionCodes -> {
      List<String> tranCodes = new ArrayList<>();
      transactionCodes.forEach(
          transactionCode -> tranCodes.add(transactionCode.getTransactionCode()));
      params.put("transactionCode", tranCodes);
    });

    Optional.ofNullable(instruction.getBillingInstructions()).ifPresent(billingInstructions -> {
      List<String> billingCodes = new ArrayList<>();
      billingInstructions.forEach(billingCode -> billingCodes.add(billingCode.getBillingCode()));
      params.put("billingCode", billingCodes);
    });

    return params;
  }

  private void deleteBusinessNotes(String hotelId,
      String reservationId,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {

    Flux.fromIterable(reservation.getReservations().getReservation().get(0).getComments()).filter(
            commentInfoType -> commentInfoType.getComment().getCommentTitle().equals(BUSINESS_NOTES))
        .flatMap(commentInfoType -> ohipReservationClient.sendChangeReservationRequest(hotelId,
                reservationId, mapToChangeRsvDeleteBusinessNotes(commentInfoType, reservation)),
            reservationOhipProperties.getMaxConcurrency())
        .collectList().block();
  }

  public BusinessItems mapMissingFieldsForBusinessItems(BusinessItems businessItems,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models
          .Reservation> reservations) {
    if (StringUtils.isBlank(businessItems.getPurchaseOrderNumber())) {
      var userDefinedFields = reservations.get(0).getReservations().getReservation().get(0)
          .getUserDefinedFields();
      if (userDefinedFields != null) {
        var purchaseOrderNumber = userDefinedFields.getCharacterUDFs().stream()
            .filter(characterUDFType -> characterUDFType.getName().equals(UDFC_PURCHASE_ORDER_NAME))
            .findFirst();
        businessItems.setPurchaseOrderNumber(
            purchaseOrderNumber.map(CharacterUDFType::getValue).orElse(null));
      }
    }

    if (StringUtils.isBlank(businessItems.getCustomReferenceNumber())) {
      var customReferenceNumber = reservations.get(0).getReservations().getReservation().get(0)
          .getCustomReference();
      businessItems.setCustomReferenceNumber(customReferenceNumber);
    }

    return businessItems;
  }

  private ChangeReservation mapToChangeReservationDiscount(
      UpdateDiscountRequest updateDiscountRequest,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservations) {

    var reservationDetails = reservations.stream()
        .map(reservation -> reservation.getReservations().getReservation().get(0))
        .toList();

    return updateDiscountRequestOhipMapper.toChangeReservationDto(
        updateDiscountRequest, reservationDetails);
  }

  private ChangeReservation mapToChangeReservationBusiness(
      BusinessItemsRequest businessItemsRequest,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation,
      List<BusinessAllowanceRuleDto> businessAllowanceRules) {

    var reservationDetails = reservation.getReservations().getReservation();

    return businessItemsRequestOhipMapper.toChangeReservationDto(businessItemsRequest,
        reservationDetails.get(0), businessAllowanceRules);
  }

  private ChangeReservation mapToChangeRsvDeleteBusinessNotes(
      CommentInfoType businessNotesComment,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {
    var comment = new CommentInfoType();
    comment.setId(businessNotesComment.getId());
    comment.setType(businessNotesComment.getType());

    var comments = new ArrayList<CommentInfoType>();
    comments.add(comment);

    var reservationIdList = reservation
        .getReservations()
        .getReservation()
        .get(0)
        .getReservationIdList();

    var rsv = new HotelReservationInstructionType();
    rsv.setReservationIdList(reservationIdList);
    rsv.setComments(comments);

    var reservations = new ArrayList<HotelReservationInstructionType>();
    reservations.add(rsv);

    var changeReservation = new ChangeReservation();
    changeReservation.setReservations(reservations);

    log.info("deleteBusinessNotes(): request to delete business notes is {} ", changeReservation);

    return changeReservation;
  }

  // To be confirmed that this will return the base amount (amountBeforeTax + discount)
  private BigDecimal getReservationsRoomRatesTotal(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationList) {
    return reservationList.stream().map(reservation ->
            reservation.getReservations().getReservation().stream()
                .map(reservationType -> Optional.ofNullable(reservationType.getRoomStay())
                    .map(RoomStayType::getRoomRates).orElseGet(Collections::emptyList).stream()
                    .map(roomRateType -> Optional.ofNullable(roomRateType.getRates())
                        .map(RatesType::getRate).orElseGet(Collections::emptyList).stream()
                        .map(rate -> Optional.ofNullable(rate.getBase())
                            .map(TotalType::getAmountBeforeTax).orElse(BigDecimal.ZERO)
                            .add(Optional.ofNullable(rate.getDiscount())
                                .map(DiscountType::getAmount).orElse(BigDecimal.ZERO)))
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                    .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
  }

  private ChangeReservation mapToUpdateReservationPackagesOhip(
      ReservationPackagesRequest reservationPackagesRequest, int roomIndex,
      PackagesResponseOhipDto packagesResponseOhipDto) {

    var packagesSelection = addPackagesFromGroup(reservationPackagesRequest.getRoomsSelections()
        .get(roomIndex).getPackagesSelection(), reservationPackagesRequest.getHotelId());

    var requestPackages = reservationPackagesRequest.getRoomsSelections()
        .get(roomIndex)
        .getPackagesSelection();

    removePackageGroupsFromRequest(packagesSelection, requestPackages);

    var changeReservation = reservationPackagesOhipMapper
        .toModel(reservationPackagesRequest, roomIndex, packagesResponseOhipDto);

    if (!Objects.isNull(changeReservation) && !Objects.isNull(
        changeReservation.getReservations())) {
      CollectionUtils.isEmpty(changeReservation.getReservations().get(0).getReservationPackages());
    }
    return changeReservation;
  }



  private ChangeReservation mapToRemoveReservationPackagesOhip(
      ReservationPackagesRequest reservationPackagesRequest, int roomIndex) {

    var packagesSelection = addPackagesFromGroup(
        reservationPackagesRequest.getPreviousRoomsSelections()
            .get(roomIndex).getPackagesSelection(), reservationPackagesRequest.getHotelId());

    var requestPackages = reservationPackagesRequest.getPreviousRoomsSelections()
        .get(roomIndex)
        .getPackagesSelection();

    removePackageGroupsFromRequest(packagesSelection, requestPackages);

    var changeReservation = reservationPackagesOhipMapper
        .toRemovalModel(reservationPackagesRequest, roomIndex);

    if (!Objects.isNull(changeReservation) && !Objects.isNull(
        changeReservation.getReservations())) {
      CollectionUtils.isEmpty(changeReservation.getReservations().get(0).getReservationPackages());
    }
    return changeReservation;
  }

  @Override
  public ReservationGuestResponse createReservationGuest(
      ReservationGuestRequest guestReservationRequest) {
    log.debug("Entered createReservationGuest for hotelId={} and {} guests",
        guestReservationRequest.getHotelId(),
        (guestReservationRequest.getStayingGuests() == null ? "null" :
            guestReservationRequest.getStayingGuests().size()));
    List<ChangeReservationDetails> changeReservations;
    if (Boolean.FALSE.equals(guestReservationRequest.getPreCheckIn())) {
      Status reservationCompanyResponse = Optional.of(guestReservationRequest)
          .map(ReservationGuestRequest::getBooker)
          .map(BookerDetails::getAddress)
          .map(BookerAddress::getCompanyName)
          .filter(StringUtils::isNotEmpty)
          .map(companyName -> getCompanyProfileResponse(guestReservationRequest))
          .orElse(null);

      var finalGuestReservationRequest = setLanguageForBooker(guestReservationRequest);

      Optional<StayingGuest> guest =
              finalGuestReservationRequest.getStayingGuests().stream()
                      .filter(g -> Boolean.TRUE.equals(g.getSameAsBooker())).findAny();

      //reuse temporary profile for booker
      String rsvId = null;
      if (guest.isPresent()) {
        rsvId = guest.get().getReservationId();
      }
      String bookerProfileId;

      //if booker=stayer, reuse temp profile for booker
      if (rsvId != null) {
        var reservation = ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
              rsvId).block();
        List<UniqueIDType> profileIds = reservation.getReservations().getReservation()
                .get(0).getReservationGuests().get(0).getProfileInfo().getProfileIdList();
        Set<String> s = Collections.singleton(profileIds.get(0).getId());
        Map<String, Profile> profilesByIds = getProfilesMap(s);
        Profile bookerProfile = profilesByIds.values().stream().findFirst().orElse(null);

        if (bookerProfile != null) {
          var profile = reservationBookerRequestOhipMapper.toDto(finalGuestReservationRequest.getBooker(), guest);
          profile.setProfileIdList(bookerProfile.getProfileIdList());
          preserveExistingPassportId(profile, bookerProfile);
          ohipReservationClient.sendUpdateProfileRequest(
                  getHotelId(finalGuestReservationRequest), profile);
        }

        bookerProfileId = profileIds.get(0).getId();
      } else {
        var reservationBookerResponse = ohipReservationClient.sendPostProfileRequest(
                getHotelId(finalGuestReservationRequest),
                reservationBookerRequestOhipMapper.toDto(finalGuestReservationRequest.getBooker(), guest));
        bookerProfileId = getProfileId(reservationBookerResponse);
      }
      final var companyProfileId = getProfileId(reservationCompanyResponse);

      //post guest profile
      finalGuestReservationRequest.getStayingGuests().forEach(
              stayingGuest -> {
                if (Boolean.FALSE.equals(stayingGuest.getSameAsBooker())) {
                  //get TEMP Profile
                  var reservation = ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
                          stayingGuest.getReservationId()).block();
                  List<UniqueIDType> profileId = reservation.getReservations().getReservation()
                          .get(0).getReservationGuests().get(0).getProfileInfo().getProfileIdList();
                  Set<String> s = Collections.singleton(profileId.get(0).getId());
                  Map<String, Profile> profilesByIds = getProfilesMap(s);
                  Profile tempProfile = profilesByIds.values().stream().findFirst().orElse(null);

                  //reuse TEMP profile for guest
                  var guestProfile = mapToCreateProfileGuestOhip(
                      stayingGuest.getStayingGuestDetails(), stayingGuest.getLanguage());
                  guestProfile.setProfileIdList(tempProfile.getProfileIdList());
                  preserveExistingPassportId(guestProfile, tempProfile);

                  ohipReservationClient.sendUpdateProfileRequest(
                          getHotelId(finalGuestReservationRequest),
                          guestProfile);
                  stayingGuest.getStayingGuestDetails().setProfileId(profileId.get(0).getId());
                }

                if (shouldBuildAccompanyingGuestProfile(stayingGuest.getAccompanyingGuestDetails())) {
                  var accompanyingGuestProfile = buildAccompanyingGuestProfile(stayingGuest);

                  var accompanyingGuest = ohipReservationClient.sendPostProfileRequest(
                      getHotelId(finalGuestReservationRequest),
                      accompanyingGuestProfile);

                  stayingGuest.getAccompanyingGuestDetails().setProfileId(getProfileId(accompanyingGuest));
                }
              });
      //link profiles to reservation
      changeReservations =
              Flux.fromIterable(finalGuestReservationRequest.getStayingGuests())
                      .flatMap(stayingGuest -> ohipReservationClient.sendPutReservationsGuestRequest(
                              getHotelId(finalGuestReservationRequest), stayingGuest.getReservationId(),
                              mapToCreateReservationGuestOhip(finalGuestReservationRequest, stayingGuest,
                                      bookerProfileId, companyProfileId)))
                      .collectList()
                      .block();

    } else {
      changeReservations = updateReservationGuest(guestReservationRequest);
    }
    return ObjectUtils.isNotEmpty(changeReservations)
        ? reservationGuestResponseOhipMapper.toReservationGuestResponseModel(changeReservations)
        : new ReservationGuestResponse();
  }

  private boolean shouldBuildAccompanyingGuestProfile(AccompanyingGuestDetails accompanyingGuestDetails) {
    return accompanyingGuestDetails != null
        && StringUtils.isNotBlank(accompanyingGuestDetails.getLastName());
  }

  private List<ChangeReservationDetails> updateReservationGuest(
      ReservationGuestRequest guestReservationRequest) {

    String hotelId = guestReservationRequest.getHotelId();
    Map<String, List<StayingGuest>> stayingGuestDetailsMap = getStayingGuestDetailsMap(
        guestReservationRequest);
    var savedReservationList = getReservationsByIdsLight(hotelId, stayingGuestDetailsMap.keySet());
    var rsvToTotalGuestsMap = extractTotalGuestForReservation(savedReservationList);
    stayingGuestDetailsMap.forEach(
        (reservationId, stayingGuests) ->
            validateStayingGuests(stayingGuests,
                rsvToTotalGuestsMap.getOrDefault(reservationId, 0)));
    Set<String> stayingGuestProfileIds = getStayingGuestProfileIds(guestReservationRequest,
        savedReservationList);
    List<Profile> stayingGuestProfileList = ohipReservationClient.sendGetProfilesByProfileIds(
        stayingGuestProfileIds);

    Map<String, Map<String, Boolean>> updatedReservationProfileIdsMap = updateProfiles(hotelId,
        stayingGuestDetailsMap, stayingGuestProfileList);

    return createChangeReservationDetailsList(hotelId, updatedReservationProfileIdsMap);
  }



  private void validateStayingGuests(List<StayingGuest> stayingGuests, Integer totalGuests) {
    var leadGuestCount = stayingGuests.stream()
        .filter(leadGuest -> leadGuest.getIsAccompanyingGuest() != null
            && !leadGuest.getIsAccompanyingGuest())
        .count();

    var accompanyingGuestsCount = stayingGuests.stream()
        .filter(leadGuest -> leadGuest.getIsAccompanyingGuest() != null
            && leadGuest.getIsAccompanyingGuest())
        .count();
    if (leadGuestCount != 1) {
      throw createAndLogException(ErrorCode.OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION,
          "A reservation must have exactly one lead guest");
    }
    //don't include the lead booker
    var allowedAdditionGuests = totalGuests != 0 ? totalGuests - 1 : 0;
    if (accompanyingGuestsCount > allowedAdditionGuests) {
      throw createAndLogException(ErrorCode.OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION,
          "Max Accompanying Guests allowed is: " + allowedAdditionGuests);
    }
  }

  private HotelReservationException createAndLogException(ErrorCode errorCode,
      String errorMessage) {
    var exception = new HotelReservationException(errorCode, errorMessage);
    ExceptionLogger.log(log, exception);
    return exception;
  }

  private Set<String> getStayingGuestProfileIds(ReservationGuestRequest guestReservationRequest,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> savedReservationList) {

    var requestProfileIds = Optional.ofNullable(guestReservationRequest.getStayingGuests())
        .orElse(Collections.emptyList())
        .stream()
        .map(StayingGuest::getStayingGuestDetails)
        .map(StayingGuestDetails::getProfileId)
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    var operaReservations = Optional.ofNullable(savedReservationList)
        .orElse(Collections.emptyList())
        .stream()
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation::getReservations)
        .filter(Objects::nonNull)
        .map(HotelReservationsType::getReservation)
        .filter(Objects::nonNull)
        .flatMap(List::stream)
        .toList();

    var bookerProfileId = operaReservations.stream()
        .map(HotelReservationType::getReservationProfiles)
        .filter(Objects::nonNull)
        .flatMap(resType -> resType.getReservationProfile().stream())
        .filter(Objects::nonNull)
        .flatMap(profileType -> profileType.getProfileIdList().stream())
        .filter(Objects::nonNull)
        .map(UniqueIDType::getId)
        .filter(Objects::nonNull)
        .filter(requestProfileIds::contains)
        .findFirst()
        .orElse("");

    var operaGuestProfileIds = operaReservations.stream()
        .flatMap(a -> a.getReservationGuests().stream())
        .filter(Objects::nonNull)
        .map(ResGuestType::getProfileInfo)
        .filter(Objects::nonNull)
        .flatMap(resGuestProfiles -> resGuestProfiles.getProfileIdList().stream())
        .filter(Objects::nonNull)
        .map(UniqueIDType::getId)
        .filter(Objects::nonNull)
        .filter(requestProfileIds::contains)
        .collect(toSet());

    //If booker and lead guest have the same profile, remove the profile id from the request
    operaGuestProfileIds.removeIf(profile -> profile.equals(bookerProfileId));

    return operaGuestProfileIds;
  }

  private Map<String, Map<String, Boolean>> updateProfiles(String hotelId,
      Map<String, List<StayingGuest>> stayingGuestDetailsMap,
      List<Profile> stayingGuestProfileList) {
    Map<String, Map<String, Boolean>> updatedReservationProfileIdsMap = new HashMap<>();

    stayingGuestDetailsMap.forEach((reservationId, stayingGuestsGroup) -> {
      Map<String, Boolean> updatedProfileIdsMap = new HashMap<>();

      stayingGuestsGroup.forEach(stayingGuest -> {
        String profileId = stayingGuest.getStayingGuestDetails().getProfileId();
        var profileDetailsOpt = findProfileDetails(stayingGuestProfileList,
            profileId);

        if (ObjectUtils.isNotEmpty(stayingGuest.getIsAccompanyingGuest())) {
          var isLeadGuest = Boolean.FALSE.equals(stayingGuest.getIsAccompanyingGuest());
          var profileDetails = profileDetailsOpt.orElseGet(Profile::new);
          updateProfile(hotelId, stayingGuest, profileDetails, updatedProfileIdsMap,
              isLeadGuest);

        }
      });

      updatedReservationProfileIdsMap.put(reservationId, updatedProfileIdsMap);
    });

    return updatedReservationProfileIdsMap;
  }

  private Optional<Profile> findProfileDetails(List<Profile> profileList, String profileId) {
    return profileList.stream()
        .filter(profile -> profile.getProfileIdList().get(0).getId().equals(profileId))
        .findFirst();

  }

  private void updateProfile(String hotelId, StayingGuest stayingGuest, Profile profileDetails,
      Map<String, Boolean> updatedProfileIdsMap, boolean primary) {

    var updateProfileRequest = reservationAccompanyingGuestProfileRequestOhipMapper.toDto(
        stayingGuest, profileDetails);

    if (ObjectUtils.isNotEmpty(updateProfileRequest)) {
      if (ObjectUtils.isNotEmpty(profileDetails) && ObjectUtils.isNotEmpty(profileDetails.getProfileDetails())) {
        ohipReservationClient.sendUpdateProfileRequest(hotelId, updateProfileRequest);
        updatedProfileIdsMap.put(profileDetails.getProfileIdList().get(0).getId(), primary);
      } else {
        var accompanyingGuestProfile = ohipReservationClient.sendPostProfileRequest(hotelId,
            updateProfileRequest);
        var accompanyingGuestProfileId = getProfileId(accompanyingGuestProfile);
        updatedProfileIdsMap.put(accompanyingGuestProfileId, primary);
      }
    }
  }

  private List<ChangeReservationDetails> createChangeReservationDetailsList(String hotelId,
      Map<String, Map<String, Boolean>> updatedReservationProfileIdsMap) {
    return updatedReservationProfileIdsMap.entrySet().stream()
        .map(entry -> {
          var createReservationAccompanyingGuest = reservationAccompanyingGuestRequestOhipMapper.toDto(
              entry.getKey(), entry.getValue());
          return ohipReservationClient.sendPutReservationsGuestRequest(hotelId, entry.getKey(),
              createReservationAccompanyingGuest).block();
        })
        .toList();
  }

  private Map<String, List<StayingGuest>> getStayingGuestDetailsMap(
      ReservationGuestRequest guestReservationRequest) {
    return Optional.ofNullable(guestReservationRequest.getStayingGuests())
        .orElse(Collections.emptyList())
        .stream()
        .collect(Collectors.groupingBy(StayingGuest::getReservationId));
  }

  private ReservationGuestRequest setLanguageForBooker(
      ReservationGuestRequest guestReservationRequest) {
    if (StringUtils.isEmpty(guestReservationRequest.getBooker().getLanguage())) {
      var reservation = ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
          guestReservationRequest.getStayingGuests().get(0).getReservationId());
      var rule = rulesAgentClient.getChannelSourceInfo(
          Objects.requireNonNull(reservation.block()).getReservations().getReservation()
              .get(0).getRoomStay().getRoomRates()
              .get(0)
              .getSourceCode());
      guestReservationRequest.getStayingGuests().forEach(stayingGuest -> {
        if (LANGUAGE_UNDEFINED.equals(rule.getRequestDetails().getLanguage())) {
          stayingGuest.setLanguage(getOperaLanguageCode(ENGLISH));
        } else {
          stayingGuest.setLanguage(
              getOperaLanguageCode(
                  rule.getRequestDetails().getLanguage().toLowerCase(Locale.ROOT)));
        }
        if (StringUtils.isEmpty(guestReservationRequest.getBooker().getLanguage())) {
          guestReservationRequest.getBooker().setLanguage(stayingGuest.getLanguage());
        }
      });
    } else {
      guestReservationRequest.getBooker()
          .setLanguage(getOperaLanguageCode(guestReservationRequest.getBooker().getLanguage()));
      guestReservationRequest.getStayingGuests().forEach(stayingGuest ->
          stayingGuest.setLanguage(guestReservationRequest.getBooker().getLanguage())
      );
    }
    return guestReservationRequest;
  }

  private Status getCompanyProfileResponse(ReservationGuestRequest guestReservationRequest) {
    return ohipReservationClient.sendPostProfileRequest(
        getHotelId(guestReservationRequest),
        reservationCompanyRequestOhipMapper.toDto(
            guestReservationRequest));
  }

  /* Update Billing Address on the basis of AddressType
   *  First call is made to retrieve the profile Id on the basis of reservation Id
   *  After the profile Id is received , make a call to update the Billing address in the Ohip with the profile Type*/
  @Override
  public void updateBillingAddress(BillingAddressRequest billingAddressRequest) {
    var hotelId = billingAddressRequest.getHotelId();

    final var reservationList = ohipReservationClient.getReservations(hotelId,
            new HashSet<>(billingAddressRequest.getReservationIds())).collectList().block();

    if (CollectionUtils.isEmpty(reservationList)) {
      return;
    }

    Set<String> guestProfileIds = getGuestProfileIdsByType(reservationList, ProfileTypeType.GUEST);
    Set<String> profileIds = getProfileIdsByType(reservationList, RESERVATIONCONTACT);
    Set<String> companyProfileIds = getProfileIdsByType(reservationList, COMPANY);
    BookerAddress bookerAddress = billingAddressRequest.getBooker().getAddress();
    String addressType = bookerAddress.getAddressType();

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCaptureBillingAddressBb())
        && Objects.equals(BUSINESS_BOOKER_CHANNEL, billingAddressRequest.getChannel())) {
      updateBillingAddressFeatureFlagOn(billingAddressRequest, guestProfileIds, hotelId,
              companyProfileIds, profileIds);
    } else {
      updateBillingAddressFeatureFlagOff(billingAddressRequest, guestProfileIds,
              addressType, hotelId, companyProfileIds, profileIds);
    }
  }

  @Override
  public void updateBillingAddressCcui(BillingAddressRequest billingAddressRequest) {
    var hotelId = billingAddressRequest.getHotelId();
    Set<String> profileIds;
    Set<String> guestProfileIds;
    Set<String> companyProfileIds;

    final var reservationList =
        ohipReservationClient.getReservations(hotelId,
            new HashSet<>(billingAddressRequest.getReservationIds())).collectList().block();
    if (!CollectionUtils.isEmpty(reservationList)) {
      guestProfileIds = getGuestProfileIdsByType(reservationList, ProfileTypeType.GUEST);
      profileIds = getProfileIdsByType(reservationList, RESERVATIONCONTACT);
      companyProfileIds = getProfileIdsByType(reservationList, COMPANY);
      BookerAddress bookerAddress = billingAddressRequest.getBooker().getAddress();
      String addressType = bookerAddress.getAddressType();

      if (guestProfileIds != null && ADDRESS_TYPE_BUSINESS.equalsIgnoreCase(addressType)) {
        for (String profileId : guestProfileIds) {
          updateBillingAddressOpera(billingAddressRequest, hotelId, profileId);
        }
      }
      if (companyProfileIds != null && ADDRESS_TYPE_BUSINESS.equalsIgnoreCase(addressType)) {
        updateBillingAddressOpera(billingAddressRequest, hotelId, companyProfileIds.iterator().next());
      }
      if (profileIds != null) {
        updateBillingAddressOpera(billingAddressRequest, hotelId, profileIds.iterator().next());
      }
    }
  }

  @Override
  public void changeReservationRatePlan(RatePlanRoomTypeChangeRequest ratePlanChangeRequest) {

    log.debug("Entered changeReservationRatePlan for hotelId={} and {} reservation ids",
        ratePlanChangeRequest.getHotelId(),
        (ratePlanChangeRequest.getReservationIds() == null ? "null" :
            ratePlanChangeRequest.getReservationIds().size()));
    ohipReservationClient.sendChangeReservationRequest(
        ratePlanChangeRequest.getHotelId(),
        ratePlanChangeRequest.getReservationIds().get(0),
        mapToChangeReservationRatePlanRoomType(ratePlanChangeRequest)).block();
  }

  @Override
  public void changeReservationRoomType(RatePlanRoomTypeChangeRequest roomTypeChangeRequest) {
    log.debug("Entered changeReservationRoomType for hotelId={} and {} reservation ids",
        roomTypeChangeRequest.getHotelId(),
        (roomTypeChangeRequest.getReservationIds() == null ? "null" :
            roomTypeChangeRequest.getReservationIds().size()));
    ohipReservationClient.sendChangeReservationRequest(
        roomTypeChangeRequest.getHotelId(),
        roomTypeChangeRequest.getReservationIds().get(0),
        mapToChangeReservationRatePlanRoomType(roomTypeChangeRequest)).block();
  }

  private List<HotelAvailabilityDto> getHotelAvailabilities(
      RatePlanRoomTypeChangeRequest ratePlanRoomTypeChangeRequest) {

    var roomTypesCount = ratePlanRoomTypeChangeRequest.getRoomTypes()
        .stream()
        .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

    List<HotelAvailabilityDto> priceBreakdownList = new ArrayList<>();
    List<Map.Entry<String, Long>> entries = new ArrayList<>(roomTypesCount.entrySet());
    IntStream.range(0, entries.size())
        .forEach(roomTypeCount -> {
          Map.Entry<String, Long> entry = entries.get(roomTypeCount);
          var hotelAvailabilitiesRequest = AvailabilityRequestDto.builder()
              .hotelId(ratePlanRoomTypeChangeRequest.getHotelId())
              .roomStayStartDate(ratePlanRoomTypeChangeRequest.getStartDate())
              .roomStayEndDate(ratePlanRoomTypeChangeRequest.getEndDate())
              .ratePlanCode(ratePlanRoomTypeChangeRequest.getRateCode())
              .roomStayQuantity(Math.toIntExact(entry.getValue()))
              .roomTypes(singletonList(entry.getKey()))
              .build();
          var priceBreakdownDto = apiLimitsService.getHotelAvailabilityResponses(
              hotelAvailabilitiesRequest).getHotelAvailability().get(0);
          priceBreakdownList.add(priceBreakdownDto);
        });

    return priceBreakdownList;
  }

  public CreateReservation mapToCreateReservationOhip(Reservation reservationRequest,
      BookingChannel bookingChannel, String cotCode, String reasonForStay) {

    var createReservation =
        reservationRequestOhipMapper.toCreateReservationModel(reservationRequest);

    if (!Objects.isNull(createReservation)
        && !Objects.isNull(createReservation.getReservations())
        && !CollectionUtils.isEmpty(createReservation.getReservations().getReservation())) {

      var reservation = createReservation.getReservations().getReservation().get(0);

      ResGuestAdditionalInfoType additionalGuestInfo = new ResGuestAdditionalInfoType();
      additionalGuestInfo.setPurposeOfStay(reasonForStay);
      reservation.setAdditionalGuestInfo(additionalGuestInfo);

      removeEmptyPackages(createReservation);

      if (Boolean.TRUE.equals(reservationRequest.getCotRequired())) {
        ResInventoryItemType inventoryItemType = new ResInventoryItemType();
        inventoryItemType.setType(cotCode);
        inventoryItemType.setQuantity(1);
        DateRangeType dateRange = new DateRangeType();
        dateRange.setStart(LocalDate.parse(reservationRequest.getArrival()));
        dateRange.setEnd(LocalDate.parse(reservationRequest.getDeparture()));
        inventoryItemType.setTimeSpan(dateRange);

        ItemInfoType itemInfoType = new ItemInfoType();
        itemInfoType.setCode(cotCode);
        itemInfoType.setQuantity(1);
        TimeSpanType timeSpan = new TimeSpanType();
        timeSpan.setStartDate(LocalDate.parse(reservationRequest.getArrival()));
        timeSpan.setEndDate(LocalDate.parse(reservationRequest.getDeparture()));
        itemInfoType.setTimeSpan(timeSpan);
        inventoryItemType.setItem(itemInfoType);

        ResInventoryItemsType inventoryItemsType = new ResInventoryItemsType();
        inventoryItemsType.setItem(List.of(inventoryItemType));
        inventoryItemsType.setItemClassCode(cotCode);
        reservation.setInventoryItems(inventoryItemsType);
      }

      if (StringUtils.isNotEmpty(reservationRequest.getDistributionIATANumber())) {
        var userDefinedTypes = reservation.getUserDefinedFields();
        if (userDefinedTypes != null) {
          final var characterUDF = new CharacterUDFType();
          characterUDF.setName(UDFC_16);
          characterUDF.setValue(reservationRequest.getDistributionIATANumber());
          userDefinedTypes.addCharacterUDFsItem(characterUDF);
          reservation.setUserDefinedFields(userDefinedTypes);
        }
      }

      if (!Objects.isNull(reservation) && !Objects.isNull(reservation.getRoomStay())) {
        var roomStayType = reservation.getRoomStay();
        roomStayType.setRoomRates(
            roomRateOhipMapper.toRoomRateTypesModel(reservationRequest.getRoomRates(),
                reservationRequest.getSourceCode(),
                reservationOhipProperties.getDefaultMarketCode(),
                bookingChannel));
      }

    }
    return createReservation;
  }

  private void removeEmptyPackages(CreateReservation createReservation) {
    createReservation.getReservations().getReservation().forEach(reservationType ->
        Optional.ofNullable(reservationType.getReservationPackages())
            .ifPresent(packages -> {
              packages.removeIf(pkg -> StringUtils.isBlank(pkg.getPackageCode()));
              if (packages.isEmpty()) {
                reservationType.setReservationPackages(null);
              }
            }));
  }

  public ChangeReservation mapToCreateReservationGuestOhip(
      ReservationGuestRequest reservationGuestRequest, StayingGuest stayingGuest,
      String bookerProfileId,
      String companyProfileId) {
    return reservationGuestRequestOhipMapper.toDto(reservationGuestRequest, stayingGuest,
        bookerProfileId, companyProfileId);
  }

  private Profile buildAccompanyingGuestProfile(StayingGuest stayingGuest) {
    StayingGuestDetails stayingGuestDetails = guestDetailsMapper
        .toDto(stayingGuest.getAccompanyingGuestDetails());

    return mapToCreateProfileGuestOhip(stayingGuestDetails, stayingGuest.getLanguage());
  }

  private Profile mapToCreateProfileGuestOhip(StayingGuestDetails guest, String language) {
    final var guestNameType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType();
    final var guestProfileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    // name
    if (StringUtils.isNotBlank(guest.getTitle())) {
      guestNameType.setNameTitle(guest.getTitle());
    }

    guestNameType.setGivenName(guest.getFirstName());
    guestNameType.setSurname(guest.getLastName());
    guestNameType.setNameType(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameTypeType.PRIMARY);

    // email
    if (StringUtils.isNotBlank(guest.getEmailAddress())) {
      final var emailType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType();
      final var emailInfoType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType();
      final var companyProfileTypeEmails = new CompanyProfileTypeEmails();
      emailType.setEmailAddress(guest.getEmailAddress());
      emailInfoType.setEmail(emailType);
      companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));
      guestProfileType.setEmails(companyProfileTypeEmails);
    }

    // address
    if (guest.getAddress() != null) {
      final var addressType = new AddressType();
      final var addressInfoType = new AddressInfoType();
      final var profileTypeAddresses = new ProfileTypeAddresses();
      final var addressLines = Stream.of(
              guest.getAddress().getAddressLine1(),
              guest.getAddress().getAddressLine2(),
              guest.getAddress().getAddressLine3(),
              guest.getAddress().getAddressLine4())
              .map(addressLine -> Objects.isNull(addressLine) ? StringUtils.EMPTY : addressLine)
              .toList();
      addressType.setAddressLine(addressLines);
      addressType.setPostalCode(guest.getAddress().getPostalCode());
      addressType.setCityName(guest.getAddress().getCityName());
      if (guest.getAddress().getCountryCode() != null) {
        final var countryNameType = new CountryNameType();
        countryNameType.setValue(guest.getAddress().getCountryCode());
        addressType.setCountry(countryNameType);
      }
      if (guest.getAddress().getAddressType() != null) {
        addressType.setType(guest.getAddress().getAddressType());
      }
      addressType.setPrimaryInd(true);
      addressInfoType.setAddress(addressType);
      profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
      guestProfileType.setAddresses(profileTypeAddresses);
    }
    final var customerType = new CustomerType();
    customerType.setPersonName(List.of(guestNameType));
    customerType.setLanguage(language);

    //additional details
    Optional.of(guest)
            .map(StayingGuestDetails::getAdditionalDetails)
            .filter(details -> !ObjectUtils.isEmpty(details))
            .ifPresent(details -> saveAdditionalDetails(guest, customerType));

    guestProfileType.setProfileType(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeType.GUEST);
    guestProfileType.setCustomer(customerType);
    Profile guestProfile = new Profile();
    guestProfile.setProfileDetails(guestProfileType);
    return guestProfile;
  }

  private static void preserveExistingPassportId(Profile newProfile, Profile existingProfile) {
    Optional.ofNullable(newProfile)
        .map(Profile::getProfileDetails)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType::getCustomer)
        .map(CustomerType::getIdentifications)
        .map(CustomerTypeIdentifications::getIdentificationInfo)
        .ifPresent(infoList -> {
          String existingId = getExistingPassportInfoId(existingProfile);
          if (existingId != null) {
            infoList.stream()
                .filter(info -> info.getIdentification() != null
                    && ID_TYPE_PASSPORT.equals(info.getIdentification().getIdType()))
                .forEach(info -> {
                  info.setId(existingId);
                  info.setType("DocumentId");
                });
          }
        });
  }

  private static String getExistingPassportInfoId(Profile profile) {
    return Optional.ofNullable(profile)
        .map(Profile::getProfileDetails)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType::getCustomer)
        .map(CustomerType::getIdentifications)
        .map(CustomerTypeIdentifications::getIdentificationInfo)
        .flatMap(infos -> infos.stream()
            .filter(info -> info.getIdentification() != null
                && ID_TYPE_PASSPORT.equals(info.getIdentification().getIdType()))
            .map(IdentificationInfoType::getId)
            .findFirst())
        .orElse(null);
  }

  private static void saveAdditionalDetails(StayingGuestDetails stayingGuestDetails, CustomerType customerType) {
    if (ObjectUtils.isNotEmpty(stayingGuestDetails.getAdditionalDetails().getDob())) {
      customerType.setBirthDate(stayingGuestDetails.getAdditionalDetails().getDob());
    }
    if (StringUtils.isNotEmpty(stayingGuestDetails.getAdditionalDetails().getNationality())) {
      customerType.setNationality(stayingGuestDetails.getAdditionalDetails().getNationality());
    }
    if (StringUtils.isNotEmpty(stayingGuestDetails.getAdditionalDetails().getPassportNumber())
        && !stayingGuestDetails.getAdditionalDetails().getPassportNumber().contains("XXX")) {
      var customerTypeIdentifications = setCustomerTypeIdentifications(stayingGuestDetails);
      customerType.setIdentifications(customerTypeIdentifications);
    }
  }

  private static CustomerTypeIdentifications setCustomerTypeIdentifications(
          StayingGuestDetails stayingGuestDetails) {
    String passportNumber = stayingGuestDetails.getAdditionalDetails().getPassportNumber();
    var identificationType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationType();
    identificationType.setIdNumber(passportNumber);
    identificationType.setIdType(ID_TYPE_PASSPORT);
    var identificationInfoType = new IdentificationInfoType();
    identificationInfoType.setIdentification(identificationType);

    var customerTypeIdentifications = new CustomerTypeIdentifications();
    customerTypeIdentifications.setIdentificationInfo(List.of(identificationInfoType));
    return customerTypeIdentifications;
  }

  private DepositFolioCriteria mapToDepositCriteria(
      DepositFolioRequest depositFolioRequest,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation,
      VatRuleResponseDto vatRuleResponseDto,
      List<ReservationCityTaxInfo> cityTaxInfoList,
      List<DepositFolioCharge> charges) {
    return depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(depositFolioRequest,
        reservation.getReservations().getReservation().get(0), vatRuleResponseDto, cityTaxInfoList,
        charges);

  }

  private String getHotelId(ReservationGuestRequest reservationGuestRequest) {
    return reservationGuestRequest.getHotelId();
  }

  private DepositFolioCriteria mapToDepositCriteriaForAmend(
      DepositFolioRequest depositFolioRequest,
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation,
      VatRuleResponseDto vatRuleResponseDto,
      List<ReservationCityTaxInfo> cityTaxInfoList) {
    return depositFoliosRequestOhipMapper.toDepositFolioCriteriaAmendModel(depositFolioRequest,
        reservation.getReservations().getReservation().get(0), vatRuleResponseDto, cityTaxInfoList);

  }

  private String getProfileId(Status status) {
    if (status == null) {
      return null;
    }
    //we don't have in Opera response the newly created profile id, so we rely only on href
    final var href = status.getLinks().get(0).getHref();
    return href.substring(href.lastIndexOf("/") + 1);
  }

  private ChangeReservation mapToChangeReservation(String reservationId,
      CreateMemoRequest createMemoRequest) {

    return memosOhipMapper.toDto(reservationId, createMemoRequest);
  }

  private ChangeReservation mapToChangeReservation(
      ConfirmReservationRequest confirmReservationRequest) {

    return confirmReservationRequestOhipMapper.toChangeReservationModel(confirmReservationRequest);
  }

  private ChangeReservation mapToChangeReservation(HotelReservationType hotelReservationType,
      String companyId,
      int folioWindow) {
    return businessItemsRequestOhipMapper.toChangeReservationDto(hotelReservationType, companyId,
        folioWindow);
  }

  private ChangeReservation mapToChangeReservation(Reservation reservation, String reservationId) {

    return changeReservationRequestOhipMapper.toChangeReservationDto(reservationId, reservation.getRoomRates(),
        reservation.getSourceCode());
  }

  private List<PackagesSelection> addPackagesFromGroup(
      List<PackagesSelection> packagesSelectionList, String hotelId) {
    List<PackagesSelection> packagesSelections = new CopyOnWriteArrayList<>();

    Flux.fromIterable(packagesSelectionList)
        .flatMap(pck -> {
          Mono<PackageGroupsInfo> packageGroup = ohipPackagesClient.getPackageGroups(
              packageGroupsRequestOhipMapper.toOhipDto(hotelId, pck.getId()));
          if (Objects.nonNull(pck.getPrice())
              && unleashWrapper.isEnabled(unleashWrapper.featureFlag().getDistributionBookingFee())) {
            return Mono.zip(packageGroup, Mono.just(pck.getNoSelections()), Mono.just(pck.getPrice()));
          } else {
            return Mono.zip(packageGroup, Mono.just(pck.getNoSelections()));
          }
        })
        .doOnNext(tuple -> {
          final var packageGroup = tuple.getT1();
          if (Objects.nonNull(packageGroup.getPackageGroupList().getPackageGroups())) {
            var packageGroupType = packageGroup.getPackageGroupList().getPackageGroups().get(0)
                .getPackageGroup().get(0);
            for (PackageCodeType membersList : packageGroupType.getMembersList()) {
              var newPackageSelection = getPackagesSelection(packagesSelectionList,
                  packageGroupType);

              newPackageSelection.setId(membersList.getCode());
              newPackageSelection.setNoSelections(tuple.getT2());
              newPackageSelection.setPackageGroup(packageGroupType.getCode());
              packagesSelections.add(newPackageSelection);
            }
          }
        }).collectList().block();

    return packagesSelections;
  }

  @Override
  public ReservationPackagesResponse getReservationsPackagesByIds(String hotelId,
      Set<String> reservationIds) {
    var reservationList = getReservations(hotelId, reservationIds);
    return reservationResponseOhipMapper.toReservationPackagesResponseModel(reservationList, false);
  }


  @Override
  public ReservationPackagesResponse getReservationsPackagesMealInclusiveRateByReservationsIds(String hotelId,
      Set<String> reservationIds) {
    var reservationList = getReservations(hotelId, reservationIds);
    return reservationResponseOhipMapper.toReservationPackagesResponseModel(reservationList, true);
  }

  @Override
  public CancelInformationResponse getCancelInformation(String hotelId, Set<String> reservationIds,
      String userDateTime) {
    log.debug("Entered getCancelInformation for hotelId={} and {} reservation ids",
        sanitizeInput(hotelId), reservationIds.size());
    var reservationMono = ohipReservationClient.getReservations(hotelId, reservationIds)
        .collectList();
    var hotelTimeZone = ohipReservationClient.getHotelConfig(hotelId)
        .getHotelConfigInfo()
        .getPropertyControls()
        .getDateTimeFormatting()
        .getTimeZoneRegion();
    var reservation = reservationMono.block();
    if (Objects.isNull(reservation) || CollectionUtils.isEmpty(reservation)) {
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_FETCH_RESERVATION_DETAILS_EXCEPTION,
          "An error was returned by OHIP: Could not fetch reservation details");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return isCancellable(reservation.get(0), userDateTime, ZoneId.of(hotelTimeZone));
  }

  @Override
  public ReservationDetailsEnhancedResponse getReservationsByExternalRefId(
      String externalReferenceId) {
    log.debug("Entered getReservationsByExternalReferenceIds for {} external reference id",
        sanitizeInput(externalReferenceId));
    var reservationsDetails =
        ohipReservationClient.sendGetReservationsByExternalReferenceId(externalReferenceId);
    if (reservationsDetails.getReservations().getReservationInfo() == null) {
      log.info("No reservations found for external reference id {}",
          sanitizeInput(externalReferenceId));
      return null;
    }
    var reservationsDetailsModel =
        reservationResponseOhipMapper.toReservationsDetailsResponseModel(
            reservationsDetails);
    var hotelId = reservationsDetails.getReservations().getReservationInfo().get(0).getHotelId();
    Map<String, Profile> profilesByIds = new HashMap<>();
    ReservationAmounts totalReservationAmounts = null;

    if (!CollectionUtils.isEmpty(reservationsDetails.getReservations().getReservationInfo())) {
      profilesByIds = getProfilesGroupedByProfileId(
          reservationsDetails.getReservations().getReservationInfo());
      totalReservationAmounts = getReservationAmounts(hotelId,
          extractReservationIds(reservationsDetails), null);
    }

    var currencyCode =
        reservationsDetails.getReservations().getReservationInfo().get(0).getRoomStay()
            .getRateAmount()
            .getCurrencyCode();

    var deposit = totalReservationAmounts == null ? null : totalReservationAmounts.getDeposit();
    var outStandingCost = totalReservationAmounts == null ? null :
        totalReservationAmounts.getOutStandingCostOfStay();
    var totalCost =
        totalReservationAmounts == null ? null : totalReservationAmounts.getTotalCostOfStay();

    return ReservationDetailsEnhancedResponse.builder()
        .reservationsDetailsResponse(reservationsDetailsModel)
        .billing(reservationResponseOhipMapper.toBillingForModel(
            getBookerProfileById(reservationsDetails, profilesByIds),
            getCompanyName(reservationsDetails)))
        .amountPaid(deposit)
        .balanceOutstanding(outStandingCost)
        .newTotal(deposit)
        .previousTotal(deposit)
        .totalCost(totalCost)
        .currencyCode(currencyCode)
        .build();
  }

  @Override
  public ReservationIdDetailsResponse getReservationsByReservationId(String hotelId,
      String reservationId) {
    log.debug("Entered getReservationsByReservationId for {} reservation id",
        sanitizeInput(reservationId));
    var reservationsDetails =
        ohipReservationClient.sendGetReservationsByReservationId(hotelId, reservationId);
    if (reservationsDetails.getReservations().getReservation() == null) {
      log.info("No reservations found for reservation id {}", sanitizeInput(reservationId));
      return null;
    }

    var reservationsDetailsModel =
        reservationResponseOhipMapper.toReservationIdDetailsResponseModel(
            reservationsDetails);
    Map<String, Profile> profilesByIds = new HashMap<>();
    ReservationAmounts totalReservationAmounts = null;

    if (!CollectionUtils.isEmpty(reservationsDetails.getReservations().getReservation())) {
      profilesByIds = getProfilesGroupedByProfileIds(
          reservationsDetails.getReservations().getReservation());
      totalReservationAmounts = getReservationAmounts(hotelId,
          extractReservationId(reservationsDetails), null);
    }

    var deposit = totalReservationAmounts == null ? null : totalReservationAmounts.getDeposit();
    var outStandingCost = totalReservationAmounts == null ? null :
        totalReservationAmounts.getOutStandingCostOfStay();
    var totalCost =
        totalReservationAmounts == null ? null : totalReservationAmounts.getTotalCostOfStay();

    return ReservationIdDetailsResponse.builder()
        .reservationIdResponse(reservationsDetailsModel)
        .billing(reservationResponseOhipMapper.toBillingForModel(
            getBookerProfileById(reservationsDetails, profilesByIds),
            getCompanyName(reservationsDetails)))
        .amountPaid(deposit)
        .balanceOutstanding(outStandingCost)
        .newTotal(deposit)
        .previousTotal(deposit)
        .totalCost(totalCost)
        .build();
  }

  @Override
  public SearchBookingsResponse searchBookings(BookingSearchCriteria bookingSearchCriteria) {

    log.debug("Entered searchBookings for {}", bookingSearchCriteria);

    var bookingSearchInput = BookingSearchInputBuilder.buildSearchInput(bookingSearchCriteria);

    List<SearchBooking> searchBookings;

    switch (bookingSearchInput.getSearchBookingsType()) {
      case BOOKER_SEARCH ->
          searchBookings = searchBookingsByBookerRelatedFields(bookingSearchInput);
      case HOTEL_SEARCH ->
          searchBookings = searchBookingsByReservationRelatedFields(bookingSearchInput, true);
      case RESERVATION_SEARCH ->
          searchBookings = searchBookingsByReservationRelatedFields(bookingSearchInput, false);
      default -> {
        var exception = new HotelReservationException(
            ErrorCode.DIGITAL_INVALID_BOOKING_SEARCH_CRITERIA_EXCEPTION,
            "Invalid booking search criteria. At least one search field should be provided");
        ExceptionLogger.log(log, exception);
        throw exception;
      }
    }

    // Filter results based on remaining search fields and order based on arrival date
    var finalSearchBookings =
        searchBookings.stream().filter(bookingSearchInput.getFilterChain()::filter)
            .sorted(Comparator.comparing(SearchBooking::getArrivalDate)).toList();

    // Implement a pagination mechanism which goes through the bookings and returns subsets
    // based on the limit and offset that we get from the frontend.
    int offset = bookingSearchCriteria.getOffset();
    int limit = bookingSearchCriteria.getLimit();

    // if there are more than 100 matching results we return an empty response with responseLimitExceeded set to true
    if (finalSearchBookings.size() > SEARCH_RESULTS_LIMIT) {
      return SearchBookingsResponse.builder().bookings(new ArrayList<>())
          .totalPages(0)
          .hasMore(false)
          .limit(limit)
          .offset(offset + limit)
          .totalResults(finalSearchBookings.size())
          .responseLimitExceeded(true)
          .build();
    }

    Iterator<SearchBooking> it = finalSearchBookings.iterator();
    List<SearchBooking> pagedSearchBookings = StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(it,
                Spliterator.ORDERED), false)
        .skip(offset)
        .limit(limit)
        .toList();

    int totalPages = ((searchBookings.size() - 1) / limit) + 1;
    return SearchBookingsResponse.builder().bookings(pagedSearchBookings)
        .totalPages(totalPages)
        .hasMore(it.hasNext())
        .limit(limit)
        .offset(offset + limit)
        .totalResults(finalSearchBookings.size())
        .responseLimitExceeded(false)
        .build();

  }

  @Override
  public List<RoomSubstitution> getSubstitutionListFromRule(String roomType, int adultsNumber,
      int childrenNumber, String bookingChannel) {

    var roomSubstitution = getRoomSubstitutionRule(roomType, adultsNumber, childrenNumber, bookingChannel);
    return roomSubstitution.getSubstitutionList()
            .stream()
            .distinct()
            .toList();
  }

  private RoomSubstitutionRuleResponse getRoomSubstitutionRule(String roomType,
      int adultsNumber, int childrenNumber, String bookingChannel) {
    log.debug("Getting Room Substitution Rule for roomType={}, adultsNumber={}, childrenNumber={}, "
        + "bookingChannel={}.", roomType, adultsNumber, childrenNumber, bookingChannel);

    var roomSubstitutionRule = rulesAgentClient.getRoomSubstitution(
        roomType, adultsNumber, childrenNumber, bookingChannel);

    var result = roomSubstitutionResponseMapper.toRoomSobstitutionModel(roomSubstitutionRule);

    log.debug("Obtained Room Substitution Rule (roomType={}, adultsNumber={}, childrenNumber={}, "
        + "bookingChannel={}): {}.", roomType, adultsNumber, childrenNumber, bookingChannel, result);

    return result;
  }

  private Set<String> extractReservationIds(ReservationsDetails reservationsDetails) {
    return reservationsDetails.getReservations().getReservationInfo()
        .stream()
        .map(reservationInfoType -> reservationInfoType.getReservationIdList()
            .stream()
            .filter(uniqueIDType -> uniqueIDType.getType().equals(RESERVATION))
            .map(UniqueIDType::getId)
            .collect(toSet()))
        .flatMap(Set::stream)
        .collect(toSet());
  }

  private Set<String> extractReservationId(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationsDetails) {
    return reservationsDetails.getReservations().getReservation()
        .stream()
        .map(reservationInfoType -> reservationInfoType.getReservationIdList()
            .stream()
            .filter(uniqueIDType -> uniqueIDType.getType().equals(RESERVATION))
            .map(UniqueIDType::getId)
            .collect(toSet()))
        .flatMap(Set::stream)
        .collect(toSet());
  }

  public CancelInformationResponse isCancellable(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation,
      String userDateTime, ZoneId hotelTimeZone) {

    final List<ResCancellationPolicyType> cancellationPolicies =
        reservation.getReservations()
        .getReservation()
        .get(0)
        .getReservationPolicies()
        .getCancellationPolicies();

    if (cancellationPolicies == null
        || cancellationPolicies.isEmpty()) {
      log.warn("No cancellation policies for this reservation.");
      return new CancelInformationResponse(false);
    }

    var absoluteDeadline = cancellationPolicies.get(0).getPolicy().getDeadline()
          .getAbsoluteDeadline();

    var reservationStatus = reservation.getReservations()
        .getReservation()
        .get(0)
        .getReservationStatus();

    var hotelZonedDateTime = convertDateToZonedDateTime(absoluteDeadline, hotelTimeZone);
    var userDate = convertStringToZonedDateTime(userDateTime, hotelTimeZone);

    return new CancelInformationResponse(
        hotelZonedDateTime.isAfter(userDate)
            && !reservationStatus.getValue().contains("Cancelled"));
  }

  public ZonedDateTime convertDateToZonedDateTime(Date dateToConvert, ZoneId zoneId) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateToConvert);
    LocalDate date = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH));
    LocalTime time = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND));
    return ZonedDateTime.of(date, time, zoneId);
  }

  public ZonedDateTime convertStringToZonedDateTime(String date, ZoneId zoneId) {
    date = date.replace(" ", "+");
    DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    ZonedDateTime zdtWithZoneOffset = ZonedDateTime.parse(date, formatter);

    return zdtWithZoneOffset.withZoneSameInstant(zoneId);
  }

  private ZonedDateTime convertStringToZonedDateTime(String date) {
    date = date.replace(" ", "+");
    DateTimeFormatter formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    return ZonedDateTime.parse(date, formatter);
  }


  @Override
  public CancelReservationResponse cancelReservation(
      CancelReservationRequest cancelReservationRequest) {
    log.debug("Entered cancelReservation for hotelId={} and {} reservations",
        cancelReservationRequest.getHotelId(),
        (cancelReservationRequest.getReservationIds() == null ? "null" :
            cancelReservationRequest.getReservationIds().size()));

    Map<String, DepositsResponse> refundedDeposits = new ConcurrentHashMap<>();

    // CC details must be captured before the deposit folio is sent, because the deposit folio
    // clears them from Opera. AtomicReference is required to write from inside the parallelStream.
    final var firstReservationCard =
        new AtomicReference<ReservationPaymentMethodType>();

    if (PaymentOption.PAY_NOW.equals(cancelReservationRequest.getPaymentOption())
        || PaymentOption.PAY_ON_ARRIVAL.equals(cancelReservationRequest.getPaymentOption())) {

      //FIXME: This should be improved
      cancelReservationRequest.getReservationIds().parallelStream().forEach(reservationId -> {

        var reservationDetails = ohipReservationClient.getReservations(
            cancelReservationRequest.getHotelId(),
            new HashSet<>(List.of(reservationId))).collectList().block();

        if (reservationDetails != null && !reservationDetails.isEmpty()) {

          // Capture CC details from the already-fetched reservationDetails BEFORE the deposit
          // folio clears them from Opera. All reservations share the same card so the last write wins.
          firstReservationCard.set(
              getReservationCardDetails(cancelReservationRequest.getHotelId(), reservationDetails));

          var reservationsNotOnHold = reservationDetails.stream()
              .filter(reservation -> reservation.getReservations().getReservation().stream()
                  .anyMatch(reservationType ->
                      reservationType.getRoomStay().getGuarantee().getOnHold() == null
                          || !reservationType.getRoomStay().getGuarantee().getOnHold()))
              .toList();
          //no reservation with on hold true
          var depositsResponse = handleReservationsNotOnHold(cancelReservationRequest,
              reservationId,
              reservationDetails, reservationsNotOnHold);
          if (depositsResponse != null) {
            refundedDeposits.put(reservationId, depositsResponse);
          }
        }
      });
    }
    var cancelReservationDetails = Flux.fromIterable(cancelReservationRequest.getReservationIds())
        .flatMap(reservationId -> ohipReservationClient.sendPostCancelReservationRequest(
            cancelReservationRequest.getHotelId(), reservationId,
            cancelReservationRequestOhipMapper.toCancelReservationModel(
                reservationId, cancelReservationRequest)))
        .collectList()
        .block();

    if (PaymentOption.PAY_NOW.equals(cancelReservationRequest.getPaymentOption())) {
      updatePaymentMethodToNonDigitalForCancel(cancelReservationRequest, firstReservationCard.get());
    }

    return cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
        cancelReservationDetails, refundedDeposits);
  }

  private void updatePaymentMethodToNonDigitalForCancel(
      CancelReservationRequest cancelReservationRequest,
      ReservationPaymentMethodType rsvCard) {
    if (ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(
        reservationOhipProperties,
        cancelReservationRequest.getHotelId())) {

      CompletableFuture.runAsync(concurrentTracer.wrap(() -> {

        log.info("Entered set non-digital payment method for hotelId = {} and reservationIds = {}",
            cancelReservationRequest.getHotelId(), cancelReservationRequest.getReservationIds());

        if (rsvCard == null || rsvCard.getPaymentCard() == null
            || rsvCard.getPaymentCard().getCardType() == null
            || rsvCard.getPaymentCard().getCardType().getValue() == null) {
          log.warn(
              "No payment card found for cancelled booking, skipping CC details update for"
                  + " hotelId={} reservationIds={}",
              cancelReservationRequest.getHotelId(),
              cancelReservationRequest.getReservationIds());
          return;
        }

        final var initialPaymentMethod =
            rsvCard.getPaymentCard().getCardType().getValue().toUpperCase();
        log.debug(
            "Setting non-digital payment method for hotelId = {} and reservationIds = {}: {}",
            cancelReservationRequest.getHotelId(), cancelReservationRequest.getReservationIds(),
            initialPaymentMethod);

        rsvCard.getPaymentCard()
            .setCardOrToken(
                uk.co.whitbread.hotel.ohip.adapter.generated.models.CardNumberTypeType.TOKEN);
        rsvCard.getPaymentCard()
            .setProcessing(
                uk.co.whitbread.hotel.ohip.adapter.generated.models.CardProcessingType.MANUAL);
        rsvCard.setPaymentMethod(initialPaymentMethod);
        rsvCard.setFolioView(reservationOhipProperties.getFolio());

        Flux.fromIterable(cancelReservationRequest.getReservationIds())
            .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                cancelReservationRequest.getHotelId(),
                reservationId, mapToChangeReservationWithPaymentCard(reservationId, rsvCard)))
            .collectList()
            .block();
      }));
    }
  }

  private ChangeReservation mapToChangeReservationWithPaymentCard(
      String reservationId, ReservationPaymentMethodType rsvCard) {
    var rsv = new HotelReservationInstructionType();

    var rsvId = new UniqueIDType();
    rsvId.setId(reservationId);
    rsvId.setType(UniqueIdTypeEnumDto.RESERVATION_TYPE.value());
    rsv.addReservationIdListItem(rsvId);

    rsv.addReservationPaymentMethodsItem(rsvCard);

    var changeReservation = new ChangeReservation();
    changeReservation.addReservationsItem(rsv);
    return changeReservation;
  }

  private DepositsResponse handleReservationsNotOnHold(
      CancelReservationRequest cancelReservationRequest,
      String reservationId,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationDetails,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationsNotOnHold) {
    //FIXME Improvements needed / logic rewrite
    if (!reservationsNotOnHold.isEmpty()) {
      List<String> reservationPackages = getPackagesCodesForVat(reservationDetails.get(0));
      var vatRulesDetails =
          rulesAgentClient.getVatCodes(getVatRegion(reservationDetails.get(0)),
              reservationPackages);

      var charges = (Objects.nonNull(cancelReservationRequest.getChargesByReservationIds())
          && Objects.nonNull(
          cancelReservationRequest.getChargesByReservationIds().get(reservationId)))
          ? cancelReservationRequest.getChargesByReservationIds().get(reservationId) : null;

      var depositFolioCriteriaNegativeAmounts =
          mapToDepositCriteria(depositFoliosRequestMapper
                  .toDepositFolioRequestModel(cancelReservationRequest, reservationId),
              reservationDetails.get(0),
              vatRulesDetails, getCityTaxDetailsPerDay(cancelReservationRequest.getHotelId(),
                  reservationId, reservationDetails.get(0)), charges);
      setDigitalPaymentMethodForPayNow(cancelReservationRequest,
          depositFolioCriteriaNegativeAmounts);
      final BigDecimal deposit;
      // DNRQ-56745 - Migrated reservations have depositPolicies->amountPaid set to "0"
      // Fetch the deposit from rate info
      if (isMigratedReservation(
          reservationDetails.get(0).getReservations().getReservation().get(0))) {
        deposit = Optional.ofNullable(getReservationAmounts(cancelReservationRequest.getHotelId(),
                Set.of(reservationId), null))
            .map(resAmount -> resAmount.getDeposit().negate())
            .orElse(BigDecimal.ZERO);

        depositFolioCriteriaNegativeAmounts.getCriteria().getPayments().get(0)
            .getPostingAmount().setAmount(deposit);
      } else {
        deposit = depositFolioCriteriaNegativeAmounts.getCriteria().getPayments().get(0)
            .getPostingAmount()
            .getAmount();
      }

      if (deposit.compareTo(BigDecimal.ZERO) < 0) {
        return postedDepositsResponseOhipMapper
            .toModel(ohipReservationClient.sendDepositFoliosRequest(
                cancelReservationRequest.getHotelId(),
                reservationId, depositFolioCriteriaNegativeAmounts));
      } else {
        log.info("No deposits to post for cancellation for reservationId={}", reservationId);
        // For PAY_ON_ARRIVAL bookings the deposit policy may report zero, but actual charges
        // can already exist in the folio (e.g. ACI transactions). Fall back to the ACI amount.
        if (PaymentOption.PAY_ON_ARRIVAL.equals(cancelReservationRequest.getPaymentOption())) {
          return handlePayOnArrivalAciCharges(cancelReservationRequest, reservationId,
              depositFolioCriteriaNegativeAmounts);
        }
      }
    }

    return null;
  }

  private DepositsResponse handlePayOnArrivalAciCharges(
      CancelReservationRequest cancelReservationRequest,
      String reservationId,
      DepositFolioCriteria depositFolioCriteria) {

    BigDecimal aciAmount = Optional.ofNullable(
            extractAmountFromFolioWindows(
                ohipReservationClient.getFoliosAciAmount(
                    cancelReservationRequest.getHotelId(), reservationId)))
        .map(BigDecimal::abs)
        .orElse(BigDecimal.ZERO);

    if (BigDecimal.ZERO.compareTo(aciAmount) != 0) {
      log.info(
          "PAY_ON_ARRIVAL folio charges found via ACI for reservationId={}, amount={}",
          reservationId, aciAmount);
      depositFolioCriteria.getCriteria().getPayments().get(0)
          .getPostingAmount().setAmount(aciAmount.negate());
      return postedDepositsResponseOhipMapper
          .toModel(ohipReservationClient.sendDepositFoliosRequest(
              cancelReservationRequest.getHotelId(), reservationId, depositFolioCriteria));
    }

    log.info("No ACI charges found for PAY_ON_ARRIVAL cancellation for reservationId={}",
        reservationId);
    return null;
  }

  private boolean isMigratedReservation(HotelReservationType reservationDetails) {
    return Optional.ofNullable(reservationDetails.getExternalReferences())
        .map(refs -> refs.stream()
            .anyMatch(ref -> ref.getIdContext().equals(EXTERNAL_REF_MIGRATION_ID_CONTEXT)))
        .orElse(false);
  }


  @Override
  public UpdateReasonForStayResponse updateReasonForStay(
      UpdateReasonForStayRequest updateReasonForStayRequest) {
    var hotelId = updateReasonForStayRequest.getHotelId();
    var reservationIds = updateReasonForStayRequest.getReservationIds();

    log.debug("Entered updateReasonForStay for hotelId={} and reservationIds={}", hotelId,
        String.join(", ", reservationIds));

    var changeReservations = Flux.fromIterable(reservationIds)
        .flatMap(reservationId ->
            ohipReservationClient.sendChangeReservationRequest(
                hotelId, reservationId, mapToUpdateReasonForStayOhip(updateReasonForStayRequest)))
        .collectList()
        .block();

    return updateReasonForStayResponseOhipMapper.toUpdateReasonForStayResponseModel(
        changeReservations);
  }

  @Override
  public void updateReservationOverrideReasons(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest) {
    var hotelId = updateReservationOverrideReasonsRequest.getHotelId();
    var reservationIds = updateReservationOverrideReasonsRequest.getReservationIds();
    log.debug("Entered updateReservationOverrideReasons for hotelId={} and reservationIds={}",
        hotelId,
        String.join(", ", reservationIds));

    ChangeReservation changeReservationRequest = mapOverrideReasonsToUdfc08Ohip(
        updateReservationOverrideReasonsRequest);
    Flux.fromIterable(reservationIds)
        .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                hotelId, reservationId, changeReservationRequest),
            reservationOhipProperties.getMaxConcurrency())
        .collectList()
        .block();
  }

  @Override
  public void updateReservationCcAgentId(
      UpdateReservationCcAgentIdRequest updateReservationCcAgentIdRequest, final boolean shouldRunInAsync) {

    var hotelId = updateReservationCcAgentIdRequest.getHotelId();
    var reservationIds = updateReservationCcAgentIdRequest.getReservationIds();
    log.debug("Entered updateReservationCcAgentId for hotelId={} and reservationIds={}",
        hotelId, String.join(", ", reservationIds));

    if (updateReservationCcAgentIdRequest.isClearFirst()) {
      processUpdateCcAgentIdRequest(
          updateReservationCcAgentIdRequest.toBuilder().ccAgentId(null).build(), hotelId,
          reservationIds);
    }
    if (shouldRunInAsync) {
      CompletableFuture.runAsync(() -> {
        try {
          Thread.sleep(reservationOhipProperties.getUpdateCcAgentIdThreadSleep());
        } catch (InterruptedException e) {
          log.error("Could not sleep before updating ccAgentId on reservationIds: {}", reservationIds);
          ExceptionLogger.log(log, e);
        }
        processUpdateCcAgentIdRequest(updateReservationCcAgentIdRequest, hotelId, reservationIds);
      });
    } else {
      processUpdateCcAgentIdRequest(updateReservationCcAgentIdRequest, hotelId, reservationIds);
    }
  }

  private void processUpdateCcAgentIdRequest(UpdateReservationCcAgentIdRequest updateReservationCcAgentIdRequest,
      String hotelId, Set<String> reservationIds) {
    ChangeReservation changeReservationRequest = mapCcAgentIdToUdfc08Ohip(
        updateReservationCcAgentIdRequest);
    Flux.fromIterable(reservationIds)
        .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                hotelId, reservationId, changeReservationRequest),
            reservationOhipProperties.getMaxConcurrency())
        .collectList()
        .block();
  }

  @Override
  public DepositsResponse getDepositsForReservationId(String hotelId, String reservationId) {
    var retrievedDepositFolio = ohipReservationClient
        .getDepositsByReservationId(hotelId, reservationId);
    DepositsResponse depositsResponse = new DepositsResponse();
    try {
      depositsResponse = depositsResponseOhipMapper.toModel(retrievedDepositFolio);
    } catch (Exception ex) {
      log.error("Error trying to map deposits.", ex);
    }
    return depositsResponse;
  }

  @Override
  public CancellationPoliciesResponse getCancellationPolicies(
      Set<String> reservationIds, String hotelId, String rateCode, String arrivalDate,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationsList) {
    log.debug(
        "Entered getCancellationPolicies for hotelId={} and {} reservation ids or {} rateCode",
        sanitizeInput(hotelId), reservationIds.size(), sanitizeInput(rateCode));

    var cancellationPolicies = CancellationPoliciesResponse.builder().build();
    var hotelTimeZone = ohipReservationClient.getHotelConfig(hotelId)
        .getHotelConfigInfo()
        .getPropertyControls()
        .getDateTimeFormatting()
        .getTimeZoneRegion();

    if (!reservationIds.isEmpty()) {
      cancellationPolicies =
          cancellationPoliciesByReservationId(hotelId, reservationIds, hotelTimeZone,
              reservationsList);
    } else if (!CollectionUtils.isEmpty(Collections.singleton(rateCode))) {
      cancellationPolicies = cancellationPoliciesByRateCodeAndArrivalDate(
          hotelId, rateCode, hotelTimeZone, arrivalDate);
    }
    return cancellationPolicies;
  }

  public MarketingPreferencesResponse getMarketingPreferences(String hotelId,
      String reservationId) {
    var reservations = ohipReservationClient.getReservations(hotelId, Set.of(reservationId))
        .collectList()
        .block();

    if (reservations == null || reservations.isEmpty()) {
      return null;
    }

    var profileIds = getProfileIdsByType(List.of(reservations.get(0)), RESERVATIONCONTACT);

    if (profileIds.isEmpty()) {
      return null;
    }

    Map<String, Profile> profilesByIds = getProfilesMap(profileIds);

    if (profilesByIds.isEmpty()) {
      return null;
    }
    Profile bookerProfile = profilesByIds.values().stream().findFirst().orElse(null);
    setWbLanguageCode(bookerProfile);
    return marketingPreferencesResponseOhipMapper.toModel(bookerProfile);
  }

  private ChangeReservation mapOverrideReasonsToUdfc08Ohip(
      UpdateReservationOverrideReasonsRequest updateReservationOverrideReasonsRequest) {
    return updateReservationOverrideReasonsRequestOhipMapper.toDto(
        updateReservationOverrideReasonsRequest);
  }

  private ChangeReservation mapCcAgentIdToUdfc08Ohip(
      UpdateReservationCcAgentIdRequest updateReservationCcAgentIdRequest) {
    return updateReservationCcAgentIdRequestOhipMapper.toDto(updateReservationCcAgentIdRequest);
  }

  @Override
  public void updateCancellationPolicy(
      UpdateCancellationPolicyRequest updateRequest) {
    var reservation = ohipReservationClient.getReservations(updateRequest.getHotelId(),
            Set.of(updateRequest.getReservationId()))
        .flatMap(res -> Mono.justOrEmpty(res).onErrorResume(e -> Mono.empty()))
        .next()
        .block();

    var resCancellationPolicyType = getResCancellationPolicyType(reservation);
    var policyId = getPolicyId(resCancellationPolicyType);

    log.info("Delete absolute deadline for hotelId : {}, reservationId : {}",
        updateRequest.getHotelId(),
        updateRequest.getReservationId());

    ohipReservationClient.deleteCancellationPolicy(updateRequest.getHotelId(),
        updateRequest.getReservationId(), policyId);

    var cancellationPolicyRequest = updateCancellationPolicyRequestOhipMapper
        .toReservationCancellationPolicyCriteriaDto(resCancellationPolicyType,
            updateRequest.getHotelId(),
            updateRequest.getReservationId(),
            updateRequest.getAbsoluteDeadline());

    log.info("Update absolute deadline to : {} for reservationId : {}",
        updateRequest.getAbsoluteDeadline(), updateRequest.getReservationId());
    ohipReservationClient.createCancellationPolicy(updateRequest.getHotelId(),
        updateRequest.getReservationId(),
        cancellationPolicyRequest);
  }

  @Override
  public List<String> getWbRoomTypes() {
    return rulesAgentProperties.getWbRoomTypes();
  }

  @Override
  public CopyReservationsResponse copyReservations(
      CopyReservationsRequest copyReservationsRequest) {
    List<String> originalReservationsList = new LinkedList<>();
    List<String> copiedReservationsList = new LinkedList<>();
    return ohipReservationClient.getReservations(copyReservationsRequest.getHotelId(),
            copyReservationsRequest.getReservationIds())
        .map(reservation -> {
          reservation.getReservations().getReservation().stream().forEach(
              res -> {
                originalReservationsList.add(res.getReservationIdList().get(0).getId());
              }
          );
          return copyReservationsRequestOhipMapper.toCreateReservationDto(reservation,
              copyReservationsRequest.getExternalReferenceId());
        })
        .flatMap(createReservation -> ohipReservationClient.sendCreateReservationRequest(
                copyReservationsRequest.getHotelId(), createReservation),
            reservationOhipProperties.getMaxConcurrency())
        .map(reservationStatusOhip -> {
          copiedReservationsList.add(reservationStatusOhip.getUniqueIdReservation());
          return reservationStatusOhip.getUniqueIdReservation();
        })
        .collect(toSet())
        .flatMapMany(i -> ohipReservationClient.getReservations(
            copyReservationsRequest.getHotelId(), i))
        .collectList()
        .map(reservationList ->
            copyReservationsResponseOhipMapper.toCopyReservationsResponseModel(reservationList,
                originalReservationsList, copiedReservationsList))
        .block();
  }

  @Override
  public void updateReservations(UpdateReservationsRequest updateReservationsRequest,
      boolean overrideInventoryCheck,
      String sourceCode, boolean isRoomTypeCharged, String originalReservationId,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> origRes) {
    log.info("Entered updateReservations for updating reservations request: bookingChannel={}",
        updateReservationsRequest.getBookingChannel());
    var updatedResDetails =
        Flux.fromIterable(updateReservationsRequest.getUpdateReservationsRequest())
            .flatMap(
                updResRequest -> {
                  ChangeReservation changeReservation = getUpdateReservationDetails(
                          updResRequest, sourceCode, overrideInventoryCheck,
                          isRoomTypeCharged, originalReservationId, origRes);
                  if (StringUtils.isNotEmpty(updateReservationsRequest.getDistributionIATANumber())) {
                    changeReservation.getReservations().forEach(reservation -> {
                      var userDefinedTypes = reservation.getUserDefinedFields();
                      if (userDefinedTypes != null) {
                        final var characterUDF = new CharacterUDFType();
                        characterUDF.setName(UDFC_16);
                        characterUDF.setValue(updateReservationsRequest.getDistributionIATANumber());
                        userDefinedTypes.addCharacterUDFsItem(characterUDF);
                        final var numericUDF = new NumericUDFType();
                        double numberDouble = Double.parseDouble(updateReservationsRequest.getDistributionIATANumber());
                        numericUDF.setName(UDFN_01);
                        numericUDF.setValue(BigDecimal.valueOf(numberDouble));
                        userDefinedTypes.addNumericUDFsItem(numericUDF);
                        reservation.setUserDefinedFields(userDefinedTypes);
                      }
                    });
                  }
                  processUserDefinedFields(updateReservationsRequest, changeReservation);

                  return ohipReservationClient.sendChangeReservationRequest(
                      updResRequest.getHotelId(),
                      updResRequest.getReservationType().getId(), changeReservation);
                })
            .collectList()
            .block();

    log.debug("Successfully updated reservations with result={}",
        Arrays.toString(
            updatedResDetails != null ? updatedResDetails.toArray() : new String[]{""}));
  }

  @Override
  public void updateReservations(UpdateReservationsRequest updateReservationsRequest,
      boolean overrideInventoryCheck, String sourceCode) {
    this.updateReservations(updateReservationsRequest, overrideInventoryCheck, sourceCode, false,
        null, null);
  }

  @Override
  public List<ChangeReservation> getEditRoomChangeReservations(UpdateReservationsRequest updateReservationsRequest,
                                                               boolean overrideInventoryCheck, String sourceCode) {
    return this.getStayDateChangeReservation(
            updateReservationsRequest, overrideInventoryCheck, sourceCode, false, null, null
    );
  }

  private ChangeReservation getUpdateReservationDetails(
          UpdateReservationRequest updResRequest, String sourceCode, boolean overrideInventoryCheck,
          boolean isRoomTypeCharged, String originalReservationId,
          List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> origRes
  ) {
    var changeReservation = updateReservationRequestOhiMapper.toDto(updResRequest);
    changeReservation.getReservations().get(0).getRoomStay().getRoomRates()
            .forEach(roomRate -> {
              roomRate.setSourceCode(sourceCode);
              //Market Code is cleared from the reservation if not sent in PUT request
              roomRate.setMarketCode(reservationOhipProperties.getDefaultMarketCode());
            });
    mapOverrideInventoryCheck(changeReservation, overrideInventoryCheck);
    if (isRoomTypeCharged) {
      mapRoomTypeCharged(changeReservation);
    }
    if (originalReservationId != null) {
      //mapUserDefinedFields(originalReservationId, changeReservation, origRes);
      mapAdditionalReservationDetails(originalReservationId, changeReservation,
              origRes);
    }
    return changeReservation;
  }

  @Override
  public List<ChangeReservation> getStayDateChangeReservation(
          UpdateReservationsRequest updateReservationsRequest,
          boolean overrideInventoryCheck,
          String sourceCode, boolean isRoomTypeCharged, String originalReservationId,
          List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> origRes
  ) {

    List<ChangeReservation> changeReservations = new ArrayList<>();
    updateReservationsRequest.getUpdateReservationsRequest().forEach(updResRequest -> {
      ChangeReservation updateReservationDetails = getUpdateReservationDetails(
              updResRequest, sourceCode, overrideInventoryCheck, isRoomTypeCharged, originalReservationId, origRes);
      changeReservations.add(updateReservationDetails);
    });

    return changeReservations;
  }

  @Override
  public void updateReservationsWithExternalRef(String hotelId, Set<String> reservationIds, String externalReference) {
    Flux.fromIterable(reservationIds)
            .flatMap(
                    reservationId -> {
                      var changeReservation = mapToChangeReservationWithExternalRef(reservationId,
                              hotelId, externalReference);
                      return ohipReservationClient.sendChangeReservationRequest(
                              hotelId, reservationId, changeReservation);
                    })
            .collectList()
            .block();
  }

  @Override
  public  void updateRoutingInstructionsWithPayeeInfo(String hotelId, Set<String> reservationIds) {
    Flux.fromIterable(reservationIds)
            .flatMap(
                    reservationId -> {
                      var reservationDetails = ohipReservationClient
                              .getReservation(hotelId, reservationId);
                      var changeReservation = mapToChangeReservationRoutings(
                              Objects.requireNonNull(reservationDetails.block()));
                      return ohipReservationClient.sendChangeReservationRequest(
                              hotelId, reservationId, changeReservation);
                    })
            .collectList()
            .block();
  }

  @Override
  public void updateReservationsToPayOnArrival(List<ReservationById> reservations) {
    Flux.fromIterable(reservations)
        .flatMap(reservationById -> ohipReservationClient.sendChangeReservationRequest(
            reservationById.getHotelId(),
            reservationById.getReservationId(),
            payOnArrivalReservationRequestOhipMapper.toChangeReservationForModel(reservationById))
        )
        .collectList()
        .block();
  }

  private ChangeReservation mapToChangeReservationWithExternalRef(String reservationId, String hotelId,
                                                                  String externalReference) {

    UniqueIDType uniqueIDType = new UniqueIDType();

    ExternalReferenceType externalReferenceType = new ExternalReferenceType();

    uniqueIDType.setType(RESERVATION);
    uniqueIDType.setId(reservationId);

    externalReferenceType.setId(externalReference);
    externalReferenceType.setIdContext(reservationOhipProperties.getContextId());

    HotelReservationInstructionType hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setReservationIdList(List.of(uniqueIDType));
    hotelReservationInstructionType.setExternalReferences(List.of(externalReferenceType));
    hotelReservationInstructionType.setHotelId(hotelId);

    ChangeReservation changeReservation = new ChangeReservation();
    changeReservation.setReservations(List.of(hotelReservationInstructionType));

    return changeReservation;
  }

  private ChangeReservation mapToChangeReservationRoutings(
          uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {

    var reservationProfiles = reservation.getReservations().getReservation().get(0)
            .getReservationProfiles();
    ChangeReservation changeReservation = new ChangeReservation();

    if (Objects.nonNull(reservationProfiles)) {
      var companyProfileList = reservationProfiles.getReservationProfile().stream()
              .filter(reservationProfileType -> COMPANY.equals(reservationProfileType.getReservationProfileType()))
              .toList();

      PayeeInfoType payeeInfoType = new PayeeInfoType();
      UniqueIDType payeeId = new UniqueIDType();

      if (!companyProfileList.isEmpty()) {
        companyProfileList.get(0).getProfileIdList()
                .stream()
                .filter(profileId -> PROFILE.equals(profileId.getType()))
                .map(UniqueIDType::getId)
                .findFirst()
                .ifPresent(payeeId::setId);
        payeeId.setType(PROFILE);

        payeeInfoType.setPayeeId(payeeId);

        var reservationFolio = reservation.getReservations().getReservation().get(0).getRoutingInstructions()
                .get(0).getFolio();

        RoutingInfoTypeFolio folio = new RoutingInfoTypeFolio();
        folio.setPayeeInfo(payeeInfoType);
        folio.setInstructions(reservationFolio.getInstructions());
        folio.setFolioWindowNo(reservationFolio.getFolioWindowNo());
        HotelReservationInstructionType reservationInstructionType = new HotelReservationInstructionType();
        RoutingInfoType routingInfoType = new RoutingInfoType();

        routingInfoType.setFolio(folio);
        reservationInstructionType.setRoutingInstructions(List.of(routingInfoType));
        changeReservation.setReservations(List.of(reservationInstructionType));
      }
    }
    return changeReservation;
  }

  private ChangeReservation mapUserDefinedFields(String originalReservationId,
      ChangeReservation changeReservation,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation>
          origRes) {
    var hotelId = changeReservation.getReservations().get(0).getHotelId();
    var originalReservationById = origRes;
    if (Objects.isNull(originalReservationById)) {
      originalReservationById = ohipReservationClient
          .getReservations(hotelId, Set.of(originalReservationId))
          .collectList()
          .block();
    }

    if (Objects.isNull(originalReservationById) || CollectionUtils.isEmpty(
        originalReservationById)) {
      var ex = new HotelReservationException(ErrorCode.DIGITAL_NO_RESERV_EXCEPTION,
          String.format("No reservations were found with given id:%s",
              originalReservationId));
      ExceptionLogger.log(log, ex);
      throw ex;
    }

    var userDefinedFieldsFromOriginal = originalReservationById.get(0).getReservations()
        .getReservation()
        .get(0).getUserDefinedFields();

    if (userDefinedFieldsFromOriginal != null) {
      changeReservation.getReservations().forEach(reservationToUpdate -> {
        var reservationToUpdateById = ohipReservationClient
            .getReservations(hotelId,
                Set.of(reservationToUpdate.getReservationIdList().get(0).getId()))
            .collectList()
            .block();
        var userDefinedFieldsToUpdate = reservationToUpdateById.get(0).getReservations()
            .getReservation()
            .get(0).getUserDefinedFields();

        if (userDefinedFieldsToUpdate == null) {
          uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType userDefinedFieldsType =
              new UserDefinedFieldsType();
          List<uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType> characterUDFs = new ArrayList<>();
          userDefinedFieldsFromOriginal.getCharacterUDFs().forEach(characterUdfOriginal -> {

            uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType characterUDFTypeToUpdate =
                new CharacterUDFType();

            characterUDFTypeToUpdate.setName(characterUdfOriginal.getName());
            characterUDFTypeToUpdate.setValue(characterUdfOriginal.getValue());

            characterUDFs.add(characterUDFTypeToUpdate);
          });
          userDefinedFieldsType.setCharacterUDFs(characterUDFs);
          reservationToUpdate.setUserDefinedFields(userDefinedFieldsType);
        }
      });
    }

    return changeReservation;
  }

  private ChangeReservation mapAdditionalReservationDetails(String originalReservationId,
      ChangeReservation changeReservation,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models
          .Reservation> origRes) {

    // map UDFs, purpose of stay and booker details
    var hotelId = changeReservation.getReservations().get(0).getHotelId();
    var originalReservationById = origRes;
    if (Objects.isNull(originalReservationById)) {
      originalReservationById = ohipReservationClient
          .getReservations(hotelId, Set.of(originalReservationId))
          .collectList()
          .block();
    }

    if (Objects.isNull(originalReservationById) || CollectionUtils.isEmpty(
        originalReservationById)) {
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_NO_RESERVATIONS_OPERA_EXCEPTION,
          String.format("No reservations were found in Opera with given id: %s",
              originalReservationId));
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    var userDefinedFieldsFromOriginal = originalReservationById.get(0).getReservations()
        .getReservation()
        .get(0).getUserDefinedFields();

    var purposeOfStayFromOriginal = originalReservationById.get(0).getReservations()
        .getReservation()
        .get(0).getAdditionalGuestInfo()
        .getPurposeOfStay();

    var reservationProfilesFromOriginal = originalReservationById.get(0).getReservations()
        .getReservation()
        .get(0).getReservationProfiles();

    changeReservation.getReservations().forEach(reservationToUpdate -> {
      var reservationToUpdateById = ohipReservationClient
          .getReservations(hotelId,
              Set.of(reservationToUpdate.getReservationIdList().get(0).getId()))
          .collectList()
          .block();

      //set purpose of stay
      if (purposeOfStayFromOriginal != null) {
        var purposeOfStayToUpdate = reservationToUpdateById.get(0).getReservations()
            .getReservation()
            .get(0).getAdditionalGuestInfo();

        if (purposeOfStayToUpdate == null) {
          ResGuestAdditionalInfoType additionalGuestInfo = new ResGuestAdditionalInfoType();
          additionalGuestInfo.setPurposeOfStay(purposeOfStayFromOriginal);
          reservationToUpdate.setAdditionalGuestInfo(additionalGuestInfo);
        } else {
          reservationToUpdate.setAdditionalGuestInfo(purposeOfStayToUpdate);
        }
      }

      // set reservation contact profile
      if (reservationProfilesFromOriginal != null) {
        var reservationProfilesToUpdate = reservationToUpdateById.get(0).getReservations()
            .getReservation()
            .get(0).getReservationProfiles();
        if (reservationProfilesToUpdate == null) {
          HotelReservationTypeReservationProfiles reservationProfiles = new HotelReservationTypeReservationProfiles();
          List<ReservationProfileType> reservationProfile = new ArrayList<>();
          ReservationProfileType reservationProfileType = new ReservationProfileType();
          List<UniqueIDType> profileIdList = new ArrayList<>();
          UniqueIDType profile = new UniqueIDType();

          var reservationContactProfile = reservationProfilesFromOriginal.getReservationProfile()
              .stream()
              .filter(
                  reservationProfileOriginal -> reservationProfileOriginal.getReservationProfileType()
                      .equals(RESERVATIONCONTACT))
              .findFirst();
          if (reservationContactProfile.isPresent()) {
            profile.setType(reservationContactProfile.get().getProfileIdList().get(0).getType());
            profile.setId(reservationContactProfile.get().getProfileIdList().get(0).getId());
            profileIdList.add(profile);

            reservationProfileType.setProfileIdList(profileIdList);
            reservationProfileType.setReservationProfileType(reservationContactProfile.get()
                .getReservationProfileType());
            reservationProfile.add(reservationProfileType);

            reservationProfiles.setReservationProfile(reservationProfile);
          }
          reservationToUpdate.setReservationProfiles(reservationProfiles);
        }
      }

      // set UDFs
      if (userDefinedFieldsFromOriginal != null) {
        var userDefinedFieldsToUpdate = reservationToUpdateById.get(0).getReservations()
            .getReservation()
            .get(0).getUserDefinedFields();

        if (userDefinedFieldsToUpdate == null) {
          uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType userDefinedFieldsType =
              new UserDefinedFieldsType();
          List<uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType> characterUDFs = new ArrayList<>();
          userDefinedFieldsFromOriginal.getCharacterUDFs().forEach(characterUdfOriginal -> {

            uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType characterUDFTypeToUpdate =
                new CharacterUDFType();

            characterUDFTypeToUpdate.setName(characterUdfOriginal.getName());
            characterUDFTypeToUpdate.setValue(characterUdfOriginal.getValue());

            characterUDFs.add(characterUDFTypeToUpdate);
          });
          userDefinedFieldsType.setCharacterUDFs(characterUDFs);
          reservationToUpdate.setUserDefinedFields(userDefinedFieldsType);
        }
      }
    });

    return changeReservation;
  }

  private String getCompanyName(ReservationsDetails reservationsDetails) {
    if (reservationsDetails.getReservations().getReservationInfo().get(0).getAttachedProfiles()
        == null) {
      return null;
    }

    return reservationsDetails.getReservations().getReservationInfo().get(0).getAttachedProfiles()
        .stream().filter(
            resAttachedProfileType -> ResProfileTypeType.COMPANY.equals(
                resAttachedProfileType.getReservationProfileType())).findFirst()
        .map(ResAttachedProfileType::getName)
        .orElse(null);
  }

  private String getCompanyName(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationsDetails) {
    if (reservationsDetails.getReservations().getReservation().get(0).getReservationProfiles()
        == null) {
      return null;
    }

    return reservationsDetails.getReservations().getReservation().get(0)
        .getReservationProfiles().getReservationProfile()
        .stream().filter(
            resAttachedProfileType -> ResProfileTypeType.COMPANY.equals(
                resAttachedProfileType.getReservationProfileType())).findFirst()
        .map(ReservationProfileType::getProfile)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType::getCompany)
        .map(CompanyType::getCompanyName)
        .orElse(null);
  }

  private Profile getBookerProfileById(ReservationsDetails reservationsDetails,
      Map<String, Profile> profiles) {
    if (reservationsDetails.getReservations().getReservationInfo().get(0).getAttachedProfiles()
        == null) {
      return null;
    }
    String bookerProfileId =
        reservationsDetails.getReservations().getReservationInfo().get(0).getAttachedProfiles()
            .stream().filter(
                resAttachedProfileType -> RESERVATIONCONTACT.equals(
                    resAttachedProfileType.getReservationProfileType())).findFirst()
            .map(resAttachedProfileType -> resAttachedProfileType.getProfileIdList().get(0).getId())
            .orElse(null);
    if (bookerProfileId != null) {
      return profiles.get(bookerProfileId);
    }

    return null;
  }

  private Profile getBookerProfileById(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservationsDetails,
      Map<String, Profile> profiles) {
    if (reservationsDetails.getReservations().getReservation().get(0).getReservationProfiles()
        == null) {
      return null;
    }
    String bookerProfileId =
        reservationsDetails.getReservations().getReservation().get(0)
            .getReservationProfiles().getReservationProfile()
            .stream().filter(
                resAttachedProfileType -> RESERVATIONCONTACT.equals(
                    resAttachedProfileType.getReservationProfileType())).findFirst()
            .map(resAttachedProfileType -> resAttachedProfileType.getProfileIdList().get(0).getId())
            .orElse(null);

    if (bookerProfileId != null) {
      return profiles.get(bookerProfileId);
    }

    return null;
  }

  private ProfileType getBookerProfileById(String profileId) {
    return bookerProfileOhipMapper.toModel(getProfileById(profileId));
  }

  private Profile getProfileById(String profileId) {
    return ohipReservationClient.sendGetProfilesByProfileIds(Set.of(profileId))
        .stream()
        .findFirst()
        .orElse(null);
  }

  private ProfileType getCompanyProfileById(String profileId) {
    return companyProfileOhipMapper.toModel(getProfileById(profileId));
  }

  private void updateCompanyId(BookerDetailsCnpRequest bookerDetailsCnpRequest, Set<String> companyProfileIds) {
    var idProfil = companyProfileIds.iterator().next();
    final var companyProfile = getCompanyProfileById(idProfil);
    if (companyProfile == null) {
      var message = String.format("Error while trying to get the profile %s", idProfil);
      var exception = new HotelReservationException(
              ErrorCode.DIGITAL_GET_PROFILE_EXCEPTION, message);
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    var companyOhipProfile = companyProfileOhipMapper.toDto(companyProfile,
            bookerDetailsCnpRequest);
    ohipReservationClient.sendUpdateProfileRequest(bookerDetailsCnpRequest.getHotelId(),
            companyOhipProfile);

  }

  @Override
  public void updateBookerDetails(BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    final var reservationList =
        ohipReservationClient.getReservations(bookerDetailsCnpRequest.getHotelId(),
            new HashSet<>(bookerDetailsCnpRequest.getReservationIds())).collectList().block();

    if (reservationList != null) {
      // Update booker profile.
      ProfileType bookerProfile = null;
      final var bookerProfileIds = getProfileIdsByType(reservationList, RESERVATIONCONTACT);
      if (!bookerProfileIds.isEmpty()) {
        bookerProfile = getBookerProfileById(bookerProfileIds.iterator().next());

        var bookerOhipProfile = bookerProfileOhipMapper.toDto(bookerProfile,
            bookerDetailsCnpRequest);
        ohipReservationClient.sendUpdateProfileRequest(bookerDetailsCnpRequest.getHotelId(),
            bookerOhipProfile);
      }

      // Update company profile.
      final var companyProfileIds = getProfileIdsByType(reservationList,
          ResProfileTypeType.COMPANY);
      // Company profile already assigned to reservation.
      if (!companyProfileIds.isEmpty()) {
        // Company Name is provided
        if (isNotBlank(bookerDetailsCnpRequest.getBooker().getCompanyName())) {
          updateCompanyId(bookerDetailsCnpRequest, companyProfileIds);
          // Company name should be deleted
        } else {
          final var bookerProfileId =
              bookerProfile == null ? null : bookerProfile.getProfileId().getId();

          Flux.fromIterable(bookerDetailsCnpRequest.getReservationIds())
              .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                      bookerDetailsCnpRequest.getHotelId(), reservationId,
                      updateReservationAttachedProfilesRequestOhipMapper.toDto(
                          bookerDetailsCnpRequest.getHotelId(),
                          reservationId, bookerProfileId, null)),
                  reservationOhipProperties.getMaxConcurrency())
              .collectList()
              .block();
        }
        // No company profile was assign before to reservation
      } else {
        if (StringUtils.isNotEmpty(bookerDetailsCnpRequest.getBooker().getCompanyName())) {
          var reservationCompanyResponse = ohipReservationClient.sendPostProfileRequest(
              bookerDetailsCnpRequest.getHotelId(),
              reservationCompanyRequestOhipMapper.toDto(
                  bookerDetailsCnpRequest.getBooker().getCompanyName()));

          final var companyProfileId = getProfileId(reservationCompanyResponse);
          final var bookerProfileId =
              bookerProfile == null ? null : bookerProfile.getProfileId().getId();

          Flux.fromIterable(bookerDetailsCnpRequest.getReservationIds())
              .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                      bookerDetailsCnpRequest.getHotelId(), reservationId,
                      updateReservationAttachedProfilesRequestOhipMapper.toDto(
                          bookerDetailsCnpRequest.getHotelId(),
                          reservationId, bookerProfileId, companyProfileId)),
                  reservationOhipProperties.getMaxConcurrency())
              .collectList()
              .block();

        }
      }
    }
  }

  @Override
  public void addBookerDetails(BookerDetailsCnpRequest bookerDetailsCnpRequest) {

    final var reservationList =
            ohipReservationClient.getReservations(bookerDetailsCnpRequest.getHotelId(),
                    new HashSet<>(bookerDetailsCnpRequest.getReservationIds())).collectList().block();
    if (reservationList != null) {
      final var bookerProfileIds = getProfileIdsByType(reservationList, RESERVATIONCONTACT);
      final var companyProfileIds = getProfileIdsByType(reservationList,
              ResProfileTypeType.COMPANY);

      //add booker profile and company profile
      if (!companyProfileIds.isEmpty() || !bookerProfileIds.isEmpty()) {
        final var bookerProfileId =
                bookerProfileIds.isEmpty() ? null : bookerProfileIds.iterator().next();
        final var companyProfileId =
                companyProfileIds.isEmpty() ? null : companyProfileIds.iterator().next();
        Flux.fromIterable(bookerDetailsCnpRequest.getReservationIds())
                .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                                bookerDetailsCnpRequest.getHotelId(), reservationId,
                                updateReservationAttachedProfilesRequestOhipMapper.toDto(
                                        bookerDetailsCnpRequest.getHotelId(),
                                        reservationId, bookerProfileId, companyProfileId)),
                        reservationOhipProperties.getMaxConcurrency())
                .collectList()
                .block();
      }
    }
  }


  @Override
  public List<ChangeReservation> getBookerDetailsChangeReservation(BookerDetailsCnpRequest bookerDetailsCnpRequest) {
    final var reservationList =
            ohipReservationClient.getReservations(bookerDetailsCnpRequest.getHotelId(),
                    new HashSet<>(bookerDetailsCnpRequest.getReservationIds())).collectList().block();
    List<ChangeReservation> bookerDetailsChangeReservation = new ArrayList<>();

    if (reservationList != null) {
      // Update booker profile.
      ProfileType bookerProfile = null;
      final var bookerProfileIds = getProfileIdsByType(reservationList, RESERVATIONCONTACT);
      if (!bookerProfileIds.isEmpty()) {
        bookerProfile = getBookerProfileById(bookerProfileIds.iterator().next());

        ohipReservationClient.sendUpdateProfileRequest(bookerDetailsCnpRequest.getHotelId(),
                bookerProfileOhipMapper.toDto(bookerProfile, bookerDetailsCnpRequest));
      }

      // Update company profile.
      final var companyProfileIds = getProfileIdsByType(reservationList,
              ResProfileTypeType.COMPANY);
      // Company profile already assigned to reservation.
      if (!companyProfileIds.isEmpty()) {
        // Company Name is provided
        if (isNotBlank(bookerDetailsCnpRequest.getBooker().getCompanyName())) {
          updateCompanyId(bookerDetailsCnpRequest, companyProfileIds);
          // Company name should be deleted
        } else {
          final var bookerProfileId =
                  bookerProfile == null ? null : bookerProfile.getProfileId().getId();

          bookerDetailsCnpRequest.getReservationIds().forEach(reservationId -> bookerDetailsChangeReservation.add(
                  updateReservationAttachedProfilesRequestOhipMapper.toDto(bookerDetailsCnpRequest.getHotelId(),
                          reservationId, bookerProfileId, null)));
        }
        // No company profile was assign before to reservation
      } else {
        getCompanyResponseForCompanyName(bookerDetailsCnpRequest, bookerProfile, bookerDetailsChangeReservation);
      }
    }
    return bookerDetailsChangeReservation.isEmpty() ? null : bookerDetailsChangeReservation;
  }

  private void getCompanyResponseForCompanyName(
      BookerDetailsCnpRequest bookerDetailsCnpRequest,
      ProfileType bookerProfile,
      List<ChangeReservation> bookerDetailsChangeReservation) {
    if (StringUtils.isNotEmpty(bookerDetailsCnpRequest.getBooker().getCompanyName())) {
      var reservationCompanyResponse = ohipReservationClient.sendPostProfileRequest(
              bookerDetailsCnpRequest.getHotelId(),
              reservationCompanyRequestOhipMapper.toDto(
                      bookerDetailsCnpRequest.getBooker().getCompanyName()));

      final var companyProfileId = getProfileId(reservationCompanyResponse);
      final var bookerProfileId =
              bookerProfile == null ? null : bookerProfile.getProfileId().getId();

      bookerDetailsCnpRequest.getReservationIds().forEach(reservationId -> bookerDetailsChangeReservation.add(
              updateReservationAttachedProfilesRequestOhipMapper.toDto(bookerDetailsCnpRequest.getHotelId(),
                      reservationId, bookerProfileId, companyProfileId)));

    }
  }

  @Override
  public void updateBookerEmail(UpdateBookerEmailRequest updateBookerEmailRequest) {
    final var reservationList =
        ohipReservationClient.getReservations(updateBookerEmailRequest.getHotelId(),
            new HashSet<>(updateBookerEmailRequest.getReservationIds())).collectList().block();
    if (reservationList != null) {

      final var bookerProfileId = getBookerProfileId(reservationList);
      if (bookerProfileId.isPresent()) {

        final var bookerProfile = getBookerProfileById(bookerProfileId.get());

        var updateEmailBookerProfile = updateBookerEmailOhipMapper.toDto(bookerProfile,
            updateBookerEmailRequest);
        ohipReservationClient.sendUpdateProfileRequest(
            updateBookerEmailRequest.getHotelId(), updateEmailBookerProfile);

        Flux.fromIterable(updateBookerEmailRequest.getReservationIds())
            .flatMap(reservationId -> ohipReservationClient.sendChangeReservationRequest(
                    updateBookerEmailRequest.getHotelId(), reservationId,
                    updateBookerReservationProfileOhipMapper.toDto(
                        updateBookerEmailRequest.getHotelId(),
                        reservationId, bookerProfile.getProfileId().getId(),
                        updateBookerEmailRequest.getEmailAddress())),
                reservationOhipProperties.getMaxConcurrency())
            .collectList()
            .block();
      }
    }
  }

  private void mapOverrideInventoryCheck(ChangeReservation changeReservation,
      boolean overrideInventoryCheck) {
    changeReservation.getReservations().forEach(reservation ->
        reservation.setOverrideInventoryCheck(overrideInventoryCheck));
  }

  private void mapRoomTypeCharged(ChangeReservation changeReservation) {
    changeReservation.getReservations().forEach(reservation ->
        reservation.getRoomStay().getRoomRates()
            .forEach(rr -> rr.setRoomTypeCharged(rr.getRoomType())));
  }

  private String getPolicyId(ResCancellationPolicyType resCancellationPolicyType) {
    return Optional.ofNullable(resCancellationPolicyType)
        .map(ResCancellationPolicyType::getPolicyId)
        .map(UniqueIDType::getId)
        .orElse(null);
  }

  private ResCancellationPolicyType getResCancellationPolicyType(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {
    return Optional.ofNullable(reservation)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation::getReservations)
        .map(
            uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType::getReservation)
        .stream()
        .flatMap(Collection::stream)
        .flatMap(hotelReservationType -> Optional.ofNullable(
                hotelReservationType.getReservationPolicies())
            .map(
                uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPoliciesType::getCancellationPolicies)
            .stream()
            .flatMap(Collection::stream))
        .findFirst()
        .orElse(null);
  }

  private ChangeReservation mapToUpdateReasonForStayOhip(
      UpdateReasonForStayRequest updateReasonForStayRequest) {
    return updateReasonForStayRequestOhipMapper.toDto(updateReasonForStayRequest);
  }

  private boolean isDonationPackagesSelection(
      ReservationPackagesRequest reservationPackagesRequest) {

    var isDonationPackagesRequest = reservationPackagesRequest.getRoomsSelections()
        .stream()
        .flatMap(roomsSelections -> roomsSelections.getPackagesSelection().stream())
        .anyMatch(p -> isDonationPackage(p.getId()));
    var roomsSelectionsReservation = getReservationsPackagesByIds(
        reservationPackagesRequest.getHotelId(),
        Set.copyOf(reservationPackagesRequest.getReservationsId()))
        .getRoomsSelections();
    var isDonationPackagesReservation = false;

    if (roomsSelectionsReservation.get(0).getPackagesSelection() != null) {
      isDonationPackagesReservation = roomsSelectionsReservation.stream()
          .filter(
              roomsSelections -> !CollectionUtils.isEmpty(roomsSelections.getPackagesSelection()))
          .flatMap(roomsSelections -> roomsSelections.getPackagesSelection().stream())
          .anyMatch(p -> isDonationPackage(p.getId()));
    }
    return isDonationPackagesRequest && isDonationPackagesReservation;
  }

  private void updateDonationPackagesToRemove(
      ReservationPackagesRequest reservationPackagesRequest) {

    var packagesSelectionDonationReservation = getReservationsPackagesByIds(
        reservationPackagesRequest.getHotelId(),
        Set.copyOf(reservationPackagesRequest.getReservationsId()))
        .getRoomsSelections().stream()
        .filter(roomsSelections -> roomsSelections.getPackagesSelection() != null)
        .flatMap(roomsSelections -> roomsSelections.getPackagesSelection().stream())
        .map(packagesSelectionMapper::toModel)
        .filter(p -> isDonationPackage(p.getId()))
        .collect(toCollection(ArrayList::new));

    if (!Objects.isNull(packagesSelectionDonationReservation)) {
      if (!Objects.isNull(reservationPackagesRequest.getPreviousRoomsSelections())
          && !Objects.isNull(reservationPackagesRequest.getPreviousRoomsSelections().get(0)
          .getPackagesSelection())) {
        reservationPackagesRequest.getPreviousRoomsSelections().get(0)
            .getPackagesSelection().add(packagesSelectionDonationReservation.get(0));
      } else {
        var roomsSelectionDonation = new ArrayList<RoomsSelections>();
        roomsSelectionDonation.add(new RoomsSelections(packagesSelectionDonationReservation));
        reservationPackagesRequest.setPreviousRoomsSelections(roomsSelectionDonation);
      }
    }
  }

  private boolean isDonationPackage(String packageId) {
    return packageId.contains(CHRTY) || packageId.contains(ZCHRY) || packageId.contains(ZR0705)
        || packageId.contains(ZCHR10) || packageId.contains(ZCHR11) || packageId.contains(ZCHR12)
        || packageId.contains(ZCHR13);
  }

  private List<String> getPackagesCodesForVat(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {
    List<String> packagesCodes = new ArrayList<>(List.of("__ACCMOD__"));
    var reservationPackages =
        reservation.getReservations().getReservation().get(0).getReservationPackages();
    if (reservationPackages != null && !reservationPackages.isEmpty()) {
      packagesCodes.addAll(
          reservationPackages.stream().filter(pck ->
                  pck.getScheduleList().get(0).getComputedResvPrice().compareTo(BigDecimal.ZERO) != 0)
              .filter(pck -> Optional.ofNullable(pck.getPackageHeaderType())
                  .map(PackageCodeHeaderType::getPostingAttributes)
                  .map(ConfigPostingAttributesType::getAddToRate)
                  .orElse(Boolean.FALSE).equals(Boolean.FALSE))
              .map(ReservationPackageType::getPackageCode)
              .toList());
    }
    return packagesCodes;
  }

  private String getVatRegion(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation reservation) {
    return reservation.getReservations().getReservation().get(0).getCashiering().getTaxType()
        .getCode();
  }

  private List<ReservationCityTaxInfo> getCityTaxDetailsPerDay(String hotelId, String reservationId,
      uk.co.whitbread.hotel.ohip.adapter.generated.models
          .Reservation reservation) {
    var cityTaxData =
        reservation.getReservations().getReservation().get(0).getReservationPackages() != null
            ? reservation.getReservations().getReservation().get(0).getReservationPackages()
            .stream()
            .filter(pckg -> CITYTAX_ID.equals(pckg.getPackageCode())).findAny().orElse(null) :
            null;

    if (cityTaxData == null) {
      return List.of();
    } else {
      return cityTaxData.getScheduleList().stream().map(item -> {
        var cityTax = ohipReservationClient.getRateInfo(hotelId, reservationId,
                item.getConsumptionDate().toString(), FALSE).getDetail().getPackages().stream()
            .filter(ratePckg -> CITYTAX_ID.equals(ratePckg.getCode())).findFirst().get();
        compareAndUpdateCityTaxWithVat(item, cityTax);
        return ReservationCityTaxInfo.builder().referenceDate(item.getConsumptionDate())
            .vatAmount(cityTax.getTaxes().getTax().get(0).getAmount()).build();
      }).toList();

    }
  }

  private void calculateCityTaxValueWithVat(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationList,
      String hotelId, Map<String, RateInfo> rateInfoMap) {
    if (reservationList != null) {
      reservationList.forEach(reservation -> {
        if (reservation.getReservations().getReservation().get(0).getReservationPackages()
            != null) {
          var reservationId =
              reservation.getReservations().getReservation().get(0).getReservationIdList()
                  .stream().filter(idType -> RESERVATION.equals(idType.getType()))
                  .findFirst().get().getId();
          reservation.getReservations().getReservation().get(0).getReservationPackages()
              .forEach(reservationPackage -> {
                if (CITYTAX_ID.equals(reservationPackage.getPackageCode())) {
                  reservationPackage.getScheduleList().forEach(scheduleType -> {
                    var cityTax = rateInfoMap.isEmpty() ? ohipReservationClient.getRateInfo(hotelId,
                            reservationId,
                            scheduleType.getConsumptionDate().toString(), FALSE).getDetail()
                        .getPackages()
                        .stream()
                        .filter(ratePckg -> CITYTAX_ID.equals(ratePckg.getCode())).findFirst().get()
                        : rateInfoMap.get(scheduleType.getConsumptionDate().toString())
                            .getDetail()
                            .getPackages().stream()
                            .filter(ratePckg -> CITYTAX_ID.equals(ratePckg.getCode()))
                            .findFirst()
                            .get();
                    compareAndUpdateCityTaxWithVat(scheduleType, cityTax);
                  });
                }
              });
        }
      });
    }
  }

  /* Compare the cityTax values from reservations and rateInfo calls
  If the values are different, the VAT is added to computedResvPrice.
   */
  private void compareAndUpdateCityTaxWithVat(ReservationPackageScheduleType scheduleType,
      TotalType cityTax) {
    var cityTaxBefore = cityTax.getAmountBeforeTax();
    var cityTaxVat = cityTax.getTaxes().getTax().get(0).getAmount();
    var totalCityTax = cityTaxBefore.add(cityTaxVat);
    var computedResvPrice = scheduleType.getComputedResvPrice();

    //Round to Opera precision
    var computedResvPriceRounded = computedResvPrice.setScale(2, RoundingMode.HALF_UP);
    log.info(
        "CityTaxBefore= {}, cityTaxVat= {}, totalCityTax= {}, computedResvPrice= {}, computerResvPriceRounded= {}",
        cityTaxBefore, cityTaxVat, totalCityTax, computedResvPrice, computedResvPriceRounded);

    if (totalCityTax.compareTo(computedResvPriceRounded) != 0) {
      var computedResvPriceWithVat = computedResvPriceRounded.add(cityTaxVat);
      scheduleType.setComputedResvPrice(computedResvPriceWithVat);
      log.info(
          "ComputedResvPriceWithVat= {}", computedResvPriceWithVat);
    }
  }


  private List<SearchBooking> searchBookingsByReservationRelatedFields(
      SearchBookingsInput searchBookingsInput, boolean hotelIncluded) {

    log.debug(
        "Entered searchBookingsByReservationRelatedFields for {} search criteria and hotelIncluded={}",
        searchBookingsInput.getSearchFields(), hotelIncluded);

    List<SearchBooking> finalSearchBookings = new ArrayList<>();

    searchBookingsInput.getSearchFields()
        .put(LIMIT, singletonList(String.valueOf(MAX_OPERA_PAGE_LIMIT)));

    var hasMoreReservations = true;
    var operaReservationsOffset = 0;
    var maximumSizeReached = false;

    while (hasMoreReservations && !maximumSizeReached) {
      searchBookingsInput.getSearchFields()
          .put(OhipConstants.OFFSET, singletonList(String.valueOf(operaReservationsOffset)));

      ReservationsDetails reservationsDetailsOhip;

      if (hotelIncluded) {
        reservationsDetailsOhip = ohipReservationClient.sendGetReservationsByHotelRelatedFields(
            searchBookingsInput.getSearchFields());
      } else {
        reservationsDetailsOhip = ohipReservationClient.sendGetReservationsByReservationRelatedFields(
            searchBookingsInput.getSearchFields());
      }

      // filter out invalid reservations
      var reservationInfos = filterOutInvalidReservations(reservationsDetailsOhip);

      // group reservations by basket reference
      var reservationsByBasketRef = getReservationsGroupedByBasketReference(reservationInfos);

      // collect and group profiles based on profile id
      var profilesByIds = getProfilesGroupedByProfileId(reservationInfos);

      List<SearchBooking> subsetReservations = searchBookingsResponseOhipMapper
          .toReservationDetailsResponseForModel(reservationsByBasketRef, profilesByIds);

      finalSearchBookings.addAll(subsetReservations);

      maximumSizeReached = checkMaximumResponseSizeReached(finalSearchBookings,
          searchBookingsInput.getFilterChain());
      hasMoreReservations = reservationsDetailsOhip.getReservations().getHasMore();
      operaReservationsOffset = reservationsDetailsOhip.getReservations().getOffset();
    }

    return finalSearchBookings;
  }

  private List<SearchBooking> searchBookingsByBookerRelatedFields(
      SearchBookingsInput searchBookingsInput) {
    log.debug("Entered searchBookingsByBookerRelatedFields for {} search criteria",
        searchBookingsInput.getSearchFields());

    List<SearchBooking> finalSearchBookings = new ArrayList<>();

    searchBookingsInput.getSearchFields()
        .put(LIMIT, singletonList(String.valueOf(MAX_OPERA_PAGE_LIMIT)));

    var hasMoreReservations = true;
    var operaReservationsOffset = 0;
    var maximumSizeReached = false;

    while (hasMoreReservations && !maximumSizeReached) {
      searchBookingsInput.getSearchFields()
          .put(OhipConstants.OFFSET, singletonList(String.valueOf(operaReservationsOffset)));

      var profileSummaries = ohipReservationClient.sendBookerGetProfileSummaries(
          searchBookingsInput.getSearchFields());

      Set<String> profileIds = new HashSet<>();
      if (profileSummaries != null && profileSummaries.getProfileSummaries() != null
          && profileSummaries.getProfileSummaries().getProfileInfo() != null) {
        profileIds.addAll(profileSummaries.getProfileSummaries().getProfileInfo()
            .stream()
            .filter(profileSummaryInfoType -> profileSummaryInfoType.getProfile().getProfileType()
                .getValue()
                .equals(ProfileTypeType.CONTACT.getValue()))
            .flatMap(profileSummaryInfoType -> profileSummaryInfoType.getProfileIdList().stream())
            .filter(uniqueIDType -> uniqueIDType.getType()
                .equals(UniqueIdTypeEnumDto.PROFILE_TYPE.value()))
            .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType::getId)
            .collect(toSet()));
      }

      if (!profileIds.isEmpty()) {
        // Second, we will call CRM to fetch the details for a specific profile
        // and retrieve the reservation ids associated with it
        var profiles = ohipReservationClient.sendGetProfilesByProfileIds(profileIds);

        var reservationIds = new ArrayList<String>();

        var futureList = profiles.stream()
            .map(profile -> profile.getProfileDetails().getStayReservationInfoList()
                .getFutureList())
            .filter(Objects::nonNull)
            .map(StayFutureListType::getReservationInfo);

        var historyList = profiles.stream()
            .map(profile -> profile.getProfileDetails().getStayReservationInfoList()
                .getHistoryList())
            .filter(Objects::nonNull)
            .map(StayHistoryListType::getReservationInfo);

        var reservationIdsFromFutureList = getProfilesReservationIds(futureList);

        var reservationIdsFromHistoryList = getProfilesReservationIds(historyList);

        reservationIds.addAll(reservationIdsFromFutureList);
        reservationIds.addAll(reservationIdsFromHistoryList);

        // Finally , we will fetch reservations details for each reservation id
        fetchReservationDetails(finalSearchBookings, profiles, reservationIds);
        maximumSizeReached =
            checkMaximumResponseSizeReached(finalSearchBookings,
                searchBookingsInput.getFilterChain());
      }

      hasMoreReservations =
          (profileSummaries != null && profileSummaries.getProfileSummaries() != null)
              && profileSummaries.getProfileSummaries().getHasMore();
      operaReservationsOffset =
          (profileSummaries != null && profileSummaries.getProfileSummaries() != null)
              ? profileSummaries.getProfileSummaries().getOffset() : 0;
    }
    return finalSearchBookings;
  }

  private boolean checkMaximumResponseSizeReached(List<SearchBooking> finalSearchBookings,
      SearchBookingsResultsFilterChain filterChain) {
    var filteredSearchBookings =
        finalSearchBookings.stream().filter(filterChain::filter)
            .sorted(Comparator.comparing(SearchBooking::getArrivalDate)).toList();
    return filteredSearchBookings.size() > SEARCH_RESULTS_LIMIT;
  }

  private String getExternalIdByContext(ReservationInfoType reservationInfoType) {

    final var externalIdsByContext = reservationInfoType.getExternalReferences().stream().collect(
        Collectors.toMap(ExternalReferenceType::getIdContext, ExternalReferenceType::getId));

    if (externalIdsByContext.containsKey(OhipConstants.EXTERNAL_REF_DIGITAL_ID_CONTEXT)) {
      return externalIdsByContext.get(OhipConstants.EXTERNAL_REF_DIGITAL_ID_CONTEXT);
    } else if (externalIdsByContext.containsKey(OhipConstants.EXTERNAL_REF_MIGRATION_ID_CONTEXT)) {
      final var externalRefId = externalIdsByContext.get(
          OhipConstants.EXTERNAL_REF_MIGRATION_ID_CONTEXT);
      if (externalRefId.contains(EXTERNAL_REFERENCE_ID_SEPARATOR)) {
        return externalRefId.substring(0, externalRefId.indexOf(EXTERNAL_REFERENCE_ID_SEPARATOR));
      }
      return externalRefId;
    } else {
      return externalIdsByContext.values().stream().findAny().orElse(EMPTY_STR);
    }
  }

  private void fetchReservationDetails(List<SearchBooking> finalSearchBookings,
      List<Profile> profiles,
      ArrayList<String> reservationIds) {
    if (!reservationIds.isEmpty()) {
      List<ReservationsDetails> reservationsDetailsList = new ArrayList<>();
      List<List<String>> partitions = partitionList(reservationIds, MAX_OPERA_PAGE_LIMIT);
      for (List<String> partition : partitions) {
        Map<String, List<String>> reservationParams = new HashMap<>();
        reservationParams.put(RESERVATION_IDS_PARAM, partition);
        reservationParams.put(LIMIT, singletonList(String.valueOf(partition.size())));
        reservationsDetailsList.add(
            ohipReservationClient.sendGetReservationsByReservationRelatedFields(
                reservationParams));
      }

      var reservationInfoTypes = reservationsDetailsList
          .stream()
          .map(
              reservationsDetails -> reservationsDetails.getReservations()
                  .getReservationInfo())
          .flatMap(Collection::stream)
          .toList();
      var reservationsByBasketRef =
          getReservationsGroupedByBasketReference(reservationInfoTypes);

      var profileIdsMap = profiles
          .stream()
          .collect(
              Collectors.toMap(
                  bookerProfile -> bookerProfile.getProfileIdList().get(0).getId(),
                  Function.identity()));

      finalSearchBookings.addAll(
          searchBookingsResponseOhipMapper.toReservationDetailsResponseForModel(
              reservationsByBasketRef,
              profileIdsMap));
    }
  }

  private List<String> getProfilesReservationIds(Stream<List<StayReservationInfoType>> listStream) {
    return listStream.filter(Objects::nonNull)
        .flatMap(Collection::stream)
        .map(StayReservationInfoType::getReservationIdList)
        .flatMap(Collection::stream)
        .filter(uniqueIDType -> uniqueIDType.getType()
            .equalsIgnoreCase(UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType::getId)
        .toList();
  }

  private Map<String, List<ReservationInfoType>> getReservationsGroupedByBasketReference(
      List<ReservationInfoType> reservationInfoTypes) {

    var reservationsByBasketRef = reservationInfoTypes.stream()
        .collect(Collectors.groupingBy(this::getExternalIdByContext));

    reservationsByBasketRef.remove(EMPTY_STR);

    return reservationsByBasketRef;
  }

  private Map<String, Profile> getProfilesGroupedByProfileId(
      List<ReservationInfoType> reservationInfoTypes) {
    // get booker details
    var profileIds = reservationInfoTypes.stream().map(entry ->
        entry.getAttachedProfiles() == null ? null : entry.getAttachedProfiles().stream()
            .filter(
                profile -> profile.getReservationProfileType().getValue()
                    .equalsIgnoreCase(OhipConstants.RESERVATION_CONTACT))
            .findFirst().map(profile -> profile.getProfileIdList().get(0).getId())
            .orElse(null)
    ).filter(Objects::nonNull).collect(toSet());

    if (!profileIds.isEmpty()) {
      final var bookerProfiles = ohipReservationClient.sendGetProfilesByProfileIds(profileIds);

      return bookerProfiles.stream().collect(
          Collectors.toMap(bookerProfile -> bookerProfile.getProfileIdList().get(0).getId(),
              Function.identity()));
    }

    return new HashMap<>();
  }

  private Map<String, Profile> getProfilesGroupedByProfileIds(
      List<HotelReservationType> reservationInfoTypes) {
    // get booker details
    var profileIds = reservationInfoTypes.stream().map(entry ->
        entry.getReservationProfiles() == null ? null : entry.getReservationProfiles()
            .getReservationProfile()
            .stream()
            .filter(
                profile -> profile.getReservationProfileType().getValue()
                    .equalsIgnoreCase(OhipConstants.RESERVATION_CONTACT))
            .findFirst().map(profile -> profile.getProfileIdList().get(0).getId())
            .orElse(null)
    ).filter(Objects::nonNull).collect(toSet());

    if (!profileIds.isEmpty()) {
      final var bookerProfiles = ohipReservationClient.sendGetProfilesByProfileIds(profileIds);

      return bookerProfiles.stream().collect(
          Collectors.toMap(bookerProfile -> bookerProfile.getProfileIdList().get(0).getId(),
              Function.identity()));
    }

    return new HashMap<>();
  }

  private List<ReservationInfoType> filterOutInvalidReservations(
      ReservationsDetails reservationsDetails) {
    var reservationInfos =
        (reservationsDetails.getReservations().getReservationInfo() != null)
            ? reservationsDetails.getReservations().getReservationInfo()
            : new ArrayList<ReservationInfoType>();

    reservationInfos.removeIf(
        reservation -> StringUtils.isEmpty(reservation.getHotelId()) || CollectionUtils.isEmpty(
            reservation.getExternalReferences()));

    return reservationInfos;
  }

  /**
   * Returns subsets of the given partitionSize.
   *
   * @param reservationIds List which should be partitioned
   * @param partitionSize  Number specifying the size of the subset
   * @return List of partitioned lists
   */
  private List<List<String>> partitionList(List<String> reservationIds, int partitionSize) {
    List<List<String>> partitions = new ArrayList<>();
    // partition the list into subsets of the given partitionSize
    for (int i = 0; i < reservationIds.size(); i += partitionSize) {
      partitions.add(
          reservationIds.subList(i, Math.min(i + partitionSize, reservationIds.size())));
    }
    return partitions;
  }

  private CreditCardInfo getCardInformation(String hotelId, String cardId) {
    return frontDeskClient.getCreditCardInfo(hotelId, cardId);
  }

  private ReservationPaymentMethodType getReservationCardDetails(String hotelId,
      List<uk.co.whitbread.hotel.ohip.adapter.generated
          .models.Reservation> reservationList) {
    log.debug("Entered getCreditCardInformation for hotelId={} and {} reservations",
        sanitizeInput(hotelId),
        reservationList == null ? "null" : reservationList.size());
    ReservationPaymentMethodType rsvCard = null;
    if (reservationList != null && !CollectionUtils.isEmpty(reservationList)) {
      var optionalReservationPaymentCard = reservationList.stream()
          .flatMap(reservation ->
              reservation.getReservations().getReservation().get(0).getReservationPaymentMethods()
                  .stream())
          .filter(payment -> !Objects.isNull(payment.getPaymentCard())
              && !Objects.isNull(payment.getPaymentCard().getCardId()))
          .findFirst();

      if (optionalReservationPaymentCard.isPresent()) {
        rsvCard = optionalReservationPaymentCard.get();
        var cardInfoResponse =
            getCardInformation(hotelId, rsvCard.getPaymentCard().getCardId().getId());
        if (cardInfoResponse != null && cardInfoResponse.getCreditCard() != null) {
          rsvCard.getPaymentCard()
              .setCardNumber(cardInfoResponse.getCreditCard().getCardNumber());
          rsvCard.getPaymentCard()
              .setExpirationDate(cardInfoResponse.getCreditCard().getExpirationDate());
          var userDefinedCardType = cardInfoResponse.getCreditCard().getUserDefinedCardType();
          if (Objects.nonNull(userDefinedCardType) && !userDefinedCardType.isEmpty()) {
            rsvCard.getPaymentCard().setUserDefinedCardType(userDefinedCardType);
          } else if (cardInfoResponse.getCreditCard().getCardType() != null) {
            rsvCard.getPaymentCard().setCardType(
                CardTypeType.valueOf(cardInfoResponse.getCreditCard().getCardType().name()));
          }
        }
      }
    }
    return rsvCard;
  }

  private String createDateWithTimeAndOffset(String arrivalDate, String hotelZone,
      Date offsetDropTime, int days) {

    //added time to the arrivalDate
    SimpleDateFormat localDateFormat = new SimpleDateFormat(TIME_FORMAT);
    String time = localDateFormat.format(offsetDropTime);
    arrivalDate = arrivalDate.concat("T");
    arrivalDate = arrivalDate.concat(time);

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    LocalDateTime dateTime = LocalDateTime.parse(arrivalDate, formatter).minusDays(days);

    return dateTime.format(formatter).concat(getTimeOffsetFormat(hotelZone, dateTime));
  }

  private String getTimeOffsetFormat(String hotelZone, LocalDateTime dateTime) {
    ZoneId zoneId = ZoneId.of(hotelZone);
    DateTimeFormatter offsetFormatter = DateTimeFormatter.ofPattern(OFFSET_FORMAT);
    ZoneOffset offset = zoneId.getRules().getOffset(dateTime);

    OffsetDateTime offsetDateTime = OffsetDateTime.of(dateTime, offset);
    return offsetFormatter.format(offsetDateTime);
  }

  public String convertDateToOffsetDateTime(Date dateToConvert, String hotelZone) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(dateToConvert);
    LocalDate date = LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH));
    LocalTime time = LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),
        calendar.get(Calendar.SECOND));

    LocalDateTime dateTime = LocalDateTime.of(date, time);
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);

    return dateTime.format(formatter).concat(getTimeOffsetFormat(hotelZone, dateTime));
  }

  public List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> getReservationsByIdsLight(
      String hotelId, Set<String> reservationIds) {
    return ohipReservationClient.getReservations(hotelId,
        reservationIds).collectList().block();
  }

  private CancellationPoliciesResponse cancellationPoliciesByReservationId(
      String hotelId, Set<String> reservationIds, String hotelTimeZone,
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> reservationsList) {
    Date absoluteDeadline;
    String description;
    String date;
    var reservationDetails = reservationsList;
    if (Objects.isNull(reservationDetails)) {
      reservationDetails = ohipReservationClient.getReservations(hotelId,
              reservationIds.stream().findFirst().stream().collect(Collectors.toSet())).collectList()
          .block();
    }
    if (reservationDetails != null && !reservationDetails.isEmpty()) {
      absoluteDeadline = reservationDetails.get(0)
          .getReservations().getReservation().get(0)
          .getReservationPolicies().getCancellationPolicies().get(0)
          .getPolicy().getDeadline().getAbsoluteDeadline();

      description = reservationDetails.get(0)
          .getReservations().getReservation().get(0)
          .getReservationPolicies().getCancellationPolicies().get(0).getComments();
      date = convertDateToOffsetDateTime(absoluteDeadline, hotelTimeZone);
    } else {
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_NO_CANCELLATION_POLICIES_EXCEPTION,
          "An error was returned by OHIP: Could not get cancellation policies from Opera.");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    return CancellationPoliciesResponse.builder()
        .time(date)
        .text(description)
        .build();
  }

  private CancellationPoliciesResponse cancellationPoliciesByRateCodeAndArrivalDate(
      String hotelId, String rateCode, String hotelTimeZone, String arrivalDate) {
    var policySchedulesDetails = ohipReservationClient.sendGetPolicySchedulesRequest(hotelId,
        rateCode);
    var policySchedules = policySchedulesDetails.getPolicySchedules();
    String policyCode;
    if (policySchedules != null && !policySchedules.isEmpty()) {
      policyCode = policySchedules.get(0).getScheduleDetail().getPolicy().getCode();
    } else {
      return new CancellationPoliciesResponse();
    }

    var policyDeadline = ohipReservationClient.sendGetCancellationPoliciesRequest(hotelId)
        .getCancelPenalties().getCancelPenalties().get(0).getCancelPenaltyConfig().stream()
        .filter(cancelPenalties -> cancelPenalties.getPolicyCode().equals(policyCode))
        .findFirst()
        .orElseThrow(
            () -> {
              var exception = new HotelReservationException(
                  ErrorCode.OHIP_GET_CANCELLATION_POLICY_EXCEPTION,
                  String.format("Error while trying to get cancellation policies by "
                      + "hotelId=%s", hotelId));
              ExceptionLogger.log(log, exception);
              throw exception;
            });

    var offsetFromArrival = policyDeadline.getDeadline().getOffsetFromArrival();
    var offsetDropTime = policyDeadline.getDeadline().getOffsetDropTime();
    var date =
        createDateWithTimeAndOffset(arrivalDate, hotelTimeZone, offsetDropTime, offsetFromArrival);

    var description = policyDeadline.getPenaltyDescription();
    return CancellationPoliciesResponse.builder()
        .time(date)
        .text(description)
        .build();
  }

  private List<BookingAllowance> getBookingAllowancesList(
      List<RoutingInstructionType> instructions, List<BusinessAllowanceRuleDto> rules,
      Set<String> reservationPackages, List<String> basketBookingAllowances) {
    final List<BookingAllowance> bookingAllowances = new LinkedList<>();
    if (!instructions.isEmpty()) {
      var startDate = Optional.ofNullable(instructions.get(0).getDuration())
          .flatMap(u -> Optional.ofNullable(u.getTimeSpan()))
          .flatMap(t -> Optional.ofNullable(t.getStartDate()));
      instructions.stream()
          .filter(
              i -> Objects.isNull(i.getDuration().getTimeSpan())
                  || i.getDuration().getTimeSpan().getStartDate().equals(startDate.orElse(null)))
          .toList()
          .forEach(i -> {
            final var budget = i.getCreditLimit();
            if (i.getTransactionCodes() != null) {
              bookingAllowances.addAll(
                  i.getTransactionCodes().stream()
                      .flatMap(t ->
                          getAllowanceForCode(t.getTransactionCode(), rules,
                              reservationPackages, basketBookingAllowances).stream()
                              .map(al -> BookingAllowance.builder().allowance(al).budget(budget)
                                  .build())
                              .filter(a -> nonNull(a.getAllowance()))
                      ).toList());
            }
            if (i.getBillingInstructions() != null) {
              bookingAllowances.addAll(
                  i.getBillingInstructions().stream()
                      .flatMap(t ->
                          getAllowanceForCode(t.getBillingCode(), rules,
                              reservationPackages, basketBookingAllowances).stream()
                              .map(al -> BookingAllowance.builder().allowance(al).budget(budget)
                                  .build())
                              .filter(a -> nonNull(a.getAllowance()))
                      ).toList());
            }
          });
      checkForDinnerWithAlcoholAllowance(bookingAllowances);
    }
    return bookingAllowances;
  }

  /**
   * Dinner allowance can be set with or without alcohol from FE. In Opera, this will translate to
   * separate transactions codes for dinner with alcohol or dinner without alcohol. If dinner is set
   * with alcohol in Opera, then we need to translate that code back to dinner allowance and alcohol
   * allowance (already mapped) for FE.
   */
  private void checkForDinnerWithAlcoholAllowance(List<BookingAllowance> bookingAllowances) {
    final var dinnerAllowanceBuilder = BookingAllowance.builder();
    bookingAllowances.stream()
        .filter(a -> a.getAllowance().equalsIgnoreCase(ALLOWANCE_ALCOHOL))
        .findFirst()
        .ifPresent(a -> {
          dinnerAllowanceBuilder.allowance(ALLOWANCE_DINNER);
          dinnerAllowanceBuilder.budget(a.getBudget());
          a.setBudget(BigDecimal.ZERO);
        });
    var dinnerAllowance = dinnerAllowanceBuilder.build();
    if (isNotBlank(dinnerAllowance.getAllowance())) {
      bookingAllowances.add(dinnerAllowance);
    }
  }

  public List<String> getAllowanceForCode(String routingCode, List<BusinessAllowanceRuleDto> rules,
      Set<String> reservationPackages, List<String> basketBookingAllowances) {
    //Check if this is a package on the reservation
    var allowanceForPackage = rules.stream()
        .filter(r -> r.getPms().equalsIgnoreCase(RULES_PMS_CODE)
            && r.getTargetId().equalsIgnoreCase(routingCode)
            && reservationPackages.contains(r.getSourceId()))
        .map(BusinessAllowanceRuleDto::getSourceId).toList();

    if (allowanceForPackage.isEmpty()) {
      //It was not a package, exclude those and search for everything else
      if (basketBookingAllowances == null || basketBookingAllowances.isEmpty()) {
        allowanceForPackage = rules.stream()
            .filter(r -> r.getPms().equalsIgnoreCase(RULES_PMS_CODE)
                && r.getTargetId().equalsIgnoreCase(routingCode)
                && Objects.nonNull(r.getSourceType()) && !SOURCE_TYPE_PACKAGE.equalsIgnoreCase(
                r.getSourceType()))
            .map(BusinessAllowanceRuleDto::getSourceId)
            .findFirst()
            .map(Collections::singletonList)
            .orElse(Collections.emptyList());
      } else {
        allowanceForPackage = rules.stream()
            .filter(r -> r.getPms().equalsIgnoreCase(RULES_PMS_CODE)
                && r.getTargetId().equalsIgnoreCase(routingCode)
                && Objects.nonNull(r.getSourceType())
                && !SOURCE_TYPE_PACKAGE.equalsIgnoreCase(r.getSourceType())
                && basketBookingAllowances.contains(r.getSourceId()))
            .map(BusinessAllowanceRuleDto::getSourceId)
            .toList();
      }
    }

    return allowanceForPackage;
  }

  @Override
  public void deleteReservation(String hotelId, String reservationId) {
    log.debug("Entered deleteReservation for hotelId ={} and reservationId={}",
        sanitizeInput(hotelId), sanitizeInput(reservationId));
    this.ohipReservationClient.deleteReservationRequest(hotelId, reservationId);
  }

  @Override
  public void movePaymentDetails(String hotelId, Set<String> reservationIds) {
    log.debug("Entered movePaymentDetails for hotelId ={} and reservationIds={}",
        sanitizeInput(hotelId), sanitizeInput(reservationIds.toString()));

    var reservations = ohipReservationClient.getReservations(hotelId, reservationIds)
        .collectList()
        .block();

    if (CollectionUtils.isEmpty(reservations) || reservationIds.size() != reservations.size()) {
      var exception = new HotelReservationException(
          ErrorCode.DIGITAL_FIND_OPERA_RESERVATION_EXCEPTION,
          "Could not find Opera reservation");
      ExceptionLogger.log(log, exception);
      throw exception;
    }
    Flux.fromIterable(reservations)
        .filter(reservation -> !Objects.isNull(reservation.getReservations().getReservation().get(0)
            .getReservationPaymentMethods())
            && !Objects.isNull(reservation.getReservations().getReservation().get(0)
            .getReservationPaymentMethods().get(0).getPaymentCard()) && !Objects.isNull(
            reservation.getReservations().getReservation().get(0)
                .getReservationPaymentMethods().get(0).getPaymentCard().getCardId()))
        .flatMap(
            reservation -> {
              var reservationPaymentMethod = reservation.getReservations().getReservation().get(0)
                  .getReservationPaymentMethods().get(0);
              var reservationId =
                  reservation.getReservations().getReservation().get(0).getReservationIdList()
                      .stream()
                      .filter(
                          idType -> idType.getType()
                              .equals(UniqueIdTypeEnumDto.RESERVATION_TYPE.value()))
                      .findFirst().get().getId();

              var cardInfoResponse =
                  frontDeskClient.getCreditCardInfo(hotelId,
                      reservationPaymentMethod.getPaymentCard().getCardId().getId());
              return ohipReservationClient.sendChangeReservationRequest(hotelId, reservationId,
                  movePaymentDetailsOhipMapper.toChangeReservationDto(reservationPaymentMethod,
                      cardInfoResponse));
            }
        ).collectList()
        .block();
  }

  @Override
  public boolean isCnpReservation(String hotelId, String reservationId) {
    var reservationDetails = ohipReservationClient.getReservations(
            hotelId, new HashSet<>(List.of(reservationId))).collectList()
        .block();
    if (CollectionUtils.isEmpty(reservationDetails)) {
      return false;
    }
    var routingInstructions = Optional.ofNullable(reservationDetails.getFirst().getReservations())
        .map(HotelReservationsType::getReservation)
        .filter(CollectionUtils::isNotEmpty)
        .map(List::getFirst)
        .map(HotelReservationType::getRoutingInstructions)
        .orElse(List.of());
    return CollectionUtils.isNotEmpty(routingInstructions)
            && routingInstructions.stream()
            .anyMatch(ri -> nonNull(ri.getFolio())
                && Integer.valueOf(FOLIO_WINDOW_2).equals(ri.getFolio().getFolioWindowNo()));
  }

  private void setCnpReservationAlert(String hotelId, String reservationId) {
    log.info("Setting CNP alert for hotelId={} and reservationId={}",
        sanitizeInput(hotelId), sanitizeInput(reservationId));

    var alert = new Alert();
    alert.setCode(CNP_ALERT_CODE);
    alert.setArea(AlertAreaType.CHECKIN.name());
    alert.setDescription(CNP_ALERT_DESCRIPTION_EN);
    alert.setScreenNotification(true);
    alert.setPrinterNotification(false);

    var updateAlertsRequest = new UpdateReservationAlertsRequest();
    updateAlertsRequest.setHotelId(hotelId);
    updateAlertsRequest.setReservationIds(Set.of(reservationId));
    updateAlertsRequest.setAlerts(List.of(alert));

    updateReservationAlerts(updateAlertsRequest);
  }

  private String getOperaLanguageCode(final String language) {
    String operaLanguageCode = "";
    if (StringUtils.isNotEmpty(language)) {
      Map<String, String> languageCodes = ohipProperties.getLanguages();
      operaLanguageCode = languageCodes.get(language);
      if (StringUtils.isEmpty(operaLanguageCode)) {
        log.debug("Booker has an invalid language code {}.", language);
      }
    }
    return operaLanguageCode;
  }

  private void setWbLanguageCode(Profile bookerProfile) {
    String operaLanguageCode = Optional.ofNullable(bookerProfile)
        .map(Profile::getProfileDetails)
        .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType::getCustomer)
        .map(CustomerType::getLanguage)
        .orElse(null);
    Map<String, String> languageCodes = ohipProperties.getLanguages();
    if (operaLanguageCode != null && languageCodes.containsValue(operaLanguageCode)) {
      String wbLanguageCode = languageCodes.entrySet()
          .stream()
          .filter(languageEntry -> languageEntry.getValue().equals(operaLanguageCode))
          .map(Map.Entry::getKey)
          .findFirst()
          .get();
      bookerProfile.getProfileDetails().getCustomer().setLanguage(wbLanguageCode);
    } else {
      log.debug("Opera language code cannot be converted to a WB language code.");
      Optional.ofNullable(bookerProfile)
          .map(Profile::getProfileDetails)
          .map(uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType::getCustomer)
          .ifPresent(customer -> customer.setLanguage(null));
    }
  }

  private void updateBillingAddressOpera(BillingAddressRequest billingAddressRequest,
      String hotelId, String profileId) {
    ProfileType profileType;
    profileType = getBookerProfileById(profileId);

    var bookerOhipProfile = bookerProfileOhipMapper.toBillingDto(profileType, billingAddressRequest);

    updateOhipProfile(billingAddressRequest, bookerOhipProfile);
    ohipReservationClient.sendUpdateProfileRequest(hotelId, bookerOhipProfile);
  }

  private void updateOhipProfile(BillingAddressRequest billingAddressRequest,
                                 Profile bookerOhipProfile) {

    bookerOhipProfile.getProfileDetails().getAddresses().getAddressInfo().get(0)
            .setType(null);
    bookerOhipProfile.getProfileDetails().getAddresses().getAddressInfo().get(0)
            .setId(null);
    bookerOhipProfile.getProfileDetails().getAddresses().getAddressInfo().get(0)
            .getAddress().setType(ADDTYPE_BILLING);
    if (billingAddressRequest.getBooker().getAddress().getCountryCode() != null) {
      final var countryNameType = new CountryNameType();
      countryNameType.setValue(billingAddressRequest.getBooker().getAddress()
              .getCountryCode());
      bookerOhipProfile.getProfileDetails().getAddresses().getAddressInfo().get(0)
              .getAddress()
              .setCountry(countryNameType);
    }

    BookerAddress address = billingAddressRequest.getBooker().getAddress();
    String cityName = address.getAddressLine4() != null ? address.getAddressLine4() : address.getCityName();

    bookerOhipProfile.getProfileDetails().getAddresses().getAddressInfo().get(0)
            .getAddress()
            .setCityName(cityName);

    if (unleashWrapper.isEnabled(unleashWrapper.featureFlag().getCaptureBillingAddressBb())) {
      bookerOhipProfile.getProfileDetails().getAddresses().getAddressInfo().get(0).getAddress()
              .setPrimaryInd(Boolean.TRUE);
    }
  }

  private void processUserDefinedFields(UpdateReservationsRequest updateReservationsRequest,
      ChangeReservation changeReservation) {

    boolean processIataNumber =
        StringUtils.isNotEmpty(updateReservationsRequest.getDistributionIATANumber());
    boolean clearCcAgentIdUdf = updateReservationsRequest.getClearCcAgentIdUdf() != null
        && updateReservationsRequest.getClearCcAgentIdUdf();

    if (processIataNumber || clearCcAgentIdUdf) {
      changeReservation.getReservations().forEach(reservation -> {
        UserDefinedFieldsType userDefinedTypes = buildUdfsForReservation(
            updateReservationsRequest, processIataNumber, clearCcAgentIdUdf, reservation);
        if (userDefinedTypes != null) {
          reservation.setUserDefinedFields(userDefinedTypes);
        }
      });
    }
  }

  private UserDefinedFieldsType buildUdfsForReservation(
      UpdateReservationsRequest updateReservationsRequest, boolean processIataNumber,
      boolean clearCcAgentIdUdf, HotelReservationInstructionType reservation) {
    var userDefinedTypes = reservation.getUserDefinedFields();
    if (userDefinedTypes != null) {
      if (processIataNumber) {
        processIataNumber(updateReservationsRequest, userDefinedTypes);
      }
      if (clearCcAgentIdUdf) {
        processClearAgentIdFromExistingUdfList(userDefinedTypes);
      }
    } else if (clearCcAgentIdUdf) {
      userDefinedTypes = getUdfListWithNoAgentId();
    }
    return userDefinedTypes;
  }

  private UserDefinedFieldsType getUdfListWithNoAgentId() {
    UserDefinedFieldsType userDefinedTypes;
    userDefinedTypes = new UserDefinedFieldsType();
    var udfC08 = new CharacterUDFType();
    udfC08.setName(UDFC_08);
    udfC08.setValue(null);
    userDefinedTypes.addCharacterUDFsItem(udfC08);
    return userDefinedTypes;
  }

  private void processClearAgentIdFromExistingUdfList(UserDefinedFieldsType userDefinedTypes) {
    if (userDefinedTypes.getCharacterUDFs().removeIf(
        characterUDFType -> UDFC_08.equals(characterUDFType.getName()))) {
      var udfC08 = new CharacterUDFType();
      udfC08.setName(UDFC_08);
      udfC08.setValue(null);
      userDefinedTypes.getCharacterUDFs().add(udfC08);
    }
  }

  private void processIataNumber(UpdateReservationsRequest updateReservationsRequest,
      UserDefinedFieldsType userDefinedTypes) {
    final var characterUDF = new CharacterUDFType();
    characterUDF.setName(UDFC_16);
    characterUDF.setValue(updateReservationsRequest.getDistributionIATANumber());
    userDefinedTypes.addCharacterUDFsItem(characterUDF);
    final var numericUDF = new NumericUDFType();
    double numberDouble = Double.parseDouble(
        updateReservationsRequest.getDistributionIATANumber());
    numericUDF.setName(UDFN_01);
    numericUDF.setValue(BigDecimal.valueOf(numberDouble));
    userDefinedTypes.addNumericUDFsItem(numericUDF);
  }

  /**
   * Used in the get package groups to support packages where the user selected a specific package.
   *
   * @param packagesSelectionList the packages the user set in the request
   * @param packages              the packages linked to a package group
   * @return a Package
   */
  private PackagesSelection getPackagesSelection(List<PackagesSelection> packagesSelectionList,
      PackageGroupType packages) {

    PackagesSelection newPackageSelection;
    var originalPackage = packagesSelectionList.stream()
        .filter(p -> p.getId().equals(packages.getCode())).findFirst();

    if (originalPackage.isPresent()
        && originalPackage.get() instanceof PackagesSelectionScheduled packageScheduled && nonNull(
        packageScheduled.getScheduledDates())) {

      newPackageSelection = new PackagesSelectionScheduled(packageScheduled.getScheduledDates());
    } else {
      newPackageSelection = new PackagesSelection();
    }
    return newPackageSelection;
  }

  private void mapPreference(HotelReservationType reservationType,
      ReservationByBasketRefResponse reservationByBasketRefResponse) {
    if (nonNull(reservationType.getPreferenceCollection()) && nonNull(
        reservationByBasketRefResponse.getReservationByIdList())) {

      var currentReservation = reservationType.getReservationIdList().stream()
          .filter(resType -> resType.getType().equals(RESERVATION)).map(UniqueIDType::getId)
          .findFirst();

      var preferenceList = reservationType.getPreferenceCollection().stream()
          .flatMap(preferenceTypeType -> {
            String preferenceType = preferenceTypeType.getPreferenceType();
            var preferences = preferenceTypeType.getPreference();
            if (preferences == null) {
              return Stream.empty();
            }
            return preferences.stream().map(
                preference -> new ReservationEventPreference(preference.getPreferenceValue(),
                    preferenceType));
          }).toList();

      if (!preferenceList.isEmpty()) {
        reservationByBasketRefResponse.getReservationByIdList().forEach(reservationById -> {
          if (currentReservation.isPresent() && currentReservation.get()
              .equals(reservationById.getReservationId())) {
            reservationById.setPreferences(preferenceList);
          }
        });
      }
    }

  }

  private void updateBillingAddressFeatureFlagOff(BillingAddressRequest billingAddressRequest,
                                                  Set<String> guestProfileIds,
                                                  String addressType,
                                                  String hotelId,
                                                  Set<String> companyProfileIds,
                                                  Set<String> profileIds) {
    if (guestProfileIds != null && ADDRESS_TYPE_BUSINESS.equalsIgnoreCase(addressType)) {
      for (String profileId : guestProfileIds) {
        updateBillingAddressOpera(billingAddressRequest, hotelId, profileId);
      }
    }
    if (companyProfileIds != null && ADDRESS_TYPE_BUSINESS.equalsIgnoreCase(addressType)) {
      updateBillingAddressOpera(billingAddressRequest, hotelId, companyProfileIds.iterator().next());
    }
    if (profileIds != null) {
      updateBillingAddressOpera(billingAddressRequest, hotelId, profileIds.iterator().next());
    }
  }

  private void updateBillingAddressFeatureFlagOn(BillingAddressRequest billingAddressRequest,
                                                 Set<String> guestProfileIds,
                                                 String hotelId,
                                                 Set<String> companyProfileIds,
                                                 Set<String> contactProfileIds) {
    if (billingAddressRequest.isUpdateGuestProfile()) {
      Optional.ofNullable(guestProfileIds).ifPresent(guestProfiles -> guestProfiles.forEach(
          guestProfile -> updateBillingAddressOpera(billingAddressRequest, hotelId, guestProfile)));
    }
    if (billingAddressRequest.isUpdateCompanyProfile()
        && CollectionUtils.isNotEmpty(companyProfileIds)) {
      updateBillingAddressOpera(billingAddressRequest, hotelId,
          companyProfileIds.iterator().next());
    }
    if (billingAddressRequest.isUpdateContactProfile()
        && CollectionUtils.isNotEmpty(contactProfileIds)) {
      updateBillingAddressOpera(billingAddressRequest, hotelId, contactProfileIds.iterator().next());
    }
  }

  @Override
  public boolean isProfileUpdated(String hotelId, String originalProfileId, String tempProfileId) {

    var originalProfile = getProfileById(originalProfileId);
    var tempProfile = getProfileById(tempProfileId);
    var needToUpdate = false;

    if (originalProfile != null && tempProfile != null) {
      needToUpdate = needToUpdateOriginalProfile(originalProfile, tempProfile);
    }

    if (needToUpdate) {
      if (tempProfile.getProfileIdList() != null) {
        tempProfile.getProfileIdList().get(0).setId(originalProfileId);
      }

      //set to empty because opera does not accept id for email and addresses
      if (tempProfile.getProfileDetails().getEmails().getEmailInfo() != null) {
        tempProfile.getProfileDetails().getEmails().getEmailInfo().stream()
            .filter(Objects::nonNull)
            .forEach(emailInfoType -> emailInfoType.setId(null));
      }

      if (tempProfile.getProfileDetails().getAddresses().getAddressInfo() != null) {
        tempProfile.getProfileDetails().getAddresses().getAddressInfo().stream()
            .filter(Objects::nonNull)
            .forEach(addressInfoType -> {
              addressInfoType.setId(null);
              addressInfoType.setType(null);
            });
      }

      ohipReservationClient.sendUpdateProfileRequest(hotelId, tempProfile);
    }

    return needToUpdate;

  }

  @Override
  public void updateReservationAlerts(UpdateReservationAlertsRequest updateAlerts) {

    record ReservationChange(String id, ChangeReservation changeReservation) {

    }

    Flux.fromIterable(
            updateAlerts.getReservationIds())
        .map(resId -> {
          var changeRsv = reservationAlertMapper.toChangeReservationAlertDto(resId,
              updateAlerts.getHotelId(), updateAlerts.getAlerts());
          return new ReservationChange(resId, changeRsv);
        })
        .flatMap(changeReservation -> ohipReservationClient
            .sendPutReservationsGuestRequest(updateAlerts.getHotelId(),
                changeReservation.id, changeReservation.changeReservation))
        .blockLast();
  }

  @Override
  public void updateAbsoluteDeadline(
      UpdateCancellationPoliciesRequest updateCancellationPoliciesRequest) {

    final var reservationIds = updateCancellationPoliciesRequest.getReservationIds();
    final var hotelId = updateCancellationPoliciesRequest.getHotelId();
    final var zonedDateTime = convertStringToZonedDateTime(
        updateCancellationPoliciesRequest.getAbsoluteDeadline());

    CompletableFuture.runAsync(() -> {
      try {
        final long delay = reservationOhipProperties.getNonDigitalThreadSleep();
        log.info("Force update of absolute deadline in {} ms", delay);
        Thread.sleep(delay);
      } catch (final InterruptedException e) {
        log.error("Could not sleep before updating absolute deadline on reservationIds: {}",
            reservationIds);
        ExceptionLogger.log(log, e);
        /* Clean up whatever needs to be handled before interrupting  */
        Thread.currentThread().interrupt();
      }
      reservationIds.parallelStream().forEach(resId -> this.updateCancellationPolicy(
          UpdateCancellationPolicyRequest.builder().reservationId(resId)
              .hotelId(hotelId)
              .absoluteDeadline(Date.from(zonedDateTime.toInstant()))
              .build()));
    });
  }

  /**
   * remove packages groups from the request sent to opera, i.e. package group MDP has 3 package codes,
   * remove MDP from the request object and leave the 3 package codes
   *
   * @param packagesSelection packages associated with a package group
   * @param requestPackages the packages that will be sent to opera
   */
  private void removePackageGroupsFromRequest(List<PackagesSelection> packagesSelection,
      List<PackagesSelection> requestPackages) {

    for (PackagesSelection groupPackages : packagesSelection) {
      requestPackages
          .removeIf(pkg -> pkg.getId().equals(groupPackages.getPackageGroup()));
    }
    requestPackages.addAll(packagesSelection);
  }

  private void removePackageGroupsFromCreateReservationRequest(
      List<PackagesSelection> packagesSelection, Reservation res) {

    var reservationPackages = Optional.ofNullable(res.getReservationPackages())
        .orElseGet(Collections::emptyList);

    if (CollectionUtils.isEmpty(packagesSelection) || reservationPackages == null) {
      return;
    }

    List<ReservationPackages> removedPackages = new ArrayList<>();
    reservationPackages = new ArrayList<>(reservationPackages);
    Iterator<ReservationPackages> iterator = reservationPackages.iterator();
    while (iterator.hasNext()) {
      ReservationPackages rp = iterator.next();
      for (PackagesSelection groupPackages : packagesSelection) {
        if (Objects.nonNull(rp.getPackageCode()) && rp.getPackageCode()
            .equals(groupPackages.getPackageGroup())) {
          removedPackages.add(rp);
          iterator.remove();
          break;
        }
      }
    }

    for (ReservationPackages removed : removedPackages) {
      for (PackagesSelection ps : packagesSelection) {
        ReservationPackages newReservationPackage = new ReservationPackages();
        org.springframework.beans.BeanUtils.copyProperties(removed, newReservationPackage);
        newReservationPackage.setPackageCode(ps.getId());
        newReservationPackage.setPackageGroup(removed.getPackageCode());
        reservationPackages.add(newReservationPackage);
      }
    }

    res.setReservationPackages(reservationPackages);
  }

  private Map<String, Integer> extractTotalGuestForReservation(
      List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> savedReservationList) {

    record StaysTuple(String rsvId, Integer totalGuests) {

    }

    if (savedReservationList != null) {
      var existingRsv = savedReservationList.stream()
          .flatMap(rsv -> rsv.getReservations().getReservation().stream())
          .map(reservation -> {
            var id = reservation.getReservationIdList().stream()
                .filter(idType -> RESERVATION.equals(idType.getType()))
                .findFirst()
                .map(UniqueIDType::getId)
                .orElse(EMPTY_STR);
            var totalGuests = Optional.of(reservation)
                .map(HotelReservationType::getRoomStay)
                .map(RoomStayType::getGuestCounts)
                .map(guestCount -> (guestCount.getAdults() != null ? guestCount.getAdults() : 0)
                    + (guestCount.getChildren() != null ? guestCount.getChildren() : 0))
                .orElse(0);
            return new StaysTuple(id, totalGuests);
          })
          .toList();
      return existingRsv.stream()
          .collect(Collectors.toMap(StaysTuple::rsvId, StaysTuple::totalGuests));
    }

    return new HashMap<>();
  }

  private void mapAlert(HotelReservationType operaReservation,
      ReservationByBasketRefResponse response) {

    if (nonNull(operaReservation.getAlerts()) && nonNull(
        response.getReservationByIdList())) {

      var currentReservation = operaReservation.getReservationIdList().stream()
          .filter(resType -> resType.getType().equals(RESERVATION)).map(UniqueIDType::getId)
          .findFirst();

      var operaAlert = operaReservation.getAlerts()
          .stream()
          .map(alert -> new ReservationAlerts(
              alert.getCode(),
              Objects.nonNull(alert.getArea()) ? alert.getArea().toString() : null,
              alert.getType(),
              alert.getId(),
              alert.getDescription()))
          .toList();

      for (var reservation : response.getReservationByIdList()) {
        if (currentReservation.isPresent()
            && reservation.getReservationId().equals(currentReservation.get())) {
          reservation.setAlerts(operaAlert);
        }
      }
    }
  }

  private ChangeReservation mapToChangeReservationRatePlanRoomType(
      RatePlanRoomTypeChangeRequest roomTypeChangeRequest) {

    var reservationsList = ohipReservationClient
        .getReservations(roomTypeChangeRequest.getHotelId(),
            Set.copyOf(roomTypeChangeRequest.getReservationIds()))
        .collectList()
        .block();

    if (CollectionUtils.isEmpty(reservationsList)
        || roomTypeChangeRequest.getReservationIds().size() != reservationsList.size()) {
      var exception = new HotelReservationException(ErrorCode.DIGITAL_WRONG_RESERVATION_ID,
          String.format("Could not find the right Opera reservation id(s) from basket reference %s",
              roomTypeChangeRequest.getBasketReferenceId()));
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    var hotelReservationTypeList = reservationsList.stream()
        .map(reservation -> reservation.getReservations().getReservation().get(0))
        .toList();

    var hotelAvailabilityList = getHotelAvailabilities(roomTypeChangeRequest);

    boolean isAnyRoomRatesEmpty = hotelAvailabilityList.stream()
        .flatMap(hotelAvailabilityDto -> hotelAvailabilityDto.getRoomStays().stream())
        .anyMatch(roomStay -> roomStay.getRoomRates() == null || roomStay.getRoomRates().isEmpty());

    if (CollectionUtils.isEmpty(hotelAvailabilityList) || isAnyRoomRatesEmpty) {
      var exception = new HotelAvailabilityException(ErrorCode.DIGITAL_WRONG_OPERA_PRICE,
          String.format(
              "Could not find any Opera price to be able to change room type for hotel %s in period %s-%s",
              roomTypeChangeRequest.getHotelId(),
              roomTypeChangeRequest.getStartDate(),
              roomTypeChangeRequest.getEndDate()));
      ExceptionLogger.log(log, exception);
      throw exception;
    }

    try {
      return roomTypeChangeRequestOhipMapper.toChangeReservationDto(roomTypeChangeRequest,
          hotelReservationTypeList, hotelAvailabilityList,
          reservationOhipProperties.getDefaultMarketCode());
    } catch (Exception exception) {
      ExceptionLogger.log(log, exception);
      throw exception;
    }
  }

  private String sanitizeInput(String input) {
    return Optional.ofNullable(input)
        .map(in -> in.replaceAll(CONTROL_CHARACTER_REGEX, "")
            .replaceAll("[^A-Za-z0-9_-]", ""))
        .orElse("null");
  }

  private List<uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation> getReservations(
      String hotelId, Set<String> reservationIds) {
    log.debug("Fetching reservations for hotelId={} and {} reservation ids",
        sanitizeInput(hotelId), reservationIds.size());
    var reservationList = ohipReservationClient.getReservations(hotelId, reservationIds)
        .collectList().block();
    sortReservationListAsInitialSet(reservationIds, reservationList);
    return reservationList;
  }

  @Override
  public PreCheckInResponse saveReservationPreRegister(PreCheckInRequest preCheckInRequest) {
    log.debug("Entered saveReservationPreRegister for {}", preCheckInRequest);

    PreCheckInReservation preCheckInReservation = buildPreCheckInRequest(preCheckInRequest.getArrivalTime(),
            preCheckInRequest.getHotelId());

    var preCheckInResponse = ohipReservationClient.savePreCheckInStatus(preCheckInReservation,
            preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId());
    boolean isSuccess = ObjectUtils.isNotEmpty(preCheckInResponse) && ObjectUtils.isNotEmpty(
            preCheckInResponse.getLinks()) && !preCheckInResponse.getLinks().isEmpty();

    if (isSuccess) {
      log.info("Successfully set preRegistered flag for hotelId={}, reservationId={}",
              preCheckInRequest.getHotelId(), preCheckInRequest.getReservationId());
    }
    return buildPreCheckInResponse(isSuccess, "Pre-Register status saved successfully",
            "Error in saving Pre-Register status");
  }

  private static PreCheckInReservation buildPreCheckInRequest(Date arrivalTime, String hotelId) {
    ReservationId reservationId = new ReservationId();
    reservationId.setType(RESERVATION);

    ReservationArrivalInfoType reservationArrivalInfoType = new ReservationArrivalInfoType();
    reservationArrivalInfoType.setArrivalTime(arrivalTime);

    PreCheckInDetailsType preCheckInDetailsType = new PreCheckInDetailsType();
    preCheckInDetailsType.setArrival(reservationArrivalInfoType);

    ReservationPreCheckInDetailsType reservationPreCheckInDetailsType = new ReservationPreCheckInDetailsType();
    reservationPreCheckInDetailsType.setReservationId(reservationId);
    reservationPreCheckInDetailsType.setHotelId(hotelId);
    reservationPreCheckInDetailsType.setPreCheckInDetails(preCheckInDetailsType);

    PreCheckInReservation preCheckInReservation = new PreCheckInReservation();
    preCheckInReservation.setReservation(reservationPreCheckInDetailsType);
    return preCheckInReservation;
  }

}
