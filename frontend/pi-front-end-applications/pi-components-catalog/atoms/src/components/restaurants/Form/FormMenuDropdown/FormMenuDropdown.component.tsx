import { Flex } from '@chakra-ui/react';
import { GET_MENUS } from '@whitbread-eos/api';
import {
  useAppData,
  fetchFormattedDateValues,
  useQueryRequestRestaurants,
} from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import { Alert } from '../../../../assets/icons';
import LoadingSpinner from '../../../LoadingSpinner';
import Notification from '../../../Notification';
import FormDropdown from '../FormDropDown/FormDropdown.component';
import {
  Error,
  Errors,
  FormDropdownOptionType,
  FormFieldProps,
  Menu,
  MenusData,
} from '../formTypes';

const FormMenuDropdown = ({
  getValues,
  setValue,
  control,
  formField,
  errors,
  time,
  selectedDateValue = String(new Date()),
  setIsMenuOptionAvailable,
}: FormFieldProps) => {
  const appData = useAppData();
  const fromDate = fetchFormattedDateValues(selectedDateValue);
  const [menuDropdownList, setMenuDropdownList] = useState<FormDropdownOptionType[]>([]);

  const {
    isLoading: isMenuLoading,
    isError: isMenuError,
    data,
    error,
  } = useQueryRequestRestaurants(['getMenus' + fromDate + time], GET_MENUS, {
    from: fromDate,
    until: fromDate,
    siteId: appData?.siteId,
    time: time,
    ocassionId: appData?.occasionId,
  });

  const typedData = data as MenusData;
  const menuList: Menu[] = typedData?.menu?.menus || [];
  const typedError = error as Error | null;
  const menuError: Errors[] = typedError?.response?.errors || [];

  useEffect(() => {
    if (menuList?.length && time) {
      const filteredOptions = menuList
        .filter((menu: Menu) => menu.available)
        .map((menu: Menu) => ({ id: menu.id, value: menu.name, label: menu.name }));
      setMenuDropdownList(filteredOptions);

      if (filteredOptions.length && setIsMenuOptionAvailable) {
        setIsMenuOptionAvailable(true);
      }
    } else {
      if (setIsMenuOptionAvailable) {
        setIsMenuOptionAvailable(false);
      }
      setMenuDropdownList([]);
    }
  }, [menuList?.length, time]);

  if (isMenuLoading) {
    return <LoadingSpinner />;
  }

  if (isMenuError) {
    return (
      <Flex>
        <Notification
          description={menuError[0].message ? 'Menu are not available' : ''}
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
    );
  }
  if (menuDropdownList.length && time) {
    return (
      <FormDropdown
        getValues={getValues}
        setValue={setValue}
        formField={{ ...formField, dropdownOptions: menuDropdownList }}
        control={control}
        errors={errors}
      />
    );
  }
  return null;
};

export default FormMenuDropdown;
