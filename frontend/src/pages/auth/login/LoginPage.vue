<script setup lang="ts">
import { ref, computed } from 'vue'
import { useForm } from 'vee-validate'
import { useI18n } from 'vue-i18n'
import { createLoginSchema, type ILoginForm } from './LoginSchema'
import { AuthApi } from '@/shared/api'

const { t, te } = useI18n()

const isPasswordVisible = ref(false)

const schema = computed(() => createLoginSchema(t))

const { defineField, handleSubmit, errors, isSubmitting } = useForm<ILoginForm>({
  validationSchema: schema,
})


const [email, emailProps] = defineField('email')
const [password, passwordProps] = defineField('password')

const emailError = computed(() => errors.value.email)
const passwordError = computed(() => errors.value.password)

const error = ref('')

const localeErrorMessage = (message: string) => {
    if (te(`login.${message}`)) {
        return t(`login.${message}`)
    } else if (te(`errors.${message}`)) {
        return t(`errors.${message}`)
    } else {
        return t('login.error')
    }
}

const handleLogin = handleSubmit(async (values) => {
    error.value = ''
    try {
        const { hasError, errorMessage } = await AuthApi.login(values);
        if (hasError) {
            error.value = localeErrorMessage(errorMessage ?? 'errors.Unknown error')
        }

    } catch (e) {
        error.value = t('errors.Unknown error')
    }
})
</script>

<template>
    <v-container fluid class="fill-height d-flex flex-column align-center justify-center">
        <h1 class="text-h1 mb-6">{{ t('application.name') }}</h1>
        
        <v-card width="400" elevation="2">
            <v-card-title class="text-h6 text-center py-4">{{ t('login.title') }}</v-card-title>
            <v-card-text>
                <v-form @submit.prevent="handleLogin">
                    <v-text-field
                        v-model="email"
                        v-bind="emailProps"
                        :error-messages="emailError"
                        :label="t('login.email')"
                        variant="outlined"
                        prepend-inner-icon="mdi-account"
                        autocomplete="username"
                    ></v-text-field>
                    
                    <v-text-field
                        v-model="password"
                        v-bind="passwordProps"
                        :error-messages="passwordError"
                        :label="t('login.password')"
                        :type="isPasswordVisible ? 'text' : 'password'"
                        :append-inner-icon="isPasswordVisible ? 'mdi-eye-off' : 'mdi-eye'"
                        @click:append-inner="isPasswordVisible = !isPasswordVisible"
                        variant="outlined"
                        prepend-inner-icon="mdi-lock"
                        autocomplete="current-password"
                        class="mt-2"
                    ></v-text-field>

                    <v-alert
                        v-if="error"
                        type="error"
                        variant="tonal"
                        class="mb-4"
                        closable
                    >
                        {{ error }}
                    </v-alert>

                    <v-btn
                        type="submit"
                        color="primary"
                        block
                        size="large"
                        class="mt-4"
                        :loading="isSubmitting"
                    >
                        {{ t('login.submit') }}
                    </v-btn>
                </v-form>
            </v-card-text>
        </v-card>
    </v-container>
</template>


<style scoped>

</style>