package uk.co.whitbread.ohip.infrastructure.rest.client.reservation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_WRONG_OPERA_PRICE;
import static uk.co.whitbread.ohip.ErrorCode.DIGITAL_WRONG_RESERVATION_ID;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_FOLIOS_ACI_AMOUNT_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_PACKAGES_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_RESERVATIONS_BY_EXTERNALREFIDS_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_RESERVATION_BY_RESID_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_GET_RESERVATION_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION;
import static uk.co.whitbread.ohip.ErrorCode.OHIP_RETRIEVE_HOTEL_CONFIG_EXCEPTION;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.EXTERNAL_REF_MIGRATION_ID_CONTEXT;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.RATE_PLAN_CODE;
import static uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants.UDFC_08;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.tracing.Tracer;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.LinkedMultiValueMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AdvanceCheckInType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertAreaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AlertType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.AmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.BillingInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CancelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CardTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChangeReservationDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CharacterUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ChargeCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CommentType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CompanyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CreateReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CurrencyAmountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.CurrentRoomInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteria;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DepositFolioCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.DiscountType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ExternalReferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.GuestCountsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationTypeReservationProfiles;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.HotelReservationsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.InstanceLink;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.NumericUDFType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PMSResStatusType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageCodeHeaderType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PackageConsumptionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PaymentCriteriaType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PolicyDeadlineType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PostedDepositFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreCheckInReservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.PreferenceTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RateInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RatesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResAttachedProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResCancelPenaltyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResCancellationPolicyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResCashieringType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResDepositPolicyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuaranteeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestAdditionalInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResGuestTypeProfileInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ResProfileTypeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.Reservation;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationId;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPackageType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPaymentMethodType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationPoliciesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationProfileType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationTaxTypeInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ReservationsDetailsReservations;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RetrievedDepositFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomRateType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoomStayType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInfoTypeFolio;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInstructionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.RoutingInstructionTypeDuration;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.StayInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TimeSpanType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TotalType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.TrxInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UniqueIDType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.UserDefinedFieldsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CompanyProfileTypeEmails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.CustomerTypeIdentifications;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.EmailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.IdentificationType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.PersonNameType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Profile;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileSummaries;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.Status;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoTypePropertyControls;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.enterprise.HotelInfoTypePropertyControlsDateTimeFormatting;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CreditCardInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.ResPaymentCardType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.CancellationPolicyDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.CancellationPolicyDetailsCancelPenalties;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.CodeDescriptionType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.ConfigCancelPenaltyType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.HotelConfigCancelPenaltiesType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.PolicyScheduleDetailType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.PolicyScheduleType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.PolicySchedulesDetails;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.rate.PackagesInfoPackageCodesList;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.HotelPackageGroupsType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageCodeType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupType;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfo;
import uk.co.whitbread.hotel.ohip.adapter.generated.models.ratev0.PackageGroupsInfoPackageGroupList;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleDto;
import uk.co.whitbread.hotel.rules.agent.generated.models.BusinessAllowanceRuleResponseDto;
import uk.co.whitbread.hotel.rules.agent.generated.models.TransactionCodeDto;
import uk.co.whitbread.hotel.rules.agent.generated.models.VatRuleResponseDto;
import uk.co.whitbread.ohip.ErrorCode;
import uk.co.whitbread.ohip.domain.logic.utils.OhipTestUtils;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeCriteria;
import uk.co.whitbread.ohip.domain.model.availability.in.RateCodeRoomInfoCriteria;
import uk.co.whitbread.ohip.domain.model.feature.FeatureFlag;
import uk.co.whitbread.ohip.domain.model.feature.UnleashWrapper;
import uk.co.whitbread.ohip.domain.model.reservation.in.AccompanyingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.Alert;
import uk.co.whitbread.ohip.domain.model.reservation.in.AttachReservationProfileRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BillingAddressRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnp;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookerDetailsCnpRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingChannel;
import uk.co.whitbread.ohip.domain.model.reservation.in.BookingSearchCriteria;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessAllowance;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItems;
import uk.co.whitbread.ohip.domain.model.reservation.in.BusinessItemsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CancelReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswer;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.CompanyQuestionAndAnswerDetailsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ConfirmReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.CreateMemoRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.DepositFolioRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.PackagesSelection;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentCard;
import uk.co.whitbread.ohip.domain.model.reservation.in.PaymentOption;
import uk.co.whitbread.ohip.domain.model.reservation.in.PreCheckInRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ProfileUpdateIndicators;
import uk.co.whitbread.ohip.domain.model.reservation.in.QuestionAndAnswerTypeEnum;
import uk.co.whitbread.ohip.domain.model.reservation.in.RatePlanRoomTypeChangeRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.RateType;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationFileAttachmentRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationGuestRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackages;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPackagesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationPreferencesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationTestUtils;
import uk.co.whitbread.ohip.domain.model.reservation.in.ReservationType;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomOccupancy;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRate;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomRateReservation;
import uk.co.whitbread.ohip.domain.model.reservation.in.RoomsSelections;
import uk.co.whitbread.ohip.domain.model.reservation.in.ScheduleList;
import uk.co.whitbread.ohip.domain.model.reservation.in.SpecialRequests;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAdditionalDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.in.StayingGuestDetails;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateBookerEmailRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCancellationPoliciesRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateCustomReferenceNumberRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateDiscountRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReasonForStayRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationAlertsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationCcAgentIdRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationOverrideReasonsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationRequest;
import uk.co.whitbread.ohip.domain.model.reservation.in.UpdateReservationsRequest;
import uk.co.whitbread.ohip.domain.model.reservation.out.AddressTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ConfirmReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmount;
import uk.co.whitbread.ohip.domain.model.reservation.out.Customer;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolio;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFolioCharge;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositFoliosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositPolicies;
import uk.co.whitbread.ohip.domain.model.reservation.out.Deposits;
import uk.co.whitbread.ohip.domain.model.reservation.out.DepositsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.EmailTypeResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.Guarantee;
import uk.co.whitbread.ohip.domain.model.reservation.out.GuestAddress;
import uk.co.whitbread.ohip.domain.model.reservation.out.MarketingPreferencesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.Memo;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemoId;
import uk.co.whitbread.ohip.domain.model.reservation.out.MemosResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.PreCheckInResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileIdResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ProfileType;
import uk.co.whitbread.ohip.domain.model.reservation.out.RateInfoSummary;
import uk.co.whitbread.ohip.domain.model.reservation.out.RatePerNight;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationById;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationCreationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationGuestResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationLightweightResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationOverrideReasons;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesDetailsResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPackagesResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationResponse;
import uk.co.whitbread.ohip.domain.model.reservation.out.ReservationsPaymentCardType;
import uk.co.whitbread.ohip.domain.model.reservation.out.RoomStay;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBooking;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingBooker;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingReservation;
import uk.co.whitbread.ohip.domain.model.reservation.out.SearchBookingStayingGuest;
import uk.co.whitbread.ohip.domain.model.reservation.out.UniqueIdType;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.exceptions.HotelAvailabilityException;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.AmountTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.HotelAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.PriceBreakdownDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RatesTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomRateTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.RoomStayTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.SummaryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.in.TotalTypeDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.InventoryAvailabilityDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.model.out.ItemInventoryResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.ApiLimitsService;
import uk.co.whitbread.ohip.infrastructure.rest.client.availability.ohip.OhipAvailabilityClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.frontdesk.ohip.OhipFrontDeskClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipConstants;
import uk.co.whitbread.ohip.infrastructure.rest.client.ohip.config.OhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackageGroupsRequestOhipMapper;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.mapper.PackagesResponseOhipMapperTest;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.in.PackagesResponseOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.model.out.PackagesRequestOhipDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.packages.ohip.OhipPackagesClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.DiscountInvalidAmountException;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.exceptions.HotelPackageException;
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
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.OhipReservationClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.reservation.ohip.properties.ReservationOhipProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.RulesAgentClient;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.BookingChannelInfoResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.ChannelRuleRequestDetailsDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.model.out.ChannelRuleResponseDto;
import uk.co.whitbread.ohip.infrastructure.rest.client.rules.properties.RulesAgentProperties;
import uk.co.whitbread.ohip.infrastructure.rest.client.utils.ReservationPaymentMethodUtils;
import uk.co.whitbread.ohip.infrastructure.rest.controller.reservation.mapper.ReservationDetailsEnhancedResponseMapper;
import uk.co.whitbread.shared.commons.logging.trace.ConcurrentTracer;

@ExtendWith(MockitoExtension.class)
class HotelReservationOutPortImplTest {

  public static final String MOCK_PACKAGES_RESPONSE_JSON = "__files/ohip_packageCodesList_object.json";
  public static final String MOCK_PROFILE_SUMMARIES_RESPONSE_JSON = "__files/ohip_profileSummariesList_object.json";
  public static final String MOCK_PROFILE_RESPONSE_JSON = "__files/ohip_profileInfo_object.json";
  public static final String MOCK_RESERVATION_DETAILS_RESPONSE_JSON = "__files/ohip_reservationsDetails_object.json";
  private static final String MOCK_ROUTING_INSTRUCTIONS_RESPONSE_JSON =
      "__files/ohip_reservationRoutingInstructions_object.json";

  private static final String MOCK_ROUTING_INSTRUCTIONS_WITH_RESERVATION_PACKAGES_RESPONSE_JSON =
      "__files/ohip_reservationRoutingInstructionsWithReservationPackages_object.json";
  private static final String MOCK_RULES_BUSINESS_ALLOWANCES_RESPONSE_JSON =
      "__files/get_business_allowances_success_response.json";

  private static final String MOCK_RATE_INFO_RESPONSE_JSON =
      "__files/ohip_rate_info.json";

  private static final String MOCK_GET_FOLIOS_RESPONSE_JSON =
      "__files/ohip_getFolios.json";
  private static final String MOCK_GET_FOLIOS_RESPONSE_CANCELLED_RSV_JSON =
      "__files/ohip_getFolios_CancelledRsv.json";
  private static final String MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON =
      "__files/ohip_getFolios_CC_Rsv.json";
  private static final String MOCK_GET_FOLIOS_WITH_ACI_RESPONSE_JSON =
      "__files/ohip_getFolios_withACI_Rsv.json";
  private static final String MOCK_OHIP_RESERVATION_ATTACHMENT_RESPONSE_JSON =
      "__files/ohip_reservation_attachment_response.json";
  private static final String HOTEL_ID = "hotelId";
  private static final String RESERVATION_ID = "123456";
  private static final String EXTERNAL_REFERENCE_ID = "externalReferenceId";
  private static final String OPERA_LANGUAGE_CODE_E = "E";
  private static final String OPERA_LANGUAGE_CODE_DE = "DE";
  private static final String WB_LANGUAGE_CODE_EN = "en";
  private static final String WB_LANGUAGE_CODE_DE = "de";
  private static final int LIMIT = 20;
  private static final int OFFSET = 0;
  private static final ObjectMapper mapper = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
      .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL);
  private final FeatureFlag featureFlag = new FeatureFlag();
  @InjectMocks @Spy
  private HotelReservationOutPortImpl hotelReservationOutPort;
  @Mock
  private OhipReservationClient ohipReservationClient;
  @Mock
  private OhipAvailabilityClient ohipAvailabilityClient;
  @Mock
  private ReservationResponseOhipMapper reservationResponseOhipMapper;
  @Mock
  private ConfirmationResponseOhipMapper confirmationResponseOhipMapper;
  @Mock
  private ChangeReservationRequestOhipMapper changeReservationRequestOhipMapper;

  @Mock
  private CompanyQuestionAndAnswerRequestOhipMapper companyQuestionAndAnswerRequestOhipMapper;
  @Mock
  private SpecialReqRequestOhipMapper specialReqRequestOhipMapper;
  @Mock
  private ConfirmReservationRequestOhipMapper confirmReservationRequestOhipMapper;
  @Mock
  private ReservationRequestOhipMapper reservationRequestOhipMapper;
  @Mock
  private ReservationPackagesOhipMapper reservationPackagesOhipMapper;
  @Mock
  private PackagesRequestDtoOhipMapper packagesRequestDtoOhipMapper;
  @Mock
  private OhipPackagesClient ohipPackagesClient;
  @Mock
  private ReservationOhipProperties reservationOhipProperties;
  @Mock
  private PackageGroupsRequestOhipMapper packageGroupsRequestOhipMapper;
  @Mock
  private PackagesSelectionMapper packagesSelectionMapper;
  @Mock
  private RulesAgentClient rulesAgentClient;
  @Mock
  private CancelReservationRequestOhipMapper cancelReservationRequestOhipMapper;
  @Mock
  private CancelReservationResponseOhipMapper cancelReservationResponseOhipMapper;
  @Mock
  private PriceBreakdownOhipMapper priceBreakdownOhipMapper;
  @Mock
  private RoomTypeChangeRequestOhipMapper roomTypeChangeRequestOhipMapper;
  @Mock
  private RatePlanChangeRequestOhipMapper rateChangeRequestOhipMapper;
  @Mock
  private UpdateDiscountRequestOhipMapper updateDiscountRequestOhipMapper;
  @Mock
  private SearchBookingsResponseOhipMapper searchBookingsResponseOhipMapper;
  @Mock
  private ReservationDetailsEnhancedResponseMapper reservationDetailsEnhancedResponseMapper;
  @Mock
  private UpdateReasonForStayRequestOhipMapper updateReasonForStayRequestOhipMapper;
  @Mock
  private UpdateReasonForStayResponseOhipMapper updateReasonForStayResponseOhipMapper;
  @Mock
  private ReservationBookerRequestOhipMapper reservationBookerRequestOhipMapper;
  @Mock
  private ReservationCompanyRequestOhipMapper reservationCompanyRequestOhipMapper;
  @Mock
  private ReservationGuestResponseOhipMapper reservationGuestResponseOhipMapper;
  @Mock
  private ReservationGuestRequestOhipMapper reservationGuestRequestOhipMapper;
  @Mock
  private UpdateReservationOverrideReasonsRequestOhipMapper updateReservationOverrideReasonsRequestOhipMapper;
  @Mock
  private UpdateReservationCcAgentIdRequestOhipMapper updateReservationCcAgentIdRequestOhipMapper;
  @Mock
  private BusinessItemsRequestOhipMapper businessItemsRequestMapper;
  @Mock
  private DepositsResponseOhipMapper depositsResponseOhipMapper;
  @Mock
  private DepositFoliosRequestMapper depositFoliosRequestMapper;
  @Mock
  private DepositFoliosRequestOhipMapper depositFoliosRequestOhipMapper;
  @Mock
  private OhipFrontDeskClient frontDeskClient;
  @Mock
  private MarketingPreferencesResponseOhipMapper marketingPreferencesResponseOhipMapper;
  @Mock
  private RulesAgentProperties rulesAgentProperties;
  @Mock
  private UpdateReservationRequestOhipMapper updateReservationRequestOhipMapper;
  @Mock
  private PayOnArrivalReservationRequestOhipMapper payOnArrivalReservationRequestOhipMapper;
  @Mock
  private MovePaymentDetailsOhipMapper movePaymentDetailsOhipMapper;
  @Mock
  private BookerProfileOhipMapper bookerProfileOhipMapper;
  @Mock
  private DepositFolioCriteriaMapper depositFolioCriteriaMapper;
  @Mock
  private CompanyProfileOhipMapper companyProfileOhipMapper;
  @Mock
  private UpdateReservationAttachedProfilesRequestOhipMapper updateReservationAttachedProfilesRequestOhipMapper;
  @Mock
  private OhipProperties ohipProperties;
  @Mock
  private UpdateBookerReservationProfileOhipMapper updateBookerReservationProfileOhipMapper;
  @Mock
  private UpdateBookerEmailOhipMapper updateBookerEmailOhipMapper;
  @Mock
  private MemosOhipMapper memosOhipMapper;
  @Mock
  private PostedDepositsResponseOhipMapper postedDepositsResponseOhipMapper;
  @Mock
  private AttachReservationProfileRequestOhipMapper attachReservationProfileRequestOhipMapper;
  @Mock
  private UpdateCustomReferenceNumberOhipMapper updateCustomReferenceNumberOhipMapper;
  @Mock
  private UnleashWrapper<FeatureFlag> unleashWrapper;
  @Spy
  private ConcurrentTracer concurrentTracer = new ConcurrentTracer(Tracer.NOOP);
  @Mock
  private ReservationAccompanyingGuestProfileRequestOhipMapper
      reservationAccompanyingGuestProfileRequestOhipMapper;
  @Mock
  private ReservationAccompanyingGuestRequestOhipMapper
      reservationAccompanyingGuestRequestOhipMapper;
  @Mock
  private LinkReservationToLeisureCustomerRequestOhipMapper linkReservationToLeisureCustomerRequestOhipMapper;
  @Mock
  private UpdatePreferencesRequestOhipMapper updatePreferencesRequestOhipMapper;
  @Mock
  private GuestDetailsMapper guestDetailsMapper;
  @Mock
  private ReservationAlertMapper reservationAlertMapper;
  @Mock
  private RoomRateOhipMapper roomRateOhipMapper;

  @Mock
  private ApiLimitsService apiLimitsService;

  @Test
  void getReservationsByExternalReferenceIds__ShouldReturnOK() {
    //Arrange
    when(ohipReservationClient.sendGetReservationsByExternalReferenceIdsRequest(HOTEL_ID,
        Collections.singletonList(EXTERNAL_REFERENCE_ID), LIMIT, OFFSET))
        .thenReturn(mockReservationsDetailsOhip());
    when(reservationResponseOhipMapper.toReservationsDetailsResponseModel(
        mockReservationsDetailsOhip())).thenReturn(
        OhipTestUtils.createReservationsDetailsResponse(HOTEL_ID, EXTERNAL_REFERENCE_ID));

    //Act
    var reservationsDetailsResponse = hotelReservationOutPort.getReservationsByExternalReferenceIds(
        HOTEL_ID, Collections.singletonList(EXTERNAL_REFERENCE_ID), LIMIT, OFFSET);

    //Assert
    assertNotNull(reservationsDetailsResponse);
    assertEquals(EXTERNAL_REFERENCE_ID,
        reservationsDetailsResponse.getReservations().getReservationInfo().get(0)
            .getExternalReferences().get(0).getId());
    assertNotNull(Collections.singletonList(EXTERNAL_REFERENCE_ID));
  }

  @Test
  void getReservationPaymentMethod__ShouldReturnOK() {

    //Arrange
    uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType card =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    UniqueIDType id = new UniqueIDType();
    id.setId("cardId");
    card.cardId(id);
    card.setCardNumber("1234567890123456");
    card.setCardType(CardTypeType.CU);
    var reservation1 = mockReservationsPayment(card);
    var reservation2 = mockReservationsPayment(card);
    AtomicInteger i = new AtomicInteger(0);
    when(ohipReservationClient.getReservationPaymentMethods(HOTEL_ID,
        RESERVATION_ID))
        .thenAnswer(invocation -> {
          i.getAndIncrement();
          if (1 == i.intValue()) {
            return Mono.just(reservation1);
          } else {
            return Mono.just(reservation2);
          }
        });

    ReservationsPaymentCardType paymentCardType = ReservationsPaymentCardType.builder()
        .paymentCardType(ReservationPaymentCardType.builder()
            .cardId(UniqueIdType.builder().id("cardId").build())
            .cardType("CU")
            .folioView(1)
            .paymentMethod("BU")
            .build())
        .build();
    when(reservationResponseOhipMapper.toPaymentCardReservationModel(
        any(), any(), any())).thenReturn(paymentCardType);

    //Act
    var reservationsResponse = hotelReservationOutPort.getReservationPaymentMethod(
        HOTEL_ID, List.of(RESERVATION_ID, RESERVATION_ID));

    //Assert
    assertEquals(2, i.get());
    assertNotNull(reservationsResponse);
    assertNotNull(reservationsResponse.get(0));
    assertEquals(2, reservationsResponse.size());
    assertEquals(1, reservationsResponse.get(0).getPaymentCardType().getFolioView());
    assertEquals("BU", reservationsResponse.get(0).getPaymentCardType().getPaymentMethod());
    assertEquals("cardId", reservationsResponse.get(0).getPaymentCardType().getCardId().getId());
  }

  @Test
  void getReservationPaymentMethod__ShouldReturnOnePayment() {

    //Arrange
    uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType card =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    UniqueIDType id = new UniqueIDType();
    id.setId("cardId");
    card.cardId(id);
    card.setCardNumber("1234567890123456");
    card.setCardType(CardTypeType.CU);
    var reservation = mockReservationsPayment(card);
    AtomicInteger i = new AtomicInteger(0);
    when(ohipReservationClient.getReservationPaymentMethods(HOTEL_ID,
        RESERVATION_ID))
        .thenAnswer(invocation -> {
          i.getAndIncrement();
          if (1 == i.intValue()) {
            return Mono.just(reservation);
          } else {
            String error = "An error was returned by OHIP!";
            return Mono.error(new HotelReservationException(
                OHIP_GET_RESERVATIONS_BY_EXTERNALREFIDS_EXCEPTION, error));
          }
        });

    ReservationsPaymentCardType paymentCardType = ReservationsPaymentCardType.builder()
        .paymentCardType(ReservationPaymentCardType.builder()
            .cardId(UniqueIdType.builder().id("cardId").build())
            .cardType("CU")
            .folioView(1)
            .paymentMethod("BU")
            .build())
        .build();
    when(reservationResponseOhipMapper.toPaymentCardReservationModel(
        any(), any(), any())).thenReturn(paymentCardType);

    //Act
    var reservationsResponse = hotelReservationOutPort.getReservationPaymentMethod(
        HOTEL_ID, List.of(RESERVATION_ID, RESERVATION_ID));

    //Assert
    assertEquals(2, i.get());
    assertNotNull(reservationsResponse);
    assertEquals(1, reservationsResponse.size());
    assertNotNull(reservationsResponse.get(0));

    assertEquals(1, reservationsResponse.get(0).getPaymentCardType().getFolioView());
    assertEquals("BU", reservationsResponse.get(0).getPaymentCardType().getPaymentMethod());
    assertEquals("cardId", reservationsResponse.get(0).getPaymentCardType().getCardId().getId());
  }

  @Test
  void getReservationsPaymentMethodsWhenError__shouldReturnEmpty() {
    //Arrange
    String error = "An error was returned by OHIP!";
    Mockito.when(ohipReservationClient.getReservationPaymentMethods(HOTEL_ID, RESERVATION_ID))
        .thenReturn(Mono.error(new HotelReservationException(
            OHIP_GET_RESERVATIONS_BY_EXTERNALREFIDS_EXCEPTION, error)));

    //Act
    var reservationsResponse = hotelReservationOutPort.getReservationPaymentMethod(
        HOTEL_ID, Collections.singletonList(RESERVATION_ID));

    // Assert
    assertEquals(0, reservationsResponse.size());
  }

  @ParameterizedTest
  @MethodSource("provideNullAndEmptyReservationIdLists")
  void getReservationsPaymentMethods__shouldReturnEmptyWhenNullOrEmpty(List<String> reservationIds) {
    //Act
    var reservationsResponse = hotelReservationOutPort.getReservationPaymentMethod(
        HOTEL_ID, reservationIds);

    // Assert
    assertEquals(0, reservationsResponse.size());
  }

  private static Stream<Arguments> provideNullAndEmptyReservationIdLists() {
    return Stream.of(
        Arguments.of((List<String>) null),
        Arguments.of(Collections.emptyList())
    );
  }

  @Test
  void getReservationsByExternalReferenceIds__shouldThrowException() {
    //Arrange
    String error = "An error was returned by OHIP!";
    Mockito.when(ohipReservationClient.sendGetReservationsByExternalReferenceIdsRequest(HOTEL_ID,
            Collections.singletonList(EXTERNAL_REFERENCE_ID), LIMIT, OFFSET))
        .thenThrow(new HotelReservationException(OHIP_GET_RESERVATIONS_BY_EXTERNALREFIDS_EXCEPTION,
            error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getReservationsByExternalReferenceIds(
                HOTEL_ID, Collections.singletonList(EXTERNAL_REFERENCE_ID), LIMIT, OFFSET)
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getReservationsByExternalReferenceIds__shouldThrowExternalRefIdsException() {
    //Arrange
    String error = "Error while trying to get reservations by external reference ids for hotelId=%s and %s ids";
    Mockito.when(ohipReservationClient.sendGetReservationsByExternalReferenceIdsRequest(HOTEL_ID,
            null, LIMIT, OFFSET))
        .thenThrow(new HotelReservationException(OHIP_GET_RESERVATIONS_BY_EXTERNALREFIDS_EXCEPTION,
            error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getReservationsByExternalReferenceIds(
                HOTEL_ID, null, LIMIT, OFFSET)
        );

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void getReservationsByReservationId__shouldThrowException() {
    //Arrange
    String error = "An error was returned by OHIP!";
    Mockito.when(ohipReservationClient.sendGetReservationsByReservationId(HOTEL_ID,
            RESERVATION_ID))
        .thenThrow(new HotelReservationException(OHIP_GET_RESERVATION_BY_RESID_EXCEPTION,
            error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getReservationsByReservationId(
                HOTEL_ID, RESERVATION_ID)
        );

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void getReservationsByIds__shouldThrowException() {
    //Arrange
    String error = "An error was returned by OHIP!";
    Mockito.when(ohipReservationClient.getReservations(HOTEL_ID,
            new HashSet<>(List.of(EXTERNAL_REFERENCE_ID))))
        .thenThrow(new HotelReservationException(OHIP_GET_RESERVATION_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getReservationsByIds(
                HOTEL_ID, new HashSet<>(List.of(EXTERNAL_REFERENCE_ID)), false, true, false)
        );

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void testGetReservationsByIds_ShouldReturnLightweightResponse() {
    // Arrange
    String hotelId = "HOTEL123";
    Set<String> reservationIds = Set.of("RES1", "RES2");
    ReservationLightweightResponse mockResponse = new ReservationLightweightResponse(List.of());
    HotelDetails hotelDetails = mockHotelDetails();
    var mockReservation = mockReservation();

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation)));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(hotelDetails);
    when(reservationResponseOhipMapper.toReservationLightweightResponseModel(
        anyList(), any(), anyString()))
        .thenReturn(mockResponse);

    // Act
    ReservationLightweightResponse response = hotelReservationOutPort.getReservationsByIds(hotelId, reservationIds);

    // Assert
    assertNotNull(response);
    verify(ohipReservationClient).getReservations(hotelId, reservationIds);
    verify(reservationResponseOhipMapper).toReservationLightweightResponseModel(
        List.of(mockReservation), hotelDetails, hotelId);
  }

  @Test
  void testGetReservationsByIds_ShouldHandleEmptyReservationIds() {
    // Arrange
    String hotelId = "HOTEL123";
    Set<String> reservationIds = Set.of();

    when(ohipReservationClient.getReservations(hotelId, reservationIds))
        .thenReturn(Flux.empty());

    // Act & Assert
    HotelReservationNotFound exception = assertThrows(HotelReservationNotFound.class, () -> {
      hotelReservationOutPort.getReservationsByIds(hotelId, reservationIds);
    });

    assertEquals("Could not find Opera reservation", exception.getMessage());
  }

  @Test
  void getReservationById__WithUserDefinedCardType__ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(frontDeskClient.getCreditCardInfo(any(), any())).thenReturn(
        mockCreditCardInfoUserDefinedCardType());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationByIdWithUserDefinedCardType());

    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard().getUserDefinedCardType());
    assertEquals("BU", reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard().getUserDefinedCardType());
  }

  @Test
  void getReservationById__WithUserDefinedCardTypeEmpty__ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(frontDeskClient.getCreditCardInfo(any(), any())).thenReturn(
        mockCreditCardInfoUserDefinedCardTypeEmpty());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());

    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard().getUserDefinedCardType());
  }

  @Test
  void getReservationById__RateInfoIncluded__ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertEquals(BigDecimal.valueOf(100), reservationByBasketRefResponse.getTotalCost());
    assertEquals(BigDecimal.valueOf(0), reservationByBasketRefResponse.getPreviousTotal());
    assertEquals(BigDecimal.valueOf(100), reservationByBasketRefResponse.getBalanceOutstanding());
    assertEquals(BigDecimal.ZERO, reservationByBasketRefResponse.getAmountPaid());
    assertEquals(BigDecimal.TEN, reservationByBasketRefResponse.getDiscount());
    assertEquals("GBP", reservationByBasketRefResponse.getCurrencyCode());

    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard());
    assertEquals("XXXXXXXXXXXX1100",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard()
            .getCardNumberMasked());
    assertEquals("4764776852337921100",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard()
            .getToken());
    assertEquals("Va",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard()
            .getCardType());

    assertNotNull(
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons());
    assertEquals("ILL",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getReasonCode());
    assertEquals("Illness",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getReasonName());
    assertEquals("John Doe",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getCallerName());
    assertEquals("James Bond",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getManagerName());
    assertTrue(
        reservationByBasketRefResponse.getReservationByIdList().get(0).isReservationOverridden());
    assertEquals("cellCode",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay().getCellCode());
    assertEquals("120",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRoomNumber());
    assertEquals("PI.com",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getBookingChannel());
    assertEquals("E",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getReservationGuestList()
            .get(0).getLanguage());
    assertEquals("First Line", reservationByBasketRefResponse.getReservationByIdList().get(0).getReservationGuestList()
        .get(0).getAddress().getAddressLine1());
    assertEquals("Big City", reservationByBasketRefResponse.getReservationByIdList().get(0).getReservationGuestList()
        .get(0).getAddress().getCityName());
    assertEquals("PO5 TA1", reservationByBasketRefResponse.getReservationByIdList().get(0).getReservationGuestList()
        .get(0).getAddress().getPostalCode());
    assertEquals(BigDecimal.valueOf(60),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getBalanceAmount());
    assertEquals(BigDecimal.valueOf(59.94),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getCityTaxPerNight());
    assertEquals(BigDecimal.valueOf(999),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getPricePerNight());

    // for non DISTR channels some fields from RatePerNight should not be populated
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getGrossPricePerNight());
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getVatRate());
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getCityTaxAmountBeforeTax());
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getCityTaxVat());

  }

  @Test
  void getReservationById__RateInfoExcluded__ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, false, false);

    //Assert
    verify(ohipReservationClient, times(0)).getReservationAmounts(anyString(), anyString());
    verify(ohipReservationClient, times(0)).getFoliosAciAmount(anyString(), anyString());

    assertNotNull(reservationByBasketRefResponse);
    assertNull(reservationByBasketRefResponse.getTotalCost());
    assertNull(reservationByBasketRefResponse.getPreviousTotal());
    assertNull(reservationByBasketRefResponse.getBalanceOutstanding());
    assertNull(reservationByBasketRefResponse.getAmountPaid());
    assertNull(reservationByBasketRefResponse.getDiscount());
    assertNull(reservationByBasketRefResponse.getNewTotal());
    assertNull(reservationByBasketRefResponse.getTotalCostWoDiscount());
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRateInfo());
    assertEquals("GBP", reservationByBasketRefResponse.getCurrencyCode());

    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard());
    assertEquals("XXXXXXXXXXXX1100",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard()
            .getCardNumberMasked());
    assertEquals("4764776852337921100",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard()
            .getToken());
    assertEquals("Va",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPaymentCard()
            .getCardType());

    assertNotNull(
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons());
    assertEquals("ILL",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getReasonCode());
    assertEquals("Illness",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getReasonName());
    assertEquals("John Doe",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getCallerName());
    assertEquals("James Bond",
        reservationByBasketRefResponse.getReservationByIdList().get(0)
            .getReservationOverrideReasons()
            .getManagerName());
    assertTrue(
        reservationByBasketRefResponse.getReservationByIdList().get(0).isReservationOverridden());
    assertEquals("cellCode",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay().getCellCode());
    assertEquals("120",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRoomNumber());
    assertEquals("PI.com",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getBookingChannel());
    assertEquals("E",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getReservationGuestList()
            .get(0).getLanguage());
    assertEquals(BigDecimal.valueOf(60),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getBalanceAmount());
    assertEquals(BigDecimal.valueOf(59.94),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getCityTaxPerNight());
    assertEquals(BigDecimal.valueOf(999),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getPricePerNight());

    // for non DISTR channels some fields from RatePerNight should not be populated
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getGrossPricePerNight());
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getVatRate());
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getCityTaxAmountBeforeTax());
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
        .getRatesPerNight().get(0).getCityTaxVat());

  }

  @Test
  void getReservationById_withAci_ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithAci())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_JSON));
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertEquals(BigDecimal.valueOf(100), reservationByBasketRefResponse.getTotalCost());
    assertEquals(BigDecimal.valueOf(100), reservationByBasketRefResponse.getPreviousTotal());
    assertEquals(BigDecimal.ZERO, reservationByBasketRefResponse.getBalanceOutstanding());
    assertEquals(BigDecimal.valueOf(100), reservationByBasketRefResponse.getAmountPaid());
    assertEquals(BigDecimal.TEN, reservationByBasketRefResponse.getDiscount());
    assertEquals("GBP", reservationByBasketRefResponse.getCurrencyCode());
  }

  @Test
  void getReservationsByIds_WithAci_shouldThrowException() throws IOException {
    //Arrange
    String error = "An error was returned by OHIP!";
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithAci())));
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString()))
        .thenThrow(new HotelReservationException(OHIP_GET_FOLIOS_ACI_AMOUNT_EXCEPTION,
            error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getReservationsByIds(
                HOTEL_ID, new HashSet<>(List.of(EXTERNAL_REFERENCE_ID)), false, true, false)
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void getReservationById__distrChannel_ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    var reservationById = mockReservationById();
    reservationById.getRoomStay().setRatesPerNight(mockRatesPerNightDistr());
    reservationById.setReservationPackageList(mockReservationPackageList());
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(reservationById);
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), true, true, false);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertNotNull(
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight());
    assertEquals("2023-07-28",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getStartDate());
    assertEquals(BigDecimal.valueOf(999),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getPricePerNight());
    assertEquals(BigDecimal.valueOf(832.5),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getGrossPricePerNight());
    assertEquals(BigDecimal.valueOf(166.5),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getVatRate());
    assertEquals(BigDecimal.valueOf(59.94),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getCityTaxPerNight());
    assertEquals(BigDecimal.valueOf(56.02),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getCityTaxAmountBeforeTax());
    assertEquals(BigDecimal.valueOf(3.92),
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getRatesPerNight().get(0).getCityTaxVat());
    IntStream.range(1, 3).forEach(num -> {
      assertEquals(String.valueOf(LocalDate.now()),
          reservationByBasketRefResponse.getReservationByIdList().
              get(0).getReservationPackageList().get(num).getStartDate());
      assertEquals(String.valueOf(LocalDate.now().plusDays(num + 3)),
          reservationByBasketRefResponse.getReservationByIdList().
              get(0).getReservationPackageList().get(num).getEndDate());
    });

  }

  @Test
  void getReservationById__ReservationNotOverridden_ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(
        mockReservationByIdWithoutOverride());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0)
        .getReservationOverrideReasons());
    assertFalse(
        reservationByBasketRefResponse.getReservationByIdList().get(0).isReservationOverridden());
  }

  @Test
  void getReservationById__ReservationCancelled_ShouldReturnOk() throws IOException {
    //Arrange
    ReservationById reservationById = mockReservationByIdWithoutOverride();
    reservationById.setReservationStatus("Cancelled");
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(reservationById);
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CANCELLED_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertEquals(BigDecimal.valueOf(100), reservationByBasketRefResponse.getPreviousTotal());
    assertEquals(BigDecimal.valueOf(-100), reservationByBasketRefResponse.getBalanceOutstanding());
    assertEquals(new BigDecimal(50), reservationByBasketRefResponse.getNewTotal());
  }

  @Test
  void getReservationsByIds__InvalidAdditionalDetails() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, false, false);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    var reservation = reservationByBasketRefResponse.getReservationByIdList().get(0);
    assertNotNull(reservation);
    var guestList = reservation.getReservationGuestList();
    assertNotNull(guestList);
    var guest = guestList.get(0);
    assertNotNull(guest);
    var additionalDetails = guest.getAdditionalDetails();
    assertNotNull(additionalDetails);
    assertNotNull(additionalDetails.getDob());
    assertNotNull(additionalDetails.getNationality());
    assertNotNull(additionalDetails.getPassportNumber());

    assertTrue(additionalDetails.getDob().isBefore(LocalDate.now()));
    assertTrue(additionalDetails.getNationality().length() <= 20);
    assertTrue(additionalDetails.getPassportNumber().matches("^[a-zA-Z0-9]+$"));
  }

  @Test
  void getReservationById__operaUiRsv_DRV_ShouldReturnOk() throws IOException {

    var reservation = mockReservationWithInstructionsAndDf();
    var reservationById = mockReservationById();
    reservationById.setGuarantee(Guarantee.builder().guaranteeCode("DRV").build());
    reservation.getReservations().getReservation().get(0).getReservationIdList().get(0).setId("12345");
    ConfirmReservationRequest confirmReservationRequest = ConfirmReservationRequest.builder()
            .reservationId("12345")
            .hotelId("TestHotelId")
            .paymentOption(PaymentOption.PAY_NOW)
            .build();
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
            Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
            mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
            Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
            anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
            anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
            .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
            any(), any())).thenReturn(reservationById);
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
            mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockBusinessAllowance());
    when(depositFoliosRequestMapper.toDepositFolioRequestModel(confirmReservationRequest))
            .thenReturn(DepositFolioRequest.builder()
                    .reservationId("12345")
                    .hotelId("TestHotelId")
                    .paymentOption(PaymentOption.PAY_NOW).build());

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
            Collections.singleton("TestReservationId"), false, true, true);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getDepositFoliosResponse());
    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getBookingAllowancesResponse());

  }

  @Test
  void getReservationById__operaUiRsv_CC_ShouldReturnOk() throws IOException {

    var reservation = mockReservationWithInstructionsAndDf();
    var reservationById = mockReservationById();
    reservationById.setGuarantee(Guarantee.builder().guaranteeCode("CC").build());
    reservation.getReservations().getReservation().get(0).getReservationIdList().get(0).setId("12345");

    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
            Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
            mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
            Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
            anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
            anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
            .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
            any(), any())).thenReturn(reservationById);
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
            mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockBusinessAllowance());

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
            Collections.singleton("TestReservationId"), false, true, true);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getDepositFoliosResponse());
    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getBookingAllowancesResponse());

  }

  @Test
  void getReservationById__homeAddressType_ShouldReturnOk() throws IOException {
    var reservation = mockReservation();
    var reservationById = mockReservationById();
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
            Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
            mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
            Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
            anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
            anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
            .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
            any(), any())).thenReturn(reservationById);
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
            mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
            Collections.singleton("TestReservationId"), false, true, true);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getReservationGuestList().get(0).getHomeAddress());
    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getReservationGuestList().get(0).getHomeAddress().getAddressLine1());
  }

  @Test
  void getReservationById__homeAddressType_Address_as_null() throws IOException {
    var reservation = mockReservation();
    reservation.getReservations().getReservation().get(0).getReservationGuests().get(0).getProfileInfo().setProfile(null);
    var reservationById = mockReservationById();
    reservationById.getReservationGuestList().get(0).setHomeAddress(null);

    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
            Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
            mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
            Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
            anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
            anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
            .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
            any(), any())).thenReturn(reservationById);
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
            mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
            Collections.singleton("TestReservationId"), false, true, true);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertNull(reservationByBasketRefResponse.getReservationByIdList().get(0)
        .getReservationGuestList().get(0).getHomeAddress());
  }

  @Test
  void saveReservation__Success() throws IOException {
    //Arrange
    var savePackages = createSavePackagesRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(hotelReservationOutPort.getReservationsPackagesByIds(anyString(), anySet()))
        .thenReturn(mockReservationsPackagesResponse());
    when(ohipPackagesClient.getPackages(any(), eq(new LinkedMultiValueMap<>()))).thenReturn(
        getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());
    when(reservationPackagesOhipMapper.toModel(any(), anyInt(), any())).thenReturn(
        mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.updateReservationPackages(createSavePackagesRequest());

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateReservationPackages(savePackages));
  }

  @Test
  void saveReservation__Success2() throws IOException {
    //Arrange
    var savePackages = createSavePackagesRequest();
    var previousRoomsSelections = new ArrayList<RoomsSelections>();
    var packagesSelection = new ArrayList<PackagesSelection>();

    previousRoomsSelections.add(new RoomsSelections(packagesSelection));
    packagesSelection.add(new PackagesSelection("1", 2));
    savePackages.setPreviousRoomsSelections(previousRoomsSelections);

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(hotelReservationOutPort.getReservationsPackagesByIds(anyString(), anySet()))
        .thenReturn(mockReservationsPackagesResponse());
    when(ohipPackagesClient.getPackages(any(), eq(new LinkedMultiValueMap<>()))).thenReturn(
        getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());
    when(reservationPackagesOhipMapper.toModel(any(), anyInt(), any())).thenReturn(
        mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any())).thenReturn(
        Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.updateReservationPackages(createSavePackagesRequest());

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateReservationPackages(savePackages));
  }

  @Test
  void saveReservationWithNewBookingFeePackage__Success() throws IOException {
    //Arrange
    var savePackages = mockReservationPackagesBookingFeeRequest();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(true);
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(hotelReservationOutPort.getReservationsPackagesByIds(anyString(), anySet()))
        .thenReturn(mockReservationsPackagesResponse());
    when(ohipPackagesClient.getPackages(any(), eq(new LinkedMultiValueMap<>()))).thenReturn(
        getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());
    when(reservationPackagesOhipMapper.toModel(any(), anyInt(), any())).thenReturn(
        mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateReservationPackages(savePackages));
  }

  @Test
  void getReservationsPackagesByIds__shouldThrowException() {
    //Arrange
    String error = "An error was returned by OHIP: Could not fetch packages from Opera reservations.";
    Mockito.when(ohipReservationClient.getReservations(any(), anySet()))
        .thenThrow(new HotelReservationException(OHIP_GET_RESERVATION_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getReservationsPackagesByIds(HOTEL_ID,
                new HashSet<>(List.of(EXTERNAL_REFERENCE_ID)))
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void updateReservationPackages__shouldThrowException() {
    //Arrange
    String error = "An error was returned by OHIP!";
    var savePackages = createSavePackagesRequest();

    when(ohipReservationClient.getReservations(any(), any())).thenThrow(
        new HotelReservationException(
            OHIP_GET_PACKAGES_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateReservationPackages(savePackages)
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void addReservationPackages__shouldThrowException() {
    //Arrange
    String error = "Could not save selected packages";
    var savePackages = createSavePackagesRequest();

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(reservationResponseOhipMapper.toReservationPackagesResponseModel(
        any(), eq(false))).thenReturn(mockReservationsPackagesResponse());
    when(packagesRequestDtoOhipMapper.toDto(any())).thenReturn(new PackagesRequestOhipDto());
    when(ohipPackagesClient.getPackages(any(), any())).thenReturn(
        Mono.just(PackagesResponseOhipDto.builder()
            .build()));
    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateReservationPackages(savePackages)
        );

    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void createHotelReservation_ValidReservationRequest_ShouldReturnOK() {
    //Arrange
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    var validHotelReservationRequest = createValidHotelReservationRequest(true);
    when(ohipReservationClient.sendCreateReservationRequest(any(), any())).thenReturn(
        Mono.just(reservationStatusOhipDto()));
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(reservationResponseOhipMapper.toReservationResponseModel(any())).thenReturn(
        mockReservationResponse());
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(true);
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any())).thenReturn(
        mockChangeReservationResponse());

    //Act
    var reservationResponse =
        hotelReservationOutPort.createReservation(validHotelReservationRequest);
    //Assert
    assertThat(reservationResponse, notNullValue());
    assertThat(reservationResponse.getReservations().get(0).getReservationId(), is("1234"));
  }

  @Test
  void createHotelReservation_ValidReservationRequest_WithEmptyPackages_ShouldReturnOK() {
    //Arrange
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    var validHotelReservationRequest = createValidHotelReservationRequestWithEmptyPackages(true);
    when(ohipReservationClient.sendCreateReservationRequest(any(), any())).thenReturn(
        Mono.just(reservationStatusOhipDto()));
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(reservationResponseOhipMapper.toReservationResponseModel(any())).thenReturn(
        mockReservationResponse());
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(true);
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any())).thenReturn(
        mockChangeReservationResponse());
    when(reservationRequestOhipMapper.toCreateReservationModel(any())).thenReturn(
        mockCreateReservationModel(validHotelReservationRequest));
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());

    //Act
    var reservationResponse =
        hotelReservationOutPort.createReservation(validHotelReservationRequest);
    //Assert
    assertThat(reservationResponse, notNullValue());
    assertThat(reservationResponse.getReservations().get(0).getReservationId(), is("1234"));
  }

  @Test
  void createHotelReservation_ValidReservationRequest_WithPackageGroup_ShouldReturnOK() {
    //Arrange
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    var validHotelReservationRequest = createValidHotelReservationRequestWithPackages();
    when(ohipReservationClient.sendCreateReservationRequest(any(), any())).thenReturn(
        Mono.just(reservationStatusOhipDto()));
    var reservation = mockReservation();
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));
    when(reservationResponseOhipMapper.toReservationResponseModel(any())).thenReturn(
        mockReservationResponse());
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(true);
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(
        mockChangeReservationResponse());
    when(reservationRequestOhipMapper.toCreateReservationModel(any())).thenReturn(
        mockCreateReservationModel(validHotelReservationRequest));
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockMdpPackageGroupsFromOhip());

    //Act
    var reservationResponse =
        hotelReservationOutPort.createReservation(validHotelReservationRequest);
    //Assert
    assertThat(reservationResponse, notNullValue());
    assertThat(reservationResponse.getReservations().get(0).getReservationId(), is("1234"));
  }

  @Test
  void createHotelReservationUserDefinedFields_ValidReservationRequest_ShouldReturnOK() {
    //Arrange
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    var validHotelReservationRequest = createValidHotelReservationRequestForIATA(true);

    when(ohipReservationClient.sendCreateReservationRequest(any(), any())).thenReturn(
        Mono.just(reservationStatusOhipDto()));
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(reservationResponseOhipMapper.toReservationResponseModel(any())).thenReturn(
        mockReservationResponse());
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(reservationRequestOhipMapper.toCreateReservationModel(any())).thenReturn(
        getCreateReservation());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(true);
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any())).thenReturn(
        mockChangeReservationResponse());

    //Act
    var reservationResponse =
        hotelReservationOutPort.createReservation(validHotelReservationRequest);
    //Assert
    assertThat(reservationResponse, notNullValue());
    assertThat(reservationResponse.getReservations().get(0).getReservationId(), is("1234"));
  }

  private CreateReservation getCreateReservation() {
    CreateReservation createReservation = new CreateReservation();

    NumericUDFType numericUDFType = new NumericUDFType();
    numericUDFType.setName("UDFN01");
    numericUDFType.setValue(BigDecimal.valueOf(Integer.parseInt("00012345")));

    UserDefinedFieldsType userDefinedFieldsType = new UserDefinedFieldsType();
    userDefinedFieldsType.addNumericUDFsItem(numericUDFType);

    HotelReservationType hotelReservationType = new HotelReservationType();
    hotelReservationType.setUserDefinedFields(userDefinedFieldsType);

    HotelReservationsType hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.addReservationItem(hotelReservationType);

    createReservation.setReservations(hotelReservationsType);

    return createReservation;
  }

  @Test
  void createHotelReservation__shouldThrowException() {
    //Arrange
    String error = "An error was returned by OHIP!";
    var validHotelReservationRequest = createValidHotelReservationRequest(true);
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCreateReservationWithSoftBundles())).thenReturn(
        false);
    Mockito.when(ohipReservationClient.sendCreateReservationRequest(any(), any()))
        .thenThrow(
            new HotelReservationException(ErrorCode.OHIP_CREATE_RESERVATION_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.createReservation(validHotelReservationRequest)
        );
    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }

  @Test
  void createHotelReservationWithCot_ValidReservationRequest_ShouldReturnOK() {
    var COT_CODE = "COT";
    //Arrange
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    var validHotelReservationRequest = createValidHotelReservationRequest(true);
    validHotelReservationRequest.getReservations().get(0).setCotRequired(true);

    when(apiLimitsService.getItemInventoryResponses(any())).thenReturn(mockItemInventoryResponse(1));
    ArgumentCaptor<String> hotelIdCaptor = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<CreateReservation> createReservationCaptor =
        ArgumentCaptor.forClass(CreateReservation.class);
    when(ohipReservationClient
        .sendCreateReservationRequest(hotelIdCaptor.capture(), createReservationCaptor.capture()))
        .thenReturn(Mono.just(reservationStatusOhipDto()));
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(reservationRequestOhipMapper.toCreateReservationModel(any())).thenReturn(
        mockCreateReservationModel(validHotelReservationRequest));
    when(apiLimitsService.getItemInventoryResponses(any())).thenReturn(mockItemInventoryResponse(1));
    when(reservationResponseOhipMapper.toReservationResponseModel(any())).thenReturn(
        mockReservationResponse());
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(true);
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
        .thenReturn(mockChangeReservationResponse());

    //Act
    var reservationResponse =
        hotelReservationOutPort.createReservation(validHotelReservationRequest);
    //Assert
    var requestReservation = validHotelReservationRequest.getReservations().get(0);
    assertThat(hotelIdCaptor.getValue(), is(requestReservation.getHotelId()));
    var inventoryItems = createReservationCaptor.getValue().getReservations().getReservation()
        .get(0).getInventoryItems();
    assertThat(inventoryItems.getItemClassCode(), is(COT_CODE));
    var inventoryItem = inventoryItems.getItem().get(0);
    assertThat(inventoryItem.getType(), is(COT_CODE));
    assertThat(inventoryItem.getQuantity(), is(1));
    assertThat(inventoryItem.getTimeSpan().getStart(),
        is(LocalDate.parse(requestReservation.getArrival())));
    assertThat(inventoryItem.getTimeSpan().getEnd(),
        is(LocalDate.parse(requestReservation.getDeparture())));
    var itemInfoType = inventoryItem.getItem();
    assertThat(itemInfoType.getCode(), is(COT_CODE));
    assertThat(itemInfoType.getQuantity(), is(1));
    assertThat(itemInfoType.getTimeSpan().getStartDate(),
        is(LocalDate.parse(requestReservation.getArrival())));
    assertThat(itemInfoType.getTimeSpan().getEndDate(),
        is(LocalDate.parse(requestReservation.getDeparture())));

    assertThat(reservationResponse, notNullValue());
    assertThat(reservationResponse.getReservations().get(0).getReservationId(), is("1234"));
  }

  private CreateReservation mockCreateReservationModel(ReservationRequest validHotelReservationRequest) {
    CreateReservation createReservation = new CreateReservation();
    HotelReservationsType hotelReservationsType = new HotelReservationsType();
    HotelReservationType hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId(validHotelReservationRequest.getReservations().get(0).getHotelId());
    RoomStayType roomStay = new RoomStayType();
    var reservation = validHotelReservationRequest.getReservations().get(0);
    roomStay.setArrivalDate(LocalDate.parse(reservation.getArrival()));
    roomStay.setDepartureDate(LocalDate.parse(reservation.getDeparture()));
    RoomRateType roomRateType = new RoomRateType();
    roomRateType.setRatePlanCode(reservation.getRoomRates().getRatePlanCode());
    roomStay.setRoomRates(List.of(roomRateType));
    hotelReservationType.setRoomStay(roomStay);
    hotelReservationType.setReservationPackages(new ArrayList<>(
        Collections.singletonList(new ReservationPackageType().packageCode(""))));
    hotelReservationsType.addReservationItem(hotelReservationType);
    createReservation.setReservations(hotelReservationsType);
    return createReservation;
  }

  @Test
  void createHotelReservationWithUnavailableCot_ValidReservationRequest_ShouldThrow() {
    //Arrange
    var validHotelReservationRequest = createValidHotelReservationRequest(true);
    validHotelReservationRequest.getReservations().get(0).setCotRequired(true);
    when(apiLimitsService.getItemInventoryResponses(any())).thenReturn(mockItemInventoryResponse(0));
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    //Act
    assertThrows(HotelReservationException.class, () ->
        hotelReservationOutPort.createReservation(validHotelReservationRequest));
  }

  @Test
  void createHotelReservationFlux__ShouldReturnOK() {
    //Arrange
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    var validHotelReservationRequest = createValidHotelReservationRequest(false);
    validHotelReservationRequest.getReservations().get(0).setCotRequired(true);

    when(apiLimitsService.getItemInventoryResponses(any())).thenReturn(mockItemInventoryResponse(1));
    when(ohipReservationClient.sendCreateReservationRequest(any(), any())).thenReturn(
        Mono.just(reservationStatusOhipDto()));
    when(reservationResponseOhipMapper.toReservationResponseLightModel(any(),
        anyString())).thenReturn(
        mockReservationResponse());
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(true);
    when(changeReservationRequestOhipMapper.toChangeReservationDto(anyString(), any(), anyString())).thenReturn(
        mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any())).thenReturn(
        mockChangeReservationResponse());

    //Act
    var reservationResponse =
        hotelReservationOutPort.createReservation(validHotelReservationRequest);
    //Assert
    assertThat(reservationResponse, notNullValue());
    assertThat(reservationResponse.getReservations().get(0).getReservationId(), is("1234"));
  }

  @ParameterizedTest
  @EnumSource(value = PaymentOption.class, names = {"PAY_NOW", "PAY_ON_ARRIVAL"})
  void confirmReservation_updateCcAgentId_success(PaymentOption paymentOption) throws Exception {

    ConfirmReservationRequest confirmReservationRequest =
        createConfirmReservationRequest(paymentOption);
    confirmReservationRequest.setCcAgentId("john.doe@wb.com");

    final var beforeUpdate = mockReservationWithPaymentCard();
    final var afterUpdate = mockReservationWithPaymentCard();
    afterUpdate.getReservations().getReservation().get(0).getReservationPolicies()
        .getDepositPolicies().get(0).setComments("Updated");

    if (paymentOption == PaymentOption.PAY_NOW) {
      when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
          Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(
              DepositFolioRequest.builder().hotelId("TEST").paymentOption(paymentOption)
                  .reservationId("12345").build());
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
          anyString())).thenReturn(mockRateInfoDetails());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(true);
    }

    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(beforeUpdate))
        .thenReturn(Mono.just(afterUpdate));
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithPaymentCard())));
    ChangeReservation changeReservation = mockChangeReservation();
    when(confirmReservationRequestOhipMapper.toChangeReservationModel(any())).thenReturn(
        changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest(
        confirmReservationRequest.getHotelId(),
        confirmReservationRequest.getReservationId(), changeReservation)).thenReturn(
        mockChangeReservationResponse());

    hotelReservationOutPort.confirmReservation(confirmReservationRequest);

    verify(hotelReservationOutPort, timeout(10000).times(1)).updateReservationCcAgentId(any(), anyBoolean());
  }

  @ParameterizedTest
  @EnumSource(value = PaymentOption.class, names = {"PAY_NOW", "PAY_ON_ARRIVAL"})
  void confirmReservationDepositFolioOnHoldFalse_updateCcAgentId_success(PaymentOption paymentOption) throws Exception {

    Reservation reservation = mockReservationWithPaymentCard();
    ConfirmReservationRequest confirmReservationRequest =
        createConfirmReservationRequest(paymentOption);
    confirmReservationRequest.setCcAgentId("john.doe@wb.com");

    if (paymentOption == PaymentOption.PAY_NOW) {
      when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
          Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(
              DepositFolioRequest.builder().hotelId("TEST").paymentOption(paymentOption)
                  .reservationId("12345").build());
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
          anyString())).thenReturn(mockRateInfoDetails());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);
    }

    when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
        Mono.just(reservation));
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithPaymentCard())));
    ChangeReservation changeReservation = mockChangeReservation();
    when(confirmReservationRequestOhipMapper.toChangeReservationModel(any())).thenReturn(
        changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest(
        confirmReservationRequest.getHotelId(),
        confirmReservationRequest.getReservationId(), changeReservation)).thenReturn(
        mockChangeReservationResponse());

    hotelReservationOutPort.confirmReservation(confirmReservationRequest);

    verify(hotelReservationOutPort, timeout(10000).times(1)).updateReservationCcAgentId(any(), anyBoolean());
  }

  @Test
  void changeReservation_ShouldReturnOk() {
    when(ohipReservationClient.getReservation(any(), any())).thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(),
        any(ChangeReservation.class)))
        .thenReturn(mockChangeReservationResponse());
    when(confirmReservationRequestOhipMapper.toChangeReservationModel(any())).thenReturn(
        new ChangeReservation());
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
        mockChangeReservationResponse().block().getReservations()))
        .thenReturn(OhipTestUtils.mockConfirmReservationResponse());
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithPaymentCard())));

    //Act
    var reservationsDetailsResponse = hotelReservationOutPort.confirmReservation(
        OhipTestUtils.createConfirmReservationRequest());

    //Assert
    assertNotNull(reservationsDetailsResponse);
    assertEquals(HOTEL_ID, reservationsDetailsResponse.getHotelId());
    assertEquals("Reserved", reservationsDetailsResponse.getReservationStatus());
  }

  @Test
  void changeCnpReservation_ShouldReturnOk() {
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getSetCnpBookingAlerts())).thenReturn(true);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(mockReservationWithFolioWindowRoutingInstruction(2));
    when(ohipReservationClient.getReservation(any(), any())).thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(),
        any(ChangeReservation.class)))
        .thenReturn(mockChangeReservationResponse());
    when(confirmReservationRequestOhipMapper.toChangeReservationModel(any())).thenReturn(
        new ChangeReservation());
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
        mockChangeReservationResponse().block().getReservations()))
        .thenReturn(OhipTestUtils.mockConfirmReservationResponse());
    when(frontDeskClient.getCreditCardInfo(anyString(), anyString())).thenReturn(
        mockCreditCardInfo());
    when(movePaymentDetailsOhipMapper.toChangeReservationDto(any(), any())).thenReturn(
        mockChangeReservation());
    when(reservationAlertMapper.toChangeReservationAlertDto(anyString(), anyString(), anyList()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));

    //Act
    var reservationsDetailsResponse = hotelReservationOutPort.confirmReservation(
        OhipTestUtils.createConfirmReservationRequest());

    //Assert
    assertNotNull(reservationsDetailsResponse);
    assertEquals(HOTEL_ID, reservationsDetailsResponse.getHotelId());
    assertEquals("Reserved", reservationsDetailsResponse.getReservationStatus());
    verify(reservationAlertMapper).toChangeReservationAlertDto(anyString(), anyString(), anyList());
    verify(ohipReservationClient).sendPutReservationsGuestRequest(anyString(), anyString(), any());
  }

  @Test
  void changeCnpReservation_ShouldSetAlertWithCorrectDetails() {
    //Arrange
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getSetCnpBookingAlerts())).thenReturn(true);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(mockReservationWithFolioWindowRoutingInstruction(2));
    when(ohipReservationClient.getReservation(any(), any())).thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(),
        any(ChangeReservation.class)))
        .thenReturn(mockChangeReservationResponse());
    when(confirmReservationRequestOhipMapper.toChangeReservationModel(any())).thenReturn(
        new ChangeReservation());
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
        mockChangeReservationResponse().block().getReservations()))
        .thenReturn(OhipTestUtils.mockConfirmReservationResponse());
    when(frontDeskClient.getCreditCardInfo(anyString(), anyString())).thenReturn(
        mockCreditCardInfo());
    when(movePaymentDetailsOhipMapper.toChangeReservationDto(any(), any())).thenReturn(
        mockChangeReservation());
    var alertCaptor = ArgumentCaptor.forClass(List.class);
    when(reservationAlertMapper.toChangeReservationAlertDto(anyString(), anyString(), alertCaptor.capture()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));

    //Act
    hotelReservationOutPort.confirmReservation(OhipTestUtils.createConfirmReservationRequest());

    //Assert
    var alerts = (List<Alert>) alertCaptor.getValue();
    assertEquals(1, alerts.size());
    var alert = alerts.get(0);
    assertEquals("ECNP", alert.getCode());
    assertEquals(AlertAreaType.CHECKIN.name(), alert.getArea());
    assertTrue(alert.isScreenNotification());
    assertFalse(alert.isPrinterNotification());
    assertTrue(alert.getDescription().contains("Payment Check Required"));
    assertTrue(alert.getDescription().contains("\n"));
  }

  @Test
  void changeCnpReservation_ShouldNotSetAlert_WhenFeatureFlagIsOff() {
    //Arrange
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getSetCnpBookingAlerts())).thenReturn(false);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(mockReservationWithFolioWindowRoutingInstruction(2));
    when(ohipReservationClient.getReservation(any(), any())).thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(),
        any(ChangeReservation.class)))
        .thenReturn(mockChangeReservationResponse());
    when(confirmReservationRequestOhipMapper.toChangeReservationModel(any())).thenReturn(
        new ChangeReservation());
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
        mockChangeReservationResponse().block().getReservations()))
        .thenReturn(OhipTestUtils.mockConfirmReservationResponse());
    when(frontDeskClient.getCreditCardInfo(anyString(), anyString())).thenReturn(
        mockCreditCardInfo());
    when(movePaymentDetailsOhipMapper.toChangeReservationDto(any(), any())).thenReturn(
        mockChangeReservation());

    //Act
    var reservationsDetailsResponse = hotelReservationOutPort.confirmReservation(
        OhipTestUtils.createConfirmReservationRequest());

    //Assert
    assertNotNull(reservationsDetailsResponse);
    verify(reservationAlertMapper, never()).toChangeReservationAlertDto(anyString(), anyString(), anyList());
  }

  @Test
  void isCnpReservation_ShouldReturnFalse_WhenNoRoutingInstructions() {
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(mockReservationForGetPackages());
    //Act
    var response = hotelReservationOutPort.isCnpReservation("HOTELID", "123456");

    //Assert
    assertEquals(Boolean.FALSE, response);
  }

  @Test
  void isCnpReservation_ShouldReturnTrue_WhenRoutingInstructionOnWindow2_CnpBooking() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet()))
            .thenReturn(mockReservationWithFolioWindowRoutingInstruction(2));

    //Act
    var response = hotelReservationOutPort.isCnpReservation("HOTELID", "123456");

    //Assert
    assertEquals(Boolean.TRUE, response);
  }

  @Test
  void isCnpReservation_ShouldReturnFalse_WhenRoutingInstructionOnWindow1_CpBooking() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet()))
            .thenReturn(mockReservationWithFolioWindowRoutingInstruction(1));

    //Act
    var response = hotelReservationOutPort.isCnpReservation("HOTELID", "123456");

    //Assert
    assertEquals(Boolean.FALSE, response);
  }

  @Test
  void isCnpReservation_ShouldReturnFalse_WhenRoutingInstructionHasNoFolio() {
    //Arrange
    var routingInfo = new RoutingInfoType();
    // folio is intentionally null — edge case
    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setRoutingInstructions(List.of(routingInfo));
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
            .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));

    //Act
    var response = hotelReservationOutPort.isCnpReservation("HOTELID", "123456");

    //Assert
    assertEquals(Boolean.FALSE, response);
  }

  @Test
  void getReservationsPackagesByIds_shouldFilterOutPackagesWithoutScheduledQuantity() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(mockReservationForGetPackages());
    when(reservationResponseOhipMapper.toReservationPackagesResponseModel(anyList(), eq(false)))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toRoomsSelectionsModel(anyList(), eq(false)))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toPackagesSelectionsModel(any()))
        .thenCallRealMethod();

    //Act
    var reservationPackages = hotelReservationOutPort.getReservationsPackagesByIds("TestHotelId",
        Collections.singleton("TestReservationId"));

    //Assert
    assertNotNull(reservationPackages);
    assertNotNull(reservationPackages.getRoomsSelections());
    var roomResponse = reservationPackages.getRoomsSelections().stream()
        .findFirst()
        .orElseThrow(() -> new AssertionError("Room not found"));
    assertEquals(1, roomResponse.getPackagesSelection().size());
    var packageResponse = roomResponse.getPackagesSelection().get(0);
    assertEquals("PIBBEV", packageResponse.getId());
    assertEquals("MDP",packageResponse.getPackageGroup());
    assertEquals(RATE_PLAN_CODE, reservationPackages.getRatePlanCode());
  }

  @Test
  void getReservationsPackagesByIds_shouldNotFilterOutPackagesWithoutScheduledQuantity() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(mockReservationForGetPackages());
    when(reservationResponseOhipMapper.toReservationPackagesResponseModel(
        mockReservationForGetPackages().collectList().block(), true))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toRoomsSelectionsModel(
        mockReservationForGetPackages().collectList().block().get(0).getReservations()
            .getReservation().get(0).getReservationPackages(),
        true))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toPackagesSelectionsModel(any()))
        .thenCallRealMethod();

    //Act
    var reservationPackages = hotelReservationOutPort.getReservationsPackagesMealInclusiveRateByReservationsIds(
        "TestHotelId",
        Collections.singleton("TestReservationId"));

    //Assert
    assertNotNull(reservationPackages);
    assertNotNull(reservationPackages.getRoomsSelections());
    var roomResponse = reservationPackages.getRoomsSelections().stream()
        .findFirst()
        .orElseThrow(() -> new AssertionError("Room not found"));
    assertEquals(2, roomResponse.getPackagesSelection().size());
    assertEquals(RATE_PLAN_CODE, reservationPackages.getRatePlanCode());
  }

  @Test
  void cancelReservation_ShouldReturnOk() {
    when(ohipReservationClient.getReservations(any(), any()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel())));
    when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
        any(CancelReservation.class)))
        .thenReturn(mockCancelReservationResponse());
    when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
        any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
    when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
        any(), anyMap()))
        .thenReturn(OhipTestUtils.mockCancelReservationResponse());

    //Act
    var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
        OhipTestUtils.createCancelReservationRequest());

    //Assert
    assertNotNull(cancelReservationResponse);
    assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());
  }

  @Test
  void cancelReservation_ShouldReturnOk2() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);
      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);

      Map<String, DepositsResponse> refundedDeposits = new HashMap<>();
      refundedDeposits.put("123456", null);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel())));
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());
    }
  }

  @Test
  void cancelReservation_ShouldReturnOk3() {
    var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
    cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);

    when(ohipReservationClient.getReservations(any(), any()))
        .thenThrow(HotelReservationException.class);

    //Act & Assert
    assertThrows(HotelReservationException.class, () -> hotelReservationOutPort.cancelReservation(
        cancelReservationRequest));
  }

  @Test
  void cancelReservation_ShouldReturnOk4() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);
      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);
      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().get(0).getRoomStay()
          .getGuarantee()
          .setOnHold(false);

      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.valueOf(-10));

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      Map<String, DepositsResponse> refundedDeposits = new HashMap<>();
      refundedDeposits.put("123456", null);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(
              DepositFolioRequest.builder().hotelId("TEST").paymentOption(PaymentOption.PAY_NOW)
                  .reservationId("12345").build());
      when(
          depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
              any(), any(), any(), any()))
          .thenReturn(depositFolioCriteria);
      when(ohipReservationClient.sendDepositFoliosRequest(any(), any(), any())).thenReturn(
          new PostedDepositFolio());
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());
    }
  }

  @Test
  void cancelReservation_ShouldReturnOk_Migrated() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);
      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);
      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().get(0).getRoomStay()
          .getGuarantee()
          .setOnHold(false);
      var extReference = new ExternalReferenceType();
      extReference.setIdContext(EXTERNAL_REF_MIGRATION_ID_CONTEXT);
      extReference.setId("123");
      mockReservationForCancel.getReservations().getReservation().get(0)
          .setExternalReferences(List.of(extReference));

      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.valueOf(-10));

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      Map<String, DepositsResponse> refundedDeposits = new HashMap<>();
      refundedDeposits.put("123456", null);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.just(Tuples.of("123", PriceBreakdownDto.builder().summary(
              SummaryDto.builder()
                  .deposit(BigDecimal.valueOf(-50))
                  .gross(BigDecimal.valueOf(40))
                  .net(BigDecimal.valueOf(60))
                  .totalCostOfStay(BigDecimal.valueOf(50))
                  .outStandingCostOfStay(BigDecimal.ZERO)
                  .build()).build())));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(
              DepositFolioRequest.builder().hotelId("TEST").paymentOption(PaymentOption.PAY_NOW)
                  .reservationId("12345").build());
      when(
          depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
              any(), any(), any(), any()))
          .thenReturn(depositFolioCriteria);
      when(ohipReservationClient.sendDepositFoliosRequest(any(), any(), any())).thenReturn(
          new PostedDepositFolio());
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());
    }
  }

  @Test
  void cancelReservation_ShouldReturnOk_Migrated_NoDeposits() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);
      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);
      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().get(0).getRoomStay()
          .getGuarantee()
          .setOnHold(false);
      var extReference = new ExternalReferenceType();
      extReference.setIdContext(EXTERNAL_REF_MIGRATION_ID_CONTEXT);
      extReference.setId("123");
      mockReservationForCancel.getReservations().getReservation().get(0)
          .setExternalReferences(List.of(extReference));

      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.valueOf(-10));

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      Map<String, DepositsResponse> refundedDeposits = new HashMap<>();
      refundedDeposits.put("123456", null);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.just(Tuples.of("123", PriceBreakdownDto.builder().summary(
              SummaryDto.builder()
                  .deposit(BigDecimal.ZERO)
                  .gross(BigDecimal.valueOf(40))
                  .net(BigDecimal.valueOf(60))
                  .totalCostOfStay(BigDecimal.valueOf(50))
                  .outStandingCostOfStay(BigDecimal.ZERO)
                  .build()).build())));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(
              DepositFolioRequest.builder().hotelId("TEST").paymentOption(PaymentOption.PAY_NOW)
                  .reservationId("12345").build());
      when(
          depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
              any(), any(), any(), any()))
          .thenReturn(depositFolioCriteria);
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());

      // Capture the refundedDeposits map to verify it's empty
      ArgumentCaptor<Map<String, DepositsResponse>> refundedDepositsCaptor =
          ArgumentCaptor.forClass(Map.class);
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          anyList(), refundedDepositsCaptor.capture()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());
      verify(ohipReservationClient, never()).sendDepositFoliosRequest(any(), any(), any());

      // Verify depositsResponse was NOT added to refundedDeposits map (because deposit was zero)
      Map<String, DepositsResponse> capturedRefundedDeposits = refundedDepositsCaptor.getValue();
      assertNotNull(capturedRefundedDeposits, "refundedDeposits map should not be null");
      assertTrue(capturedRefundedDeposits.isEmpty(),
          "refundedDeposits map should be empty when deposit is zero");
    }
  }

  @Test
  void cancelReservation_whenDepositIsNegative_shouldAddDepositsResponseToRefundedDepositsMap() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);

      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);
      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().getFirst().getRoomStay()
          .getGuarantee()
          .setOnHold(false);

      var extReference = new ExternalReferenceType();
      extReference.setIdContext(EXTERNAL_REF_MIGRATION_ID_CONTEXT);
      extReference.setId("123");
      mockReservationForCancel.getReservations().getReservation().getFirst()
          .setExternalReferences(List.of(extReference));

      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.valueOf(-10));

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      // Create expected depositsResponse
      var expectedDepositsResponse = new DepositsResponse();
      expectedDepositsResponse.setDeposits(List.of(
          Deposits.builder().paymentReference("REFUND123").build()));

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.just(Tuples.of("123", PriceBreakdownDto.builder().summary(
              SummaryDto.builder()
                  .deposit(BigDecimal.valueOf(-50))  // Negative deposit triggers depositsResponse != null
                  .gross(BigDecimal.valueOf(40))
                  .net(BigDecimal.valueOf(60))
                  .totalCostOfStay(BigDecimal.valueOf(50))
                  .outStandingCostOfStay(BigDecimal.ZERO)
                  .build()).build())));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(
              DepositFolioRequest.builder().hotelId("TEST").paymentOption(PaymentOption.PAY_NOW)
                  .reservationId("123456").build());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
              any(), any(), any(), any()))
          .thenReturn(depositFolioCriteria);
      when(ohipReservationClient.sendDepositFoliosRequest(any(), any(), any()))
          .thenReturn(new PostedDepositFolio());
      when(postedDepositsResponseOhipMapper.toModel(any())).thenReturn(expectedDepositsResponse);
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());

      // Capture the refundedDeposits map passed to the mapper
      ArgumentCaptor<Map<String, DepositsResponse>> refundedDepositsCaptor =
          ArgumentCaptor.forClass(Map.class);
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          anyList(), refundedDepositsCaptor.capture()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());

      // Verify depositsResponse was added to refundedDeposits map
      verify(ohipReservationClient, times(1)).sendDepositFoliosRequest(any(), any(), any());
      verify(postedDepositsResponseOhipMapper, times(1)).toModel(any());

      Map<String, DepositsResponse> capturedRefundedDeposits = refundedDepositsCaptor.getValue();
      assertNotNull(capturedRefundedDeposits, "refundedDeposits map should not be null");
      assertFalse(capturedRefundedDeposits.isEmpty(), "refundedDeposits map should not be empty");
      assertTrue(capturedRefundedDeposits.containsKey("123456"),
          "refundedDeposits map should contain the reservation ID");
      assertEquals(expectedDepositsResponse, capturedRefundedDeposits.get("123456"),
          "refundedDeposits map should contain the correct DepositsResponse");
    }
  }

  @Test
  void cancelReservation_whenPaymentToggleEnabled_shouldUpdatePaymentMethod() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);

      Map<String, DepositsResponse> refundedDeposits = new HashMap<>();
      refundedDeposits.put("123456", null);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel())));
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());
      var changeRsvCaptor = ArgumentCaptor.forClass(ChangeReservation.class);

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());

      verify(ohipReservationClient, Mockito.timeout(1000)).sendChangeReservationRequest(
          any(String.class),
          any(String.class), changeRsvCaptor.capture());
      assertEquals("VA",
          changeRsvCaptor.getValue().getReservations().getFirst().getReservationPaymentMethods().getFirst()
              .getPaymentMethod());
    }
  }

  @ParameterizedTest
  @MethodSource("providePaymentOptionsForDepositProcessing")
  void cancelReservation_shouldProcessDepositsForPayNowAndPayOnArrival(
      PaymentOption paymentOption, boolean shouldProcessDeposits) {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);

      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(paymentOption);

      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          any(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      if (shouldProcessDeposits) {
        when(ohipReservationClient.getReservations(any(), any()))
            .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel())));
      }

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());

      if (shouldProcessDeposits) {
        verify(ohipReservationClient, atLeastOnce()).getReservations(any(), any());
      } else {
        verify(ohipReservationClient, never()).getReservations(any(), any());
      }
    }
  }

  private static Stream<Arguments> providePaymentOptionsForDepositProcessing() {
    return Stream.of(
        Arguments.of(PaymentOption.PAY_NOW, true),
        Arguments.of(PaymentOption.PAY_ON_ARRIVAL, true),
        Arguments.of(PaymentOption.RESERVE_WITHOUT_CARD, false),
        Arguments.of(PaymentOption.ACCOUNT_COMPANY, false)
    );
  }

  @Test
  void cancelReservation_withPayOnArrival_shouldProcessDepositsWhenNotOnHold() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);

      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_ON_ARRIVAL);
      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().get(0).getRoomStay()
          .getGuarantee()
          .setOnHold(false);

      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.valueOf(-10));

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      Map<String, DepositsResponse> refundedDeposits = new HashMap<>();
      refundedDeposits.put("123456", null);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(
              DepositFolioRequest.builder().hotelId("TEST").paymentOption(PaymentOption.PAY_ON_ARRIVAL)
                  .reservationId("12345").build());
      when(
          depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
              any(), any(), any(), any()))
          .thenReturn(depositFolioCriteria);
      when(ohipReservationClient.sendDepositFoliosRequest(any(), any(), any())).thenReturn(
          new PostedDepositFolio());
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().getFirst());
      verify(ohipReservationClient, atLeastOnce()).getReservations(any(), any());
      verify(depositFoliosRequestMapper, atLeastOnce()).toDepositFolioRequestModel(any(), any());
    }
  }

  @Test
  void cancelReservation_withPayOnArrival_shouldSendDepositFolioWhenZeroDepositButAciChargesExist()
      throws IOException {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);

      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_ON_ARRIVAL);

      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().get(0).getRoomStay()
          .getGuarantee().setOnHold(false);

      // Deposit amount is zero – triggers the else branch
      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.ZERO);

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(DepositFolioRequest.builder().hotelId("TEST")
              .paymentOption(PaymentOption.PAY_ON_ARRIVAL).reservationId("12345").build());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
          any(), any(), any(), any())).thenReturn(depositFolioCriteria);
      // ACI folio contains a non-zero charge (payment.amount = 100)
      when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString()))
          .thenReturn(mockReservationAciAmount(MOCK_GET_FOLIOS_WITH_ACI_RESPONSE_JSON));
      when(ohipReservationClient.sendDepositFoliosRequest(any(), any(), any()))
          .thenReturn(new PostedDepositFolio());
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      verify(ohipReservationClient, atLeastOnce()).getFoliosAciAmount(anyString(), anyString());
      verify(ohipReservationClient, atLeastOnce()).sendDepositFoliosRequest(any(), any(), any());
      // Verify the posting amount was overridden with the negated ACI amount
      assertEquals(BigDecimal.valueOf(-100),
          depositFolioCriteria.getCriteria().getPayments().get(0).getPostingAmount().getAmount());
    }
  }

  @Test
  void cancelReservation_withPayOnArrival_shouldNotSendDepositFolioWhenZeroDepositAndZeroAci() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);

      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_ON_ARRIVAL);

      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().get(0).getRoomStay()
          .getGuarantee().setOnHold(false);

      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.ZERO);

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(DepositFolioRequest.builder().hotelId("TEST")
              .paymentOption(PaymentOption.PAY_ON_ARRIVAL).reservationId("12345").build());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
          any(), any(), any(), any())).thenReturn(depositFolioCriteria);
      // ACI folio returns null – no charges found
      when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(null);
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      verify(ohipReservationClient, atLeastOnce()).getFoliosAciAmount(anyString(), anyString());
      verify(ohipReservationClient, never()).sendDepositFoliosRequest(any(), any(), any());
    }
  }

  @Test
  void cancelReservation_withPayNow_shouldNotInvokeAciPathWhenDepositIsZero() {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(false);

      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(PaymentOption.PAY_NOW);

      var mockReservationForCancel = mockReservationForCancel();
      mockReservationForCancel.getReservations().getReservation().get(0).getRoomStay()
          .getGuarantee().setOnHold(false);

      var currencyAmountType = new CurrencyAmountType();
      currencyAmountType.amount(BigDecimal.ZERO);

      var paymentCriteriaType = new PaymentCriteriaType();
      paymentCriteriaType.setPostingAmount(currencyAmountType);

      var depositFolioCriteriaType = new DepositFolioCriteriaType();
      depositFolioCriteriaType.setPayments(List.of(paymentCriteriaType));

      var depositFolioCriteria = new DepositFolioCriteria();
      depositFolioCriteria.setCriteria(depositFolioCriteriaType);

      when(ohipReservationClient.getReservations(any(), any()))
          .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel)));
      when(rulesAgentClient.getVatCodes(any(), any())).thenReturn(new VatRuleResponseDto());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any(), any()))
          .thenReturn(DepositFolioRequest.builder().hotelId("TEST")
              .paymentOption(PaymentOption.PAY_NOW).reservationId("12345").build());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(any(DepositFolioRequest.class),
          any(), any(), any(), any())).thenReturn(depositFolioCriteria);
      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(anyList(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      // ACI path is guarded by PAY_ON_ARRIVAL – must not be triggered for PAY_NOW
      verify(ohipReservationClient, never()).getFoliosAciAmount(anyString(), anyString());
      verify(ohipReservationClient, never()).sendDepositFoliosRequest(any(), any(), any());
    }
  }

  @ParameterizedTest
  @MethodSource("providePaymentOptionsForPaymentMethodUpdate")
  void cancelReservation_shouldUpdatePaymentMethodOnlyForPayNow(
      PaymentOption paymentOption, boolean shouldUpdatePaymentMethod) {
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);

      var cancelReservationRequest = OhipTestUtils.createCancelReservationRequest();
      cancelReservationRequest.setPaymentOption(paymentOption);

      // Only stub getReservations for payment options that trigger deposit processing
      if (PaymentOption.PAY_NOW.equals(paymentOption)
          || PaymentOption.PAY_ON_ARRIVAL.equals(paymentOption)) {
        when(ohipReservationClient.getReservations(any(), any()))
            .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationForCancel())));
      }

      when(ohipReservationClient.sendPostCancelReservationRequest(any(), any(),
          any(CancelReservation.class))).thenReturn(mockCancelReservationResponse());
      when(cancelReservationRequestOhipMapper.toCancelReservationModel(anyString(),
          any(CancelReservationRequest.class))).thenReturn(new CancelReservation());
      when(cancelReservationResponseOhipMapper.toCancelReservationResponseModel(
          any(), anyMap()))
          .thenReturn(OhipTestUtils.mockCancelReservationResponse());

      //Act
      var cancelReservationResponse = hotelReservationOutPort.cancelReservation(
          cancelReservationRequest);

      //Assert
      assertNotNull(cancelReservationResponse);
      assertEquals("9876", cancelReservationResponse.getCancellationIds().get(0));

      if (shouldUpdatePaymentMethod) {
        verify(ohipReservationClient, Mockito.timeout(1000)).sendChangeReservationRequest(
            any(String.class), any(String.class), any(ChangeReservation.class));
      } else {
        verify(ohipReservationClient, never()).sendChangeReservationRequest(
            any(String.class), any(String.class), any(ChangeReservation.class));
      }
    }
  }

  private static Stream<Arguments> providePaymentOptionsForPaymentMethodUpdate() {
    return Stream.of(
        Arguments.of(PaymentOption.PAY_NOW, true),
        Arguments.of(PaymentOption.PAY_ON_ARRIVAL, false),
        Arguments.of(PaymentOption.RESERVE_WITHOUT_CARD, false),
        Arguments.of(PaymentOption.ACCOUNT_COMPANY, false)
    );
  }

  @Test
  void getCancelPolicies__ShouldReturnOk() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationForCancel())));
    when(ohipReservationClient.getHotelConfig("TestHotelId")).thenReturn(
        mockHotelDetailsResponse());

    //Act
    var cancelInformation = hotelReservationOutPort.getCancelInformation("TestHotelId",
        Collections.singleton("12345"), "2022-09-15T07:47:19 00:00");

    //Assert
    assertNotNull(cancelInformation);
    assertEquals(true, cancelInformation.getIsCancellable());

  }

  @Test
  void hotelReservation_NullCancellationPolicies() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithNullCancellationPolicy())));
    when(ohipReservationClient.getHotelConfig("TestHotelId")).thenReturn(
        mockHotelDetailsResponse());

    //Act
    var cancelInformation = hotelReservationOutPort.getCancelInformation("TestHotelId",
        Collections.singleton("12345"), "2022-09-15T07:47:19 00:00");

    //Assert
    assertNotNull(cancelInformation);
    assertEquals(false, cancelInformation.getIsCancellable());

  }

  @Test
  void hotelReservation_EmptyCancellationPolicies() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithEmptyCancellationPolicy())));
    when(ohipReservationClient.getHotelConfig("TestHotelId")).thenReturn(
        mockHotelDetailsResponse());

    //Act
    var cancelInformation = hotelReservationOutPort.getCancelInformation("TestHotelId",
        Collections.singleton("12345"), "2022-09-15T07:47:19 00:00");

    //Assert
    assertNotNull(cancelInformation);
    assertEquals(false, cancelInformation.getIsCancellable());

  }

  @Test
  void getCancelPolicies__shouldThrowException() {
    //Arrange
    String error = "An error was returned by OHIP: Could not fetch reservation policies.";
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationForCancel())));
    when(ohipReservationClient.getHotelConfig("TestHotelId")).thenThrow(
        new HotelReservationException(
            OHIP_RETRIEVE_HOTEL_CONFIG_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getCancelInformation("TestHotelId",
                Collections.singleton("12345"), "2022-09-15T07:47:19 00:00")
        );
    // Assert
    Assertions.assertEquals(exception.getMessage(), error);

  }

  @Test
  void changeReservationRatePlan__Success() {
    //Arrange
    var ratePlanChangeRequest = mockRatePlanChangeRequest();
    var reservation = mockReservationRatePlane();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));
    when(apiLimitsService.getHotelAvailabilityResponses(any()))
        .thenReturn(mockHotelAvailabilityDetailsDto());
    when(roomTypeChangeRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any()))
        .thenReturn(null);
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeRateReservationResponse());

    //Act
    hotelReservationOutPort.changeReservationRatePlan(ratePlanChangeRequest);

    //Assert
    assertDoesNotThrow(
        () -> hotelReservationOutPort.changeReservationRatePlan(ratePlanChangeRequest));
  }

  @Test
  void changeReservationRatePlan__shouldThrowException__whenGettingReservation() {
    // Arrange
    String error = "An error was returned by OHIP!";
    var ratePlanChangeRequest = mockRatePlanChangeRequest();
    Mockito.when(ohipReservationClient.getReservations(any(), any()))
        .thenThrow(new HotelReservationException(DIGITAL_WRONG_RESERVATION_ID, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.changeReservationRatePlan(ratePlanChangeRequest)
        );

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void changeReservationRatePlan__shouldThrowException__whenGettingAvailability() {
    // Arrange
    String error = "An error was returned by OHIP!";
    var ratePlanChangeRequest = mockRatePlanChangeRequest();
    var reservation = mockReservationRatePlane();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));
    Mockito.when(apiLimitsService.getHotelAvailabilityResponses(any()))
        .thenThrow(new HotelAvailabilityException(DIGITAL_WRONG_OPERA_PRICE, error));

    //Act
    HotelAvailabilityException exception = Assertions
        .assertThrows(HotelAvailabilityException.class, () ->
            hotelReservationOutPort.changeReservationRatePlan(ratePlanChangeRequest)
        );

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void changeReservationRatePlan__shouldThrowException__whenUpdatingReservation() {
    // Arrange
    String error = "An error was returned by OHIP!";
    var ratePlanChangeRequest = mockRatePlanChangeRequest();
    var reservation = mockReservationRatePlane();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));
    when(apiLimitsService.getHotelAvailabilityResponses(any()))
        .thenReturn(mockHotelAvailabilityDetailsDto());
    when(roomTypeChangeRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any()))
        .thenReturn(null);
    Mockito.when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenThrow(new HotelReservationException(OHIP_CHANGE_RESERVATION_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.changeReservationRatePlan(ratePlanChangeRequest)
        );

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void changeReservationRoomType__Success() {
    //Arrange
    var roomTypeChangeRequest = mockRoomTypeChangeRequest();
    var reservation = mockReservationRatePlane();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));
    when(apiLimitsService.getHotelAvailabilityResponses(any()))
        .thenReturn(mockHotelAvailabilityDetailsDto());
    when(roomTypeChangeRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any()))
        .thenReturn(null);
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeRateReservationResponse());

    //Act
    hotelReservationOutPort.changeReservationRoomType(roomTypeChangeRequest);

    //Assert
    assertDoesNotThrow(
        () -> hotelReservationOutPort.changeReservationRoomType(roomTypeChangeRequest));
  }

  private HotelAvailabilityDetailsDto mockHotelAvailabilityDetailsDto() {
    var amountTypeDto = AmountTypeDto.builder()
        .start("2022-11-01")
        .end("2022-11-03")
        .base(TotalTypeDto.builder()
            .amountBeforeTax(BigDecimal.valueOf(100))
            .build())
        .build();
    var rates = RatesTypeDto.builder()
        .rate(List.of(amountTypeDto))
        .build();

    var flexRoomRateTypeDto = RoomRateTypeDto.builder()
        .roomType("VDOUBL")
        .ratePlanCode("FLEXRATE")
        .rates(rates)
        .start("2022-11-01")
        .end("2022-11-03")
        .build();

    var roomStaysDto = RoomStayTypeDto.builder()
        .roomRates(new ArrayList<>(List.of(flexRoomRateTypeDto)))
        .build();

    var hotelAvailabilityDto = HotelAvailabilityDto.builder()
        .hotelId("LONEUS")
        .ratePlanSet("PBF")
        .roomStays(List.of(roomStaysDto))
        .build();
    return HotelAvailabilityDetailsDto.builder()
        .hotelAvailability(List.of(hotelAvailabilityDto))
        .build();
  }

  @Test
  void changeReservationRoomType__shouldThrowExceptionWhenUpdatingReservation() {
    // Arrange
    String error = "An error was returned by OHIP!";
    var roomTypeChangeRequest = mockRoomTypeChangeRequest();
    var reservation = mockReservationRatePlane();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));
    when(apiLimitsService.getHotelAvailabilityResponses(any()))
        .thenReturn(mockHotelAvailabilityDetailsDto());
    Mockito.when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenThrow(new HotelReservationException(OHIP_CHANGE_RESERVATION_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.changeReservationRoomType(roomTypeChangeRequest)
        );

    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void changeReservationRoomType_shouldThrowExceptionWhenNoOperaReservationFound() {
    // Arrange
    String basketRef = "BASKET123";
    String expectedError = String.format(
        "Could not find the right Opera reservation id(s) from basket reference %s",
        basketRef
    );

    var roomTypeChangeRequest = mockRoomTypeChangeRequest();
    roomTypeChangeRequest.setBasketReferenceId(basketRef);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.empty());

    // Act
    HotelReservationException exception = Assertions.assertThrows(
        HotelReservationException.class,
        () -> hotelReservationOutPort.changeReservationRoomType(roomTypeChangeRequest)
    );

    // Assert
    Assertions.assertEquals(expectedError, exception.getMessage());
    verify(ohipReservationClient).getReservations(anyString(), anySet());
    verifyNoMoreInteractions(ohipReservationClient);
  }

  @Test
  void changeReservationRoomType_shouldThrowExceptionWhenNoPriceFound() {
    // Arrange
    var request = mockRoomTypeChangeRequest();
    String expectedError = String.format(
        "Could not find any Opera price to be able to change room type for hotel %s in period %s-%s",
        request.getHotelId(), request.getStartDate(), request.getEndDate());

    var reservation = mockReservationRatePlane();

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));

    var hotelAvailabilitiesWithEmptyRoomRates = HotelAvailabilityDetailsDto.builder()
        .hotelAvailability(Collections.singletonList(
            HotelAvailabilityDto.builder()
                .hotelId(request.getHotelId())
                .ratePlanSet("PBF")
                .roomStays(Collections.singletonList(
                    RoomStayTypeDto.builder()
                        .roomRates(Collections.emptyList())
                        .build()))
                .build()))
        .build();

    when(apiLimitsService.getHotelAvailabilityResponses(any()))
        .thenReturn(hotelAvailabilitiesWithEmptyRoomRates);

    // Act & Assert
    HotelAvailabilityException exception = Assertions.assertThrows(
        HotelAvailabilityException.class,
        () -> hotelReservationOutPort.changeReservationRoomType(request)
    );

    // Assert
    Assertions.assertEquals(expectedError, exception.getMessage());
    verify(ohipReservationClient).getReservations(anyString(), anySet());
    verify(apiLimitsService).getHotelAvailabilityResponses(any());
    verifyNoMoreInteractions(ohipReservationClient);
  }

  @Test
  void updateDiscount__Success() {
    //Arrange
    var updateDiscountRequest = mockUpdateDiscountRequest();
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());

    //Act
    hotelReservationOutPort.updateDiscount(updateDiscountRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateDiscount(updateDiscountRequest));
  }


  @Test
  void updateDiscountEmpty__Exception() {
    //Arrange
    var updateDiscountRequest = mockUpdateDiscountRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.EMPTY_LIST));

    //Act
    HotelReservationNotFound exception = Assertions
        .assertThrows(HotelReservationNotFound.class, () ->
            hotelReservationOutPort.updateDiscount(updateDiscountRequest)
        );
    // Assert
    Assertions.assertEquals("Could not find Opera reservation to apply discount",
        exception.getMessage());
  }

  @Test
  void updateDiscount__Exception() {
    //Arrange
    var updateDiscountRequest = mockUpdateDiscountRequest();
    updateDiscountRequest.setDiscountAmount(BigDecimal.valueOf(200));
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    //Act
    DiscountInvalidAmountException exception = Assertions
        .assertThrows(DiscountInvalidAmountException.class, () ->
            hotelReservationOutPort.updateDiscount(updateDiscountRequest)
        );
    // Assert
    Assertions.assertEquals("Discount amount is larger than base amount", exception.getMessage());
  }

  @Test
  void updateCompanyQuestionAndAnswerDetails__Success() {
    //Arrange
    var companyQuestionAndAnswerDetailsRequest = mockCompanyQuestionAndAnswerDetailsRequest();

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());

    when(companyQuestionAndAnswerRequestOhipMapper.toChangeReservationDto(any(), any(),
        any())).thenReturn(null);

    //Act
    hotelReservationOutPort.updateCompanyQuestionAndAnswerDetails(
        companyQuestionAndAnswerDetailsRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateCompanyQuestionAndAnswerDetails(
        companyQuestionAndAnswerDetailsRequest));
  }

  @Test
  void updateCompanyQuestionAndAnswerDetails__No_Reservation__Fail() {
    //Arrange
    var companyQuestionAndAnswerDetailsRequest = mockCompanyQuestionAndAnswerDetailsRequest();

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));

    // Act //Assert
    Assertions.assertDoesNotThrow(() -> hotelReservationOutPort
        .updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest));
  }

  @Test
  void updateCompanyQuestionAndAnswerDetails__Exception() {
    //Arrange

    var companyQuestionAndAnswerDetailsRequest = mockCompanyQuestionAndAnswerDetailsRequest();
    Reservation reservation = mockReservation();

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any())).thenThrow(
        new HotelReservationException(
            ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION, "err"));
    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort
                .updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest));
    // Assert
    Assertions.assertEquals("err", exception.getMessage());
  }

  @Test
  void updateCompanyQuestionAndAnswerDetails__No_QnA() {
    //Arrange
    CompanyQuestionAndAnswerDetailsRequest companyQuestionAndAnswerDetailsRequest =
        new CompanyQuestionAndAnswerDetailsRequest();

    companyQuestionAndAnswerDetailsRequest.setHotelId("LONEUS");

    when(ohipReservationClient.getReservations(anyString(), any()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    // Act //Assert
    Assertions.assertDoesNotThrow(() -> hotelReservationOutPort
        .updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest));
  }

  @Test
  void updateCompanyQuestionAndAnswerDetails__ExistingComments__Fail() {
    //Arrange
    var companyQuestionAndAnswerDetailsRequest = mockCompanyQuestionAndAnswerDetailsRequest();
    Reservation reservation = mockReservation();
    HotelReservationType hotelReservationType = reservation.getReservations().getReservation()
        .get(0);
    CommentInfoType commentInfoType = new CommentInfoType();
    CommentType commentType = new CommentType();
    commentType.setType(QuestionAndAnswerTypeEnum.PUR_ORD_QNA.name());
    commentInfoType.setComment(commentType);
    hotelReservationType.setComments(List.of(commentInfoType));
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(reservation)));

    // Act //Assert
    Assertions.assertDoesNotThrow(() -> hotelReservationOutPort
        .updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest));
  }

  @Test
  void updateCompanyQuestionAndAnswerDetails__Later_Request__Fail() {
    //Arrange
    var companyQuestionAndAnswerDetailsRequest = mockCompanyQuestionAndAnswerDetailsRequest();
    companyQuestionAndAnswerDetailsRequest.getCompanyQuestionAndAnswerDetails()
        .setCustomerReferenceQuestionAndAnswer(null);
    companyQuestionAndAnswerDetailsRequest.getCompanyQuestionAndAnswerDetails()
        .setUserDefinedQuestionAndAnswers(null);
    companyQuestionAndAnswerDetailsRequest.getCompanyQuestionAndAnswerDetails()
        .setPurchaseOrderQuestionAndAnswer(null);

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    // Act //Assert
    Assertions.assertDoesNotThrow(() -> hotelReservationOutPort
        .updateCompanyQuestionAndAnswerDetails(companyQuestionAndAnswerDetailsRequest));

  }

  @Test
  void updateBusinessItems_NoReservations_Exception() {
    //Arrange
    var businessItemRequest = mockBusinessItemRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.EMPTY_LIST));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateBusinessItems(businessItemRequest)
        );
    // Assert
    Assertions.assertEquals("Could not find Opera reservation to apply business items", exception.getMessage());
  }

  @Test
  void getBusinessItems_NoReservations_Exception() {
    //Arrange
    var businessItemRequest = mockBusinessItemRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
            .thenReturn(Flux.fromIterable(Collections.EMPTY_LIST));

    //Act
    HotelReservationException exception = Assertions
            .assertThrows(HotelReservationException.class, () ->
                    hotelReservationOutPort.getBookingAllowanceChangeReservation(businessItemRequest)
            );
    // Assert
    Assertions.assertEquals("Could not find Opera reservation to apply business items", exception.getMessage());
  }

  @Test
  void updateBusinessItems_NoBusinessItems_Exception() {
    //Arrange
    var error = "error";
    var businessItemRequest = mockBusinessItemRequest();
    businessItemRequest.setBusinessItems(null);
    businessItemRequest.setChannel("DISTR");
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any())).thenThrow(
        new HotelReservationException(ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION, error));
    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateBusinessItems(businessItemRequest)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void updateBusinessItems__Success() {
    //Arrange
    var businessItemRequest = mockBusinessItemRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());

    //Act
    hotelReservationOutPort.updateBusinessItems(businessItemRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));
  }

  @Test
  void updateBusinessItems_nullBusinessItems_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    //Act
    hotelReservationOutPort.updateBusinessItems(businessItemRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));
    verifyNoInteractions(rulesAgentClient);

  }

  @Test
  void updateBusinessItems_nullPackages_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemPrePaidRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationWithoutPackages())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockBusinessAllowance());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("1234"), Mono.just(mockPriceBreakdownDto())));
    //Act
    hotelReservationOutPort.updateBusinessItems(businessItemRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));

  }

  @Test
  void updateBusinessItems_nullPaymentCard_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemPrePaidRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationWithoutPaymentCard())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockBusinessAllowance());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("1234"), Mono.just(mockPriceBreakdownDto())));
    //Act
    hotelReservationOutPort.updateBusinessItems(businessItemRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));

  }

  @Test
  void updateBusinessItems_nullCardId_prepaidChannel_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemPrePaidRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationWithNullCardId())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockBusinessAllowance());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("1234"), Mono.just(mockPriceBreakdownDto())));
    
    //Act & Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));
  }

  @Test
  void getBusinessItems_nullBusinessItems_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
            .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    //Act
    List<ChangeReservation> bookingAllowanceChangeReservation = hotelReservationOutPort
            .getBookingAllowanceChangeReservation(businessItemRequest);

    //Assert
    assertNotNull(bookingAllowanceChangeReservation);

  }

  @Test
  void getBusinessItems_notNullBusinessItems_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemRequest();
    businessItemRequest.setChannel("BB");
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    //Act
    List<ChangeReservation> bookingAllowanceChangeReservation = hotelReservationOutPort
        .getBookingAllowanceChangeReservation(businessItemRequest);

    //Assert
    assertNotNull(bookingAllowanceChangeReservation);
  }

  @Test
  void getBusinessItems_prepaidBusinessItems_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemRequest();
    businessItemRequest.setChannel(OhipConstants.PREPAID_CHANNEL);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    //Act
    List<ChangeReservation> bookingAllowanceChangeReservation = hotelReservationOutPort
        .getBookingAllowanceChangeReservation(businessItemRequest);

    //Assert
    assertNotNull(bookingAllowanceChangeReservation);

  }

  @Test
  void getBusinessItems_noChannelBusinessItems_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemRequest();
    businessItemRequest.setChannel("PI");
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    //Act
    List<ChangeReservation> bookingAllowanceChangeReservation = hotelReservationOutPort
        .getBookingAllowanceChangeReservation(businessItemRequest);

    //Assert
    assertNull(bookingAllowanceChangeReservation);
  }

  @Test
  void getBusinessItems_emptyBookingAllowanceDetails_ThrowError() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemRequest();

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.empty());

    assertThrows(HotelReservationException.class, () -> hotelReservationOutPort
        .getBookingAllowanceChangeReservation(businessItemRequest));
  }

  @Test
  void getBusinessItems_differentSizeBookingAllowanceDetails_ThrowError() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemRequest();
    businessItemRequest.setReservationIds(Set.of("100100", "100101"));

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    assertThrows(HotelReservationException.class, () -> hotelReservationOutPort
        .getBookingAllowanceChangeReservation(businessItemRequest));
  }

  @Test
  void getSpecialRequestChangeReservation_withNullValue() {
    assertNull(hotelReservationOutPort.getSpecialRequestChangeReservation(null));
  }

  @Test
  void updateBusinessItems_prePaidChannel_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemPrePaidRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString()))
        .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockBusinessAllowance());

    //Act
    hotelReservationOutPort.updateBusinessItems(businessItemRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));
  }

  @Test
  void getBusinessItems_prePaidChannel_Success() {
    //Arrange
    var businessItemRequest = mockNullBusinessItemPrePaidRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
            .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));

    //Act
    hotelReservationOutPort.getBookingAllowanceChangeReservation(businessItemRequest);

    //Assert
    List<ChangeReservation> changeReservations = assertDoesNotThrow(() -> hotelReservationOutPort
            .getBookingAllowanceChangeReservation(businessItemRequest));

    assertNotNull(changeReservations);
  }

  @Test
  void updateSpecialRequests__Success() {
    var mockSpecialRequests = mockSpecialRequests();
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
        .thenReturn(mockChangeDiscountReservationResponse());

    when(specialReqRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any())).thenReturn(
        mockChangeReservation());
    when(specialReqRequestOhipMapper.toChangeReservationRemoveExstCommentDto(any(), any(),
        any())).thenReturn(
        mockChangeReservation());

    hotelReservationOutPort.updateSpecialRequests(mockSpecialRequests);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateSpecialRequests(mockSpecialRequests));
  }

  @Test
  void updateCustomReferenceNumber__Success() {
    var request = UpdateCustomReferenceNumberRequest.builder()
        .hotelId("FRESUD")
        .reservationIds(Set.of("12345"))
        .customReferenceNumber("AmendedReference")
        .build();

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(updateCustomReferenceNumberOhipMapper.toChangeReservationDto(any(), any(),
        any())).thenReturn(
        mockChangeReservation());

    hotelReservationOutPort.updateCustomReferenceNumber(request);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateCustomReferenceNumber(request));
  }

  @Test
  void updateCustomReferenceNumber__Exception() {
    var request = UpdateCustomReferenceNumberRequest.builder()
        .hotelId("FRESUD")
        .reservationIds(Set.of("12345"))
        .customReferenceNumber("AmendedReference")
        .build();

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
        .thenThrow(
            new HotelReservationException(ErrorCode.OHIP_CHANGE_RESERVATION_EXCEPTION, "error"));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateCustomReferenceNumber(request)
        );

    // Assert
    Assertions.assertEquals("error", exception.getMessage());
  }

  private SpecialRequests mockSpecialRequests() {
    return SpecialRequests.builder()
        .hotelId("MANOLD")
        .specialRequests(Collections.singletonList("SING"))
        .reservationIds(Collections.singletonList("12345"))
        .bookingNotes(Collections.singletonList("Wake up Call"))
        .build();
  }

  @Test
  void updateBusinessItems_WithoutPurchesOrder__Success() {
    //Arrange
    var businessItemRequest = mockBusinessItemRequestWithotPurchasOrder();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "100100", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());

    //Act
    hotelReservationOutPort.updateBusinessItems(businessItemRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));
  }

  @Test
  void getReservationsByExternalReferenceId__ShouldReturnOK() {
    //Arrange
    when(ohipReservationClient.sendGetReservationsByExternalReferenceId(EXTERNAL_REFERENCE_ID))
        .thenReturn(mockReservationsDetailsOhip());
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("1234"), Mono.just(mockPriceBreakdownDto())));
    when(reservationResponseOhipMapper.toReservationsDetailsResponseModel(
        mockReservationsDetailsOhip()))
        .thenReturn(
            OhipTestUtils.createReservationsDetailsResponse(HOTEL_ID, EXTERNAL_REFERENCE_ID));

    //Act
    var reservationDetailsEnhanced = hotelReservationOutPort
        .getReservationsByExternalRefId(EXTERNAL_REFERENCE_ID);

    //Assert
    assertNotNull(reservationDetailsEnhanced);
    assertEquals(EXTERNAL_REFERENCE_ID,
        reservationDetailsEnhanced.getReservationsDetailsResponse().getReservations()
            .getReservationInfo().get(0)
            .getExternalReferences().get(0).getId());
  }


  @Test
  void getReservationsByReservationId__ShouldReturnOK() {
    //Arrange
    when(ohipReservationClient.sendGetReservationsByReservationId(HOTEL_ID, RESERVATION_ID))
        .thenReturn(mockReservationIdDetailsOhip());
    lenient().when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    lenient().when(ohipReservationClient.getReservationAmounts(anyString(), anyString()))
        .thenReturn(
            Mono.zip(Mono.just("1234"), Mono.just(mockPriceBreakdownDto())));
    when(reservationResponseOhipMapper.toReservationIdDetailsResponseModel(
        mockReservationIdDetailsOhip()))
        .thenReturn(
            OhipTestUtils.createReservationIdDetailsResponse(HOTEL_ID, RESERVATION_ID));

    //Act
    var reservationDetailsEnhanced = hotelReservationOutPort
        .getReservationsByReservationId(HOTEL_ID, RESERVATION_ID);

    //Assert
    assertNotNull(reservationDetailsEnhanced);
    assertEquals(RESERVATION_ID,
        reservationDetailsEnhanced.getReservationIdResponse().getReservations()
            .getReservation().get(0)
            .getReservationIdList().get(0).getId());
  }

  @Test
  void confirmAmendSingleCall__Success() throws IOException {
    var businessItemRequest = mockBusinessItemRequest();
    Set<String> reservationIds = businessItemRequest.getReservationIds();
    SpecialRequests specialRequests = mockSpecialRequests();
    specialRequests.setReservationIds(new ArrayList<>(reservationIds));
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();
    updateReservationPackageRequest.setReservationsId(new ArrayList<>(reservationIds));
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();

    ChangeReservation changeReservation = mockChangeReservationWithComments();
    changeReservation.getReservations().get(0).setRoomStay(new RoomStayType());

    List<ChangeReservation> stayDateRequest = List.of(changeReservation);
    List<List<ChangeReservation>> editRoomRequest = List.of(stayDateRequest);

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(false);
    when(specialReqRequestOhipMapper.toChangeReservationDto(anyString(), anyString(), anyList(), anyList()))
            .thenReturn(changeReservation);

    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(businessItemsRequestMapper.toChangeReservationDto(any(), any(), anyList()))
            .thenReturn(changeReservation);
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
                    Collections.singletonList(mockProfile()))
            .thenReturn(Collections.singletonList(mockProfile()));

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(hotelReservationOutPort.getReservationsPackagesByIds(anyString(), anySet()))
            .thenReturn(mockReservationsPackagesResponse());
    when(ohipPackagesClient.getPackages(any(), eq(new LinkedMultiValueMap<>()))).thenReturn(
            getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());
    when(reservationPackagesOhipMapper.toModel(any(), anyInt(), any())).thenReturn(
            changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
            .thenReturn(mockChangeReservationResponse());

    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
            mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
            Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
            anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
            anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
            .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
            any(), any())).thenReturn(mockReservationById());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
            mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));
    doNothing().when(hotelReservationOutPort).updateBookerDetails(any());

    var response = hotelReservationOutPort.confirmAmendSingleCall(List.of(specialRequests),
            bookerDetailsCnpRequest, stayDateRequest, editRoomRequest,
            businessItemRequest, updateReservationPackageRequest, true);

    assertNotNull(response);
  }

  @Test
  void confirmAmendSingleCall_editRoomRequestEmpty_Success() throws IOException {
    var businessItemRequest = mockBusinessItemRequest();
    Set<String> reservationIds = businessItemRequest.getReservationIds();
    SpecialRequests specialRequests = mockSpecialRequests();
    specialRequests.setReservationIds(new ArrayList<>(reservationIds));
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();

    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();

    ChangeReservation changeReservation = mockChangeReservationWithComments();
    changeReservation.getReservations().get(0).setRoomStay(new RoomStayType());

    List<ChangeReservation> stayDateRequest = List.of(changeReservation);
    List<List<ChangeReservation>> editRoomRequest = new ArrayList<>();
    updateReservationPackageRequest.setReservationsId(changeReservation.getReservations().stream().map(item ->
        item.getReservationIdList().get(0).getId()).toList());

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(true);
    when(specialReqRequestOhipMapper.toChangeReservationDto(anyString(), anyString(), anyList(), anyList()))
        .thenReturn(changeReservation);

    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(businessItemsRequestMapper.toChangeReservationDto(any(), any(), anyList()))
        .thenReturn(changeReservation);
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()))
        .thenReturn(Collections.singletonList(mockProfile()));

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(hotelReservationOutPort.getReservationsPackagesByIds(anyString(), anySet()))
        .thenReturn(mockReservationsPackagesResponse());
    when(ohipPackagesClient.getPackages(any(), eq(new LinkedMultiValueMap<>()))).thenReturn(
        getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());
    when(reservationPackagesOhipMapper.toModel(any(), anyInt(), any())).thenReturn(
        changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));
    doNothing().when(hotelReservationOutPort).updateBookerDetails(any());

    var response = hotelReservationOutPort.confirmAmendSingleCall(List.of(specialRequests),
        bookerDetailsCnpRequest, stayDateRequest, editRoomRequest,
        businessItemRequest, updateReservationPackageRequest, true);

    assertNotNull(response);
  }

  @Test
  void confirmAmendSingleCall_editRoomRequestEmpty_andStayDateRequestNull_Success() throws IOException {
    var businessItemRequest = mockBusinessItemRequest();
    Set<String> reservationIds = businessItemRequest.getReservationIds();
    SpecialRequests specialRequests = mockSpecialRequests();
    specialRequests.setReservationIds(new ArrayList<>(reservationIds)); // Ensure mutable list
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();

    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();

    ChangeReservation changeReservation = mockChangeReservationWithComments();
    changeReservation.getReservations().get(0).setRoomStay(new RoomStayType());

    List<ChangeReservation> stayDateRequest = null;
    List<List<ChangeReservation>> editRoomRequest = new ArrayList<>(); // Ensure mutable list
    updateReservationPackageRequest.setReservationsId(
        new ArrayList<>(changeReservation.getReservations().stream()
            .map(item -> item.getReservationIdList().get(0).getId())
            .collect(Collectors.toList()))); // Ensure mutable list

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(true);
    when(specialReqRequestOhipMapper.toChangeReservationDto(anyString(), anyString(), anyList(), anyList()))
        .thenReturn(changeReservation);

    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(businessItemsRequestMapper.toChangeReservationDto(any(), any(), anyList()))
        .thenReturn(changeReservation);
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()))
        .thenReturn(Collections.singletonList(mockProfile()));

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(hotelReservationOutPort.getReservationsPackagesByIds(anyString(), anySet()))
        .thenReturn(mockReservationsPackagesResponse());
    when(ohipPackagesClient.getPackages(any(), eq(new LinkedMultiValueMap<>()))).thenReturn(
        getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());
    when(reservationPackagesOhipMapper.toModel(any(), anyInt(), any())).thenReturn(
        changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));
    doNothing().when(hotelReservationOutPort).updateBookerDetails(any());

    var response = hotelReservationOutPort.confirmAmendSingleCall(List.of(specialRequests),
        bookerDetailsCnpRequest, stayDateRequest, editRoomRequest,
        businessItemRequest, updateReservationPackageRequest, true);

    assertNotNull(response);
  }

  @Test
  void confirmAmendSingleCall_stayDateRequestNull_Success() throws IOException {
    var businessItemRequest = mockBusinessItemRequest();
    Set<String> reservationIds = businessItemRequest.getReservationIds();
    SpecialRequests specialRequests = mockSpecialRequests();
    specialRequests.setReservationIds(new ArrayList<>(reservationIds));
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();

    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();

    ChangeReservation changeReservation = mockChangeReservationWithComments();
    changeReservation.getReservations().get(0).setRoomStay(new RoomStayType());

    List<ChangeReservation> stayDateRequest = null;
    List<List<ChangeReservation>> editRoomRequest = List.of(List.of(changeReservation));
    updateReservationPackageRequest.setReservationsId(changeReservation.getReservations().stream().map(item ->
        item.getReservationIdList().get(0).getId()).toList());

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(true);
    when(specialReqRequestOhipMapper.toChangeReservationDto(anyString(), anyString(), anyList(), anyList()))
        .thenReturn(changeReservation);

    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(businessItemsRequestMapper.toChangeReservationDto(any(), any(), anyList()))
        .thenReturn(changeReservation);
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()))
        .thenReturn(Collections.singletonList(mockProfile()));

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(hotelReservationOutPort.getReservationsPackagesByIds(anyString(), anySet()))
        .thenReturn(mockReservationsPackagesResponse());
    when(ohipPackagesClient.getPackages(any(), eq(new LinkedMultiValueMap<>()))).thenReturn(
        getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(any())).thenReturn(mockPackageGroupsFromOhip());
    when(reservationPackagesOhipMapper.toModel(any(), anyInt(), any())).thenReturn(
        changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));
    doNothing().when(hotelReservationOutPort).updateBookerDetails(any());

    var response = hotelReservationOutPort.confirmAmendSingleCall(List.of(specialRequests),
        bookerDetailsCnpRequest, stayDateRequest, editRoomRequest,
        businessItemRequest, updateReservationPackageRequest, true);

    assertNotNull(response);
  }

  @Test
  void confirmAmendSingleCall_editRoomRequestEmpty_andUpdatePackageRequestNull_Success() throws IOException {
    var businessItemRequest = mockBusinessItemRequest();
    Set<String> reservationIds = businessItemRequest.getReservationIds();
    SpecialRequests specialRequests = mockSpecialRequests();
    specialRequests.setReservationIds(new ArrayList<>(reservationIds));
    ReservationPackagesRequest updateReservationPackageRequest = null;

    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();

    ChangeReservation changeReservation = mockChangeReservationWithComments();
    changeReservation.getReservations().get(0).setRoomStay(new RoomStayType());

    List<ChangeReservation> stayDateRequest = List.of(changeReservation);
    List<List<ChangeReservation>> editRoomRequest = new ArrayList<>();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(true);
    when(specialReqRequestOhipMapper.toChangeReservationDto(anyString(), anyString(), anyList(), anyList()))
        .thenReturn(changeReservation);

    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(businessItemsRequestMapper.toChangeReservationDto(any(), any(), anyList()))
        .thenReturn(changeReservation);
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()))
        .thenReturn(Collections.singletonList(mockProfile()));

    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationById());
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));
    doNothing().when(hotelReservationOutPort).updateBookerDetails(any());

    var response = hotelReservationOutPort.confirmAmendSingleCall(List.of(specialRequests),
        bookerDetailsCnpRequest, stayDateRequest, editRoomRequest,
        businessItemRequest, updateReservationPackageRequest, true);

    assertNotNull(response);
  }

  @Test
  void updateReservationSingleCall__Success() throws IOException {

    BusinessItemsRequest businessItemsRequest = mockBusinessItemRequest();
    SpecialRequests specialRequests = mockSpecialRequests();
    ReservationGuestRequest guestReservationRequest = createGuestReservationRequest(
        WB_LANGUAGE_CODE_EN);
    guestReservationRequest.setBookerProfileId("12345");
    guestReservationRequest.setCompanyProfileId("12345");
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();
    ConfirmReservationRequest confirmReservationRequest = createConfirmPrePaidReservationRequest();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(true);
    when(ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
        guestReservationRequest.getStayingGuests().get(0).getReservationId()))
        .thenReturn(Mono.just(mockReservation()));

    when(packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest)).thenReturn(
        new PackagesRequestOhipDto());
    when(ohipPackagesClient.getPackages(any(), any())).thenReturn(getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(
        packageGroupsRequestOhipMapper.toOhipDto(anyString(), anyString()))).thenReturn(
        mockPackageGroupsFromOhip());

    var packagesResponseOhipDto = ohipPackagesClient.getPackages(
        packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest),
        new LinkedMultiValueMap<>()).block();

    when(reservationPackagesOhipMapper.toModel(updateReservationPackageRequest, 0,
        packagesResponseOhipDto))
        .thenReturn(mockChangeReservation());
    when(specialReqRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any())).thenReturn(
        mockChangeReservationWithComments());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(mockChangeReservation());
    when(businessItemsRequestMapper.toChangeReservationDto(any(),
        any(), anyList())).thenReturn(mockChangeReservationWithComments());

    when(confirmReservationRequestOhipMapper.toChangeReservationModel(
        confirmReservationRequest)).thenReturn(mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(
        confirmReservationRequest.getHotelId(),
        confirmReservationRequest.getReservationId(), mockChangeReservation())).thenReturn(
        createChangeReservationDetails());
    var reservationsDetailsOhip = ohipReservationClient.sendChangeReservationRequest(
        confirmReservationRequest.getHotelId(),
        confirmReservationRequest.getReservationId(), mockChangeReservation()).block();
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(reservationsDetailsOhip
        .getReservations())).thenReturn(mockConfirmReservationResponse());
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.just(mockReservation()));

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
        .thenReturn(createChangeReservationDetails());

    var response = hotelReservationOutPort.updateReservationSingleCall(businessItemsRequest,
        specialRequests,
        guestReservationRequest, updateReservationPackageRequest, confirmReservationRequest, true);

    assertNotNull(response);
    assertEquals("HOTELTEST", response.getHotelId());
    assertEquals("Reserved", response.getReservationStatus());
  }

  @Test
  void updateReservationSingleCallFullData__Success() throws IOException {

    BusinessItemsRequest businessItemsRequest = mockBusinessItemRequest();
    SpecialRequests specialRequests = mockSpecialRequests();
    ReservationGuestRequest guestReservationRequest = createGuestReservationRequest(
            WB_LANGUAGE_CODE_EN);
    guestReservationRequest.setBookerProfileId("12345");
    guestReservationRequest.setCompanyProfileId("12345");
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();
    ConfirmReservationRequest confirmReservationRequest = createConfirmPrePaidReservationRequest();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(false);
    when(ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
            guestReservationRequest.getStayingGuests().get(0).getReservationId()))
            .thenReturn(Mono.just(mockReservation()));

    when(packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest)).thenReturn(
            new PackagesRequestOhipDto());
    when(ohipPackagesClient.getPackages(any(), any())).thenReturn(getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(
            packageGroupsRequestOhipMapper.toOhipDto(anyString(), anyString()))).thenReturn(
            mockPackageGroupsFromOhip());

    var packagesResponseOhipDto = ohipPackagesClient.getPackages(
            packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest),
            new LinkedMultiValueMap<>()).block();

    var changeReservation = mockChangeReservation();
    var characterUDFType = new CharacterUDFType();
    characterUDFType.setName("UDFC15");
    characterUDFType.setValue("cellCode");

    var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(new ArrayList<>(List.of(characterUDFType)));

    changeReservation.getReservations().get(0).setUserDefinedFields(userDefinedFields);

    when(reservationPackagesOhipMapper.toModel(updateReservationPackageRequest, 0,
            packagesResponseOhipDto))
            .thenReturn(changeReservation);
    when(specialReqRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any())).thenReturn(
            mockChangeReservationWithComments());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
            anyString(), anyString())).thenReturn(mockChangeReservation());
    when(businessItemsRequestMapper.toChangeReservationDto(any(),
            any(), anyList())).thenReturn(mockChangeReservationWithComments());

    when(confirmReservationRequestOhipMapper.toChangeReservationModel(
            confirmReservationRequest)).thenReturn(mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(
            confirmReservationRequest.getHotelId(),
            confirmReservationRequest.getReservationId(), mockChangeReservation())).thenReturn(
            createChangeReservationDetails());
    var reservationsDetailsOhip = ohipReservationClient.sendChangeReservationRequest(
            confirmReservationRequest.getHotelId(),
            confirmReservationRequest.getReservationId(), mockChangeReservation()).block();
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(reservationsDetailsOhip
            .getReservations())).thenReturn(mockConfirmReservationResponse());
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
            Flux.just(mockReservation()));

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
            .thenReturn(createChangeReservationDetails());

    var response = hotelReservationOutPort.updateReservationSingleCall(
            businessItemsRequest,
            specialRequests,
            guestReservationRequest,
            updateReservationPackageRequest,
            confirmReservationRequest,
            true);

    assertNotNull(response);
    assertEquals("HOTELTEST", response.getHotelId());
    assertEquals("Reserved", response.getReservationStatus());
  }

  @Test
  void updateReservationSingleCallCustomReference_Success() throws IOException {

    BusinessItemsRequest businessItemsRequest = mockBusinessItemRequest();
    SpecialRequests specialRequests = mockSpecialRequests();
    ReservationGuestRequest guestReservationRequest = createGuestReservationRequest(
        WB_LANGUAGE_CODE_EN);
    guestReservationRequest.setBookerProfileId("12345");
    guestReservationRequest.setCompanyProfileId("12345");
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();
    ConfirmReservationRequest confirmReservationRequest = createConfirmPrePaidReservationRequest();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(false);
    when(ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
        guestReservationRequest.getStayingGuests().get(0).getReservationId()))
        .thenReturn(Mono.just(mockReservation()));

    when(packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest)).thenReturn(
        new PackagesRequestOhipDto());
    when(ohipPackagesClient.getPackages(any(), any())).thenReturn(getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(
        packageGroupsRequestOhipMapper.toOhipDto(anyString(), anyString()))).thenReturn(
        mockPackageGroupsFromOhip());

    var packagesResponseOhipDto = ohipPackagesClient.getPackages(
        packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest),
        new LinkedMultiValueMap<>()).block();

    var changeReservation = mockChangeReservation();
    var characterUDFType = new CharacterUDFType();
    characterUDFType.setName("UDFC15");
    characterUDFType.setValue("cellCode");

    var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(new ArrayList<>(List.of(characterUDFType)));

    changeReservation.getReservations().get(0).setUserDefinedFields(userDefinedFields);

    when(reservationPackagesOhipMapper.toModel(updateReservationPackageRequest, 0,
        packagesResponseOhipDto))
        .thenReturn(changeReservation);
    when(specialReqRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any())).thenReturn(
        mockChangeReservationWithComments());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(mockChangeReservation());
    when(businessItemsRequestMapper.toChangeReservationDto(any(),
        any(), anyList())).thenReturn(mockChangeReservationWithComments());

    when(confirmReservationRequestOhipMapper.toChangeReservationModel(
        confirmReservationRequest)).thenReturn(mockChangeReservation());
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(any(HotelReservationsType.class)))
          .thenReturn(mockConfirmReservationResponse());
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.just(mockReservation()));

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
        .thenReturn(createChangeReservationDetails());

    var response = hotelReservationOutPort.updateReservationSingleCall(
        businessItemsRequest,
        specialRequests,
        guestReservationRequest,
        updateReservationPackageRequest,
        confirmReservationRequest,
        true);

    ArgumentCaptor<ChangeReservation> captor = ArgumentCaptor.forClass(ChangeReservation.class);
    verify(ohipReservationClient).sendChangeReservationRequest(
        eq(confirmReservationRequest.getHotelId()),
        eq(confirmReservationRequest.getReservationId()),
        captor.capture()
    );

    ChangeReservation capturedArgument = captor.getValue();
    assertNotNull(capturedArgument);
    assertEquals("customRef123", capturedArgument.getReservations().get(0).getCustomReference());
    assertNotNull(response);
    assertEquals("HOTELTEST", response.getHotelId());
    assertEquals("Reserved", response.getReservationStatus());
  }

  @Test
  void createReservationGuest__Success() {
    //Arrange
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var guestReservationRequest = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    when(reservationBookerRequestOhipMapper.toDto(any(), any())).thenReturn(new Profile());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class))).thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
            .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()));
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus("1234"))
        .thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(
        anyString(), anyString(), any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);
    //Act
    var reservationGuestResponse = hotelReservationOutPort
        .createReservationGuest(guestReservationRequest);

    //Assert
    assertNotNull(reservationGuestResponse);
    verify(ohipReservationClient, times(2)).sendPutReservationsGuestRequest(
        anyString(), anyString(), any());
    verify(ohipReservationClient, times(2)).sendPostProfileRequest(
        anyString(), any());
  }

  @Test
  void createReservationGuestAndAccompanyingGuest__Success() {
    //Arrange
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var guestReservationRequest = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    guestReservationRequest.getStayingGuests().get(0).setAccompanyingGuestDetails(createAccompanyingGuestDetails());
    when(reservationBookerRequestOhipMapper.toDto(any(), any())).thenReturn(new Profile());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class))).thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus("1234"))
        .thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(
        anyString(), anyString(), any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);
    when(guestDetailsMapper.toDto(any())).thenReturn(guestReservationRequest.getStayingGuests().get(0).getStayingGuestDetails());
    //Act
    var reservationGuestResponse = hotelReservationOutPort
        .createReservationGuest(guestReservationRequest);

    //Assert
    assertNotNull(reservationGuestResponse);
    verify(ohipReservationClient, times(2)).sendPutReservationsGuestRequest(
        anyString(), anyString(), any());
    verify(ohipReservationClient, times(3)).sendPostProfileRequest(
        anyString(), any());
  }

  @Test
  void createReservationGuestSameAsBookerAndAccompanyingGuest__Success() {
    //Arrange
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var guestReservationRequest = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    AccompanyingGuestDetails accompanyingGuestDetails = AccompanyingGuestDetails.builder()
        .lastName("Lastname")
        .build();
    guestReservationRequest.getStayingGuests().get(0).setAccompanyingGuestDetails(accompanyingGuestDetails);
    guestReservationRequest.getStayingGuests().get(0).setSameAsBooker(true);
    when(reservationBookerRequestOhipMapper.toDto(any(), any())).thenReturn(new Profile());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class))).thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus("1234"))
        .thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(
        anyString(), anyString(), any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);
    when(guestDetailsMapper.toDto(any())).thenReturn(guestReservationRequest.getStayingGuests().get(0).getStayingGuestDetails());
    //Act
    var reservationGuestResponse = hotelReservationOutPort
        .createReservationGuest(guestReservationRequest);

    //Assert
    assertNotNull(reservationGuestResponse);
    verify(ohipReservationClient, times(2)).sendPutReservationsGuestRequest(
        anyString(), anyString(), any());
    verify(ohipReservationClient, times(2)).sendPostProfileRequest(
        anyString(), any());
  }

  @Test
  void createReservationGuestAndAccompanyingGuestWithLastName__Success() {
    //Arrange
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var guestReservationRequest = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    AccompanyingGuestDetails accompanyingGuestDetails = AccompanyingGuestDetails.builder()
        .lastName("Lastname")
        .build();
    guestReservationRequest.getStayingGuests().get(0).setAccompanyingGuestDetails(accompanyingGuestDetails);
    when(reservationBookerRequestOhipMapper.toDto(any(), any())).thenReturn(new Profile());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class))).thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus("1234"))
        .thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(
        anyString(), anyString(), any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);
    when(guestDetailsMapper.toDto(any())).thenReturn(guestReservationRequest.getStayingGuests().get(0).getStayingGuestDetails());
    //Act
    var reservationGuestResponse = hotelReservationOutPort
        .createReservationGuest(guestReservationRequest);

    //Assert
    assertNotNull(reservationGuestResponse);
    verify(ohipReservationClient, times(2)).sendPutReservationsGuestRequest(
        anyString(), anyString(), any());
    verify(ohipReservationClient, times(3)).sendPostProfileRequest(
        anyString(), any());
  }

  @Test
  void createReservationGuestAndAccompanyingGuestWithoutLastName__Success() {
    //Arrange
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var guestReservationRequest = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    AccompanyingGuestDetails accompanyingGuestDetails = AccompanyingGuestDetails.builder()
        .title("Mr")
        .firstName("Firstname")
        .emailAddress("email@fakedomain.com")
        .build();
    guestReservationRequest.getStayingGuests().get(0).setAccompanyingGuestDetails(accompanyingGuestDetails);
    when(reservationBookerRequestOhipMapper.toDto(any(), any())).thenReturn(new Profile());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class))).thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus("1234"))
        .thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(
        anyString(), anyString(), any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);
    //Act
    var reservationGuestResponse = hotelReservationOutPort
        .createReservationGuest(guestReservationRequest);

    //Assert
    assertNotNull(reservationGuestResponse);
    verify(ohipReservationClient, times(2)).sendPutReservationsGuestRequest(
        anyString(), anyString(), any());
    verify(ohipReservationClient, times(2)).sendPostProfileRequest(
        anyString(), any());
  }

  @Test
  void createReservationGuest_defaultLanguage_Success() {
    //Arrange
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var guestReservationRequest = createGuestReservationRequest(null);
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class))).thenReturn(new Profile());
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()));
    when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
        Mono.just(mockReservation()));
    when(rulesAgentClient.getChannelSourceInfo(anyString())).thenReturn(
        mockChannelSourceInfoResponse());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus("1234"))
        .thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(
        anyString(), anyString(), any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);
    //Act
    var reservationGuestResponse = hotelReservationOutPort
        .createReservationGuest(guestReservationRequest);

    //Assert
    assertNotNull(reservationGuestResponse);
    verify(ohipReservationClient, times(2)).sendPutReservationsGuestRequest(
        anyString(), anyString(), any());
  }

  @Test
  void createReservationGuest__shouldThrowException() {
    // Arrange
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    String error = "An error was returned by OHIP!";
    var guestReservationRequest = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    when(ohipReservationClient.getReservation(anyString(), anyString()))
            .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()));
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus("1234"))
        .thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(
        anyString(), anyString(), any())).thenThrow(
        new HotelReservationException(OHIP_PUT_RESERVATIONS_GUEST_EXCEPTION,
            error));
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.createReservationGuest(guestReservationRequest)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void createReservationGuest_PreCheckInTrue_Success()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Arrange
    ReservationGuestRequest guestReservationRequest = createAccompanyingGuestReservationRequest(
        false);
    guestReservationRequest.setPreCheckIn(Boolean.TRUE);

    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(),
        any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithGuestNumber(RESERVATION_ID,2,2))));

    // Act
    ReservationGuestResponse reservationGuestResponse = hotelReservationOutPort.createReservationGuest(
        guestReservationRequest);

    // Assert
    assertNotNull(reservationGuestResponse);

    Method updateReservationGuestMethod = HotelReservationOutPortImpl.class.getDeclaredMethod(
        "updateReservationGuest", ReservationGuestRequest.class);
    updateReservationGuestMethod.setAccessible(true);

    @SuppressWarnings("unchecked")
    List<ChangeReservationDetails> changeReservationDetails =
        (List<ChangeReservationDetails>) updateReservationGuestMethod.invoke(
            hotelReservationOutPort,
            guestReservationRequest);

    assertNotNull(changeReservationDetails);

    verify(reservationGuestResponseOhipMapper, times(1)).toReservationGuestResponseModel(any());
  }

  @Test
  void createReservationGuest_PreCheckInTrue_WithProfileDetails_Success()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    when(ohipReservationClient.sendUpdateProfileRequest(anyString(), any())).thenReturn(
        mockStatus("1234")).thenReturn(mockStatus("2345"));
    when(reservationAccompanyingGuestProfileRequestOhipMapper.toDto(any(), any())).thenReturn(
        mockProfile());
    // Act
    invokeUpdateProfileWithDetails(mockProfile(), false);

    // Assert
    verify(ohipReservationClient, times(1)).sendUpdateProfileRequest(anyString(), any());
    verify(ohipReservationClient, never()).sendPostProfileRequest(anyString(), any());
  }

  @Test
  void createReservationGuest_PreCheckInTrue_WithoutProfileDetails_Success()
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
        mockStatus("1234"));
    when(reservationAccompanyingGuestProfileRequestOhipMapper.toDto(any(), any())).thenReturn(
        mockProfile());
    // Act
    invokeUpdateProfileWithDetails(null, true);

    // Assert
    verify(ohipReservationClient, never()).sendUpdateProfileRequest(anyString(), any());
    verify(ohipReservationClient, times(1)).sendPostProfileRequest(anyString(), any());
  }

  private void invokeUpdateProfileWithDetails(Profile profileDetails, boolean primary)
      throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method updateProfileMethod = HotelReservationOutPortImpl.class.getDeclaredMethod(
        "updateProfile", String.class, StayingGuest.class, Profile.class, Map.class, boolean.class);
    updateProfileMethod.setAccessible(true);

    ReservationGuestRequest guestReservationRequest = createGuestReservationRequest(
        WB_LANGUAGE_CODE_EN);
    StayingGuest stayingGuest = guestReservationRequest.getStayingGuests().get(0);

    Map<String, Boolean> updatedProfileIdsMap = new HashMap<>();
    updateProfileMethod.invoke(hotelReservationOutPort, HOTEL_ID, stayingGuest, profileDetails,
        updatedProfileIdsMap, primary);

    assertNotNull(updatedProfileIdsMap);
  }

  @Test
  void createReservationGuest_ProfileIdNull() {
    ReservationGuestRequest guestReservationRequest = createAccompanyingGuestReservationRequest(
        false);
    guestReservationRequest.getStayingGuests().get(0).getStayingGuestDetails().setProfileId(null);

    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any())).thenReturn(
        new ReservationGuestResponse());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(),
        any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithGuestNumber(RESERVATION_ID,2,2))));

    // Act
    ReservationGuestResponse reservationGuestResponse = hotelReservationOutPort.createReservationGuest(
        guestReservationRequest);

    // Assert
    assertNotNull(reservationGuestResponse);
    assertNull(reservationGuestResponse.getHotelId());
    assertNull(reservationGuestResponse.getReservationIds());
  }

  @Test
  void createReservationGuest_AccompanyingGuestNull() {
    ReservationGuestRequest guestReservationRequest = createAccompanyingGuestReservationRequest(null);
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithGuestNumber(RESERVATION_ID,2,2))));

    // Act
    HotelReservationException exception = assertThrows(HotelReservationException.class, () -> {
      hotelReservationOutPort.createReservationGuest(guestReservationRequest);
    });

    // Assert
    assertEquals("A reservation must have exactly one lead guest", exception.getMessage());
  }

  @Test
  void updateReservationGuest_MoreThanOneLeadGuest_ThrowsException() {
    // Arrange
    ReservationGuestRequest guestReservationRequest = createAccompanyingGuestReservationRequest(
        false);
    List<StayingGuest> stayingGuests = new ArrayList<>(guestReservationRequest.getStayingGuests());
    List<StayingGuest> mockGuests = mockStayingGuestsDetails();
    mockGuests.get(0).setIsAccompanyingGuest(false);
    stayingGuests.addAll(mockGuests);
    guestReservationRequest.setStayingGuests(stayingGuests);
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));

    HotelReservationException exception = assertThrows(HotelReservationException.class, () -> {
      hotelReservationOutPort.createReservationGuest(guestReservationRequest);
    });
    assertEquals("A reservation must have exactly one lead guest", exception.getMessage());
  }

  @Test
  void createReservationGuest_PreCheckInTrue_MoreThanThreeGuests_ExceptionThrown() {
    // Arrange
    ReservationGuestRequest guestReservationRequest = createAccompanyingGuestReservationRequest(
        true);
    List<StayingGuest> stayingGuests = new ArrayList<>(guestReservationRequest.getStayingGuests());
    stayingGuests.forEach(guest -> guest.setReservationId(RESERVATION_ID));
    List<StayingGuest> mockGuests = mockStayingGuestsDetails();
    mockGuests.forEach(guest -> guest.setReservationId(RESERVATION_ID));
    stayingGuests.addAll(mockGuests);
    guestReservationRequest.setStayingGuests(stayingGuests);
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithGuestNumber(RESERVATION_ID,2,2))));

    // Act & Assert
    HotelReservationException exception = assertThrows(HotelReservationException.class, () -> {
      hotelReservationOutPort.createReservationGuest(guestReservationRequest);
    });
    assertEquals("Max Accompanying Guests allowed is: 3", exception.getMessage());
  }

  @Test
  void createReservationGuest_PreCheckInTrue_AddressNull_ThrowsException() {
    // Arrange
    var mockProfile = new Profile();
    var uniqueIDType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId("31783");
    uniqueIDType.setType("Profile");
    mockProfile.setProfileIdList(List.of(uniqueIDType));

    String error = "No existing address found for ID: 12345";
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile));
    when(reservationAccompanyingGuestProfileRequestOhipMapper.toDto(any(), any())).thenThrow(
            new IllegalArgumentException(error));
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithGuestNumber(RESERVATION_ID,1,0))));
    ReservationGuestRequest guestReservationRequest = createAccompanyingGuestReservationRequest(
            true);
    List<StayingGuest> stayingGuests = new ArrayList<>(guestReservationRequest.getStayingGuests());
    stayingGuests.forEach(guest -> guest.setIsAccompanyingGuest(false));

    // Act & Assert
    var exception = assertThrows(IllegalArgumentException.class, () -> {
      hotelReservationOutPort.createReservationGuest(guestReservationRequest);
    });
    assertEquals(error, exception.getMessage());
  }

  @Test
  void getDepositsResponse__Success() {
    //Arrange
    var hotelId = "LONEUS";
    var resNo = "1234567";
    var depositsResponse = new DepositsResponse();
    depositsResponse.setDeposits(
        List.of(Deposits.builder().paymentReference("3CPReference").build()));
    when(ohipReservationClient.getDepositsByReservationId(
        hotelId, resNo)).thenReturn(new RetrievedDepositFolio());
    when(depositsResponseOhipMapper.toModel(any())).thenReturn(depositsResponse);

    //Act
    var response = hotelReservationOutPort.getDepositsForReservationId(hotelId, resNo);

    //Assert
    assertThat(response.getDeposits(), hasSize(1));
    assertEquals("3CPReference", response.getDeposits().get(0).getPaymentReference());
  }

  @Test
  void confirmPrePaidReservation__ShouldReturnOk() throws IOException {

    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {
      final ArgumentCaptor<ChangeReservation> changeReqCaptor =
          ArgumentCaptor.forClass(ChangeReservation.class);
      InOrder inOrder = inOrder(ohipReservationClient);

      final var beforeUpdate = mockReservationWithPaymentCard();
      final var afterUpdate = mockReservationWithPaymentCard();
      afterUpdate.getReservations().getReservation().get(0).getReservationPolicies()
          .getDepositPolicies().get(0).setComments("Updated");
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
          Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());

      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenReturn(new PostedDepositFolio());

      when(ohipReservationClient.getReservation(anyString(), anyString()))
          .thenReturn(Mono.just(beforeUpdate))
          .thenReturn(Mono.just(afterUpdate));

      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
          Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
          Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
          anyString())).thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(true);

      //Act
      var reservationsDetailsResponse = hotelReservationOutPort.confirmReservation(
          createConfirmPrePaidReservationRequest());

      //Assert
      assertNotNull(reservationsDetailsResponse);
      assertEquals("HOTELTEST", reservationsDetailsResponse.getHotelId());
      assertEquals("Reserved", reservationsDetailsResponse.getReservationStatus());
      // 1) Assert the first call happens without waiting
      inOrder.verify(ohipReservationClient, Mockito.times(1))
          .sendChangeReservationRequest(anyString(), anyString(), changeReqCaptor.capture());
      // 2) Assert the second call happens later (up to 1s)
      inOrder.verify(ohipReservationClient, Mockito.timeout(1500).times(1))
          .sendChangeReservationRequest(anyString(), anyString(), changeReqCaptor.capture());
    }
  }


  @Test
  void confirmPrePaidReservationDepositFolioOnHoldFalse__ShouldReturnOk() throws IOException {

    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
          Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());

      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenReturn(new PostedDepositFolio());

      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(mockReservationWithPaymentCard()));

      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
          Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
          Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
          anyString())).thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);

      //Act
      var reservationsDetailsResponse = hotelReservationOutPort.confirmReservation(
          createConfirmPrePaidReservationRequest());

      //Assert
      assertNotNull(reservationsDetailsResponse);
      assertEquals("HOTELTEST", reservationsDetailsResponse.getHotelId());
      assertEquals("Reserved", reservationsDetailsResponse.getReservationStatus());
      verify(ohipReservationClient, Mockito.timeout(1000)).sendChangeReservationRequest(anyString(),
          anyString(), any());
    }
  }


  @Test
  void confirmPrePaidReservation__ShouldFailIfDepositPolicyDoesNotUpdate() {

    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {
      var error = "An error was returned by OHIP: Could not fetch reservation details with updated deposit policy after change reservation request.";

      final var reservation = mockReservationWithPaymentCard();
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());

      when(ohipReservationClient.getReservation(anyString(), anyString()))
          .thenReturn(Mono.just(reservation));

      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
          Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(true);

      //Act

      HotelReservationException exception = Assertions
          .assertThrows(HotelReservationException.class, () ->
              hotelReservationOutPort.confirmReservation(
                  createConfirmPrePaidReservationRequest())
          );
      // Assert
      Assertions.assertEquals(error, exception.getMessage());
      verify(ohipReservationClient, times(4)).getReservation(anyString(), anyString());
    }
  }

  @Test
  void getCancellationPoliciesResponse__reservationId__Success() {
    //Arrange
    var hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");

    when(ohipReservationClient.getReservations(hotelId, reservationIds))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getHotelConfig("HOTELTEST")).thenReturn(mockHotelDetailsResponse());
    //Act
    var response = hotelReservationOutPort.getCancellationPolicies(
        reservationIds, hotelId, null, null, null);

    //Assert
    assertEquals("Cancellations after 1pm on the day of arrival charged 100% of 1 night",
        response.getText());
    assertEquals("2022-04-04T01:00:00+01:00", response.getTime());
  }


  @Test
  void getCancellationPoliciesResponse_reservationId__Exception() {
    //Arrange
    var hotelId = "HOTELTEST";
    var error = "An error was returned by OHIP: Could not get cancellation policies from Opera.";
    Set<String> reservationIds = Collections.singleton("147");

    when(ohipReservationClient.getReservations(hotelId, reservationIds))
        .thenReturn(Flux.fromIterable(Collections.EMPTY_LIST));
    when(ohipReservationClient.getHotelConfig("HOTELTEST")).thenReturn(mockHotelDetailsResponse());

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getCancellationPolicies(
                reservationIds, hotelId, null, null, null)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void getCancellationPoliciesResponse__reservationId__shouldThrowException() {
    //Arrange
    var error = "An error was returned by OHIP: Could not get cancellation policies from Opera.";
    var hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");
    when(ohipReservationClient.getHotelConfig("HOTELTEST")).thenThrow(new HotelReservationException(
        OHIP_RETRIEVE_HOTEL_CONFIG_EXCEPTION, error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.getCancellationPolicies(
                reservationIds, hotelId, null, null, null)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void getCancellationPoliciesResponse__ratePlanCode__Success() {
    //Arrange
    var hotelId = "HOTELTEST";
    var ratePlan = "FLEXRATE";
    var arrivalDate = "2022-04-04";

    when(ohipReservationClient.sendGetPolicySchedulesRequest(hotelId, ratePlan))
        .thenReturn(mockPolicySchedulesDetails());
    when(ohipReservationClient.sendGetCancellationPoliciesRequest(hotelId))
        .thenReturn(mockCancellationPolicyDetails());
    when(ohipReservationClient.getHotelConfig("HOTELTEST")).thenReturn(mockHotelDetailsResponse());
    //Act
    var response = hotelReservationOutPort.getCancellationPolicies(
        Collections.EMPTY_SET, hotelId, ratePlan, arrivalDate, null);

    //Assert
    assertEquals("Cancellations after 1pm on the day of arrival charged 100% of 1 night",
        response.getText());
    assertEquals("2022-04-04T01:00:00+01:00", response.getTime());
  }

  @Test
  void updateReservation__Success() {
    //Arrange
    var updReservationRequest = mockUpdateReservationsRequestRequest();
    updReservationRequest.setClearCcAgentIdUdf(true);
    ChangeReservation changeReservation = mockChangeReservationUpdateReservationRequest(true);
    when(updateReservationRequestOhipMapper.toDto(any()))
        .thenReturn(changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest("MANOLD", "1268956",
        changeReservation)).thenReturn(mockUpdateReservationResponse());

    //Act
    hotelReservationOutPort.updateReservations(updReservationRequest, false, null);

    //Assert
    assertDoesNotThrow(
        () -> hotelReservationOutPort.updateReservations(updReservationRequest, false,
            null));
  }

  @Test
  void updateReservation__Success2() {
    //Arrange
    var updReservationRequest = mockUpdateReservationsRequestRequest();
    updReservationRequest.setDistributionIATANumber("12345678");
    var changeReservationMock = mockChangeReservationUpdateReservationRequest(true);
    when(updateReservationRequestOhipMapper.toDto(any()))
        .thenReturn(changeReservationMock);
    when(ohipReservationClient.getReservations(any(), any()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendChangeReservationRequest("MANOLD", "1268956",
        changeReservationMock))
        .thenReturn(mockUpdateReservationResponse());

    //Act
    hotelReservationOutPort.updateReservations(updReservationRequest, false, null, false, "99999",
        null);

    //Assert
    assertDoesNotThrow(
        () -> hotelReservationOutPort.updateReservations(updReservationRequest, false,
            null));
  }

  @Test
  void updateReservation__Success3() {
    // Arrange
    var updReservationRequest = mockUpdateReservationsRequestRequest();
    updReservationRequest.setDistributionIATANumber("12345678");
    var changeReservationMock = mockChangeReservationUpdateReservationRequest(true);

    when(updateReservationRequestOhipMapper.toDto(any()))
            .thenReturn(changeReservationMock);

    when(ohipReservationClient.getReservations(any(), any()))
            .thenReturn(Flux.just(mockReservationWithPurpose("BUSINESS")))
            .thenReturn(Flux.just(mockReservationWithAdditionalInfoNull()));

    when(ohipReservationClient.sendChangeReservationRequest("MANOLD", "1268956", changeReservationMock))
            .thenReturn(mockUpdateReservationResponse());

    // Act
    hotelReservationOutPort.updateReservations(
            updReservationRequest, false, null, false, "99999", null);

    // Assert (kept as you had)
    assertDoesNotThrow(() ->
            hotelReservationOutPort.updateReservations(updReservationRequest, false, null));

  }


  @Test
  void updateReservation_NoUdfsOnRequest_clearAgentIdUdf_Success() {
    //Arrange
    var updReservationRequest = mockUpdateReservationsRequestRequest();
    updReservationRequest.setClearCcAgentIdUdf(true);
    ChangeReservation changeReservation = mockChangeReservationUpdateReservationRequest(false);
    when(updateReservationRequestOhipMapper.toDto(any()))
        .thenReturn(changeReservation);
    when(ohipReservationClient.sendChangeReservationRequest("MANOLD", "1268956",
        changeReservation)).thenReturn(mockUpdateReservationResponse());

    //Act
    hotelReservationOutPort.updateReservations(updReservationRequest, false, null);

    //Assert
    assertDoesNotThrow(
        () -> hotelReservationOutPort.updateReservations(updReservationRequest, false,
            null));
  }

  @Test
  void updateReservation__Exception() {
    //Arrange
    var updReservationRequest = mockUpdateReservationsRequestRequest();
    when(updateReservationRequestOhipMapper.toDto(any()))
        .thenReturn(mockChangeReservationUpdateReservationRequest(true));
    when(ohipReservationClient.getReservations(any(), any()))
        .thenReturn(Flux.fromIterable(Collections.EMPTY_LIST));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateReservations(updReservationRequest, false, null, false,
                "99999",
                null)
        );
    // Assert
    Assertions.assertEquals("No reservations were found in Opera with given id: 99999", exception.getMessage());
  }

  private CreditCardInfo mockCreditCardInfo() {
    var creditCardInfo = new CreditCardInfo();
    var creditCard = new ResPaymentCardType();

    var cardId = new uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.UniqueIDType();
    cardId.setId("221134");
    cardId.setType("CreditCard");

    creditCard.setCardNumber("1234567812345678");
    creditCard.setCardHolderName("cardHolderName");
    creditCard.setCardNumberLast4Digits("6789");
    creditCard.setCardId(cardId);
    creditCard.setCardType(uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CardTypeType.AX);

    creditCardInfo.setCreditCard(creditCard);
    return creditCardInfo;
  }

  private CreditCardInfo mockCreditCardInfoUserDefinedCardType() {
    var creditCardInfo = new CreditCardInfo();
    var creditCard = new ResPaymentCardType();

    var cardId = new uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.UniqueIDType();
    cardId.setId("221134");
    cardId.setType("CreditCard");

    creditCard.setCardNumber("1234567812345678");
    creditCard.setCardHolderName("cardHolderName");
    creditCard.setCardNumberLast4Digits("6789");
    creditCard.setCardId(cardId);
    creditCard.setCardType(uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CardTypeType.AX);
    creditCard.setUserDefinedCardType("BU");

    creditCardInfo.setCreditCard(creditCard);
    return creditCardInfo;
  }

  private CreditCardInfo mockCreditCardInfoUserDefinedCardTypeEmpty() {
    var creditCardInfo = new CreditCardInfo();
    var creditCard = new ResPaymentCardType();

    var cardId = new uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.UniqueIDType();
    cardId.setId("221134");
    cardId.setType("CreditCard");

    creditCard.setCardNumber("1234567812345678");
    creditCard.setCardHolderName("cardHolderName");
    creditCard.setCardNumberLast4Digits("6789");
    creditCard.setCardId(cardId);
    creditCard.setCardType(uk.co.whitbread.hotel.ohip.adapter.generated.models.fof.CardTypeType.AX);
    creditCard.setUserDefinedCardType("");

    creditCardInfo.setCreditCard(creditCard);
    return creditCardInfo;
  }

  @Test
  void getMarketingPreferences__Success() {
    //Arrange
    var hotelId = "LONEUS";
    var resNo = "1234567";
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    languageCodes.put(WB_LANGUAGE_CODE_DE, OPERA_LANGUAGE_CODE_DE);
    var marketingPreferencesResponse = new MarketingPreferencesResponse();
    marketingPreferencesResponse.setContactValue("mail@mail.com");
    marketingPreferencesResponse.setOptIn(true);
    marketingPreferencesResponse.setCustomer(
        new Customer("Mr", "Sarah", "Smith", "GB", WB_LANGUAGE_CODE_EN));
    when(ohipReservationClient.getReservations(hotelId, Set.of(resNo))).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockMarketingReservationResponse())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfileResponse().get(0)));
    when(marketingPreferencesResponseOhipMapper.toModel(any())).thenReturn(
        marketingPreferencesResponse);
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);

    //Act
    var response = hotelReservationOutPort.getMarketingPreferences(hotelId, resNo);

    //Assert
    assertEquals("mail@mail.com", response.getContactValue());
    assertTrue(response.getOptIn());
    assertNotNull(response.getCustomer());
    assertEquals("Sarah", response.getCustomer().getFirstName());
  }

  @Test
  void searchBookingsByBookerRelatedFields_ShouldReturnOK() throws IOException {
    //Arrange
    BookingSearchCriteria searchCriteria = BookingSearchCriteria.builder()
        .bookerEmail("pod6@mailinator.com")
        .limit(10)
        .offset(0)
        .build();

    when(ohipReservationClient.sendBookerGetProfileSummaries(anyMap())).thenReturn(
        mockProfileSummaries());
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        mockProfileList());
    when(ohipReservationClient.sendGetReservationsByReservationRelatedFields(anyMap())).thenReturn(
        mockReservationList());
    when(searchBookingsResponseOhipMapper.toReservationDetailsResponseForModel(anyMap(),
        anyMap())).thenReturn(mockBookingSearchReservationDetails(1));

    //Act
    var bookingSearchResponse = hotelReservationOutPort.searchBookings(searchCriteria);

    //Assert
    assertNotNull(bookingSearchResponse);
    assertThat(bookingSearchResponse.getBookings(), hasSize(1));
  }

  @Test
  void searchBookingsByReservationRelatedFields_ShouldReturnOK() {
    //Arrange
    BookingSearchCriteria searchCriteria = BookingSearchCriteria.builder()
        .bookingReference("AKU4641269")
        .offset(0)
        .limit(10)
        .build();

    when(ohipReservationClient.sendGetReservationsByReservationRelatedFields(anyMap())).thenReturn(
        mockSearchReservationResponseOhip(0, 100, 1, false));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        mockProfileResponse());
    when(searchBookingsResponseOhipMapper.toReservationDetailsResponseForModel(anyMap(),
        anyMap())).thenReturn(
        mockBookingSearchReservationDetails(1));

    //Act
    var bookingSearchResponse = hotelReservationOutPort.searchBookings(searchCriteria);

    //Assert
    assertNotNull(bookingSearchResponse);
    assertThat(bookingSearchResponse.getBookings(), hasSize(1));
  }

  @Test
  void searchBookingsByByReservationRelatedFields_ShouldReturnResultsLimitExceeded() {
    //Arrange
    BookingSearchCriteria searchCriteria = BookingSearchCriteria.builder()
        .arrivalDate("2023-02-22")
        .offset(0)
        .limit(10)
        .build();

    when(ohipReservationClient.sendGetReservationsByReservationRelatedFields(anyMap())).thenReturn(
            mockSearchReservationResponseOhip(0, 100, 100, true))
        .thenReturn(mockSearchReservationResponseOhip(100, 100, 2, false));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            mockProfileResponse())
        .thenReturn(mockProfileResponse());
    when(searchBookingsResponseOhipMapper.toReservationDetailsResponseForModel(anyMap(),
        anyMap())).thenReturn(
            mockBookingSearchReservationDetails(100))
        .thenReturn(mockBookingSearchReservationDetails(2));

    //Act
    var bookingSearchResponse = hotelReservationOutPort.searchBookings(searchCriteria);

    //Assert
    assertNotNull(bookingSearchResponse);
    assertThat(bookingSearchResponse.getBookings(), hasSize(0));
    assertTrue(bookingSearchResponse.isResponseLimitExceeded());
  }

  @Test
  void searchBookingsByHotelRelatedFields_ShouldReturnOK() {
    //Arrange
    BookingSearchCriteria searchCriteria = BookingSearchCriteria.builder()
        .hotelId("MANOLD")
        .offset(0)
        .limit(10)
        .build();

    when(ohipReservationClient.sendGetReservationsByHotelRelatedFields(anyMap())).thenReturn(
        mockSearchReservationResponseOhip(0, 100, 1, false));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        mockProfileResponse());
    when(searchBookingsResponseOhipMapper.toReservationDetailsResponseForModel(anyMap(),
        anyMap())).thenReturn(
        mockBookingSearchReservationDetails(1));

    //Act
    var bookingSearchResponse = hotelReservationOutPort.searchBookings(searchCriteria);

    //Assert
    assertNotNull(bookingSearchResponse);
    assertThat(bookingSearchResponse.getBookings(), hasSize(1));
  }

  @Test
  void updateReasonForStay__Success() {
    //Arrange
    var updateReasonForStayRequest = mockUpdateReasonForStayRequest();
    when(updateReasonForStayRequestOhipMapper.toDto(any())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(
        anyString(), anyString(), any())).thenReturn(mockChangeReservationResponse());
    when(updateReasonForStayResponseOhipMapper.toUpdateReasonForStayResponseModel(
        any())).thenCallRealMethod();

    //Act
    var updateReasonForStayResponse = hotelReservationOutPort.updateReasonForStay(
        updateReasonForStayRequest);

    //Assert
    assertThat(updateReasonForStayResponse.getReservationIds(), hasSize(1));
    assertEquals("123456", updateReasonForStayResponse.getReservationIds().get(0));
    assertEquals("LONEUS", updateReasonForStayResponse.getHotelId());
  }

  @Test
  void updateReasonForStay__shouldThrowException() {
    // Arrange
    String error = "An error was returned by OHIP!";
    var updateReasonForStayRequest = mockUpdateReasonForStayRequest();
    when(updateReasonForStayRequestOhipMapper.toDto(any())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(
        anyString(), anyString(), any())).thenThrow(
        new HotelReservationException(OHIP_CHANGE_RESERVATION_EXCEPTION,
            error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateReasonForStay(updateReasonForStayRequest)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());

  }

  @Test
  void updateReservationOverrideReasons__Success() {
    //Arrange
    var updateReservationOverrideReasonsRequest = mockUpdateReservationOverrideReasonsRequest();
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(updateReservationOverrideReasonsRequestOhipMapper.toDto(any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(
        mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.updateReservationOverrideReasons(
        updateReservationOverrideReasonsRequest);

    //Assert
    verify(ohipReservationClient, times(1))
        .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void updateCcAgentId__Success() {
    //Arrange
    var updateReservationCcAgentIdRequest = mockUpdateReservationCcAgentIdRequest(false);
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(updateReservationCcAgentIdRequestOhipMapper.toDto(any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(
        mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.updateReservationCcAgentId(updateReservationCcAgentIdRequest, true);

    //Assert
    verify(ohipReservationClient, timeout(3000).times(1))
        .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void updateCcAgentId_clearFirst_Success() {
    //Arrange
    var updateReservationCcAgentIdRequest = mockUpdateReservationCcAgentIdRequest(true);
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(updateReservationCcAgentIdRequestOhipMapper.toDto(any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(
        mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.updateReservationCcAgentId(updateReservationCcAgentIdRequest, true);

    //Assert
    verify(ohipReservationClient, timeout(3000).times(2))
        .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  private DepositFolioCriteria mockDepositFolioCriteria() {
    DepositFolioCriteria depositFolioCriteria = new DepositFolioCriteria();
    DepositFolioCriteriaType depositFolioCriteriaType = new DepositFolioCriteriaType();
    PaymentCriteriaType payments = new PaymentCriteriaType();
    depositFolioCriteriaType.setHotelId("HOTELTEST");
    ReservationId reservationId = new ReservationId();
    reservationId.setId("100100");
    depositFolioCriteriaType.setReservationId(reservationId);

    ChargeCriteriaType chargeItem1 = new ChargeCriteriaType();
    CurrencyAmountType currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(new BigDecimal(100));
    chargeItem1.setPrice(currencyAmountType);

    ChargeCriteriaType chargeItem2 = new ChargeCriteriaType();
    CurrencyAmountType currencyAmountType2 = new CurrencyAmountType();
    currencyAmountType2.setAmount(new BigDecimal(100));
    chargeItem2.setPrice(currencyAmountType2);

    depositFolioCriteriaType.setCharges(List.of(chargeItem1, chargeItem2));
    depositFolioCriteriaType.setPayments(List.of(payments));

    depositFolioCriteria.setCriteria(depositFolioCriteriaType);

    return depositFolioCriteria;
  }

  @Test
  void updateReservationOverrideReasons__shouldThrowException() {
    // Arrange
    String error = "An error was returned by OHIP!";
    var updateReservationOverrideReasonsRequest = mockUpdateReservationOverrideReasonsRequest();
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(updateReservationOverrideReasonsRequestOhipMapper.toDto(any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenThrow(new HotelReservationException(OHIP_CHANGE_RESERVATION_EXCEPTION,
        error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateReservationOverrideReasons(
                updateReservationOverrideReasonsRequest)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void updateReservationCcAgentId__shouldThrowException() {
    // Arrange
    String error = "An error was returned by OHIP!";
    var updateReservationCcAgentIdsRequest = mockUpdateReservationCcAgentIdRequest(true);
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(updateReservationCcAgentIdRequestOhipMapper.toDto(any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenThrow(new HotelReservationException(OHIP_CHANGE_RESERVATION_EXCEPTION,
        error));

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.updateReservationCcAgentId(
                updateReservationCcAgentIdsRequest, true)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void updateReservationsToPayOnArrival__Success() {
    //Arrange
    var reservations = mockReservationByIdList();
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(mockChangeReservationResponse());
    when(payOnArrivalReservationRequestOhipMapper.toChangeReservationForModel(
        reservations.get(0))).thenReturn(mockChangeReservation());

    //Act
    hotelReservationOutPort.updateReservationsToPayOnArrival(reservations);

    //Assert
    verify(ohipReservationClient, times(1))
        .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void updateReservationsWithExternalRef__Success() {
    //Arrange
    var hotelId = "hotelId";
    var reservationId = "1244";
    var externalReference = "31243";

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
            any())).thenReturn(Mono.just(new ChangeReservationDetails()));

    //Act
    hotelReservationOutPort.updateReservationsWithExternalRef(hotelId, Set.of(reservationId), externalReference);

    //Assert
    verify(ohipReservationClient, times(1))
            .sendChangeReservationRequest(anyString(), anyString(), any());
  }


  @Test
  void updateReservationsWithPayeeInfo__Success() {
    //Arrange
    var hotelId = "testHotelId";
    var reservationId = "12345";

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
            any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
            Mono.just(mockReservationWithInstructionsAndDf()));
    //Act
    hotelReservationOutPort.updateRoutingInstructionsWithPayeeInfo(hotelId, Set.of(reservationId));

    //Assert
    verify(ohipReservationClient, times(1))
            .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void updateReservationsWithPayeeInfo__noCompanyProfile() {
    //Arrange
    var hotelId = "testHotelId";
    var reservationId = "12345";

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
            any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
            Mono.just(mockReservationNoCompanyProfile()));
    //Act
    hotelReservationOutPort.updateRoutingInstructionsWithPayeeInfo(hotelId, Set.of(reservationId));

    //Assert
    verify(ohipReservationClient, times(1))
            .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void updateReservationsWithPayeeInfo__noReservationProfiles() {
    //Arrange
    var hotelId = "testHotelId";
    var reservationId = "12345";
    var reservation = mockReservationNoCompanyProfile();

    reservation.getReservations().getReservation().get(0).setReservationProfiles(null);

    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
            any())).thenReturn(Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
            Mono.just(reservation));
    //Act
    hotelReservationOutPort.updateRoutingInstructionsWithPayeeInfo(hotelId, Set.of(reservationId));

    //Assert
    verify(ohipReservationClient, times(1))
            .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void getBookingAllowances__Success() throws IOException {

    when(ohipReservationClient.getReservationWithRoutingInstructions(anyString(), anyString())).
        thenReturn(getReservation());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(getBusinessAllowances());

    var response = hotelReservationOutPort.getBookingAllowances("MANOLD", "1234567", List.of());

    assertNotNull(response);
    assertEquals(4, response.getBookingAllowances().size());
  }

  @Test
  void getBookingAllowances_with_reservation_packages_Success() throws IOException {

    when(ohipReservationClient.getReservationWithRoutingInstructions(anyString(), anyString())).
        thenReturn(getReservationWithPackages());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(getBusinessAllowances());

    var response = hotelReservationOutPort.getBookingAllowances("MANOLD", "1234567", List.of());

    assertNotNull(response);
    assertEquals(3, response.getBookingAllowances().size());
  }

  @Test
  void getBookingAllowancesWithBasketAllowancesSuccess() throws IOException {

    when(ohipReservationClient.getReservationWithRoutingInstructions(anyString(), anyString())).
        thenReturn(getReservation());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(getBusinessAllowances());

    var response = hotelReservationOutPort.getBookingAllowances("MANOLD", "1234567", List.of("alcohol"));

    assertNotNull(response);
    assertEquals(2, response.getBookingAllowances().size());
  }

  @Test
  void movePayment_RsvWithCardDetails_inW1__Success() {
    //Arrange
    var hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservationWithPaymentDetails())));
    when(frontDeskClient.getCreditCardInfo(anyString(), anyString())).thenReturn(
        mockCreditCardInfo());
    when(movePaymentDetailsOhipMapper.toChangeReservationDto(any(), any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(
        mockChangeReservationResponse());
    //Act
    hotelReservationOutPort.movePaymentDetails(hotelId, reservationIds);

    //Assert
    verify(ohipReservationClient, times(1))
        .sendChangeReservationRequest(anyString(), anyString(), any());
    assertDoesNotThrow(() -> hotelReservationOutPort.movePaymentDetails(hotelId, reservationIds));
  }

  @Test
  void movePayment_RsvWithoutCardDetails_inW1__Success() {
    //Arrange
    var hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(movePaymentDetailsOhipMapper.toChangeReservationDto(any(), any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(
        mockChangeReservationResponse());
    //Act
    hotelReservationOutPort.movePaymentDetails(hotelId, reservationIds);

    //Assert
    verify(ohipReservationClient, times(1))
        .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void movePayment_RsvWithDummyPaymentCard_NullCardId__Success() {
    //Arrange
    var hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");

    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationWithNullCardId())));

    //Act & Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.movePaymentDetails(hotelId, reservationIds));
    verify(ohipReservationClient, never())
        .sendChangeReservationRequest(anyString(), anyString(), any());
    verify(frontDeskClient, never()).getCreditCardInfo(anyString(), anyString());
  }

  @Test
  void updatePayeeInfo_nonGuaranteed_A2C_POA__Success() {
    //Arrange
    var reservationDetails = mockReservationWithInstructionsAndDf();
    var resGuaranteeType = new ResGuaranteeType();
    resGuaranteeType.setGuaranteeCode("NON");
    reservationDetails.getReservations().getReservation().get(0).getRoomStay().setGuarantee(resGuaranteeType);
    var confirmReservationRequest =
            createConfirmReservationRequest(PaymentOption.ACCOUNT_COMPANY);

    when(ohipReservationClient.getReservation(any(), any())).thenReturn(Mono.just(reservationDetails));
    when(reservationOhipProperties.getNonGuaranteeCode()).thenReturn("NON");
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
            any())).thenReturn(mockChangeReservationResponse());
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
            Flux.fromIterable(Collections.singletonList(reservationDetails)));
    //Act
    hotelReservationOutPort.confirmReservation(confirmReservationRequest);

    //Assert
    verify(ohipReservationClient, times(2))
            .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void updateBookerDetailsAndCompany__Success() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()))
        .thenReturn(Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toModel(any())).thenReturn(mockProfileType());
    when(bookerProfileOhipMapper.toDto(any(), any())).thenReturn(mockProfile());
    when(companyProfileOhipMapper.toModel(any())).thenReturn(mockProfileType());
    when(companyProfileOhipMapper.toDto(any(), any())).thenReturn(mockProfile());

    //Act
    hotelReservationOutPort.updateBookerDetails(bookerDetailsCnpRequest);

    //Assert
    verify(ohipReservationClient, times(2)).sendUpdateProfileRequest(anyString(), any());
  }

  @Test
  void updateBillingAddress__Success() {
    //Arrange
    var billingAddressRequest = mockBillingAddressRequest();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfileBilling()))
        .thenReturn(Collections.singletonList(mockProfileBilling()));
    when(bookerProfileOhipMapper.toBillingDto(any(), any())).thenReturn(mockProfileBilling());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCaptureBillingAddressBb()))
            .thenReturn(false);

    //Act
    hotelReservationOutPort.updateBillingAddress(billingAddressRequest);

    //Assert
    verify(ohipReservationClient, times(1)).sendUpdateProfileRequest(anyString(), any());
  }

  @Test
  void updateBillingAddressBusiness__Success() {
    //Arrange
    var billingAddressRequest = mockBillingAddressRequestBusiness();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
                    Collections.singletonList(mockProfile()))
            .thenReturn(Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toBillingDto(any(), any())).thenReturn(mockProfileBusiness());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCaptureBillingAddressBb()))
            .thenReturn(false);
    //Act
    hotelReservationOutPort.updateBillingAddress(billingAddressRequest);

    //Assert
    verify(ohipReservationClient, times(3)).sendUpdateProfileRequest(anyString(), any());
  }

  @Test
  void updateBillingAddressBusiness__SuccessFeatureFlagOn() {
    //Arrange
    var billingAddressRequest = mockBillingAddressRequestBusinessGuestAndCompanyUpdates();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
                    Collections.singletonList(mockProfile()))
            .thenReturn(Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toBillingDto(any(), any())).thenReturn(mockProfileBusiness());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCaptureBillingAddressBb()))
            .thenReturn(true);
    //Act
    hotelReservationOutPort.updateBillingAddress(billingAddressRequest);

    //Assert
    verify(ohipReservationClient, times(3)).sendUpdateProfileRequest(anyString(), any());
  }

  @Test
  void updateBillingAddressCcui__Success() {
    //Arrange
    var billingAddressRequest = mockBillingAddressRequestBusiness();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
                    Collections.singletonList(mockProfile()))
            .thenReturn(Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toBillingDto(any(), any())).thenReturn(mockProfileBusiness());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCaptureBillingAddressBb()))
            .thenReturn(false);

    //Act
    hotelReservationOutPort.updateBillingAddressCcui(billingAddressRequest);

    //Assert
    verify(ohipReservationClient, times(3)).sendUpdateProfileRequest(anyString(), any());
  }

  @Test
  void updateBillingAddress__CountryCodeNull() {
    //Arrange
    var billingAddressRequest = mockBillingAddressRequestBusiness();
   billingAddressRequest.getBooker().getAddress().setCountryCode(null);

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
                    Collections.singletonList(mockProfile()))
            .thenReturn(Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toBillingDto(any(), any())).thenReturn(mockProfileBusiness());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCaptureBillingAddressBb()))
            .thenReturn(false);

    //Act
    hotelReservationOutPort.updateBillingAddress(billingAddressRequest);

    //Assert
    verify(ohipReservationClient, times(3)).sendUpdateProfileRequest(anyString(), any());
  }

  @Test
  void updateBillingAddress__CityName() {
    //Arrange
    var billingAddressRequest = mockBillingAddressRequestBusiness();
    billingAddressRequest.getBooker().getAddress().setAddressLine4(null);

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
                    Collections.singletonList(mockProfile()))
            .thenReturn(Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toBillingDto(any(), any())).thenReturn(mockProfileBusiness());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getCaptureBillingAddressBb()))
            .thenReturn(false);

    //Act
    hotelReservationOutPort.updateBillingAddress(billingAddressRequest);

    //Assert
    verify(ohipReservationClient, times(3)).sendUpdateProfileRequest(anyString(), any());
  }

  @Test
  void updateBookerDetailsAndRemoveCompany__Success() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();
    bookerDetailsCnpRequest.getBooker().setCompanyName(null);

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toModel(any())).thenReturn(mockProfileType());
    when(bookerProfileOhipMapper.toDto(any(), any())).thenReturn(mockProfile());
    when(updateReservationAttachedProfilesRequestOhipMapper.toDto(anyString(), anyString(),
        anyString(),
        eq(null))).thenReturn(mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    //Act
    hotelReservationOutPort.updateBookerDetails(bookerDetailsCnpRequest);

    //Assert
    verify(ohipReservationClient, times(1)).sendUpdateProfileRequest(anyString(), any());
    verify(ohipReservationClient, times(1)).sendChangeReservationRequest(anyString(), anyString(),
        any());
  }

  @Test
  void addBookerDetailsAndCompany__Success() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequestForAddRoom();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(updateReservationAttachedProfilesRequestOhipMapper.toDto(anyString(), anyString(),
            anyString(),anyString())).thenReturn(mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
            .thenReturn(mockChangeReservationResponse());
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    //Act
    hotelReservationOutPort.addBookerDetails(bookerDetailsCnpRequest);

    //Assert
    verify(ohipReservationClient, times(2)).sendChangeReservationRequest(anyString(), anyString(),
            any());
  }

  @Test
  void addBookerDetailsAndCompany__WithoutCompany() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequestForAddRoom();

    Reservation reservation = mockReservationNoCompanyProfile();
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
            .thenReturn(mockChangeReservationResponse());
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    //Act
    hotelReservationOutPort.addBookerDetails(bookerDetailsCnpRequest);

    //Assert
    verify(ohipReservationClient, times(2)).sendChangeReservationRequest(anyString(), anyString(),
            any());
  }

  @Test
  void getBookerDetailsAndRemoveCompany__Success() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();
    bookerDetailsCnpRequest.getBooker().setCompanyName(null);
    var mockedReservations = mockReservation();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockedReservations)));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toModel(any())).thenReturn(mockProfileType());
    when(bookerProfileOhipMapper.toDto(any(), any())).thenReturn(mockProfile());
    when(updateReservationAttachedProfilesRequestOhipMapper.toDto(anyString(), anyString(),
        anyString(),
        eq(null))).thenReturn(mockChangeReservation());

    //Act
    List<ChangeReservation> bookerDetailsChangeReservation = hotelReservationOutPort
        .getBookerDetailsChangeReservation(bookerDetailsCnpRequest);

    //Assert
    assertNotNull(bookerDetailsChangeReservation);
    verify(ohipReservationClient, times(1)).sendUpdateProfileRequest(anyString(), any());
    assertNotNull(mockedReservations);
  }

  @Test
  void getBookerDetailsAndRemoveCompany__ThrowErrorWhileTryingToGetTheProfile() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();
    bookerDetailsCnpRequest.getBooker().setCompanyName("Something");
    var reservation = mockReservation();
    reservation.getReservations().getReservation().get(0).getReservationProfiles()
        .setReservationProfile(
            reservation.getReservations().getReservation().get(0).getReservationProfiles()
                .getReservationProfile().stream()
                .filter(reservationProfileType -> ResProfileTypeType.COMPANY.equals(
                    reservationProfileType.getReservationProfileType())).toList());

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));

    assertThrows(HotelReservationException.class, () ->
        hotelReservationOutPort.getBookerDetailsChangeReservation(bookerDetailsCnpRequest));
  }

  @Test
  void updateBookerDetailsAndAddCompany__Success() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();
    var reservation = mockReservation();
    reservation.getReservations().getReservation().get(0).getReservationProfiles()
        .setReservationProfile(
            reservation.getReservations().getReservation().get(0).getReservationProfiles()
                .getReservationProfile().stream()
                .filter(reservationProfileType -> ResProfileTypeType.RESERVATIONCONTACT.equals(
                    reservationProfileType.getReservationProfileType())).toList());
    var profileType = mockProfileType();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
        Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toModel(any())).thenReturn(profileType);
    when(bookerProfileOhipMapper.toDto(any(), any())).thenReturn(mockProfile());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
        mockStatus(profileType.getProfileId().getId()));
    when(updateReservationAttachedProfilesRequestOhipMapper.toDto(anyString(), anyString(),
        anyString(),
        anyString())).thenReturn(mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);

    //Act
    hotelReservationOutPort.updateBookerDetails(bookerDetailsCnpRequest);

    //Assert
    verify(ohipReservationClient, times(1)).sendUpdateProfileRequest(anyString(), any());
    verify(ohipReservationClient, times(1)).sendPostProfileRequest(anyString(), any());
    verify(ohipReservationClient, times(1)).sendChangeReservationRequest(anyString(), anyString(),
        any());
  }

  @Test
  void getBookerDetailsAndAddCompany__Success() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();
    var reservation = mockReservation();
    reservation.getReservations().getReservation().get(0).getReservationProfiles()
            .setReservationProfile(
                    reservation.getReservations().getReservation().get(0).getReservationProfiles()
                            .getReservationProfile().stream()
                            .filter(reservationProfileType -> ResProfileTypeType.RESERVATIONCONTACT.equals(
                                    reservationProfileType.getReservationProfileType())).toList());
    var profileType = mockProfileType();

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
            Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet())).thenReturn(
            Collections.singletonList(mockProfile()));
    when(bookerProfileOhipMapper.toModel(any())).thenReturn(profileType);
    when(bookerProfileOhipMapper.toDto(any(), any())).thenReturn(mockProfile());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any())).thenReturn(
            mockStatus(profileType.getProfileId().getId()));
    when(updateReservationAttachedProfilesRequestOhipMapper.toDto(anyString(), anyString(),
            anyString(),
            anyString())).thenReturn(mockChangeReservation());

    //Act
    List<ChangeReservation> bookerDetailsChangeReservation = hotelReservationOutPort
            .getBookerDetailsChangeReservation(bookerDetailsCnpRequest);

    //Assert
    assertNotNull(bookerDetailsChangeReservation);
  }

  @Test
  void updateBookerDetailsNothingToUpdate__Success() {
    //Arrange
    var bookerDetailsCnpRequest = mockBookerDetailsCnpRequest();
    bookerDetailsCnpRequest.getBooker().setCompanyName(null);
    var reservation = mockReservation();
    reservation.getReservations().getReservation().get(0).getReservationProfiles()
        .setReservationProfile(new ArrayList<>());

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));

    //Act
    hotelReservationOutPort.updateBookerDetails(bookerDetailsCnpRequest);

    //Assert
    verifyNoMoreInteractions(ohipReservationClient);
  }

  @Test
  void getDepositFolioForReservations__EmptyList() {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.emptyList()));

    //Act
    DepositFoliosResponse result =
        hotelReservationOutPort.getDepositFolioForReservations("hotelId",
            Set.of("resId1", "resId2"));

    //Assert
    verify(rulesAgentClient, never()).getVatCodes(anyString(), anyList());
    verify(depositFoliosRequestMapper, never()).toDepositFolioRequestModel(any());
    verify(ohipReservationClient, never()).getReservationAmounts(anyString(), anyString());
    verify(depositFoliosRequestOhipMapper, never()).toDepositFolioCriteriaAmendModel(any(), any(),
        any(), any());
    assertNotNull(result);
    assertTrue(result.getDepositFolios().isEmpty());

  }

  @Test
  void getDepositFolioForReservations__Success() throws IOException {
    //Arrange
    DepositFolio depositFolio = DepositFolio.builder()
        .reservationId("resId1")
        .hotelId("hotelId1").build();
    depositFolio.setCharges(mockDepositFolioCharges());
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(mockRateInfoDetails());
    when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
    when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
        .thenReturn(mockDepositFolioConfirmationRequest());
    when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaAmendModel(
        any(), any(), any(), any())).thenReturn(mockDepositFolioCriteria());

    when(depositFolioCriteriaMapper.toDepositFolioModel(any())).thenReturn(depositFolio);
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("resId1"), Mono.just(mockReservationAmounts())));

    //Act
    DepositFoliosResponse result =
        hotelReservationOutPort.getDepositFolioForReservations("hotelId", Set.of("resId1"));

    //Assert
    assertNotNull(result);
    assertNotNull(result.getDepositFolios());
    assertFalse(result.getDepositFolios().isEmpty());
    assertEquals("transactionCode1",
        result.getDepositFolios().get(0).getCharges().get(0).getTransactionCode());
  }

  @Test
  void getDepositFolioForCompareCityTax_VatInclusive_Success() throws IOException {
    //Arrange

    var reservation = mockReservation();

    var reservationPackageScheduleType = reservation.getReservations().getReservation().get(0)
        .getReservationPackages().get(0).getScheduleList().get(0);
    reservationPackageScheduleType.setComputedResvPrice(BigDecimal.valueOf(59.94));

    DepositFolio depositFolio = DepositFolio.builder()
        .reservationId("resId1")
        .hotelId("hotelId1").build();
    depositFolio.setCharges(mockDepositFolioCharges());
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(mockRateInfoDetails());
    when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
    when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
        .thenReturn(mockDepositFolioConfirmationRequest());
    when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaAmendModel(
        any(), any(), any(), any())).thenReturn(mockDepositFolioCriteria());

    when(depositFolioCriteriaMapper.toDepositFolioModel(any())).thenReturn(depositFolio);
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("resId1"), Mono.just(mockReservationAmounts())));

    //Act
    DepositFoliosResponse result =
        hotelReservationOutPort.getDepositFolioForReservations("hotelId", Set.of("resId1"));

    //Assert
    assertNotNull(result);
    assertNotNull(result.getDepositFolios());
    assertFalse(result.getDepositFolios().isEmpty());
    assertEquals(BigDecimal.valueOf(59.94),
        reservationPackageScheduleType.getComputedResvPrice());
  }

  @Test
  void getDepositFolioForCompareCityTax_DifferentPrecisions_RoundUp_NonVatInclusive_Success()
      throws IOException {
    //Arrange

    var reservation = mockReservation();

    var reservationPackageScheduleType = reservation.getReservations().getReservation().get(0)
        .getReservationPackages().get(0).getScheduleList().get(0);
    reservationPackageScheduleType.setComputedResvPrice(BigDecimal.valueOf(67.025));

    var mockRateInfoDetailsTestValue = mockRateInfoDetails();
    mockRateInfoDetailsTestValue.getDetail().getPackages().stream()
        .filter(ratePckg -> "CITYTAX".equals(ratePckg.getCode()))
        .findFirst().get().setAmountBeforeTax(BigDecimal.valueOf(67.03));

    DepositFolio depositFolio = DepositFolio.builder()
        .reservationId("resId1")
        .hotelId("hotelId1").build();
    depositFolio.setCharges(mockDepositFolioCharges());
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
        .thenReturn(mockRateInfoDetailsTestValue);
    when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
    when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
        .thenReturn(mockDepositFolioConfirmationRequest());
    when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaAmendModel(
        any(), any(), any(), any())).thenReturn(mockDepositFolioCriteria());

    when(depositFolioCriteriaMapper.toDepositFolioModel(any())).thenReturn(depositFolio);
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("resId1"), Mono.just(mockReservationAmounts())));

    //Act
    DepositFoliosResponse result =
        hotelReservationOutPort.getDepositFolioForReservations("hotelId", Set.of("resId1"));

    //Assert
    assertNotNull(result);
    assertNotNull(result.getDepositFolios());
    assertFalse(result.getDepositFolios().isEmpty());
    assertEquals(BigDecimal.valueOf(70.95),
        reservationPackageScheduleType.getComputedResvPrice());
  }

  @Test
  void createDepositFolioForReservations__Success() {
    //Arrange
    DepositFolio depositFolio = DepositFolio.builder().reservationId("resId1")
        .hotelId("hotelId1").build();
    depositFolio.setCharges(mockDepositFolioCharges());
    DepositFoliosResponse depositFoliosResponse = DepositFoliosResponse.builder()
        .depositFolios(Collections.singletonList(depositFolio)).build();

    Reservation reservation = mockReservation();

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));
    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("CA");
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);

    var resPaymentTypeCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    var cardId = new UniqueIDType();
    cardId.setId("221134");
    cardId.setType("CreditCard");

    resPaymentTypeCard.setCardId(cardId);
    reservationPaymentMethod.setPaymentCard(resPaymentTypeCard);
    reservation.getReservations().getReservation().get(0)
        .setReservationPaymentMethods(Collections.singletonList(reservationPaymentMethod));

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));

    when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
        any(), any(), any())).thenReturn(mockDepositFolioCriteria());

    when(frontDeskClient.getCreditCardInfo(anyString(), anyString())).thenReturn(
        mockCreditCardInfo());

    //Act
    hotelReservationOutPort.createDepositFolios(depositFoliosResponse);

    //Assert
    verify(ohipReservationClient).sendDepositFoliosRequest(any(), any(), any());
    assertDoesNotThrow(() -> hotelReservationOutPort.createDepositFolios(depositFoliosResponse));
  }

  @Test
  void createDepositFolioForReservations_dummyPaymentCardWithNullCardId_Success() {
    //Arrange
    DepositFolio depositFolio = DepositFolio.builder().reservationId("resId1")
        .hotelId("hotelId1").build();
    depositFolio.setCharges(mockDepositFolioCharges());
    DepositFoliosResponse depositFoliosResponse = DepositFoliosResponse.builder()
        .depositFolios(Collections.singletonList(depositFolio)).build();

    Reservation reservation = mockReservation();

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));

    // Dummy payment method: paymentCard is not null, but all its fields (including cardId) are null
    var dummyReservationPaymentMethod = new ReservationPaymentMethodType();
    dummyReservationPaymentMethod.setPaymentMethod("CA");
    dummyReservationPaymentMethod.setFolioView(1);
    dummyReservationPaymentMethod.setBalance(currencyAmountType);
    var dummyPaymentCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    dummyReservationPaymentMethod.setPaymentCard(dummyPaymentCard);

    // Valid payment method with a proper cardId
    var validReservationPaymentMethod = new ReservationPaymentMethodType();
    validReservationPaymentMethod.setPaymentMethod("CA");
    validReservationPaymentMethod.setFolioView(1);
    validReservationPaymentMethod.setBalance(currencyAmountType);
    var validPaymentCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    var cardId = new UniqueIDType();
    cardId.setId("221134");
    cardId.setType("CreditCard");
    validPaymentCard.setCardId(cardId);
    validReservationPaymentMethod.setPaymentCard(validPaymentCard);

    reservation.getReservations().getReservation().get(0)
        .setReservationPaymentMethods(
            List.of(dummyReservationPaymentMethod, validReservationPaymentMethod));

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));

    when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
        any(), any(), any())).thenReturn(mockDepositFolioCriteria());

    when(frontDeskClient.getCreditCardInfo(anyString(), anyString())).thenReturn(
        mockCreditCardInfo());

    //Act & Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.createDepositFolios(depositFoliosResponse));
    verify(ohipReservationClient).sendDepositFoliosRequest(any(), any(), any());
    verify(frontDeskClient).getCreditCardInfo("hotelId1", "221134");
  }

  @Test
  void createDepositFolioForMigratedReservations__Success() {
    //Arrange
    DepositFolio depositFolio = DepositFolio.builder().reservationId("resId1")
        .hotelId("hotelId1").charges(mockDepositFolioCharges()).defaultPaymentMethod("DVA").build();
    DepositFoliosResponse depositFoliosResponse = DepositFoliosResponse.builder()
        .depositFolios(Collections.singletonList(depositFolio)).build();

    Reservation reservation = mockReservation();

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));
    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("VA");
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);

    var resPaymentTypeCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    var cardId = new UniqueIDType();
    cardId.setId("221134");
    cardId.setType("CreditCard");

    resPaymentTypeCard.setCardId(cardId);
    reservationPaymentMethod.setPaymentCard(resPaymentTypeCard);
    reservation.getReservations().getReservation().get(0)
        .setReservationPaymentMethods(Collections.singletonList(reservationPaymentMethod));

    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));

    when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
        any(), any(), any())).thenReturn(mockDepositFolioCriteria());

    when(frontDeskClient.getCreditCardInfo(anyString(), anyString())).thenReturn(
        mockCreditCardInfo());

    //Act
    hotelReservationOutPort.createDepositFolios(depositFoliosResponse);

    //Assert
    assertEquals("DVA", reservationPaymentMethod.getPaymentMethod());
    verify(ohipReservationClient).sendDepositFoliosRequest(any(), any(), any());
    assertDoesNotThrow(() -> hotelReservationOutPort.createDepositFolios(depositFoliosResponse));
  }

  private List<DepositFolioCharge> mockDepositFolioCharges() {
    DepositFolioCharge depositFolioCharge1 = DepositFolioCharge.builder()
        .transactionCode("transactionCode1")
        .currencyAmount(CurrencyAmount.builder()
            .amount(new BigDecimal(100))
            .currencyCode("GBP")
            .build())
        .quantity(1).build();

    DepositFolioCharge depositFolioCharge2 = DepositFolioCharge.builder()
        .transactionCode("transactionCode2")
        .currencyAmount(CurrencyAmount.builder()
            .amount(new BigDecimal(200))
            .currencyCode("GBP")
            .build())
        .quantity(2).build();

    return List.of(depositFolioCharge1, depositFolioCharge2);
  }

  @Test
  void deleteRoutingInstruction__RoutingsPresent__Success() throws IOException {
    //Arrange
    var hotelId = "HOTELTEST";
    Set<String> reservationIds = new HashSet<>();
    reservationIds.add("147");
    reservationIds.add("148");

    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(ohipReservationClient.getReservationWithRoutingInstructions(anyString(), anyString())).
        thenReturn(getReservation());
    when(ohipReservationClient.deleteRoutingInstruction(anyString(), anyString(), any())).
        thenReturn(Mono.empty());
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.deleteRoutingInstruction(hotelId, reservationIds);

    //Assert
    verify(ohipReservationClient, times(16))
        .deleteRoutingInstruction(anyString(), anyString(), any());
    verify(ohipReservationClient, times(2))
        .sendChangeReservationRequest(any(), any(), any());
    assertDoesNotThrow(
        () -> hotelReservationOutPort.deleteRoutingInstruction(hotelId, reservationIds));
  }

  @Test
  void deleteRoutingInstruction__RoutingsNotPresent__Success() throws IOException {
    //Arrange
    var hotelId = "HOTELTEST";
    Set<String> reservationIds = Collections.singleton("147");
    var reservation = getReservation();
    reservation.getReservations()
        .getReservation()
        .stream().findFirst()
        .ifPresent(hotelReservationType -> hotelReservationType.setRoutingInstructions(
            Collections.emptyList()));

    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(ohipReservationClient.getReservationWithRoutingInstructions(anyString(), anyString())).
        thenReturn(reservation);
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any()))
        .thenReturn(mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.deleteRoutingInstruction(hotelId, reservationIds);

    //Assert
    verify(ohipReservationClient, never()).deleteRoutingInstruction(anyString(), anyString(),
        any());
    verify(ohipReservationClient, times(1)).sendChangeReservationRequest(any(), any(), any());
    assertDoesNotThrow(
        () -> hotelReservationOutPort.deleteRoutingInstruction(hotelId, reservationIds));
  }

  @Test
  void updateBookerEmail_ShouldReturnOK() {
    //Arrange
    var validUpdateBookerEmailRequest = createValidUpdateBookerEmailRequest();

    when(ohipReservationClient.getReservations(any(), any())).thenReturn(
        Flux.just(createReservation()));
    Profile bookerProfile = createBookerProfile();
    when(updateBookerEmailOhipMapper.toDto(any(), any())).thenReturn(bookerProfile);
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any())).thenReturn(
        createChangeReservationDetails());
    when(ohipReservationClient.sendGetProfilesByProfileIds(any())).thenReturn(
        List.of(bookerProfile));
    when(bookerProfileOhipMapper.toModel(bookerProfile)).thenReturn(createBookerProfileType());
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(10);
    when(updateBookerReservationProfileOhipMapper.toDto(any(), any(), any(), any())).thenReturn(
        new ChangeReservation());
    //Act
    hotelReservationOutPort.updateBookerEmail(validUpdateBookerEmailRequest);
    //Assert

    assertDoesNotThrow(
        () -> hotelReservationOutPort.updateBookerEmail(validUpdateBookerEmailRequest));
  }

  @Test
  void createMemo_Success() {
    // Arrange
    var request = new CreateMemoRequest("HOTEL", Collections.singletonList("reservationId"),
        "memo");

    var memo =
        Memo.builder()
            .ids(Collections.singletonList(MemoId.builder().reservationId("reservationId")
                .memoIds(Collections.singletonList("memoId")).build()))
            .description("memo")
            .build();
    var memosResponse = MemosResponse.builder().memos(Collections.singletonList(memo)).build();

    when(memosOhipMapper.toDto(any(), any())).thenReturn(new ChangeReservation());
    when(memosOhipMapper.toModel(anyList())).thenReturn(memosResponse);
    when(ohipReservationClient.sendChangeReservationRequest(any(), any(), any())).thenReturn(
        Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.getReservationWithRoutingInstructions(anyString(),
        anyString())).thenReturn(
        new Reservation());

    // Act
    var response = hotelReservationOutPort.createMemo(request);

    // Assert
    assertNotNull(response);
    assertEquals("memo", response.getMemos().get(0).getDescription());
  }

  @Test
  void getMemos_Success() {
    // Arrange

    var memo =
        Memo.builder()
            .ids(Collections.singletonList(MemoId.builder().reservationId("reservationId")
                .memoIds(Collections.singletonList("memoId")).build()))
            .description("memo")
            .build();
    var memosResponse = MemosResponse.builder().memos(Collections.singletonList(memo)).build();

    when(memosOhipMapper.toModel(anyList())).thenReturn(memosResponse);
    when(ohipReservationClient.getReservationWithRoutingInstructions(anyString(),
        anyString())).thenReturn(
        new Reservation());

    // Act
    var response = hotelReservationOutPort.getMemos("hotelId", Set.of("reservationId"));

    // Assert
    assertNotNull(response);
    assertEquals("memo", response.getMemos().get(0).getDescription());
  }

  @Test
  void attachProfileToReservations_Success() {
    // Arrange
    when(attachReservationProfileRequestOhipMapper.toDto(anyString(), anyString()))
        .thenReturn(mockChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(eq("TEST"), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(any()))
        .thenReturn(List.of(new Profile()));
    when(companyProfileOhipMapper.toModel(any()))
        .thenReturn(ProfileType.builder()
            .profileId(ProfileIdResponse.builder()
                .id("123")
                .build())
            .build());

    // Act
    hotelReservationOutPort.attachProfileToReservations(AttachReservationProfileRequest.builder()
        .hotelId("TEST")
        .profileId("123")
        .reservationIds(Set.of("123", "456"))
        .build());

    // Assert
    verify(ohipReservationClient, times(2))
        .sendPutReservationsGuestRequest(eq("TEST"), anyString(), any());
  }

  @Test
  void attachProfileToReservations_NonExistingProfile() {
    // Arrange
    when(ohipReservationClient.sendGetProfilesByProfileIds(Set.of("123")))
        .thenReturn(Collections.EMPTY_LIST);

    // Act & Assert
    Assertions.assertThrows(HotelReservationException.class, () ->
        hotelReservationOutPort.attachProfileToReservations(
            AttachReservationProfileRequest.builder()
                .hotelId("TEST")
                .profileId("123")
                .reservationIds(Set.of("123", "456"))
                .build())
    );
  }

  @Test
  void updateReservationSingleCallSuccessWithoutSpecialRequest() throws IOException {
    BusinessItemsRequest businessItemsRequest = mockBusinessItemRequest();
    SpecialRequests specialRequests = mockSpecialRequests();
    specialRequests.setSpecialRequests(null);
    specialRequests.setBookingNotes(null);
    ReservationGuestRequest guestReservationRequest = createGuestReservationRequest(
        WB_LANGUAGE_CODE_EN);
    guestReservationRequest.setBookerProfileId("12345");
    guestReservationRequest.setCompanyProfileId("12345");
    ReservationPackagesRequest updateReservationPackageRequest = mockReservationPackagesRequest();
    ConfirmReservationRequest confirmReservationRequest = createConfirmPrePaidReservationRequest();
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getDistributionBookingFee())).thenReturn(false);
    when(ohipReservationClient.getReservation(guestReservationRequest.getHotelId(),
        guestReservationRequest.getStayingGuests().get(0).getReservationId()))
        .thenReturn(Mono.just(mockReservation()));
    when(specialReqRequestOhipMapper.toChangeReservationDto(any(), any(), any(), any())).thenReturn(
        mockChangeReservationWithComments());
    when(packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest)).thenReturn(
        new PackagesRequestOhipDto());
    when(ohipPackagesClient.getPackages(any(), any())).thenReturn(getPackagesFromOhip());
    when(ohipPackagesClient.getPackageGroups(
        packageGroupsRequestOhipMapper.toOhipDto(anyString(), anyString()))).thenReturn(
        mockPackageGroupsFromOhip());
    var packagesResponseOhipDto = ohipPackagesClient.getPackages(
        packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest),
        new LinkedMultiValueMap<>()).block();
    when(reservationPackagesOhipMapper.toModel(updateReservationPackageRequest, 0,
        packagesResponseOhipDto))
        .thenReturn(mockChangeReservation());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
    when(reservationGuestRequestOhipMapper.toDto(any(), any(),
        anyString(), anyString())).thenReturn(mockChangeReservation());
    when(businessItemsRequestMapper.toChangeReservationDto(any(),
        any(), anyList())).thenReturn(mockChangeReservationWithComments());
    when(confirmReservationRequestOhipMapper.toChangeReservationModel(
        confirmReservationRequest)).thenReturn(mockChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(
        confirmReservationRequest.getHotelId(),
        confirmReservationRequest.getReservationId(), mockChangeReservation())).thenReturn(
        createChangeReservationDetails());
    var reservationsDetailsOhip = ohipReservationClient.sendChangeReservationRequest(
        confirmReservationRequest.getHotelId(),
        confirmReservationRequest.getReservationId(), mockChangeReservation()).block();
    when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(reservationsDetailsOhip
        .getReservations())).thenReturn(mockConfirmReservationResponse());
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.just(mockReservation()));
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
        .thenReturn(createChangeReservationDetails());
    var response = hotelReservationOutPort.updateReservationSingleCall(businessItemsRequest,
        specialRequests,
        guestReservationRequest, updateReservationPackageRequest, confirmReservationRequest, true);

    assertNotNull(response);
    assertEquals("HOTELTEST", response.getHotelId());
    assertEquals("Reserved", response.getReservationStatus());
  }

  @Test
  void updateDistrReservationsUpdatingUpdatePackageNotPresent() {
    BusinessItemsRequest businessItemsRequest = new BusinessItemsRequest();
    SpecialRequests specialRequests = new SpecialRequests();
    var guestReservationRequest = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    ReservationPackagesRequest updateReservationPackageRequest = ReservationPackagesRequest.builder()
        .reservationsId(List.of("101")).build();
    ConfirmReservationRequest confirmReservationRequest = getConfirmReservationRequest();
    boolean shouldUpdatePackages = true;
    String error = "An error was returned by OHIP!Error during Packages request creation!";
    doReturn(Mono.just(createReservation())).when(ohipReservationClient)
        .getReservation(guestReservationRequest.getHotelId(),
            guestReservationRequest.getStayingGuests().get(0).getReservationId());

    when(ohipPackagesClient.getPackages(
        packagesRequestDtoOhipMapper.toDto(updateReservationPackageRequest),
        new LinkedMultiValueMap<>())).thenThrow(
        new HotelPackageException(ErrorCode.OHIP_GET_PACKAGES_EXCEPTION, error));
    //Act
    HotelPackageException exception = Assertions
        .assertThrows(HotelPackageException.class, () ->
            hotelReservationOutPort.updateReservationSingleCall(businessItemsRequest,
                specialRequests,
                guestReservationRequest, updateReservationPackageRequest, confirmReservationRequest,
                shouldUpdatePackages)
        );
    // Assert
    Assertions.assertEquals(error, exception.getMessage());
  }

  @Test
  void addAttachmentToReservation_ValidPdfWithinSizeLimit_ShouldReturnSuccess() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_GlobalFalseOverwriteTrue_ShouldReturnSuccess()
      throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setGlobal(false);
    request.setOverwriteExistingFile(true);
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_InvalidBase64_ShouldReturnFailure() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setFileAttachment("Invalid Base64");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Error", response.getStatus());
    assertEquals("File attachment is not a valid base64 string", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_NonPdfFile_ShouldReturnFailure() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setFileAttachment(
        Base64.getEncoder().encodeToString(new byte[]{0x00, 0x01, 0x02, 0x03, 0x04}));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Error", response.getStatus());
    assertEquals("File is not a valid PDF", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_FileTooLarge_ShouldReturnFailure() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    byte[] largeFile = new byte[11 * 1024 * 1024];
    largeFile[0] = 0x25;
    largeFile[1] = 0x50;
    largeFile[2] = 0x44;
    largeFile[3] = 0x46;
    largeFile[4] = 0x2D;
    request.setFileAttachment(Base64.getEncoder().encodeToString(largeFile));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Error", response.getStatus());
    assertEquals("File size exceeds the maximum limit of 10MB", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_LargeNonPdfFile_ShouldReturnPdfValidationFailure()
      throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    byte[] largeFile = new byte[11 * 1024 * 1024];
    Arrays.fill(largeFile, (byte) 0x00);
    request.setFileAttachment(Base64.getEncoder().encodeToString(largeFile));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Error", response.getStatus());
    assertEquals("File size exceeds the maximum limit of 10MB", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_OhipClientError_ShouldReturnFailure() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Error");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Error", response.getStatus());
    assertEquals("Error in adding attachment", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_OverwriteExistingFileNull_ShouldSetOverwriteToN()
      throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setOverwriteExistingFile(null);
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_OverwriteExistingFileTrue_ShouldSetOverwriteToY()
      throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setOverwriteExistingFile(true);
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_OverwriteExistingFileFalse_ShouldSetOverwriteToN()
      throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setOverwriteExistingFile(false);
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_GlobalYNNull_ShouldSetOverwriteToN() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setGlobal(null);
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_GlobalYNTrue_ShouldSetOverwriteToY() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setGlobal(true);
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_GlobalYNFalse_ShouldSetOverwriteToN() throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setGlobal(false);
    when(ohipReservationClient.addAttachmentToReservation(any())).thenReturn("Success");

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Success", response.getStatus());
    assertEquals("Attachment added successfully", response.getMessage());
  }

  @Test
  void addAttachmentToReservation_SmallNonPdfFile_ShouldReturnPdfValidationFailure()
      throws IOException {
    // Arrange
    ReservationFileAttachmentRequest request = OhipTestUtils.mockReservationFileAttachmentRequest();
    request.setFileAttachment(
        Base64.getEncoder().encodeToString(new byte[]{0x00, 0x01, 0x02, 0x03}));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.addAttachmentToReservation(request);

    // Assert
    assertEquals("Error", response.getStatus());
    assertEquals("File is not a valid PDF", response.getMessage());
  }

  @Test
  void saveReservationPreCheckIn_ShouldReturnSuccess() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    ChangeReservation mockChangeReservation = new ChangeReservation();

    when(ohipReservationClient.savePreCheckInStatus(any(PreCheckInReservation.class), eq("STUAIR"),
        eq("123456"))).thenReturn(mockStatus("1234"));
    when(reservationAccompanyingGuestRequestOhipMapper.toAddOrDeleteAlertModel(anyString(),
        anyString(), anyString(), isNull())).thenReturn(mockChangeReservation);
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Success", response.getStatus());
    assertEquals("Pre-CheckIn status saved successfully", response.getMessage());
    verify(ohipReservationClient, times(1)).savePreCheckInStatus(any(PreCheckInReservation.class),
        eq("STUAIR"), eq("123456"));
    verify(ohipReservationClient, times(1)).sendPutReservationsGuestRequest(anyString(),
        anyString(), any(ChangeReservation.class));
  }

  @Test
  void saveReservationPreCheckIn_WithGermanLanguageShouldReturnSuccess() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    request.setLanguage("DE");
    when(ohipReservationClient.savePreCheckInStatus(any(PreCheckInReservation.class), eq("STUAIR"),
        eq("123456"))).thenReturn(mockStatus("1234"));
    when(reservationAccompanyingGuestRequestOhipMapper.toAddOrDeleteAlertModel(anyString(),
        anyString(), anyString(), isNull())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Success", response.getStatus());
    assertEquals("Pre-CheckIn status saved successfully", response.getMessage());

    verify(ohipReservationClient, times(1)).savePreCheckInStatus(any(PreCheckInReservation.class),
        eq("STUAIR"), eq("123456"));
    verify(ohipReservationClient, times(1)).sendPutReservationsGuestRequest(anyString(),
        anyString(), any(ChangeReservation.class));
  }

  @Test
  void saveReservationPreCheckIn_ShouldReturnError() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    PreCheckInReservation mockPreCheckInReservation = OhipTestUtils.mockReservationPreCheckInInfo();
    when(ohipReservationClient.savePreCheckInStatus(mockPreCheckInReservation, "STUAIR",
        "123456")).thenReturn(new Status());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("Error in saving Pre-CheckIn status", response.getMessage());
    verify(ohipReservationClient, times(1)).savePreCheckInStatus(mockPreCheckInReservation,
        "STUAIR", "123456");
  }

  @Test
  void saveReservationPreCheckIn_PreCheckInResponseIsNull_ShouldReturnError() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    when(ohipReservationClient.savePreCheckInStatus(any(), anyString(), anyString())).thenReturn(
        null);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("Error in saving Pre-CheckIn status", response.getMessage());
    verify(ohipReservationClient, times(1)).savePreCheckInStatus(any(), anyString(), anyString());
  }

  @Test
  void saveReservationPreCheckIn_LinksIsNull_ShouldReturnError() {
    // Arrange
    PreCheckInReservation mockPreCheckInReservation = OhipTestUtils.mockReservationPreCheckInInfo();
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    request.setArrivalTime(mockPreCheckInReservation.getReservation().getPreCheckInDetails().getArrival()
        .getArrivalTime());
    Status mockStatus = new Status();
    mockStatus.setLinks(null);
    when(ohipReservationClient.savePreCheckInStatus(mockPreCheckInReservation, "STUAIR",
        "123456")).thenReturn(mockStatus);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("Error in saving Pre-CheckIn status", response.getMessage());
    verify(ohipReservationClient, times(1)).savePreCheckInStatus(mockPreCheckInReservation,
        "STUAIR", "123456");
  }

  @Test
  void saveReservationPreCheckIn_LinksIsEmpty_ShouldReturnError() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();

    Status mockStatus = new Status();
    mockStatus.setLinks(new ArrayList<>());

    doReturn(mockStatus)
        .when(ohipReservationClient)
        .savePreCheckInStatus(any(PreCheckInReservation.class), eq("STUAIR"), eq("123456"));
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("Error in saving Pre-CheckIn status", response.getMessage());

    verify(ohipReservationClient, times(1))
        .savePreCheckInStatus(any(PreCheckInReservation.class), eq("STUAIR"), eq("123456"));
  }

  @Test
  void saveReservationPreCheckIn_PreCheckInResponseHasLinks_ShouldReturnSuccess() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();

    // Use argument matchers
    when(ohipReservationClient.savePreCheckInStatus(any(PreCheckInReservation.class), eq("STUAIR"),
        eq("123456")))
        .thenReturn(mockStatus("12345"));
    when(reservationAccompanyingGuestRequestOhipMapper.toAddOrDeleteAlertModel(anyString(),
        anyString(), anyString(), isNull()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Success", response.getStatus());
    assertEquals("Pre-CheckIn status saved successfully", response.getMessage());
    verify(ohipReservationClient, times(1)).savePreCheckInStatus(any(PreCheckInReservation.class),
        eq("STUAIR"), eq("123456"));
    verify(ohipReservationClient, times(1)).sendPutReservationsGuestRequest(anyString(),
        anyString(), any(ChangeReservation.class));
  }

  @Test
  void saveReservationPreCheckIn_WhenClientReturnsNull_ShouldReturnError() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    when(ohipReservationClient.savePreCheckInStatus(any(PreCheckInReservation.class), anyString(),
        anyString())).thenReturn(null);
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("Error in saving Pre-CheckIn status", response.getMessage());
    verify(ohipReservationClient, times(1)).savePreCheckInStatus(any(PreCheckInReservation.class),
        anyString(), anyString());
  }

  @Test
  void saveReservationPreCheckIn_PreCheckInResponseHasNoLinks_ShouldReturnError() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    var mockStatusResponse = new Status();
    mockStatusResponse.setLinks(Collections.emptyList());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(false);

    // Use argument matchers
    when(ohipReservationClient.savePreCheckInStatus(any(PreCheckInReservation.class), anyString(),
        anyString())).thenReturn(mockStatusResponse);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Error", response.getStatus());
    assertEquals("Error in saving Pre-CheckIn status", response.getMessage());
    verify(ohipReservationClient, times(1)).savePreCheckInStatus(any(PreCheckInReservation.class),
        anyString(), anyString());
    verify(ohipReservationClient, times(0)).sendPutReservationsGuestRequest(anyString(),
        anyString(), any(ChangeReservation.class));
  }

  @Test
  void deleteReservationPreCheckIn_Success() {
    // Arrange
    String hotelId = "FRAMTI";
    String reservationId = "1875583";

    // Act
    hotelReservationOutPort.deleteReservationPreCheckIn(hotelId, reservationId);

    // Assert
    verify(ohipReservationClient, times(1))
        .deleteReservationPreCheckIn(hotelId, reservationId);
  }

  @Test
  void deleteReservationPreCheckIn_WithReservationIdIsNull() {
    // Arrange
    String hotelId = "FRAMTI";
    String reservationId = null;
    String error = "Error while trying to delete reservation pre-checkIn for "
        + "hotelId=FRAMTI and reservationId=null";

    doThrow(new HotelReservationException(ErrorCode.OHIP_DELETE_RESERVATION_EXCEPTION, error))
        .when(ohipReservationClient).deleteReservationPreCheckIn(hotelId, null);

    // Act & Assert
    HotelReservationException exception = assertThrows(
        HotelReservationException.class,
        () -> hotelReservationOutPort.deleteReservationPreCheckIn(hotelId, reservationId)
    );

    assertTrue(exception.getMessage().contains(error));
  }

  @Test
  void deleteRegCardAttachment_Success() throws IOException {
    // Arrange
    String hotelId = "FRAMTI";
    String reservationId = "1875583";
    String attachmentId = "57659";

    Reservation mockReservation = mockReservationAttachment();
    when(ohipReservationClient.sendGetReservationsByReservationId(hotelId, reservationId))
        .thenReturn(mockReservation);
    when(ohipReservationClient.sendPutReservationsGuestRequest(eq(hotelId), eq(reservationId),
        any()))
        .thenReturn(Mono.empty());

    // Act
    hotelReservationOutPort.deleteRegCardAttachment(hotelId, reservationId);

    // Assert
    verify(ohipReservationClient, times(1))
        .deleteReservationAttachment(hotelId, reservationId, attachmentId);
    verify(ohipReservationClient, times(1))
        .sendPutReservationsGuestRequest(eq(hotelId), eq(reservationId), any());
  }

  @Test
  void deleteRegCardAttachment_WithReservationAttachmentIsNull() {
    // Arrange
    String hotelId = "FRAMTI";
    String reservationId = "1875583";

    when(ohipReservationClient.sendGetReservationsByReservationId(hotelId, reservationId))
        .thenReturn(null);

    // Act & Assert
    HotelReservationException exception = assertThrows(
        HotelReservationException.class,
        () -> hotelReservationOutPort.deleteRegCardAttachment(hotelId, reservationId)
    );

    assertTrue(exception.getMessage().contains("Reservation attachment not found"));
    verify(ohipReservationClient, never())
        .deleteReservationAttachment(eq(hotelId), eq(reservationId), anyString());
    verify(ohipReservationClient, never())
        .sendPutReservationsGuestRequest(eq(hotelId), eq(reservationId), any());
  }

  @Test
  void deleteRegCardAttachment_WithAttachmentNotFound() throws IOException {
    // Arrange
    String hotelId = "FRAMTI";
    String reservationId = "1875583";

    var mockAttachment = mockReservationAttachment();
    mockAttachment.getReservations().getReservation().get(0)
        .setAttachments(Collections.emptyList());
    when(ohipReservationClient.sendGetReservationsByReservationId(hotelId, reservationId))
        .thenReturn(mockAttachment);
    when(ohipReservationClient.sendPutReservationsGuestRequest(eq(hotelId), eq(reservationId),
        any())).thenReturn(Mono.empty());

    // Act
    hotelReservationOutPort.deleteRegCardAttachment(hotelId, reservationId);

    // Assert
    verify(ohipReservationClient, never())
        .deleteReservationAttachment(eq(hotelId), eq(reservationId), anyString());
    verify(ohipReservationClient, times(1))
        .sendPutReservationsGuestRequest(eq(hotelId), eq(reservationId), any());
  }

  @Test
  void deleteRegCardAttachment_WithReservationNotFound() throws IOException {
    // Arrange
    String hotelId = "HOTELTEST";
    String reservationId = "12345";

    var mockAttachment = mockReservationAttachment();
    mockAttachment.getReservations().setReservation(Collections.emptyList());
    when(ohipReservationClient.sendGetReservationsByReservationId(hotelId, reservationId))
        .thenReturn(mockAttachment);

    // Act
    hotelReservationOutPort.deleteRegCardAttachment(hotelId, reservationId);

    // Assert
    verify(ohipReservationClient, never())
        .deleteReservationAttachment(eq(hotelId), eq(reservationId), anyString());
    verify(ohipReservationClient, never())
        .sendPutReservationsGuestRequest(eq(hotelId), eq(reservationId), any());
  }

  @Test
  void deleteRegCardAttachment_WithNoPreCheckInAlert() throws IOException {
    // Arrange
    String hotelId = "FRAMTI";
    String reservationId = "1875583";
    String attachmentId = "57659";

    var mockReservation = mockReservationAttachment();
    mockReservation.getReservations().getReservation().get(0).setAlerts(Collections.emptyList());
    when(ohipReservationClient.sendGetReservationsByReservationId(hotelId, reservationId))
        .thenReturn(mockReservation);

    doNothing().when(ohipReservationClient)
        .deleteReservationAttachment(hotelId, reservationId, attachmentId);

    // Act
    hotelReservationOutPort.deleteRegCardAttachment(hotelId, reservationId);

    // Assert
    verify(ohipReservationClient, times(1))
        .deleteReservationAttachment(hotelId, reservationId, attachmentId);
    verify(ohipReservationClient, never())
        .sendPutReservationsGuestRequest(eq(hotelId), eq(reservationId), any());
  }

  @Test
  void linkReservationToLeisureCustomer__Success() {
    //Arrange
    var linkReservationToLeisureCustomerRequest = ReservationTestUtils.mockLinkReservationToLeisureCustomerRequest();
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    when(linkReservationToLeisureCustomerRequestOhipMapper.toDto(any())).thenReturn(
        new ChangeReservation());
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(),
        any())).thenReturn(
        mockChangeReservationResponse());

    //Act
    hotelReservationOutPort.linkReservationToLeisureCustomer(
        linkReservationToLeisureCustomerRequest);

    //Assert
    verify(ohipReservationClient, times(1))
        .sendChangeReservationRequest(anyString(), anyString(), any());
  }

  @Test
  void updateReservationPreferences_Success() {
    // Arrange
    String hotelId = "FRAMTI";
    String reservationId = "1875583";
    ReservationPreferencesRequest preferencesRequest = new ReservationPreferencesRequest();
    preferencesRequest.setHotelId(hotelId);
    preferencesRequest.setReservationsIds(List.of(reservationId));
    preferencesRequest.setPreferencesCollections(List.of());
    var changeReservationRequest = mockChangeReservation(reservationId);

    when(updatePreferencesRequestOhipMapper.toDto(preferencesRequest, reservationId))
        .thenReturn(changeReservationRequest);
    when(ohipReservationClient.sendChangeReservationRequest(eq(hotelId), eq(reservationId), any()))
        .thenReturn(Mono.empty());

    // Act
    hotelReservationOutPort.updateReservationPreferences(preferencesRequest);

    // Assert
    verify(ohipReservationClient, times(1))
        .sendChangeReservationRequest(hotelId, reservationId, changeReservationRequest);
  }

  @Test
  void getReservationById__WithPreferences__ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithPreferences())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(frontDeskClient.getCreditCardInfo(any(), any())).thenReturn(
        mockCreditCardInfoUserDefinedCardType());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationByIdWithUserDefinedCardType());

    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    assertNotNull(reservationByBasketRefResponse.getReservationByIdList().get(0).getPreferences());
    assertEquals("Events",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPreferences().get(0)
            .getPreferenceType());
    assertEquals("test1",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getPreferences().get(0)
            .getCode());
  }
  
  @Test
  void createHotelReservation__shouldThrowException__whenFixedRateUpdateFails() {
    //Arrange
    String error = "Error while trying to change reservation for ";
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    
    var validHotelReservationRequest = createValidHotelReservationRequest(true);
    when(ohipReservationClient.sendCreateReservationRequest(any(), any())).thenReturn(
        Mono.just(reservationStatusOhipDto()));
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(true);
    when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any())).thenThrow(
        new HotelReservationException(OHIP_CHANGE_RESERVATION_EXCEPTION, error)
    );

    //Act
    HotelReservationException exception = Assertions
        .assertThrows(HotelReservationException.class, () ->
            hotelReservationOutPort.createReservation(validHotelReservationRequest)
        );
    // Assert
    Assertions.assertEquals(exception.getMessage(), error);
  }
  
  @Test
  void createReservation_verifyChangeReservationNotCalled_whenFlagIsOff() {
    
    when(reservationOhipProperties.getMaxConcurrency()).thenReturn(1);
    
    var validHotelReservationRequest = createValidHotelReservationRequest(true);
    when(ohipReservationClient.sendCreateReservationRequest(any(), any())).thenReturn(
        Mono.just(reservationStatusOhipDto()));
    when(ohipReservationClient.getReservations(anyString(), any())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservation())));
    when(reservationResponseOhipMapper.toReservationResponseModel(any())).thenReturn(
        mockReservationResponse());
    when(rulesAgentClient.getBookingChannelInfo(any())).thenReturn(
        mockBookingChannelInfoResponse());
    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getFixedRate())).thenReturn(false);
    
    //Act
    hotelReservationOutPort.createReservation(validHotelReservationRequest);
    
    verify(ohipReservationClient, times(0)).sendChangeReservationRequest(anyString(), anyString(), any());
    
  }

  @Test
  void confirmWindow3Reservation__ShouldReturnOk() throws IOException {

    //Arrange
    Reservation reservation = mockReservationWithPaymentCard();
    RoutingInfoType routingInfoType = new RoutingInfoType();
    RoutingInfoTypeFolio routingInfoTypeFolio = new RoutingInfoTypeFolio();
    routingInfoTypeFolio.setFolioWindowNo(3);
    routingInfoTypeFolio.setPaymentMethod(null);
    routingInfoType.setFolio(routingInfoTypeFolio);
    reservation.getReservations().getReservation().get(0).setRoutingInstructions(Collections.singletonList(routingInfoType));

    final ObjectMapper mapper = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    final Reservation afterUpdate = mapper.readValue(
        mapper.writeValueAsBytes(reservation),
        Reservation.class
    );

    afterUpdate.getReservations().getReservation().get(0).getReservationPolicies()
        .getDepositPolicies().get(0).setComments("Updated");

    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
            .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
                      () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
              .thenReturn(true);
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
              Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
              .thenReturn(mockDepositFolioConfirmationRequest());

      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
              .thenReturn(new PostedDepositFolio());

      when(ohipReservationClient.getReservation(anyString(), anyString()))
          .thenReturn(Mono.just(reservation))
          .thenReturn(Mono.just(afterUpdate));

      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
              any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(ohipReservationClient.getReservationAmounts(any(), any()))
              .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
              Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
              Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
              anyString())).thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
              .thenReturn(createChangeReservationDetails());
      when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
      when(reservationOhipProperties.getNonGuaranteeCode()).thenReturn("NON");
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(true);

      //Act
      var reservationsDetailsResponse = hotelReservationOutPort.confirmReservation(
              createConfirmPrePaidReservationRequest());

      //Assert
      assertNotNull(reservationsDetailsResponse);
      assertEquals("HOTELTEST", reservationsDetailsResponse.getHotelId());
      assertEquals("Reserved", reservationsDetailsResponse.getReservationStatus());
      verify(ohipReservationClient, atLeastOnce()).sendChangeReservationRequest(anyString(),
              anyString(), any());
    }
  }

  @Test
  void confirmWindow3ReservationDepositFolioOnHoldFalse__ShouldReturnOk() throws IOException {

    //Arrange
    Reservation reservation = mockReservationWithPaymentCard();
    RoutingInfoType routingInfoType = new RoutingInfoType();
    RoutingInfoTypeFolio routingInfoTypeFolio = new RoutingInfoTypeFolio();
    routingInfoTypeFolio.setFolioWindowNo(3);
    routingInfoTypeFolio.setPaymentMethod(null);
    routingInfoType.setFolio(routingInfoTypeFolio);
    reservation.getReservations().getReservation().get(0).setRoutingInstructions(Collections.singletonList(routingInfoType));

    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
          Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());

      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenReturn(new PostedDepositFolio());

      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(reservation));

      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
          Flux.fromIterable(Collections.singletonList(mockReservation())));
      when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
          Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
          anyString())).thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());
      when(reservationOhipProperties.getNonGuaranteeCode()).thenReturn("NON");
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);

      //Act
      var reservationsDetailsResponse = hotelReservationOutPort.confirmReservation(
          createConfirmPrePaidReservationRequest());

      //Assert
      assertNotNull(reservationsDetailsResponse);
      assertEquals("HOTELTEST", reservationsDetailsResponse.getHotelId());
      assertEquals("Reserved", reservationsDetailsResponse.getReservationStatus());
      verify(ohipReservationClient, atLeastOnce()).sendChangeReservationRequest(anyString(),
          anyString(), any());
    }
  }

  // -------------------------------------------------------------------------
  // sendDepositFoliosWithVerification tests
  // -------------------------------------------------------------------------

  @Test
  void confirmPrePaidReservation_depositFolioCreationVerificationFlagOff_ShouldCallSendDirectly()
      throws IOException {
    // Arrange
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(mockReservationWithPaymentCard()));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      lenient().when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenReturn(new PostedDepositFolio());
      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(false);

      // Act
      hotelReservationOutPort.confirmReservation(createConfirmPrePaidReservationRequest());

      // Assert: direct call to sendDepositFoliosRequest, no folio check
      verify(ohipReservationClient, times(1)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, never()).getFoliosAciAmount(anyString(), anyString());
    }
  }

  @Test
  void confirmPrePaidReservation_depositFolioCreationVerificationFlagOn_InitialCallSucceeds_ShouldNotVerifyFolio()
      throws IOException {
    // Arrange
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(mockReservationWithPaymentCard()));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      lenient().when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenReturn(new PostedDepositFolio());
      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(true);

      // Act
      hotelReservationOutPort.confirmReservation(createConfirmPrePaidReservationRequest());

      // Assert: single call, no folio verification needed
      verify(ohipReservationClient, times(1)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, never()).getFoliosAciAmount(anyString(), anyString());
    }
  }

  @Test
  void confirmPrePaidReservation_depositFolioCreationVerificationFlagOn_InitialCallFails_FolioAlreadyCreated_ShouldNotRetry()
      throws IOException {
    // Arrange
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      var originalException = new HotelReservationException(
          ErrorCode.OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION, "OPERA timeout");
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(mockReservationWithPaymentCard()));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      lenient().when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      doThrow(originalException).when(ohipReservationClient)
          .sendDepositFoliosRequest(anyString(), anyString(), any());
      when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString()))
          .thenReturn(mockFoliosResponseWithPaymentMethod("DVA"));
      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(true);

      // Act
      var result = hotelReservationOutPort.confirmReservation(createConfirmPrePaidReservationRequest());

      // Assert: folio check performed, no retry of sendDepositFoliosRequest
      assertNotNull(result);
      verify(ohipReservationClient, times(1)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, times(1)).getFoliosAciAmount(anyString(), anyString());
    }
  }

  @Test
  void confirmPrePaidReservation_depositFolioCreationVerificationFlagOn_InitialCallFails_FolioNotFound_ShouldRetry()
      throws IOException {
    // Arrange
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      var originalException = new HotelReservationException(
          ErrorCode.OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION, "OPERA timeout");
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(mockReservationWithPaymentCard()));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      lenient().when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      // Initial call fails; retry succeeds
      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenThrow(originalException)
          .thenReturn(new PostedDepositFolio());
      // Folio not found: no window matches DVA payment method
      when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString()))
          .thenReturn(mockFoliosResponseWithPaymentMethod("CA"));
      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(true);

      // Act
      var result = hotelReservationOutPort.confirmReservation(createConfirmPrePaidReservationRequest());

      // Assert: folio check performed, retry attempted
      assertNotNull(result);
      verify(ohipReservationClient, times(2)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, times(1)).getFoliosAciAmount(anyString(), anyString());
    }
  }

  @Test
  void confirmPrePaidReservation_depositFolioCreationVerificationFlagOn_BothCallsFail_ShouldRethrowOriginalException()
      throws IOException {
    // Arrange
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      var originalException = new HotelReservationException(
          ErrorCode.OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION, "OPERA timeout");
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(mockReservationWithPaymentCard()));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      // Both initial and retry fail
      doThrow(originalException).when(ohipReservationClient)
          .sendDepositFoliosRequest(anyString(), anyString(), any());
      // Folio not found: no window matches DVA payment method
      when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString()))
          .thenReturn(mockFoliosResponseWithPaymentMethod("CA"));
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(true);

      // Act + Assert: original exception is re-thrown
      ConfirmReservationRequest confirmPrePaidReservationRequest = createConfirmPrePaidReservationRequest();
      var thrown = assertThrows(HotelReservationException.class, () ->
          hotelReservationOutPort.confirmReservation(confirmPrePaidReservationRequest));

      assertEquals(originalException.getMessage(), thrown.getMessage());
      verify(ohipReservationClient, times(2)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, times(1)).getFoliosAciAmount(anyString(), anyString());
    }
  }

  @Test
  void confirmPrePaidReservation_depositFolioCreationVerificationFlagOn_InitialCallFails_FoliosCheckFails_ShouldRetry()
      throws IOException {
    // Arrange
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));
    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      var originalException = new HotelReservationException(
          ErrorCode.OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION, "OPERA timeout");
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString())).thenReturn(
          Mono.just(mockReservationWithPaymentCard()));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      lenient().when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      // Initial call fails; retry succeeds
      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenThrow(originalException)
          .thenReturn(new PostedDepositFolio());
      // getFoliosAciAmount itself throws — treated as folio not found, retry should be attempted
      doThrow(new HotelReservationException(ErrorCode.OHIP_GET_FOLIOS_ACI_AMOUNT_EXCEPTION,
          "timeout"))
          .when(ohipReservationClient).getFoliosAciAmount(anyString(), anyString());
      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(false);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(true);

      // Act
      var result = hotelReservationOutPort.confirmReservation(createConfirmPrePaidReservationRequest());

      // Assert: verification failed, retry was still attempted and succeeded
      assertNotNull(result);
      verify(ohipReservationClient, times(2)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, times(1)).getFoliosAciAmount(anyString(), anyString());
    }
  }

  @Test
  void confirmPrePaidReservation_nonLegacyPath_depositFolioCreationVerificationFlagOn_InitialCallFails_FolioAlreadyCreated_ShouldNotRetry()
      throws IOException {
    // Arrange - non-legacy path (depositFolioPostAfterDisableOnHold = true)
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    lenient().when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));
    final var reservation = mockReservationWithPaymentCard();
    final var updatedReservation = mockReservationWithPaymentCard();
    updatedReservation.getReservations().getReservation().get(0)
        .getReservationPolicies().getDepositPolicies().get(0).setComments("Updated");

    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      var originalException = new HotelReservationException(
          ErrorCode.OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION, "OPERA timeout");
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString()))
          .thenReturn(Mono.just(reservation))
          .thenReturn(Mono.just(updatedReservation));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      doThrow(originalException).when(ohipReservationClient)
          .sendDepositFoliosRequest(anyString(), anyString(), any());
      when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString()))
          .thenReturn(mockFoliosResponseWithPaymentMethod("DVA"));
      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(true);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(true);

      // Act
      var result = hotelReservationOutPort.confirmReservation(createConfirmPrePaidReservationRequest());

      // Assert: folio already existed, no retry
      assertNotNull(result);
      verify(ohipReservationClient, times(1)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, times(1)).getFoliosAciAmount(anyString(), anyString());
    }
  }

  @Test
  void confirmPrePaidReservation_nonLegacyPath_depositFolioCreationVerificationFlagOn_InitialCallFails_FolioNotFound_ShouldRetry()
      throws IOException {
    // Arrange - non-legacy path (depositFolioPostAfterDisableOnHold = true)
    // Initialize feature flag Feature objects with distinct keys to avoid null-argument stub conflicts
    var ffOnHold = new FeatureFlag.Feature();
    ffOnHold.setKey("deposit_folio_post_after_disable_on_hold");
    featureFlag.setDepositFolioPostAfterDisableOnHold(ffOnHold);
    var ffVerification = new FeatureFlag.Feature();
    ffVerification.setKey("deposit_folio_creation_verification");
    featureFlag.setDepositFolioCreationVerification(ffVerification);
    lenient().when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.emptyList()));
    final var reservation = mockReservationWithPaymentCard();
    final var updatedReservation = mockReservationWithPaymentCard();
    updatedReservation.getReservations().getReservation().get(0)
        .getReservationPolicies().getDepositPolicies().get(0).setComments("Updated");

    try (MockedStatic<ReservationPaymentMethodUtils> utilities = Mockito
        .mockStatic(ReservationPaymentMethodUtils.class)) {

      var originalException = new HotelReservationException(
          ErrorCode.OHIP_SEND_DEPOSIT_FOLIOS_EXCEPTION, "OPERA timeout");
      utilities.when(
              () -> ReservationPaymentMethodUtils.hasHotelPaymentMethodNonDigital(any(), any()))
          .thenReturn(true);
      when(ohipReservationClient.getReservation(anyString(), anyString()))
          .thenReturn(Mono.just(reservation))
          .thenReturn(Mono.just(updatedReservation));
      when(rulesAgentClient.getVatCodes(anyString(), anyList())).thenReturn(mockVatRules());
      when(depositFoliosRequestMapper.toDepositFolioRequestModel(any()))
          .thenReturn(mockDepositFolioConfirmationRequest());
      when(depositFoliosRequestOhipMapper.toDepositFolioCriteriaModel(
          any(DepositFolioRequest.class), any(), any(), any(), any()))
          .thenReturn(mockDepositFolioCriteriaWithPaymentMethod("DVA"));
      when(ohipReservationClient.getReservationAmounts(any(), any()))
          .thenReturn(Mono.zip(Mono.just("100100"), Mono.just(mockReservationAmounts())));
      when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(), anyString()))
          .thenReturn(mockRateInfoDetails());
      when(ohipReservationClient.sendChangeReservationRequest(anyString(), anyString(), any()))
          .thenReturn(createChangeReservationDetails());
      // Initial call fails; retry succeeds
      when(ohipReservationClient.sendDepositFoliosRequest(anyString(), anyString(), any()))
          .thenThrow(originalException)
          .thenReturn(new PostedDepositFolio());
      // Folio not found: no window matches DVA
      when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString()))
          .thenReturn(mockFoliosResponseWithPaymentMethod("CA"));
      when(confirmationResponseOhipMapper.toConfirmReservationResponseModel(
          any(HotelReservationType.class))).thenReturn(mockConfirmReservationResponse());
      when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioPostAfterDisableOnHold()))
          .thenReturn(true);
      when(unleashWrapper.isEnabled(featureFlag.getDepositFolioCreationVerification()))
          .thenReturn(true);

      // Act
      var result = hotelReservationOutPort.confirmReservation(createConfirmPrePaidReservationRequest());

      // Assert: folio not found, retry attempted
      assertNotNull(result);
      verify(ohipReservationClient, times(2)).sendDepositFoliosRequest(anyString(), anyString(),
          any());
      verify(ohipReservationClient, times(1)).getFoliosAciAmount(anyString(), anyString());
    }
  }
  // -------------------------------------------------------------------------

  @Test
  void isProfileUpdated_returnTrue() {
    String originalProfile = "1234";
    String tempProfile = "5678";

    when(ohipReservationClient.sendGetProfilesByProfileIds(Set.of(originalProfile))).thenReturn(
        mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "tester@testerson.ro", "1234567"));
    when(ohipReservationClient.sendGetProfilesByProfileIds(Set.of(tempProfile))).thenReturn(
        mockProfileToCompare("5678", "Mr", "Tester", "Testerson", "tester@testerson.ro", "1234567"));

    //Act
    var response = hotelReservationOutPort.isProfileUpdated(HOTEL_ID, originalProfile, tempProfile);

    verify(ohipReservationClient, times(1)).sendUpdateProfileRequest(anyString(), any());
    assertTrue(response);
  }

  @Test
  void isProfileUpdated_returnFalse_differentAddress() {
    String originalProfile = "1234";
    String tempProfile = "5678";

    when(ohipReservationClient.sendGetProfilesByProfileIds(Set.of(originalProfile))).thenReturn(
        mockProfileToCompare("1234", "Mr", "Tester", "Testerson", "tester@testerson.ro", "12346"));
    when(ohipReservationClient.sendGetProfilesByProfileIds(Set.of(tempProfile))).thenReturn(
        mockProfileToCompare("5678", "Mr", "Tester", "Testerson", "tester@testerson.ro", "123456789"));

    //Act
    var response = hotelReservationOutPort.isProfileUpdated(HOTEL_ID, originalProfile, tempProfile);

    verify(ohipReservationClient, times(0)).sendUpdateProfileRequest(anyString(), any());
    assertFalse(response);
  }

  @Test
  void isProfileUpdated_returnFalse_differentGuestDetails() {
    String originalProfile = "1234";
    String tempProfile = "5678";

    when(ohipReservationClient.sendGetProfilesByProfileIds(Set.of(originalProfile))).thenReturn(
        mockProfileToCompare("1234", "Mr", "Tester1", "Testerson", "tester@testerrrrrrson.ro", "1234567"));
    when(ohipReservationClient.sendGetProfilesByProfileIds(Set.of(tempProfile))).thenReturn(
        mockProfileToCompare("5678", "Mr", "Tester", "Testerson", "tester@testerson.ro", "1234567"));

    //Act
    var response = hotelReservationOutPort.isProfileUpdated(HOTEL_ID, originalProfile, tempProfile);

    verify(ohipReservationClient, times(0)).sendUpdateProfileRequest(anyString(), any());
    assertFalse(response);
  }

  @Test
  void updateReservationAlerts_success() {
    //Arrange
    var reservationId = Set.of(RESERVATION_ID);
    var updateUdfRequest = new UpdateReservationAlertsRequest();
    updateUdfRequest.setHotelId(HOTEL_ID);
    updateUdfRequest.setReservationIds(reservationId);
    updateUdfRequest.setAlerts(List.of(new Alert("id", "area", "code",
        "description", true, false)));

    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.empty());

    when(reservationAlertMapper.toChangeReservationAlertDto(anyString(), any(), anyList())).thenReturn(
        new ChangeReservation());

    //Act
    hotelReservationOutPort.updateReservationAlerts(updateUdfRequest);

    //Assert
    verify(ohipReservationClient, times(1)).sendPutReservationsGuestRequest(any(), anyString(), any());

  }

  @Test
  void getReservationAmounts_Successful() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationAmounts = hotelReservationOutPort.getReservationAmounts("FRAMTI", Set.of("1231231") );

    assertNotNull(reservationAmounts);
    assertEquals(100, reservationAmounts.getOutStandingCostOfStay().intValue());
  }

  @Test
  void getReservationAmountsWithACI_Successful() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_WITH_ACI_RESPONSE_JSON));

    //Act
    var reservationAmounts = hotelReservationOutPort.getReservationAmounts("FRAMTI", Set.of("1231231") );

    assertNotNull(reservationAmounts);
    assertEquals(0, reservationAmounts.getOutStandingCostOfStay().intValue());
  }



  @Test
  void updateAbsoluteDeadLine__Success() {
    //Arrange
    var hotelId = "FRAMTI";
    var reservationIds = Set.of("123123");
    var absoluteDeadline = "2011-12-03T10:15:30+01:00[Europe/Bucharest]";
    var updateCancellationPoliciesRequest = UpdateCancellationPoliciesRequest.builder()
        .reservationIds(new ArrayList<>(reservationIds))
        .hotelId(hotelId)
        .absoluteDeadline(absoluteDeadline).build();
    when(ohipReservationClient.getReservations(hotelId, reservationIds)).thenReturn(Flux.fromIterable(
        List.of(mockReservation())));
    //Act
    hotelReservationOutPort.updateAbsoluteDeadline(updateCancellationPoliciesRequest);
    //Assert
    verify(hotelReservationOutPort, timeout(10000).times(1))
        .updateCancellationPolicy(any());
  }

  @ParameterizedTest
  @CsvSource({"1,0",","})
  void createReservationGuest_PreCheckInTrue_NoAccGuestsAllowed_ExceptionThrown(Integer adults,
      Integer children) {
    // Arrange
    ReservationGuestRequest guestReservationRequest = createAccompanyingGuestReservationRequest(
        true);
    List<StayingGuest> stayingGuests = new ArrayList<>(guestReservationRequest.getStayingGuests());
    stayingGuests.forEach(guest -> guest.setReservationId(RESERVATION_ID));
    List<StayingGuest> mockGuests = mockStayingGuestsDetails();
    mockGuests.forEach(guest -> guest.setReservationId(RESERVATION_ID));
    stayingGuests.addAll(mockGuests);
    guestReservationRequest.setStayingGuests(stayingGuests);
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithGuestNumber(RESERVATION_ID,adults,children))));

    // Act & Assert
    HotelReservationException exception = assertThrows(HotelReservationException.class, () -> {
      hotelReservationOutPort.createReservationGuest(guestReservationRequest);
    });
    assertEquals("Max Accompanying Guests allowed is: 0", exception.getMessage());
  }

  @Test
  void getReservationById__WithAlerts__ShouldReturnOk() throws IOException {
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(mockReservationWithAlerts())));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(frontDeskClient.getCreditCardInfo(any(), any())).thenReturn(
        mockCreditCardInfoUserDefinedCardType());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(mockReservationByIdWithUserDefinedCardType());

    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, false);

    //assert
    var alerts = reservationByBasketRefResponse.getReservationByIdList().get(0).getAlerts();
    assertNotNull(alerts);
    assertEquals("CIOL", alerts.get(0).getCode());
    assertEquals("CIOL", alerts.get(1).getCode());
    assertNull(alerts.get(1).getArea());
    assertNull(alerts.get(2).getArea());
    assertNull(alerts.get(2).getCode());

  }

  @Test
  void updateBusinessItemsBookingHasAlerts__Success() {
    //Arrange
    var businessItemRequest = mockBusinessItemRequest();
    when(ohipReservationClient.getReservations(anyString(), anySet()))
        .thenReturn(Flux.fromIterable(Collections.singletonList(mockReservationWithAlerts())));
    when(ohipReservationClient.sendChangeReservationRequest("HOTELCODE", "12345", null))
        .thenReturn(mockChangeDiscountReservationResponse());
    when(rulesAgentClient.getBusinessAllowances()).thenReturn(mockAllowances());

    //Act
    hotelReservationOutPort.updateBusinessItems(businessItemRequest);

    //Assert
    assertDoesNotThrow(() -> hotelReservationOutPort.updateBusinessItems(businessItemRequest));
  }

  @Test
  void getReservationById__roomStay_WithPromotionCode_ShouldReturnOk() throws IOException {
    var reservation = mockReservation();
    var reservationById = mockReservationById();
    reservationById.getRoomStay().setPromotionCode("ST10R");
    //Arrange
    when(ohipReservationClient.getReservations(anyString(), anySet())).thenReturn(
        Flux.fromIterable(Collections.singletonList(reservation)));
    when(ohipReservationClient.getHotelConfig(anyString())).thenReturn(
        mockHotelDetails());
    when(ohipReservationClient.getReservationAmounts(anyString(), anyString())).thenReturn(
        Mono.zip(Mono.just("TestReservationId"), Mono.just(mockReservationAmounts())));
    when(ohipReservationClient.getRateInfo(anyString(), anyString(), anyString(),
        anyString())).thenReturn(mockRateInfoDetails());
    when(reservationResponseOhipMapper.toReservationByBasketRefResponseModel(
        anyList(), anyMap(), anyMap(), any(), any(), any(), any(), any(), any(), any()))
        .thenCallRealMethod();
    when(reservationResponseOhipMapper.toReservationByIdModel(any(), any(), any(), any(),
        any(), any())).thenReturn(reservationById);
    when(ohipReservationClient.getFoliosAciAmount(anyString(), anyString())).thenReturn(
        mockReservationAciAmount(MOCK_GET_FOLIOS_RESPONSE_CC_RSV_JSON));

    //Act
    var reservationByBasketRefResponse = hotelReservationOutPort.getReservationsByIds("TestHotelId",
        Collections.singleton("TestReservationId"), false, true, true);

    //Assert
    assertNotNull(reservationByBasketRefResponse);
    assertEquals("ST10R",
        reservationByBasketRefResponse.getReservationByIdList().get(0).getRoomStay()
            .getPromotionCode());
  }


  private Reservation mockReservationWithPurpose(String purpose) {
    Reservation r = mockReservation();
    ResGuestAdditionalInfoType info = new ResGuestAdditionalInfoType();
    info.setPurposeOfStay(purpose);
    r.getReservations().getReservation().get(0).setAdditionalGuestInfo(info);
    return r;
  }

  private Reservation mockReservationWithAdditionalInfoNull() {
    Reservation r = mockReservation();
    r.getReservations().getReservation().get(0).setAdditionalGuestInfo(null);
    return r;
  }

  private Reservation mockReservationAttachment() throws IOException {
    return mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(MOCK_OHIP_RESERVATION_ATTACHMENT_RESPONSE_JSON),
        Reservation.class);
  }

  private static ConfirmReservationRequest getConfirmReservationRequest() {
    return ConfirmReservationRequest.builder()
        .reservationId("34865")
        .hotelId("LONEUS")
        .paymentOption(PaymentOption.PAY_NOW)
        .paymentCard(PaymentCard.builder()
            .token("4111111111111111")
            .cardType("VA")
            .expirationDate("2025-03-31")
            .cardHolderName("Test")
            .build())
        .build();
  }

  private ProfileType createBookerProfileType() {
    ProfileIdResponse profileIdResponse = new ProfileIdResponse("profileId", "reservationGuest");
    ProfileType profileType = new ProfileType();
    profileType.setProfileId(profileIdResponse);
    return profileType;
  }

  private Mono<ChangeReservationDetails> createChangeReservationDetails() {
    ChangeReservationDetails changeReservationDetails = new ChangeReservationDetails();
    changeReservationDetails.setReservations(createHotelReservationsType());
    return Mono.just(changeReservationDetails);
  }

  private Reservation createReservation() {
    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("profileId");
    uniqueIdType.setType("reservationGuest");

    ResGuestTypeProfileInfo resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();
    resGuestTypeProfileInfo.setProfileIdList(List.of(uniqueIdType));

    ResGuestType reservationGuest = new ResGuestType();
    reservationGuest.setPrimary(Boolean.TRUE);
    reservationGuest.setProfileInfo(resGuestTypeProfileInfo);

    HotelReservationType hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");
    hotelReservationType.setReservationIdList(List.of(uniqueIdType));
    hotelReservationType.setReservationGuests(List.of(reservationGuest));

    HotelReservationsType hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(List.of(hotelReservationType));

    Reservation reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    return reservation;
  }

  private Profile createBookerProfile() {
    var email = "test.email@domain.uk";

    var emailType = new EmailType();
    emailType.setEmailAddress(email);

    EmailInfoType emailInfoType = new EmailInfoType();
    emailInfoType.setEmail(emailType);
    emailInfoType.setId("previousEmailId");
    emailInfoType.setType("EMAIL");

    CompanyProfileTypeEmails companyProfileTypeEmails = new CompanyProfileTypeEmails();
    companyProfileTypeEmails.setEmailInfo(List.of(emailInfoType));

    var profileDetails = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileDetails.setEmails(companyProfileTypeEmails);

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("profileId");
    uniqueIdType.setType("reservationGuest");

    Profile profile = new Profile();
    profile.setProfileDetails(profileDetails);
    return profile;
  }

  private UpdateBookerEmailRequest createValidUpdateBookerEmailRequest() {
    return UpdateBookerEmailRequest.builder()
        .reservationIds(Set.of("123456"))
        .hotelId("HOTELTEST")
        .emailAddress("secondEmail@domain.uk")
        .build();
  }

  private BookerDetailsCnpRequest mockBookerDetailsCnpRequest() {
    return BookerDetailsCnpRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("1234"))
        .booker(BookerDetailsCnp.builder()
            .emailAddress("test@whitbread.com")
            .companyName("companyName")
            .build())
        .build();
  }

  private BookerDetailsCnpRequest mockBookerDetailsCnpRequestForAddRoom() {
    return BookerDetailsCnpRequest.builder()
            .hotelId("LONEUS")
            .reservationIds(List.of("1234", "5678"))
            .booker(BookerDetailsCnp.builder()
                    .emailAddress("test@whitbread.com")
                    .companyName("companyName")
                    .build())
            .build();
  }

  private BillingAddressRequest mockBillingAddressRequest() {
    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("addressLine1")
        .addressLine2("addressLine2")
        .addressLine3("addressLine3")
        .addressLine4("addressLine4")
        .cityName("London")
        .countryCode("UK")
        .build();
    return BillingAddressRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("1234"))
        .booker(BookerDetails.builder()
            .firstName("Emma")
            .lastName("Watson")
            .address(address)
            .build())
        .build();
  }

  private BillingAddressRequest mockBillingAddressRequestBusiness() {
    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("BUSINESS")
        .addressLine1("addressLine1")
        .addressLine2("addressLine2")
        .addressLine3("addressLine3")
        .addressLine4("addressLine4")
        .cityName("London")
        .countryCode("UK")
        .build();
    return BillingAddressRequest.builder()
        .hotelId("LONEUS")
        .reservationIds(List.of("1234"))
        .booker(BookerDetails.builder()
            .firstName("Emma")
            .lastName("Watson")
            .address(address)
            .build())
        .build();
  }

  private BillingAddressRequest mockBillingAddressRequestBusinessGuestAndCompanyUpdates() {
    final BookerAddress address = BookerAddress.builder()
            .postalCode("MZC AD")
            .addressType("BUSINESS")
            .addressLine1("addressLine1")
            .addressLine2("addressLine2")
            .addressLine3("addressLine3")
            .addressLine4("addressLine4")
            .cityName("London")
            .countryCode("UK")
            .build();
    return BillingAddressRequest.builder()
            .hotelId("LONEUS")
            .reservationIds(List.of("1234"))
            .booker(BookerDetails.builder()
                    .firstName("Emma")
                    .lastName("Watson")
                    .address(address)
                    .build())
            .profileUpdateIndicators(new ProfileUpdateIndicators(true, true, true))
            .build();
  }

  private BusinessAllowanceRuleResponseDto getBusinessAllowances() throws IOException {
    return mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(MOCK_RULES_BUSINESS_ALLOWANCES_RESPONSE_JSON),
        BusinessAllowanceRuleResponseDto.class);
  }

  private Reservation getReservation() throws IOException {
    return mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(MOCK_ROUTING_INSTRUCTIONS_RESPONSE_JSON),
        Reservation.class);
  }

  private Reservation getReservationWithPackages() throws IOException {
    return mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(MOCK_ROUTING_INSTRUCTIONS_WITH_RESERVATION_PACKAGES_RESPONSE_JSON),
        Reservation.class);
  }

  private UpdateReservationOverrideReasonsRequest mockUpdateReservationOverrideReasonsRequest() {
    return UpdateReservationOverrideReasonsRequest.builder()
        .hotelId("BERALX")
        .reasonCode("ILL")
        .reasonName("Medical Appointents")
        .callerName("John Doe")
        .reservationIds(Set.of("123456"))
        .build();
  }

  private UpdateReservationCcAgentIdRequest mockUpdateReservationCcAgentIdRequest(
      boolean clearFirst) {
    return UpdateReservationCcAgentIdRequest.builder()
        .hotelId("BERALX")
        .ccAgentId("jane.doe@wb.com")
        .reservationIds(Set.of("123456"))
        .clearFirst(clearFirst)
        .build();
  }

  private UpdateReasonForStayRequest mockUpdateReasonForStayRequest() {
    return UpdateReasonForStayRequest.builder()
        .hotelId("LONEUS")
        .reasonForStay("LEI")
        .reservationIds(List.of("123456"))
        .build();
  }

  private List<Profile> mockProfileResponse() {
    var uniqueIdType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIdType.setId("1245279");

    var person = new PersonNameType();
    person.setNameTitle("Mr");
    person.setGivenName("John");
    person.setSurname("Doe");

    var customer = new CustomerType();
    customer.setPersonName(List.of(person));
    customer.setLanguage(OPERA_LANGUAGE_CODE_E);

    var profileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileType.setCustomer(customer);

    var profile = new Profile();
    profile.setProfileIdList(List.of(uniqueIdType));
    profile.setProfileDetails(profileType);
    return Collections.singletonList(profile);

  }

  private ReservationsDetails mockSearchReservationResponseOhip(int offset, int limit, int size,
      boolean hasMore) {

    var reservationInfoTypes = new ArrayList<ReservationInfoType>();

    for (int index = 0; index < size; index++) {
      var externalReferences = new ExternalReferenceType();
      externalReferences.setId("MANOLD9400159");
      externalReferences.setIdContext("WB_DIGITAL");

      var profileId = new UniqueIDType();
      profileId.setId("1245279");

      var attachedProfiles = new ResAttachedProfileType();
      attachedProfiles.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);
      attachedProfiles.setProfileIdList(List.of(profileId));

      var reservationInfoType = new ReservationInfoType();
      reservationInfoType.setHotelId("MANOLD");
      reservationInfoType.setExternalReferences(List.of(externalReferences));
      reservationInfoType.setAttachedProfiles(List.of(attachedProfiles));
      reservationInfoTypes.add(reservationInfoType);
    }

    var reservationsDetailsReservations = new ReservationsDetailsReservations();
    reservationsDetailsReservations.setReservationInfo(reservationInfoTypes);

    var reservationsDetails = new ReservationsDetails();
    reservationsDetails.setReservations(reservationsDetailsReservations);
    reservationsDetailsReservations.setHasMore(hasMore);
    reservationsDetailsReservations.setOffset(offset);
    reservationsDetailsReservations.setLimit(limit);

    return reservationsDetails;
  }

  private static Reservation mockReservationsPayment(
      uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType card) {
    Reservation reservation = new Reservation();
    HotelReservationsType reserType = new HotelReservationsType();
    HotelReservationType type = new HotelReservationType();
    type.setHotelId(HOTEL_ID);
    ReservationPaymentMethodType paymentMeth = new ReservationPaymentMethodType();
    paymentMeth.setFolioView(1);
    paymentMeth.setPaymentMethod("BU");
    paymentMeth.setPaymentCard(card);
    List<ReservationPaymentMethodType> paymentList = List.of(paymentMeth);
    type.setReservationPaymentMethods(paymentList);
    reserType.setReservation(List.of(type));
    reservation.setReservations(reserType);
    return reservation;
  }

  private ReservationsDetails mockReservationsDetailsOhip() {
    ReservationsDetails reservationsDetails = new ReservationsDetails();
    ReservationsDetailsReservations reservations = new ReservationsDetailsReservations();
    ReservationInfoType reservationInfoType = new ReservationInfoType();
    ExternalReferenceType externalReferenceType = new ExternalReferenceType();
    StayInfoType stayInfoType = new StayInfoType();
    ResGuestInfoType reservationGuest = new ResGuestInfoType();
    UniqueIDType uniqueIDType = new UniqueIDType();
    uniqueIDType.setType("Reservation");
    uniqueIDType.setId("1234");
    CurrencyAmountType currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(100));
    currencyAmountType.setCurrencyCode("GBP");
    externalReferenceType.setId(EXTERNAL_REFERENCE_ID);
    externalReferenceType.setIdContext("externalRef");
    reservationGuest.setId("1234");
    reservationGuest.setSurname("Jhon");
    reservationInfoType.setReservationGuest(reservationGuest);
    reservationInfoType.setReservationIdList(List.of(uniqueIDType));
    stayInfoType.setRateAmount(currencyAmountType);
    reservationInfoType.setRoomStay(stayInfoType);
    reservationInfoType.setExternalReferences(Collections.singletonList(externalReferenceType));
    reservationInfoType.setHotelId(HOTEL_ID);
    ResAttachedProfileType resAttachedProfileType = new ResAttachedProfileType();
    UniqueIDType profileUniqueIDType = new UniqueIDType();
    profileUniqueIDType.setType("Profile");
    profileUniqueIDType.setId("1234");
    resAttachedProfileType.setProfileIdList(Collections.singletonList(profileUniqueIDType));
    resAttachedProfileType.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);
    reservationInfoType.setAttachedProfiles(Collections.singletonList(resAttachedProfileType));
    reservations.setReservationInfo(Collections.singletonList(reservationInfoType));
    reservationsDetails.setReservations(reservations);
    return reservationsDetails;
  }


  private Reservation mockReservationIdDetailsOhip() {
    Reservation reservation = new Reservation();
    HotelReservationsType hotelReservationsType = new HotelReservationsType();
    List<HotelReservationType> hotelReservationTypelist = new ArrayList<HotelReservationType>();
    HotelReservationType hotelReservationType = new HotelReservationType();
    ExternalReferenceType externalReferenceType = new ExternalReferenceType();
    RoomStayType roomStayType = new RoomStayType();
    UniqueIDType uniqueIDType = new UniqueIDType();
    uniqueIDType.setType("Reservation");
    uniqueIDType.setId("1234");
    roomStayType.setRegistrationNumber(uniqueIDType);
    hotelReservationType.setReservationIdList(List.of(uniqueIDType));
    hotelReservationType.setExternalReferences(Collections.singletonList(externalReferenceType));
    hotelReservationType.setHotelId(HOTEL_ID);
    HotelReservationTypeReservationProfiles resAttachedProfileType = new HotelReservationTypeReservationProfiles();
    ReservationProfileType reservationProfileType = new ReservationProfileType();

    UniqueIDType profileUniqueIDType = new UniqueIDType();
    profileUniqueIDType.setType("Profile");
    profileUniqueIDType.setId("1234");
    hotelReservationTypelist.add(hotelReservationType);
    hotelReservationsType.setReservation(hotelReservationTypelist);
    reservationProfileType.setProfileIdList(Collections.singletonList(profileUniqueIDType));
    reservationProfileType.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);
    resAttachedProfileType.setReservationProfile(Collections.singletonList(reservationProfileType));
    hotelReservationType.setReservationProfiles(resAttachedProfileType);
    reservation.setReservations(hotelReservationsType);
    return reservation;
  }


  private List<ReservationGuest> mockReservationGuestList() {
    return Collections.singletonList(ReservationGuest.builder()
        .surname("Jhon")
        .givenName("Gate")
        .fullName("Jhon Gate")
        .guestRestricted(false)
        .email("jhon@gmail.com")
        .address(mockReservationGuestAddress())
        .additionalDetails(mockStayingGuestAdditionalDetails())
        .phoneNumber("0747132132")
        .language("E")
        .homeAddress(mockReservationGuestAddress())
        .build());
  }

  private GuestAddress mockReservationGuestAddress() {
    return GuestAddress.builder()
        .addressType("HOME")
        .addressLine1("First Line")
        .cityName("Big City")
        .postalCode("PO5 TA1")
        .addressId("12345")
        .build();
  }

  private StayingGuestAdditionalDetails mockStayingGuestAdditionalDetails() {
    return StayingGuestAdditionalDetails.builder()
        .dob(LocalDate.parse("1996-07-13"))
        .nationality("UK")
        .passportNumber("ABCD1234")
        .build();
  }

  private RoomStay mockRoomStay() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.of(2022, 5, 5))
        .departureDate(LocalDate.of(2022, 5, 7))
        .adultCount(2)
        .childCount(0)
        .ratePlanCode("AXWS")
        .marketCode("OTH")
        .roomType("SINGLE")
        .ratesPerNight(mockRatesPerNight())
        .cellCode("cellCode")
        .roomNumber("120")
        .bookingChannel("PI.com")
        .build();
  }

  private RoomStay mockRoomStayReservation() {
    return RoomStay.builder()
        .arrivalDate(LocalDate.of(2022, 5, 5))
        .departureDate(LocalDate.of(2022, 5, 7))
        .adultCount(1)
        .childCount(0)
        .ratePlanCode("AXWS")
        .roomType("SINGLE")
        .build();
  }

  private List<DepositPolicies> mockDepositPolicies() {
    return Collections.singletonList(DepositPolicies.builder()
        .amountDue(uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType.builder()
            .currencyCode("1")
            .amount(BigDecimal.ONE)
            .build())
        .amountPaid(uk.co.whitbread.ohip.domain.model.reservation.out.CurrencyAmountType.builder()
            .currencyCode("1")
            .amount(BigDecimal.ONE)
            .build())
        .policyCode("code")
        .build());
  }

  private ReservationById mockReservationById() {
    return ReservationById.builder()
        .reservationId("12345")
        .hotelId("LONEUS")
        .reservationGuestList(mockReservationGuestList())
        .roomStay(mockRoomStay())
        .depositPolicies(mockDepositPolicies())
        .paymentCard(ReservationPaymentCardType.builder().cardNumberMasked("XXXXXXXXXXXX1100")
            .token("4764776852337921100").expirationDate(LocalDate.of(2026, 5, 31))
            .cardType("Va").build())
        .reservationOverrideReasons(mockUserDefinedFieldsForOverrideReasons())
        .balanceAmount(BigDecimal.valueOf(60))
        .reservationStatus("Reserved")
        .preCheckInStatus(false)
        .build();
  }

  private List<RatePerNight> mockRatesPerNight() {
    var ratePerNight1 = RatePerNight.builder()
        .startDate("2023-07-28")
        .pricePerNight(BigDecimal.valueOf(999))
        .cityTaxPerNight(BigDecimal.valueOf(59.94))
        .build();

    var ratePerNight2 = RatePerNight.builder()
        .startDate("2023-07-29")
        .pricePerNight(BigDecimal.valueOf(999))
        .cityTaxPerNight(BigDecimal.valueOf(59.94))
        .build();
    return Arrays.asList(ratePerNight1, ratePerNight2);
  }

  private List<RatePerNight> mockRatesPerNightDistr() {
    var ratePerNight1 = RatePerNight.builder()
        .startDate("2023-07-28")
        .pricePerNight(BigDecimal.valueOf(999))
        .grossPricePerNight(BigDecimal.valueOf(832.5))
        .vatRate(BigDecimal.valueOf(166.5))
        .cityTaxPerNight(BigDecimal.valueOf(59.94))
        .cityTaxAmountBeforeTax(BigDecimal.valueOf(56.02))
        .cityTaxVat(BigDecimal.valueOf(3.92))
        .build();

    var ratePerNight2 = RatePerNight.builder()
        .startDate("2023-07-29")
        .pricePerNight(BigDecimal.valueOf(999))
        .grossPricePerNight(BigDecimal.valueOf(832.5))
        .vatRate(BigDecimal.valueOf(166.5))
        .cityTaxPerNight(BigDecimal.valueOf(59.94))
        .cityTaxAmountBeforeTax(BigDecimal.valueOf(56.02))
        .cityTaxVat(BigDecimal.valueOf(3.92))
        .build();

    return Arrays.asList(ratePerNight1, ratePerNight2);
  }

  private List<ReservationPackagesDetailsResponse> mockReservationPackageList() {
    var package1 = ReservationPackagesDetailsResponse.builder()
        .packageCode("CITYTAX")
        .description("City Tax")
        .unitPrice(BigDecimal.valueOf(59.94))
        .totalQuantity(1)
        .computedPrice(BigDecimal.valueOf(59.94))
        .grossPrice(BigDecimal.valueOf(56.02))
        .vatTax(BigDecimal.valueOf(3.92))
        .startDate(String.valueOf(LocalDate.now()))
        .endDate(String.valueOf(LocalDate.now().plusDays(3)))
        .build();
    var package2 = ReservationPackagesDetailsResponse.builder()
        .packageCode("BFADBF")
        .description("Premier Inn Breakfast Food")
        .unitPrice(BigDecimal.valueOf(9.45))
        .totalQuantity(1)
        .computedPrice(BigDecimal.valueOf(9.45))
        .grossPrice(BigDecimal.valueOf(8.83))
        .vatTax(BigDecimal.valueOf(0.62))
        .startDate(String.valueOf(LocalDate.now()))
        .endDate(String.valueOf(LocalDate.now().plusDays(4)))
        .build();
    var package3 = ReservationPackagesDetailsResponse.builder()
        .packageCode("PIBBEV")
        .description("Premier Inn Breakfast Beverage")
        .unitPrice(BigDecimal.valueOf(4.05))
        .totalQuantity(1)
        .computedPrice(BigDecimal.valueOf(4.05))
        .grossPrice(BigDecimal.valueOf(3.4))
        .vatTax(BigDecimal.valueOf(0.65))
        .startDate(String.valueOf(LocalDate.now()))
        .endDate(String.valueOf(LocalDate.now().plusDays(5)))
        .build();

    return Arrays.asList(package1, package2, package3);
  }

  private List<ReservationById> mockReservationByIdList() {
    return Collections.singletonList(mockReservationById());
  }

  private ReservationById mockReservationByIdWithoutOverride() {
    RateInfoSummary rateInfoSummary = new RateInfoSummary();
    rateInfoSummary.setOutStandingCostOfStay(BigDecimal.valueOf(50));
    uk.co.whitbread.ohip.domain.model.reservation.out.RateInfo rateInfo =
            new uk.co.whitbread.ohip.domain.model.reservation.out.RateInfo(rateInfoSummary);

    return ReservationById.builder()
        .reservationGuestList(mockReservationGuestList())
        .roomStay(mockRoomStay())
        .depositPolicies(mockDepositPolicies())
            .rateInfo(rateInfo)
        .paymentCard(ReservationPaymentCardType.builder().cardNumberMasked("XXXXXXXXXXXX1100")
            .token("4764776852337921100").expirationDate(LocalDate.of(2026, 5, 31))
            .cardType("Va").build())
        .reservationStatus("Reserved")
        .preCheckInStatus(false)
        .build();
  }

  private ReservationOverrideReasons mockUserDefinedFieldsForOverrideReasons() {
    return ReservationOverrideReasons.builder()
        .reasonCode("ILL")
        .reasonName("Illness")
        .callerName("John Doe")
        .managerName("James Bond")
        .build();
  }

  private BusinessAllowanceRuleResponseDto mockBusinessAllowance() {
    var businessAllowanceRuleCityTax = new BusinessAllowanceRuleDto();
    businessAllowanceRuleCityTax.setSourceId("CITYTAX");
    businessAllowanceRuleCityTax.setTargetId("CITY");
    businessAllowanceRuleCityTax.setPms("OP");
    businessAllowanceRuleCityTax.setSourceType("sourceType");
    var businessAllowanceRuleAccmod = new BusinessAllowanceRuleDto();
    businessAllowanceRuleAccmod.setSourceId("accommodation");
    businessAllowanceRuleAccmod.setTargetId("ROOM");
    businessAllowanceRuleAccmod.setPms("OP");
    businessAllowanceRuleAccmod.setSourceType("sourceType");
    return new BusinessAllowanceRuleResponseDto()
        .addBusinessAllowancesItem(businessAllowanceRuleCityTax)
        .addBusinessAllowancesItem(businessAllowanceRuleAccmod);
  }

  private PriceBreakdownDto mockReservationAmounts() {

    var summary = new SummaryDto();
    summary.setTotalCostOfStay(BigDecimal.valueOf(100));
    summary.setOutStandingCostOfStay(BigDecimal.valueOf(100));
    summary.setGross(BigDecimal.valueOf(80));
    summary.setNet(BigDecimal.valueOf(100));
    summary.setDeposit(BigDecimal.ZERO);

    var reservationAmounts = new PriceBreakdownDto();
    reservationAmounts.setSummary(summary);

    return reservationAmounts;
  }

  private RateInfo mockRateInfoDetails() throws IOException {
    return mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(MOCK_RATE_INFO_RESPONSE_JSON),
        RateInfo.class);
  }

  private HotelDetails mockHotelDetails() {

    var hotelInfo = new HotelInfoType();

    var hotelDetails = new HotelDetails();
    hotelDetails.setHotelConfigInfo(hotelInfo);

    return hotelDetails;
  }

  private Reservation mockReservation() {

    var uniqueId = new UniqueIDType();
    uniqueId.setType("Reservation");
    uniqueId.setId("100100");

    var total = new TotalType();
    total.setCurrencyCode("GBP");

    var discount = new DiscountType();
    discount.setAmount(BigDecimal.TEN);

    var amount = new AmountType();
    amount.setBase(total);
    amount.setDiscount(discount);

    var totalType = new TotalType();
    totalType.setAmountAfterTax(BigDecimal.valueOf(100L));
    amount.setTotal(totalType);

    var rate = new RatesType();
    rate.setRate(Collections.singletonList(amount));

    var roomRate = new RoomRateType();
    roomRate.setRates(rate);
    roomRate.setSourceCodeDescription("PI.com");
    roomRate.setSourceCode("38");

    var currentRoomInfo = new CurrentRoomInfoType();
    currentRoomInfo.setRoomId("120");
    GuestCountsType guestCountsType = new GuestCountsType();
    guestCountsType.setAdults(2);

    ResGuaranteeType guaranteeType = new ResGuaranteeType();
    guaranteeType.setGuaranteeCode("CC");

    var room = new RoomStayType();
    room.setArrivalDate(LocalDate.of(2023, 7, 28));
    room.setDepartureDate(LocalDate.of(2023, 7, 30));
    room.setRoomRates(Collections.singletonList(roomRate));
    room.setCurrentRoomInfo(currentRoomInfo);
    room.setGuestCounts(guestCountsType);
    room.setGuarantee(guaranteeType);

    var resCashiering = new ResCashieringType();
    var taxType = new ReservationTaxTypeInfo();
    taxType.setCode("UK");
    taxType.setDescription("UK VAT");
    resCashiering.setTaxType(taxType);

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));

    var resPaymentCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    resPaymentCard.setCardId(new UniqueIDType());
    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("CA");
    reservationPaymentMethod.setPaymentCard(resPaymentCard);
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);

    var characterUDFType = new CharacterUDFType();
    characterUDFType.setName("UDFC15");
    characterUDFType.setValue("cellCode");

    var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(new ArrayList<>(List.of(characterUDFType)));

    var bookerReservationProfileType = new ReservationProfileType();
    var profileUniqueIdType = new UniqueIDType();
    profileUniqueIdType.setId("123456");
    profileUniqueIdType.setType("Profile");
    bookerReservationProfileType.setProfileIdList(List.of(profileUniqueIdType));
    bookerReservationProfileType.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);

    var companyReservationProfileType = new ReservationProfileType();
    var companyUniqueIdType = new UniqueIDType();
    companyUniqueIdType.setId("123456");
    companyUniqueIdType.setType("Company");
    companyReservationProfileType.setProfileIdList(List.of(companyUniqueIdType));
    var companyType = new CompanyType();
    companyType.setCompanyName("CompanyName");
    var companyProfileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType();
    companyProfileType.setCompany(companyType);
    companyReservationProfileType.setProfile(companyProfileType);
    companyReservationProfileType.setReservationProfileType(ResProfileTypeType.COMPANY);

    var hotelReservationTypeReservationProfiles = new HotelReservationTypeReservationProfiles();
    hotelReservationTypeReservationProfiles.setReservationProfile(
        Arrays.asList(bookerReservationProfileType, companyReservationProfileType));

    var reservationPackage = new ReservationPackageType();
    reservationPackage.setPackageCode("CITYTAX");
    reservationPackage.setStartDate(LocalDate.of(2023, 7, 28));
    reservationPackage.setEndDate(LocalDate.of(2023, 7, 28));

    var reservationPackageScheduleType = new ReservationPackageScheduleType();
    reservationPackageScheduleType.setConsumptionDate(LocalDate.of(2023, 7, 28));
    reservationPackageScheduleType.setUnitPrice(BigDecimal.valueOf(59.94));
    reservationPackageScheduleType.setTotalQuantity(1);
    reservationPackageScheduleType.setComputedResvPrice(BigDecimal.valueOf(59.94));
    reservationPackage.setScheduleList(Collections.singletonList(reservationPackageScheduleType));

    var resGuestAdditionalInfoType = new ResGuestAdditionalInfoType();
    resGuestAdditionalInfoType.setPurposeOfStay("purposeOfStay");

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("profileId");
    uniqueIdType.setType("reservationGuest");

    uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType profileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType();
    profileType.setProfileType(ProfileTypeType.GUEST);

    ResGuestTypeProfileInfo resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();
    resGuestTypeProfileInfo.setProfileIdList(List.of(uniqueIdType));
    resGuestTypeProfileInfo.setProfile(profileType);

    ResGuestType reservationGuest = new ResGuestType();
    reservationGuest.setPrimary(Boolean.TRUE);
    reservationGuest.setProfileInfo(resGuestTypeProfileInfo);

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");
    hotelReservationType.setRoomStay(room);
    hotelReservationType.setReservationPaymentMethods(
        Collections.singletonList(reservationPaymentMethod));
    hotelReservationType.setCashiering(resCashiering);
    hotelReservationType.setUserDefinedFields(userDefinedFields);
    hotelReservationType.setReservationIdList(Collections.singletonList(uniqueId));
    hotelReservationType.setReservationProfiles(hotelReservationTypeReservationProfiles);
    hotelReservationType.setReservationPackages(Collections.singletonList(reservationPackage));
    hotelReservationType.setAdditionalGuestInfo(resGuestAdditionalInfoType);
    hotelReservationType.setReservationGuests(List.of(reservationGuest));

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));

    Date dateRepresentation = mockDate();

    var policyDeadlineType = new PolicyDeadlineType();
    policyDeadlineType.setAbsoluteDeadline(dateRepresentation);

    var cancelPenaltyType = new ResCancelPenaltyType();
    cancelPenaltyType.setDeadline(policyDeadlineType);

    var cancellationPolicies = new ResCancellationPolicyType();
    cancellationPolicies.setPolicy(cancelPenaltyType);
    cancellationPolicies.setComments(
        "Cancellations after 1pm on the day of arrival charged 100% of 1 night");

    var amountPaid = new CurrencyAmountType();
    amountPaid.setAmount(new BigDecimal(0));

    var amountDue = new CurrencyAmountType();
    amountDue.setAmount(new BigDecimal(100));

    var resDepositPolicyType = new ResDepositPolicyType();
    resDepositPolicyType.setAmountPaid(amountPaid);
    resDepositPolicyType.setAmountDue(amountDue);

    var reservationPoliciesType = new ReservationPoliciesType();
    reservationPoliciesType.setCancellationPolicies(
        Collections.singletonList(cancellationPolicies));
    reservationPoliciesType.setDepositPolicies(Collections.singletonList(resDepositPolicyType));

    hotelReservationType.setReservationPolicies(reservationPoliciesType);

    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    var comments = new CommentInfoType();
    comments.setType("Comment");
    comments.setId("12345");

    var comment = new CommentType();
    comment.setCommentTitle("SPECIAL_REQUESTS");

    comments.setComment(comment);
    reservation.getReservations().getReservation().get(0).setComments(List.of(comments));

    ExternalReferenceType externalReferenceType = new ExternalReferenceType();
    externalReferenceType.setId("WB-EXTERNAL-REF-12345");
    externalReferenceType.setIdContext("WB_DIGITAL");
    reservation.getReservations().getReservation().get(0).setExternalReferences(List.of(externalReferenceType));

    return reservation;
  }

  private HotelReservationsType createHotelReservationsType() {

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    return hotelReservationsType;
  }

  private Reservation mockReservationNoCompanyProfile() {

    var uniqueId = new UniqueIDType();
    uniqueId.setType("Reservation");
    uniqueId.setId("100100");

    var total = new TotalType();
    total.setCurrencyCode("GBP");

    var discount = new DiscountType();
    discount.setAmount(BigDecimal.TEN);

    var amount = new AmountType();
    amount.setBase(total);
    amount.setDiscount(discount);

    var totalType = new TotalType();
    totalType.setAmountAfterTax(BigDecimal.valueOf(100L));
    amount.setTotal(totalType);

    var rate = new RatesType();
    rate.setRate(Collections.singletonList(amount));

    var roomRate = new RoomRateType();
    roomRate.setRates(rate);
    roomRate.setSourceCodeDescription("PI.com");
    roomRate.setSourceCode("38");

    var currentRoomInfo = new CurrentRoomInfoType();
    currentRoomInfo.setRoomId("120");
    GuestCountsType guestCountsType = new GuestCountsType();
    guestCountsType.setAdults(2);

    var room = new RoomStayType();
    room.setArrivalDate(LocalDate.of(2023, 7, 28));
    room.setDepartureDate(LocalDate.of(2023, 7, 30));
    room.setRoomRates(Collections.singletonList(roomRate));
    room.setCurrentRoomInfo(currentRoomInfo);
    room.setGuestCounts(guestCountsType);

    var resCashiering = new ResCashieringType();
    var taxType = new ReservationTaxTypeInfo();
    taxType.setCode("UK");
    taxType.setDescription("UK VAT");
    resCashiering.setTaxType(taxType);

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));

    var resPaymentCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    resPaymentCard.setCardId(new UniqueIDType());
    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("CA");
    reservationPaymentMethod.setPaymentCard(resPaymentCard);
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);

    var characterUDFType = new CharacterUDFType();
    characterUDFType.setName("UDFC15");
    characterUDFType.setValue("cellCode");

    var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(new ArrayList<>(List.of(characterUDFType)));

    var bookerReservationProfileType = new ReservationProfileType();
    var profileUniqueIdType = new UniqueIDType();
    profileUniqueIdType.setId("123456");
    profileUniqueIdType.setType("Profile");
    bookerReservationProfileType.setProfileIdList(List.of(profileUniqueIdType));
    bookerReservationProfileType.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);

    var hotelReservationTypeReservationProfiles = new HotelReservationTypeReservationProfiles();
    hotelReservationTypeReservationProfiles.setReservationProfile(
            Arrays.asList(bookerReservationProfileType));

    var reservationPackage = new ReservationPackageType();
    reservationPackage.setPackageCode("CITYTAX");
    reservationPackage.setStartDate(LocalDate.of(2023, 7, 28));
    reservationPackage.setEndDate(LocalDate.of(2023, 7, 28));

    var reservationPackageScheduleType = new ReservationPackageScheduleType();
    reservationPackageScheduleType.setConsumptionDate(LocalDate.of(2023, 7, 28));
    reservationPackageScheduleType.setUnitPrice(BigDecimal.valueOf(59.94));
    reservationPackageScheduleType.setTotalQuantity(1);
    reservationPackageScheduleType.setComputedResvPrice(BigDecimal.valueOf(59.94));
    reservationPackage.setScheduleList(Collections.singletonList(reservationPackageScheduleType));

    var resGuestAdditionalInfoType = new ResGuestAdditionalInfoType();
    resGuestAdditionalInfoType.setPurposeOfStay("purposeOfStay");

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("profileId");
    uniqueIdType.setType("reservationGuest");

    uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType profileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ProfileType();
    profileType.setProfileType(ProfileTypeType.GUEST);

    ResGuestTypeProfileInfo resGuestTypeProfileInfo = new ResGuestTypeProfileInfo();
    resGuestTypeProfileInfo.setProfileIdList(List.of(uniqueIdType));
    resGuestTypeProfileInfo.setProfile(profileType);

    ResGuestType reservationGuest = new ResGuestType();
    reservationGuest.setPrimary(Boolean.TRUE);
    reservationGuest.setProfileInfo(resGuestTypeProfileInfo);

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");
    hotelReservationType.setRoomStay(room);
    hotelReservationType.setReservationPaymentMethods(
            Collections.singletonList(reservationPaymentMethod));
    hotelReservationType.setCashiering(resCashiering);
    hotelReservationType.setUserDefinedFields(userDefinedFields);
    hotelReservationType.setReservationIdList(Collections.singletonList(uniqueId));
    hotelReservationType.setReservationProfiles(hotelReservationTypeReservationProfiles);
    hotelReservationType.setReservationPackages(Collections.singletonList(reservationPackage));
    hotelReservationType.setAdditionalGuestInfo(resGuestAdditionalInfoType);
    hotelReservationType.setReservationGuests(List.of(reservationGuest));

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));

    Date dateRepresentation = mockDate();

    var policyDeadlineType = new PolicyDeadlineType();
    policyDeadlineType.setAbsoluteDeadline(dateRepresentation);

    var cancelPenaltyType = new ResCancelPenaltyType();
    cancelPenaltyType.setDeadline(policyDeadlineType);

    var cancellationPolicies = new ResCancellationPolicyType();
    cancellationPolicies.setPolicy(cancelPenaltyType);
    cancellationPolicies.setComments(
            "Cancellations after 1pm on the day of arrival charged 100% of 1 night");

    var amountPaid = new CurrencyAmountType();
    amountPaid.setAmount(new BigDecimal(0));

    var amountDue = new CurrencyAmountType();
    amountDue.setAmount(new BigDecimal(100));

    var resDepositPolicyType = new ResDepositPolicyType();
    resDepositPolicyType.setAmountPaid(amountPaid);
    resDepositPolicyType.setAmountDue(amountDue);

    var reservationPoliciesType = new ReservationPoliciesType();
    reservationPoliciesType.setCancellationPolicies(
            Collections.singletonList(cancellationPolicies));
    reservationPoliciesType.setDepositPolicies(Collections.singletonList(resDepositPolicyType));

    hotelReservationType.setReservationPolicies(reservationPoliciesType);

    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    var comments = new CommentInfoType();
    comments.setType("Comment");
    comments.setId("12345");

    var comment = new CommentType();
    comment.setCommentTitle("SPECIAL_REQUESTS");

    comments.setComment(comment);
    reservation.getReservations().getReservation().get(0).setComments(List.of(comments));

    return reservation;
  }

  private Reservation mockReservationWithInstructionsAndDf() {
    var reservation = mockReservation();

    RoutingInfoType routingInfoType = new RoutingInfoType();
    RoutingInfoTypeFolio routingInfoTypeFolio = new RoutingInfoTypeFolio();
    RoutingInstructionType routingInstructionType = new RoutingInstructionType();

    routingInstructionType.setCreditLimit(new BigDecimal(100));

    TrxInfoType transactions = new TrxInfoType();
    transactions.setTransactionCode("CITYTAX");
    BillingInstructionType billingInstructionType = new BillingInstructionType();
    billingInstructionType.setBillingCode("billingCode");

    RoutingInstructionTypeDuration duration = new RoutingInstructionTypeDuration();
    TimeSpanType timeSpanType = new TimeSpanType();
    timeSpanType.setStartDate(LocalDate.of(2023, 7, 28));
    duration.setTimeSpan(timeSpanType);
    duration.getTimeSpan().setStartDate(LocalDate.of(2023, 7, 28));

    routingInstructionType.setTransactionCodes(List.of(transactions));
    routingInstructionType.setBillingInstructions(List.of(billingInstructionType));
    routingInstructionType.setDuration(duration);

    routingInfoTypeFolio.setInstructions(List.of(routingInstructionType));
    routingInfoType.setFolio(routingInfoTypeFolio);

    reservation.getReservations().getReservation().get(0).setRoutingInstructions(List.of(routingInfoType));

    return reservation;
  }

  private Reservation mockReservationWithoutPackages() {
    var mockReservationWithoutPackages = mockReservation();
    mockReservationWithoutPackages.getReservations().getReservation().get(0)
        .setReservationPackages(null);
    return mockReservationWithoutPackages;
  }

  private Reservation mockReservationWithoutPaymentCard() {
    var mockReservationWithoutPaymtCard = mockReservation();
    mockReservationWithoutPaymtCard.getReservations().getReservation().get(0)
        .getReservationPaymentMethods().get(0).setPaymentCard(null);
    return mockReservationWithoutPaymtCard;
  }

  private Reservation mockReservationWithNullCardId() {
    var reservation = mockReservation();
    var dummyPaymentCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    dummyPaymentCard.setCardId(null);
    reservation.getReservations().getReservation().get(0)
        .getReservationPaymentMethods().get(0).setPaymentCard(dummyPaymentCard);
    return reservation;
  }

  private Reservation mockReservationWithAci() {
    var mockReservationWithoutAci = mockReservation();
    AdvanceCheckInType advanceCheckInType = new AdvanceCheckInType();
    advanceCheckInType.setAdvanceCheckedIn(true);
    mockReservationWithoutAci.getReservations().getReservation().get(0)
        .setAdvanceCheckIn(advanceCheckInType);
    return mockReservationWithoutAci;
  }

  private FoliosResponseDto mockReservationAciAmount(String json_file_name) throws IOException {
    return mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(json_file_name),
        FoliosResponseDto.class);
  }

  private Reservation mockReservationWithPaymentCard() {
    var reservationWithoutPaymentCard = mockReservation();
    var resPaymentTypeCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    var cardId = new UniqueIDType();
    cardId.setId("221134");
    cardId.setType("CreditCard");

    resPaymentTypeCard.setCardNumber("1234567812345678");
    resPaymentTypeCard.setCardHolderName("cardHolderName");
    resPaymentTypeCard.setCardNumberLast4Digits("6789");
    resPaymentTypeCard.setCardId(cardId);

    reservationWithoutPaymentCard.getReservations().getReservation().get(0)
        .getReservationPaymentMethods().get(0)
        .setPaymentCard(resPaymentTypeCard);

    return reservationWithoutPaymentCard;
  }

  private Reservation mockReservationWithPaymentDetails() {
    var reservation = mockReservation();
    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));
    var cardId = new UniqueIDType();
    cardId.setType("cardId");
    cardId.setId("22222");
    var resPaymentCardType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    resPaymentCardType.setCardId(cardId);
    resPaymentCardType.setCardNumberMasked("XXXXXXXXXXXX1100");
    resPaymentCardType.cardType(CardTypeType.MC);
    resPaymentCardType.setCardHolderName("Tester Testerson");
    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentCard(resPaymentCardType);
    reservationPaymentMethod.setPaymentMethod("MC");
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);
    reservation.getReservations().getReservation().get(0)
        .setReservationPaymentMethods(Collections.singletonList(reservationPaymentMethod));
    return reservation;
  }

  private Date mockDate() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, 2022);
    calendar.set(Calendar.MONTH, Calendar.APRIL);
    calendar.set(Calendar.DAY_OF_MONTH, 4);
    calendar.set(Calendar.HOUR_OF_DAY, 1);
    calendar.set(Calendar.MINUTE, 0);
    calendar.set(Calendar.SECOND, 0);

    return calendar.getTime();
  }

  private Mono<ChangeReservationDetails> mockChangeReservationResponse() {
    ChangeReservationDetails details = new ChangeReservationDetails();
    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("123456");
    reservationIdItem.setType("Reservation");
    UniqueIDType confirmationIdItem = new UniqueIDType();
    confirmationIdItem.setId("264401");
    confirmationIdItem.setType("Confirmation");
    HotelReservationsType reservations = new HotelReservationsType();
    HotelReservationType reservation = new HotelReservationType();
    RoomStayType roomStay = new RoomStayType();
    roomStay.setArrivalDate(LocalDate.parse("2022-08-01"));
    roomStay.setDepartureDate(LocalDate.parse("2022-08-02"));
    reservation.setRoomStay(roomStay);
    reservation.setHotelId("LONEUS");
    reservation.setReservationIdList(List.of(reservationIdItem, confirmationIdItem));
    reservation.setReservationStatus(PMSResStatusType.RESERVED);
    reservations.setReservation(List.of(reservation));
    details.setReservations(reservations);
    return Mono.just(details);
  }

  private ReservationRequest createValidHotelReservationRequest(boolean isgetReservationsByIds) {

    RoomRateReservation roomRateReservation = RoomRateReservation.builder()
        .start("2022-10-20")
        .end("2022-10-20")
        .roomType("SBD")
        .ratePlanCode("DAILY")
        .cellCode("ABC")
        .build();

    uk.co.whitbread.ohip.domain.model.reservation.in.Reservation res1 =
        uk.co.whitbread.ohip.domain.model.reservation.in.Reservation.builder()
            .hotelId("HOTELTEST")
            .arrival("2021-11-05")
            .departure("2021-11-06")
            .adults(1)
            .children(0)
            .cotRequired(false)
            .externalReferenceId("12345")
            .roomRates(roomRateReservation)
            .gdsReferenceNumber("ABCD1234")
            .bookingNotes("NONSMOKING")
            .distributionUsername("testDistUsername")
            .distributionIATANumber("00000000123456")
            .bookingType("ANON")
            .operaCompanyId("123")
            .build();

    BookingChannel bookingChannel = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .build();

    return new ReservationRequest(
        Collections.singletonList(res1),
        bookingChannel,
        isgetReservationsByIds);
  }

  private ReservationRequest createValidHotelReservationRequestWithPackages() {

    RoomRateReservation roomRateReservation = RoomRateReservation.builder()
        .start("2026-02-18")
        .end("2026-02-20")
        .roomType("DOUBLE")
        .ratePlanCode("FLEXRATE")
        .build();

    var res1 = uk.co.whitbread.ohip.domain.model.reservation.in.Reservation.builder()
        .hotelId("LONEUS")
        .arrival("2026-02-18")
        .departure("2026-02-20")
        .adults(1)
        .children(0)
        .cotRequired(false)
        .externalReferenceId("12345")
        .roomRates(roomRateReservation)
        .reservationPackages(Collections.singletonList(ReservationPackages.builder()
            .packageCode("MDP")
            .startDate("2026-02-18")
            .endDate("2026-02-20")
            .scheduleList(List.of(
                ScheduleList
                    .builder()
                    .consumptionDate("2026-02-18")
                    .unitPrice(BigDecimal.valueOf(0))
                    .totalQuantity(2)
                    .reservationDate("2026-02-18")
                    .build(),
                ScheduleList
                    .builder()
                    .consumptionDate("2026-02-19")
                    .unitPrice(BigDecimal.valueOf(0))
                    .totalQuantity(2)
                    .reservationDate("2026-02-19")
                    .build()
            ))
            .build()))
        .build();

    BookingChannel bookingChannel = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .build();

    return new ReservationRequest(
        Collections.singletonList(res1),
        bookingChannel, true);
  }

  private ReservationRequest createValidHotelReservationRequestWithEmptyPackages(boolean isgetReservationsByIds) {

    RoomRateReservation roomRateReservation = RoomRateReservation.builder()
        .start("2022-10-20")
        .end("2022-10-20")
        .roomType("SBD")
        .ratePlanCode("DAILY")
        .cellCode("ABC")
        .build();

    uk.co.whitbread.ohip.domain.model.reservation.in.Reservation res1 =
        uk.co.whitbread.ohip.domain.model.reservation.in.Reservation.builder()
            .hotelId("HOTELTEST")
            .arrival("2021-11-05")
            .departure("2021-11-06")
            .adults(1)
            .children(0)
            .cotRequired(false)
            .externalReferenceId("12345")
            .roomRates(roomRateReservation)
            .gdsReferenceNumber("ABCD1234")
            .bookingNotes("NONSMOKING")
            .distributionUsername("testDistUsername")
            .distributionIATANumber("00000000123456")
            .bookingType("ANON")
            .operaCompanyId("123")
            .reservationPackages(Collections.singletonList(ReservationPackages.builder().packageCode("").build()))
            .build();

    BookingChannel bookingChannel = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .build();

    return new ReservationRequest(
        Collections.singletonList(res1),
        bookingChannel,
        isgetReservationsByIds);
  }

  private ReservationRequest createValidHotelReservationRequestForIATA(
      boolean isgetReservationsByIds) {

    RoomRateReservation roomRateReservation = RoomRateReservation.builder()
        .start("2022-10-20")
        .end("2022-10-20")
        .roomType("SBD")
        .ratePlanCode("DAILY")
        .cellCode("ABC")
        .build();

    uk.co.whitbread.ohip.domain.model.reservation.in.Reservation res1 =
        uk.co.whitbread.ohip.domain.model.reservation.in.Reservation.builder()
            .hotelId("HOTELTEST")
            .arrival("2021-11-05")
            .departure("2021-11-06")
            .adults(1)
            .children(0)
            .cotRequired(false)
            .externalReferenceId("12345")
            .roomRates(roomRateReservation)
            .gdsReferenceNumber("ABCD1234")
            .bookingNotes("NONSMOKING")
            .distributionUsername("testDistUsername")
            .distributionIATANumber("00000000123456")
            .bookingType("ANON")
            .distributionIATANumber("TESTIATA")
            .build();

    BookingChannel bookingChannel = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .build();

    return new ReservationRequest(
        Collections.singletonList(res1),
        bookingChannel,
        isgetReservationsByIds);
  }

  private List<ReservationCreationResponse> mockReservationCreationResponse() {
    return Collections.singletonList(ReservationCreationResponse.builder()
        .reservationId("1234")
        .createDateTime("2021-11-01T22:00:00Z")
        .roomStay(mockRoomStayReservation())
        .build());
  }

  private ReservationResponse mockReservationResponse() {
    return ReservationResponse.builder()
        .reservations(mockReservationCreationResponse())
        .totalCost(BigDecimal.valueOf(15984))
        .build();
  }

  private BookingChannelInfoResponseDto mockBookingChannelInfoResponse() {
    return BookingChannelInfoResponseDto.builder()
        .sourceId("44")
        .build();
  }

  private ChannelRuleResponseDto mockChannelSourceInfoResponse() {
    return ChannelRuleResponseDto.builder().sourceId("38")
        .requestDetails(
            ChannelRuleRequestDetailsDto.builder().language("N/A").channel("DISTR").build())
        .build();
  }

  private BusinessAllowanceRuleResponseDto mockAllowances() {
    BusinessAllowanceRuleDto businessAllowanceRuleDto = new BusinessAllowanceRuleDto();
    businessAllowanceRuleDto.setPms("OP");
    businessAllowanceRuleDto.setSourceId("allowance");
    businessAllowanceRuleDto.setTargetId("target");
    BusinessAllowanceRuleResponseDto responseDto = new BusinessAllowanceRuleResponseDto();
    responseDto.setBusinessAllowances(List.of(businessAllowanceRuleDto));
    return responseDto;
  }

  private ReservationStatusOhipDto reservationStatusOhipDto() {
    ReservationStatusOhipDto reservationStatusOhipDto = new ReservationStatusOhipDto();
    InstanceLink instanceLink = new InstanceLink();
    instanceLink.setHref("https://reservation/1234");
    reservationStatusOhipDto.setLinks(Collections.singletonList(instanceLink));
    return reservationStatusOhipDto;
  }

  private ReservationPackagesRequest createSavePackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setReservationsId(Collections.singletonList("123456"));
    reservationPackagesRequest.setHotelId("HOTELCODE");
    reservationPackagesRequest.setArrival("2022-04-02");
    reservationPackagesRequest.setDeparture("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(
        Collections.singletonList(createRoomsSelections()));
    return reservationPackagesRequest;
  }

  private ReservationPackagesRequest mockReservationPackagesRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setReservationsId(Collections.singletonList("0"));
    reservationPackagesRequest.setHotelId("HOTELCODE");
    reservationPackagesRequest.setArrival("2022-04-02");
    reservationPackagesRequest.setDeparture("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(
        new ArrayList<>(List.of(createRoomsSelections())));
    return reservationPackagesRequest;
  }

  private PackagesSelection createPackagesSelection() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("PIBTEST");

    return packagesSelection;
  }

  private PackagesSelection createPackagesSelectionBookingFee() {
    PackagesSelection packagesSelection = new PackagesSelection();

    packagesSelection.setNoSelections(1);
    packagesSelection.setId("ZN0000");
    packagesSelection.setPrice(24.50);

    return packagesSelection;
  }

  private RoomsSelections createRoomsSelectionsBookingFee() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(new ArrayList<>(Collections.singletonList(createPackagesSelectionBookingFee())));

    return roomsSelections;
  }

  private ReservationPackagesRequest mockReservationPackagesBookingFeeRequest() {
    ReservationPackagesRequest reservationPackagesRequest = new ReservationPackagesRequest();

    reservationPackagesRequest.setReservationsId(Collections.singletonList("123456"));
    reservationPackagesRequest.setHotelId("HOTELCODE");
    reservationPackagesRequest.setArrival("2022-04-02");
    reservationPackagesRequest.setDeparture("2022-04-03");

    reservationPackagesRequest.setRoomsSelections(
        new ArrayList<>(List.of(createRoomsSelectionsBookingFee())));
    return reservationPackagesRequest;
  }

  private RoomsSelections createRoomsSelections() {
    RoomsSelections roomsSelections = new RoomsSelections();

    roomsSelections.setPackagesSelection(new ArrayList<>(Collections.singletonList(createPackagesSelection())));

    return roomsSelections;
  }

  private ChangeReservation mockChangeReservation() {

    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("123456");
    reservationIdItem.setType("Reservation");

    HotelReservationInstructionType hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setHotelId("HOTELCODE");
    hotelReservationInstructionType.setReservationIdList(
        Collections.singletonList(reservationIdItem));

    ReservationPackageType reservationPackages = new ReservationPackageType();
    reservationPackages.setEndDate(LocalDate.of(2022, 4, 3));
    reservationPackages.setStartDate(LocalDate.of(2022, 4, 2));
    reservationPackages.setPackageCode("PIBTEST");
    reservationPackages.setPackageGroup("MDP");

    ReservationPackageScheduleType reservationPackageScheduleType = new ReservationPackageScheduleType();
    reservationPackageScheduleType.setReservationDate(reservationPackages.getStartDate());
    reservationPackageScheduleType.setUnitPrice(BigDecimal.valueOf(24.99));
    reservationPackageScheduleType.setConsumptionDate(reservationPackages.getStartDate());

    PackageCodeHeaderType packageCodeHeaderType = new PackageCodeHeaderType();
    var characterUDFType = new CharacterUDFType();
    characterUDFType.setName("UDFC15");
    characterUDFType.setValue("cellCode");

    var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(new ArrayList<>(List.of(characterUDFType)));
    hotelReservationInstructionType.setUserDefinedFields(userDefinedFields);
    reservationPackages.setPackageHeaderType(packageCodeHeaderType);
    reservationPackages.setScheduleList(Collections.singletonList(reservationPackageScheduleType));
    hotelReservationInstructionType.setReservationPackages(
        Collections.singletonList(reservationPackages));
    ChangeReservation changeReservation = new ChangeReservation();
    changeReservation.setReservations(Collections.singletonList(hotelReservationInstructionType));

    return changeReservation;

  }

  private CommentInfoType mockComments() {
    CommentInfoType commentInfoType = new CommentInfoType();
    CommentType commentType = new CommentType();
    commentType.setType(QuestionAndAnswerTypeEnum.PUR_ORD_QNA.name());
    commentInfoType.setComment(commentType);
    return commentInfoType;
  }

  private ChangeReservation mockChangeReservationWithComments() {

    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("123456");
    reservationIdItem.setType("Reservation");

    HotelReservationInstructionType hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setHotelId("HOTELCODE");
    hotelReservationInstructionType.setReservationIdList(
        Collections.singletonList(reservationIdItem));
    hotelReservationInstructionType.setComments(List.of(mockComments()));
    ReservationPackageType reservationPackages = new ReservationPackageType();
    reservationPackages.setEndDate(LocalDate.of(2022, 4, 3));
    reservationPackages.setStartDate(LocalDate.of(2022, 4, 2));
    reservationPackages.setPackageCode("PIBTEST");

    ReservationPackageScheduleType reservationPackageScheduleType = new ReservationPackageScheduleType();
    reservationPackageScheduleType.setReservationDate(reservationPackages.getStartDate());
    reservationPackageScheduleType.setUnitPrice(BigDecimal.valueOf(24.99));
    reservationPackageScheduleType.setConsumptionDate(reservationPackages.getStartDate());

    PackageCodeHeaderType packageCodeHeaderType = new PackageCodeHeaderType();

    var characterUDFType = new CharacterUDFType();
    characterUDFType.setName("UDFC11");
    characterUDFType.setValue("purchase123");

    var userDefinedFields = new UserDefinedFieldsType();
    userDefinedFields.setCharacterUDFs(new ArrayList<>(List.of(characterUDFType)));
    hotelReservationInstructionType.setUserDefinedFields(userDefinedFields);
    hotelReservationInstructionType.setCustomReference("customRef123");

    reservationPackages.setPackageHeaderType(packageCodeHeaderType);
    reservationPackages.setScheduleList(Collections.singletonList(reservationPackageScheduleType));
    hotelReservationInstructionType.setReservationPackages(
        Collections.singletonList(reservationPackages));
    ChangeReservation changeReservation = new ChangeReservation();
    changeReservation.setReservations(Collections.singletonList(hotelReservationInstructionType));

    return changeReservation;

  }

  private Mono<PackagesResponseOhipDto> getPackagesFromOhip() throws IOException {
    return Mono.just(PackagesResponseOhipDto.builder()
        .packageCodesList(mapper.readValue(PackagesResponseOhipMapperTest.class.getClassLoader()
                .getResource(MOCK_PACKAGES_RESPONSE_JSON),
            PackagesInfoPackageCodesList.class))
        .build());
  }

  private Mono<PackageGroupsInfo> mockPackageGroupsFromOhip() {
    var packageGroupsInfo = new PackageGroupsInfo();
    var packageGroupsInfoPackageGroupList = new PackageGroupsInfoPackageGroupList();
    packageGroupsInfo.setPackageGroupList(packageGroupsInfoPackageGroupList);

    return Mono.just(packageGroupsInfo);
  }

  private Mono<PackageGroupsInfo> mockMdpPackageGroupsFromOhip() {
    var packageGroupsInfo = new PackageGroupsInfo();
    var packageGroupList = new PackageGroupsInfoPackageGroupList();
    var hotelPackageGroupsType = new HotelPackageGroupsType();
    var packageGroup = new PackageGroupType();

    var mdbeva = new PackageCodeType();
    mdbeva.setCode("MDBEVA");
    var mdbfst = new PackageCodeType();
    mdbfst.setCode("MDBFST");

    packageGroup.setCode("MDP");
    packageGroup.setMembersList(Arrays.asList(mdbeva, mdbfst));

    hotelPackageGroupsType.setPackageGroup(List.of(packageGroup));
    packageGroupList.setPackageGroups(List.of(hotelPackageGroupsType));
    packageGroupsInfo.setPackageGroupList(packageGroupList);

    return Mono.just(packageGroupsInfo);
  }

  private ReservationPackagesResponse mockReservationsPackagesResponse() {
    return ReservationPackagesResponse.builder()
        .roomsSelections(new ArrayList<>(Arrays.asList(
            uk.co.whitbread.ohip.domain.model.reservation.out.RoomsSelections.builder()
                .packagesSelection(new ArrayList<>(Arrays.asList(
                    uk.co.whitbread.ohip.domain.model.reservation.out.PackagesSelection.builder()
                        .noSelections(2).id("TEST").packageGroup("MDP").build()))).build())))
        .build();
  }

  private Mono<CancelReservationDetails> mockCancelReservationResponse() {
    CancelReservationDetails details = new CancelReservationDetails();
    CancelReservationType cancelReservationType = new CancelReservationType();
    UniqueIDType uniqueIDType = new UniqueIDType();
    uniqueIDType.setType("Cancellation");
    uniqueIDType.setType("9876");
    cancelReservationType.setReservationIdList(Collections.singletonList(uniqueIDType));
    details.setReservations(Collections.singletonList(cancelReservationType));
    return Mono.just(details);
  }

  private Reservation mockReservationForCancel() {
    var reservation = new Reservation();

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("TestHotelId");

    var resCashieringType = new ResCashieringType();
    var taxType = new ReservationTaxTypeInfo();
    taxType.setCode("CITYTAX");
    resCashieringType.setTaxType(taxType);

    hotelReservationType.setCashiering(resCashieringType);

    var reservationStatus = PMSResStatusType.RESERVED;

    hotelReservationType.setReservationStatus(reservationStatus);
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    reservation.setReservations(hotelReservationsType);

    var policyDeadlineType = new PolicyDeadlineType();
    policyDeadlineType.setAbsoluteDeadline(new Date(2022, Calendar.DECEMBER, 10));

    var cancelPenaltyType = new ResCancelPenaltyType();
    cancelPenaltyType.setDeadline(policyDeadlineType);

    var cancellationPolicies = new ResCancellationPolicyType();
    cancellationPolicies.setPolicy(cancelPenaltyType);

    var reservationPoliciesType = new ReservationPoliciesType();
    reservationPoliciesType.setCancellationPolicies(
        Collections.singletonList(cancellationPolicies));

    var roomRateType = new RoomRateType();
    roomRateType.setRatePlanCode("SEMIFLEX");

    var roomStayType = new RoomStayType();
    roomStayType.setRoomRates(List.of(roomRateType));

    var guarantee = new ResGuaranteeType();
    guarantee.setOnHold(true);
    roomStayType.setGuarantee(guarantee);

    hotelReservationType.setReservationPolicies(reservationPoliciesType);
    hotelReservationType.setRoomStay(roomStayType);

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("2434");
    uniqueIdType.setType("Va");
    var creditCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    creditCard.setCardId(uniqueIdType);
    creditCard.setCardType(CardTypeType.VA);

    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("DVA");
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);
    reservationPaymentMethod.setPaymentCard(creditCard);

    hotelReservationType.setReservationPaymentMethods(
        Collections.singletonList(reservationPaymentMethod));

    return reservation;
  }

  private Reservation mockReservationWithEmptyCancellationPolicy() {
    var reservation = new Reservation();

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("TestHotelId");
    UniqueIDType reservation1 = new UniqueIDType();
    reservation1.setId("Reservation1");
    UniqueIDType reservation2 = new UniqueIDType();
    reservation2.setId("Reservation2");
    hotelReservationType.setReservationIdList(List.of(reservation1, reservation2));
    hotelReservationType.setHotelId("TestHotelId");

    var resCashieringType = new ResCashieringType();
    var taxType = new ReservationTaxTypeInfo();
    taxType.setCode("CITYTAX");
    resCashieringType.setTaxType(taxType);

    hotelReservationType.setCashiering(resCashieringType);

    var reservationStatus = PMSResStatusType.RESERVED;

    hotelReservationType.setReservationStatus(reservationStatus);
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    reservation.setReservations(hotelReservationsType);

    var policyDeadlineType = new PolicyDeadlineType();
    policyDeadlineType.setAbsoluteDeadline(new Date(2022, Calendar.DECEMBER, 10));

    var cancelPenaltyType = new ResCancelPenaltyType();
    cancelPenaltyType.setDeadline(policyDeadlineType);

    var cancellationPolicies = new ResCancellationPolicyType();
    cancellationPolicies.setPolicy(cancelPenaltyType);

    var reservationPoliciesType = new ReservationPoliciesType();
    reservationPoliciesType.setCancellationPolicies(Collections.EMPTY_LIST);

    var roomRateType = new RoomRateType();
    roomRateType.setRatePlanCode("SEMIFLEX");

    var roomStayType = new RoomStayType();
    roomStayType.setRoomRates(List.of(roomRateType));

    var guarantee = new ResGuaranteeType();
    guarantee.setOnHold(true);
    roomStayType.setGuarantee(guarantee);

    hotelReservationType.setReservationPolicies(reservationPoliciesType);
    hotelReservationType.setRoomStay(roomStayType);

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("2434");
    uniqueIdType.setType("Va");
    var creditCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    creditCard.setCardId(uniqueIdType);
    creditCard.setCardType(CardTypeType.VA);

    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("DVA");
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);
    reservationPaymentMethod.setPaymentCard(creditCard);

    hotelReservationType.setReservationPaymentMethods(
        Collections.singletonList(reservationPaymentMethod));

    return reservation;
  }

  private Reservation mockReservationWithNullCancellationPolicy() {
    var reservation = new Reservation();

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("TestHotelId");
    UniqueIDType reservation1 = new UniqueIDType();
    reservation1.setId("Reservation1");
    UniqueIDType reservation2 = new UniqueIDType();
    reservation2.setId("Reservation2");
    hotelReservationType.setReservationIdList(List.of(reservation1, reservation2));

    var resCashieringType = new ResCashieringType();
    var taxType = new ReservationTaxTypeInfo();
    taxType.setCode("CITYTAX");
    resCashieringType.setTaxType(taxType);

    hotelReservationType.setCashiering(resCashieringType);

    var reservationStatus = PMSResStatusType.RESERVED;

    hotelReservationType.setReservationStatus(reservationStatus);
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    reservation.setReservations(hotelReservationsType);

    var reservationPoliciesType = new ReservationPoliciesType();
    reservationPoliciesType.setCancellationPolicies(null);

    var roomRateType = new RoomRateType();
    roomRateType.setRatePlanCode("SEMIFLEX");

    var roomStayType = new RoomStayType();
    roomStayType.setRoomRates(List.of(roomRateType));

    var guarantee = new ResGuaranteeType();
    guarantee.setOnHold(true);
    roomStayType.setGuarantee(guarantee);

    hotelReservationType.setReservationPolicies(reservationPoliciesType);
    hotelReservationType.setRoomStay(roomStayType);

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("2434");
    uniqueIdType.setType("Va");
    var creditCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    creditCard.setCardId(uniqueIdType);
    creditCard.setCardType(CardTypeType.VA);

    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("DVA");
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);
    reservationPaymentMethod.setPaymentCard(creditCard);

    hotelReservationType.setReservationPaymentMethods(
        Collections.singletonList(reservationPaymentMethod));

    return reservation;
  }


  private HotelDetails mockHotelDetailsResponse() {
    var hotelDetails = new HotelDetails();

    var hotelDateTime = new HotelInfoTypePropertyControlsDateTimeFormatting();
    hotelDateTime.setTimeZoneRegion("Europe/London");

    var hotelInfoTypePropertyControls = new HotelInfoTypePropertyControls();
    hotelInfoTypePropertyControls.setDateTimeFormatting(hotelDateTime);

    var hotelInfoType = new HotelInfoType();
    hotelInfoType.setPropertyControls(hotelInfoTypePropertyControls);

    hotelDetails.setHotelConfigInfo(hotelInfoType);

    return hotelDetails;
  }

  private UpdateDiscountRequest mockUpdateDiscountRequest() {
    return UpdateDiscountRequest.builder()
        .hotelId("HOTELCODE")
        .currency("GBP")
        .reservationIds(Set.of("100100"))
        .discountAmount(BigDecimal.valueOf(5))
        .build();
  }

  private BusinessItemsRequest mockBusinessItemRequest() {
    return BusinessItemsRequest.builder()
        .reservationIds(Set.of("123456"))
        .hotelId("HOTELCODE")
        .companyId("1248097")
        .businessItems(BusinessItems.builder()
            .purchaseOrderNumber("1010101010")
            .customReferenceNumber("11001100")
            .businessAllowances(List.of(BusinessAllowance.builder()
                .budget(BigDecimal.ZERO)
                .allowance("allowance")
                .isAuthorised(Boolean.TRUE)
                .build()))
            .businessNotes("TestNote")
            .build())
        .build();
  }

  private BusinessItemsRequest mockNullBusinessItemRequest() {
    return BusinessItemsRequest.builder()
        .reservationIds(Set.of("100100"))
        .hotelId("HOTELCODE")
        .companyId("1248097")
        .channel("DISTR")
            .build();
  }

  private BusinessItemsRequest mockNullBusinessItemPrePaidRequest() {
    return BusinessItemsRequest.builder()
        .reservationIds(Set.of("100100"))
        .hotelId("HOTELCODE")
        .companyId("1248097")
        .channel("PREPAID")
        .build();
  }

  private CompanyQuestionAndAnswerDetailsRequest mockCompanyQuestionAndAnswerDetailsRequest() {
    return CompanyQuestionAndAnswerDetailsRequest.builder()
        .reservationIds(Set.of("100100"))
        .hotelId("HOTELCODE")
        .companyQuestionAndAnswerDetails(CompanyQuestionAndAnswerDetails.builder()
            .customerReferenceQuestionAndAnswer(
                CompanyQuestionAndAnswer.builder().question("Who am I?").answer(
                    "Test").build())
            .purchaseOrderQuestionAndAnswer(
                CompanyQuestionAndAnswer.builder().question("Who am I?").answer(
                    "Test").build())
            .userDefinedQuestionAndAnswers(
                List.of(CompanyQuestionAndAnswer.builder().question("Who am I?").answer(
                    "Test").build())).build()).build();
  }

  private BusinessItemsRequest mockBusinessItemRequestWithotPurchasOrder() {
    return BusinessItemsRequest.builder()
        .reservationIds(Set.of("100100"))
        .hotelId("HOTELCODE")
        .businessItems(BusinessItems.builder()
            .businessAllowances(List.of(BusinessAllowance.builder()
                .budget(BigDecimal.ZERO)
                .allowance("allowance")
                .isAuthorised(Boolean.TRUE)
                .build()))
            .businessNotes("TestNote")
            .build())
        .build();
  }

  private RatePlanRoomTypeChangeRequest mockRatePlanChangeRequest() {
    return RatePlanRoomTypeChangeRequest.builder()
        .hotelId("HOTELCODE")
        .reservationIds(Collections.singletonList("100100"))
        .basketReferenceId("HOTELCODE1001001")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .currency("GBP")
        .rateCode("FLEXRATE")
        .roomTypes(Collections.singletonList("DOUBLE"))
        .adultsNumber(Collections.singletonList(1))
        .childrenNumber(Collections.singletonList(0))
        .build();
  }

  private RatePlanRoomTypeChangeRequest mockRoomTypeChangeRequest() {
    return RatePlanRoomTypeChangeRequest.builder()
        .hotelId("HOTELCODE")
        .reservationIds(Collections.singletonList("100100"))
        .basketReferenceId("HOTELCODE1001001")
        .startDate("2022-03-01")
        .endDate("2022-03-03")
        .currency("GBP")
        .rateCode("FLEXRATE")
        .roomTypes(Collections.singletonList("VDOUBL"))
        .adultsNumber(Collections.singletonList(1))
        .childrenNumber(Collections.singletonList(0))
        .build();
  }

  private Reservation mockReservationRatePlane() {
    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("100100");
    reservationIdItem.setType("Reservation");

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELCODE");
    hotelReservationType.setReservationIdList(Collections.singletonList(reservationIdItem));

    RoomStayType roomStayType = new RoomStayType();
    RoomRateType roomRatesTmp = new RoomRateType();
    RatesType rates = new RatesType();
    TotalType base = new TotalType();
    base.amountBeforeTax(BigDecimal.valueOf(200));

    AmountType amountType = new AmountType();
    amountType.setBase(base);
    amountType.setStart(LocalDate.of(2022, 3, 1));
    amountType.setEnd(LocalDate.of(2022, 3, 3));
    rates.addRateItem(amountType);
    roomRatesTmp.setRoomType("DOUBLE");

    roomRatesTmp.setRatePlanCode("FLEXRATE");
    roomRatesTmp.setRates(rates);
    roomRatesTmp.setSourceCode("44");
    roomStayType.addRoomRatesItem(roomRatesTmp);

    hotelReservationType.setRoomStay(roomStayType);
    hotelReservationType.setHotelId("HOTELCODE");

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));

    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    return reservation;
  }

  private RateCodeCriteria mockRateCodeCriteria() {
    return RateCodeCriteria.builder()
        .hotelId("HOTELCODE")
        .ratePlanCode("FLEXRATE")
        .arrivalDate("2022-03-01")
        .departureDate("2022-03-03")
        .roomInfoCriteriaList(new ArrayList<>())
        .build();

  }

  private RateCodeRoomInfoCriteria mockRateCodeRoomInfoCriteria() {
    return RateCodeRoomInfoCriteria.builder()
        .roomType("DOUBLE")
        .adultsNo(1)
        .childrenNo(0)
        .build();
  }

  private PriceBreakdownDto mockPriceBreakdownDto() {
    return PriceBreakdownDto.builder()
        .summary(mockSummaryDto())
        .build();
  }

  private SummaryDto mockSummaryDto() {
    return SummaryDto.builder()
        .currencyCode("GBP")
        .start("2022-03-01")
        .end("2022-03-03")
        .totalCostOfStay(BigDecimal.valueOf(260))
        .outStandingCostOfStay(BigDecimal.valueOf(260))
        .deposit(BigDecimal.valueOf(-100))
        .net(BigDecimal.valueOf(180))
        .gross(BigDecimal.valueOf(200))
        .build();
  }

  private Mono<ChangeReservationDetails> mockChangeRateReservationResponse() {
    ChangeReservationDetails detailes = new ChangeReservationDetails();
    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("100100");
    reservationIdItem.setType("Reservation");
    HotelReservationsType reservations = new HotelReservationsType();
    HotelReservationType reservation = new HotelReservationType();
    RoomStayType roomStay = new RoomStayType();
    roomStay.setArrivalDate(LocalDate.parse("2022-03-01"));
    roomStay.setDepartureDate(LocalDate.parse("2022-03-03"));
    reservation.setRoomStay(roomStay);
    reservation.setHotelId("HOTELCODE");
    reservation.setReservationIdList(List.of(reservationIdItem));
    reservation.setReservationStatus(PMSResStatusType.RESERVED);
    reservations.setReservation(List.of(reservation));
    detailes.setReservations(reservations);
    return Mono.just(detailes);
  }

  private Mono<ChangeReservationDetails> mockChangeDiscountReservationResponse() {
    ChangeReservationDetails details = new ChangeReservationDetails();
    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("100100");
    reservationIdItem.setType("Reservation");
    HotelReservationsType reservations = new HotelReservationsType();
    HotelReservationType reservation = new HotelReservationType();
    RoomStayType roomStay = new RoomStayType();
    roomStay.setArrivalDate(LocalDate.parse("2022-03-01"));
    roomStay.setDepartureDate(LocalDate.parse("2022-03-03"));
    RoomRateType roomRateType = new RoomRateType();
    RatesType ratesType = new RatesType();
    AmountType amountType = new AmountType();
    DiscountType discountType = new DiscountType();
    discountType.setAmount(BigDecimal.valueOf(10));
    discountType.setCurrencyCode("GBP");
    discountType.setDiscountCode("PR");
    discountType.discountReason("Promotion");
    amountType.setDiscount(discountType);
    ratesType.addRateItem(amountType);
    roomStay.setRoomRates(Collections.singletonList(roomRateType));
    reservation.setRoomStay(roomStay);
    reservation.setHotelId("HOTELCODE");
    reservation.setReservationIdList(List.of(reservationIdItem));
    reservation.setReservationStatus(PMSResStatusType.RESERVED);
    reservations.setReservation(List.of(reservation));
    details.setReservations(reservations);
    return Mono.just(details);
  }

  private ProfileSummaries mockProfileSummaries() throws IOException {

    return mapper.readValue(
        HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(MOCK_PROFILE_SUMMARIES_RESPONSE_JSON),
        ProfileSummaries.class);
  }

  private List<Profile> mockProfileList() throws IOException {

    return Collections.singletonList(
        mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
            .getResource(MOCK_PROFILE_RESPONSE_JSON), Profile.class));
  }

  private ReservationsDetails mockReservationList() throws IOException {

    return mapper.readValue(HotelReservationOutPortImplTest.class.getClassLoader()
        .getResource(MOCK_RESERVATION_DETAILS_RESPONSE_JSON), ReservationsDetails.class);
  }

  private List<SearchBooking> mockBookingSearchReservationDetails(int size) {
    var searchBookingsResults = new ArrayList<SearchBooking>();
    for (int index = 0; index < size; index++) {
      SearchBookingBooker booker = SearchBookingBooker.builder()
          .firstName("norrix")
          .lastName("max")
          .profileId("1148167")
          .title("Dr")
          .build();
      SearchBookingStayingGuest stayingGuest = SearchBookingStayingGuest.builder()
          .firstName("John")
          .lastName("Doe")
          .profileId("1148169")
          .title("Dr")
          .build();
      SearchBookingReservation searchBookingReservation = SearchBookingReservation.builder()
          .reservationId("377670")
          .confirmationId("4659966")
          .stayingGuest(stayingGuest)
          .build();
      SearchBooking searchBookingReservationDetail = SearchBooking.builder()
          .bookingReference("AKU9758324")
          .hotelId("LONEUS")
          .hotelName("London Euston")
          .status("Reserved")
          .booker(booker)
          .arrivalDate(LocalDate.now().plusDays(1))
          .departureDate(LocalDate.now().plusDays(3))
          .reservations(Collections.singletonList(searchBookingReservation))
          .build();

      searchBookingsResults.add(searchBookingReservationDetail);
    }

    return searchBookingsResults;
  }

  private VatRuleResponseDto mockVatRules() {
    TransactionCodeDto tr = new TransactionCodeDto();
    tr.setPkgCode("__ACCMOD__");
    tr.setTranCode("9016");
    VatRuleResponseDto result = new VatRuleResponseDto();
    result.setTranCodes(Collections.singletonList(tr));
    return result;
  }

  private DepositFolioRequest mockDepositFolioConfirmationRequest() {
    return DepositFolioRequest.builder().hotelId("HOTELTEST").reservationId("RESERVATION_ID")
        .paymentOption(PaymentOption.PAY_NOW).paymentId("PAYMENT_ID").isCancelRequest(false)
        .paymentMethod("DAX")
        .paymentCard(
            PaymentCard.builder().cardType("AX").token("3492404973517136665")
                .expirationDate("2025-03-31").cardNumberLast4Digits("6665").cardHolderName("Name")
                .build()).build();
  }


  private ConfirmReservationRequest createConfirmPrePaidReservationRequest() {
    return ConfirmReservationRequest.builder()
        .reservationId("RESERVATION_ID")
        .hotelId("HOTELTEST")
        .paymentOption(PaymentOption.PAY_NOW)
        .paymentMethod("DAX")
        .paymentCard(PaymentCard.builder().cardType("AX").token("3492404973517136665")
            .expirationDate("2025-03-31").cardNumberLast4Digits("6665").cardHolderName("Name")
            .build())
        .build();
  }

  private ConfirmReservationRequest createConfirmReservationRequest(PaymentOption paymentOption) {
    return ConfirmReservationRequest.builder()
            .reservationId("RESERVATION_ID")
            .hotelId("HOTELTEST")
            .paymentOption(paymentOption)
            .paymentMethod("CA")
            .build();
  }

  private ConfirmReservationResponse mockConfirmReservationResponse() {
    return ConfirmReservationResponse.builder()
        .hotelId("HOTELTEST")
        .reservationStatus("Reserved")
        .build();
  }

  private Reservation mockMarketingReservationResponse() {

    final var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("MANOLD");

    final var reservationUniqueIdType = new UniqueIDType();
    reservationUniqueIdType.setType("Reservation");
    reservationUniqueIdType.setId("123456");
    hotelReservationType.setReservationIdList(Collections.singletonList(reservationUniqueIdType));

    final var hotelReservationTypeReservationProfiles = new HotelReservationTypeReservationProfiles();
    final var reservationProfileType = new ReservationProfileType();
    final var profileUniqueIdType = new UniqueIDType();
    profileUniqueIdType.setType("Profile");
    profileUniqueIdType.setId("123456");
    reservationProfileType.setProfileIdList(Collections.singletonList(profileUniqueIdType));
    reservationProfileType.setReservationProfileType(ResProfileTypeType.RESERVATIONCONTACT);
    hotelReservationTypeReservationProfiles.setReservationProfile(
        Collections.singletonList(reservationProfileType));

    hotelReservationType.setReservationProfiles(hotelReservationTypeReservationProfiles);

    hotelReservationType.setReservationProfiles(hotelReservationTypeReservationProfiles);

    final var reservation = new Reservation();
    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    reservation.setReservations(hotelReservationsType);

    return reservation;
  }

  private ItemInventoryResponseDto mockItemInventoryResponse(int i) {
    return ItemInventoryResponseDto.builder()
        .itemsInventory(Collections.singletonList(ItemInventoryDto.builder()
            .code("COT")
            .name("Cot")
            .inventories(Collections.singletonList(InventoryAvailabilityDto.builder()
                .available(i)
                .build()))
            .build()))
        .build();
  }

  private CancellationPolicyDetails mockCancellationPolicyDetails() {
    ConfigCancelPenaltyType configCancelPenaltyType = new ConfigCancelPenaltyType();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.PolicyDeadlineType policyDeadlineType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.hotel.config.PolicyDeadlineType();

    policyDeadlineType.setAbsoluteDeadline(mockDate());
    policyDeadlineType.setOffsetFromArrival(0);
    policyDeadlineType.setOffsetDropTime(mockDate());

    configCancelPenaltyType.setPolicyCode("DOA");
    configCancelPenaltyType.setDeadline(policyDeadlineType);
    configCancelPenaltyType.setPenaltyDescription(
        "Cancellations after 1pm on the day of arrival charged 100% of 1 night");

    HotelConfigCancelPenaltiesType cancelPenaltiesType = new HotelConfigCancelPenaltiesType();
    cancelPenaltiesType.setCancelPenaltyConfig(Collections.singletonList(configCancelPenaltyType));

    CancellationPolicyDetailsCancelPenalties cancelPenalties = new CancellationPolicyDetailsCancelPenalties();
    cancelPenalties.setCancelPenalties(Collections.singletonList(cancelPenaltiesType));

    CancellationPolicyDetails cancellationPolicyDetails = new CancellationPolicyDetails();
    cancellationPolicyDetails.setCancelPenalties(cancelPenalties);

    return cancellationPolicyDetails;
  }

  private PolicySchedulesDetails mockPolicySchedulesDetails() {
    PolicyScheduleType policyScheduleType = new PolicyScheduleType();
    PolicyScheduleDetailType policyScheduleDetailType = new PolicyScheduleDetailType();
    CodeDescriptionType codeDescriptionType = new CodeDescriptionType();

    codeDescriptionType.setCode("DOA");
    codeDescriptionType.setDescription(
        "Cancellations after 1pm on the day of arrival charged 1 night");

    policyScheduleDetailType.setPolicy(codeDescriptionType);
    policyScheduleType.setScheduleDetail(policyScheduleDetailType);

    PolicySchedulesDetails policySchedulesDetails = new PolicySchedulesDetails();
    policySchedulesDetails.setPolicySchedules(Collections.singletonList(policyScheduleType));

    return policySchedulesDetails;
  }

  private Status mockStatus(String profileId) {
    var instanceLinks = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.InstanceLink();
    instanceLinks.setHref("https://reservation/" + profileId);

    var status = new Status();
    status.setLinks(List.of(instanceLinks));

    return status;
  }

  private AccompanyingGuestDetails createAccompanyingGuestDetails() {
    return AccompanyingGuestDetails.builder()
        .title("Mr")
        .firstName("Fake")
        .lastName("Data")
        .emailAddress("fake.email@fakedomain.com")
        .build();
  }

  private ReservationGuestRequest createGuestReservationRequest(String language) {
    final String hotelId = "MANOLD";

    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .companyName("Whitbread")
        .build();

    final BookerDetails booker = BookerDetails
        .builder()
        .title("Mrs")
        .firstName("John")
        .lastName("McEnroe")
        .emailAddress("john.mcenroe@mail.com")
        .mobile("+39567463783")
        .address(address)
        .acceptFutureMailing(Boolean.FALSE)
        .build();
    if (StringUtils.isNotEmpty(language)) {
      booker.setLanguage(language);
    }

    final StayingGuestAddress guestAddress = StayingGuestAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .build();

    final StayingGuestAdditionalDetails guestAdditionalDetails = StayingGuestAdditionalDetails.builder()
        .dob(LocalDate.parse("1996-07-13"))
        .nationality("Briton")
        .passportNumber("ABCD1234")
        .build();

    final StayingGuestDetails guestDetails = StayingGuestDetails
        .builder()
        .title("Mrs")
        .firstName("Debbie")
        .lastName("Doe")
        .employeeAccountId("ABC123457")
        .emailAddress("debbie_doe@whitbread.com")
        .address(guestAddress)
        .additionalDetails(guestAdditionalDetails)
        .profileId("31783")
        .build();

    final StayingGuest stayingGuest = StayingGuest.builder()
        .reservationId("1234567")
        .sameAsBooker(false)
        .stayingGuestDetails(guestDetails)
        .isAccompanyingGuest(true)
        .build();

    final StayingGuestDetails guestDetails2 = StayingGuestDetails
        .builder()
        .title("Mrs")
        .firstName("Carla")
        .lastName("Dominic")
        .employeeAccountId("CHG124567")
        .emailAddress("carla_dominic@whitbread.com")
        .address(guestAddress)
        .additionalDetails(guestAdditionalDetails)
        .build();

    final StayingGuest stayingGuest2 = StayingGuest.builder()
        .reservationId("ABC467637")
        .sameAsBooker(false)
        .stayingGuestDetails(guestDetails2)
        .isAccompanyingGuest(true)
        .build();

    return ReservationGuestRequest.builder()
        .booker(booker)
        .stayingGuests(List.of(stayingGuest, stayingGuest2))
        .hotelId(hotelId)
        .reasonForStay("LEI")
        .sendEmailConfirmation(Boolean.TRUE)
        .sendEmailInvoice(Boolean.TRUE)
        .build();

  }

  private ReservationGuestRequest createAccompanyingGuestReservationRequest(
      Boolean accompanyingGuest) {
    final String hotelId = "MANOLD";

    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .companyName("Whitbread")
        .build();

    final BookerDetails booker = BookerDetails
        .builder()
        .title("Mrs")
        .firstName("John")
        .lastName("McEnroe")
        .emailAddress("john.mcenroe@mail.com")
        .mobile("+39567463783")
        .address(address)
        .acceptFutureMailing(Boolean.FALSE)
        .language(WB_LANGUAGE_CODE_EN)
        .build();

    final StayingGuestAddress guestAddress = StayingGuestAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK").addressId("12345")
        .build();

    final StayingGuestDetails guestDetails = StayingGuestDetails
        .builder()
        .title("Mrs")
        .firstName("Debbie")
        .lastName("Doe")
        .employeeAccountId("ABC123457")
        .emailAddress("debbie_doe@whitbread.com")
        .address(guestAddress)
        .profileId("31783")
        .build();

    final StayingGuest stayingGuest = StayingGuest.builder()
        .reservationId("1234567")
        .sameAsBooker(false)
        .stayingGuestDetails(guestDetails)
        .build();
    if (ObjectUtils.isNotEmpty(accompanyingGuest)) {
      stayingGuest.setIsAccompanyingGuest(accompanyingGuest);
    }

    return ReservationGuestRequest.builder()
        .booker(booker)
        .stayingGuests(List.of(stayingGuest))
        .hotelId(hotelId)
        .reasonForStay("LEI")
        .sendEmailConfirmation(Boolean.TRUE)
        .sendEmailInvoice(Boolean.TRUE)
        .preCheckIn(Boolean.TRUE)
        .build();
  }

  private List<StayingGuest> mockStayingGuestsDetails() {
    final StayingGuestAddress guestAddress = StayingGuestAddress.builder()
        .postalCode("MZC AD")
        .addressType("HOME")
        .addressLine1("4 Brockley Avenue")
        .countryCode("UK")
        .build();

    final StayingGuestDetails guestDetails1 = StayingGuestDetails
        .builder()
        .title("Mr")
        .firstName("John")
        .lastName("Smith")
        .employeeAccountId("ABC123458")
        .emailAddress("john_smith@whitbread.com")
        .address(guestAddress)
        .profileId("31784")
        .build();

    final StayingGuest stayingGuest1 = StayingGuest.builder()
        .reservationId("1234567")
        .sameAsBooker(false)
        .stayingGuestDetails(guestDetails1)
        .isAccompanyingGuest(false)
        .build();

    final StayingGuestDetails guestDetails2 = StayingGuestDetails
        .builder()
        .title("Ms")
        .firstName("Jane")
        .lastName("Doe")
        .employeeAccountId("ABC123459")
        .emailAddress("jane_doe@whitbread.com")
        .address(guestAddress)
        .profileId("31785")
        .build();

    final StayingGuest stayingGuest2 = StayingGuest.builder()
        .reservationId("1234569")
        .sameAsBooker(false)
        .stayingGuestDetails(guestDetails2)
        .isAccompanyingGuest(true)
        .build();

    final StayingGuestDetails guestDetails3 = StayingGuestDetails
        .builder()
        .title("Mr")
        .firstName("John")
        .lastName("Doe")
        .employeeAccountId("ABC123455")
        .emailAddress("john_doe@whitbread.com")
        .address(guestAddress)
        .profileId("31786")
        .build();

    final StayingGuest stayingGuest3 = StayingGuest.builder()
        .reservationId("1234566")
        .sameAsBooker(false)
        .stayingGuestDetails(guestDetails3)
        .isAccompanyingGuest(true)
        .build();

    final StayingGuestDetails guestDetails4 = StayingGuestDetails
        .builder()
        .title("Mr")
        .firstName("Adam")
        .lastName("Smith")
        .employeeAccountId("ABC123454")
        .emailAddress("adam_smith@whitbread.com")
        .address(guestAddress)
        .profileId("31784")
        .build();

    final StayingGuest stayingGuest4 = StayingGuest.builder()
        .reservationId("1234566")
        .sameAsBooker(false)
        .stayingGuestDetails(guestDetails4)
        .isAccompanyingGuest(true)
        .build();

    return List.of(stayingGuest1, stayingGuest2, stayingGuest3, stayingGuest4);
  }

  @NotNull
  private Profile mockProfile() {
    Profile result = new Profile();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType uniqueIDType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId("1234");
    uniqueIDType.setType("Reservation");
    result.setProfileIdList(List.of(uniqueIDType));
    result.setProfileDetails(new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType());
    return result;
  }

  @NotNull
  private Profile mockProfileBilling() {
    Profile result = new Profile();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType addressType = new
        uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType addressInfoType = new
        uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses profileTypeAddresses = new
        uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType profType = new
        uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();

    addressType.setType("BILLING");
    addressType.setCounty("UK");
    addressInfoType.setId("1234");
    addressInfoType.setType("BILLING");
    addressInfoType.setAddress(addressType);
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
    profType.setAddresses(profileTypeAddresses);

    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType uniqueIDType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId("1234");
    uniqueIDType.setType("Reservation");
    result.setProfileIdList(List.of(uniqueIDType));
    result.setProfileDetails(profType);
    return result;
  }

  @NotNull
  private Profile mockProfileBusiness() {
    Profile result = new Profile();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType addressType = new
            uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType addressInfoType = new
            uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressInfoType();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses profileTypeAddresses = new
            uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileTypeAddresses();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType profType = new
            uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();

    addressType.setType("BUSINESS");
    addressType.setCounty("UK");
    addressInfoType.setId("1234");
    addressInfoType.setType("BUSINESS");
    addressInfoType.setAddress(addressType);
    profileTypeAddresses.setAddressInfo(List.of(addressInfoType));
    profType.setAddresses(profileTypeAddresses);

    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType uniqueIDType =
            new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId("1234");
    uniqueIDType.setType("Reservation");
    result.setProfileIdList(List.of(uniqueIDType));
    result.setProfileDetails(profType);
    return result;
  }

  @NotNull
  private List<Profile> mockProfileToCompare(String profileId, String title, String givenName, String surname, String email, String postalCode) {
    Profile result = new Profile();
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType uniqueIDType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId(profileId);
    uniqueIDType.setType("Profile");
    result.setProfileIdList(List.of(uniqueIDType));
    var crmProfileType = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    var crmCustomer = new CustomerType();
    var crmPersonName = new PersonNameType();
    crmPersonName.setNameTitle(title);
    crmPersonName.setGivenName(givenName);
    crmPersonName.setSurname(surname);
    crmCustomer.setPersonName(List.of(crmPersonName));
    crmProfileType.setCustomer(crmCustomer);
    var emailDetails = new CompanyProfileTypeEmails();
    var emailInfo = new EmailInfoType();
    var emailType = new EmailType();
    emailType.setEmailAddress(email);
    emailInfo.setEmail(emailType);
    emailDetails.emailInfo(List.of(emailInfo));
    crmProfileType.setEmails(emailDetails);
    var address = new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.AddressType();
    address.setPostalCode(postalCode);
    var addressInfo = new AddressInfoType();
    addressInfo.setAddress(address);
    var profileAddress = new ProfileTypeAddresses();
    profileAddress.setAddressInfo(List.of(addressInfo));
    crmProfileType.setAddresses(profileAddress);
    result.setProfileDetails(crmProfileType);

    return List.of(result);
  }

  private ProfileType mockProfileType() {
    return ProfileType.builder()
        .profileId(ProfileIdResponse.builder().id("123456").type("Profile").build())
        .address(AddressTypeResponse.builder().id("123456").type("HOME").cityName("London").build())
        .email(EmailTypeResponse.builder().id("123456").type("HOME").build())
        .build();
  }

  private UpdateReservationsRequest mockUpdateReservationsRequestRequest() {

    var bc = BookingChannel.builder()
        .channel("PI")
        .subchannel("WEB")
        .language("EN")
        .build();

    var resType = ReservationType.builder()
        .type("Reservation")
        .id("1268956")
        .build();

    var roomOccupancy = new RoomOccupancy(1, 0);

    var rateType = RateType.builder()
        .rate(List.of(uk.co.whitbread.ohip.domain.model.reservation.in.AmountType.builder()
            .base(uk.co.whitbread.ohip.domain.model.reservation.in.TotalType.builder()
                .amountBeforeTax(new BigDecimal(100))
                .currencyCode("USD")
                .build())
            .start("2023-04-23")
            .end("2023-04-24")
            .build()))
        .build();

    var roomRate = new RoomRate(rateType, "FAM", "FLEXRATE", roomOccupancy,
        "2023-04-23", "2023-04-24", false);

    var roomStay = new uk.co.whitbread.ohip.domain.model.reservation.in.RoomStay("2023-04-24",
        "2023-04-27", roomOccupancy, List.of(roomRate));

    var updateReservationReq = UpdateReservationRequest.builder()
        .hotelId("MANOLD")
        .reservationType(resType)
        .roomStay(roomStay)
        .build();

    return UpdateReservationsRequest.builder()
        .bookingChannel(bc)
        .updateReservationsRequest(List.of(updateReservationReq))
        .build();
  }

  private Mono<ChangeReservationDetails> mockUpdateReservationResponse() {
    ChangeReservationDetails details = new ChangeReservationDetails();
    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("1268956");
    reservationIdItem.setType("Reservation");
    HotelReservationsType reservations = new HotelReservationsType();
    HotelReservationType reservation = new HotelReservationType();
    RoomStayType roomStay = new RoomStayType();
    roomStay.setArrivalDate(LocalDate.parse("2022-03-01"));
    roomStay.setDepartureDate(LocalDate.parse("2022-03-03"));
    RoomRateType roomRateType = new RoomRateType();
    RatesType ratesType = new RatesType();
    AmountType amountType = new AmountType();
    ratesType.addRateItem(amountType);
    roomStay.setRoomRates(Collections.singletonList(roomRateType));
    reservation.setRoomStay(roomStay);
    reservation.setHotelId("MANOLD");
    reservation.setReservationIdList(List.of(reservationIdItem));
    reservation.setReservationStatus(PMSResStatusType.RESERVED);
    reservations.setReservation(List.of(reservation));
    details.setReservations(reservations);
    return Mono.just(details);
  }

  private ChangeReservation mockChangeReservationUpdateReservationRequest(boolean withUdfs) {
    UniqueIDType reservationIdItem = new UniqueIDType();
    reservationIdItem.setId("1268956");
    reservationIdItem.setType("Reservation");

    HotelReservationInstructionType hotelReservationInstructionType = new HotelReservationInstructionType();
    hotelReservationInstructionType.setHotelId("MANOLD");
    hotelReservationInstructionType.setReservationIdList(
        Collections.singletonList(reservationIdItem));
    hotelReservationInstructionType.setOverrideInventoryCheck(false);

    if (withUdfs) {
      UserDefinedFieldsType userDefinedFieldsType = new UserDefinedFieldsType();
      var udf08 = new CharacterUDFType();
      udf08.setName(UDFC_08);
      udf08.setValue("some override reason, for instance");
      userDefinedFieldsType.setCharacterUDFs(new ArrayList<>(List.of(udf08)));
      hotelReservationInstructionType.setUserDefinedFields(userDefinedFieldsType);
    }

    RoomRateType roomRateType = new RoomRateType();
    RoomStayType roomStay = new RoomStayType();
    roomStay.setRoomRates(List.of(roomRateType));
    hotelReservationInstructionType.setRoomStay(roomStay);

    ChangeReservation changeReservation = new ChangeReservation();
    changeReservation.setReservations(Collections.singletonList(hotelReservationInstructionType));

    return changeReservation;
  }

  private Flux<Reservation> mockReservationForGetPackages() {
    var uniqueId = new UniqueIDType();
    uniqueId.setType("Reservation");
    uniqueId.setId("100100");

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");
    hotelReservationType.setReservationIdList(Collections.singletonList(uniqueId));

    var pibbevReservationPackage = new ReservationPackageType();
    pibbevReservationPackage.setEndDate(LocalDate.of(2024, 4, 3));
    pibbevReservationPackage.setStartDate(LocalDate.of(2024, 4, 2));
    pibbevReservationPackage.setPackageCode("PIBBEV");
    pibbevReservationPackage.setPackageGroup("MDP");

    ReservationPackageScheduleType reservationPackageScheduleType = new ReservationPackageScheduleType();
    reservationPackageScheduleType.setReservationDate(pibbevReservationPackage.getStartDate());
    reservationPackageScheduleType.setUnitPrice(BigDecimal.valueOf(4.05));
    reservationPackageScheduleType.setConsumptionDate(pibbevReservationPackage.getStartDate());
    reservationPackageScheduleType.setTotalQuantity(1);

    PackageConsumptionType consumption = new PackageConsumptionType();
    consumption.setTotalQuantity(1);
    pibbevReservationPackage.setConsumptionDetails(consumption);

    pibbevReservationPackage.setPackageHeaderType(new PackageCodeHeaderType());
    pibbevReservationPackage.setScheduleList(
        Collections.singletonList(reservationPackageScheduleType));

    var bbibReservationPackage = new ReservationPackageType();
    bbibReservationPackage.setEndDate(LocalDate.of(2024, 4, 3));
    bbibReservationPackage.setStartDate(LocalDate.of(2024, 4, 2));
    bbibReservationPackage.setPackageCode("BBIB");

    bbibReservationPackage.setPackageHeaderType(new PackageCodeHeaderType());
    bbibReservationPackage.setScheduleList(
        Collections.singletonList(new ReservationPackageScheduleType()));
    bbibReservationPackage.setConsumptionDetails(consumption);

    var roomRateType = new RoomRateType();
    roomRateType.setRatePlanCode(RATE_PLAN_CODE);

    var roomStayType = new RoomStayType();
    roomStayType.setRoomRates(Collections.singletonList(roomRateType));

    hotelReservationType.setReservationPackages(
        List.of(pibbevReservationPackage, bbibReservationPackage));
    hotelReservationType.setRoomStay(roomStayType);

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    return Flux.fromIterable(Collections.singletonList(reservation));
  }

  private Flux<Reservation> mockReservationWithFolioWindowRoutingInstruction(int folioWindowNo) {
    var uniqueId = new UniqueIDType();
    uniqueId.setType("Reservation");
    uniqueId.setId("100100");

    var hotelReservationType = new HotelReservationType();
    hotelReservationType.setHotelId("HOTELTEST");
    hotelReservationType.setReservationIdList(Collections.singletonList(uniqueId));

    var pibbevReservationPackage = new ReservationPackageType();
    pibbevReservationPackage.setEndDate(LocalDate.of(2024, 4, 3));
    pibbevReservationPackage.setStartDate(LocalDate.of(2024, 4, 2));
    pibbevReservationPackage.setPackageCode("PIBBEV");

    ReservationPackageScheduleType reservationPackageScheduleType = new ReservationPackageScheduleType();
    reservationPackageScheduleType.setReservationDate(pibbevReservationPackage.getStartDate());
    reservationPackageScheduleType.setUnitPrice(BigDecimal.valueOf(4.05));
    reservationPackageScheduleType.setConsumptionDate(pibbevReservationPackage.getStartDate());
    reservationPackageScheduleType.setTotalQuantity(1);

    var routingInfoFolio = new RoutingInfoTypeFolio();
    routingInfoFolio.setFolioWindowNo(folioWindowNo);

    var routingInfo = new RoutingInfoType();
    routingInfo.setFolio(routingInfoFolio);
    routingInfo.setRefreshFolio(true);
    hotelReservationType.setRoutingInstructions(List.of(routingInfo));

    PackageConsumptionType consumption = new PackageConsumptionType();
    consumption.setTotalQuantity(1);
    pibbevReservationPackage.setConsumptionDetails(consumption);

    pibbevReservationPackage.setPackageHeaderType(new PackageCodeHeaderType());
    pibbevReservationPackage.setScheduleList(
        Collections.singletonList(reservationPackageScheduleType));

    var bbibReservationPackage = new ReservationPackageType();
    bbibReservationPackage.setEndDate(LocalDate.of(2024, 4, 3));
    bbibReservationPackage.setStartDate(LocalDate.of(2024, 4, 2));
    bbibReservationPackage.setPackageCode("BBIB");

    bbibReservationPackage.setPackageHeaderType(new PackageCodeHeaderType());
    bbibReservationPackage.setScheduleList(
        Collections.singletonList(new ReservationPackageScheduleType()));
    bbibReservationPackage.setConsumptionDetails(consumption);

    hotelReservationType.setReservationPackages(
        List.of(pibbevReservationPackage, bbibReservationPackage));

    var total = new TotalType();
    total.setCurrencyCode("GBP");

    var discount = new DiscountType();
    discount.setAmount(BigDecimal.TEN);

    var amount = new AmountType();
    amount.setBase(total);
    amount.setDiscount(discount);

    var totalType = new TotalType();
    totalType.setAmountAfterTax(BigDecimal.valueOf(100L));
    amount.setTotal(totalType);

    var rate = new RatesType();
    rate.setRate(Collections.singletonList(amount));

    var roomRate = new RoomRateType();
    roomRate.setRates(rate);
    roomRate.setSourceCodeDescription("PI.com");

    var currentRoomInfo = new CurrentRoomInfoType();
    currentRoomInfo.setRoomId("120");

    var room = new RoomStayType();
    room.setArrivalDate(LocalDate.of(2023, 7, 28));
    room.setDepartureDate(LocalDate.of(2023, 7, 30));
    room.setRoomRates(Collections.singletonList(roomRate));
    room.setCurrentRoomInfo(currentRoomInfo);

    hotelReservationType.roomStay(room);

    var currencyAmountType = new CurrencyAmountType();
    currencyAmountType.setAmount(BigDecimal.valueOf(60));

    var uniqueIdType = new UniqueIDType();
    uniqueIdType.setId("2434");
    uniqueIdType.setType("Va");
    var creditCard = new uk.co.whitbread.hotel.ohip.adapter.generated.models.ResPaymentCardType();
    creditCard.setCardId(uniqueIdType);

    var reservationPaymentMethod = new ReservationPaymentMethodType();
    reservationPaymentMethod.setPaymentMethod("CA");
    reservationPaymentMethod.setFolioView(1);
    reservationPaymentMethod.setBalance(currencyAmountType);
    reservationPaymentMethod.setPaymentCard(creditCard);

    var hotelReservationsType = new HotelReservationsType();
    hotelReservationsType.setReservation(Collections.singletonList(hotelReservationType));
    hotelReservationType.setReservationPaymentMethods(
        Collections.singletonList(reservationPaymentMethod));
    var reservation = new Reservation();
    reservation.setReservations(hotelReservationsType);

    return Flux.fromIterable(Collections.singletonList(reservation));
  }

  private ReservationById mockReservationByIdWithUserDefinedCardType() {
    return ReservationById.builder()
        .reservationId("12345")
        .hotelId("LONEUS")
        .reservationGuestList(mockReservationGuestList())
        .roomStay(mockRoomStay())
        .depositPolicies(mockDepositPolicies())
        .paymentCard(ReservationPaymentCardType.builder().cardNumberMasked("XXXXXXXXXXXX1100")
            .token("4764776852337921100").expirationDate(LocalDate.of(2026, 5, 31))
            .userDefinedCardType("BU")
            .cardType("Va").build())
        .reservationOverrideReasons(mockUserDefinedFieldsForOverrideReasons())
        .balanceAmount(BigDecimal.valueOf(60))
        .reservationStatus("Reserved")
        .preCheckInStatus(false)
        .build();
  }

  private Reservation mockReservationWithPreferences() {
    var reservation = mockReservation();
    reservation.getReservations().getReservation().get(0).getReservationIdList().get(0)
        .setId("12345");
    var firstPreference = new PreferenceType();
    firstPreference.setPreferenceValue("test1");
    var firstPreferenceType = new PreferenceTypeType();
    var secondPreferenceType = new PreferenceTypeType();
    var thirdPreferenceType = new PreferenceTypeType();
    thirdPreferenceType.setPreferenceType("testType");
    firstPreferenceType.setPreferenceType("Events");
    firstPreferenceType.setPreference(List.of(firstPreference));
    reservation.getReservations().getReservation().get(0).setPreferenceCollection(
        List.of(firstPreferenceType, secondPreferenceType, thirdPreferenceType));
    return reservation;
  }

  private ChangeReservation mockChangeReservation(String reservationId) {
    var changeReservationRequest = new ChangeReservation();
    var reservation = new HotelReservationInstructionType();
    var uniqueId = new UniqueIDType();
    uniqueId.setId(reservationId);
    uniqueId.setType("Reservation");
    reservation.setReservationIdList(List.of(uniqueId));
    changeReservationRequest.setReservations(List.of(reservation));
    return changeReservationRequest;
  }

  private Reservation mockReservationWithGuestNumber(String rsvId,Integer adults,Integer children) {
    var reservationList = mockReservation();
    reservationList.getReservations().getReservation().get(0).getReservationIdList().get(0).setId(rsvId);
    reservationList.getReservations().getReservation().get(0).getRoomStay().getGuestCounts().setAdults(adults);
    reservationList.getReservations().getReservation().get(0).getRoomStay().getGuestCounts().setChildren(children);

    return reservationList;
  }

  private Reservation mockReservationWithAlerts() {
    var reservation = mockReservation();
    reservation.getReservations().getReservation().get(0).getReservationIdList().get(0)
        .setId("12345");
    var firstAlert = new AlertType();
    firstAlert.setCode("CIOL");
    firstAlert.setArea(AlertAreaType.CHECKIN);
    firstAlert.setDescription("test alert");
    firstAlert.setId("testId");
    var secondAlert = new AlertType();
    secondAlert.setCode("CIOL");
    secondAlert.setDescription("test alert");
    secondAlert.setId("testId");
    var emptyAlertObj = new AlertType();
    var alerts = new ArrayList<AlertType>();
    alerts.add(firstAlert);
    alerts.add(secondAlert);
    alerts.add(emptyAlertObj);

    reservation.getReservations().getReservation().get(0).setAlerts(alerts);
    return reservation;
  }

  @Test
  void saveReservationPreCheckIn_FeatureFlagEnabled_ShouldSkipOperaCallAndReturnSuccess() {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();

    when(unleashWrapper.featureFlag()).thenReturn(featureFlag);
    when(unleashWrapper.isEnabled(featureFlag.getMobilePreRegisteredRepurpose())).thenReturn(true);
    when(reservationAccompanyingGuestRequestOhipMapper.toAddOrDeleteAlertModel(anyString(), anyString(), anyString(),
            isNull())).thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
            .thenReturn(Mono.just(new ChangeReservationDetails()));

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreCheckIn(request);

    // Assert
    assertNotNull(response);
    assertEquals("Success", response.getStatus());
    assertEquals("Pre-CheckIn status saved successfully", response.getMessage());

    //verify
    verify(ohipReservationClient, never()).savePreCheckInStatus(any(), anyString(), anyString());
    verify(ohipReservationClient, times(1)).sendPutReservationsGuestRequest(anyString(), anyString(), any());
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("saveReservationPreRegisterScenarios")
  void saveReservationPreRegister_shouldReturnExpectedResponse(String scenario, Status ohipStatusResponse,
                                                               String expectedStatus, String expectedMessage) {
    // Arrange
    PreCheckInRequest request = OhipTestUtils.mockPreCheckInRequest();
    when(ohipReservationClient.savePreCheckInStatus(any(PreCheckInReservation.class), eq(request.getHotelId()),
            eq(request.getReservationId()))).thenReturn(ohipStatusResponse);

    // Act
    PreCheckInResponse response = hotelReservationOutPort.saveReservationPreRegister(request);

    // Assert
    assertNotNull(response);
    assertEquals(expectedStatus, response.getStatus());
    assertEquals(expectedMessage, response.getMessage());

    verify(ohipReservationClient, times(1)).savePreCheckInStatus(any(PreCheckInReservation.class),
            eq(request.getHotelId()), eq(request.getReservationId()));
  }

  private static Stream<Arguments> saveReservationPreRegisterScenarios() {
    return Stream.of(
            Arguments.of("SUCCESS - Status contains non-empty links", statusWithLinks(),
                    "Success", "Pre-Register status saved successfully"),
                Arguments.of("ERROR - Status contains empty links list", statusWithEmptyLinks(),
                        "Error", "Error in saving Pre-Register status"),
                Arguments.of("ERROR - Status contains null links", statusWithNullLinks(),
                        "Error", "Error in saving Pre-Register status"),
                Arguments.of("ERROR - Status response is null", null,
                        "Error", "Error in saving Pre-Register status")
    );
  }

  private static Status statusWithLinks() {
    Status status = new Status();
    status.setLinks(List.of(new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.InstanceLink()));
    return status;
  }

  private static Status statusWithEmptyLinks() {
    Status status = new Status();
    status.setLinks(Collections.emptyList());
    return status;
  }

  private static Status statusWithNullLinks() {
    return new Status();
  }

  @Test
  void createReservationGuest_SameAsBooker_ExistingPassportIdPreservedOnUpdate() {
    // Arrange: booker=stayer, mapper returns profile with passport but no existing id,
    // OPERA returns a profile with the existing passport id "38107"
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var request = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    request.getStayingGuests().get(0).setSameAsBooker(true);

    when(reservationBookerRequestOhipMapper.toDto(any(), any()))
        .thenReturn(mockProfileWithPassportNoId());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class)))
        .thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet()))
        .thenReturn(Collections.singletonList(mockProfileWithPassportId("38107")));
    when(ohipReservationClient.sendUpdateProfileRequest(anyString(), any()))
        .thenReturn(new Status());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any()))
        .thenReturn(mockStatus("1234")).thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(), anyString(), anyString()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any()))
        .thenReturn(new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);

    // Act
    hotelReservationOutPort.createReservationGuest(request);

    // Assert: profile sent to OPERA has the existing identification id "38107"
    ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
    verify(ohipReservationClient, atLeastOnce()).sendUpdateProfileRequest(anyString(),
        profileCaptor.capture());
    Profile sentProfile = profileCaptor.getAllValues().get(0);
    assertNotNull(sentProfile.getProfileDetails().getCustomer().getIdentifications());
    assertEquals("38107",
        sentProfile.getProfileDetails().getCustomer().getIdentifications()
            .getIdentificationInfo().get(0).getId(),
        "Existing passport identification id must be preserved so OPERA updates not creates");
  }

  @Test
  void createReservationGuest_StayingGuest_ExistingPassportIdPreservedOnUpdate() {
    // Arrange: sameAsBooker=false, guest with real passport,
    // OPERA has existing passport record with id "38107"
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var request = buildSingleGuestRequest("A32131r", WB_LANGUAGE_CODE_EN);

    when(reservationBookerRequestOhipMapper.toDto(any(), any())).thenReturn(new Profile());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class)))
        .thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet()))
        .thenReturn(Collections.singletonList(mockProfileWithPassportId("38107")));
    when(ohipReservationClient.sendUpdateProfileRequest(anyString(), any()))
        .thenReturn(new Status());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any()))
        .thenReturn(mockStatus("1234"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(), anyString(), anyString()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any()))
        .thenReturn(new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);

    // Act
    hotelReservationOutPort.createReservationGuest(request);

    // Assert: profile sent to OPERA has the existing identification id "38107"
    ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
    verify(ohipReservationClient, atLeastOnce()).sendUpdateProfileRequest(anyString(),
        profileCaptor.capture());
    Profile sentProfile = profileCaptor.getValue();
    assertNotNull(sentProfile.getProfileDetails().getCustomer().getIdentifications());
    assertEquals("38107",
        sentProfile.getProfileDetails().getCustomer().getIdentifications()
            .getIdentificationInfo().get(0).getId(),
        "Existing passport identification id must be preserved so OPERA updates not creates");
  }

  @Test
  void createReservationGuest_StayingGuest_MaskedPassportNotSentToOpera() {
    // Arrange: sameAsBooker=false, guest with masked passport "XXXX32"
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var request = buildSingleGuestRequest("XXXX32", WB_LANGUAGE_CODE_EN);

    when(reservationBookerRequestOhipMapper.toDto(any(), any())).thenReturn(new Profile());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class)))
        .thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet()))
        .thenReturn(Collections.singletonList(mockProfile()));
    when(ohipReservationClient.sendUpdateProfileRequest(anyString(), any()))
        .thenReturn(new Status());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any()))
        .thenReturn(mockStatus("1234"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(), anyString(), anyString()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any()))
        .thenReturn(new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);

    // Act
    hotelReservationOutPort.createReservationGuest(request);

    // Assert: profile sent to OPERA has NO passport identification
    ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
    verify(ohipReservationClient, atLeastOnce()).sendUpdateProfileRequest(anyString(),
        profileCaptor.capture());
    Profile sentProfile = profileCaptor.getValue();
    assertNull(
        sentProfile.getProfileDetails().getCustomer().getIdentifications(),
        "Masked passport should not be sent to OPERA");
  }

  @Test
  void preserveExistingPassportId_NonPassportIdentificationIsNotModified() {
    // Arrange: sameAsBooker=true so bookerMapper's profile goes through preserveExistingPassportId.
    // New profile has both PASSPORT + NATIONAL_ID; existing OPERA profile has passport id "38107".
    // Only the PASSPORT entry must receive the existing id — NATIONAL_ID must remain untouched.
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var request = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    request.getStayingGuests().get(0).setSameAsBooker(true);

    when(reservationBookerRequestOhipMapper.toDto(any(), any()))
        .thenReturn(mockProfileWithPassportAndOtherIdNoId());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class)))
        .thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet()))
        .thenReturn(Collections.singletonList(mockProfileWithPassportId("38107")));
    when(ohipReservationClient.sendUpdateProfileRequest(anyString(), any()))
        .thenReturn(new Status());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any()))
        .thenReturn(mockStatus("1234")).thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(), anyString(), anyString()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any()))
        .thenReturn(new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);

    // Act
    hotelReservationOutPort.createReservationGuest(request);

    // Assert
    ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
    verify(ohipReservationClient, atLeastOnce()).sendUpdateProfileRequest(anyString(),
        profileCaptor.capture());
    Profile sentProfile = profileCaptor.getAllValues().get(0);
    var infoList = sentProfile.getProfileDetails().getCustomer().getIdentifications()
        .getIdentificationInfo();

    var passportEntry = infoList.stream()
        .filter(i -> "PASSPORT".equals(i.getIdentification().getIdType()))
        .findFirst().orElseThrow();
    var nationalIdEntry = infoList.stream()
        .filter(i -> "NATIONAL_ID".equals(i.getIdentification().getIdType()))
        .findFirst().orElseThrow();

    assertEquals("38107", passportEntry.getId(),
        "Existing passport id must be set on the PASSPORT entry");
    assertEquals("DocumentId", passportEntry.getType());
    assertNull(nationalIdEntry.getId(),
        "Non-passport identification id must not be modified");
  }

  @Test
  void getExistingPassportInfoId_NullIdentificationEntry_DoesNotThrowAndReturnsPassportId() {
    // Arrange: sameAsBooker=true; OPERA returns a profile where one IdentificationInfoType has
    // a null identification (partially populated) alongside the real passport entry.
    // The method must not throw NPE and must still return the passport id "38107".
    HashMap<String, String> languageCodes = new HashMap<>();
    languageCodes.put(WB_LANGUAGE_CODE_EN, OPERA_LANGUAGE_CODE_E);
    var request = createGuestReservationRequest(WB_LANGUAGE_CODE_EN);
    request.getStayingGuests().get(0).setSameAsBooker(true);

    when(reservationBookerRequestOhipMapper.toDto(any(), any()))
        .thenReturn(mockProfileWithPassportNoId());
    when(reservationCompanyRequestOhipMapper.toDto(any(ReservationGuestRequest.class)))
        .thenReturn(new Profile());
    when(ohipReservationClient.getReservation(anyString(), anyString()))
        .thenReturn(Mono.just(mockReservation()));
    when(ohipReservationClient.sendGetProfilesByProfileIds(anySet()))
        .thenReturn(Collections.singletonList(mockProfileWithNullIdentificationEntry("38107")));
    when(ohipReservationClient.sendUpdateProfileRequest(anyString(), any()))
        .thenReturn(new Status());
    when(ohipReservationClient.sendPostProfileRequest(anyString(), any()))
        .thenReturn(mockStatus("1234")).thenReturn(mockStatus("2345"));
    when(reservationGuestRequestOhipMapper.toDto(any(), any(), anyString(), anyString()))
        .thenReturn(new ChangeReservation());
    when(ohipReservationClient.sendPutReservationsGuestRequest(anyString(), anyString(), any()))
        .thenReturn(Mono.just(new ChangeReservationDetails()));
    when(reservationGuestResponseOhipMapper.toReservationGuestResponseModel(any()))
        .thenReturn(new ReservationGuestResponse());
    when(ohipProperties.getLanguages()).thenReturn(languageCodes);

    // Act — must not throw NullPointerException
    hotelReservationOutPort.createReservationGuest(request);

    // Assert: passport id "38107" is still found and set despite the null entry
    ArgumentCaptor<Profile> profileCaptor = ArgumentCaptor.forClass(Profile.class);
    verify(ohipReservationClient, atLeastOnce()).sendUpdateProfileRequest(anyString(),
        profileCaptor.capture());
    Profile sentProfile = profileCaptor.getAllValues().get(0);
    assertEquals("38107",
        sentProfile.getProfileDetails().getCustomer().getIdentifications()
            .getIdentificationInfo().get(0).getId(),
        "Passport id must be resolved correctly even when other entries have null identification");
  }

  private ReservationGuestRequest buildSingleGuestRequest(String passportNumber, String language) {
    final BookerAddress address = BookerAddress.builder()
        .postalCode("MZC AD").addressType("HOME").addressLine1("4 Brockley Avenue")
        .countryCode("UK").companyName("Whitbread").build();
    final BookerDetails booker = BookerDetails.builder()
        .title("Mrs").firstName("John").lastName("McEnroe")
        .emailAddress("john.mcenroe@mail.com").mobile("+39567463783")
        .address(address).acceptFutureMailing(Boolean.FALSE).language(language).build();
    final StayingGuestAddress guestAddress = StayingGuestAddress.builder()
        .postalCode("MZC AD").addressType("HOME").addressLine1("4 Brockley Avenue")
        .countryCode("UK").build();
    final StayingGuestAdditionalDetails additionalDetails = StayingGuestAdditionalDetails.builder()
        .dob(LocalDate.parse("1996-07-13")).nationality("Briton")
        .passportNumber(passportNumber).build();
    final StayingGuestDetails guestDetails = StayingGuestDetails.builder()
        .title("Mrs").firstName("Debbie").lastName("Doe")
        .address(guestAddress).additionalDetails(additionalDetails).profileId("31783").build();
    final StayingGuest stayingGuest = StayingGuest.builder()
        .reservationId("1234567").sameAsBooker(false).stayingGuestDetails(guestDetails)
        .isAccompanyingGuest(false).build();
    return ReservationGuestRequest.builder()
        .booker(booker).stayingGuests(List.of(stayingGuest))
        .hotelId("MANOLD").reasonForStay("LEI")
        .sendEmailConfirmation(Boolean.TRUE).sendEmailInvoice(Boolean.TRUE).build();
  }

  private Profile mockProfileWithPassportId(String passportInfoId) {
    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType uniqueIDType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId("1234");
    uniqueIDType.setType("Profile");

    IdentificationType identification = new IdentificationType();
    identification.setIdType("PASSPORT");
    identification.setIdNumber("A32131r");
    identification.setPrimaryInd(true);

    IdentificationInfoType identificationInfo = new IdentificationInfoType();
    identificationInfo.setIdentification(identification);
    identificationInfo.setId(passportInfoId);
    identificationInfo.setType("DocumentId");

    CustomerTypeIdentifications identifications = new CustomerTypeIdentifications();
    identifications.setIdentificationInfo(List.of(identificationInfo));

    CustomerType customer = new CustomerType();
    customer.setIdentifications(identifications);

    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType profileType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileType.setCustomer(customer);

    Profile profile = new Profile();
    profile.setProfileIdList(List.of(uniqueIDType));
    profile.setProfileDetails(profileType);
    return profile;
  }

  private Profile mockProfileWithPassportAndOtherIdNoId() {
    IdentificationType passport = new IdentificationType();
    passport.setIdType("PASSPORT");
    passport.setIdNumber("A32131r");
    passport.setPrimaryInd(true);
    IdentificationInfoType passportInfo = new IdentificationInfoType();
    passportInfo.setIdentification(passport);

    IdentificationType nationalId = new IdentificationType();
    nationalId.setIdType("NATIONAL_ID");
    nationalId.setIdNumber("NI999");
    IdentificationInfoType nationalIdInfo = new IdentificationInfoType();
    nationalIdInfo.setIdentification(nationalId);

    CustomerTypeIdentifications identifications = new CustomerTypeIdentifications();
    identifications.setIdentificationInfo(new java.util.ArrayList<>(List.of(passportInfo, nationalIdInfo)));

    CustomerType customer = new CustomerType();
    customer.setIdentifications(identifications);

    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType profileType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileType.setCustomer(customer);

    Profile profile = new Profile();
    profile.setProfileDetails(profileType);
    return profile;
  }

  private Profile mockProfileWithPassportNoId() {
    IdentificationType identification = new IdentificationType();
    identification.setIdType("PASSPORT");
    identification.setIdNumber("A32131r");
    identification.setPrimaryInd(true);

    IdentificationInfoType identificationInfo = new IdentificationInfoType();
    identificationInfo.setIdentification(identification);

    CustomerTypeIdentifications identifications = new CustomerTypeIdentifications();
    identifications.setIdentificationInfo(List.of(identificationInfo));

    CustomerType customer = new CustomerType();
    customer.setIdentifications(identifications);

    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType profileType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileType.setCustomer(customer);

    Profile profile = new Profile();
    profile.setProfileDetails(profileType);
    return profile;
  }

  private Profile mockProfileWithNullIdentificationEntry(String passportInfoId) {
    IdentificationType identification = new IdentificationType();
    identification.setIdType("PASSPORT");
    identification.setIdNumber("A32131r");
    IdentificationInfoType passportInfo = new IdentificationInfoType();
    passportInfo.setIdentification(identification);
    passportInfo.setId(passportInfoId);
    passportInfo.setType("DocumentId");

    // entry with null identification — simulates partially populated OPERA profile
    IdentificationInfoType nullIdentificationInfo = new IdentificationInfoType();

    CustomerTypeIdentifications identifications = new CustomerTypeIdentifications();
    identifications.setIdentificationInfo(
        new java.util.ArrayList<>(List.of(nullIdentificationInfo, passportInfo)));

    CustomerType customer = new CustomerType();
    customer.setIdentifications(identifications);

    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType profileType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.ProfileType();
    profileType.setCustomer(customer);

    uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType uniqueIDType =
        new uk.co.whitbread.hotel.ohip.adapter.generated.models.crm.UniqueIDType();
    uniqueIDType.setId("1234");
    uniqueIDType.setType("Profile");

    Profile profile = new Profile();
    profile.setProfileIdList(List.of(uniqueIDType));
    profile.setProfileDetails(profileType);
    return profile;
  }

  private DepositFolioCriteria mockDepositFolioCriteriaWithPaymentMethod(String paymentMethod) {
    var pm = new ReservationPaymentMethodType();
    pm.setPaymentMethod(paymentMethod);

    var criteriaType = new DepositFolioCriteriaType();
    criteriaType.setPaymentMethod(pm);

    var criteria = new DepositFolioCriteria();
    criteria.setCriteria(criteriaType);
    return criteria;
  }

  private FoliosResponseDto mockFoliosResponseWithPaymentMethod(String paymentMethod) {
    var pm = new ReservationPaymentMethodType();
    pm.setPaymentMethod(paymentMethod);

    var window = FolioWindowsDto.builder().paymentMethod(pm).build();
    var folioInfo = ReservationFolioInformationDto.builder()
        .folioWindowType(List.of(window))
        .build();
    return FoliosResponseDto.builder().reservationFolioInformation(folioInfo).build();
  }
}
