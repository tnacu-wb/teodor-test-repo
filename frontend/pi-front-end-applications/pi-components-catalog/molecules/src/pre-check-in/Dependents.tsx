import { Box, Flex } from '@chakra-ui/react';
import { GET_COUNTRIES, SITE_LEISURE } from '@whitbread-eos/api';
import { Dropdown, Accordion } from '@whitbread-eos/atoms';
import {
  formatDataTestId,
  getSortedCountriesByCurrentLang,
  useCustomLocale,
  useQueryRequest,
} from '@whitbread-eos/utils';
import { useTranslation } from 'next-i18next';
import { useEffect, useState } from 'react';
import { Controller, useController, useFieldArray } from 'react-hook-form';

import DependentInfo from './DependentInfo';
import RemoveDependentConfirmationModal from './RemoveDependentConfirmationModal';
import {
  dropdownStyles,
  Nationality,
  renderDropdownStyles,
  accordionOverwriteStyles,
  bookDetailGridStyles,
  handleAccordionToggle,
  CountryType,
} from './common';

const Dependents = ({ control, formField, errors, getValues, handleSetValue }: any) => {
  const { dependents } = formField.props;
  const [confirmationModalOpen, setConfirmationModalOpen] = useState(false);
  const [dependentIndexToBeRemoved, setDependentIndexToBeRemoved] = useState<number>();
  const [dependentIdToBeUpdated, setDependentIdToBeUpdated] = useState<string>();
  const [accordionIndex, setAccordionIndex] = useState<number>(0);
  const [dropDownKey, setDropDownKey] = useState<number>(1);

  const { field: dependentField } = useController({ name: 'dependent', control });

  const { fields, remove, append } = useFieldArray({
    control,
    name: 'dependents',
  });

  const { t } = useTranslation();
  const { language, country } = useCustomLocale();
  const [nationalities, setNationalities] = useState<Nationality[]>([]);

  const { data: countriesData, isSuccess: countriesRequestSuccess } = useQueryRequest(
    ['GetCountries', country, language, SITE_LEISURE],
    GET_COUNTRIES,
    {
      country,
      language,
      site: SITE_LEISURE,
    }
  );

  useEffect(() => {
    if (countriesRequestSuccess && !nationalities?.length) {
      const sortedCountries = getSortedCountriesByCurrentLang(
        countriesData?.countries?.countries,
        language
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

  const addDependent = (fieldsToAdd: number) => {
    for (let i = 0; i < fieldsToAdd; i++) {
      append({ nationality: {}, passport: '', firstname: '', lastname: '', dateofbirth: '' });
    }
  };

  const removeDependentConfirmation = (index: number) => {
    setDependentIndexToBeRemoved(index);
    setConfirmationModalOpen(true);
  };

  const handleDependentDelete = () => {
    if (dependentIndexToBeRemoved !== undefined) {
      remove(dependentIndexToBeRemoved);
      dependentField.onChange(String(dependentField.value - 1));
      setDependentIndexToBeRemoved(undefined);
    }
    if (dependentIdToBeUpdated) {
      removeDependentFields();
      addDependent(+dependentIdToBeUpdated);

      dependentField.onChange(dependentIdToBeUpdated);
      setDependentIdToBeUpdated(undefined);
    }
    setConfirmationModalOpen(false);
  };

  const removeDependentFields = () => {
    fields.forEach(() => remove(0));
    dependentField.onChange('0');
  };

  const handleDependentChange = (count: string) => {
    const fieldsToAdd = getFieldCount(count);
    if (fieldsToAdd > 0) {
      dependentField.onChange(count);
      addDependent(fieldsToAdd);
    } else if (fieldsToAdd < 0) openConfirmationModal(count);
  };

  const openConfirmationModal = (count: string) => {
    setDependentIdToBeUpdated(count);
    setConfirmationModalOpen(true);
  };

  const handleConfirmationModalClose = () => {
    setConfirmationModalOpen(false);
    setDependentIdToBeUpdated(undefined);
    setDependentIndexToBeRemoved(undefined);
    setDropDownKey((prevKey) => prevKey + 1);
  };

  const getFieldCount = (count: string) => +count - fields.length;

  useEffect(() => {
    if (errors && Object.keys(errors)?.length) {
      setAccordionIndex(0);
    }
  }, [errors]);

  return (
    <Accordion
      accordionItems={[
        {
          onToggleSection: () => handleAccordionToggle(accordionIndex, setAccordionIndex),
          title: t('precheckin.additionalguests.title'),
          content: (
            <>
              <Box>
                <Flex wrap="wrap" justifyContent={'space-between'} flexDirection="column">
                  <Box
                    {...bookDetailGridStyles}
                    data-testid={formatDataTestId(formField.testid, 'Dependents')}
                  >
                    <Controller
                      name="dependent"
                      control={control}
                      render={({ field: { value, onBlur } }) => (
                        <Dropdown
                          key={dropDownKey}
                          onChange={(o: any) => handleDependentChange(o?.id)}
                          showStatusIcon={false}
                          options={dependents}
                          dataTestId={formatDataTestId(formField.testid, 'Dependents')}
                          label={t('precheckin.details.dependant')}
                          hasError={Boolean(errors?.dependent?.message)}
                          selectedId={value}
                          matchWidth
                          dropdownStyles={{
                            ...dropdownStyles,
                            menuButtonStyles: renderDropdownStyles(false),
                          }}
                          onBlur={onBlur}
                        />
                      )}
                    />
                  </Box>
                </Flex>
                {Boolean(fields.length) && (
                  <>
                    {fields.map((dependent, i) => {
                      return (
                        <DependentInfo
                          getValues={getValues}
                          key={dependent.id}
                          index={i}
                          removeDependent={removeDependentConfirmation}
                          control={control}
                          nationalities={nationalities}
                          formField={formField}
                          errors={errors}
                          handleSetValue={handleSetValue}
                        />
                      );
                    })}
                  </>
                )}
              </Box>
              <RemoveDependentConfirmationModal
                isOpen={confirmationModalOpen}
                onClose={handleConfirmationModalClose}
                handleDependentDelete={handleDependentDelete}
              />
            </>
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

export default Dependents;
