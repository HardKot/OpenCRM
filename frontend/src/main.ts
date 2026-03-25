import { createApp } from 'vue'

import './style.css'
import '@/app/styles'
import { router } from "@/app/routes"
import { i18n } from '@/app/i18n'

import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { aliases, mdi } from 'vuetify/iconsets/mdi'


import App from './App.vue'

const vuetify = createVuetify({
  components,
  directives,
  icons: {
    defaultSet: 'mdi',
    aliases,
    sets: {
      mdi,
    },
  },
})


createApp(App).use(vuetify).use(router).use(i18n).mount('#app')
