'use client';

import { EmployeeDetails, OptionType } from '@whitbread-eos/api';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import { getEmployees, getEmployeeDetails } from '@whitbread-eos/utils/server';
import debounce from 'lodash/debounce';
import { useCallback, useEffect, useRef, useState } from 'react';
import * as React from 'react';
import { UseFormSetError } from 'react-hook-form';

import { Popover, PopoverContent, PopoverTrigger } from '../../Popover';
import { SanitizedContent } from '../../SanitizedContent';
import { ErrorTooltip } from '../../Tooltip';
import { FormInput } from '../FormInput';

const PAGE_SIZE = 20;

type Props = {
  companyId: string;
  id: string;
  name: string;
  value: string;
  errors?: Record<string, any>;
  errorIcon: string;
  inputIcon?: string;
  placeholder: string;
  disabled?: boolean;
  setError: UseFormSetError<any>;
  onChange?: (value: string) => void;
  onBlur?: () => void;
  onFocus?: () => void;
  handleEmployeeChange?: (value: OptionType | null) => void;
  className?: string;
  requireAdditionalDetails?: boolean;
  populateInitialEmployee?: boolean;
};

const FormPeoplePicker = React.forwardRef<HTMLInputElement, Props>(
  (
    {
      companyId,
      id,
      value,
      setError,
      onChange = () => {
        return;
      },
      onBlur = () => {
        return;
      },
      onFocus = () => {
        return;
      },
      errors,
      errorIcon,
      inputIcon,
      name,
      placeholder,
      disabled,
      handleEmployeeChange,
      className,
      requireAdditionalDetails = false,
      populateInitialEmployee = false,
    }: Props,
    ref
  ) => {
    const { t } = useTranslation('users');
    const [employees, setEmployees] = useState<OptionType[]>([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [selectedEmployee, setSelectedEmployee] = useState<OptionType | null>(null);
    const [isFocus, setIsFocus] = useState(false);
    const pointerSelectingRef = useRef(false);
    const token = getAuthCookie();
    const error = errors?.[name];
    const hasError = !!error?.type;

    const formatEmployee = (
      title: string | undefined | null,
      firstName: string | undefined | null,
      lastName: string | undefined | null,
      emailAddress: string | undefined | null
    ) => {
      const employeeTitle = title ?? '';
      const employeeFirstName = firstName ?? '';
      const employeeLastName = lastName ?? '';
      const employeeEmailAddress = emailAddress ?? '';
      return employeeEmailAddress
        ? `${employeeTitle} ${employeeFirstName} ${employeeLastName} (${employeeEmailAddress})`.trim()
        : `${employeeTitle} ${employeeFirstName} ${employeeLastName}`.trim();
    };

    const findSelectedEmployeeById = (id: string) => {
      const employee = employees.find((employee) => employee.value === id);
      return employee?.employeeData;
    };

    const getData = useCallback(
      debounce(async (search = '') => {
        const { employees } = await getEmployees(companyId, PAGE_SIZE, token, 1, search);

        setEmployees(
          employees?.map((employee: EmployeeDetails) => {
            return {
              value: employee.id,
              label: formatEmployee(
                employee.title,
                employee.firstName,
                employee.lastName,
                employee.emailAddress
              ),
              employeeData: {
                title: employee.title,
                firstName: employee.firstName,
                lastName: employee.lastName,
                emailAddress: employee.emailAddress,
                employeeIdNumber: employee.employeeId,
              },
            };
          })
        );

        if (!employees.length) {
          setError(name, {
            type: 'custom',
            message: (
              <span>
                <SanitizedContent replacements={{ '%searchTerm%': `'${search}'` }}>
                  {t('userMgmt.manageEmployees.seachresults.noEmployeesFoundErrorMsg')}
                </SanitizedContent>
              </span>
            ) as unknown as string,
          });
        }
      }, 300),
      []
    );

    const getHighlightedText = (text: string, highlight: string) => {
      if (!highlight.trim()) {
        return text;
      }

      const regex = new RegExp(`(${highlight})`, 'gi');
      return text.split(regex).map((part, index) =>
        part.toLowerCase() === highlight.toLowerCase() ? (
          <span key={index} className="font-bold">
            {part}
          </span>
        ) : (
          part
        )
      );
    };

    useEffect(() => {
      if (!value) {
        setSelectedEmployee(null);
        handleEmployeeChange && handleEmployeeChange(null);
        return;
      }
      if (selectedEmployee?.value === value) {
        return;
      }

      if (requireAdditionalDetails && populateInitialEmployee) {
        const getEmployeeInfo = async () => {
          const { title, firstName, lastName, emailAddress, phoneNumber, mobileNumber, position } =
            await getEmployeeDetails(companyId, value, token);

          const selectedEmployeeDataObj = {
            value,
            label: formatEmployee(title, firstName, lastName, emailAddress),
            employeeData: handleEmployeeChange
              ? {
                  title,
                  firstName,
                  lastName,
                  emailAddress,
                  phoneNumber,
                  mobileNumber,
                  jobTitle: position,
                }
              : undefined,
          };

          setSelectedEmployee(selectedEmployeeDataObj);
          handleEmployeeChange && handleEmployeeChange(selectedEmployeeDataObj);
        };

        getEmployeeInfo();
      }
    }, [value]);

    useEffect(() => {
      if (searchTerm.length < 3) {
        return;
      }

      getData(searchTerm);
    }, [searchTerm]);

    const selectEmployee = async (employee: OptionType) => {
      try {
        setIsFocus(false);

        const {
          title = '',
          firstName = '',
          lastName = '',
          emailAddress = '',
          phoneNumber = '',
          mobileNumber = '',
          position = '',
          employeeIdNumber = '',
        } = requireAdditionalDetails
          ? await getEmployeeDetails(companyId, employee.value, token)
          : findSelectedEmployeeById(employee.value);

        const employeeDataObj = {
          ...employee,
          employeeData: handleEmployeeChange
            ? {
                title,
                firstName,
                lastName,
                emailAddress,
                phoneNumber,
                mobileNumber,
                jobTitle: position,
                employeeIdNumber,
              }
            : undefined,
        };

        setSelectedEmployee(employeeDataObj);
        handleEmployeeChange && handleEmployeeChange(employeeDataObj);
        setSearchTerm('');
        onChange(employee.value);
      } finally {
        if (pointerSelectingRef.current) {
          window.setTimeout(() => {
            pointerSelectingRef.current = false;
          }, 0);
        }
      }
    };

    const handleOptionPointerDown = async (
      employee: OptionType,
      event: React.PointerEvent<HTMLButtonElement>
    ) => {
      if (event.pointerType === 'touch') {
        return;
      }
      event.preventDefault();
      pointerSelectingRef.current = true;
      await selectEmployee(employee);
    };

    const handleOptionMouseDown = async (
      employee: OptionType,
      event: React.MouseEvent<HTMLButtonElement>
    ) => {
      if (pointerSelectingRef.current || event.button !== 0) {
        return;
      }
      event.preventDefault();
      pointerSelectingRef.current = true;
      await selectEmployee(employee);
    };

    const handleOptionClick = async (
      employee: OptionType,
      event: React.MouseEvent<HTMLButtonElement>
    ) => {
      event.preventDefault();
      if (pointerSelectingRef.current) {
        return;
      }
      await selectEmployee(employee);
    };

    return (
      <div className={className}>
        <FormInput
          id={id}
          name={name}
          value={selectedEmployee ? selectedEmployee.label : searchTerm}
          placeholder={placeholder}
          errors={errors}
          errorIcon={errorIcon}
          showErrorTooltip={false}
          inputIcon={inputIcon}
          disabled={disabled}
          onChange={(e) => {
            const newValue = e.target.value;
            setSearchTerm(newValue);

            if (selectedEmployee) {
              setSelectedEmployee(null);
              onChange('');
              setEmployees([]);
              setIsFocus(true);
            }

            if (!isFocus) {
              setIsFocus(true);
            }
          }}
          onFocus={() => {
            setIsFocus(true);

            if (!selectedEmployee) {
              setEmployees([]);
              setSearchTerm('');
            }
            onFocus();
          }}
          onBlur={() => {
            onBlur();
            setIsFocus(false);
          }}
          ref={ref}
        />

        <Popover open={isFocus && employees.length > 0}>
          <PopoverTrigger asChild>
            <div></div>
          </PopoverTrigger>
          <ErrorTooltip
            icon={errorIcon}
            content={error?.message}
            open={hasError}
            testId={`${id}-Error-Tooltip`}
            className="!flex"
            mobile
          />
          <PopoverContent
            onInteractOutside={() => {
              setIsFocus(false);
            }}
            align="start"
            className={popoverStyle}
            onOpenAutoFocus={(e) => e.preventDefault()}
            data-testid={`${id}-IB-Form-People-Picker-Dropdown`}
          >
            {employees.map((employee: OptionType) => (
              <button
                type="button"
                data-testid={`${id}-IB-Form-People-Picker-Option-${employee.value}`}
                key={employee.value}
                className={optionStyle}
                onPointerDown={(event) => handleOptionPointerDown(employee, event)}
                onMouseDown={(event) => handleOptionMouseDown(employee, event)}
                onClick={(event) => handleOptionClick(employee, event)}
              >
                <div>{getHighlightedText(employee.label, searchTerm)}</div>
              </button>
            ))}
          </PopoverContent>
        </Popover>
      </div>
    );
  }
);
FormPeoplePicker.displayName = 'Form People Picker';

const popoverStyle = 'w-full flex flex-col max-h-60 overflow-scroll p-0';
const optionStyle =
  'flex w-[--radix-popover-trigger-width] items-center hover:bg-lightGrey5 text-left text-sm py-2.5 px-4 focus-visible:outline-none';

export { FormPeoplePicker };
