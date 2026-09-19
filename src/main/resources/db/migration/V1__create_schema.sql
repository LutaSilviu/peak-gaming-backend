CREATE TABLE station_types (
    code            VARCHAR(16)   PRIMARY KEY,
    short_name      VARCHAR(32)   NOT NULL,
    full_name       VARCHAR(64)   NOT NULL,
    people_per_unit SMALLINT      NOT NULL CHECK (people_per_unit > 0),
    max_people      SMALLINT      NOT NULL CHECK (max_people > 0)
);

CREATE TABLE stations (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code            VARCHAR(16)   NOT NULL UNIQUE,
    station_type    VARCHAR(16)   NOT NULL REFERENCES station_types (code),
    display_order   SMALLINT      NOT NULL
);

CREATE INDEX idx_stations_type ON stations (station_type);

CREATE TABLE price_tiers (
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    station_type    VARCHAR(16)   NOT NULL REFERENCES station_types (code),
    student_rate    BOOLEAN       NOT NULL DEFAULT FALSE,
    duration_hours  SMALLINT      NOT NULL CHECK (duration_hours > 0),
    price_lei       NUMERIC(8,2)  NOT NULL CHECK (price_lei >= 0),
    UNIQUE (station_type, student_rate, duration_hours)
);

CREATE TABLE zones (
    code            VARCHAR(16)   PRIMARY KEY,
    label           VARCHAR(32)   NOT NULL,
    tag             VARCHAR(64)   NOT NULL,
    title           VARCHAR(128)  NOT NULL,
    description     TEXT          NOT NULL,
    capacity_label  VARCHAR(32)   NOT NULL,
    hours_label     VARCHAR(32)   NOT NULL,
    display_order   SMALLINT      NOT NULL
);

CREATE TABLE reservations (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    confirmation_code   VARCHAR(16)   NOT NULL UNIQUE,
    station_type        VARCHAR(16)   NOT NULL REFERENCES station_types (code),
    people_count        SMALLINT      NOT NULL CHECK (people_count > 0),
    student_rate        BOOLEAN       NOT NULL DEFAULT FALSE,
    reservation_date    DATE          NOT NULL,
    start_hour          SMALLINT      NOT NULL CHECK (start_hour >= 0 AND start_hour < 24),
    duration_hours      SMALLINT      NOT NULL CHECK (duration_hours > 0),
    customer_name       VARCHAR(128)  NOT NULL,
    customer_phone      VARCHAR(32)   NOT NULL,
    source              VARCHAR(32)   NOT NULL DEFAULT 'website',
    total_price_lei     NUMERIC(8,2)  NOT NULL CHECK (total_price_lei >= 0),
    status              VARCHAR(16)   NOT NULL DEFAULT 'noua'
                            CHECK (status IN ('noua', 'confirmata', 'anulata')),
    created_at          TIMESTAMPTZ   NOT NULL DEFAULT now()
);

CREATE INDEX idx_reservations_date ON reservations (reservation_date);
CREATE INDEX idx_reservations_date_status ON reservations (reservation_date, status);

CREATE TABLE reservation_stations (
    reservation_id  BIGINT NOT NULL REFERENCES reservations (id) ON DELETE CASCADE,
    station_id      BIGINT NOT NULL REFERENCES stations (id),
    PRIMARY KEY (reservation_id, station_id)
);

CREATE INDEX idx_reservation_stations_station ON reservation_stations (station_id);
