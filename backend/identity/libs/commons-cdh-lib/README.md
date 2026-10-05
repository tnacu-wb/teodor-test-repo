<h1>Customer Data Hub APIs wrapper</h1>

In the context of Account Services project scope, the Whitbread active microservices shall be integrated with Customer Data Hub (CDH).

Project Scope: Have a common project for CDH APIs exposure to be used as a shared library to Whitbread microservices

CDH APIs documentation: D3 - Account Services Phase 1 - Design v1.2
https://whitbread.sharepoint.com/:w:/r/sites/CustomerHUBWS1/_layouts/15/Doc.aspx?sourcedoc=%7B2D3A11F6-EA5F-49C7-BE1D-E8E72A837933%7D&file=D3%20-%20Account%20Services%20Phase%201%20-%20Design%20v1.2.docx&action=default&mobileredirect=true&cid=5137f0cc-470a-4462-a773-eadf25a65c9b

## 17.2.3
| Type | Ticket(s)    | Description                 | Notes     |
|------|--------------|-----------------------------|-----------|
| Fix  | CTECH-12155  | Update GetEmployeesResponse |           |

## 17.2.2
| Type | Ticket(s)  | Description                               | Notes     |
|------|------------|-------------------------------------------|-----------|
| Fix  | CTECH-9985 | Add additional parameters for get company |           |

## 17.2.1
| Type | Ticket(s)  | Description               | Notes     |
|------|------------|---------------------------|-----------|
| Fix  | CTECH-9110 | Expose number of employee |           |

## 17.0.0
| Type | Ticket(s)  | Description                             | Notes     |
|------|------------|-----------------------------------------|-----------|
| Feat | CTECH-7146 | upgrade to springboot 4.0.3 and java 25 |           |

## 16.19.3
| Type | Ticket(s)  | Description            | Notes     |
|------|------------|------------------------|-----------|
| Feat | CTECH-3800 | Improve error handling |           |


## 16.19.2
| Type | Ticket(s)  | Description                          | Notes     |
|------|------------|--------------------------------------|-----------|
| Feat | CTECH-1291 | Add BUSINESS_PAY_MANAGER AccessLevel |           |


## 16.19.0
| Type | Ticket(s)  | Description                                   | Notes     |
|------|------------|-----------------------------------------------|-----------|
| Fix  | DNRQ-89364 | Fix DELETE requests after spring boot upgrade |           |

## 16.18.2
| Type    | Ticket(s)  | Description                      | Notes     |
|---------|------------|----------------------------------|-----------|
| Feature | DNRQ-88389 | Revert Cache getCompanyEmployees |           |

## 16.18.1
| Type    | Ticket(s)  | Description     | Notes     |
|---------|------------|-----------------|-----------|
| Feature | DNRQ-88388 | fix evict cache |           |

## 16.18.0
| Type    | Ticket(s)  | Description               | Notes     |
|---------|------------|---------------------------|-----------|
| Feature | DNRQ-88389 | Cache getCompanyEmployees |           |

## 16.17.2
| Type | Ticket(s)  | Description            | Notes     |
|------|------------|------------------------|-----------|
| Fix  | DNRQ-88230 | Fix registration cache |           |

## 16.17.1
| Type | Ticket(s)  | Description                        | Notes     |
|------|------------|------------------------------------|-----------|
| Fix  | DNRQ-88230 | Fallback to GB when scheme is null |           |

## 16.17.0
| Type | Ticket(s)  | Description                                      | Notes     |
|------|------------|--------------------------------------------------|-----------|
| Bug  | DNRQ-88180 | Can't add or remove a card under payment options |           |

## 16.16.0
| Type    | Ticket(s)  | Description                                                    | Notes     |
|---------|------------|----------------------------------------------------------------|-----------|
| Feature | DNRQ-88176 | question-is-not-showing-up-and-can't-delete-the-existing-ones  |           |

## 16.15.0
| Type    | Ticket(s)  | Description                         | Notes     |
|---------|------------|-------------------------------------|-----------|
| Feature | DNRQ-88179 | Evict-Cache-when-a-user-is-tethered |           |

## 16.14.0
| Type    | Ticket(s)  | Description               | Notes |
|---------|------------|---------------------------|-------|
| Feature | DNRQ-87951 | Cache getDashboardDetails |       |

## 16.13.0
| Type    | Ticket(s)  | Description             | Notes |
|---------|------------|-------------------------|-------|
| Feature | DNRQ-87819 | Fix redis object mapper |       |

## 16.12.0
| Type    | Ticket(s)  | Description                            | Notes |
|---------|------------|----------------------------------------|-------|
| Feature | DNRQ-87819 | Cache get employee and company details |       |

## 16.11.0
| Type    | Ticket(s)  | Description                              | Notes |
|---------|------------|------------------------------------------|-------|
| Feature | DNRQ-79458 | Integrate new fields for Company Details |       |


## 16.10.0
| Type    | Ticket(s)  | Description                                   | Notes |
|---------|------------|-----------------------------------------------|-------|
| Feature | DNRQ-84128 | Add directDebit related fields on Application |       |

## 16.9.1
| Type | Ticket(s)  | Description                              | Notes |
|------|------------|------------------------------------------|-------|
| Fix  | DNRQ-83901 | PIBA Account numbers must all be Strings |       |

## 16.9.0
| Type    | Ticket(s)  | Description                   | Notes |
|---------|------------|-------------------------------|-------|
| Feature | DNRQ-83812 | Integrate CDH CardHolders API |       |

## 16.8.0
| Type    | Ticket(s)  | Description                    | Notes |
|---------|------------|--------------------------------|-------|
| Feature | DNRQ-78847 | Update Company endpoints to V3 |       |

## 16.7.0
| Type    | Ticket(s)   | Description                            | Notes |
|---------|-------------|----------------------------------------|-------|
| Feature | DNRQ-82351  | Integrate CDH FetchApplicationUser API |       |

## 16.6.1
| Type    | Ticket(s)                                                      | Description                                                                                                                            | Notes |
|---------|----------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------------------------|-------|
| Feature | DNRQ-82317                                                     | - Add a new field to CompanySpendingResponse (BookingCurrency)<br> - Remove old Jackson Config<br> - Use Bean Qualifier for webClient. |       |

## 16.6.0
| Type    | Ticket(s)  | Description                               | Notes |
|---------|------------|-------------------------------------------|-------|
| Feature | DNRQ-82206 | Integrate CDH UpdateApplicationStatus API |       |

## 16.5.1
| Type    | Ticket(s)  | Description                           | Notes |
|---------|------------|---------------------------------------|-------|
| Feature | DNRQ-81311 | Integrate CDH GetUpcomingBookings API |       |

## 16.5.0
| Type    | Ticket(s)  | Description                        | Notes |
|---------|------------|------------------------------------|-------|
| Feature | DNRQ-79132 | Integrate CDH FetchApplication API |       |

## 16.4.0
| Type    | Ticket(s)  | Description                    | Notes |
|---------|------------|--------------------------------|-------|
| Feature | DNRQ-80949 | Integrate CDH TetheredUser API |       |

## 16.3.0
| Type    | Ticket(s)   | Description                              | Notes |
|---------|-------------|------------------------------------------|-------|
| Feature |  DNRQ-80870 | Integrate CDH UpdateApplication endpoint |       |

## 16.2.0
| Type    | Ticket(s)  | Description                             | Notes |
|---------|------------|-----------------------------------------|-------|
| Feature | DNRQ-80868 | Integrate CDH StartApplication endpoint |       |

## 16.1.0
| Type    | Ticket(s)  | Description                             | Notes |
|---------|------------|-----------------------------------------|-------|
| Feature | DNRQ-80231 | Integrate CDH GetTransactionDetails API |       |

## 16.0.0
| Type    | Ticket(s)  | Description                                                                                  | Notes |
|---------|------------|----------------------------------------------------------------------------------------------|-------|
| Feature | DNRQ-79239 | OAuthFeignClientFallbackFactory - change the fallbackfactory from hystrix to springframework |       |

## 15.3.0
| Type    | Ticket(s)  | Description                                   | Notes |
|---------|------------|-----------------------------------------------|-------|
| Feature | DNRQ-77673 | Integrate CDH GetAccountLevelDetails endpoint |       |

## 15.2.0
| Type    | Ticket(s)  | Description                                | Notes |
|---------|------------|--------------------------------------------|-------|
| Feature | DNRQ-77653 | Integrate CDH GetDashboardDetails endpoint |       |

## 15.1.0
| Type    | Ticket(s)   | Description                                    | Notes |
|---------|-------------|------------------------------------------------|-------|
| Feature | DNRQ-77518  | Integrate CDH GetCompanyLevelDetails endpoint  |       |

## 15.0.0
| Type    | Ticket(s)   | Description                                                                 | Notes |
|---------|-------------|-----------------------------------------------------------------------------|-------|
| Feature | DNRQ-68210  | CDH token removed from Redis cache and stored on each microservice instance |       |

## 14.6.0
| Type    | Ticket(s)   | Description                                                                           | Notes |
|---------|-------------|---------------------------------------------------------------------------------------|-------|
| Feature | DNRQ-68212  | Use V2 endpoint for company creation and add support for employee activation endpoint |       |

## v14.5.0
| Type    | Ticket(s)  | Description                                            | Notes |
|---------|------------|--------------------------------------------------------|-------|
| Feature | DNRQ-68225 | Bulk employee download - increase webClient memorySize |       |

## v14.4.0
| Type    | Ticket(s)  | Description                                           | Notes |
|---------|------------|-------------------------------------------------------|-------|
| Feature | DNRQ-67562 | Company employee - Implement CentralCardIdString logic|       |

## v14.3.1
| Type    | Ticket(s)  | Description                                           | Notes |
|---------|------------|-------------------------------------------------------|-------|
| Bugfix  | DNRQ-67636 | Booking allowances updates removes some CDH fields    |       |

## v14.3.0
| Type    | Ticket(s)  | Description                                           | Notes |
|---------|------------|-------------------------------------------------------|-------|
| Feature | DNRQ-67461 | Add employeeAccountId field to EmployeeAccountRequest |       |

## v14.2.0
| Type    | Ticket(s)  | Description                        | Notes |
|---------|------------|------------------------------------|-------|
| Feature | DNRQ-67114 | Add deleted field on company cards |       |

## v14.1.0
| Type    | Ticket(s)  | Description                                 | Notes |
|---------|------------|---------------------------------------------|-------|
| Feature | DNRQ-66079 | Use V2 endpoint for CDH update employee     |       |
| Feature | DNRQ-66272 | CompanyType added to CompanyAccountRequest  |       |

## v14.0.1
| Type   | Ticket(s)  | Description                                                     | Notes                                                                                                                                     |
|--------|------------|-----------------------------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------|
| Bugfix | DNRQ-64975 | Fixed webclient issue where connections were prematurely closed | config.httpClient.connectionTimeout (default value = 10000) and config.httpClient.responseTimeout (default value = 30) configs were added |

## v14.0.0
| Type    | Ticket(s)  | Description                                                                      | Notes                                                                   |
|---------|------------|----------------------------------------------------------------------------------|-------------------------------------------------------------------------|
| Feature | DNRQ-63557 | Upgrate to spring boot 3 and java 17 after upgrating hotel-register microservice | Upgrate to spring boot 3 and java 17; fixed dependencies after upgrate; |

## v13.11.0
| Type    | Ticket(s)  | Description                                                                | Notes |
|---------|------------|----------------------------------------------------------------------------|-------|
| Feature | DNRQ-61043 | APPLICATION_FORM_URLENCODED_VALUE header added for getCustomerAccountList  |       |

## v13.10.0
| Type    | Ticket(s)  | Description                                           | Notes                                                                |
|---------|------------|-------------------------------------------------------|----------------------------------------------------------------------|
| Feature | DNRQ-52033 | Integrate with the new custom account search endpoint | totalResults and searchResults added to GetCustomerAccountsResponse  |

## v13.9.0
| Type    | Ticket(s)  | Description                                           | Notes                                                                   |
|---------|------------|-------------------------------------------------------|-------------------------------------------------------------------------|
| Feature | DNRQ-52033 | Integrate with the new custom account search endpoint | It comes with extended search criteria to accommodate CCUI agents needs |

## v13.8.0
| Type    | Ticket(s)  | Description                                                      | Notes                              |
|---------|------------|------------------------------------------------------------------|------------------------------------|
| Feature | DNRQ-56255 | Better exception handling for booking history retrieval from CDH | Enhancement for covering all cases |

## v13.7.0
| Type    | Ticket(s)  | Description                                                       | Notes |
|---------|------------|-------------------------------------------------------------------|-------|
| Feature | DNRQ-56030 | revert initial fix, as it was not behaving correctly in all cases |       |

## v13.6.0
| Type    | Ticket(s)  | Description                                                      | Notes |
|---------|------------|------------------------------------------------------------------|-------|
| Feature | DNRQ-56030 | Better exception handling for booking history retrieval from CDH |       |

## v13.5.0
| Type    | Ticket(s)  | Description                                 | Notes |
|---------|------------|---------------------------------------------|-------|
| Bugfix  | DNRQ-49594 | Fixed CDH Authorization token caching issue |       |       |
| Feature | DNRQ-48656 | Added source system to search booking       |       |

## v13.4.0
## v13.3.0
| Type    | Ticket(s)  | Description                      | Notes |
|---------|------------|----------------------------------|-------|
| Feature | DNRQ-49377 | Added support for Totals summary |       |

## v13.2.0
| Type    | Ticket(s)  | Description                       | Notes |
|---------|------------|-----------------------------------|-------|
| Feature | DNRQ-49377 | Added support for Bookings API V2 |       |

## v13.1.1
| Type    | Ticket(s)  | Description                | Notes |
|---------|------------|----------------------------|-------|
| Feature | DNRQ-48957 | Fix error response logging |       |

## v13.1.0
| Type    | Ticket(s)  | Description                     | Notes |
|---------|------------|---------------------------------|-------|
| Feature | DNRQ-48957 | Improved error response logging |       |

## v13.0.0
| Type    | Ticket(s)  | Description                                                | Notes                                                  |
|---------|------------|------------------------------------------------------------|--------------------------------------------------------|
| Feature | DNRQ-47124 | Define UpsellItem code as a String instead of Long         | Breaking change, so updating the lib version to 13.0.0 |

## v12.2.1
| Type    | Ticket(s)  | Description                                                | Notes                                                                                                                                                                                                                 |
|---------|------------|------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Bugfix  | DNRQ-42651 | fix CDH bearer token refresh issue                         | The ticket ID is referring to the UAT deploy of Account Services. The refresh token bug was found after the deploy was made and is necessary for the deploy to work, that is why it is included in the deploy ticket. |

## v12.2.0
| Type    | Ticket(s)  | Description                                                | Notes                                                                                                                                                                                                                 |
|---------|------------|------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Feature | DNRQ-37072 | EmployeeStatus changed to Status in EmployeeAccountRequest |                                                                                                                                                                                                                       |

## v12.1.0
| Type    | Ticket(s)  | Description                                                 | Notes |
|---------|------------|-------------------------------------------------------------|-------|
| Feature | DNRQ-30851 | Added access level filter to GetCompanyEmployeesQueryParams |       |

## v12.0.0
| Type         | Ticket(s)  | Description                                                                                                                  | Notes |
|--------------|------------|------------------------------------------------------------------------------------------------------------------------------|-------|
| Housekeeping | DNRQ-33940 | Change from companyAccountID to companyAccountId in GetCustomerAccountBookingsQueryParams and add EmployeeAnswers to Booking |       |

## v11.0.0
| Type         | Ticket(s)  | Description                                   | Notes                                                                            |
|--------------|------------|-----------------------------------------------|----------------------------------------------------------------------------------|
| Housekeeping | DNRQ-36701 | Follow-up on CDH API changes - FoodPreference | Also made updates on BookingPreference and PaymentCard.ElectronicInvoiceRequired |

## v10.0.0
| Type         | Ticket(s)  | Description                           | Notes |
|--------------|------------|---------------------------------------|-------|
| Housekeeping | DNRQ-31061 | Follow-up on CDH API response changes |       |

## v9.0.0
| Type         | Ticket(s)  | Description                           | Notes |
|--------------|------------|---------------------------------------|-------|
| Housekeeping | DNRQ-30842 | Updated get companies response schema |       |

## v8.1.0
| Type    | Ticket(s)  | Description                                       | Notes |
|---------|------------|---------------------------------------------------|-------|
| Feature | DNRQ-27862 | Added support for create employee question in cdh |       |

## v8.1.0
| Type    | Ticket(s)  | Description                                                           | Notes |
|---------|------------|-----------------------------------------------------------------------|-------|
| Feature | DNRQ-27903 | Add access modifier for company response to update booking allowances |       |


## v8.0.0
| Type    | Ticket(s)  | Description                             | Notes |
|---------|------------|-----------------------------------------|-------|
| Feature | DNRQ-27538 | Added support for update company in cdh |       |

## v7.0.0
| Type    | Ticket(s)  | Description                                          | Notes |
|---------|------------|------------------------------------------------------|-------|
| Feature | DNRQ-27854 | Changing models schema according with new cdh schema |       |

## v6.4.0
| Type    | Ticket(s)  | Description                                              | Notes |
|---------|------------|----------------------------------------------------------|-------|
| Feature | DNRQ-27882 | Added support for update employee questions              |       |

## v6.3.0
| Type    | Ticket(s)  | Description                                              | Notes |
|---------|------------|----------------------------------------------------------|-------|
| Feature | DNRQ-27536 | Change json properties name according to new cdh schemas |       |

## v6.2.0
| Type    | Ticket(s)  | Description                                 | Notes |
|---------|------------|---------------------------------------------|-------|
| Feature | DNRQ-27885 | Added support for delete employee questions |       |

## v6.1.0
| Type    | Ticket(s)  | Description                              | Notes |
|---------|------------|------------------------------------------|-------|
| Feature | DNRQ-27847 | Added support for get employee questions |       |

## v6.0.0
| Type         | Ticket(s)  | Description                          | Notes            |
|--------------|------------|--------------------------------------|------------------|
| Housekeeping | DNRQ-32355 | Update get employees response schema | Breaking changes |

## v5.5.0
| Type    | Ticket(s)  | Description                        | Notes |
|---------|------------|------------------------------------|-------|
| Feature | DNRQ-30473 | Added support for add payment card |       |

## v5.4.0
| Type    | Ticket(s)  | Description                                            | Notes |
|---------|------------|--------------------------------------------------------|-------|
| Feature | DNRQ-30474 | Added support for update payment card details          |       |

## v5.3.0
| Type    | Ticket(s)  | Description                                            | Notes |
|---------|------------|--------------------------------------------------------|-------|
| Feature | DNRQ-30475 | Added support for delete and get company payment cards |       |

## v5.2.0
| Type    | Ticket(s)  | Description                            | Notes |
|---------|------------|----------------------------------------|-------|
| Feature | DNRQ-26299 | Added support for get company endpoint |       |

## v5.1.2
| Type    | Ticket(s)  | Description                                                                             | Notes |
|---------|------------|-----------------------------------------------------------------------------------------|-------|
| Feature | DNRQ-27060 | Changing BusinessPaymentPreference.electronicInvoiceRequest type from String to boolean |       |

## v5.1.1
| Type   | Ticket(s)  | Description                                       | Notes |
|--------|------------|---------------------------------------------------|-------|
| Bugfix | DNRQ-27065 | Get status code from CDH response instead of body |       |

## v5.1.0
| Type    | Ticket(s)  | Description                                                  | Notes |
|---------|------------|--------------------------------------------------------------|-------|
| Feature | DNRQ-25068 | Added support for get companies and create company endpoints |       |

## v5.0.1
| Type   | Ticket(s)  | Description                                     | Notes                                                               |
|--------|------------|-------------------------------------------------|---------------------------------------------------------------------|
| Bugfix | DNRQ-28739 | Used different subscription key for Booking API | Added cdh.api.oauth.booking-subscription-key configuration property |

## v5.0.0
| Type    | Ticket(s)  | Description                        | Notes                                                       |
|---------|------------|------------------------------------|-------------------------------------------------------------|
| Feature | DNRQ-15488 | Pi.com - ReservationSearch support | Breaking changes in the cdh.api.host configuration property |

## v4.0.0
| Type    | Ticket(s)  | Description                                        | Notes                                                                                 |
|---------|------------|----------------------------------------------------|---------------------------------------------------------------------------------------|
| Feature | DNRQ-27098 | Add support for employee API's in commons-cdh-lib  | Increment version to 4.0.0 because of braking changes in the configuration properties |

## v3.0.1
| Type   | Ticket(s)  | Description                                              | Notes |
|--------|------------|----------------------------------------------------------|-------|
| Bugfix | DNRQ-24833 | Fixed the error that appears when creating a user in CDH |       |

## v3.0.0
| Type    | Ticket(s)  | Description                                                         | Notes |
|---------|------------|---------------------------------------------------------------------|-------|
| Feature | DNRQ-23987 | Adapt CDH APIs implementations to the latest CDH API specifications |       |

## v2.0.0
| Type    | Ticket(s)  | Description                                             | Notes                                      |
|---------|------------|---------------------------------------------------------|--------------------------------------------|
| Feature | DNRQ-21106 | PI.com - BE - integration of CDH account lazy migration | Config properties improvement refactoring. |

## v1.1.0
| Type | Ticket(s)  | Description         | Notes                                          |
| --- |------------|---------------------|------------------------------------------------|
| Feature | DNRQ-15481 | PI.com integration with CDH DeleteCustomerAccount | Added support for deleting a customer account. |

## v1.0.0
| Type | Ticket(s)  | Description         | Notes |
| --- |------------|---------------------| --- |
| Feature | DNRQ-16247 | CDH common services | No config changes |

## Supported APIs

Currently, the following CDH APIs are supported:

* **Get Customer Account**
* **Create Customer Account**
* **Update Customer Account**
* **Delete Customer Account**

## How to configure

The following properties need to be added in application.yml/bootstrap.yml file:

    cdh:
        oauth-client:
            host: https://login.microsoftonline.com
            token-url: ${AZURE_OAUTH_CLIENT_TOKEN_URL}/oauth2/v2.0/token
            client-id: ${AZURE_OAUTH_CLIENT_CLIENT_ID}
            client-secret: ${AZURE_OAUTH_CLIENT_CLIENT_SECRET}
            scope: https://wbch-uat-api-v2.azurewebsites.net/.default
            grant-type: client_credentials
            enabled: true
        api:
            host: https://whitbread-uat.azure-api.net
            is-oauth: true
            oauth:
                subscription-key: ${CUSTOMER_HUB_OAUTH_SUBSCRIPTION_KEY}
                booking-subscription-key: ${BOOKING_HUB_OAUTH_SUBSCRIPTION_KEY}

Add @EnableCaching to the main class in order to enable caching in Spring boot, this way you can get the CDH bearer token from cache, if it's present. 
