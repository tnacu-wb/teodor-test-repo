import { Flex, FlexProps, Text, TextProps } from '@chakra-ui/react';
import { BUSINESS_BOOKER_USER_ROLES, GDP_ACCOMPANYING_GUEST_DETAILS } from '@whitbread-eos/api';
import {
  formatDataTestId,
  useCookieForABTesting,
  useSemanticTypography,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import BBGuestDetailsGeneralRoom from '../BBDynamicGuestDetailsForm/BBGuestDetailsGeneralRoom';
import BBGuestDetailsRoom from '../BBGuestDetailsForm/BBGuestDetailsRoom';

export default function BBAllGuestsDetailsForm({
  control,
  formField,
  errors,
  reset,
  handleResetField,
}: any) {
  const { t } = useTranslation();
  const getTypographyProps = useSemanticTypography();
  const {
    numberOfRooms = 1,
    labels,
    queryClient,
    guestList,
    reservationByIdList,
    setGuestUser,
    isDynamicSearchVisible,
    isAmendPage,
    bbEmployeeList,
    defaultGuest,
    onEditBbInput,
    isAccompanyingGuestDetailsEnabled,
    accessLevel,
    selfBookerDetails,
    baseDataTestIdAccompayningGuestDetails,
    userDetails,
  } = formField.props;
  const { testid, dropdownOptions, name } = formField;

  // A/B testing for accompanying guests
  const hasAccompanyingGuestCookie = useCookieForABTesting(
    GDP_ACCOMPANYING_GUEST_DETAILS.cookieName,
    GDP_ACCOMPANYING_GUEST_DETAILS.mode
  );

  function renderContent() {
    return [...Array(numberOfRooms)].map((value: any, index: number) => {
      const isFirstRoom = index === 0;
      const hasOneRoom = numberOfRooms === 1;
      const firstRoomOfMultipleRooms = isFirstRoom && numberOfRooms > 1;
      const hasMoreThanOneAdult = reservationByIdList[index]?.roomStay?.adultsNumber > 1;
      const accompanyingGuestDetailsTitleStyle = hasOneRoom
        ? accompanyingGuestDetailsBaseTitleStyle
        : smallAccompanyingGDTitleStyle(firstRoomOfMultipleRooms);
      const guestTitleNeedsMargin =
        hasOneRoom &&
        hasMoreThanOneAdult &&
        accessLevel !== BUSINESS_BOOKER_USER_ROLES.SELF &&
        isAccompanyingGuestDetailsEnabled &&
        hasAccompanyingGuestCookie;

      return (
        <Flex {...guestDetailsSectionStyle}>
          <Flex direction={'column'} mr={{ xl: '2xl' }}>
            {isFirstRoom && (
              <Text
                {...guestDetailsTitleLayoutStyles}
                {...getTypographyProps(
                  guestDetailsTitleLegacyTypography,
                  guestDetailsTitleSemanticTypography
                )}
                mb={{
                  xl: guestTitleNeedsMargin ? '2xl' : '',
                }}
                data-testid={formatDataTestId('GuestDetailsBBContainer', 'GuestDetailsTitle')}
              >
                {t('booking.contactDetails.guestDetails')}
              </Text>
            )}
            {accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF &&
            reservationByIdList[index]?.roomStay?.adultsNumber > 1 &&
            isAccompanyingGuestDetailsEnabled &&
            hasAccompanyingGuestCookie ? (
              <Text
                data-testid={formatDataTestId('GuestDetailsBBContainer', 'SelfBookerDetails')}
                {...accompanyingGuestDetailsDescriptionLayoutStyles}
                {...getTypographyProps(
                  accompanyingGuestDetailsDescriptionLegacyTypography,
                  accompanyingGuestDetailsDescriptionSemanticTypography
                )}
              >
                {selfBookerDetails}
              </Text>
            ) : (
              <BBGuestDetailsRoom
                t={t}
                roomNumber={index}
                numberOfRooms={numberOfRooms}
                labels={labels}
                testid={testid}
                control={control}
                errors={errors}
                dropdownOptions={dropdownOptions}
                formField={formField}
                queryClient={queryClient}
                guestList={guestList}
                setGuestUser={setGuestUser}
                index={index}
                key={testid}
                reset={reset}
                isDynamicSearchVisible={isDynamicSearchVisible}
                isAmendPage={isAmendPage}
                bbEmployeeList={bbEmployeeList}
                defaultGuest={defaultGuest}
                onEditBbInput={onEditBbInput}
                componentName={name}
                reservationByIdList={reservationByIdList}
                handleResetField={handleResetField}
                userDetails={userDetails}
              />
            )}
          </Flex>

          {isAccompanyingGuestDetailsEnabled &&
            hasAccompanyingGuestCookie &&
            reservationByIdList[index]?.roomStay?.adultsNumber > 1 && (
              <Flex direction={'column'}>
                <Text
                  {...accompanyingGuestDetailsTitleStyle}
                  {...getTypographyProps(
                    guestDetailsTitleLegacyTypography,
                    guestDetailsTitleSemanticTypography
                  )}
                  data-testid={formatDataTestId(
                    'AccompanyingGuestDetailsBBContainer',
                    'AccompanyingGDTitle'
                  )}
                >
                  {t('booking.contactDetails.accompanyingGuest.title')}
                </Text>
                <Text
                  {...accompanyingGuestDetailsDescriptionLayoutStyles}
                  {...getTypographyProps(
                    accompanyingGuestDetailsDescriptionLegacyTypography,
                    accompanyingGuestDetailsDescriptionSemanticTypography
                  )}
                  data-testid={formatDataTestId(
                    'AccompanyingGuestDetailsBBContainer',
                    'AccompanyingGDDescription'
                  )}
                >
                  {t('booking.accompanyingGuest.description')}
                </Text>

                {/*TODO: Use this component for guest details section after the accompanying guest details is fully tested */}
                <BBGuestDetailsGeneralRoom
                  t={t}
                  roomNumber={index}
                  labels={labels}
                  testid={formatDataTestId(baseDataTestIdAccompayningGuestDetails, 'Form')}
                  componentName={'bbAccompanyingGuestDetails'}
                  control={control}
                  errors={errors}
                  dropdownOptions={dropdownOptions}
                  formField={formField}
                  queryClient={queryClient}
                  guestList={guestList}
                  setGuestUser={setGuestUser}
                  index={index}
                  key={formatDataTestId(baseDataTestIdAccompayningGuestDetails, 'Form')}
                  handleResetField={handleResetField}
                  isDynamicSearchVisible={isDynamicSearchVisible}
                  isAmendPage={isAmendPage}
                  bbEmployeeList={bbEmployeeList}
                  defaultGuest={defaultGuest}
                  onEditBbInput={onEditBbInput}
                  userDetails={userDetails}
                />
              </Flex>
            )}
        </Flex>
      );
    });
  }

  return (
    <Flex
      direction="column"
      justifyContent={'space-between'}
      {...{ ...formField.styles }}
      data-testid={formatDataTestId(testid, 'Container')}
    >
      {renderContent()}
    </Flex>
  );
}

const guestDetailsTitleLayoutStyles = {
  as: 'h3',
  color: 'darkGrey1',
  mt: {
    mobile: 'xl',
  },
} as TextProps;

const guestDetailsTitleLegacyTypography = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '3',
} as TextProps;

const guestDetailsTitleSemanticTypography = {
  textStyle: 'heading-s',
} as TextProps;

const accompanyingGuestDetailsBaseTitleStyle = {
  as: 'h3',
  color: 'darkGrey1',
  mt: {
    mobile: 'xl',
  },
} as TextProps;

const smallAccompanyingGDTitleStyle = (firstRoomOfMultipleRooms: boolean) => {
  return {
    color: 'darkGrey1',
    mt: { xl: firstRoomOfMultipleRooms ? '8.25rem' : '4.75rem', lg: 'lg', mobile: 'lg' },
  } as TextProps;
};

const accompanyingGuestDetailsDescriptionLayoutStyles = {
  textAlign: 'left',
  color: 'darkGrey1',
  marginTop: 'md',
} as TextProps;

const accompanyingGuestDetailsDescriptionLegacyTypography = {
  fontSize: 'md',
  fontWeight: 'normal',
  lineHeight: '3',
};

const accompanyingGuestDetailsDescriptionSemanticTypography = {
  textStyle: 'body-m-regular',
};

const guestDetailsSectionStyle = {
  flexDirection: { xl: 'row', lg: 'column', mobile: 'column' },
  justifyContent: 'space-between',
} as FlexProps;
