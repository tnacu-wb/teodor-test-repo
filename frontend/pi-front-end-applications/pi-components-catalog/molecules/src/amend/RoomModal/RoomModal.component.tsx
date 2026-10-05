import { Box, Button, Divider, Flex, Heading, Text } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import {
  AmendHotelAvailabilityData,
  AmendLeadGuestLabels,
  AmendLeadGuestValidationLabels,
  AmendResetNotificationLabels,
  AmendRoomAvailabilityLabels,
  AmendRoomDetailsType,
  Area,
  BBLeadGuestDetailsType,
  Channel,
  Customer,
  GuestDetails,
  IsHotelAvailableType,
  KeyValuePair,
  PageName,
  ReservationLeadGuestType,
  SearchRoomOccupancyLimitationsType,
  Suggestion,
} from '@whitbread-eos/api';
import {
  Alert,
  Dropdown,
  DropdownOption,
  Error,
  FormProps,
  ModalVariants,
  Notification,
  Success,
  PromotionsNotification,
} from '@whitbread-eos/atoms';
import {
  createOptionsAdultsChildrenDropdown,
  getAdultsChildrenOptions,
  getGQLClient,
  PromotionsInformation,
} from '@whitbread-eos/utils';
import { Dispatch, SetStateAction, useCallback, useEffect, useMemo, useState } from 'react';

import { CHILDREN_NUMBER_KEY, ROOM_TYPE_KEY } from '../../search/RoomPicker/constants';
import { BBLeadGuestDetails } from '../BBLeadGuestDetails';
import { LeadGuestDetails } from '../LeadGuestDetails';
import { getPromotionsInformation } from '../utilities';
import {
  ADULTS_NUMBER_KEY,
  ADULTS_PARAM,
  CHILDREN_PARAM,
  CITYTAX_HOTEL_COUNTRY_EN,
  CITYTAX_HOTEL_COUNTRY_DE,
} from './constants';

interface Props {
  isOpen: boolean;
  isHotelAvailable: IsHotelAvailableType;
  isAvailabilityButtonEnabled: boolean;
  onCloseModal: () => void;
  onChange: (chosenOption: DropdownOption | undefined, param: string) => void;
  onAddNewRoom: (guestDetails: FormProps['defaultValues']) => void;
  onAvailabilityCheck: () => Promise<void>;
  onUpdateRoom: (guestDetails: FormProps['defaultValues']) => void;
  getFormState: FormProps['getFormState'];
  title: string;
  roomRules: SearchRoomOccupancyLimitationsType;
  roomDetails: AmendRoomDetailsType;
  roomTypeOptions: DropdownOption[];
  labels: {
    roomAvailabilityLabels: AmendRoomAvailabilityLabels;
    leadGuestLabels: AmendLeadGuestLabels;
    leadGuestValidationLabels: AmendLeadGuestValidationLabels;
    notificationLabels: AmendResetNotificationLabels;
  };
  baseDataTestId: string;
  roomOccupancyDetails: {
    displayRoomOccupancy: boolean;
    price: string;
    guests: string;
  };
  leadGuestDetails: FormProps['defaultValues'];
  isEdit: boolean;
  roomNumber?: number;
  setGuestDetails?: (value: ReservationLeadGuestType) => void;
  isLocationRequired: boolean;
  setIsLocationRequired: Dispatch<SetStateAction<boolean>>;
  isUpdateBtnDisabled?: boolean;
  variant: Area;
  bbEmployeeList?: Suggestion[];
  isAdultsDecreased: boolean;
  hotelCountry: string;
  language: string;
  isBbGuestEdited: boolean;
  setIsBbGuestEdited: (value: boolean) => void;
  userDetails?: Customer;
  brand: string;
  channel: Channel;
  hotelAvailabilityParams: AmendHotelAvailabilityData;
  isPromoCodeLandingPageEnabled: boolean;
  isPromotionsInHotelAvailabilityEnabled: boolean;
  promoData: PromotionsInformation | null;
  setPromoData: Dispatch<SetStateAction<PromotionsInformation | null>>;
}

export default function RoomModalComponent({
  isOpen,
  onCloseModal,
  title,
  roomRules,
  onAvailabilityCheck,
  onChange,
  onAddNewRoom,
  onUpdateRoom,
  getFormState,
  isAvailabilityButtonEnabled,
  isHotelAvailable,
  roomDetails,
  labels,
  baseDataTestId,
  roomTypeOptions,
  roomOccupancyDetails,
  isEdit,
  roomNumber,
  leadGuestDetails,
  setGuestDetails,
  setIsLocationRequired,
  isUpdateBtnDisabled,
  variant,
  bbEmployeeList,
  isAdultsDecreased,
  hotelCountry,
  language,
  isBbGuestEdited,
  setIsBbGuestEdited,
  userDetails,
  brand,
  channel,
  hotelAvailabilityParams,
  isPromoCodeLandingPageEnabled,
  isPromotionsInHotelAvailabilityEnabled,
  promoData,
  setPromoData,
}: Readonly<Props>) {
  const { roomAvailabilityLabels, leadGuestLabels, leadGuestValidationLabels, notificationLabels } =
    labels;
  const { displayNotification, available } = isHotelAvailable;
  const { children, adults, roomType } = roomDetails;
  const { displayRoomOccupancy, price, guests } = roomOccupancyDetails;
  const [bbEditLeadGuestDetails, setBbEditLeadGuestDetails] = useState<GuestDetails>();
  const isBb = variant === Area.BB;

  const queryClient = useQueryClient();
  const client = getGQLClient();
  const adultsPlaceholder = `${adults} ${
    adults > 1 ? roomAvailabilityLabels.adults : roomAvailabilityLabels.adult
  }`;

  const fetchPromotionsInformation = async () => {
    try {
      const res = await getPromotionsInformation(
        hotelAvailabilityParams?.arrival,
        hotelAvailabilityParams?.departure,
        hotelAvailabilityParams?.country,
        language,
        brand,
        channel,
        hotelAvailabilityParams?.originalBasketReference ?? '',
        queryClient,
        client,
        isPromoCodeLandingPageEnabled
      );
      setPromoData(res);
    } catch (e) {
      console.log('e');
    }
  };

  const onSubmit = (guestDetails: FormProps['defaultValues']) => {
    isEdit ? onUpdateRoom(guestDetails) : onAddNewRoom(guestDetails);
    onCloseModal();
  };
  const prefixDataTestId = 'add-room-modal';

  const initialBbLeadGuestDetailsData = {
    bbGuestDetails: [
      { title: '', firstName: '', lastName: '', emailAddress: '', id: '', composedName: '' },
    ],
  };

  const selectedGuest = useMemo((): GuestDetails => {
    let selectedGuest = bbEmployeeList?.find(
      (item) =>
        item.employee.firstName === leadGuestDetails.firstName &&
        item.employee.lastName === leadGuestDetails.lastName &&
        item.employee.title === leadGuestDetails.title &&
        item.employee.emailAddress === leadGuestDetails.email
    )?.employee;

    if (!selectedGuest) {
      selectedGuest = {
        firstName: leadGuestDetails.firstName || '',
        lastName: leadGuestDetails.lastName || '',
        title: leadGuestDetails.title || '',
        emailAddress: leadGuestDetails.emailAddress || '',
      } as GuestDetails;
    }
    return selectedGuest;
  }, [bbEmployeeList, leadGuestDetails]);
  useEffect(() => {
    if (isEdit && variant === Area.BB) {
      setBbEditLeadGuestDetails(selectedGuest);
      setBbLeadGuestDetails({ bbGuestDetails: [selectedGuest] });
    }
  }, [isEdit, leadGuestDetails, bbEmployeeList, variant, selectedGuest]);

  const [bbLeadGuestDetails, setBbLeadGuestDetails] = useState<FormProps['defaultValues']>(
    initialBbLeadGuestDetailsData
  );

  function setGuestUser(user: Suggestion, index: number) {
    const tempList: any = bbLeadGuestDetails;

    if (tempList?.bbGuestDetails[index]) {
      tempList.bbGuestDetails[index] = user;
      setBbLeadGuestDetails(tempList);
    }
  }

  const onBbSubmit = (guestDetails: BBLeadGuestDetailsType) => {
    isEdit
      ? onUpdateRoom(guestDetails?.bbGuestDetails[0])
      : onAddNewRoom(guestDetails?.bbGuestDetails[0]);
    onCloseModal();
    setBbLeadGuestDetails(initialBbLeadGuestDetailsData);
  };

  const isUpdateDisabled = useCallback(() => {
    if (variant === Area.PI || variant === Area.CCUI) {
      return isUpdateBtnDisabled;
    }
    if (isEdit) {
      return !(isBbGuestEdited || isHotelAvailable.available);
    }

    return !isHotelAvailable.available;
  }, [variant, isUpdateBtnDisabled, isHotelAvailable, isBbGuestEdited, isEdit]);

  function onEditBbInput(data: KeyValuePair | GuestDetails) {
    if ('key' in data) {
      const { key, value } = data;

      setBbEditLeadGuestDetails((prevValue: SetStateAction<GuestDetails | undefined>) => {
        return { ...prevValue, [key]: value } as GuestDetails;
      });
    } else {
      setBbEditLeadGuestDetails(data);
    }
    setIsBbGuestEdited(true);
  }

  const resetMealsAfterEditRoomLabel = notificationLabels.description.replace(
    '[number]',
    `${roomNumber}`
  );

  return (
    <ModalVariants
      isOpen={isOpen}
      onClose={onCloseModal}
      variant="default"
      contentContainerStyles={{ overflow: 'auto' }}
      variantProps={{ title: '', delimiter: true }}
      headerStyles={{ textAlign: 'center', justifyContent: 'center' }}
      updatedWidth={{ md: 'auto', lg: '50.5rem', xl: '61rem' }}
    >
      <Heading
        as="h2"
        data-testid={`${prefixDataTestId}-title`}
        {...headingStyles}
        fontSize="lg"
        textAlign="center"
        mt="lg"
      >
        {title}
      </Heading>
      <Flex
        justifyContent="space-between"
        gap={{ base: 'md', sm: 'lg' }}
        direction={{ base: 'column', sm: 'row' }}
        {...modalContentStyles}
      >
        <Flex direction="column" {...columnWrapperStyles}>
          <Heading as="h3" data-testid={`${prefixDataTestId}-guests-title`} {...headingStyles}>
            {roomAvailabilityLabels.guests}
          </Heading>
          <Flex direction="row" mb="md" gap={{ base: 'md', md: 'sm' }}>
            <Dropdown
              dataTestId={`${prefixDataTestId}-adults-number`}
              onChange={(chosenOption: DropdownOption | undefined) => {
                onChange(chosenOption, ADULTS_PARAM);
              }}
              options={createOptionsAdultsChildrenDropdown(
                getAdultsChildrenOptions(roomRules, ADULTS_NUMBER_KEY),
                roomAvailabilityLabels.adult,
                roomAvailabilityLabels.adults
              )}
              placeholder={adultsPlaceholder}
              matchWidth
              dropdownStyles={{ ...dropdownStyles }}
            />
            <Dropdown
              dataTestId={`${prefixDataTestId}-children-number`}
              onChange={(chosenOption: DropdownOption | undefined) =>
                onChange(chosenOption, CHILDREN_PARAM)
              }
              options={createOptionsAdultsChildrenDropdown(
                getAdultsChildrenOptions(roomRules, CHILDREN_NUMBER_KEY),
                roomAvailabilityLabels.child,
                roomAvailabilityLabels.children
              )}
              placeholder={`${children} ${roomAvailabilityLabels.children}`}
              matchWidth
              dropdownStyles={{ ...dropdownStyles }}
            />
          </Flex>
          <Dropdown
            dataTestId={`${prefixDataTestId}-room-type`}
            onChange={(chosenOption: DropdownOption | undefined) =>
              onChange(chosenOption, ROOM_TYPE_KEY)
            }
            options={roomTypeOptions}
            placeholder={roomDetails.roomType}
            selectedId={roomDetails.roomType}
            matchWidth
            dropdownStyles={{ ...dropdownStyles }}
          />
          <Button
            size="full"
            variant="primary"
            data-testid={`${prefixDataTestId}-availability-button`}
            mt="md"
            mb="xmd"
            isDisabled={!isAvailabilityButtonEnabled}
            onClick={async () => {
              if (!isPromotionsInHotelAvailabilityEnabled) {
                await fetchPromotionsInformation();
              }
              await onAvailabilityCheck();
            }}
          >
            {roomAvailabilityLabels.checkRoomAvailability}
          </Button>

          {displayNotification && (
            <>
              {available ? (
                <Notification
                  status="success"
                  variant="success"
                  svg={<Success />}
                  description={roomAvailabilityLabels.roomAvailable}
                  data-testid={`${prefixDataTestId}-room-available-success`}
                />
              ) : (
                !promoData?.showPromo && (
                  <Notification
                    title={roomAvailabilityLabels.roomsUnavailable}
                    status="error"
                    variant="error"
                    svg={<Error />}
                    description={roomAvailabilityLabels.roomsUnavailableDescription}
                    data-testid={`${prefixDataTestId}-room-unavailable-error`}
                  />
                )
              )}
            </>
          )}
          {!promoData?.showPromo && isEdit && isAdultsDecreased && available && (
            <Box mt="xmd">
              <Notification
                status="warning"
                variant="alert"
                svg={<Alert />}
                title={notificationLabels.title}
                description={resetMealsAfterEditRoomLabel}
                data-testid={`${prefixDataTestId}-room-warning-notification`}
              />
            </Box>
          )}
          {promoData && (
            <Box>
              <PromotionsNotification
                page={PageName.AMEND}
                promotionBannerData={promoData}
                elementName={PageName.ROOMS_AND_GUESTS}
              />
            </Box>
          )}
        </Flex>
        <Flex direction="column" {...columnWrapperStyles}>
          <Heading
            as="h3"
            data-testid={`${prefixDataTestId}-lead-guest-title`}
            {...{ ...leadGuestHeadingStyles, mb: isBb ? '0' : 'md' }}
          >
            {roomAvailabilityLabels.leadGuest}
          </Heading>
          {isBb ? (
            <BBLeadGuestDetails
              onSubmit={onBbSubmit}
              numberOfRooms={1}
              labels={leadGuestLabels}
              validationLabels={leadGuestValidationLabels}
              isDynamicSearchVisible={true}
              guestList={bbLeadGuestDetails}
              setGuestUser={setGuestUser}
              getFormState={getFormState}
              queryClient={queryClient}
              isEdit={isEdit}
              bbEmployeeList={bbEmployeeList}
              defaultGuest={bbEditLeadGuestDetails}
              onEditBbInput={onEditBbInput}
              userDetails={userDetails}
            />
          ) : (
            <LeadGuestDetails
              labels={leadGuestLabels}
              validationLabels={labels.leadGuestValidationLabels}
              baseDataTestId={baseDataTestId}
              getFormState={getFormState}
              leadGuestDetails={leadGuestDetails}
              onSubmit={onSubmit}
              isEdit={isEdit}
              setGuestDetails={setGuestDetails}
              setIsLocationRequired={setIsLocationRequired}
              hotelCountry={hotelCountry}
              language={language}
            />
          )}
          <Divider {...dividerStyles} />
          {displayRoomOccupancy && (
            <>
              <Flex direction="row" justifyContent="space-between" alignItems="center">
                <Text
                  color="darkGrey2"
                  data-testid={`${prefixDataTestId}-number-of-guests-room-type`}
                >
                  {guests} <b>{roomType}</b>
                </Text>
                <Text {...priceStyle} data-testid={`${prefixDataTestId}-price`}>
                  {price}
                </Text>
              </Flex>
              {(hotelCountry === CITYTAX_HOTEL_COUNTRY_EN ||
                hotelCountry === CITYTAX_HOTEL_COUNTRY_DE) && (
                <Flex justifyContent="flex-end">
                  <Text
                    textAlign="right"
                    as="span"
                    {...cityTaxNotIncludedStyle}
                    data-testid={`${prefixDataTestId}-cityTaxNotIncluded`}
                  >
                    {roomAvailabilityLabels.cityTaxNotIncluded}
                  </Text>
                </Flex>
              )}

              <Divider {...dividerStyles} />
            </>
          )}
          <Button
            size="full"
            variant="secondary"
            data-testid="add-edit-room-button"
            form="leadGuestDetailsForm"
            type="submit"
            isDisabled={isUpdateDisabled()}
          >
            {isEdit ? roomAvailabilityLabels.update : roomAvailabilityLabels.addRoom}
          </Button>
          <Button
            size="full"
            variant="tertiary"
            data-testid="add-room-close-button"
            mt="md"
            onClick={() => {
              setBbEditLeadGuestDetails(selectedGuest);
              setBbLeadGuestDetails({ bbGuestDetails: [selectedGuest] });
              onCloseModal();
            }}
          >
            {roomAvailabilityLabels.cancelBtn}
          </Button>
        </Flex>
      </Flex>
    </ModalVariants>
  );
}

const modalContentStyles = {
  p: { base: 'md', md: 'lg', xl: '2xl' },
  pb: { lg: 'xl', xl: 'xl' },
  mt: 'xl',
  w: { base: 'full', md: '48rem', lg: '50.5rem', xl: '61rem' },
};

const columnWrapperStyles = {
  w: { base: 'full', sm: '48rem', md: 'full' },
  maxW: { md: '48rem', xl: '27.25rem' },
};

const headingStyles = {
  fontSize: 'md',
  fontWeight: 'semibold',
  lineHeight: '3',
  mb: 'md',
  color: 'darkGrey1',
};

const dropdownStyles = {
  menuButtonStyles: {
    borderColor: 'lightGrey1',
  },
};

const leadGuestHeadingStyles = {
  mt: {
    mobile: 'var(--chakra-space-lg)',
    sm: 0,
  },
  ...headingStyles,
};

const dividerStyles = {
  my: 'xl',
  color: 'lightGrey1',
  border: '1px solid',
};

const priceStyle = {
  pl: '1rem',
  color: 'darkGrey1',
  fontSize: 'xl',
};

const cityTaxNotIncludedStyle = {
  w: '32%',
  color: 'darkGrey1',
  fontSize: 'sm',
};
