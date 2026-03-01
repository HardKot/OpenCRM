CREATE TABLE public.tenant (
    id UUID PRIMARY KEY,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    is_ready BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE public.users (
    id UUID PRIMARY KEY,

    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,

    tenant_id UUID NOT NULL,
    FOREIGN KEY (tenant_id) REFERENCES public.tenant(id) ON DELETE CASCADE
)