CREATE OR REPLACE FUNCTION set_product_price_on_sale()
RETURNS TRIGGER AS $$
BEGIN
    SELECT pp.price
    INTO NEW.product_price_on_sale_date
    FROM product_price pp
    JOIN sale s ON s.id = NEW.sale_id
    WHERE pp.product_id = NEW.product_id
      AND pp.price_change_date::date <= s.date
    ORDER BY pp.price_change_date DESC
    LIMIT 1;

     -- 2. Se não encontrou (preço nulo), busca o preço mais antigo disponível
    IF NEW.product_price_on_sale_date IS NULL THEN
        SELECT pp.price
        INTO NEW.product_price_on_sale_date
        FROM product_price pp
        WHERE pp.product_id = NEW.product_id
        ORDER BY pp.price_change_date ASC -- Pega o primeiro registro histórico
        LIMIT 1;
    END IF;

    -- Opcional: Se AINDA assim for nulo, significa que o produto não existe na tabela de preços
    IF NEW.product_price_on_sale_date IS NULL THEN
        RAISE EXCEPTION 'Produto % não possui nenhum preço em toda a base de dados', NEW.product_id;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE OR REPLACE TRIGGER trg_set_product_price_on_sale
BEFORE INSERT ON sale_product
FOR EACH ROW
EXECUTE FUNCTION set_product_price_on_sale();
