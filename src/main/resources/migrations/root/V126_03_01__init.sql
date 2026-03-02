CREATE TABLE public.tenants (
    id UUID PRIMARY KEY,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_ready BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE public.users (
    id UUID PRIMARY KEY,

    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    employee_id INTEGER,

    tenant_id UUID NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES public.tenants(id) ON DELETE CASCADE
)