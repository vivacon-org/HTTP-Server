CREATE TABLE schedules (
    id VARCHAR(20) PRIMARY KEY,
    description TEXT,
    rule_json JSONB NOT NULL,
    next_run TIMESTAMP NOT NULL,
    last_run TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
