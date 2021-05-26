DELETE FROM reports.template_parameters WHERE templateid = 'e8c66178-81b6-4b43-b8b9-b3206285fdc2';

--Insert our "Facility Consumption" report params
INSERT INTO reports.template_parameters (id, datatype, defaultvalue, description, displayname, displayproperty, name, required, selectexpression,selectproperty, templateid, selectmethod, selectbody) VALUES
(
    'efda5fbb-4307-42a7-b642-f35a63404c60',
    'java.lang.String', null, null,
    'Instituição',
    'name',
    'facilityName',
    false,
    '/api/facilities',
    'name',
    'e8c66178-81b6-4b43-b8b9-b3206285fdc2',
    'GET',
    null
),
(
    '8f862204-2038-433f-bd48-164cea8749f2',
    'java.lang.String', null, null,
    'Nome do Produto',
    'fullProductName',
    'productId',
    false,
    '/api/orderables',
    'id',
    'e8c66178-81b6-4b43-b8b9-b3206285fdc2',
    'GET',
    null
),
(
    'dc4a27a8-0fdd-4554-88ee-5c38cd13a2ce',
    'java.lang.String', null, 'checkboxes',
    'Programa',
    'name',
    'program',
    false,
    '/api/programs',
    'name',
    'e8c66178-81b6-4b43-b8b9-b3206285fdc2',
    'GET',
    null
),
(
    '1ef2b895-51f6-4426-80a0-019bab7ed55e',
    'java.lang.String', null, null,
    'Início',
    'startDate',
    'periodStartDate',
    false,
    '/api/processingPeriods',
    'startDate',
    'e8c66178-81b6-4b43-b8b9-b3206285fdc2',
    'GET',
    null
),
(
    '2264823b-0b18-4848-b0fd-9a52a53d31ef',
    'java.lang.String', null, null,
    'Término',
    'endDate',
    'periodEndDate',
    false,
    '/api/processingPeriods',
    'endDate',
    'e8c66178-81b6-4b43-b8b9-b3206285fdc2',
    'GET',
    null
),
(
    '0399b964-aeb3-4cb4-856b-15410ff70b29',
    'java.lang.String', null, null,
    'Província',
    'name',
    'geographicZoneId',
    false,
    '/api/geographicZones/search',
    'id',
    'e8c66178-81b6-4b43-b8b9-b3206285fdc2',
    'POST',
    '{"levelNumber": 2}'
);
