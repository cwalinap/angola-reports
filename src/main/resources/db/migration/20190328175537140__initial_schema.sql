--
-- TOC entry 412 (class 1259 OID 38900)
-- Name: configuration_settings; Type: TABLE; Schema: reports; Owner: postgres
--

CREATE TABLE configuration_settings (
    key character varying(255) NOT NULL,
    value text NOT NULL
);


--
-- TOC entry 413 (class 1259 OID 38908)
-- Name: jasper_template_parameter_dependencies; Type: TABLE; Schema: reports; Owner: postgres
--

CREATE TABLE jasper_template_parameter_dependencies (
    id uuid NOT NULL,
    dependency text NOT NULL,
    placeholder text NOT NULL,
    property text NOT NULL,
    parameterid uuid NOT NULL
);


--
-- TOC entry 414 (class 1259 OID 38916)
-- Name: jasper_templates; Type: TABLE; Schema: reports; Owner: postgres
--

CREATE TABLE jasper_templates (
    id uuid NOT NULL,
    data bytea,
    description text,
    isdisplayed boolean DEFAULT true,
    name text NOT NULL,
    type text
);


--
-- TOC entry 415 (class 1259 OID 38925)
-- Name: jaspertemplate_supportedformats; Type: TABLE; Schema: reports; Owner: postgres
--

CREATE TABLE jaspertemplate_supportedformats (
    jaspertemplateid uuid NOT NULL,
    supportedformats character varying(255)
);


--
-- TOC entry 416 (class 1259 OID 38928)
-- Name: jaspertemplateparameter_options; Type: TABLE; Schema: reports; Owner: postgres
--

CREATE TABLE jaspertemplateparameter_options (
    jaspertemplateparameterid uuid NOT NULL,
    options character varying(255)
);


--
-- TOC entry 417 (class 1259 OID 38931)
-- Name: template_parameters; Type: TABLE; Schema: reports; Owner: postgres
--

CREATE TABLE template_parameters (
    id uuid NOT NULL,
    datatype text,
    defaultvalue text,
    description text,
    displayname text,
    displayproperty text,
    name text,
    required boolean NOT NULL,
    selectexpression text,
    selectproperty text,
    templateid uuid NOT NULL
);



--
-- TOC entry 4183 (class 2606 OID 38907)
-- Name: configuration_settings configuration_settings_pkey; Type: CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY configuration_settings
    ADD CONSTRAINT configuration_settings_pkey PRIMARY KEY (key);


--
-- TOC entry 4185 (class 2606 OID 38915)
-- Name: jasper_template_parameter_dependencies jasper_template_parameter_dependencies_pkey; Type: CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY jasper_template_parameter_dependencies
    ADD CONSTRAINT jasper_template_parameter_dependencies_pkey PRIMARY KEY (id);


--
-- TOC entry 4187 (class 2606 OID 38924)
-- Name: jasper_templates jasper_templates_pkey; Type: CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY jasper_templates
    ADD CONSTRAINT jasper_templates_pkey PRIMARY KEY (id);


--
-- TOC entry 4191 (class 2606 OID 38938)
-- Name: template_parameters template_parameters_pkey; Type: CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT template_parameters_pkey PRIMARY KEY (id);


--
-- TOC entry 4189 (class 2606 OID 38940)
-- Name: jasper_templates uk_5878s5vb2v4y53vun95nrdvgw; Type: CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY jasper_templates
    ADD CONSTRAINT uk_5878s5vb2v4y53vun95nrdvgw UNIQUE (name);


--
-- TOC entry 4209 (class 2606 OID 38956)
-- Name: template_parameters fkaqoftrtebrnodyelvkgc3r2h1; Type: FK CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY template_parameters
    ADD CONSTRAINT fkaqoftrtebrnodyelvkgc3r2h1 FOREIGN KEY (templateid) REFERENCES jasper_templates(id);


--
-- TOC entry 4206 (class 2606 OID 38941)
-- Name: jasper_template_parameter_dependencies fkjbuuhld94osgeojc9sda5dqq; Type: FK CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY jasper_template_parameter_dependencies
    ADD CONSTRAINT fkjbuuhld94osgeojc9sda5dqq FOREIGN KEY (parameterid) REFERENCES template_parameters(id);


--
-- TOC entry 4207 (class 2606 OID 38946)
-- Name: jaspertemplate_supportedformats fkrn2fpldtdp4v06uxwmgctgcic; Type: FK CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY jaspertemplate_supportedformats
    ADD CONSTRAINT fkrn2fpldtdp4v06uxwmgctgcic FOREIGN KEY (jaspertemplateid) REFERENCES jasper_templates(id);


--
-- TOC entry 4208 (class 2606 OID 38951)
-- Name: jaspertemplateparameter_options fks50n5t8p8739i22wtn9x3n2f2; Type: FK CONSTRAINT; Schema: reports; Owner: postgres
--

ALTER TABLE ONLY jaspertemplateparameter_options
    ADD CONSTRAINT fks50n5t8p8739i22wtn9x3n2f2 FOREIGN KEY (jaspertemplateparameterid) REFERENCES template_parameters(id);
