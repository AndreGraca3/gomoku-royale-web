/** @type {import('tailwindcss').Config} */
module.exports = {
    content: ["./src/**/*.{js,jsx,ts,tsx}"],
    theme: {
        extend: {
            gridTemplateColumns: {
                // Simple 15 column grid
                '15': 'repeat(15, minmax(0, 1fr))',
                // Simple 19 column grid
                '19': 'repeat(19, minmax(0, 1fr))'
            },
            spacing: {
                '120': '30rem',
            },
            colors: {
                "theme-color": "var(--theme-color)",
                "dark-theme-color": "var(--dark-theme-color)",
                "gr-yellow": "var(--gr-yellow)",
            },
            animation: {
                "heart-beat": "heart-beat 2s infinite",
            },
            keyframes: {
                "heart-beat": {
                    "0%": {transform: "scale(1)"},
                    "10%": {transform: "scale(1.2)"},
                    "20%": {transform: "scale(1.1)"},
                    "30%": {transform: "scale(1.2)"},
                    "50%": {transform: "scale(1)"},
                    "100%": {transform: "scale(1)"},
                },
            },
        },
    },
    plugins: [],
};
