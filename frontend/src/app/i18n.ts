import { createI18n } from 'vue-i18n'
import ru from '@/app/locales/ru.json'

export const i18n = createI18n({
  legacy: false, 
  locale: 'ru', 
  fallbackLocale: 'ru', 
  messages: {
    ru
  },
})
