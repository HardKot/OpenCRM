CREATE TABLE clients (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    tenant_id UUID NOT NULL,
    email VARCHAR(255) DEFAULT "",
    phoneNumber VARCHAR(255) DEFAULT "",
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
);

CREATE TABLE employees (
    id SERIAL PRIMARY KEY,
    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    tenant_id UUID NOT NULL,
    email VARCHAR(255) DEFAULT "",
    phone_number VARCHAR(255) DEFAULT "",
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
);

INSERT INTO
    clients (
        firstname,
        lastname,
        patronymic,
        tenant_id
    )
VALUES (
        'Иван',
        'Иванов',
        'Иванович',
        '${tenantId}'
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
        'Менеджер'
    );