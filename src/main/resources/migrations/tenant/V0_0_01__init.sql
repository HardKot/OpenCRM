CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    tenant_id UUID NOT NULL,
    email VARCHAR(255) DEFAULT '',
    phone_number VARCHAR(255) DEFAULT '',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    tenant_id UUID NOT NULL,
    position VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) DEFAULT '',
    email VARCHAR(255) DEFAULT '',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE investigation_logs (
    id SERIAL PRIMARY KEY,
    employee_id INTEGER NOT NULL,
    tenant_id UUID NOT NULL,
    details JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES employees (id)
);

INSERT INTO
    employees (
        firstname,
        lastname,
        patronymic,
        tenant_id,
        position
    )
VALUES (
        'Петр',
        'Петров',
        'Петрович',
        '${tenantId}',
        'Владелец'
    );

INSERT INTO
    investigation_logs (
        employee_id,
        tenant_id,
        details
    )
VALUES (
        1,
        '${tenantId}',
        '{"description": "Создание организации" }'
    ),
    (
        1,
        '${tenantId}',
        '{"description": "Создание владельца организации", "action": "CREATE", "entity": "EMPLOYEE", "entityId": 1 }'
    );