<a href="https://codeclimate.com/github/MarkDementev/fundAssetsData/maintainability"><img src="https://api.codeclimate.com/v1/badges/db745d42d986457e00f8/maintainability" /></a>
<a href="https://codeclimate.com/github/MarkDementev/fundAssetsData/test_coverage"><img src="https://api.codeclimate.com/v1/badges/db745d42d986457e00f8/test_coverage" /></a>

# Overview

Spring Boot REST application – web-service for storing data of investing assets. The service is intended for private investors who do not have enough brokerage applications to control their own assets. This problem often arises for investors with average capital, the size of which is already larger than the market average, but less than for creating a family office.

## Functionality of version 0.1-b

The service currently supports operations with the following types of assets:

1)	Bank accounts with cash

2)	Fixed-rate bonds

Adding support for deposits is planned for the near future. 

The initial support for these types of assets is due to the historically highest level of yield on bonds and deposits, as determined by the policy of the Central Bank of the Russian Federation.

## How to use

The code is covered with comments using JavaDoc. Also, when running the application, documentation is available using Swagger at /swagger.html.

The basic usage scenario is as follows. It involves creating a bank account, recording data on the brokerage tariff, creating an asset owner, indicating the amount of available funds in the account. After that, it is possible to carry out transactions with investment assets.

NB: Service requires authentication to /login before any another requests!

### 1 - Creating account

#### Request:

```sh
POST data/accounts + JSON
```

```sh
{
    "organisationWhereAccountOpened": "ABC Bank",
    "accountNumber": "1234567890",
    "accountOpeningDate": "2000-01-01"
}
```

#### Response:

```sh
{
    "id": 1,
    "organisationWhereAccountOpened": "ABC Bank",
    "accountNumber": "1234567890",
    "accountOpeningDate": [
        2000,
        1,
        1
    ],
    "createdAt": 1732280628.893573000,
    "updatedAt": 1732280628.893643000
}
```

### 2 – Creating commission value

#### Request:

```sh
POST /data/turnover-commission-values + JSON
```

```sh
{
    "accountID": 1,
    "assetTypeName": "FixedRateBondPackage",
    "commissionPercentValue": "10"
}
```

#### Response:

```sh
{
    "id": 1,
    "account": {
        "id": 1,
        "organisationWhereAccountOpened": "ABC Bank",
        "accountNumber": "1234567890",
        "accountOpeningDate": [
            2000,
            1,
            1
        ],
        "createdAt": 1732298164.299000000,
        "updatedAt": 1732298164.299000000
    },
    "assetTypeName": "FixedRateBondPackage",
    "commissionPercentValue": 0.1,
    "createdAt": 1732298181.227027000,
    "updatedAt": 1732298181.227042000
}
```

### 3 – Creating assets owner

```sh
POST data/owners/russia + JSON
```

```sh
{
    "name": "name",
    "surname": "surname",
    "birthDate": "25.05.1999",
    "email": "Email_sur@mail.ru",
    "patronymic": "patronymic",
    "sex": "MAN",
    "mobilePhoneNumber": "9888888888",
    "passportSeries": "2424",
    "passportNumber": "111111",
    "placeOfBirth": "placeOfBirth",
    "placeOfPassportGiven": "placeOfPassportGiven",
    "issueDate": "24.08.2021",
    "issuerOrganisationCode": "377-777"
}
```

#### Response:

```sh
{
    "id": 1,
    "name": "name",
    "surname": "surname",
    "birthDate": [
        1999,
        5,
        25
    ],
    "email": "Email_sur@mail.ru",
    "createdAt": 1732377693.645703000,
    "updatedAt": 1732377693.645791000,
    "patronymic": "patronymic",
    "sex": "MAN",
    "mobilePhoneNumber": "+79888888888",
    "passportSeries": "2424",
    "passportNumber": "111111",
    "placeOfBirth": "placeOfBirth",
    "placeOfPassportGiven": "placeOfPassportGiven",
    "issueDate": [
        2021,
        8,
        24
    ],
    "issuerOrganisationCode": "377-777"
}
```

### 4 – Creating cash account

### 5 – Creating asset (fixed rate bond in this scenario)
