 SELECT prmp.production_id,
    prmp.purchase_id,
    rm.name AS raw_material_name,
    rm.description AS raw_material_description,
    pur.price_per_unit AS raw_material_price_per_unit,
    pur.quantity AS raw_material_quantity,
    mu.symbol AS measurement_symbol,
    prmp.quantity_used
   FROM production_raw_material_purchase prmp
     JOIN purchase pur ON pur.id = prmp.purchase_id
     JOIN raw_material rm ON rm.id = pur.raw_material_id
     JOIN measurement_unit mu ON mu.id = pur.measurement_unit_id;