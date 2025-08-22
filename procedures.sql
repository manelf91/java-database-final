DELIMITER $$
CREATE PROCEDURE GetMonthlySalesForEachStore(IN year_param INT, IN month_param INT)
BEGIN
    SELECT
        od.store_id,
        SUM(DISTINCT od.total_price) AS total_sales,  -- Use DISTINCT to avoid duplicates
        MONTH(od.date) AS sale_month,
        YEAR(od.date) AS sale_year
    FROM
        order_details od
    JOIN
        order_item oi ON od.id = oi.order_id
    WHERE
        YEAR(od.date) = year_param
        AND MONTH(od.date) = month_param
    GROUP BY
        od.store_id, MONTH(od.date), YEAR(od.date)
    ORDER BY
        total_sales DESC;
END $$

CREATE PROCEDURE GetAggregateSalesForCompany(IN year_param INT, IN month_param INT)
BEGIN
    SELECT
        SUM(DISTINCT od.total_price) AS total_sales,
        MONTH(od.date) AS sale_month,
        YEAR(od.date) AS sale_year
    FROM
        order_details od
    JOIN
        order_item oi ON od.id = oi.order_id
    WHERE
        YEAR(od.date) = year_param
        AND MONTH(od.date) = month_param
    GROUP BY
        MONTH(od.date), YEAR(od.date);
END$$

CREATE PROCEDURE GetTopSellingProductsByCategory(IN target_month INT, IN target_year INT)
BEGIN
    SELECT
        p.category,
        p.name,
        SUM(oi.quantity) AS total_quantity_sold,
        SUM(oi.price * oi.quantity) AS total_sales
    FROM
        product p
    JOIN
        order_item oi ON p.id = oi.product_id
    JOIN
        order_details od ON oi.order_id = od.id
    WHERE
        MONTH(od.date) = target_month  -- Use the provided month
        AND YEAR(od.date) = target_year  -- Use the provided year
    GROUP BY
        p.category, p.name
    HAVING
        SUM(oi.quantity) = (
            SELECT
                MAX(total_quantity)
            FROM (
                SELECT
                    SUM(oi2.quantity) AS total_quantity
                FROM
                    order_item oi2
                JOIN
                    order_details od2 ON oi2.order_id = od2.id
                JOIN
                    product p2 ON oi2.product_id = p2.id
                WHERE
                    MONTH(od2.date) = target_month  -- Same month
                    AND YEAR(od2.date) = target_year  -- Same year
                    AND p2.category = p.category  -- Same category
                GROUP BY
                    p2.name  -- Group by product name to calculate the total for each product
            ) AS Subquery
        )
    ORDER BY
        p.category;
END$$
DELIMITER ;