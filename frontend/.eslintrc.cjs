module.exports = {
  parser: "@typescript-eslint/parser",
  parserOptions: {
    ecmaVersion: "latest",
    sourceType: "module",
    ecmaFeatures: {
      jsx: true,
    },
  },
  env: {
    node: true,
    browser: true,
    es2021: true,
  },
  plugins: [
    "unicorn",
    "react",
    "react-hooks",
    "boundaries",
    "@typescript-eslint",
  ],
  extends: [
    "eslint:recommended",
    "plugin:react/recommended",
    "plugin:react-hooks/recommended",
  ],
  settings: {
    react: {
      version: "detect",
    },
    "boundaries/elements": [
      {
        type: "app",
        pattern: "app/*",
      },
      {
        type: "pages",
        pattern: "pages/*",
      },
      {
        type: "widgets",
        pattern: "widgets/*",
      },
      {
        type: "features",
        pattern: "features/*",
      },
      {
        type: "entities",
        pattern: "entities/*",
      },
      {
        type: "shared",
        pattern: "shared/*",
      },
    ],
    "boundaries/ignore": ["**/*.test.*", "**/*.spec.*"],
  },
  rules: {
    "no-undefined": "off",
    "@typescript-eslint/no-inferrable-types": "off",
    "unicorn/no-useless-undefined": [
      "error",
      {
        checkArguments: false,
      },
    ],
    "@typescript-eslint/naming-convention": [
      "error",
      {
        selector: "function",
        format: ["camelCase", "PascalCase"],
      },
      {
        selector: "variable",
        modifiers: ["const", "global"],
        format: ["UPPER_CASE", "PascalCase", "camelCase"],
      },
      {
        selector: "class",
        format: ["PascalCase"],
      },
      {
        selector: "interface",
        format: ["PascalCase"],
      },
      {
        selector: "typeAlias",
        format: ["PascalCase"],
      },
    ],
    "react/react-in-jsx-scope": "off",
    "react/jsx-pascal-case": [
      "error",
      {
        allowAllCaps: false,
      },
    ],
    "react-hooks/rules-of-hooks": "error",
    "react-hooks/exhaustive-deps": "warn",
    "max-lines-per-function": [
      "off",
      {
        max: 50,
        skipBlankLines: true,
        skipComments: true,
      },
    ],
    "max-statements": ["off", 10],
    complexity: ["off", 10],
    "react/function-component-definition": [
      "error",
      {
        namedComponents: [
          "arrow-function",
          "function-declaration",
          "function-expression",
        ],
        unnamedComponents: "arrow-function",
      },
    ],
    "unicorn/filename-case": [
      "error",
      {
        cases: {
          pascalCase: true,
          camelCase: true,
        },
        ignore: ["\\.test\\.(tsx?|jsx?)$", "\\.spec\\.(tsx?|jsx?)$"],
      },
    ],
    "boundaries/element-types": [
      "error",
      {
        default: "disallow",
        rules: [
          {
            from: "app",
            allow: ["pages", "widgets", "features", "entities", "shared"],
          },
          {
            from: "pages",
            allow: ["widgets", "features", "entities", "shared"],
          },
          {
            from: "widgets",
            allow: ["features", "entities", "shared"],
          },
          {
            from: "features",
            allow: ["entities", "shared"],
          },
          {
            from: "entities",
            allow: ["shared"],
          },
          {
            from: "shared",
            allow: ["shared"],
          },
        ],
      },
    ],
    "boundaries/no-private": ["error"],
  },
  overrides: [
    {
      files: [
        "**/__tests__/**/*.{ts,tsx,js,jsx}",
        "**/*.{test,spec}.{ts,tsx,js,jsx}",
      ],
      env: {
        jest: true,
      },
      rules: {
        "max-lines-per-function": "off",
        "max-statements": "off",
        complexity: "off",
      },
    },
  ],
};
