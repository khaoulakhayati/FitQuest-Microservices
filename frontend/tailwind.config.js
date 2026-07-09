/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ['./src/**/*.{html,ts}'],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        navy: {
          950: '#0a0f1a',
          900: '#0f1729',
          800: '#152238',
          700: '#1c2d4a',
        },
        cyan: {
          glow: '#22d3ee',
          400: '#22d3ee',
          500: '#06b6d4',
          600: '#0891b2',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
      },
      boxShadow: {
        glass: '0 8px 32px rgba(0, 0, 0, 0.37)',
        glow: '0 0 20px rgba(34, 211, 238, 0.25)',
      },
      backdropBlur: {
        glass: '12px',
      },
    },
  },
  plugins: [],
};
