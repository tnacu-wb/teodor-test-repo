import {
  Box,
  Text,
  Flex,
  useMediaQuery,
  BoxProps,
  FlexProps,
  TextProps,
  HeadingProps,
  useToken,
  InputGroup,
} from '@chakra-ui/react';
import { GET_COUNTRIES } from '@whitbread-eos/api';
import {
  Input,
  SingleDatePicker,
  FORM_FIELD_TYPES,
  MultiSelect,
  Accordion,
  Info as InfoIcon,
} from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  getSortedCountriesByCurrentLang,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import dynamic from 'next/dynamic';
import { useRouter } from 'next/router';
import { useEffect, useState } from 'react';
import { Controller, ControllerRenderProps } from 'react-hook-form';

import CountriesDropdown from '../guest-details/Countries';
import RoomCard from './RoomCard';
import {
  countryDropdownStyle,
  dataErrorText,
  fieldProps,
  bookDetailGridStyles,
  showPassportField,
  inputIconStyles,
  inputStyle,
  personalDetailsFields,
  todayLabel,
  tomorrowLabel,
  Nationality,
  accordionOverwriteStyles,
  getNationality,
  handleAccordionToggle,
  CountryType,
  addScrollEvent,
  handleNationalityChange,
} from './common';

const Tooltip = dynamic(
  async () => {
    const { Tooltip } = await import('@whitbread-eos/atoms');
    return { default: Tooltip };
  },
  {
    ssr: false,
  }
);

interface FieldController {
  field: ControllerRenderProps;
  label: string;
  name: string;
  type: string;
  testId: string;
  disabled?: boolean;
}
type BaseStyles = Record<string, any>;

const PersonalDetails = ({ control, formField, errors, getValues, handleSetValue }: any) => {
  const { t } = useTranslation();
  const { query } = useRouter();
  const { language, country } = useCustomLocale();
  const { setIsLocationRequired, isMobilePreRegisteredRepurposeEnabled } = formField.props;
  const [darkGrey1] = useToken('colors', ['darkGrey1']);
  const [lightGrey1, primary] = useToken('colors', ['lightGrey1', 'primary']);
  const [space4xl] = useToken('space', ['4xl']);
  const [fontMd] = useToken('fontSizes', ['md']);
  const [darkGrey2] = useToken('colors', ['darkGrey2']);
  const [isLargerThanSm] = useMediaQuery('(min-width: 576px)');
  const [countries, setCountries] = useState<{ value: string }[]>([]);
  const [nationalities, setNationalities] = useState<Nationality[]>([]);
  const [accordionIndex, setAccordionIndex] = useState(0);
  const [toggleDobInfo, setToggleDobInfo] = useState(false);

  const { data: countriesData, isSuccess: countriesRequestSuccess } = useQueryRequest(
    ['GetCountries', country, language, 'leisure'],
    GET_COUNTRIES,
    {
      country,
      language,
      site: 'leisure',
    }
  );

  const nationalityErrorMessage = errors?.['nationality']?.message
    ? errors?.['nationality']?.message
    : errors?.['nationality']?.value?.message;

  const multiSelectStyles = {
    control: (baseStyles: BaseStyles) => ({
      ...baseStyles,
      borderColor: lightGrey1,
      minHeight: space4xl,
      fontSize: fontMd,
      boxShadow: 'none',
      borderWidth: '1px',
      borderRadius: '0.375rem',
      ':hover': { borderColor: lightGrey1 },
      border: nationalityErrorMessage ? '2px solid var(--chakra-colors-error)' : baseStyles.border,
    }),
    placeholder: (baseStyles: BaseStyles) => ({
      ...baseStyles,
      color: darkGrey2,
    }),
    valueContainer: (baseStyles: BaseStyles) => ({
      ...baseStyles,
      padding: '2px 16px',
    }),
    container: (baseStyles: BaseStyles) => ({
      ...baseStyles,
      '&:hover': { borderColor: primary },
    }),
  };

  useEffect(() => {
    if (countriesRequestSuccess && !countries?.length) {
      const sortedCountries = getSortedCountriesByCurrentLang(
        countriesData?.countries?.countries,
        language
      );
      setCountries(
        sortedCountries?.map(({ countryName }: { countryName: string }) => ({ value: countryName }))
      );
      setNationalities(
        sortedCountries?.map(({ nationality = '', countryCode, flagSrc }: CountryType) => ({
          value: countryCode,
          label: nationality || '',
          image: flagSrc,
        }))
      );
    }
  }, [countriesRequestSuccess, countriesData]);

  let datePickerStyles = {
    ...inputStyle,
    _placeholder: { color: darkGrey1, opacity: 0.8 },
  };
  if (errors?.dateOfBirth?.message) {
    datePickerStyles = {
      ...datePickerStyles,
      border: '1px solid var(--chakra-colors-error)',
      borderColor: 'none',
    };
  }

  useEffect(() => {
    let timeoutId: ReturnType<typeof setTimeout>;
    if (toggleDobInfo) timeoutId = setTimeout(() => setToggleDobInfo(false), 3000);

    return () => clearTimeout(timeoutId);
  }, [toggleDobInfo]);

  const fieldController = ({ field, label, name, type, testId, disabled }: FieldController) => {
    switch (type) {
      case 'input':
        return (
          <Input
            {...formField.props}
            {...fieldProps(field)}
            isDisabled={disabled}
            type="text"
            showIcon={false}
            placeholderText={t(`precheckin.details.${label}`)}
            showLabel
            label={t(`precheckin.details.${label}`)}
            error={errors?.[name]?.message}
            className="sessioncamhidetext assist-no-show"
          />
        );
      case 'countryDropdown':
        return (
          <CountriesDropdown
            formField={{
              ...formField,
              label: t(`precheckin.details.${label}`),
              type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
              testid: formatDataTestId(formField.testid, 'CountryDropdown'),
              props: {
                ...formField.props,
                showIcon: false,
                setIsLocationRequired,
                styles: countryDropdownStyle,
              },
            }}
            getValues={getValues()}
            field={field}
          />
        );
      case 'datePicker':
        return (
          <Box position="relative" mb={toggleDobInfo ? '2xl' : ''}>
            <Tooltip
              {...tooltipStyle}
              description={t('precheckin.dob.tooltip')}
              variant="inlineInfo"
              placement="bottom-start"
              isOpen={toggleDobInfo}
              textStyle={{ fontSize: 'xs' }}
              position="relative"
              boxShadow="none"
            >
              <InputGroup display="block" isolation="auto">
                <SingleDatePicker
                  {...(language === 'de' ? { locale: 'de' } : { locale: 'en' })}
                  name={name}
                  minDate={new Date(new Date().getFullYear() - 100, 0, 1)} // 100 years ago
                  maxDate={new Date()} // Current date
                  inputPlaceholder={t(`precheckin.details.${label}`)}
                  inputLabel={t(`precheckin.details.${label}`)}
                  defaultStartDate={field.value || null}
                  isRightIcon
                  popperPlacement={isLargerThanSm ? 'bottom' : 'top'}
                  displayDateFormat="dd MMM yyyy"
                  dataTestId={formatDataTestId(formField.testid, testId)}
                  dateFormat="dd MMM yyyy"
                  customHeader
                  labels={{
                    todayLabel,
                    tomorrowLabel,
                  }}
                  datepickerStyles={{
                    inputGroupStyles: {},
                    datepickerInputElementStyles: datePickerStyles,
                    iconStyles: inputIconStyles,
                  }}
                  onSelectDate={field.onChange}
                  isError={errors?.[name]?.message ? true : false}
                />
              </InputGroup>
            </Tooltip>
            <Box {...infoIconStyles} onClick={() => !toggleDobInfo && setToggleDobInfo(true)}>
              <InfoIcon data-testid="datepicker-info-icon" />
            </Box>
            {errors?.[name]?.message && (
              <Text style={dataErrorText} mt="2">
                {errors?.[name]?.message}
              </Text>
            )}
          </Box>
        );
      case 'autoComplete':
        return (
          <>
            <MultiSelect
              name={name}
              options={nationalities}
              dataTestId={formatDataTestId(formField.testid, testId)}
              value={getNationality(field.value, nationalities)}
              onChange={(fieldProp) =>
                handleNationalityChange(fieldProp, field, 'passport', handleSetValue)
              }
              placeholder={t(`precheckin.details.${label}`)}
              styles={multiSelectStyles}
              isClearable={true}
            />
            {nationalityErrorMessage && (
              <Text style={dataErrorText} mt="2">
                {nationalityErrorMessage}
              </Text>
            )}
          </>
        );

      default:
        return <></>;
    }
  };

  const {
    firstName,
    lastName,
    adultsNumber,
    childrenNumber,
    roomName,
    roomNo,
    preCheckInStatus,
    deRegCardCompleted,
  } = getValues();
  const room = {
    firstName,
    lastName,
    adultsNumber,
    childrenNumber,
    roomName,
    preCheckInStatus,
    deRegCardCompleted,
  };

  useEffect(() => {
    if (errors && Object.keys(errors)?.length) {
      setAccordionIndex(0);
    }
    addScrollEvent();
  }, [errors]);

  return (
    <Accordion
      accordionItems={[
        {
          onToggleSection: () => handleAccordionToggle(accordionIndex, setAccordionIndex),
          title: query?.reservationId ? (
            <RoomCard
              room={room}
              roomIndex={roomNo}
              testid={'LeadGuestDetailsHeader'}
              roomCardContainerStyle={roomCardContainerStyle}
              displayIcon={false}
              clickEventRequired={false}
              preCheckInStatus={
                isMobilePreRegisteredRepurposeEnabled
                  ? room.deRegCardCompleted
                  : room.preCheckInStatus
              }
            />
          ) : (
            t('precheckin.yourdetails.title')
          ),
          content: (
            <Flex
              wrap="wrap"
              justifyContent={'space-between'}
              flexDirection="column"
              key={`personalDetail-${roomNo || 0}`}
            >
              {!!(query?.reservationId || roomNo) && (
                <Text {...roomCardContainerStyle.subHeadingStyle}>
                  {t('precheckin.yourdetails.title')}
                </Text>
              )}
              {personalDetailsFields.map(
                ({ label, name, testId, type, disabled }) =>
                  !!(name !== 'passport' || showPassportField(getValues(), 'nationality')) && (
                    <Box
                      {...bookDetailGridStyles}
                      data-testid={formatDataTestId(formField.testid, testId)}
                      key={`${name}-${roomNo || 0}`}
                    >
                      <Controller
                        name={name}
                        control={control}
                        render={({ field }) =>
                          fieldController({ field, label, name, type, testId, disabled })
                        }
                      />
                    </Box>
                  )
              )}
            </Flex>
          ),
        },
      ]}
      bgColor="var(--chakra-colors-baseWhite)"
      allowMultiple={false}
      accordionOverwriteStyles={{
        ...accordionOverwriteStyles,
        container: { ...accordionOverwriteStyles?.container, index: [accordionIndex] },
      }}
    />
  );
};

const roomCardContainerStyle = {
  boxParentStyle: {
    border: 0,
    p: { mobile: '0', md: '0' },
    mb: '0',
    mt: 0,
    px: { mobile: '0', md: '0' },
    cursor: 'pointer',
  },
  boxesContainerStyle: {
    p: { mobile: 'md md', md: 'xl xl' },
    direction: { mobile: 'column', md: 'row' },
    cursor: 'pointer',
    alignItems: 'center',
    justifyContent: 'space-between',
  } as FlexProps,
  firstColumnStyle: {
    w: { mobile: '100%', md: 'auto' },
    minW: '15rem',
  } as BoxProps,
  secondColumnStyle: {
    w: { mobile: '100%', md: 'auto' },
    alignSelf: 'flex-end',
    fontWeight: 'normal',
    fontSize: 'md',
    pt: { mobile: 'lg' },
  } as BoxProps,
  thirdColumnStyle: {
    ml: 'auto',
    w: { mobile: '100%', md: 'auto' },
    alignSelf: 'center',
    textAlign: 'right',
  } as BoxProps,
  headingStyle: {
    fontSize: 'xl',
    fontWeight: 'semibold',
    color: 'baseBlack',
    mb: 'lg',
  } as HeadingProps,
  subHeadingStyle: {
    fontSize: 'lg',
    fontWeight: 'semibold',
    color: 'baseBlack',
    textTransform: 'capitalize',
    pt: 'xl',
  } as HeadingProps,
  textStyle: {
    color: 'var(--chakra-colors-darkGrey1)',
    fontWeight: 'sm',
    textTransform: 'capitalize',
  } as TextProps,
  iconStyle: {
    justifyContent: 'center',
  },
};

const infoIconStyles = {
  position: 'absolute',
  right: '-30px',
  top: '4',
  display: { xs: 'none', sm: 'none', md: 'block' },
  py: '1',
  cursor: 'pointer',
} as BoxProps;

const tooltipStyle = {
  display: 'flex',
  alignContent: 'center',
  maxW: '100%',
} as BoxProps;

export default PersonalDetails;
