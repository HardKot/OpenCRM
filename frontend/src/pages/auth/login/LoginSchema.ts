import * as yup from 'yup'


export const createLoginSchema = (t: (key: string, values?: any) => string) => {
  return yup.object({
    email: yup
      .string()
      .required(t('login.required', { field: t('login.email') }))
      .email(t('login.email')),
      
    password: yup
      .string()
      .required(t('login.required', { field: t('login.password') }))
  })
}

export type ILoginForm = yup.InferType<ReturnType<typeof createLoginSchema>>
