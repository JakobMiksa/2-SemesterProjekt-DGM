SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID('dbo.SaleOrderLine', 'U') IS NOT NULL
        DROP TABLE dbo.SaleOrderLine;

    IF OBJECT_ID('dbo.ReservedOrderLine', 'U') IS NOT NULL
        DROP TABLE dbo.ReservedOrderLine;

    IF OBJECT_ID('dbo.SaleOrder', 'U') IS NOT NULL
        DROP TABLE dbo.SaleOrder;

    IF OBJECT_ID('dbo.ReservedOrder', 'U') IS NOT NULL
        DROP TABLE dbo.ReservedOrder;

    IF OBJECT_ID('dbo.StockItem', 'U') IS NOT NULL
        DROP TABLE dbo.StockItem;

    IF OBJECT_ID('dbo.Price', 'U') IS NOT NULL
        DROP TABLE dbo.Price;

    IF OBJECT_ID('dbo.Product', 'U') IS NOT NULL
        DROP TABLE dbo.Product;

    IF OBJECT_ID('dbo.Location', 'U') IS NOT NULL
        DROP TABLE dbo.Location;

    IF OBJECT_ID('dbo.ProductCategory', 'U') IS NOT NULL
        DROP TABLE dbo.ProductCategory;

    IF OBJECT_ID('dbo.Customer', 'U') IS NOT NULL
        DROP TABLE dbo.Customer;

    CREATE TABLE dbo.Customer (
        phoneNo VARCHAR(20) NOT NULL,
        name VARCHAR(100) NOT NULL,

        CONSTRAINT PK_Customer PRIMARY KEY (phoneNo)
    );

    CREATE TABLE dbo.ProductCategory (
        categoryName VARCHAR(50) NOT NULL,
        name VARCHAR(100) NOT NULL,

        CONSTRAINT PK_ProductCategory PRIMARY KEY (categoryName)
    );

    CREATE TABLE dbo.Product (
        productNumber INT NOT NULL,
        categoryName VARCHAR(50) NOT NULL,
        name VARCHAR(100) NOT NULL,

        CONSTRAINT PK_Product PRIMARY KEY (productNumber),
        CONSTRAINT FK_Product_ProductCategory
            FOREIGN KEY (categoryName)
            REFERENCES dbo.ProductCategory(categoryName)
    );

    CREATE TABLE dbo.Location (
        locationId INT IDENTITY(1,1) NOT NULL,
        name VARCHAR(100) NOT NULL,

        CONSTRAINT PK_Location PRIMARY KEY (locationId),
        CONSTRAINT UQ_Location_Name UNIQUE (name)
    );

    CREATE TABLE dbo.Price (
        priceId INT IDENTITY(1,1) NOT NULL,
        productNumber INT NOT NULL,
        amount DECIMAL(10,2) NOT NULL,
        validFrom DATE NOT NULL,
        validTo DATE NULL,

        CONSTRAINT PK_Price PRIMARY KEY (priceId),
        CONSTRAINT FK_Price_Product
            FOREIGN KEY (productNumber)
            REFERENCES dbo.Product(productNumber),
        CONSTRAINT CHK_Price_Amount
            CHECK (amount >= 0),
        CONSTRAINT CHK_Price_ValidDates
            CHECK (validTo IS NULL OR validTo >= validFrom)
    );

    CREATE TABLE dbo.StockItem (
        stockItemId INT IDENTITY(1,1) NOT NULL,
        productNumber INT NOT NULL,
        locationId INT NOT NULL,
        availableQty INT NOT NULL,
        expirationDate DATE NOT NULL,

        CONSTRAINT PK_StockItem PRIMARY KEY (stockItemId),
        CONSTRAINT FK_StockItem_Product
            FOREIGN KEY (productNumber)
            REFERENCES dbo.Product(productNumber),
        CONSTRAINT FK_StockItem_Location
            FOREIGN KEY (locationId)
            REFERENCES dbo.Location(locationId),
        CONSTRAINT CHK_StockItem_AvailableQty
            CHECK (availableQty >= 0)
    );

    CREATE TABLE dbo.ReservedOrder (
        reservedOrderId INT IDENTITY(1,1) NOT NULL,
        customerPhoneNo VARCHAR(20) NOT NULL,
        [date] DATE NOT NULL,
        expiryDate DATE NOT NULL,
        paymentMethod VARCHAR(50) NOT NULL,
        total DECIMAL(10,2) NOT NULL,

        CONSTRAINT PK_ReservedOrder PRIMARY KEY (reservedOrderId),
        CONSTRAINT FK_ReservedOrder_Customer
            FOREIGN KEY (customerPhoneNo)
            REFERENCES dbo.Customer(phoneNo),
        CONSTRAINT CHK_ReservedOrder_Total
            CHECK (total >= 0),
        CONSTRAINT CHK_ReservedOrder_ExpiryDate
            CHECK (expiryDate >= [date])
    );

    CREATE TABLE dbo.ReservedOrderLine (
        reservedOrderLineId INT IDENTITY(1,1) NOT NULL,
        reservedOrderId INT NOT NULL,
        productNumber INT NOT NULL,
        quantity INT NOT NULL,
        unitPrice DECIMAL(10,2) NOT NULL,

        CONSTRAINT PK_ReservedOrderLine PRIMARY KEY (reservedOrderLineId),
        CONSTRAINT FK_ReservedOrderLine_ReservedOrder
            FOREIGN KEY (reservedOrderId)
            REFERENCES dbo.ReservedOrder(reservedOrderId),
        CONSTRAINT FK_ReservedOrderLine_Product
            FOREIGN KEY (productNumber)
            REFERENCES dbo.Product(productNumber),
        CONSTRAINT CHK_ReservedOrderLine_Quantity
            CHECK (quantity > 0),
        CONSTRAINT CHK_ReservedOrderLine_UnitPrice
            CHECK (unitPrice >= 0)
    );

    CREATE TABLE dbo.SaleOrder (
        saleOrderId INT IDENTITY(1,1) NOT NULL,
        [date] DATE NOT NULL,
        paymentMethod VARCHAR(50) NOT NULL,
        total DECIMAL(10,2) NOT NULL,

        CONSTRAINT PK_SaleOrder PRIMARY KEY (saleOrderId),
        CONSTRAINT CHK_SaleOrder_Total
            CHECK (total >= 0)
    );

    CREATE TABLE dbo.SaleOrderLine (
        saleOrderLineId INT IDENTITY(1,1) NOT NULL,
        saleOrderId INT NOT NULL,
        productNumber INT NOT NULL,
        quantity INT NOT NULL,
        unitPrice DECIMAL(10,2) NOT NULL,

        CONSTRAINT PK_SaleOrderLine PRIMARY KEY (saleOrderLineId),
        CONSTRAINT FK_SaleOrderLine_SaleOrder
            FOREIGN KEY (saleOrderId)
            REFERENCES dbo.SaleOrder(saleOrderId),
        CONSTRAINT FK_SaleOrderLine_Product
            FOREIGN KEY (productNumber)
            REFERENCES dbo.Product(productNumber),
        CONSTRAINT CHK_SaleOrderLine_Quantity
            CHECK (quantity > 0),
        CONSTRAINT CHK_SaleOrderLine_UnitPrice
            CHECK (unitPrice >= 0)
    );

    CREATE INDEX IX_Product_CategoryName
        ON dbo.Product(categoryName);

    CREATE INDEX IX_Price_ProductNumber_ValidDates
        ON dbo.Price(productNumber, validFrom, validTo);

    CREATE INDEX IX_StockItem_ProductNumber
        ON dbo.StockItem(productNumber);

    CREATE INDEX IX_StockItem_LocationId
        ON dbo.StockItem(locationId);

    CREATE INDEX IX_ReservedOrder_CustomerPhoneNo
        ON dbo.ReservedOrder(customerPhoneNo);

    CREATE INDEX IX_ReservedOrderLine_ReservedOrderId
        ON dbo.ReservedOrderLine(reservedOrderId);

    CREATE INDEX IX_ReservedOrderLine_ProductNumber
        ON dbo.ReservedOrderLine(productNumber);

    CREATE INDEX IX_SaleOrderLine_SaleOrderId
        ON dbo.SaleOrderLine(saleOrderId);

    CREATE INDEX IX_SaleOrderLine_ProductNumber
        ON dbo.SaleOrderLine(productNumber);

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;
