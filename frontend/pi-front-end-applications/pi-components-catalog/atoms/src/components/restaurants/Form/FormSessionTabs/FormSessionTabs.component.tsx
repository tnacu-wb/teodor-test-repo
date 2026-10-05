import { Flex } from '@chakra-ui/react';
import { GET_SLOTS } from '@whitbread-eos/api';
import { useAppData, useQueryRequestRestaurants } from '@whitbread-eos/utils';
// import { analytics } from '~utils/analytics';
import { fetchFormattedDateValues } from '@whitbread-eos/utils/restaurants';
import { Controller } from 'react-hook-form';

import { Alert } from '../../../../assets/icons';
import LoadingSpinner from '../../../LoadingSpinner';
import Notification from '../../../Notification';
import Tabs from '../../Tabs';
import { FormFieldProps } from '../formTypes';

function FormSessionTabs({
  control,
  adult = 0,
  childrenValue = 0,
  isEnquiry,
  selectedDateValue = String(new Date()),
  formField,
  errors,
  setValue,
  getValues,
  selectedTimeSlot,
  clearErrors,
}: Readonly<FormFieldProps>) {
  const appData = useAppData();

  const fromDate = fetchFormattedDateValues(selectedDateValue);

  const {
    isLoading,
    isError,
    data,
    error,
  }: { isLoading: boolean; isError: boolean; data: any; error: any } = useQueryRequestRestaurants(
    ['getSlots' + fromDate, adult, childrenValue],
    GET_SLOTS,
    {
      from: fromDate,
      until: fromDate,
      adult: isEnquiry ? 1 : adult,
      children: isEnquiry ? 0 : childrenValue,
      siteId: appData?.siteId,
      time: '',
    },
    undefined,
    undefined,
    {
      onSettled: () => {
        setValue('time', '');
      },
    }
  );

  if (isLoading) {
    return <LoadingSpinner />;
  }

  const sessionsdata = data?.slots?.dates?.[0];
  const {
    lunchAvailable = false,
    dinnerAvailable = false,
    breakFastAvailable = false,
    sessionDto = {
      dinner: [],
      lunch: [],
      breakFast: [],
    },
  } = sessionsdata ?? {};

  const sessionTypeMap: Record<string, string> = {
    lunch: 'Lunch',
    dinner: 'Dinner',
    breakFast: 'Breakfast',
  };

  const availabilityMap: Record<string, boolean> = {
    breakFast: breakFastAvailable,
    lunch: lunchAvailable,
    dinner: dinnerAvailable,
  };

  const filteredSessionDto = Object.fromEntries(
    Object.entries(sessionDto).filter(([key]) => availabilityMap[key])
  );

  const arrayOfsessionListData = Object.keys(filteredSessionDto)
    .reverse()
    .map((item) => ({
      name: sessionTypeMap[item],
      availablility: true,
    }));
  // const unavailableSession = arrayOfsessionListData
  //   .map(({ availablility, name }) => !availablility && name)
  //   .filter(Boolean) as string[];
  // analytics.update({
  //   sessionUnavailability:
  //     unavailableSession.length === 3 ? 'All Sessions' : unavailableSession.join(', '),
  // });

  const tabOnClickHandler = (value: string | number, onChange: (arg0: string | number) => void) => {
    if (value && clearErrors) clearErrors();
    onChange(value);
  };

  return (
    <Flex
      direction="column"
      {...{ ...defaultStyles, ...formField.styles }}
      data-testid={formField.testid}
      data-fieldname={formField.name}
    >
      <Controller
        name={formField.name}
        control={control}
        render={({ field }) => {
          const { onChange } = field;
          return (
            <Tabs
              variant="tabsButton"
              tabData={arrayOfsessionListData}
              tabPanelData={filteredSessionDto}
              baseDataTestId="Session"
              onClick={(value: string | number) => tabOnClickHandler(value, onChange)}
              {...formField.props}
              {...field}
              tabLabel={formField.tabLabel}
              tapLabel={formField.tapLabel}
              error={errors?.[formField.name]?.message}
              setValue={setValue}
              getValues={getValues}
              selectedTimeSlot={selectedTimeSlot}
            />
          );
        }}
      />
      {isError && (
        <Flex>
          <Notification
            description={
              error?.response?.errors[0].message
                ? 'Slots are not available, try another date'
                : ('' as string)
            }
            wrapperStyles={{
              display: 'flex',
              justifyContent: 'center',
            }}
            variant="alert"
            status="error"
            maxW="full"
            svg={<Alert />}
          />
        </Flex>
      )}
    </Flex>
  );
}

const defaultStyles = {
  marginBottom: 'var(--chakra-space-lg)',
};

export default FormSessionTabs;
