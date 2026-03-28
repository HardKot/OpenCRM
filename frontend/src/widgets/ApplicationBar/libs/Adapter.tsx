import { Entity } from "#entities/User";
import { EmployeeDto } from "#shared/api";

function entityToEmployee(entity: Entity): EmployeeDto {
    return {
        id: entity.id,
        firstname: entity.firstname,
        lastname: entity.lastname,
        patronymic: entity.patronymic,
        email: entity.email,
        isDeleted: entity.isDeleted,
        phone: entity.phone,
        position: entity.position
    }
}

export { entityToEmployee }