SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF NOT EXISTS (SELECT 1 FROM dbo.ProductCategory WHERE categoryName = 'MEAT')
        INSERT INTO dbo.ProductCategory (categoryName, name)
        VALUES ('MEAT', 'Kød');

    IF NOT EXISTS (SELECT 1 FROM dbo.ProductCategory WHERE categoryName = 'VEGETABLES')
        INSERT INTO dbo.ProductCategory (categoryName, name)
        VALUES ('VEGETABLES', 'Grøntsager');

    IF NOT EXISTS (SELECT 1 FROM dbo.ProductCategory WHERE categoryName = 'EGGS')
        INSERT INTO dbo.ProductCategory (categoryName, name)
        VALUES ('EGGS', 'Æg');

    IF NOT EXISTS (SELECT 1 FROM dbo.ProductCategory WHERE categoryName = 'OTHER')
        INSERT INTO dbo.ProductCategory (categoryName, name)
        VALUES ('OTHER', 'Andet');

    IF NOT EXISTS (SELECT 1 FROM dbo.Product WHERE productNumber = 1001)
        INSERT INTO dbo.Product (productNumber, categoryName, name)
        VALUES (1001, 'EGGS', 'Æg 10 stk');

    IF NOT EXISTS (SELECT 1 FROM dbo.Product WHERE productNumber = 2001)
        INSERT INTO dbo.Product (productNumber, categoryName, name)
        VALUES (2001, 'MEAT', 'Oksekød 1 kg');

    IF NOT EXISTS (SELECT 1 FROM dbo.Product WHERE productNumber = 3001)
        INSERT INTO dbo.Product (productNumber, categoryName, name)
        VALUES (3001, 'VEGETABLES', 'Gulerødder 0.5 kg');

    IF NOT EXISTS (SELECT 1 FROM dbo.Location WHERE name = 'Den Glade Bondemand')
        INSERT INTO dbo.Location (name)
        VALUES ('Den Glade Bondemand');

    IF NOT EXISTS (SELECT 1 FROM dbo.Location WHERE name = 'Vejbod')
        INSERT INTO dbo.Location (name)
        VALUES ('Vejbod');

    IF NOT EXISTS (SELECT 1 FROM dbo.Location WHERE name = 'Fryser 1')
        INSERT INTO dbo.Location (name)
        VALUES ('Fryser 1');

    IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE phoneNo = '11111111')
        INSERT INTO dbo.Customer (phoneNo, name)
        VALUES ('11111111', 'Jakob');

    IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE phoneNo = '22222222')
        INSERT INTO dbo.Customer (phoneNo, name)
        VALUES ('22222222', 'Marvin');

    IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE phoneNo = '33333333')
        INSERT INTO dbo.Customer (phoneNo, name)
        VALUES ('33333333', 'Oscar');

    IF NOT EXISTS (SELECT 1 FROM dbo.Customer WHERE phoneNo = '44444444')
        INSERT INTO dbo.Customer (phoneNo, name)
        VALUES ('44444444', 'Christian');

    IF NOT EXISTS (
        SELECT 1
        FROM dbo.Price
        WHERE productNumber = 1001
          AND validFrom = '2026-01-01'
    )
        INSERT INTO dbo.Price (productNumber, amount, validFrom, validTo)
        VALUES (1001, 25.00, '2026-01-01', NULL);

    IF NOT EXISTS (
        SELECT 1
        FROM dbo.Price
        WHERE productNumber = 2001
          AND validFrom = '2026-01-01'
    )
        INSERT INTO dbo.Price (productNumber, amount, validFrom, validTo)
        VALUES (2001, 160.00, '2026-01-01', NULL);

    IF NOT EXISTS (
        SELECT 1
        FROM dbo.Price
        WHERE productNumber = 3001
          AND validFrom = '2026-01-01'
    )
        INSERT INTO dbo.Price (productNumber, amount, validFrom, validTo)
        VALUES (3001, 15.00, '2026-01-01', NULL);

    DECLARE @denGladeBondemandId INT = (
        SELECT locationId
        FROM dbo.Location
        WHERE name = 'Den Glade Bondemand'
    );

    DECLARE @vejbodId INT = (
        SELECT locationId
        FROM dbo.Location
        WHERE name = 'Vejbod'
    );

    DECLARE @fryserId INT = (
        SELECT locationId
        FROM dbo.Location
        WHERE name = 'Fryser 1'
    );

    IF NOT EXISTS (
        SELECT 1
        FROM dbo.StockItem
        WHERE productNumber = 1001
          AND locationId = @vejbodId
          AND expirationDate = '2026-06-01'
    )
        INSERT INTO dbo.StockItem (productNumber, locationId, availableQty, expirationDate)
        VALUES (1001, @vejbodId, 30, '2026-06-01');

    IF NOT EXISTS (
        SELECT 1
        FROM dbo.StockItem
        WHERE productNumber = 2001
          AND locationId = @fryserId
          AND expirationDate = '2026-12-31'
    )
        INSERT INTO dbo.StockItem (productNumber, locationId, availableQty, expirationDate)
        VALUES (2001, @fryserId, 12, '2026-12-31');

    IF NOT EXISTS (
        SELECT 1
        FROM dbo.StockItem
        WHERE productNumber = 3001
          AND locationId = @denGladeBondemandId
          AND expirationDate = '2026-05-20'
    )
        INSERT INTO dbo.StockItem (productNumber, locationId, availableQty, expirationDate)
        VALUES (3001, @denGladeBondemandId, 20, '2026-05-20');

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0
        ROLLBACK TRANSACTION;

    THROW;
END CATCH;
