-- Stock status report
UPDATE reports.jasper_templates
SET name = 'Situação de Stock'
WHERE id = '097e109a-96e2-40f5-908a-6e770bf660d5';

-- Reporting Rate report
UPDATE reports.jasper_templates
SET name = 'Reportagem de Envio de Relatórios'
WHERE id = '3ac08504-08e1-4b31-8929-f4bfb9112f69';

-- Adjustments Summary report
UPDATE reports.jasper_templates
SET name = 'Resumo de Ajuste'
WHERE id = '3ae277e4-fe3e-42fa-ac97-d43868c2e9d8';

-- Non-reporting facilities report
UPDATE reports.jasper_templates
SET name = 'Instituições que não subemeteram as requisições'
WHERE id = '3fafb1cb-659b-4182-8c84-6df209a0f8d5';

-- Facility Assignment Configuration Errors report
UPDATE reports.jasper_templates
SET name = 'Configuração de Instalações'
WHERE id = '5308cb58-a5b7-4741-a3d3-13fb24871bac';

-- LMIS Summary report
UPDATE reports.jasper_templates
SET name = 'LMIS Resumo'
WHERE id = '93d09638-4dc7-4c94-a9f2-e80b5c62408e';
