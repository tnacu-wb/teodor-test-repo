import {
  Box,
  Flex,
  BoxProps,
  FlexProps,
  TextProps,
  StyleProps,
  Text,
  Link,
  RadioGroup,
} from '@chakra-ui/react';
import { Area, FS_SILENT_SUBSTITUTION, SilentSubstitutionLocalStorage } from '@whitbread-eos/api';
import {
  Checkbox,
  Input,
  FORM_FIELD_TYPES,
  Info,
  Notification,
  Button,
  Icon,
  Path,
  Rectangle,
  RadioButton,
} from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  useFeatureSwitch,
  useSemanticTypography,
  getCurrentReservationStorageData,
  displayStorageSubstitutionLabels,
  GLOBALS,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useRouter } from 'next/router';
import { ReactElement, useEffect, useState } from 'react';
import { Controller, useWatch } from 'react-hook-form';

import LeadGuestFields from '../AdditionalInformation/LeadGuestAdditionalFields';
import { additionalDetailsFieldsBooker } from '../AdditionalInformation/common';
import CountriesDropdown from '../Countries/CountriesDropdown';
import GuestInputs from '../GuestInputs';
import PostcodeAddress from '../PostcodeAddress/PostcodeAddress.component';

type LeadGuestDetails = {
  title: string;
  firstName: string;
  lastName: string;
};

interface LeadGuestAddressToggle {
  [key: string]: boolean;
}

export default function FormLeadGuest({
  control,
  formField,
  errors,
  getValues,
  handleSetValue,
  handleResetField,
}: any) {
  const errorStyles =
    formField.props?.useTooltip && !!errors?.[formField.name]?.message ? tooltipErrorStyles : {};
  const {
    showIcon,
    isLocationRequired,
    setIsLocationRequired,
    currentLang,
    isBookingForSomeoneElse,
    isGermanHotel,
    isMultiRoomRedesignEnabled,
    isSingleRoomRedesignEnabled,
    isAdditionalInformationEnabled,
    showCheckInInfo,
    setShowCheckInInfo,
    shouldAskForAccompanyingGuest,
  } = formField.props;
  const [leadGuest, setLeadGuest] = useState(false);
  const [manualToggle, setManualToggle] = useState<LeadGuestAddressToggle>({});
  const leadGuestValues = useWatch({ name: 'leadGuest', control });
  const bookingForSomeoneElseCheckbox = useWatch({ name: 'bookingForSomeoneElse', control });
  const personalDetailsFirstname = useWatch({ name: 'firstName', control });
  const personalDetailsLastname = useWatch({ name: 'lastName', control });
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const roomNumber = (number: any) => {
    return number + 1;
  };

  const bookingForSomeoneElseCheckboxLabelTypographyStyles = getTypographyProps(
    {},
    bookingForSomeoneElseCheckboxLabelSemanticTypography
  );

  const consentOptions = [
    {
      name: t('precheckin.button.yes'),
      value: 'true',
      dataTestID: `${formatDataTestId(formField.testid, 'consent')}_consentYes`,
      type: `${formatDataTestId(formField.testid, 'consent')}_consentYes`,
    },
    {
      name: t('precheckin.button.no'),
      value: 'false',
      dataTestID: `${formatDataTestId(formField.testid, 'consent')}_consentYes`,
      type: `${formatDataTestId(formField.testid, 'consent')}_consentNo`,
    },
  ];
  const [tempLeadGuests, setTempLeadGuests] = useState<{ [key: string]: LeadGuestDetails }>({});
  const [guestAddress, setGuestAddress] = useState([
    {
      postalCode: '',
      country: '',
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      companyName: '',
    },
  ]);

  const updateManualToggle = (key: number, value: boolean) => {
    setManualToggle({ ...manualToggle, [key]: value });
  };

  const formatText = (adultsNumber: number, childrenNumber: number) => {
    const childStr =
      childrenNumber > 1 ? t('account.dashboard.children') : t('account.dashboard.child');
    const adultStr =
      adultsNumber > 1 ? t('account.dashboard.adults') : t('account.dashboard.adult');

    return (
      <>
        {adultsNumber >= 1 && ` ${adultsNumber} ${adultStr}`}
        {childrenNumber >= 1 &&
          ` ${childrenNumber}
          ${childStr}`}
      </>
    );
  };

  const updateGuestRoomDetails = (
    key: string | number,
    title: string,
    firstName: string,
    lastName: string
  ) => {
    handleSetValue(`leadGuest[${key}].firstName.`, firstName);
    handleResetField(`leadGuest[${key}].firstName`, { defaultValue: firstName });
    handleSetValue(`leadGuest[${key}].lastName.`, lastName);
    handleResetField(`leadGuest[${key}].lastName`, { defaultValue: lastName });
    handleSetValue(`leadGuest[${key}].title.`, title);
    handleResetField(`leadGuest[${key}].title`, { defaultValue: title });
  };

  const setTemporaryGuestDetailsValues = (roomCount: number, formData: any) => {
    if (!roomCount) {
      return;
    }

    let updatedState = {};

    for (let key = 0; key < roomCount; key++) {
      const tempLeadGuest: LeadGuestDetails = {
        title: formData?.leadGuest[key]?.title || '',
        firstName: formData?.leadGuest[key]?.firstName || '',
        lastName: formData?.leadGuest[key]?.lastName || '',
      };

      const isSameAsYourDetails =
        tempLeadGuest.title === formData?.title &&
        tempLeadGuest.firstName === formData?.firstName &&
        tempLeadGuest.lastName === formData?.lastName;

      if (isSameAsYourDetails) {
        continue;
      }

      const leadGuestKey = `key${key}`;
      updatedState = { ...updatedState, [leadGuestKey]: tempLeadGuest };
    }

    setTempLeadGuests({ ...tempLeadGuests, ...updatedState });
  };

  const isSingleRoomBooking = formField.bkndData.rooms.length === 1;

  const updateOtherLeadGuestDetails = (key: number) => {
    formField.bkndData.rooms.map((value2: any, roomIndex: number) => {
      if (key !== roomIndex) {
        handleSetValue(`leadGuest[${roomIndex}].stayInThisRoom.`, false);
        handleResetField(`leadGuest[${roomIndex}].stayInThisRoom`, {
          defaultValue: false,
        });

        const tempLeadGuestKey = `key${roomIndex}`;
        if (tempLeadGuests[tempLeadGuestKey]) {
          const tempLeadGuest = tempLeadGuests[tempLeadGuestKey];

          updateGuestRoomDetails(
            roomIndex,
            tempLeadGuest?.title || '',
            tempLeadGuest?.firstName || '',
            tempLeadGuest?.lastName || ''
          );
        }
      }
    });
  };

  const handleStayInThisRoom = (key: number) => {
    const formData = getValues();
    setTemporaryGuestDetailsValues(formField.bkndData.rooms.length, formData);
    updateOtherLeadGuestDetails(key);
    const leadGuestKey = `key${key}`;

    if (formData?.leadGuest[key]['stayInThisRoom'] === false) {
      if (tempLeadGuests[leadGuestKey]) {
        const tempLeadGuest = tempLeadGuests[leadGuestKey];

        updateGuestRoomDetails(
          key,
          tempLeadGuest?.title || '',
          tempLeadGuest?.firstName || '',
          tempLeadGuest?.lastName || ''
        );
      }
    } else {
      updateGuestRoomDetails(key, formData?.title, formData?.firstName, formData?.lastName);
    }
  };

  useEffect(() => {
    const formData = getValues();
    if (formData?.leadGuest) {
      for (let i = 0; i < formData.leadGuest.length; i++) {
        if (formData.leadGuest[i].stayInThisRoom === true) {
          updateGuestRoomDetails(i, formData.title, formData.firstName, formData.lastName);
        }
      }
    }
  }, [personalDetailsFirstname, personalDetailsLastname]);

  useEffect(() => {
    guestAddress?.forEach((address, index) => {
      handleSetValue(`leadGuest[${index}].addressLine1`, address?.addressLine1);
      handleSetValue(`leadGuest[${index}].addressLine2`, address?.addressLine2);
      handleSetValue(`leadGuest[${index}].addressLine3`, address?.addressLine3);
      handleSetValue(`leadGuest[${index}].addressLine4`, address?.addressLine4);
      handleSetValue(`leadGuest[${index}].postcodeAddress`, address?.postalCode);
      handleSetValue(`leadGuest[${index}].companyName`, address?.companyName);
      handleSetValue(`leadGuest[${index}].country`, address?.country);
    });
  }, [guestAddress]);

  const isSilentFeatureFlagEnabled = useFeatureSwitch({
    featureSwitchKey: FS_SILENT_SUBSTITUTION,
  });

  const router = useRouter();
  const reservationId = (router?.query?.reservationId as string) ?? '';

  const currentReservationRoomData = isSilentFeatureFlagEnabled
    ? getCurrentReservationStorageData(reservationId)
    : ({} as SilentSubstitutionLocalStorage);

  const isSingleBookingRedesign = isSingleRoomRedesignEnabled && isSingleRoomBooking;

  const isSingleBookingRedesignForSomeoneElse = isSingleBookingRedesign && isBookingForSomeoneElse;

  const inputStyleFields = isSingleBookingRedesign ? singleBookingStyleFields : inputStyle;

  // De hotel only for address change
  const inputStyleAddressFields =
    isSingleBookingRedesignForSomeoneElse && isGermanHotel ? singleBookingStyleFields : inputStyle;

  const boxParentStyleSwitch =
    isMultiRoomRedesignEnabled && !isSingleRoomBooking ? boxParentStyle : boxParentStyleDefault;

  const messageStyleSwitch = isMultiRoomRedesignEnabled || isSingleRoomBooking ? '' : messageStyles;

  const boxesContainerStyleSwitch =
    isMultiRoomRedesignEnabled && !isSingleRoomBooking
      ? boxesContainerStyle
      : boxesContainerStyleDefault;

  const singleBookingDDStyles = isSingleBookingRedesign
    ? singleBookingDropdownStyles
    : dropdownStyle;

  const singleBookingStyles =
    isSingleRoomBooking && isSingleRoomRedesignEnabled ? borderBottom : '';

  const guestDetailStyles = isSingleBookingRedesign ? singleBookingStyleFields : guestDetailStyle;

  const paddingHeader =
    (isSingleRoomBooking && !isSingleRoomRedesignEnabled) ||
    (!isSingleRoomBooking && !isMultiRoomRedesignEnabled);

  const additionalFields = (key: number) => {
    const leadGuest = getValues()?.leadGuest?.[key];
    const nationality = leadGuest?.nationality;
    const consent = leadGuest?.consent;

    return additionalDetailsFieldsBooker.map(
      ({ label, testId, type, name }, index) =>
        (name !== 'passport' || (nationality && nationality?.value !== 'DE')) && (
          <LeadGuestFields
            type={type}
            key={index}
            name={`leadGuest[${key}].${name}`}
            styles={inputStyleFields}
            testId={testId}
            label={label}
            isDisabled={!leadGuest?.stayInThisRoom && !consent}
            control={control}
            formField={formField}
            getValues={getValues}
            errMsg={errors?.leadGuest?.[key]?.[name]?.message}
            handleSetValue={handleSetValue}
            fieldToClear={`leadGuest[${key}].passport`}
          />
        )
    );
  };

  const isFieldDisabled = (key: number) => {
    const leadGuest = getValues()?.leadGuest?.[key];
    return !leadGuest?.consent;
  };

  const toggleCheckInInfo = (id: string) => () => {
    if (!Object.prototype.hasOwnProperty.call(showCheckInInfo, id)) {
      setShowCheckInInfo({ ...showCheckInInfo, [id]: true });
    } else {
      setShowCheckInInfo({ ...showCheckInInfo, [id]: !showCheckInInfo?.[id] });
    }
  };

  // Booking for Someone else checkbox
  return (
    <Flex
      direction="column"
      {...{ ...formField.styles, ...errorStyles }}
      data-testid={formatDataTestId(formField.testid, 'Container')}
    >
      {/* Only show if SingleBookingRedsign Feature switch is off AND single booking  */}
      {!isSingleRoomRedesignEnabled && formField.bkndData.rooms.length === 1 && (
        <Controller
          name="bookingForSomeoneElse"
          control={control}
          render={({ field: { onChange } }) => {
            return (
              <Box data-testid={formatDataTestId(formField.testid, 'Checkbox')}>
                <Checkbox
                  {...formField.props}
                  onChange={() => {
                    onChange(!leadGuest);
                    setLeadGuest(!leadGuest);
                  }}
                  isChecked={bookingForSomeoneElseCheckbox}
                >
                  <Text
                    {...bookingForSomeoneElseCheckboxLabelTypographyStyles}
                    data-testid={formatDataTestId(formField.testid, 'CheckboxText')}
                  >
                    {formField.props?.area === Area.CCUI
                      ? t('ccui.booking.leadGuest.iAmBookingForSomeoneElse')
                      : t('booking.leadGuest.iAmBookingForSomeoneElse')}
                  </Text>
                </Checkbox>
              </Box>
            );
          }}
        />
      )}
      {/* Render Booking for Someone else checkbox with single instance of Lead Guest, if SingleBookingRedsign Feature switch is off AND single booking */}
      {!isSingleRoomRedesignEnabled &&
        bookingForSomeoneElseCheckbox &&
        formField.bkndData.rooms.length === 1 &&
        renderContent()}
      {/* Render Lead Guest blocks (Single or Multi) if SingleBookingRedsign Feature switch is ON, OR SingleBookingRedsign Feature switch is OFF and multiple rooms */}
      {((isSingleRoomRedesignEnabled && isBookingForSomeoneElse) ||
        formField.bkndData.rooms.length > 1) &&
        renderContent()}
      {((!isBookingForSomeoneElse && isSingleRoomRedesignEnabled) ||
        (!bookingForSomeoneElseCheckbox && !isSingleRoomRedesignEnabled)) &&
        formField.bkndData.rooms.length === 1 &&
        shouldAskForAccompanyingGuest &&
        formField.bkndData.rooms?.[0]?.roomStay?.adultsNumber === 2 &&
        renderSingleRoomAccompanyingGuest()}
    </Flex>
  );

  function renderSingleRoomAccompanyingGuest() {
    return <Box {...singleBookingStyles}>{renderAccompanyingGuest(0)}</Box>;
  }

  // Lead Guest content
  function renderContent() {
    const formData = getValues();
    return (
      <Box {...singleBookingStyles}>
        <Text
          {...leadGuestHeaderLayoutStyles}
          {...getTypographyProps(
            leadGuestHeaderLegacyTypography,
            leadGuestHeaderSemanticTypography
          )}
          {...(paddingHeader && { ...paddingTopBottom })}
          data-testid={formatDataTestId(formField.testid, 'Header')}
        >
          {formField.props?.area === Area.CCUI &&
            !isSingleBookingRedesign &&
            t('ccui.booking.leadGuest.labelWhoIsTheLead')}

          {formField.props?.area === Area.PI &&
            !isSingleBookingRedesign &&
            t('booking.leadGuest.labelWhoIsTheLead')}
        </Text>

        {formField.bkndData.rooms.map((value: any, key: number) => {
          return (
            <Box {...boxParentStyleSwitch} key={key}>
              {formField.bkndData.rooms.length >= 2 && (
                <Flex
                  {...boxesContainerStyleSwitch}
                  data-testid={formatDataTestId(
                    formField.testid,
                    `ContainerRoom-${roomNumber(key)}`
                  )}
                  mb={
                    leadGuestValues?.[key]?.stayInThisRoom ||
                    (shouldAskForAccompanyingGuest &&
                      !isSingleRoomBooking &&
                      value?.roomStay?.adultsNumber === 2)
                      ? 0
                      : 'lg'
                  }
                >
                  <Box {...boxesStyles}>
                    <Text
                      {...headerRoomLayoutStyles}
                      {...getTypographyProps(
                        headerRoomLegacyTypography,
                        headerRoomSemanticTypography
                      )}
                      data-testid={formatDataTestId(
                        formField.testid,
                        `HeaderRoom-${roomNumber(key)}`
                      )}
                    >
                      {t('booking.leadGuest.ForRoom').replace('[roomNumber]', roomNumber(key))}
                    </Text>
                    <Text
                      {...detailsRoomLayoutStyles}
                      {...getTypographyProps(
                        detailsRoomLegacyTypography,
                        detailsRoomSemanticTypography
                      )}
                      data-testid={formatDataTestId(formField.testid, `DetailsRoom`)}
                    >
                      {displayStorageSubstitutionLabels(
                        currentReservationRoomData?.value?.[key],
                        value?.roomStay?.roomExtraInfo?.roomName,
                        isSilentFeatureFlagEnabled
                      )}
                      {formatText(value?.roomStay?.adultsNumber, value?.roomStay?.childrenNumber)}
                    </Text>
                  </Box>
                  <Box {...boxesStyles}>
                    <Controller
                      name={`leadGuest[${key}][stayInThisRoom]`}
                      control={control}
                      render={({ field: { onChange } }) => renderCheckboxComponent(key, onChange)}
                    />
                  </Box>
                </Flex>
              )}

              {/* Lead Guest consent, title, fname, lname, email */}
              {leadGuestValues?.[key]?.stayInThisRoom !== true ? (
                <Box {...messageStyleSwitch}>
                  {isSingleBookingRedesign &&
                    isBookingForSomeoneElse &&
                    isSingleRoomBooking &&
                    !isAdditionalInformationEnabled && (
                      <Box {...inputStyleFields}>
                        <Text {...getTypographyProps({}, whoIsStayingNoticeSemanticTypography)}>
                          {t('booking.guestDetails.whoIsStayingInThisRoom')}
                        </Text>
                      </Box>
                    )}
                  {!isMultiRoomRedesignEnabled && !isSingleRoomRedesignEnabled && (
                    <Notification
                      status="info"
                      svg={<Info />}
                      variant="infoGrey"
                      maxW="full"
                      description={t('booking.leadGuest.message')}
                      data-testid={formatDataTestId(formField.testid, 'Notification')}
                    />
                  )}

                  {isAdditionalInformationEnabled &&
                    !isMultiRoomRedesignEnabled &&
                    renderConsentFieldComponent(key)}

                  {!isSingleBookingRedesign &&
                    shouldAskForAccompanyingGuest &&
                    !isSingleRoomBooking &&
                    value?.roomStay?.adultsNumber === 2 && (
                      <Box>
                        <Text {...titleStyle}>{t('booking.leadGuest.Details')}</Text>
                      </Box>
                    )}

                  <GuestInputs
                    control={control}
                    formField={formField}
                    errors={errors?.leadGuest?.[key]}
                    index={key}
                    showIcon={showIcon}
                    inputStyle={inputStyleFields}
                    dropdownStyle={singleBookingDDStyles}
                  />

                  {shouldAskForAccompanyingGuest &&
                    value?.roomStay?.adultsNumber === 2 &&
                    renderAccompanyingGuest(key)}

                  {isGermanHotel &&
                    isMultiRoomRedesignEnabled &&
                    isAdditionalInformationEnabled &&
                    renderCheckInInformation(value)}

                  {isSingleBookingRedesign && isBookingForSomeoneElse && !isGermanHotel && (
                    <Box {...singleBookingStyleBorders}></Box>
                  )}
                  {/* Note: style switches depending on feature switch enabled. */}
                  {isMultiRoomRedesignEnabled &&
                    isGermanHotel &&
                    showCheckInInfo?.[value?.reservationId] && (
                      <>
                        {isAdditionalInformationEnabled && (
                          <>
                            <Box
                              {...guestDetailStyles}
                              data-testid={formatDataTestId(
                                formField.testid,
                                'guestDetailsSomeoneElseHeadingOptional'
                              )}
                            ></Box>
                            {renderConsentFieldComponent(key)}

                            {renderEmailComponent(key)}

                            {isSingleBookingRedesign && <Box {...singleBookingStyleBorders}></Box>}
                          </>
                        )}

                        {currentLang !== 'de' && (
                          <Box {...inputStyleAddressFields}>
                            <Controller
                              name={`leadGuest[${key}].postcodeAddress`}
                              control={control}
                              render={({ field }) => {
                                return (
                                  <PostcodeAddress
                                    formField={{
                                      ...formField,
                                      label: t('booking.addressPostCode'),
                                      type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
                                      name: 'postcodeAddress',
                                      isDisabled: isFieldDisabled(key),
                                      testid: formatDataTestId(
                                        `leadGuest[${key}]`,
                                        'PostcodeAddress'
                                      ),
                                    }}
                                    field={field}
                                    getValues={getValues()}
                                    index={key}
                                    setGuestAddress={setGuestAddress}
                                    updateManualToggle={updateManualToggle}
                                  />
                                );
                              }}
                            />
                          </Box>
                        )}
                        {currentLang !== 'de' && (
                          <Box {...inputStyleAddressFields}>
                            {!(manualToggle[key] || leadGuestValues?.[key]?.addressLine1) && (
                              <Link {...linkStyles} onClick={() => updateManualToggle(key, true)}>
                                {t('booking.enterManuallAddress')}
                              </Link>
                            )}
                          </Box>
                        )}

                        {isGermanHotel &&
                          (manualToggle[key] ||
                            leadGuestValues?.[key]?.addressLine1 ||
                            (showCheckInInfo[value.reservationId] &&
                              currentLang === GLOBALS.language.DE)) && (
                            <>
                              <Box {...inputStyleAddressFields}>
                                <Controller
                                  name={`leadGuest[${key}].addressLine1`}
                                  control={control}
                                  render={({ field }) => {
                                    return (
                                      <Input
                                        {...formField.props}
                                        {...field}
                                        type="text"
                                        placeholderText={t('booking.guest.addressAddress1')}
                                        label={t('booking.guest.addressAddress1')}
                                        isDisabled={isFieldDisabled(key)}
                                        className="sessioncamhidetext assist-no-show"
                                        dataTestId={formatDataTestId(
                                          formField.testid,
                                          'addressAddress1'
                                        )}
                                      />
                                    );
                                  }}
                                />
                              </Box>
                              <Box {...inputStyleAddressFields}>
                                <Controller
                                  name={`leadGuest[${key}].addressLine2`}
                                  control={control}
                                  render={({ field }) => {
                                    return (
                                      <Input
                                        {...formField.props}
                                        {...field}
                                        type="text"
                                        placeholderText={t('booking.guest.addressAddress2')}
                                        label={t('booking.guest.addressAddress2')}
                                        isDisabled={isFieldDisabled(key)}
                                        className="sessioncamhidetext assist-no-show"
                                        dataTestId={formatDataTestId(
                                          formField.testid,
                                          'addressAddress2'
                                        )}
                                      />
                                    );
                                  }}
                                />
                              </Box>
                              <Box {...inputStyleAddressFields}>
                                <Controller
                                  name={`leadGuest[${key}].addressLine3`}
                                  control={control}
                                  render={({ field }) => {
                                    return (
                                      <Input
                                        {...formField.props}
                                        {...field}
                                        type="text"
                                        placeholderText={t('booking.guest.addressAddress3')}
                                        label={t('booking.guest.addressAddress3')}
                                        isDisabled={isFieldDisabled(key)}
                                        className="sessioncamhidetext assist-no-show"
                                        dataTestId={formatDataTestId(
                                          formField.testid,
                                          'addressAddress3'
                                        )}
                                      />
                                    );
                                  }}
                                />
                              </Box>
                              {!isLocationRequired && (
                                <Box {...inputStyleAddressFields}>
                                  <Controller
                                    name={`leadGuest[${key}].addressLine4`}
                                    control={control}
                                    render={({ field }) => {
                                      return (
                                        <Input
                                          {...formField.props}
                                          {...field}
                                          type="text"
                                          placeholderText={t('booking.guest.addressAddress4')}
                                          label={t('booking.guest.addressAddress4')}
                                          isDisabled={isFieldDisabled(key)}
                                          className="sessioncamhidetext assist-no-show"
                                          dataTestId={formatDataTestId(
                                            formField.testid,
                                            'addressAddress4'
                                          )}
                                        />
                                      );
                                    }}
                                  />
                                </Box>
                              )}
                              <Box {...inputStyleAddressFields}>
                                <Controller
                                  name={`leadGuest[${key}].postcodeAddress`}
                                  control={control}
                                  render={({ field }) => {
                                    return (
                                      <Input
                                        {...formField.props}
                                        {...field}
                                        id={'homePostalCodes'}
                                        type="text"
                                        placeholderText={t('booking.guest.addressPostCode')}
                                        label={t('booking.guest.addressPostCode')}
                                        isDisabled={isFieldDisabled(key)}
                                        className="sessioncamhidetext assist-no-show"
                                        dataTestId={formatDataTestId(
                                          formField.testid,
                                          'addressPostCode'
                                        )}
                                      />
                                    );
                                  }}
                                />
                              </Box>
                              {isLocationRequired && (
                                <Box {...inputStyleAddressFields}>
                                  <Controller
                                    name={`leadGuest[${key}].addressLine4`}
                                    control={control}
                                    render={({ field }) => {
                                      return (
                                        <Input
                                          id={'homeAddressCityName'}
                                          {...formField.props}
                                          {...field}
                                          type="text"
                                          placeholderText={t('booking.guest.addressCity')}
                                          label={t('booking.guest.addressCity')}
                                          isDisabled={isFieldDisabled(key)}
                                          className="sessioncamhidetext assist-no-show"
                                          dataTestId={formatDataTestId(
                                            formField.testid,
                                            'cityName'
                                          )}
                                        />
                                      );
                                    }}
                                  />
                                </Box>
                              )}
                              <Box {...inputStyleAddressFields}>
                                <Controller
                                  name={`leadGuest[${key}][countryCode]`}
                                  control={control}
                                  defaultValue={currentLang === 'en' ? 'GB' : 'DE'}
                                  render={({ field }) => {
                                    return (
                                      <CountriesDropdown
                                        formField={{
                                          ...formField,
                                          label: t('booking.country'),
                                          type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
                                          name: 'countryCode',
                                          isDisabled: isFieldDisabled(key),
                                          testid: formatDataTestId(
                                            `leadGuest[${key}]`,
                                            'CountrySelector'
                                          ),
                                          props: {
                                            ...formField.props,
                                            showIcon: false,
                                            setIsLocationRequired,
                                          },
                                        }}
                                        getValues={getValues()}
                                        field={field}
                                      />
                                    );
                                  }}
                                />
                              </Box>
                            </>
                          )}
                        {additionalFields(key)}
                      </>
                    )}
                  {isSingleBookingRedesignForSomeoneElse &&
                    isGermanHotel &&
                    !isAdditionalInformationEnabled && <Box {...singleBookingStyleBorders}></Box>}

                  {isAdditionalInformationEnabled && !isMultiRoomRedesignEnabled && (
                    <>
                      <Box
                        {...guestDetailStyles}
                        data-testid={formatDataTestId(
                          formField.testid,
                          'guestDetailsSomeoneElseHeadingOptional'
                        )}
                      >
                        <Text {...heading3}>{t('precheckin.guestdetails')}</Text>
                        <Text>{t('precheckin.completedetails.statement')}</Text>
                      </Box>

                      {additionalFields(key)}
                      {isSingleBookingRedesign && <Box {...singleBookingStyleBorders}></Box>}
                    </>
                  )}
                </Box>
              ) : (
                shouldAskForAccompanyingGuest &&
                value?.roomStay?.adultsNumber === 2 && (
                  <Box {...messageStyleSwitch}>
                    <Box>
                      <Text {...titleStyle}>{t('booking.leadGuest.Details')}</Text>
                    </Box>
                    <Box>
                      <Text {...subtitleStyle}>
                        {formData.title} {formData.firstName} {formData.lastName}
                      </Text>
                    </Box>
                    {renderAccompanyingGuest(key)}
                  </Box>
                )
              )}

              {isGermanHotel &&
                isMultiRoomRedesignEnabled &&
                isAdditionalInformationEnabled &&
                leadGuestValues?.[key]?.stayInThisRoom && (
                  <>
                    {renderCheckInInformation(value)}
                    {showCheckInInfo?.[value?.reservationId] && additionalFields(key)}
                  </>
                )}
            </Box>
          );
        })}
      </Box>
    );
  }

  function renderAccompanyingGuest(key: number) {
    return (
      <Box data-testid={formatDataTestId(formField.testid, 'AccompanyingContainer')}>
        <Box {...inputStyleFields}>
          <Text {...titleStyle}>{t('booking.contactDetails.accompanyingGuest.title')}</Text>
        </Box>
        <Box {...inputStyleFields}>
          <Text {...subtitleStyle}>
            {t('booking.contactDetails.accompanyingGuest.description')}
          </Text>
        </Box>
        <GuestInputs
          control={control}
          formField={formField}
          errors={errors?.leadGuest?.[key]}
          index={key}
          showIcon={showIcon}
          inputStyle={inputStyleFields}
          dropdownStyle={singleBookingDDStyles}
          prefix={'accompanying'}
          fieldsRequired={false}
        />
      </Box>
    );
  }

  function renderCheckInInformation(value: any) {
    return (
      <>
        <Box
          {...inputStyleFields}
          maxW="100%"
          pt="xl"
          data-testid={formatDataTestId(formField.testid, 'guestDetailsMyselfHeadingOptional')}
        >
          <Text {...heading3}>{t('precheckin.guestdetails')}</Text>
          <Text mt="md">{t('precheckin.completedetails.statement')}</Text>
        </Box>

        <Box {...inputStyleFields}>
          <Button
            variant="tertiary"
            size="full"
            onClick={toggleCheckInInfo(value.reservationId)}
            {...checkinButtonStyles}
            data-testId={formatDataTestId(formField.testid, 'CheckinButton')}
          >
            {showCheckInInfo?.[value?.reservationId] ? (
              <>
                <Icon
                  svg={<Rectangle color={'var(--chakra-colors-darkGrey2)'} />}
                  {...iconStyles}
                />
                {` ${t('booking.guestDetails.hideCheckIn')}`}
              </>
            ) : (
              <>
                <Icon svg={<Path color={'var(--chakra-colors-darkGrey2)'} />} {...iconStyles} />
                {` ${t('booking.guestDetails.addCheckIn')}`}
              </>
            )}
          </Button>
        </Box>
      </>
    );
  }

  function renderCheckboxComponent(key: number, onChange: (...event: any[]) => void): ReactElement {
    return (
      <Box
        {...rightboxStyle}
        data-testid={formatDataTestId(formField.testid, `RoomCheckbox-${roomNumber(key)}`)}
      >
        <Checkbox
          onChange={(option: any) => {
            onChange(option);
            handleStayInThisRoom(key);
          }}
          isChecked={leadGuestValues?.[key]?.stayInThisRoom}
        >
          <Text
            {...roomCheckboxLabelLayoutStyles}
            {...getTypographyProps(
              roomCheckboxLabelLegacyTypography,
              roomCheckboxLabelSemanticTypography
            )}
            data-testid={formatDataTestId(formField.testid, `RoomCheckboxText-${roomNumber(key)}`)}
          >
            {t('booking.leadGuest.ImStayingInThisRoom')}
          </Text>
        </Checkbox>
      </Box>
    );
  }

  function renderConsentFieldComponent(key: number): ReactElement {
    return (
      <>
        <Box {...guestDetailStyles} pt={!isSingleRoomRedesignEnabled ? 'lg' : ''}>
          <Text {...textStyle}>{t('precheckin.consentstatement')}</Text>
        </Box>
        <Box {...inputStyleFields} data-testid={formatDataTestId(formField.testid, 'Consent')}>
          <Controller
            name={`leadGuest[${key}][consent]`}
            control={control}
            render={({ field: { onChange, value } }) =>
              renderConsentRadioGroupComponent(value, onChange)
            }
          />
        </Box>
        {errors?.leadGuest?.[key]?.consent?.message && (
          <Box {...guestDetailStyles} pb="sm">
            <Text
              {...errorTextStyles}
              fontSize="sm"
              mt={0}
              dataTestId={formatDataTestId(formField.testid, 'ConsentErrorText')}
            >
              {errors?.leadGuest?.[key]?.consent?.message}
            </Text>
          </Box>
        )}
      </>
    );
  }

  function renderEmailComponent(key: number): ReactElement {
    return (
      <Box {...inputStyleFields}>
        <Controller
          name={`leadGuest[${key}][email]`}
          control={control}
          render={({ field }) => (
            <Input
              {...formField.props}
              {...field}
              isDisabled={isFieldDisabled(key)}
              type="email"
              placeholderText={t('precheckin.additionalfields.email')}
              label={t('precheckin.additionalfields.email')}
              error={errors?.leadGuest?.[key]?.email?.message}
              className="sessioncamhidetext assist-no-show"
              dataTestId={formatDataTestId(formField.testid, 'email')}
            />
          )}
        />
      </Box>
    );
  }

  function renderConsentRadioGroupComponent(
    value: any,
    onChange: (...event: any[]) => void
  ): ReactElement {
    return (
      <RadioGroup
        value={value !== undefined ? String(!!value) : ''}
        onChange={(val) => onChange(Boolean(val === 'true'))}
        data-testid={`${formatDataTestId(formField.testid, 'Consent')}_radio-group`}
        {...consentRadioStyle}
      >
        {consentOptions.map((option) => (
          <RadioButton
            key={option.type}
            value={option.value}
            data-testid={option.dataTestID}
            type={option.type}
            variant="borderless"
            width="full"
          >
            <Text {...consentRadioStyle} data-testid={`${option.dataTestID}_text`}>
              {option.name}
            </Text>
          </RadioButton>
        ))}
      </RadioGroup>
    );
  }
}

const singleBookingStyleFields = {
  paddingLeft: '5',
  paddingRight: '5',
  paddingBottom: 'var(--chakra-space-md)',
  borderLeft: '1px solid var(--chakra-colors-lightGrey2)',
  borderRight: '1px solid var(--chakra-colors-lightGrey2)',
  maxW: {
    md: '100%',
    xl: '55rem',
  },
  sx: {
    '.chakra-input__group, .chakra-button, .chakra-radio-group, .chakra-menu__menu-button,  .chakra-alert':
      {
        maxW: {
          mobile: '100%',
          sm: '16.375rem',
          md: '21.75rem',
          lg: '24.5rem',
          xl: '26.25rem',
        },
      },
  },
};

const singleBookingStyleBorders = {
  maxW: {
    md: '100%',
    xl: '55rem',
  },
};

const singleBookingDropdownStyles = {
  maxW: '8.5rem !important',
};

const leadGuestHeaderLayoutStyles = {
  color: 'darkGrey1',
};

const leadGuestHeaderLegacyTypography = {
  fontSize: 'xl',
  fontWeight: '600',
  lineHeight: '3',
} as TextProps;

const leadGuestHeaderSemanticTypography = {
  textStyle: 'heading-m',
} as TextProps;

const heading3 = {
  fontSize: 'lg',
  fontWeight: '700',
  lineHeight: '3',
  color: 'darkGrey1',
};

const textStyle = {
  color: 'var(--chakra-colors-darkGrey1)',
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '2',
} as TextProps;

const headerRoomLayoutStyles = {
  color: 'var(--chakra-colors-darkGrey1)',
};

const headerRoomLegacyTypography = {
  fontSize: 'lg',
  fontWeight: '700',
  lineHeight: '3',
} as TextProps;

const headerRoomSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as TextProps;

const detailsRoomLayoutStyles = {
  color: 'var(--chakra-colors-darkGrey1)',
};

const detailsRoomLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '2',
} as TextProps;

const detailsRoomSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const roomCheckboxLabelLayoutStyles = {
  color: 'var(--chakra-colors-darkGrey1)',
  pos: { mobile: 'relative', md: 'absolute' },
  right: { mobile: '0px', md: '45px' },
  top: { md: '-2px' },
  whiteSpace: { mobile: 'initial', md: 'nowrap' },
} as TextProps;

const roomCheckboxLabelLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'bold',
  lineHeight: '3',
} as TextProps;

const roomCheckboxLabelSemanticTypography = {
  textStyle: 'body-m-emphasis',
} as TextProps;

const boxParentStyle = {
  border: '1px solid var(--chakra-colors-lightGrey3)',
  p: { mobile: 'md', md: 'lg md lg md' },
  mb: '4xl',
  mt: 'md',
  _last: {
    mb: '0',
  },
};

const boxParentStyleDefault = {
  mb: 'xl',
  _last: {
    mb: '0',
  },
};

const boxesContainerStyle = {
  p: { mobile: 'md 0', md: 'lg 0' },
  mb: '0',
  direction: { mobile: 'column', md: 'row' },
} as FlexProps;

const boxesContainerStyleDefault = {
  border: '1px solid var(--chakra-colors-lightGrey3)',
  padding: 'var(--chakra-space-md)',
} as FlexProps;

const boxesStyles = {
  w: { mobile: '100%', md: '50%' },
} as BoxProps;

const rightboxStyle = {
  ...heading3,
  textAlign: { mobile: 'left', md: 'right' },
  fontStyle: 'md',
} as BoxProps;

const messageStyles = {
  paddingTop: '2',
};

const dropdownStyle = {
  maxW: '8.5rem',
  mt: 'sm',
  mb: '0',
} as StyleProps;

const inputStyle = {
  maxW: {
    mobile: '100%',
    sm: '16.375rem',
    md: '21.75rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  marginBottom: 'var(--chakra-space-md)',
};

const tooltipErrorStyles = {
  marginBottom: 'var(--chakra-space-6xl)',
} as StyleProps;

export const formErrorStyles = {
  height: 'var(--chakra-space-lg)',
  position: 'relative',
} as BoxProps;
export const errorMessageStyles = {
  position: 'absolute',
} as BoxProps;
export const errorTextStyles = {
  color: 'error',
  fontSize: 'xs',
  marginLeft: 'md',
  marginTop: 'sm',
  whiteSpace: 'nowrap',
} as TextProps;

const linkStyles = {
  color: 'btnSecondaryEnabled',
  textDecoration: 'underline',
};

const consentRadioStyle = {
  alignItems: 'center',
  justifyContent: 'space-between',
  width: 'full',
  display: 'flex',
  fontSize: 'md',
};

const guestDetailStyle = {
  ...singleBookingStyleFields,
  border: 'none',
  pl: 0,
  pt: 0,
} as BoxProps;

const iconStyles = {
  mr: '0.75rem',
  display: {
    base: 'none',
    xs: 'block',
  },
};

const checkinButtonStyles = {
  mt: '1rem',
};

const borderBottom = {
  borderBottom: '1px solid var(--chakra-colors-lightGrey2)',
};

const paddingTopBottom = {
  mt: 'md',
  mb: 'md',
};

const titleStyle = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '4',
  color: 'darkGrey1',
  pt: 'lg',
  pb: 'var(--chakra-space-3)',
};

const subtitleStyle = {
  fontSize: 'md',
  maxW: {
    mobile: '100%',
    sm: '16.375rem',
    md: '21.75rem',
    lg: '24.5rem',
    xl: '26.25rem',
  },
  marginBottom: 'var(--chakra-space-md)',
};

const bookingForSomeoneElseCheckboxLabelSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;

const whoIsStayingNoticeSemanticTypography = {
  textStyle: 'body-m-regular',
} as TextProps;
