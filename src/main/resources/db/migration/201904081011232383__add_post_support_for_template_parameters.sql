ALTER TABLE reports.template_parameters ADD COLUMN selectmethod character varying(255) NOT NULL DEFAULT 'GET';

ALTER TABLE reports.template_parameters ADD COLUMN selectbody character varying(255);

UPDATE reports.template_parameters SET selectmethod = 'POST' WHERE name = 'district' or name = 'GeographicZone';

UPDATE reports.template_parameters SET selectbody = '{"levelNumber": 2}' WHERE name = 'district' or name = 'GeographicZone';

UPDATE reports.template_parameters SET selectexpression = '/api/geographicZones/search' WHERE name = 'district' or name = 'GeographicZone';
