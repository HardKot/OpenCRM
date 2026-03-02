CREATE TABLE clients (
    id INTEGER PRIMARY KEY,

    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,

    tenant_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phoneNumber VARCHAR(255) NOT NULL,

    FOREIGN KEY (tenant_id) REFERENCES public.tenants(id) ON DELETE CASCADE
);

CREATE TABLE employees (
    id INTEGER PRIMARY KEY,

    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,

    tenant_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(255) NOT NULL,

    FOREIGN KEY (tenant_id) REFERENCES public.tenants(id) ON DELETE CASCADE
);