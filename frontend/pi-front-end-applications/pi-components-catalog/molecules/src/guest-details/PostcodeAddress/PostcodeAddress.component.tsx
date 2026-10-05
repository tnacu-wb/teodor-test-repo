import { Box, Flex } from '@chakra-ui/react';
import { useQueryClient } from '@tanstack/react-query';
import { GET_ADDRESSES, GET_FORMATTED_ADDRESS } from '@whitbread-eos/api';
import {
  Button,
  Dropdown,
  DropdownCustomContent,
  FORM_VALIDATIONS,
  Input,
} from '@whitbread-eos/atoms';
import { formatDataTestId, useCustomLocale, useQueryRequest } from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { memo, useCallback, useEffect, useState } from 'react';
import { string } from 'yup';

type AddressPostalCode = {
  id: string;
  addressText: string;
};

type GuestAddress = {
  postalCode?: string;
  country?: string;
  addressLine1?: string;
  addressLine2?: string;
  addressLine3?: string;
  addressLine4?: string;
  companyName?: string;
};

function PostcodeAddress({
  formField,
  field,
  reset,
  getValues,
  index,
  setGuestAddress,
  updateManualToggle,
}: any) {
  const { t } = useTranslation();
  const queryClient = useQueryClient();
  const { language: currentLang } = useCustomLocale();
  const [postCodeInput, setPostCodeInput] = useState('');
  const [addressesPostalCode, setAddressesPostalCode] = useState<{
    partialAddress: AddressPostalCode[];
  }>();
  const [postcodeErrorMessage, setPostcodeErrorMessage] = useState('');

  const [isRequestAddressEnabled, setIsRequestAddressEnabled] = useState(false);
  const [selectedAddressPostCode, setSelectedAddressPostCode] = useState<AddressPostalCode>({
    id: '',
    addressText: '',
  });
  const [isAddressSelected, setIsAddressSelected] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(true);
  const [placeholderDropdown, setPlaceholderDropdown] = useState('');
  const [isFindBtnClicked, setIsFindBtnClicked] = useState(false);

  const baseDataTestId = 'PostcodeAddress';

  const showIcon = formField.props?.showIcon;
  const fieldName = formField.props?.fieldName || '';
  const containerStyle = formField.props?.containerStyle;
  const externalButtonStyle = formField.props?.buttonStyle ?? {};

  const handleInputErrors = async (value: string) => {
    const checkError = string()
      .required(t('config.errorMessages.yourDetails.postcode.required'))
      .matches(
        currentLang === 'en'
          ? FORM_VALIDATIONS.POSTAL_CODE.MATCHES_UK
          : FORM_VALIDATIONS.POSTAL_CODE.MATCHES_DE,
        t('config.errorMessages.yourDetails.postcode.invalid')
      );

    const validationResult = await checkError.validate(value.toUpperCase()).catch((err: any) => {
      return err;
    });

    setPostcodeErrorMessage(validationResult.errors?.[0]);
    return validationResult.errors?.[0];
  };

  const onChangeFn = (val: string) => {
    handleInputErrors(val);
    onChange(val);
    setPostCodeInput(val.trim());
    setIsRequestAddressEnabled(false);
    setIsAddressSelected(false);
    setIsFindBtnClicked(false);
  };

  const { name, value, onChange } = field;

  // get addresses by postcode
  const { data: addressesData, isSuccess: addressesRequestSuccess } = useQueryRequest(
    ['addresses', postCodeInput],
    GET_ADDRESSES,
    {
      searchTerm: postCodeInput,
    },
    { enabled: isRequestAddressEnabled },
    undefined,
    true
  );

  // get address info  by selected address
  const { data: addressInfoData, isSuccess: addressInfoRequestSuccess } = useQueryRequest(
    ['addresses-info', postCodeInput, selectedAddressPostCode.id],
    GET_FORMATTED_ADDRESS,
    {
      identifier: encodeURIComponent(selectedAddressPostCode.id),
    },
    { enabled: isAddressSelected },
    undefined,
    true
  );

  const updateAddress = useCallback(
    (data: any) => {
      const {
        postalCode,
        country,
        addressLine1,
        addressLine2,
        addressLine3,
        addressLine4,
        companyName,
      } = data?.formattedAddress || {};

      if (setGuestAddress && updateManualToggle && index !== undefined) {
        setGuestAddress((prevItems: GuestAddress[]) => {
          const updatedItems = [...prevItems];
          updatedItems[index] = {
            postalCode,
            country,
            addressLine1,
            addressLine2,
            addressLine3,
            addressLine4,
            companyName,
          };
          return updatedItems;
        });
        updateManualToggle(index, true);
      }

      if (!reset) {
        return;
      }
      const resetObject = {
        ...getValues(),
        [`${fieldName}manualAddressToggle`]:
          fieldName?.length > 0 ? 'billingAddress' : 'manualAddress',
        [`${fieldName}addressSelection`]: companyName?.length ? 'BUSINESS' : 'HOME',
        [`${fieldName}companyName`]: companyName ?? '',
        [`${fieldName}addressLine1`]: addressLine1 ?? '',
        [`${fieldName}addressLine2`]: addressLine2 ?? '',
        [`${fieldName}addressLine3`]: addressLine3 ?? '',
        [`${fieldName}addressLine4`]: addressLine4 ?? '',
        [`${fieldName}cityName`]: addressLine4,
        [`${fieldName}postalCode`]: postalCode,
        [`${fieldName}countryCode`]: country,
      };
      reset(resetObject);
    },
    [reset]
  );

  useEffect(() => {
    if (addressesPostalCode && addressesPostalCode.partialAddress.length > 0) {
      setPlaceholderDropdown(addressesPostalCode.partialAddress[0].addressText);
      setIsDropdownOpen(true);
    }
  }, [addressesPostalCode]);

  useEffect(() => {
    if (addressesRequestSuccess && isFindBtnClicked) {
      setAddressesPostalCode(addressesData);

      if (addressesData?.partialAddress?.length === 0) {
        setPostcodeErrorMessage(t('config.errorMessages.yourDetails.postcode.invalid'));
      }
    }
  }, [addressesRequestSuccess, addressesData, addressesData?.partialAddress, t, isFindBtnClicked]);

  useEffect(() => {
    if (addressInfoRequestSuccess && selectedAddressPostCode) {
      updateAddress(addressInfoData);
    }
  }, [selectedAddressPostCode, addressInfoRequestSuccess, addressInfoData, updateAddress]);

  const Container = containerStyle ? Flex : Box;

  return (
    <Box>
      <Container align="flex-end" gap="md" {...(containerStyle ?? {})}>
        <Input
          {...PostCodeStyle}
          my="md"
          value={value}
          name={name}
          type={formField.type}
          placeholderText={formField.label}
          label={formField.label}
          error={postcodeErrorMessage}
          data-testid={formatDataTestId(baseDataTestId, 'InputPostCode')}
          onChange={onChangeFn}
          showIcon={showIcon !== undefined ? showIcon : true}
          className="sessioncamhidetext assist-no-show"
          styles={formField?.props?.styles}
          isDisabled={formField.isDisabled}
        />
        <Button
          data-testid={formatDataTestId(baseDataTestId, 'FindAddressBtn')}
          onClick={handleFindAddress}
          variant="tertiary"
          {...buttonStyle}
          size="full"
          {...externalButtonStyle}
          isDisabled={formField.isDisabled}
          {...formField?.props?.buttonLabelTypography}
        >
          {t('booking.findAddress')}
        </Button>
      </Container>

      {addressesPostalCode?.partialAddress && addressesPostalCode?.partialAddress?.length > 0 && (
        <Box className="assist-no-show">
          <Dropdown
            dropdownStyles={{
              ...formField?.props?.dropdownStyles,
              menuListStyles: {
                ...menuListStyles,
                ...formField?.props?.dropdownStyles?.menuListStyles,
              },
            }}
            dataTestId={formatDataTestId(baseDataTestId, 'DropdownComp')}
            isOpenMenu={isDropdownOpen}
            placeholder={placeholderDropdown}
            onChange={(address: any) => {
              setSelectedAddressPostCode(address);
              setIsAddressSelected(true);
              setIsDropdownOpen(false);
            }}
            options={addressesPostalCode.partialAddress?.map(
              ({ id, addressText }: AddressPostalCode) => ({
                label: addressText,
                id,
                addressText,
              })
            )}
          >
            {DropdownCustomContent}
          </Dropdown>
        </Box>
      )}
    </Box>
  );

  function handleFindAddress() {
    handleInputErrors(postCodeInput).then((res) => {
      if (!res) {
        setIsRequestAddressEnabled(true);
        queryClient.invalidateQueries({ queryKey: ['addresses'] });
      }
    });
    setIsFindBtnClicked(true);
  }
}

const menuListStyles = {
  w: {
    mobile: 'full',
    xs: 'full',
    sm: '25.063rem',
    md: '27.563rem',
    xl: '26.25rem',
  },
  maxH: 'var(--chakra-space-52)',
};
const PostCodeStyle = {
  mb: 'xl',
};
const buttonStyle = {
  mt: 'md',
  mb: 'md',
};

export default memo(PostcodeAddress);
