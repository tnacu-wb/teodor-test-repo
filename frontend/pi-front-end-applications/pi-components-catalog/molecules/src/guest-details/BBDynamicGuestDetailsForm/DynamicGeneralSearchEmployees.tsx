import { Box, BoxProps, Flex, FlexProps } from '@chakra-ui/react';
import { QueryClient } from '@tanstack/react-query';
import { GuestDetails, KeyValuePair, Suggestion } from '@whitbread-eos/api';
import { Card, Input } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  generateSuggestionList,
  getListOfEmployees,
  renderSanitizedHtml,
  useDebounce,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useRef, useState } from 'react';

interface Props {
  queryClient: QueryClient;
  listExclusion: GuestDetails[];
  setGuestUser: any;
  index: number;
  setDisplayDynamic: any;
  control: any;
  errors: any;
  baseDataTestId?: string;
  isDisabled?: boolean;
  bbEmployeeList?: Suggestion[];
  defaultGuest?: GuestDetails;
  isAmendPage?: boolean;
  onEditBbInput?: (data: KeyValuePair | GuestDetails) => void;
  amendInputWidth?: {
    mobile: string;
    sm: string;
    md: string;
    lg: string;
    xl: string;
  };
  componentName: string;
}

export default function DynamicGeneralSearchEmployee({
  queryClient,
  listExclusion,
  setGuestUser,
  index,
  setDisplayDynamic,
  control,
  errors,
  baseDataTestId,
  isDisabled,
  bbEmployeeList = [],
  defaultGuest,
  isAmendPage,
  onEditBbInput,
  amendInputWidth,
  componentName,
}: Readonly<Props>) {
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
  const defaultGuestComposedName = isDefaultGuestSelected
    ? `${defaultGuest?.title} ${defaultGuest?.firstName} ${defaultGuest?.lastName}`
    : '';

  const { t } = useTranslation('');
  const [suggestions, setSuggestions] = useState<Suggestion[]>(bbEmployeeList);
  const [activeKey, setActiveKey] = useState(0);
  const [selectedItem, setSelectedItem] = useState<GuestDetails>(defaultGuestValue);
  const [isDropDownVisible, setIsDropDownVisible] = useState(!isAmendPage);
  const [hasError, setHasError] = useState(false);
  const [value, setValue] = useState<string>(defaultGuestComposedName);
  const refInput = useRef<any>(null);
  const debouncedOnChange = useDebounce(debounceInput);

  useEffect(() => {
    setActiveKey(0);
  }, [suggestions]);

  useEffect(() => {
    if (
      errors?.[componentName]?.[index] &&
      Object.keys(errors?.[componentName][index]).length > 0
    ) {
      setHasError(true);
    }
  }, [errors, index]);

  return (
    <Flex {...inputWrapperStyle} width={isAmendPage ? amendInputWidth : inputWrapperStyle.width}>
      <Input
        autoComplete="off"
        value={value}
        onClick={onClickInput}
        name="searchCriteria"
        data-testid={formatDataTestId(baseDataTestId, 'Input-Dynamic-Search')}
        placeholderText={t('booking.searchCriteria')}
        label={t('booking.searchCriteria')}
        showIcon={!isAmendPage && !!selectedItem}
        error={hasError && t('config.errorMessages.yourDetails.empSearch.invalid')}
        onBlur={onChangeHandler}
        onChange={onChangeInput}
        onKeyDown={onKeyDownInput}
        inputRef={refInput}
        isDisabled={isDisabled}
        className="sessioncamhidetext assist-no-show"
      />

      {suggestions.length > 0 && isDropDownVisible && (
        <Card
          {...cardSuggestionsStyle}
          data-testid={formatDataTestId(baseDataTestId, `SuggestionCard`)}
        >
          {suggestions.map((suggestion: Suggestion, key: number) => {
            return (
              <Box
                {...suggestionItemStyle(activeKey === key)}
                key={suggestion.employee.composedName}
                id={`DropDownItem-${key}`}
                data-testid={formatDataTestId(baseDataTestId, `DropDownItem-${key}`)}
                onMouseOver={() => setActiveKey(key)}
                onClick={onChangeHandler}
                className="assist-no-show"
              >
                {renderSanitizedHtml(suggestion.prettyFormatDisplay)}
              </Box>
            );
          })}
        </Card>
      )}
    </Flex>
  );

  function onKeyDownInput(event: KeyboardEvent) {
    const key = event.key;

    if (key === 'Enter') {
      event.preventDefault();
      if (suggestions.length === 0 || selectedItem.id !== '') {
        setDisplayDynamic(false);
      } else {
        onChangeHandler();
      }
    }

    if (key === 'ArrowUp') {
      if (activeKey === 0) {
        scrollToElement(suggestions.length - 1);
        return setActiveKey(suggestions.length - 1);
      }

      scrollToElement(activeKey - 1);
      setActiveKey(activeKey - 1);
    }

    if (key === 'ArrowDown') {
      if (activeKey === suggestions.length - 1) {
        scrollToElement(0);
        return setActiveKey(0);
      }

      scrollToElement(activeKey + 1);
      setActiveKey(activeKey + 1);
    }
  }

  function onClickInput() {
    if (value !== '' && selectedItem.firstName !== '') {
      // when you enter second time after you selected a valid suggestion
      resetField();
      setHasError(true);
    } else {
      setIsDropDownVisible(true);
    }
  }

  function resetField() {
    setValue('');
    setSelectedItem(defaultGuestValue);
    setGuestUser(defaultGuestValue, index, componentName);
    setSuggestions([]);
    setActiveKey(0);
    if (componentName === 'bbGuestDetails') {
      control._formValues.bbGuestDetails[index] = defaultGuestValue;
    } else if (
      componentName === 'bbAccompanyingGuestDetails' &&
      control?._formValues?.bbAccompanyingGuestDetails[index]
    ) {
      control._formValues.bbAccompanyingGuestDetails[index] = defaultGuestValue;
    } else if (
      componentName === 'guestDetailsBBForm' &&
      control?._formValues?.guestDetailsBBForm[index]
    ) {
      control._formValues.guestDetailsBBForm[index] = defaultGuestValue;
    }

    setIsDropDownVisible(true);
  }

  function debounceInput() {
    if (value.length > 2 && value.length <= 30) {
      searchEmployees(value);
    } else {
      setSuggestions([]);
    }
  }

  function onChangeInput(value: string) {
    setHasError(false);
    debouncedOnChange();
    setValue(value);
  }

  function onChangeHandler() {
    if (value === '' || (value !== '' && suggestions.length === 0)) {
      // on blur when you typed nothing or no suggestions exist
      setHasError(true);
      resetField();
    }
    if (suggestions.length > 0 && isDropDownVisible) {
      const { title, firstName, lastName } = suggestions[activeKey].employee;
      const value = `${title} ${firstName} ${lastName}`;
      setValue(value);
      setSelectedItem(suggestions[activeKey]?.employee);
      setGuestUser(suggestions[activeKey]?.employee, index, componentName);
      if (componentName === 'bbGuestDetails') {
        control._formValues.bbGuestDetails[index] = suggestions[activeKey]?.employee;
      } else if (
        componentName === 'bbAccompanyingGuestDetails' &&
        control?._formValues?.bbAccompanyingGuestDetails[index]
      ) {
        control._formValues.bbAccompanyingGuestDetails[index] = suggestions[activeKey]?.employee;
      } else if (
        componentName === 'guestDetailsBBForm' &&
        control?._formValues?.guestDetailsBBForm[index]
      ) {
        control._formValues.guestDetailsBBForm[index] = suggestions[activeKey]?.employee;
      }
      setIsDropDownVisible(false);
      onEditBbInput?.(suggestions[activeKey]?.employee);
    }
  }

  function searchEmployees(searchCriteria: string) {
    getListOfEmployees({
      awaitingApproval: false,
      bookingChannel: 'CBT',
      page: 1,
      searchCriteria: searchCriteria,
      size: 10,
      queryClient: queryClient,
    })
      .then((data) => {
        if (data) {
          setSuggestions(generateSuggestionList(data, value, listExclusion));
        } else {
          setSuggestions([]);
        }
      })
      .catch((err) => {
        console.log(err);
        setHasError(true);
        setSuggestions([]);
      });
  }
}

function suggestionItemStyle(isActiveKey: boolean) {
  return {
    bg: isActiveKey ? 'lightGrey5' : 'baseWhite',
    px: 'md',
    py: 'xmd',
    lineHeight: '1',
    fontFamily: 'body',
    alignItems: 'center',
  } as BoxProps;
}

function scrollToElement(activeKey: number) {
  const element = document.getElementById(`DropDownItem-${activeKey}`);
  if (element) {
    element.scrollIntoView({
      block: 'nearest',
      behavior: 'smooth',
    });
  }
}

const cardSuggestionsStyle = {
  flexDirection: 'column',
  maxHeight: '12.625rem',
  overflowY: 'scroll',
  padding: '0',
} as FlexProps;

const inputWrapperStyle = {
  width: { mobile: '100%', sm: '21.938rem', md: '21.75rem', lg: '24.5rem', xl: '26.25rem' },
  marginTop: 'md',
  borderColor: 'lightGrey1',
  flexDirection: 'column',
} as FlexProps;
