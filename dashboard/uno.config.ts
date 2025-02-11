import {
    defineConfig,
    presetAttributify,
    presetIcons,
    presetTypography,
    presetUno,
    presetWebFonts,
    transformerDirectives,
    transformerVariantGroup
} from 'unocss'

export default defineConfig({
    presets: [
        presetUno(),
        presetAttributify(),
        presetIcons(),
        presetWebFonts({
            provider: 'google',
            fonts: {
                sans: 'Roboto'
            }
        }),
        presetTypography()
    ],
    transformers: [
        transformerDirectives(),
        transformerVariantGroup()
    ],
    content: {
        pipeline: {
            include: [/\.(vue|svelte|[jt]sx|mdx?|astro|elm|php|phtml|html)($|\?)/, 'src/**/*.{js,ts}']
        }
    }
})