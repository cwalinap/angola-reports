INSERT INTO reports.jasper_templates VALUES
(
    '33f166c5-bd42-4b64-895e-eb84718a4ac1',
    '\x',
    null,
    true,
    'Stock Disponível (Por Producto)',
    'Consistency Report'
);

--Insert our "Stock on hand per product" report params
INSERT INTO reports.template_parameters VALUES
(
    '39089b9e-3d07-4674-9e0c-934d620265e2',
    'java.lang.String', null, 'checkboxes',
    'Programa',
    'name',
    'program',
    true,
    '/api/programs',
    'name',
    '33f166c5-bd42-4b64-895e-eb84718a4ac1',
    'GET',
    null
),
(
    '4cf0592f-6341-43d6-bf5b-1a28b7b1fe82',
    'java.lang.String', null, null,
    'Instituição',
    'name',
    'facility',
    false,
    '/api/facilities',
    'name',
    '33f166c5-bd42-4b64-895e-eb84718a4ac1',
    'GET',
    null
),
(
    '1f7b51b1-7f99-4b4e-89d1-e3491bd08da3',
    'java.lang.String', null, 'datepickers',
    'Data',
    'date',
    'date',
    true,
    null,
    null,
    '33f166c5-bd42-4b64-895e-eb84718a4ac1',
    'GET',
    null
),
(
    '452ca877-ff69-444d-a00d-1c0317b1bf5c',
    'java.lang.String', null, null,
    'Zona Geográfica',
    'name',
    'district',
    false,
    '/api/geographicZones',
    'name',
    '33f166c5-bd42-4b64-895e-eb84718a4ac1',
    'GET',
    null
),
(
    'bc735716-b57b-4f10-b07e-35dab53389ed',
    'java.lang.String', null, null,
    'Produto',
    'fullProductName',
    'productId',
    false,
    '/api/orderables',
    'id',
    '33f166c5-bd42-4b64-895e-eb84718a4ac1',
    'GET',
    null
);