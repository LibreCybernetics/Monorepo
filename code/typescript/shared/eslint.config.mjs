import globals from "globals";
import eslint from "@eslint/js";
import tseslint from "typescript-eslint";

export default [
    {
        files: ["**/*.ts"]
    },
    {
        ignores: [
            "eslint.config.mjs",
        ]
    },
    {
        languageOptions: {
            globals: {...globals.browser, ...globals.node}
        }
    },
    eslint.configs.recommended,
    ...tseslint.configs.strictTypeChecked,
    ...tseslint.configs.stylisticTypeChecked,
    {
        languageOptions: {
            parserOptions: {
                projectService: true,
                tsconfigRootDir: import.meta.dirname,
            },
        },
    },
];