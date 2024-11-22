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

### 1 - Creating account

### 2 – Creating cash account

### 3 – Creating commission value

### 4 – Creating assets owner

### 5 – Creating asset (fixed rate bond in this scenario)

