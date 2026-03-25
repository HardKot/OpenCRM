
import { reactive } from "vue"

const userStore = reactive({
    userId: "",
    tenantId: "",
    entityId: 0,
    permissions: [] as string[],
})

export { userStore } 