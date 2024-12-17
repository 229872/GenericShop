import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import fs from 'fs';
import {environment} from './src/utils/constants.ts'


export default defineConfig({
  plugins: [react()],
  server: {
    https: environment.protocol === 'https' ? {
      key: fs.readFileSync('./certs/localhost_no_pass.key'),
      cert: fs.readFileSync('./certs/localhost.crt'),
    } : false,
    host: 'localhost',
    port: 5173, 
  },
});