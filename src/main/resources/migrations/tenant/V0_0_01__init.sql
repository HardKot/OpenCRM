CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    tenant_id UUID NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    email VARCHAR(255) DEFAULT '',
    phone_number VARCHAR(255) DEFAULT '',
    balance INTEGER DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    tenant_id UUID NOT NULL,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255) DEFAULT '',
    email VARCHAR(255) DEFAULT '',
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE investigation_logs (
    id SERIAL PRIMARY KEY,
    tenant_id UUID NOT NULL,
    author_entity_id BIGINT,
    author_entity_name VARCHAR(255) NOT NULL,
    details JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
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
        author_entity_name,
        tenant_id,
        details
    )
VALUES (
        'SYSTEM',
        '${tenantId}',
        '{"description": "Создание организации" }'
    ),
    (
        'SYSTEM',
        '${tenantId}',
        '{"description": "Создание владельца организации", "action": "CREATE", "entity": "EMPLOYEE", "entityId": 1 }'
    );