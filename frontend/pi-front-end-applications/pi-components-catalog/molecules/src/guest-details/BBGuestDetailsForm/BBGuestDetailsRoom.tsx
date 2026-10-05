import { Box, BoxProps, Flex, Text, TextProps } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import {
  BIReservationListItem,
  BUSINESS_BOOKER_USER_ROLES,
  FT_BB_ACCOMPANYING_GUEST_DETAILS,
  GuestDetails,
  KeyValuePair,
  Suggestion,
  Customer,
} from '@whitbread-eos/api';
import { Dropdown, FieldsType, Input } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  isTheSameGuest,
  useFeatureToggle,
  useSemanticTypography,
  useUserData,
  useUserDetails,
} from '@whitbread-eos/utils';
import { useEffect, useRef, useState } from 'react';
import { Control, Controller } from 'react-hook-form';

import DynamicSearchEmployee from './DynamicSearchEmployees';

interface BBGuestDetailsRoomProps {
  roomNumber: number;
  numberOfRooms: number;
  labels: {
    firstName: string;
    lastName: string;
    email: string;
    title: string;
  };
  testid: string;
  t: (x: string, y?: { [key: string]: string }) => string;
  control: Control;
  errors: any;
  dropdownOptions: [];
  formField: FieldsType;
  queryClient: QueryClient;
  guestList: {
    bbGuestDetails: [];
  };
  setGuestUser: any;
  index: number;
  reset: (...rest: any) => void;
  handleResetField?: (fieldName: string, options?: Record<string, boolean | any>) => void;
  isDynamicSearchVisible: boolean;
  isAmendPage?: boolean;
  bbEmployeeList?: Suggestion[];
  defaultGuest?: GuestDetails;
  onEditBbInput?: (data: KeyValuePair | GuestDetails) => void;
  componentName?: string;
  reservationByIdList?: BIReservationListItem[];
  userDetails?: Customer;
}

export default function BBGuestDetailsRoom({
  roomNumber,
  numberOfRooms,
  labels,
  testid,
  t,
  control,
  errors,
  dropdownOptions,
  formField,
  queryClient,
  guestList,
  setGuestUser,
  index,
  reset,
  handleResetField,
  isDynamicSearchVisible,
  isAmendPage,
  bbEmployeeList,
  defaultGuest,
  onEditBbInput,
  componentName,
  reservationByIdList,
  userDetails,
}: Readonly<BBGuestDetailsRoomProps>) {
  const { [FT_BB_ACCOMPANYING_GUEST_DETAILS]: isAccompanyingGuestDetailsEnabled } =
    useFeatureToggle();

  const getTypographyProps = useSemanticTypography();
  const inputLabelStyles = {
    ...formInputStyle,
    inputElementStyles: {
      ...formInputStyle.inputElementStyles,
      ...getTypographyProps(inputTextLegacyTypography, inputTextSemanticTypography),
    },
    labelElementStyles: getTypographyProps(labelLegacyTypography, labelSemanticTypography),
  };
  const dropdownMenuButtonTextStyles = getTypographyProps(
    dropdownLegacyTypography,
    dropdownSemanticTypography
  );

  const [displayDynamic, setDisplayDynamic] = useState(isDynamicSearchVisible);

  const prevObject = useRef({});
  const isDefaultGuestSelected = isAmendPage && !!defaultGuest;
  const defaultGuestValue = isDefaultGuestSelected
    ? defaultGuest
    : {
        emailAddress: '',
        firstName: '',
        id: '',
        lastName: '',
        title: '',
        composedName: '',
      };

  const getComposedGuest = () => {
    if (control._formValues?.bbGuestDetails[index]?.composedName) {
      const composedName =
        control._formValues?.bbGuestDetails?.[index].composedName.split(' ') || [];

      prevObject.current = {
        title: composedName[0],
        firstName: composedName[1],
        lastName: composedName[2],
        emailAddress: composedName[3].replace(/[{()}]/g, ''),
      };
      return { ...prevObject.current };
    }
    return {
      title: '',
      firstName: '',
      lastName: '',
      emailAddress: '',
    };
  };

  useEffect(() => {
    if (
      !displayDynamic &&
      control?._formValues?.bbGuestDetails?.[index]?.id?.length &&
      !isTheSameGuest(control._formValues.bbGuestDetails[index], getComposedGuest())
    ) {
      control._formValues.bbGuestDetails[index].id = '';
    }
  });

  const resetFields = () => {
    setGuestUser(defaultGuestValue, index, componentName);
  };

  const handleSwitchToDynamic = () => {
    if (
      Object.keys(control._formState.errors).length > 0 &&
      control._formState.errors.bbGuestDetails
    ) {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      control._formState.errors.bbGuestDetails[index] = {};
    }
    if (componentName && handleResetField) {
      resetFields();
      handleResetField(componentName, { defaultValue: [defaultGuestValue] });
    } else {
      reset(guestList, { keepValues: true, keepErrors: true });
    }
    setDisplayDynamic(!displayDynamic);
  };

  const { isLoggedIn } = useUserData();
  const userData = useUserDetails(true, isLoggedIn, userDetails);
  const accessLevel = userData?.business?.accessLevel;
  const isInputDisabled = isAmendPage && accessLevel === BUSINESS_BOOKER_USER_ROLES.SELF;
  const has2AdulstInRoom =
    (reservationByIdList && reservationByIdList[index]?.roomStay?.adultsNumber === 2) ?? false;

  return (
    <Box data-testid={formatDataTestId(testid, `Room-${roomNumber + 1}`)}>
      {numberOfRooms > 1 && (
        <Text
          {...roomNumberLayoutStyles}
          {...getTypographyProps(roomNumberLegacyTypography, roomNumberSemanticTypography)}
          data-testid={formatDataTestId(testid, `LabelRoomNumber ${roomNumber + 1}`)}
        >{`${t('booking.summary.room')} ${roomNumber + 1}`}</Text>
      )}
      {numberOfRooms > 1 && isAccompanyingGuestDetailsEnabled && (
        <Text
          {...leadGuestTitleLayoutStyles(has2AdulstInRoom)}
          {...getTypographyProps(leadGuestTitleLegacyTypography, leadGuestTitleSemanticTypography)}
          data-testid={formatDataTestId(testid, 'LeadGuestDetails')}
        >
          {t('booking.leadGuest.Details')}
        </Text>
      )}
      {displayDynamic ? (
        <Flex
          flexDirection="column"
          data-testid={formatDataTestId(testid, `DynamicGuestLead-${roomNumber + 1}`)}
        >
          <DynamicSearchEmployee
            baseDataTestId={testid}
            queryClient={queryClient}
            listExclusion={guestList.bbGuestDetails}
            setGuestUser={setGuestUser}
            setDisplayDynamic={setDisplayDynamic}
            index={index}
            control={control}
            errors={errors}
            isDisabled={isInputDisabled}
            bbEmployeeList={bbEmployeeList}
            defaultGuest={defaultGuest}
            isAmendPage={isAmendPage}
            amendInputWidth={amendInputWidth}
            onEditBbInput={onEditBbInput}
            componentName={componentName}
          />
          {!isInputDisabled && (
            <Text
              {...switchLinkLayoutStyles}
              {...getTypographyProps(switchLinkLegacyTypography, switchLinkSemanticTypography)}
              data-testid={formatDataTestId(testid, 'SwitchToManual')}
              onClick={() => {
                // from dynamic to manual
                if (
                  control._formState.errors.bbGuestDetails &&
                  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                  // @ts-ignore
                  Object.keys(control._formState.errors.bbGuestDetails[index] || {}).length
                ) {
                  // eslint-disable-next-line @typescript-eslint/ban-ts-comment
                  // @ts-ignore
                  control._formState.errors.bbGuestDetails[index] = {};
                }
                setDisplayDynamic(!displayDynamic);
              }}
            >
              {t('booking.enterManualEmployee')}
            </Text>
          )}
        </Flex>
      ) : (
        <Flex
          flexDirection="column"
          key={roomNumber}
          data-testid={formatDataTestId(testid, `ManualGuestLead-${roomNumber + 1}`)}
        >
          <Box
            {...inputWrapperStyle}
            width={isAmendPage ? amendInputWidth : inputWrapperStyle.width}
          >
            <Controller
              name={`bbGuestDetails[${roomNumber}][title]`}
              control={control}
              render={({ field }: any) => {
                // eslint-disable-next-line @typescript-eslint/no-unused-vars
                const { onChange, ref: _, ...extraPropsField } = field;
                return (
                  <Dropdown
                    {...extraPropsField}
                    onChange={(o) => {
                      onChange(o?.id);
                      const editedField: KeyValuePair = { key: 'title', value: o?.id as string };
                      onEditBbInput?.(editedField);
                    }}
                    showStatusIcon
                    options={dropdownOptions}
                    placeholder={labels?.title}
                    hasError={Boolean(errors?.bbGuestDetails?.[roomNumber]?.title?.message)}
                    dataTestId={formatDataTestId(testid, 'TitleDropdown')}
                    selectedId={field.value}
                    matchWidth
                    dropdownStyles={{
                      ...dropdownStyles,
                      menuButtonStyles: renderDropdownStyles(
                        Boolean(errors?.bbGuestDetails?.[roomNumber]?.title?.message)
                      ),
                      menuButtonTextStyles: dropdownMenuButtonTextStyles,
                    }}
                  />
                );
              }}
            />
            {errors?.bbGuestDetails?.[roomNumber]?.title?.message && (
              <Box {...formErrorStyles} data-testid={formatDataTestId(testid, 'ErrorContainer')}>
                <Box {...errorMessageStyles}>
                  <Text
                    {...errorTextLayoutStyles}
                    {...getTypographyProps(errorTextLegacyTypography, errorTextSemanticTypography)}
                    data-testid={formatDataTestId(testid, 'ErrorText')}
                  >
                    {errors?.bbGuestDetails?.[roomNumber]?.title?.message}
                  </Text>
                </Box>
              </Box>
            )}
          </Box>
          <Box
            {...inputWrapperStyle}
            width={isAmendPage ? amendInputWidth : inputWrapperStyle.width}
          >
            <Controller
              name={`bbGuestDetails[${roomNumber}][firstName]`}
              control={control}
              render={({ field }: any) => {
                // eslint-disable-next-line @typescript-eslint/no-unused-vars
                const { onChange, ref: _, ...extraPropsField } = field;
                return (
                  <Input
                    {...formField.props}
                    styles={inputLabelStyles}
                    {...extraPropsField}
                    onChange={(v) => {
                      onChange(v);
                      const editedField: KeyValuePair = { key: 'firstName', value: v };
                      onEditBbInput?.(editedField);
                    }}
                    type="text"
                    showIcon
                    placeholderText={labels?.firstName}
                    label={labels?.firstName}
                    error={errors?.bbGuestDetails?.[roomNumber]?.firstName?.message}
                    className="sessioncamhidetext assist-no-show"
                    data-testid={formatDataTestId(testid, 'FirstName')}
                  />
                );
              }}
            />
          </Box>
          <Box
            {...inputWrapperStyle}
            width={isAmendPage ? amendInputWidth : inputWrapperStyle.width}
          >
            <Controller
              name={`bbGuestDetails[${roomNumber}][lastName]`}
              control={control}
              render={({ field }: any) => {
                // eslint-disable-next-line @typescript-eslint/no-unused-vars
                const { onChange, ref: _, ...extraPropsField } = field;
                return (
                  <Input
                    {...formField.props}
                    styles={inputLabelStyles}
                    {...extraPropsField}
                    onChange={(v) => {
                      onChange(v);
                      const editedField: KeyValuePair = { key: 'lastName', value: v };
                      onEditBbInput?.(editedField);
                    }}
                    type="text"
                    placeholderText={labels?.lastName}
                    label={labels?.lastName}
                    showIcon
                    error={errors?.bbGuestDetails?.[roomNumber]?.lastName?.message}
                    className="sessioncamhidetext assist-no-show"
                    data-testid={formatDataTestId(testid, 'LastName')}
                  />
                );
              }}
            />
          </Box>
          <Box
            {...inputWrapperStyle}
            width={isAmendPage ? amendInputWidth : inputWrapperStyle.width}
          >
            <Controller
              name={`bbGuestDetails[${roomNumber}][emailAddress]`}
              control={control}
              render={({ field }: any) => {
                // eslint-disable-next-line @typescript-eslint/no-unused-vars
                const { onChange, ref: _, ...extraPropsField } = field;
                return (
                  <Input
                    {...formField.props}
                    styles={inputLabelStyles}
                    {...extraPropsField}
                    onChange={(v) => {
                      onChange(v);
                      const editedField: KeyValuePair = { key: 'emailAddress', value: v };
                      onEditBbInput?.(editedField);
                    }}
                    type="text"
                    placeholderText={labels?.email}
                    label={labels?.email}
                    error={errors?.bbGuestDetails?.[roomNumber]?.emailAddress?.message}
                    showIcon
                    className="sessioncamhidetext assist-no-show"
                    data-testid={formatDataTestId(testid, 'Email')}
                    emailFieldProps={{
                      inputMode: 'email' as const,
                      autoCapitalize: 'off',
                      autoCorrect: 'off',
                      autoComplete: 'email',
                    }}
                  />
                );
              }}
            />
          </Box>
          <Text
            data-testid={formatDataTestId(testid, 'SwitchToDynamic')}
            {...switchLinkLayoutStyles}
            {...getTypographyProps(switchLinkLegacyTypography, switchLinkSemanticTypography)}
            // button from manual to dynamic
            onClick={handleSwitchToDynamic}
          >
            {t('booking.searchForEmployee')}
          </Text>
        </Flex>
      )}
    </Box>
  );
}

const leadGuestTitleLayoutStyles = (has2AdulstInRoom: boolean) => {
  return {
    color: 'darkGrey1',
    mb: { xl: has2AdulstInRoom && '2xl' },
    mt: 'xlg',
  } as TextProps;
};

const leadGuestTitleLegacyTypography = {
  fontSize: 'lg',
  fontWeight: 'semibold',
  lineHeight: '3',
};

const leadGuestTitleSemanticTypography = {
  textStyle: 'heading-s',
};

const roomNumberLayoutStyles = {
  color: 'darkGrey1',
  mt: 'xl',
} as TextProps;

const roomNumberLegacyTypography = {
  fontSize: 'xl',
  fontWeight: 'semibold',
  lineHeight: '3',
};

const roomNumberSemanticTypography = {
  textStyle: 'heading-s',
};

const inputWrapperStyle = {
  width: { mobile: '100%', sm: '21.938rem', md: '21.75rem', lg: '24.5rem', xl: '26.25rem' },
  marginTop: 'md',
  borderColor: 'lightGrey1',
};

const formInputStyle = {
  inputElementStyles: {
    borderColor: 'lightGrey1',
    _hover: { borderColor: 'darkGrey1' },
  },
};

const labelLegacyTypography = {
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: '1',
};

const labelSemanticTypography = {
  textStyle: 'body-s-regular',
};

const inputTextLegacyTypography = {};

const inputTextSemanticTypography = {
  textStyle: 'body-m-regular',
};

const dropdownLegacyTypography = {};

const dropdownSemanticTypography = {
  textStyle: 'body-s-regular',
};

const formErrorStyles = {
  height: 'var(--chakra-space-lg)',
  position: 'relative',
} as BoxProps;

const errorMessageStyles = {
  position: 'absolute',
} as BoxProps;

const errorTextLayoutStyles = {
  color: 'error',
  marginLeft: 'md',
  marginTop: 'sm',
  whiteSpace: 'nowrap',
} as TextProps;

const errorTextLegacyTypography = {
  fontSize: 'xs',
};

const errorTextSemanticTypography = {
  textStyle: 'body-s-regular',
};

const renderDropdownStyles = (hasError: boolean) => {
  return {
    border: hasError ? '2px solid' : '1px solid',
    borderColor: hasError ? 'var(--chakra-colors-error)' : 'lightGrey1',
    borderRadius: 'var(--chakra-radii-md)',
  };
};

const switchLinkLayoutStyles = {
  textDecoration: 'underline',
  width: 'fit-content',
  color: 'btnSecondaryEnabled',
  mt: 'md',
  cursor: 'pointer',
  whiteSpace: 'nowrap',
} as TextProps;

const switchLinkLegacyTypography = {
  fontSize: 'md',
  lineHeight: '3',
  fontWeight: 'normal',
};

const switchLinkSemanticTypography = {
  textStyle: 'link-m-regular',
};

const amendInputWidth = {
  mobile: '100%',
  sm: '100%',
  md: '100%',
  lg: '100%',
  xl: '100%',
};

const dropdownStyles = {
  menuListStyles: {
    py: 0,
    maxHeight: '12.5rem',
    zIndex: 999999,
  },
};
