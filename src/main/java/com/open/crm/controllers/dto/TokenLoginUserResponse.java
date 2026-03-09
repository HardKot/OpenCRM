package com.open.crm.controllers.dto;

import java.util.UUID;

public record TokenLoginUserResponse(boolean success, UUID userId, UUID tenantId,

                String message, String accessToken, String refreshToken) {

}
