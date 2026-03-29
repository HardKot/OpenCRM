import { ChangeLanguage, ChangeTheme } from "#features/AppSettings";
import { ChangePassword } from "#features/ChangePassword/ui/ChangePassword";
import { useI18n } from "#shared/hooks"
import { Layout, View } from "#shared/ui"

const Settings = () => {
    const { t } = useI18n();

    return (
        <View bgcolor="background.default">
            <Layout.Grid>
                <Layout.Item size={"auto"} >
                    <Layout.Paper minHeight={160}>
                        <ChangeLanguage />
                    </Layout.Paper>
                </Layout.Item>

                <Layout.Item size={"auto"} >
                    <Layout.Paper minHeight={160}>
                        <ChangeTheme />
                    </Layout.Paper>
                </Layout.Item>
                
                <Layout.ItemPaper size={"auto"} minHeight={160}>
                    <ChangePassword />
                </Layout.ItemPaper>
            </Layout.Grid>
        </View>
    )
}

export { Settings }