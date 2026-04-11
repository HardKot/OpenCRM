interface Entity {
  id: number;
  isDeleted: boolean;
  firstname: string;
  lastname: string;
  patronymic: string;
  position: string;
  email: string;
  phone: string;
}

export type { Entity };
