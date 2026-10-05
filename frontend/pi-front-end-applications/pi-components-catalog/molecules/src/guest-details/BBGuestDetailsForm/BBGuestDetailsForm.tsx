import { Flex } from '@chakra-ui/react';
import { formatDataTestId } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';

import BBGuestDetailsRoom from './BBGuestDetailsRoom';

export default function BBGuestDetailsForm({ control, formField, errors, reset }: any) {
  const { t } = useTranslation();
  const {
    numberOfRooms = 1,
    labels,
    queryClient,
    guestList,
    setGuestUser,
    isDynamicSearchVisible,
    isAmendPage,
    bbEmployeeList,
    defaultGuest,
    onEditBbInput,
    userDetails,
  } = formField.props;
  const { testid, dropdownOptions } = formField;

  function renderContent() {
    return [...Array(numberOfRooms)].map((value: any, index: number) => {
      return (
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
          userDetails={userDetails}
        />
      );
    });
  }

  return (
    <Flex
      direction="column"
      {...{ ...formField.styles }}
      data-testid={formatDataTestId(testid, 'Container')}
    >
      {renderContent()}
    </Flex>
  );
}
