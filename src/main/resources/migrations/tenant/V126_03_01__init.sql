CREATE TABLE clents (
    id UUID PRIMARY KEY,

    firstname VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    patronymic VARCHAR(255) NOT NULL,

    tenant_id UUID NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phoneNumber VARCHAR(255) NOT NULL,

    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
);